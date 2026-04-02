package dalbit.adapter.persistence.jpa.external.user.out;

import dalbit.adapter.persistence.jpa.external.user.entity.UserJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    @Query("SELECT u.id FROM UserJpaEntity u WHERE u.externalId = :externalId")
    Optional<Long> findIdByExternalId(@Param("externalId") String externalId);

    Optional<UserJpaEntity> findByExternalId(String externalId);

    Optional<UserJpaEntity> findByProviderId(String providerId);
}
