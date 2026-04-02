package dalbit.adapter.persistence.jpa.external.userDevice.entity;

import dalbit.domain.userDevice.OsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_device", indexes = {
    @Index(name = "idx_user_device_user_id", columnList = "user_id"),
    @Index(name = "idx_user_device_device_unique_id", columnList = "device_unique_id"),
    @Index(name = "idx_user_device_fcm_token", columnList = "fcm_token")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDeviceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_unique_id", nullable = false, unique = true)
    private String deviceUniqueId;

    @Column(name = "fcm_token", nullable = false, unique = true)
    private String fcmToken;

    @Column(name = "os_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OsType osType;

    @Column(name = "last_active_at", nullable = false)
    private LocalDateTime lastActiveAt;

    @Builder
    private UserDeviceJpaEntity(Long id, Long userId, String deviceUniqueId, String fcmToken, OsType osType, LocalDateTime lastActiveAt) {
        this.id = id;
        this.userId = userId;
        this.deviceUniqueId = deviceUniqueId;
        this.fcmToken = fcmToken;
        this.osType = osType;
        this.lastActiveAt = lastActiveAt;
    }
}
