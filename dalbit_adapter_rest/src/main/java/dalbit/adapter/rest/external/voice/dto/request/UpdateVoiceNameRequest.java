package dalbit.adapter.rest.external.voice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateVoiceNameRequest(
    @NotBlank(message = "목소리 ID 값은 필수 입력값입니다.")
    @Size(max = 50, message = "ID 값은 10자를 초과할 수 없습니다.")
    String externalId,

    @NotBlank(message = "변경할 목소리 이름은 필수 입력값입니다.")
    @Size(max = 10, message = "목소리 이름은 10자를 초과할 수 없습니다.")
    String name
)
{ }
