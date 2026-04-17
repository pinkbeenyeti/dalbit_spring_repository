package dalbit.adapter.persistence.jpa.external.audio.entity;

import dalbit.domain.audio.GenerationStatus;
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

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

@Entity
@Getter
@Table(name = "audio_book", indexes = {
    @Index(name = "idx_audio_book_external_id", columnList = "external_id", unique = true),
    @Index(name = "idx_audio_book_user_id", columnList = "user_id"),
    @Index(name = "idx_audio_book_voice_id", columnList = "voice_id"),
    @Index(name = "idx_audio_book_fairytale_id", columnList = "fairytale_id"),
    @Index(name = "idx_audio_book_status_created_at", columnList = "status, created_at")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AudioBookJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "voice_id", nullable = false)
    private Long voiceId;

    @Column(name = "fairytale_id", nullable = false)
    private Long fairytaleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenerationStatus status;

    @Column(name = "audio_url")
    private String audioUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private AudioBookJpaEntity(Long id, String externalId, Long userId, Long voiceId, Long fairytaleId, GenerationStatus status, String audioUrl, LocalDateTime createdAt) {
        this.id = id;
        this.externalId = externalId;
        this.userId = userId;
        this.voiceId = voiceId;
        this.fairytaleId = fairytaleId;
        this.status = status;
        this.audioUrl = audioUrl;
        this.createdAt = createdAt;
    }
}
