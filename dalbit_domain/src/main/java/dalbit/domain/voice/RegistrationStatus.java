package dalbit.domain.voice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegistrationStatus {

    WAITING_UPLOAD("업로드 대기중"),
    PROCESSING("학습 진행중"),
    COMPLETED("학습 완료"),
    FAILED("학습 실패");

    private final String description;
}
