package dalbit.application.messaging.queue.dto;

import java.util.List;

public record VoiceTrainingCompleteEvent(
    Long userId,
    String voiceExternalId,
    List<String> tokens,
    boolean isSuccess
) {
}
