package dalbit.application.rest.external.auth.dto;

public record TokenResult(
    String accessToken,
    String refreshToken
) {

}
