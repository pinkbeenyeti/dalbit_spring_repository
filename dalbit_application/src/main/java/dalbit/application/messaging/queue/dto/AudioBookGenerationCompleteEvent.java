package dalbit.application.messaging.queue.dto;

import java.util.List;

public record AudioBookGenerationCompleteEvent(
    Long userId,
    String audioBookExternalId,
    List<String> tokens,
    boolean isSuccess
) {
}
