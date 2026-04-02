package dalbit.application.auth.oauth.port;


import dalbit.application.auth.oauth.dto.OAuth2UserInfo;

public interface LoadOAuth2UserInfoPort {
    OAuth2UserInfo loadUserInfo(String provider, String token);
}
