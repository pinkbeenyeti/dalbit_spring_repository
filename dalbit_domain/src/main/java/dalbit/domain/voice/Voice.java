package dalbit.domain.voice;

import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Voice {

    private final Long id;
    private final Long userId;
    private final String externalId;
    private RegistrationStatus status;
    private VoiceName name;
    private String modelUrl;
    private final LocalDateTime createdAt;

    @Builder
    private Voice(Long id, Long userId, String externalId, RegistrationStatus status, VoiceName name, String modelUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.externalId = externalId;
        this.status = status;
        this.name = name;
        this.modelUrl = modelUrl;
        this.createdAt = createdAt;
    }

    public static Voice register(Long userId, VoiceName name) {
        return new Voice(null, userId, UUID.randomUUID().toString(), RegistrationStatus.WAITING_UPLOAD, name, null, LocalDateTime.now());
    }

    public void startTraining() {
        if (this.status == RegistrationStatus.COMPLETED || this.status == null) {
            throw new DalbitException(ErrorCode.INVALID_VOICE_STATUS);
        }

        this.status = RegistrationStatus.PROCESSING;
    }

    public void completeTraining(String modelUrl) {
        if (this.status != RegistrationStatus.PROCESSING) {
            throw new DalbitException(ErrorCode.INVALID_VOICE_STATUS);
        }

        this.status = RegistrationStatus.COMPLETED;
        this.modelUrl = modelUrl;
    }

    public void failTraining() {
        this.status = RegistrationStatus.FAILED;
    }

    public void updateVoiceName(VoiceName newName) {
        this.name = newName;
    }

    public String getRecordDirectory() {
        return String.format("voice/record/%s/%s/",
            this.externalId, this.name.getValue());
    }

    public String getRecordFilePath(int index) {
        return getRecordDirectory() + index + ".wav";
    }

    public String getModelPath() {
        return String.format("voice/model/%s", this.externalId);
    }
}
