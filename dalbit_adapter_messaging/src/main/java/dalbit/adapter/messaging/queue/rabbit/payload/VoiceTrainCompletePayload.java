package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VoiceTrainCompletePayload(
    @JsonProperty("voice_external_id") String voiceExternalId,
    @JsonProperty("voice_model_url") String voiceModelUrl
) {
    public VoiceTrainCompletePayload {
        if (!StringUtils.hasText(voiceExternalId)) {
            throw new IllegalArgumentException("AI 서버 응답 에러: voice_external_id가 누락되었습니다.");
        }

        if (!StringUtils.hasText(voiceModelUrl)) {
            throw new IllegalArgumentException("AI 서버 응답 에러: voice_model_url이 누락되었습니다.");
        }
    }
}
