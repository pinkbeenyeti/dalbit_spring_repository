package dalbit.adapter.persistence.jpa.external.device.out;

import dalbit.adapter.persistence.jpa.external.device.entity.DeviceJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceJpaRepository extends JpaRepository<DeviceJpaEntity, Long> {
    Optional<DeviceJpaEntity> findBySerialNumber(String serialNumber);
    List<DeviceJpaEntity> findAllByUserId(Long userId);

}
