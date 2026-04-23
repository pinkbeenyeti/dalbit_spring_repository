package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import dalbit.domain.voice.Voice;
import lombok.Builder;

@Builder
public record VoiceTrainingRequestPayload(
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_audio_path") String voiceAudioPath,
    @JsonProperty("voice_model_path") String voiceModelPath
) {

    public static VoiceTrainingRequestPayload from(Voice voice) {
        return VoiceTrainingRequestPayload.builder()
            .voiceExternalId(voice.getExternalId())
            .voiceAudioPath(voice.getRecordDirectory())
            .voiceModelPath(voice.getModelUrl())
            .build();
    }
}
