package dalbit.domain.audio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GenerationStatus {

    PROCESSING("생성 진행중"),
    COMPLETED("생성 완료"),
    FAILED("생성 실패");

    private final String description;
}
