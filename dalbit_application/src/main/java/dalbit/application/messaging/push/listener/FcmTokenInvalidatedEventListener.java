package dalbit.application.messaging.push.listener;

import dalbit.application.messaging.push.dto.FcmTokenInvalidatedEvent;
import dalbit.application.persistence.jpa.userDevice.port.DeleteUserDevicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmTokenInvalidatedEventListener {

    private final DeleteUserDevicePort deleteUserDevicePort;

    @EventListener
    @Transactional
    public void handleTokenInvalidation(FcmTokenInvalidatedEvent event) {
        log.info("[UserDevice-Domain] 토큰 무효화 이벤트 수신. DB 업데이트(NULL 처리)를 시작합니다.");

        try {
            deleteUserDevicePort.deleteFcmTokens(event.invalidTokens());
            log.info("[UserDevice-Domain] 총 {}건의 토큰 무효화 완료", event.invalidTokens().size());
        } catch (Exception e) {
            log.error("[UserDevice-Domain] 토큰 무효화 처리 중 DB 에러 발생: {}", e.getMessage(), e);
        }
    }
}
