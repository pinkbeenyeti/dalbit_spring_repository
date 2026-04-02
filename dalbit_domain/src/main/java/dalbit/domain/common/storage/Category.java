package dalbit.domain.common.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    VOICE("voice/record/%s/%d.wav");

    private final String basePath;

    public String generatePath(String targetId, int index) {
        return String.format(basePath, targetId, index);
    }
}
