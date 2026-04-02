package dalbit.adapter.persistence.jpa.external.fairytale.out;

import dalbit.adapter.persistence.jpa.external.fairytale.mapper.FairytaleJpaMapper;
import dalbit.application.persistence.jpa.fairytale.port.LoadFairytalePort;
import dalbit.domain.fairytale.Fairytale;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FairytalePersistenceAdapter implements LoadFairytalePort {

    private final FairytaleJpaRepository fairytaleJpaRepository;
    private final FairytaleJpaMapper fairytaleJpaMapper;

    @Override
    public List<Fairytale> loadAllFairytale() {
        return fairytaleJpaRepository.findAll().stream()
            .map(fairytaleJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Fairytale> loadFairytaleById(Long fairytaleId) {
        return fairytaleJpaRepository.findById(fairytaleId)
            .map(fairytaleJpaMapper::toDomain);
    }
}
