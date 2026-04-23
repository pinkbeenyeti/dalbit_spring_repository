package dalbit.application.storage.event;

import dalbit.application.storage.port.DeleteStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageEventListener {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000L;

    private final DeleteStoragePort deleteStoragePort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStorageDeleteEvent(StorageDeleteEvent event) {
        if (event.isEmpty()) {
            return;
        }

        log.info("[StorageEvent] 스토리지 삭제 시작 (파일: {}건, 디렉터리: {}건)",
            event.filePaths().size(), event.directoryPaths().size());

        executeWithRetry(() -> {
            deleteStoragePort.deleteFiles(event.filePaths());
            deleteStoragePort.deleteDirectories(event.directoryPaths());
        });
    }

    private void executeWithRetry(Runnable action) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                log.warn("[StorageEvent] 삭제 시도 실패 ({}/{}): {}", attempt, MAX_RETRIES, e.getMessage());

                if (attempt == MAX_RETRIES) {
                    log.error("[StorageEvent] 최대 재시도 횟수 초과. 수동 확인이 필요할 수 있습니다.", e);
                    break;
                }

                backoff(attempt);
            }
        }
    }

    private void backoff(int attempt) {
        try {
            Thread.sleep(RETRY_DELAY_MS * attempt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
