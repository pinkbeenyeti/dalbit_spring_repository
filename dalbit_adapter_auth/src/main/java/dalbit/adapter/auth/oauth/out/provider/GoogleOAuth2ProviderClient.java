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
@Component("googleOAuth2ProviderClient")
public class GoogleOAuth2ProviderClient implements OAuth2ProviderClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2UserInfo getUserInfo(String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> attributes = response.getBody();

            String providerId = "GOOGLE_" + attributes.get("sub").toString();
            String name = attributes.get("name").toString();
            String email = attributes.get("email").toString();

            return new OAuth2UserInfo(providerId, name, email);
        } catch (HttpClientErrorException e) {
            log.error("[Google OAuth2] 유효하지 않은 토큰입니다. Status: {}, Response: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new DalbitException(ErrorCode.INVALID_PROVIDER_TOKEN);
        } catch (HttpServerErrorException e) {
            log.error("[Google OAuth2] 카카오 서버 장애. Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new DalbitException(ErrorCode.EXTERNAL_OAUTH2_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[Google OAuth2] 사용자 정보를 가져오는 중 오류가 발생했습니다.", e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
