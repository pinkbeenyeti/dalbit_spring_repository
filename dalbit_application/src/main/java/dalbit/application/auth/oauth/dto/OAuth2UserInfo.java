package dalbit.application.auth.oauth.dto;

public record OAuth2UserInfo(
    String providerId,
    String name,
    String email
) {

}
