package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AudioBookGenerateRequestPayload(
    @JsonProperty("audioBook_audio_external_id") String audioBookExternalId,
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_model_path") String voiceModelPath,
    @JsonProperty("fairytale_script_path") String fairytaleScriptPath,
    @JsonProperty("audioBook_audio_path") String audioBookAudioPath
) {

    public static AudioBookGenerateRequestPayload from(String audioBookExternalId, String voiceExternalId, String fairytaleScriptPath) {
        return AudioBookGenerateRequestPayload.builder()
            .audioBookExternalId(audioBookExternalId)
            .voiceExternalId(voiceExternalId)
            .voiceModelPath("dalbit/voice/" + voiceExternalId + "/model")
            .fairytaleScriptPath(fairytaleScriptPath)
            .audioBookAudioPath("dalbit/audioBook/" + audioBookExternalId + "/audio")
            .build();
    }
}
