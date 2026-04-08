package dalbit.application.messaging.queue.dto;

public record AudioBookGenerationRequestEvent(
    String externalId,
    String voiceExternalId,
    Long fairytaleId
) {

}
