package dalbit.adapter.rest.external.userDevice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserDeviceTokenRequest(
    @NotBlank(message = "기기 고유 ID는 필수입니다.")
    String deviceUniqueId,

    @NotBlank(message = "FCM 토큰 값은 필수입니다.")
    String fcmToken
) {

}
