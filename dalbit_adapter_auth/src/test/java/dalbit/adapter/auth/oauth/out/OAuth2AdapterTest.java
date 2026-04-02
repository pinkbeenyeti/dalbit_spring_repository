package dalbit.adapter.auth.oauth.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import dalbit.application.auth.oauth.dto.OAuth2UserInfo;
import dalbit.application.auth.oauth.port.OAuth2ProviderClient;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2Adapter, 라우팅 로직 테스트")
class OAuth2AdapterTest {

    private OAuth2Adapter oAuth2Adapter;

    @Mock private OAuth2ProviderClient kakaoClient;
    @Mock private OAuth2ProviderClient googleClient;

    private final String TOKEN = "test-token";
    private OAuth2UserInfo oAuth2UserInfo;

    @BeforeEach
    void setUp() {
        Map<String, OAuth2ProviderClient> clients = new HashMap<>();

        clients.put("kakaoOAuth2ProviderClient", kakaoClient);
        clients.put("googleOAuth2ProviderClient", googleClient);

        oAuth2Adapter = new OAuth2Adapter(clients);
        oAuth2UserInfo = new OAuth2UserInfo("test_provider_id", "testUser", "test@test.com");
    }

    @Nested
    @DisplayName("loadUserInfo 테스트: ")
    class loadUserInfo {

        @Test
        @DisplayName("올바른 Provider, 클라이언트 정보 반환")
        void success_valid_provider() {
            given(kakaoClient.getUserInfo(TOKEN)).willReturn(oAuth2UserInfo);

            OAuth2UserInfo result = oAuth2Adapter.loadUserInfo("KAKAO", TOKEN);

            assertThat(result.providerId()).isEqualTo("test_provider_id");
            assertThat(result.name()).isEqualTo("testUser");

            then(kakaoClient).should(times(1)).getUserInfo(TOKEN);
            then(googleClient).should(times(0)).getUserInfo(TOKEN);
        }

        @Test
        @DisplayName("대소문자가 섞인 Provider, 클라이언트 정보 반환")
        void success_lowercase_provider() {
            given(googleClient.getUserInfo(TOKEN)).willReturn(oAuth2UserInfo);

            OAuth2UserInfo result = oAuth2Adapter.loadUserInfo("Google", TOKEN);

            assertThat(result).isNotNull();
            then(googleClient).should(times(1)).getUserInfo(TOKEN);
        }

        @Test
        @DisplayName("등록되지 않은 Provider, 예외 발생")
        void fail_invalid_provider() {
            String invalidProvider = "NAVER";

            assertThatThrownBy(() -> oAuth2Adapter.loadUserInfo(invalidProvider, TOKEN))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.INVALID_PROVIDER.getMessage());

            then(kakaoClient).should(times(0)).getUserInfo(TOKEN);
            then(googleClient).should(times(0)).getUserInfo(TOKEN);
        }
    }
}