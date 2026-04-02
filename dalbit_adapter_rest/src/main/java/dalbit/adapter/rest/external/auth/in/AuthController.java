package dalbit.adapter.rest.external.auth.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.auth.dto.request.RefreshTokenRequest;
import dalbit.adapter.rest.external.auth.dto.request.SocialLoginRequest;
import dalbit.adapter.rest.external.auth.dto.response.SocialLoginResponse;
import dalbit.adapter.rest.external.auth.dto.response.TokenResponse;
import dalbit.application.rest.external.auth.dto.SocialLoginResult;
import dalbit.application.rest.external.auth.dto.TokenResult;
import dalbit.application.rest.external.auth.useCase.LoginUseCase;
import dalbit.application.rest.external.auth.useCase.ReissueTokenUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ReissueTokenUseCase reissueTokenUseCase;

    @PostMapping("login")
    public ResponseEntity<ApiResponse<SocialLoginResponse>> socialLogin(
        @RequestBody @Valid SocialLoginRequest request
    ) {
        SocialLoginResult result = loginUseCase.socialLogin(request.provider(), request.token());
        log.info("토큰 값: {}, {}", result.accessToken(), result.refreshToken());
        return ApiResponse.success(SocialLoginResponse.from(result));
    }

    @PostMapping("refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshTokens(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        TokenResult result = reissueTokenUseCase.reissue(request.refreshToken());
        return ApiResponse.success(TokenResponse.from(result));
    }
}
