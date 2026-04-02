package dalbit.application.messaging.push.dto;

import java.util.List;

public record FcmTokenInvalidatedEvent(
    List<String> invalidTokens)
{ }
