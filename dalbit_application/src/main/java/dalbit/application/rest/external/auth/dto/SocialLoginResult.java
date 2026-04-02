package dalbit.application.rest.external.auth.dto;

public record SocialLoginResult(
    String userExternalId,
    String accessToken,
    String refreshToken
) {

}
