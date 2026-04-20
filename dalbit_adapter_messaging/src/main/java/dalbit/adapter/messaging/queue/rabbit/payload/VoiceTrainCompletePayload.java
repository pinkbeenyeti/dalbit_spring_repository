package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VoiceTrainCompletePayload(
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_model_url") String voiceModelUrl,
    @JsonProperty("voice_training_status") String voiceTrainingStatus
) {
    public VoiceTrainCompletePayload {
        if (!StringUtils.hasText(voiceExternalId)) {
            throw new IllegalArgumentException("AI 서버 응답 에러: voice_external_id가 누락되었습니다.");
        }
        if (!StringUtils.hasText(voiceTrainingStatus)) {
            throw new IllegalArgumentException("AI 서버 응답 에러: voice_training_status가 누락되었습니다.");
        }
    }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(voiceTrainingStatus);
    }
}
