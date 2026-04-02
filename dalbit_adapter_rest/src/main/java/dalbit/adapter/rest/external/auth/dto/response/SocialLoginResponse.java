package dalbit.adapter.rest.external.auth.dto.response;

import dalbit.application.rest.external.auth.dto.SocialLoginResult;

public record SocialLoginResponse(
    String userExternalId,
    String accessToken,
    String refreshToken
) {
    public static SocialLoginResponse from(SocialLoginResult result) {
        return new SocialLoginResponse(
            result.userExternalId(),
            result.accessToken(),
            result.refreshToken()
        );
    }
}
