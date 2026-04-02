package dalbit.application.messaging.push.port;

import java.util.List;

public interface NotificationPort {
    void sendMulticastNotification(List<String> tokens, String title, String body);
}
