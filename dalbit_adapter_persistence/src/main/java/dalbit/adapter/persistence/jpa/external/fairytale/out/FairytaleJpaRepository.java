package dalbit.adapter.persistence.jpa.external.fairytale.out;

import dalbit.adapter.persistence.jpa.external.fairytale.entity.FairytaleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FairytaleJpaRepository extends JpaRepository<FairytaleJpaEntity, Long> {
}
