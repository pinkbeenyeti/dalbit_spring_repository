package dalbit.adapter.messaging.push.fcm.out;

import dalbit.application.messaging.push.dto.FcmTokenInvalidatedEvent;
import dalbit.application.messaging.push.port.NotificationPort;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmNotificationAdapter implements NotificationPort {

    private final Executor fcmExecutor;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void sendMulticastNotification(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        Notification notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();

        MulticastMessage message = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(notification)
            .build();

        ApiFuture<BatchResponse> future = FirebaseMessaging.getInstance().sendEachForMulticastAsync(message);
        ApiFutures.addCallback(future, new ApiFutureCallback<>() {
            @Override
            public void onSuccess(BatchResponse response) {
                handleResponse(response, tokens);
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("[FCM] 메시지 전송 중 치명적 네트워크 오류 발생: {}", t.getMessage(), t);
            }
        }, fcmExecutor);
    }

    private void handleResponse(BatchResponse response, List<String> tokens) {
        if (response.getFailureCount() == 0) {
            log.info("[FCM] 메시지 전송 전체 성공: {}건", response.getSuccessCount());
            return;
        }

        List<SendResponse> responses = response.getResponses();
        List<String> tokensToDelete = new ArrayList<>();

        for (int i=0; i<responses.size(); i++) {
            SendResponse sendResponse = responses.get(i);

            if (!sendResponse.isSuccessful()) {
                String failedToken = tokens.get(i);

                FirebaseMessagingException exception = sendResponse.getException();
                MessagingErrorCode errorCode = exception.getMessagingErrorCode();

                if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                    tokensToDelete.add(failedToken);
                }

                log.warn("[FCM] 전송 실패 - 토큰: {}, 사유: {}", failedToken, errorCode);
            }
        }

        if (!tokensToDelete.isEmpty()) {
            log.info("[FCM] 무효화된 토큰 {}건에 대해 DB 삭제를 요청합니다.", tokensToDelete.size());
            eventPublisher.publishEvent(new FcmTokenInvalidatedEvent(tokensToDelete));
        }
    }

}
