package dalbit.adapter.rest.external.voice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterVoiceRequest(
    @NotBlank(message = "목소리 이름은 필수 입력값입니다.")
    @Size(max = 50, message = "목소리 이름은 10자를 초과할 수 없습니다.")
    String name
) {

}
