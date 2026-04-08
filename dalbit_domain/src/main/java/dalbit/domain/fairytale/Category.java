package dalbit.domain.fairytale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    TRADITIONAL("C1", "전래동화"),
    CLASSIC("C2", "명작동화"),
    AESOP("C3", "이솝우화");

    private final String code;
    private final String title;
}
