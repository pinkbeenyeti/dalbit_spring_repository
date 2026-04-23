package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import dalbit.domain.audio.AudioBook;
import dalbit.domain.voice.Voice;
import lombok.Builder;

@Builder
public record AudioBookGenerateRequestPayload(
    @JsonProperty("audioBook_external_id") String audioBookExternalId,
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_model_path") String voiceModelPath,
    @JsonProperty("fairytale_id") Long fairytaleId,
    @JsonProperty("audioBook_audio_path") String audioBookAudioPath
) {

    public static AudioBookGenerateRequestPayload from(AudioBook audioBook, Voice voice, Long fairytaleId) {
        return AudioBookGenerateRequestPayload.builder()
            .audioBookExternalId(audioBook.getExternalId())
            .voiceExternalId(voice.getExternalId())
            .voiceModelPath(voice.getModelUrl())
            .fairytaleId(fairytaleId)
            .audioBookAudioPath(AudioBook.getBaseDirectory(voice.getExternalId()))
            .build();
    }
}
