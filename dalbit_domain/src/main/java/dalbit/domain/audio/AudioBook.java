package dalbit.domain.audio;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AudioBook {

    private final Long id;
    private final String externalId;
    private final Long userId;
    private final Long voiceId;
    private final Long fairytaleId;
    private GenerationStatus status;
    private String audioUrl;
    private final LocalDateTime createdAt;

    @Builder
    private AudioBook(Long id, String externalId, Long userId, Long voiceId, Long fairytaleId, GenerationStatus status, String audioUrl, LocalDateTime createdAt) {
        this.id = id;
        this.externalId = externalId;
        this.userId = userId;
        this.voiceId = voiceId;
        this.fairytaleId = fairytaleId;
        this.status = status;
        this.audioUrl = audioUrl;
        this.createdAt = createdAt;
    }

    public static AudioBook generate(Long userId, Long fairytaleId, Long voiceId) {
        return new AudioBook(null, UUID.randomUUID().toString(), userId, voiceId, fairytaleId, GenerationStatus.PROCESSING, null, LocalDateTime.now());
    }

    public void fail() {
        this.status = GenerationStatus.FAILED;
    }

    public void complete(String audioUrl) {
        this.status = GenerationStatus.COMPLETED;
        this.audioUrl = audioUrl;
    }
}
