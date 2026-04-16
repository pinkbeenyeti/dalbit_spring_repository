package dalbit.adapter.messaging.scheduler;

import dalbit.application.rest.external.voice.useCase.DeleteVoiceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceCleanupScheduler {

    private final DeleteVoiceUseCase deleteVoiceUseCase;

    @Value("${voice.cleanup.retention-hours:24}")
    private int retentionHours;

    /**
     * 매 시간마다 실행되어 업로드 대기 중이거나 실패한 목소리 중
     * 설정된 보관 기간(retentionHours)이 지난 데이터를 삭제합니다.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredVoices() {
        log.info("만료된 목소리 데이터 정리 시작 (보관 기간: {}시간)", retentionHours);
        try {
            deleteVoiceUseCase.cleanupExpiredVoices(retentionHours);
            log.info("만료된 목소리 데이터 정리 완료");
        } catch (Exception e) {
            log.error("만료된 목소리 데이터 정리 중 오류 발생", e);
        }
    }
}
