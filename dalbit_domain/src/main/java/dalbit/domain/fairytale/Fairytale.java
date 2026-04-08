package dalbit.domain.fairytale;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Fairytale {

    private final Long id;
    private final String title;
    private final Category category;
    private final String content;

    @Builder
    private Fairytale(Long id, String title, Category category, String content) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.content = content;
    }

}