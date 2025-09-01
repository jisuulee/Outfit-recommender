package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.service.UserService;
import com.outfitly.outfit_recommender.service.VerificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRecoveryController {

    private final VerificationService verificationService;
    private final UserService userService;

    // 아이디 찾기: 이메일로 유저명 반환
    @PostMapping("/id/find")
    public ResponseEntity<?> findId(@RequestBody FindIdRequest req) {
        String username = userService.findUsernameByEmail(req.getEmail());
        if (username == null) {
            return ResponseEntity.status(404).body("해당 이메일의 계정을 찾을 수 없습니다.");
        }
        String masked = maskUsername(username); // ex) ab***23
        return ResponseEntity.ok(new FindIdResponse(masked));
    }

    // 비밀번호 재설정 코드 발송
    @PostMapping("/password/request")
    public ResponseEntity<?> requestReset(@RequestBody ResetPwRequest req) {
        // 이메일로 코드 발송 (purpose=RESET_PW)
        verificationService.sendCode(req.getEmail(), "RESET_PW");
        return ResponseEntity.ok().build();
    }

    // 비밀번호 재설정 코드 확인
    @PostMapping("/password/verify")
    public ResponseEntity<?> verifyReset(@RequestBody VerifyResetPwRequest req) {
        boolean ok = verificationService.verifyCode(req.getEmail(), "RESET_PW", req.getCode());
        return ok ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body("코드가 일치하지 않거나 만료되었습니다.");
    }

    // 새 비밀번호 적용
    @PostMapping("/password/reset")
    public ResponseEntity<?> doReset(@RequestBody DoResetPwRequest req) {
        boolean ok = verificationService.verifyCode(req.getEmail(), "RESET_PW", req.getCode());
        if (!ok) return ResponseEntity.badRequest().body("코드가 일치하지 않거나 만료되었습니다.");

        userService.updatePasswordByEmail(req.getEmail(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }

    private String maskUsername(String username) {
        if (username.length() <= 2) return username.charAt(0) + "*";
        int show = Math.min(2, username.length());
        String visible = username.substring(0, show);
        return visible + "*".repeat(Math.max(1, username.length()-show));
    }

    @Data public static class FindIdRequest { private String email; }
    @Data public static class FindIdResponse { private final String username; }
    @Data public static class ResetPwRequest { private String email; }
    @Data public static class VerifyResetPwRequest { private String email; private String code; }
    @Data public static class DoResetPwRequest { private String email; private String code; private String newPassword; }
}
