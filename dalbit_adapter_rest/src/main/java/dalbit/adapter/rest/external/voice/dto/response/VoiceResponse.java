package dalbit.adapter.rest.external.voice.dto.response;

import dalbit.domain.voice.RegistrationStatus;
import dalbit.domain.voice.Voice;

public record VoiceResponse(
    String externalId,
    String name,
    RegistrationStatus status
) {

    public static VoiceResponse from(Voice voice) {
        return new VoiceResponse(
            voice.getExternalId(),
            voice.getName().getValue(),
            voice.getStatus()
        );
    }
}
