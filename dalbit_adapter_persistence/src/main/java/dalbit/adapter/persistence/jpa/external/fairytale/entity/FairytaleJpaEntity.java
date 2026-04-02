package dalbit.adapter.persistence.jpa.external.fairytale.entity;
import dalbit.domain.fairytale.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "fairytale")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FairytaleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(name = "content_url", nullable = false)
    private String contentUrl;

    @Builder
    public FairytaleJpaEntity(Long id, String title, Category category, String contentUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.contentUrl = contentUrl;
    }
}
