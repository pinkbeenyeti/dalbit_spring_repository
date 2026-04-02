package dalbit.adapter.auth.oauth.out;

import dalbit.application.auth.oauth.dto.OAuth2UserInfo;
import dalbit.application.auth.oauth.port.LoadOAuth2UserInfoPort;
import dalbit.application.auth.oauth.port.OAuth2ProviderClient;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2Adapter implements LoadOAuth2UserInfoPort {

    private final Map<String, OAuth2ProviderClient> providerClients;

    @Override
    public OAuth2UserInfo loadUserInfo(String provider, String token) {
        OAuth2ProviderClient client = Optional.ofNullable(providerClients.get(provider.toLowerCase() + "OAuth2ProviderClient"))
                .orElseThrow(() -> {
                    log.error("[OAuth2] 지원하지 않는 Provider 입니다: {}", provider);
                    return new DalbitException(ErrorCode.INVALID_PROVIDER);
                });

        return client.getUserInfo(token);
    }
}
