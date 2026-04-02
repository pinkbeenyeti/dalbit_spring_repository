package dalbit.application.persistence.jpa.userDevice.port;

import java.util.List;

public interface DeleteUserDevicePort {
    int deleteFcmTokens(List<String> tokens);
}
