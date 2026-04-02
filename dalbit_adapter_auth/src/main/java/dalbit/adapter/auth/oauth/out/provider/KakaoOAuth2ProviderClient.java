package dalbit.adapter.auth.oauth.out.provider;

import dalbit.application.auth.oauth.port.OAuth2ProviderClient;
import dalbit.application.auth.oauth.dto.OAuth2UserInfo;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component("kakaoOAuth2ProviderClient")
public class KakaoOAuth2ProviderClient implements OAuth2ProviderClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2UserInfo getUserInfo(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> attributes = response.getBody();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            String providerId = "KAKAO_" + attributes.get("id").toString();
            String name = profile.get("nickname").toString();
            String email = kakaoAccount.containsKey("email") ? kakaoAccount.get("email").toString() : null;

            return new OAuth2UserInfo(providerId, name, email);
        } catch (HttpClientErrorException e) {
            log.error("[Kakao OAuth2] 유효하지 않은 토큰입니다. Status: {}, Response: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new DalbitException(ErrorCode.INVALID_PROVIDER_TOKEN);
        } catch (HttpServerErrorException e) {
            log.error("[Kakao OAuth2] 카카오 서버 장애. Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new DalbitException(ErrorCode.EXTERNAL_OAUTH2_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[Kakao OAuth2] 사용자 정보를 가져오는 중 오류가 발생했습니다.", e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
