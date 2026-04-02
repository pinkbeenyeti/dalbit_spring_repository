package dalbit.application.rest.external.fairytale.service;

import dalbit.application.rest.external.fairytale.useCase.GetFairytaleUseCase;
import dalbit.application.persistence.jpa.fairytale.port.LoadFairytalePort;
import dalbit.domain.fairytale.Fairytale;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FairytaleJpaService implements GetFairytaleUseCase {

    private final LoadFairytalePort loadFairytalePort;

    @Override
    @Transactional(readOnly = true)
    public List<Fairytale> getFairytale() {
        return loadFairytalePort.loadAllFairytale();
    }
}
