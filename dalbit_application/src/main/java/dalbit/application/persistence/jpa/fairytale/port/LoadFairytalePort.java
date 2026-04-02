package dalbit.application.persistence.jpa.fairytale.port;

import dalbit.domain.fairytale.Fairytale;
import java.util.List;
import java.util.Optional;

public interface LoadFairytalePort {
    List<Fairytale> loadAllFairytale();
    Optional<Fairytale> loadFairytaleById(Long fairytaleId);
}
