package dalbit.adapter.rest.external.userDevice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserDeviceRequest(
    @NotBlank(message = "유저 기기 고유 ID는 필수입니다.")
    String deviceUniqueId,

    @NotBlank(message = "FCM 토큰은 필수입니다.")
    String fcmToken,

    @NotNull(message = "OS 타입(IOS, ANDROID 등)은 필수입니다.")
    String osType
) {

}
