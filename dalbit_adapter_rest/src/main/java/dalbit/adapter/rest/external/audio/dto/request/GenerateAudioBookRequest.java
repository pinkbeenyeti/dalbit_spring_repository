package dalbit.adapter.rest.external.audio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GenerateAudioBookRequest(
    @NotNull
    Long fairytaleId,

    @NotBlank(message = "목소리 ID 값은 필수 입력값입니다.")
    @Size(max = 50, message = "ID 값은 50자를 초과할 수 없습니다.")
    String voiceExternalId
) {

}
