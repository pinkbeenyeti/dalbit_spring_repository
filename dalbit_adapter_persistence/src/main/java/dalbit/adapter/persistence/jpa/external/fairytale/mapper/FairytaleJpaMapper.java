package dalbit.adapter.persistence.jpa.external.fairytale.mapper;

import dalbit.adapter.persistence.jpa.external.fairytale.entity.FairytaleJpaEntity;
import dalbit.domain.fairytale.Fairytale;
import org.springframework.stereotype.Component;

@Component
public class FairytaleJpaMapper {

    public FairytaleJpaEntity toEntity(Fairytale fairytale) {
        if (fairytale == null) return null;

        return FairytaleJpaEntity.builder()
            .id(fairytale.getId())
            .title(fairytale.getTitle())
            .category(fairytale.getCategory())
            .contentUrl(fairytale.getContentUrl())
            .build();
    }

    public Fairytale toDomain(FairytaleJpaEntity entity) {
        if (entity == null) return null;

        return Fairytale.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .category(entity.getCategory())
            .contentUrl(entity.getContentUrl())
            .build();
    }

}
