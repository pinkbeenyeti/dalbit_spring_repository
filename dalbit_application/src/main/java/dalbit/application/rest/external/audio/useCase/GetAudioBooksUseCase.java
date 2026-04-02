package dalbit.application.rest.external.audio.useCase;

import dalbit.application.persistence.jpa.audio.dto.AudioBookResult;
import java.util.List;

public interface GetAudioBooksUseCase {
    List<AudioBookResult> getAudioBooks(Long userId);
}
