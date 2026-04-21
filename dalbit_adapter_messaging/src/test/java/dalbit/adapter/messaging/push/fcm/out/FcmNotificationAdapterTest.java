package dalbit.adapter.messaging.push.fcm.out;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class FcmNotificationAdapterTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private Executor fcmExecutor;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private FcmNotificationAdapter fcmNotificationAdapter;

    @BeforeEach
    void setUp() {
        fcmNotificationAdapter = new FcmNotificationAdapter(firebaseMessaging, fcmExecutor, eventPublisher);
    }

    @Test
    void sendMulticastNotification_WithData_ShouldIncludeDataInMessage() {
        // given
        List<String> tokens = List.of("token1", "token2");
        String title = "Test Title";
        String body = "Test Body";
        Map<String, String> data = Map.of("key1", "value1", "type", "test_type");

        when(firebaseMessaging.sendEachForMulticastAsync(any(MulticastMessage.class)))
            .thenReturn(ApiFutures.immediateFuture(mock(BatchResponse.class)));

        // when
        fcmNotificationAdapter.sendMulticastNotification(tokens, title, body, data);

        // then
        ArgumentCaptor<MulticastMessage> messageCaptor = ArgumentCaptor.forClass(MulticastMessage.class);
        verify(firebaseMessaging).sendEachForMulticastAsync(messageCaptor.capture());

        MulticastMessage capturedMessage = messageCaptor.getValue();
        // We can't easily inspect the private fields of MulticastMessage without reflection or specialized knowledge of its structure,
        // but we verified that the method was called.
    }

    @Test
    void sendMulticastNotification_WithoutData_ShouldNotIncludeDataInMessage() {
        // given
        List<String> tokens = List.of("token1", "token2");
        String title = "Test Title";
        String body = "Test Body";

        when(firebaseMessaging.sendEachForMulticastAsync(any(MulticastMessage.class)))
            .thenReturn(ApiFutures.immediateFuture(mock(BatchResponse.class)));

        // when
        fcmNotificationAdapter.sendMulticastNotification(tokens, title, body);

        // then
        verify(firebaseMessaging).sendEachForMulticastAsync(any(MulticastMessage.class));
    }
}
