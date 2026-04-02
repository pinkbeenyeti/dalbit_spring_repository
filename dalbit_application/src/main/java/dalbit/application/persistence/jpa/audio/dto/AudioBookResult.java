package dalbit.application.persistence.jpa.audio.dto;

import dalbit.domain.audio.GenerationStatus;

public record AudioBookResult(
    String externalId,
    String voiceExternalId,
    Long fairytaleId,
    GenerationStatus status,
    String audioUrl
) { }
