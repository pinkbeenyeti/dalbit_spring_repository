package dalbit.application.messaging.queue.listener;

import dalbit.application.messaging.push.port.NotificationPort;
import dalbit.application.messaging.queue.dto.AudioBookGenerationCompleteEvent;
import dalbit.application.messaging.queue.dto.AudioBookGenerationRequestEvent;
import dalbit.application.messaging.queue.port.SendAudioBookGeneratePort;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioBookGenerateEventListener {

    private static final String NOTIFY_TITLE = "달빛 동화책 완성! \uD83C\uDF19";
    private static final String NOTIFY_BODY = "요청하신 오디오북이 완성되었어요. 지금 바로 아이에게 들려주세요";

    private final SendAudioBookGeneratePort sendAudioBookGeneratePort;
    private final NotificationPort notificationPort;

    @TransactionalEventListener(phase =  TransactionPhase.AFTER_COMMIT)
    public void handleAudioBookGenerationRequestEvent(AudioBookGenerationRequestEvent event) {
        log.info("DB 커밋 완료. 오디오 북 생성 메시지 큐 전송 시작: {}", event.audioBook().getExternalId());
        sendAudioBookGeneratePort.sendAudioBookGenerateRequest(event.audioBook(), event.voice(), event.fairytaleId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAudioBookGenerationCompletionEvent(AudioBookGenerationCompleteEvent event) {
        log.info("DB 커밋 완료. 오디오북 생성 결과 푸시 알림 전송 시작 - user_Id: {}, audioBook_external_id: {}, success: {}", 
            event.userId(), event.audioBookExternalId(), event.isSuccess());

        String title = event.isSuccess() ? "달빛 동화책 완성! \uD83C\uDF19" : "동화책 생성 실패 \u26A0\uFE0F";
        String body = event.isSuccess() ? 
            "요청하신 오디오북이 완성되었어요. 지금 바로 아이에게 들려주세요" : 
            "오디오북을 만드는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";

        try {
            Map<String, String> data = Map.of(
                "type", "audio_book_complete",
                "audioBookExternalId", event.audioBookExternalId(),
                "isSuccess", String.valueOf(event.isSuccess())
            );

            notificationPort.sendMulticastNotification(event.tokens(), title, body, data);
        } catch (Exception e) {
            log.error("오디오북 알림 전송 실패 - user_Id: {}, audioBook_external_id: {}", event.userId(), event.audioBookExternalId());
        }
    }
}
