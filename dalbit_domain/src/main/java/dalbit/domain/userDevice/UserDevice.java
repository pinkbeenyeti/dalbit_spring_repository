package dalbit.domain.userDevice;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserDevice {

    private final Long id;
    private Long userId;
    private final String deviceUniqueId;
    private String fcmToken;
    private final OsType osType;
    private LocalDateTime lastActiveAt;

    @Builder
    private UserDevice(Long id, Long useId, String deviceUniqueId, String fcmToken, OsType osType, LocalDateTime localDateTime) {
        this.id = id;
        this.userId = useId;
        this.deviceUniqueId = deviceUniqueId;
        this.fcmToken = fcmToken;
        this.osType = osType;
        this.lastActiveAt = localDateTime;
    }

    public static UserDevice register(Long userId, String deviceUniqueId, String fcmToken, OsType osType) {
        return new UserDevice(null, userId, deviceUniqueId, fcmToken, osType, LocalDateTime.now());
    }

    public void updateToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateTokenAndUser(Long currentUserId, String newFcmToken) {
        this.userId = currentUserId;
        this.fcmToken = newFcmToken;
        this.lastActiveAt = LocalDateTime.now();
    }

}
