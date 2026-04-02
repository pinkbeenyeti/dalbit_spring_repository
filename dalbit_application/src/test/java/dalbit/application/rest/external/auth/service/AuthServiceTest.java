package dalbit.application.rest.external.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import dalbit.application.auth.jwt.port.GenerateTokenPort;
import dalbit.application.auth.jwt.port.VerifyTokenPort;
import dalbit.application.auth.oauth.dto.OAuth2UserInfo;
import dalbit.application.auth.oauth.port.LoadOAuth2UserInfoPort;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.application.persistence.jpa.user.port.SaveUserPort;
import dalbit.application.persistence.redis.auth.port.ManageRefreshTokenPort;
import dalbit.application.rest.external.auth.dto.TokenResult;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.user.Role;
import dalbit.domain.user.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService, 비즈니스 로직 테스트")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private LoadOAuth2UserInfoPort loadOAuth2UserInfoPort;
    @Mock private GenerateTokenPort generateTokenPort;
    @Mock private VerifyTokenPort verifyTokenPort;
    @Mock private LoadUserPort loadUserPort;
    @Mock private SaveUserPort saveUserPort;
    @Mock private ManageRefreshTokenPort manageRefreshTokenPort;
    @Mock private User mockUser;

    private final String PROVIDER = "provider";
    private final String PROVIDER_ID = "provider_12345";
    private final String OAUTH_TOKEN = "oauth-dummy-token";

    private final String ACCESS_TOKEN = "access-token-000";
    private final String REFRESH_TOKEN = "refresh-token-000";
    private final String EXTERNAL_USER_ID = UUID.randomUUID().toString();

    @Nested
    @DisplayName("social login 테스트: ")
    class socialLogin {

        @BeforeEach
        void setUp() {
            when(mockUser.getExternalId()).thenReturn(EXTERNAL_USER_ID);
            when(mockUser.getRole()).thenReturn(Role.USER);

            given(generateTokenPort.createAccessToken(EXTERNAL_USER_ID, Role.USER)).willReturn(ACCESS_TOKEN);
            given(generateTokenPort.createRefreshToken(EXTERNAL_USER_ID)).willReturn(REFRESH_TOKEN);
        }

        @Test
        @DisplayName("신규 소셜 로그인 유저, DB에 새로운 사용자 저장 후 토큰 발급")
        void success_new_kakao_user() {
            OAuth2UserInfo kakaoUserInfo = new OAuth2UserInfo(PROVIDER_ID, "TESTUSER", "TEST@TEST.COM");

            given(loadOAuth2UserInfoPort.loadUserInfo(PROVIDER, OAUTH_TOKEN)).willReturn(kakaoUserInfo);
            given(loadUserPort.loadUserByProviderId(PROVIDER_ID)).willReturn(Optional.empty());
            given(saveUserPort.saveUser(any(User.class))).willReturn(mockUser);

            TokenResult result = authService.socialLogin(PROVIDER, OAUTH_TOKEN);

            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);

            then(saveUserPort).should(times(1)).saveUser(any(User.class));
            then(manageRefreshTokenPort).should(times(1)).saveRefreshToken(EXTERNAL_USER_ID, REFRESH_TOKEN);
        }

        @Test
        @DisplayName("기존 소셜 로그인 유저, DB 저장 없이 토큰 발급")
        void success_existing_kakao_user() {
            OAuth2UserInfo kakaoUserInfo = new OAuth2UserInfo(PROVIDER_ID, "TESTUSER", "TEST@TEST.COM");

            given(loadOAuth2UserInfoPort.loadUserInfo(PROVIDER, OAUTH_TOKEN)).willReturn(kakaoUserInfo);
            given(loadUserPort.loadUserByProviderId(PROVIDER_ID)).willReturn(Optional.of(mockUser));

            TokenResult result = authService.socialLogin(PROVIDER, OAUTH_TOKEN);

            assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);

            then(saveUserPort).should(never()).saveUser(any(User.class));
            then(manageRefreshTokenPort).should(times(1)).saveRefreshToken(EXTERNAL_USER_ID, REFRESH_TOKEN);
        }
    }

    @Nested
    @DisplayName("token 재발급 테스트: ")
    class reissue {
        private final String NEW_ACCESS_TOKEN = "new-access-token";
        private final String NEW_REFRESH_TOKEN = "new-refresh-token";

        @BeforeEach
        void setUp() {
            given(verifyTokenPort.getExternalIdFromToken(REFRESH_TOKEN)).willReturn(EXTERNAL_USER_ID);
        }

        @Test
        @DisplayName("유효한 리프레시 토큰, 새로운 토큰 반환")
        void success_reissue_token() {
            when(mockUser.getExternalId()).thenReturn(EXTERNAL_USER_ID);
            when(mockUser.getRole()).thenReturn(Role.USER);

            given(manageRefreshTokenPort.getRefreshToken(EXTERNAL_USER_ID)).willReturn(Optional.of(REFRESH_TOKEN));
            given(loadUserPort.loadUserByExternalId(EXTERNAL_USER_ID)).willReturn(Optional.of(mockUser));

            given(generateTokenPort.createAccessToken(EXTERNAL_USER_ID, Role.USER)).willReturn(NEW_ACCESS_TOKEN);
            given(generateTokenPort.createRefreshToken(EXTERNAL_USER_ID)).willReturn(NEW_REFRESH_TOKEN);

            TokenResult result = authService.reissue(REFRESH_TOKEN);

            assertThat(result.accessToken()).isEqualTo(NEW_ACCESS_TOKEN);
            assertThat(result.refreshToken()).isEqualTo(NEW_REFRESH_TOKEN);
            then(manageRefreshTokenPort).should(times(1)).saveRefreshToken(EXTERNAL_USER_ID, NEW_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰, 해당 User의 Refresh Token이 Redis에 존재하지 않음.")
        void fail_token_not_found_in_redis() {
            given(manageRefreshTokenPort.getRefreshToken(EXTERNAL_USER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.reissue(REFRESH_TOKEN))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_REFRESH_TOKEN.getMessage());
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰, 해당 User의 Refresh Token이 Redis에 저장된 것과 다름. (토큰 탈취)")
        void fail_token_mismatch() {
            String hijackedToken = "hijacked-token";

            given(manageRefreshTokenPort.getRefreshToken(EXTERNAL_USER_ID)).willReturn(Optional.of(hijackedToken));

            assertThatThrownBy(() -> authService.reissue(REFRESH_TOKEN))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());

            then(manageRefreshTokenPort).should(times(1)).deleteRefreshToken(EXTERNAL_USER_ID);
        }

        @Test
        @DisplayName("유효하지 않은 리프레시 토큰, Refresh Token은 정상이나, 해당 유저가 존재하지 않음. (토큰 로직 해킹)")
        void fail_user_not_found() {
            given(manageRefreshTokenPort.getRefreshToken(EXTERNAL_USER_ID)).willReturn(Optional.of(REFRESH_TOKEN));
            given(loadUserPort.loadUserByExternalId(EXTERNAL_USER_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.reissue(REFRESH_TOKEN))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_USER.getMessage());
        }
    }

}