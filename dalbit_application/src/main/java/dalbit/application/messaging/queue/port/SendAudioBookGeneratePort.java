package dalbit.application.messaging.queue.port;

public interface SendAudioBookGeneratePort {
    void sendAudioBookGenerateRequest(String audioBookExternalId, String voiceExternalId, Long fairytaleId);
}
