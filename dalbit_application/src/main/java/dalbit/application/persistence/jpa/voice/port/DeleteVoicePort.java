package dalbit.application.persistence.jpa.voice.port;

public interface DeleteVoicePort {
    void deleteVoiceByUserIdAndExternalId(Long userId, String externalId);
}
