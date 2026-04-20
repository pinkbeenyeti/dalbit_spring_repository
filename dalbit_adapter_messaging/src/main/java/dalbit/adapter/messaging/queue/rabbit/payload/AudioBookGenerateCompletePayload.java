package dalbit.adapter.messaging.queue.rabbit.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioBookGenerateCompletePayload(
    @JsonProperty("audioBook_external_id") String audioBookExternalId,
    @JsonProperty("audioBook_audio_url") String audioBookAudioUrl,
    @JsonProperty("audioBook_generation_status") String audioBookGenerationStatus
) {

    public AudioBookGenerateCompletePayload {
        if (!StringUtils.hasText(audioBookExternalId)) {
            throw new IllegalArgumentException("AI 서버 응답 에러: audioBook_external_id가 누락되었습니다.");
        }
        if (!StringUtils.hasText(audioBookGenerationStatus)) {
            throw new IllegalArgumentException("AI 서버 응답 에러: audioBook_generation_status가 누락되었습니다.");
        }
    }

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(audioBookGenerationStatus);
    }
}
