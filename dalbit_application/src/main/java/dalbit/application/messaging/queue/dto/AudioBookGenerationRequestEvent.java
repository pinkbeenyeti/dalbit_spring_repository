package dalbit.application.messaging.queue.dto;

import dalbit.domain.audio.AudioBook;
import dalbit.domain.voice.Voice;

public record AudioBookGenerationRequestEvent(
    AudioBook audioBook,
    Voice voice,
    Long fairytaleId
) {

}
