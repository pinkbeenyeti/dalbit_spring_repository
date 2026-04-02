package dalbit.adapter.persistence.jpa.external.userDevice.out;

import dalbit.adapter.persistence.jpa.external.userDevice.entity.UserDeviceJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDeviceJpaRepository extends JpaRepository<UserDeviceJpaEntity, Long> {
    List<UserDeviceJpaEntity> findAllByUserId(Long userId);

    @Query("SELECT u FROM UserDeviceJpaEntity u WHERE u.userId = :userId AND u.fcmToken IS NOT NULL AND TRIM(u.fcmToken) <> ''")
    List<UserDeviceJpaEntity> findValidDevicesByUserId(@Param("userId") Long userId);

    Optional<UserDeviceJpaEntity> findByDeviceUniqueId(String deviceUniqueId);

    Optional<UserDeviceJpaEntity> findByUserIdAndDeviceUniqueId(Long userId, String deviceUniqueId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE UserDeviceJpaEntity u SET u.fcmToken = NULL WHERE u.fcmToken IN :tokens")
    int nullifyFcmTokensIn(@Param("tokens") List<String> tokens);
}
