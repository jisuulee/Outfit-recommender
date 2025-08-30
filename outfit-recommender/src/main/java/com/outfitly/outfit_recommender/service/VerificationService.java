package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.entity.Verification;
import com.outfitly.outfit_recommender.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationRepository repo;
    private final EmailService mailService;

    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 5;

    private String generate6Digit() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(900_000) + 100_000;
        return Integer.toString(n);
    }

    @Transactional
    public void sendCode(String email, String purpose) {
        // 재발송 쿨다운
        repo.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose).ifPresent(latest -> {
            if (latest.getLastSentAt() != null &&
                    Duration.between(latest.getLastSentAt(), LocalDateTime.now()).compareTo(RESEND_COOLDOWN) < 0) {
                throw new IllegalArgumentException("요청이 너무 잦습니다. 잠시 후 다시 시도해주세요.");
            }
        });

        String code = generate6Digit();
        Verification token = Verification.builder()
                .email(email)
                .purpose(purpose)
                .code(code)
                .expiresAt(LocalDateTime.now().plus(EXPIRES_IN))
                .used(false)
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .lastSentAt(LocalDateTime.now())
                .build();
        repo.save(token);

        String subject = "[Outfitly] 이메일 인증 코드";
        String body = """
                인증 코드는 %s 입니다.
                유효시간: %d분
                """.formatted(code, EXPIRES_IN.toMinutes());
        mailService.send(email, subject, body);
    }

    @Transactional
    public boolean verifyCode(String email, String purpose, String code) {
        // 인증 코드 정규화
        code    = code.replaceAll("\\s", "");

        Verification latest = repo.findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청이 존재하지 않습니다."));

        if (latest.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 코드입니다.");
        }
        if (latest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        if (latest.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("시도 횟수를 초과했습니다.");
        }

        if (!latest.getCode().equals(code)) {
            latest.setAttempts(latest.getAttempts() + 1);
            return false;
        }

        latest.setUsed(true);
        latest.setUsedAt(LocalDateTime.now());
        return true;
    }
}
