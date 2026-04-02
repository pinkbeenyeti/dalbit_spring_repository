package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record VoiceTrainingRequestPayload(
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_audio_path") String voiceAudioPath,
    @JsonProperty("voice_model_path") String voiceModelPath
) {

    public static VoiceTrainingRequestPayload from(String externalId) {
        return VoiceTrainingRequestPayload.builder()
            .voiceExternalId(externalId)
            .voiceAudioPath("dalbit/voice/" + externalId + "/audio")
            .voiceModelPath("dalbit/voice/" + externalId + "/model")
            .build();
    }
}
