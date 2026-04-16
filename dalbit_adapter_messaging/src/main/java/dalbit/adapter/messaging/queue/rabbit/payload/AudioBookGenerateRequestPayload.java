package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AudioBookGenerateRequestPayload(
    @JsonProperty("audioBook_external_id") String audioBookExternalId,
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_model_path") String voiceModelPath,
    @JsonProperty("fairytale_id") Long fairytaleId,
    @JsonProperty("audioBook_audio_path") String audioBookAudioPath
) {

    public static AudioBookGenerateRequestPayload from(String audioBookExternalId, String voiceExternalId, Long fairytaleId) {
        return AudioBookGenerateRequestPayload.builder()
            .audioBookExternalId(audioBookExternalId)
            .voiceExternalId(voiceExternalId)
            .voiceModelPath("dalbit/voice/" + voiceExternalId + "/model")
            .fairytaleId(fairytaleId)
            .audioBookAudioPath("dalbit/audioBook/" + audioBookExternalId + "/audio")
            .build();
    }
}
