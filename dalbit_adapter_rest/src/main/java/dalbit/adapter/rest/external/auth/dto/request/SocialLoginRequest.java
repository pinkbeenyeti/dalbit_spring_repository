package dalbit.adapter.rest.external.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
    @NotBlank(message = "Provider 값은 필수입니다. (e.g., GOOGLE, KAKAO)")
    String provider,

    @NotBlank(message = "토큰 값은 필수입니다.")
    String token
) {

}
