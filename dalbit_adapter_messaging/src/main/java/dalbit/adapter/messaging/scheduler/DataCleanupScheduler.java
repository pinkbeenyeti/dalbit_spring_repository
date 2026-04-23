package dalbit.adapter.messaging.scheduler;

import dalbit.application.rest.external.audio.useCase.DeleteAudioBookUseCase;
import dalbit.application.rest.external.voice.useCase.DeleteVoiceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataCleanupScheduler {

    private final DeleteVoiceUseCase deleteVoiceUseCase;
    private final DeleteAudioBookUseCase deleteAudioBookUseCase;

    @Value("${voice.cleanup.retention-hours:6}")
    private int retentionHours;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredData() {
        log.info("데이터 정리 및 상태 체크 시작 (기준 시간: {}시간)", retentionHours);
        
        try {
            deleteVoiceUseCase.handleStuckVoices(retentionHours);
            deleteAudioBookUseCase.handleStuckAudioBooks(retentionHours);
            log.info("정체된 데이터 상태 변경 완료");
        } catch (Exception e) {
            log.error("데이터 상태 변경 중 오류 발생", e);
        }

        try {
            deleteVoiceUseCase.cleanupExpiredVoices(retentionHours);
            log.info("만료된 목소리 데이터 정리 완료");
        } catch (Exception e) {
            log.error("만료된 목소리 데이터 정리 중 오류 발생", e);
        }

        try {
            deleteAudioBookUseCase.cleanupExpiredAudioBooks(retentionHours);
            log.info("만료된 오디오북 데이터 정리 완료");
        } catch (Exception e) {
            log.error("만료된 오디오북 데이터 정리 중 오류 발생", e);
        }
    }
}
