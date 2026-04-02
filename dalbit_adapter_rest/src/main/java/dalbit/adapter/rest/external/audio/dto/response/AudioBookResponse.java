package dalbit.adapter.rest.external.audio.dto.response;

import dalbit.application.persistence.jpa.audio.dto.AudioBookResult;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AudioBookResponse(
    String audioBookExternalId,
    String voiceExternalId,
    Long fairytaleId,
    String status,
    String audioUrl
) {
    public static AudioBookResponse from(AudioBookResult result) {
        return new AudioBookResponse(
            result.externalId(),
            result.voiceExternalId(),
            result.fairytaleId(),
            result.status().name(),
            result.audioUrl()
        );
    }
}
