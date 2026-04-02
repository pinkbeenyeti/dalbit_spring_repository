package dalbit.adapter.rest.external.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "jwt 토큰을 반드시 포함하여야 합니다.")
    String refreshToken
) {

}
