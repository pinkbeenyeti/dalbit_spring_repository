package dalbit.application.messaging.queue.listener;

import dalbit.application.messaging.push.port.NotificationPort;
import dalbit.application.messaging.queue.dto.VoiceTrainingCompleteEvent;
import dalbit.application.messaging.queue.dto.VoiceTrainingRequestEvent;
import dalbit.application.messaging.queue.port.SendVoiceTrainingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceTrainingEventListener {

    private static final String NOTIFY_TITLE = "목소리 학습 완료! \uD83C\uDF99\uFE0F";
    private static final String NOTIFY_BODY = "요청하신 목소리 학습이 성공적으로 끝났어요. 지금 바로 동화를 만들어 보세요!";

    private final SendVoiceTrainingPort sendVoiceTrainingPort;
    private final NotificationPort notificationPort;

    @TransactionalEventListener(phase =  TransactionPhase.AFTER_COMMIT)
    public void handleVoiceTrainingRequestEvent(VoiceTrainingRequestEvent event) {
        log.info("DB 커밋 완료. 목소리 학습 메시지 큐 전송 시작 - voice_external_id: {}", event.externalId());
        sendVoiceTrainingPort.sendVoiceTrainingRequest(event.externalId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleVoiceTrainingCompleteEvent(VoiceTrainingCompleteEvent event) {
        log.info("DB 커밋 완료. 목소리 학습 완료 푸시 알림 전송 시작 - user_Id: {}, voice_external_id: {}", event.userId(), event.voiceExternalId());

        try {
            notificationPort.sendMulticastNotification(event.tokens(), NOTIFY_TITLE, NOTIFY_BODY);
        } catch (Exception e) {
            log.error("목소리 학습 완료 FCM 알림 전송 실패 - user_Id: {}, voice_external_id: {}", event.userId(), event.voiceExternalId());
        }
    }
}
