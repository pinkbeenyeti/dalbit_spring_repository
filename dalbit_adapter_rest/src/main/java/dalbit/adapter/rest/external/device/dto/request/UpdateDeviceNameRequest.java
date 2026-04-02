package dalbit.adapter.rest.external.device.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDeviceNameRequest(
    @NotBlank(message = "시리얼 넘버는 필수 입력값입니다.")
    @Size(max = 50, message = "시리얼 넘버는 50자를 초과할 수 없습니다.")
    String serialNumber,

    @NotBlank(message = "새로운 기기 이름은 필수 입력값입니다.")
    String newName
) {

}
