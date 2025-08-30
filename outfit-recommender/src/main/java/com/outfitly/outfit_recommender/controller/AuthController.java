package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.service.VerificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/email")
public class AuthController {

    private final VerificationService verificationService;

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody SendRequest req) {
        verificationService.sendCode(req.getEmail(), req.getPurpose());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyRequest req) {
        boolean ok = verificationService.verifyCode(req.getEmail(), req.getPurpose(), req.getCode());
        return ok ? ResponseEntity.ok().build()
                : ResponseEntity.status(400).body("코드가 일치하지 않습니다.");
    }

    @Data
    public static class SendRequest {
        private String email;
        private String purpose;
    }
    @Data
    public static class VerifyRequest {
        private String email;
        private String purpose;
        private String code;
    }
}
