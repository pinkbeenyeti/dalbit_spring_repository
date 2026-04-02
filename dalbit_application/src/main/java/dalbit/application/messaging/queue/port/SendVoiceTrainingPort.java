package dalbit.application.messaging.queue.port;

public interface SendVoiceTrainingPort {
    void sendVoiceTrainingRequest(String externalId);
}
