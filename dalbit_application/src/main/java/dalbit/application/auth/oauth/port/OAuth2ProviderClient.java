package dalbit.application.auth.oauth.port;

import dalbit.application.auth.oauth.dto.OAuth2UserInfo;

public interface OAuth2ProviderClient {
    OAuth2UserInfo getUserInfo(String token);
}
