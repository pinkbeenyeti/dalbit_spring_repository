package dalbit.domain.audio;

import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AudioBook {

    private final Long id;
    private final String externalId;
    private final Long userId;
    private final Long voiceId;
    private final Long fairytaleId;
    private GenerationStatus status;
    private String audioUrl;

    @Builder
    private AudioBook(Long id, String externalId, Long userId, Long voiceId, Long fairytaleId, GenerationStatus status, String audioUrl) {
        this.id = id;
        this.externalId = externalId;
        this.userId = userId;
        this.voiceId = voiceId;
        this.fairytaleId = fairytaleId;
        this.status = status;
        this.audioUrl = audioUrl;
    }

    public static AudioBook generate(Long userId, Long fairytaleId, Long voiceId) {
        return new AudioBook(null, UUID.randomUUID().toString(), userId, voiceId, fairytaleId, GenerationStatus.PROCESSING, null);
    }

    public void complete(String audioUrl) {
        this.status = GenerationStatus.COMPLETED;
        this.audioUrl = audioUrl;
    }

    public void fail() {
        this.status = GenerationStatus.FAILED;
    }
}
