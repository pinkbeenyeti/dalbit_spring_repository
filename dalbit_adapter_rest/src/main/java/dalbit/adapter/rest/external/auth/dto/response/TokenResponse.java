package dalbit.adapter.rest.external.auth.dto.response;

import dalbit.application.rest.external.auth.dto.TokenResult;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {
    public static TokenResponse from(TokenResult result) {
        return new TokenResponse(
            result.accessToken(),
            result.refreshToken()
        );
    }
}
