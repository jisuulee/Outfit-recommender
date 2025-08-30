package com.outfitly.outfit_recommender.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Table(name = "verification",
        indexes = {
                @Index(name="idx_email_purpose", columnList="email,purpose")
        })
public class Verification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255)
    private String email;

    @Column(nullable=false, length=20)
    private String purpose; // "SIGN_UP", "RESET_PW", "CHANGE_EMAIL" 등

    @Column(nullable=false, length=10)
    private String code; // 6자리 숫자 등

    @Column(nullable=false)
    private LocalDateTime expiresAt;

    @Column(nullable=false)
    private boolean used;

    @Column(nullable=false)
    private int attempts; // 검증 시 실패 횟수 누적

    @Column(nullable=false)
    private LocalDateTime createdAt;

    private LocalDateTime usedAt;

    // 재발송 쿨다운 체크용
    private LocalDateTime lastSentAt;
}
