package dalbit.application.messaging.push.port;

import java.util.List;
import java.util.Map;

public interface NotificationPort {
    void sendMulticastNotification(List<String> tokens, String title, String body);
    void sendMulticastNotification(List<String> tokens, String title, String body, Map<String, String> data);
}
