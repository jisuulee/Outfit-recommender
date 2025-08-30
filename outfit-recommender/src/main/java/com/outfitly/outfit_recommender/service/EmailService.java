package com.outfitly.outfit_recommender.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from; // 반드시 인증 계정과 동일한 주소

    public void send(String to, String subject, String text) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            // true = 멀티파트 허용; "UTF-8" 명시
            MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");

            // From은 "plain email"로만. (표시명 넣고 싶으면 아래 주석처럼)
            helper.setFrom(from);
            // helper.setFrom(new InternetAddress(from, "Outfitly", "UTF-8"));

            // 수신자 주소는 공백/쉼표 제거 등 최소 정제 권장
            helper.setTo(to.trim());

            helper.setSubject(subject);
            // 두 번째 파라미터 false = 텍스트, true = HTML
            helper.setText(text, false);

            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("메일 발송 실패: " + e.getMessage(), e);
        }
    }
}
