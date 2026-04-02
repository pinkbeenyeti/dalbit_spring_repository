package dalbit.adapter.persistence.jpa.external.device.entity;

import dalbit.domain.device.DeviceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "device", indexes =  {
    @Index(name = "idx_device_user_id", columnList = "user_id"),
    @Index(name = "idx_device_serial_number", columnList = "serial_number")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name = "device_secret", nullable = false, length = 255)
    private String deviceSecret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;

    private String name;

    @Builder
    private DeviceJpaEntity(Long id, Long userId, String serialNumber, String deviceSecret, DeviceType type, String name) {
        this.id = id;
        this.userId = userId;
        this.serialNumber = serialNumber;
        this.deviceSecret = deviceSecret;
        this.type = type;
        this.name = name;
    }
}
