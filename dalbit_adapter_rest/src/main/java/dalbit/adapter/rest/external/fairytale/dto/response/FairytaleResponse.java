package dalbit.adapter.rest.external.fairytale.dto.response;

import dalbit.domain.fairytale.Fairytale;

public record FairytaleResponse(
    Long id,
    String title,
    String category
) {
    public static FairytaleResponse from(Fairytale fairytale) {
        return new FairytaleResponse(
            fairytale.getId(),
            fairytale.getTitle(),
            fairytale.getCategory().getTitle()
        );
    }
}
