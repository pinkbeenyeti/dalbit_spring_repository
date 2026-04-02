package dalbit.adapter.rest.external.voice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleterVoiceRequest(
    @NotBlank(message = "목소리 ID 값은 필수 입력값입니다.")
    String externalId
) {

}
