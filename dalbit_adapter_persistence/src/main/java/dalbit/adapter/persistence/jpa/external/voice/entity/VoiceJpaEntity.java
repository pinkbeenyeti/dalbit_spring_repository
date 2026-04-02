package dalbit.adapter.persistence.jpa.external.voice.entity;

import dalbit.domain.voice.RegistrationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "voice", indexes = {
    @Index(name = "idx_voice_user_id", columnList = "user_id"),
    @Index(name = "idx_voice_external_id", columnList = "external_id", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @Column(nullable = false)
    private String name;

    @Column(name = "model_url")
    private String modelUrl;

    @Builder
    private VoiceJpaEntity(Long id, Long userId, String externalId, RegistrationStatus status, String name, String modelUrl) {
        this.id = id;
        this.userId = userId;
        this.externalId = externalId;
        this.status = status;
        this.name = name;
        this.modelUrl = modelUrl;
    }
}
