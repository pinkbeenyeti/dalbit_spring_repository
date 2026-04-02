package dalbit.application.rest.external.auth.service;

import dalbit.application.auth.jwt.port.GenerateTokenPort;
import dalbit.application.auth.jwt.port.VerifyTokenPort;
import dalbit.application.auth.oauth.dto.OAuth2UserInfo;
import dalbit.application.auth.oauth.port.LoadOAuth2UserInfoPort;
import dalbit.application.persistence.jpa.user.port.SaveUserPort;
import dalbit.application.persistence.redis.auth.port.ManageRefreshTokenPort;
import dalbit.application.rest.external.auth.dto.SocialLoginResult;
import dalbit.application.rest.external.auth.dto.TokenResult;
import dalbit.application.rest.external.auth.useCase.LoginUseCase;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.application.rest.external.auth.useCase.ReissueTokenUseCase;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.user.Role;
import dalbit.domain.user.User;
import dalbit.domain.user.UserEmail;
import dalbit.domain.user.UserName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements LoginUseCase, ReissueTokenUseCase {

    private final LoadOAuth2UserInfoPort loadOAuth2UserInfoPort;
    private final GenerateTokenPort generateTokenPort;
    private final VerifyTokenPort verifyTokenPort;

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final ManageRefreshTokenPort manageRefreshTokenPort;

    @Override
    @Transactional
    public SocialLoginResult socialLogin(String provider, String token) {
        OAuth2UserInfo userInfo = loadOAuth2UserInfoPort.loadUserInfo(provider, token);

        User user = loadUserPort.loadUserByProviderId(userInfo.providerId())
            .orElseGet(() -> {
                User newUser = User.register(userInfo.providerId(), UserName.of(userInfo.name()), UserEmail.of(userInfo.email()), Role.USER);
                return saveUserPort.saveUser(newUser);
            });

        String accessToken = generateTokenPort.createAccessToken(user.getExternalId(), user.getRole());
        String refreshToken = generateTokenPort.createRefreshToken(user.getExternalId());

        manageRefreshTokenPort.saveRefreshToken(user.getExternalId(), refreshToken);
        log.info("[로그인 성공] userId: {}", user.getId());

        return new SocialLoginResult(user.getExternalId(), accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResult reissue(String refreshToken) {
        String userExternalId = verifyTokenPort.getExternalIdFromToken(refreshToken);

        String savedRefreshToken = manageRefreshTokenPort.getRefreshToken(userExternalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_REFRESH_TOKEN));

        if (!savedRefreshToken.equals(refreshToken)) {
            manageRefreshTokenPort.deleteRefreshToken(userExternalId);
            throw new DalbitException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = loadUserPort.loadUserByExternalId(userExternalId)
            .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_USER));

        String newAccessToken = generateTokenPort.createAccessToken(user.getExternalId(), user.getRole());
        String newRefreshToken = generateTokenPort.createRefreshToken(user.getExternalId());

        manageRefreshTokenPort.saveRefreshToken(user.getExternalId(), newRefreshToken);
        log.info("[토큰 재발급 성공] userExternalId: {}", user.getExternalId());

        return new TokenResult(newAccessToken, newRefreshToken);
    }
}
