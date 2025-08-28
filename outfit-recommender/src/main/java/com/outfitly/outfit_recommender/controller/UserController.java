package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.ChangePasswordRequestDto;
import com.outfitly.outfit_recommender.dto.LoginRequestDto;
import com.outfitly.outfit_recommender.dto.SignupRequestDto;
import com.outfitly.outfit_recommender.dto.UpdateUserRequestDto;
import com.outfitly.outfit_recommender.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        String result = userService.register(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        String token = userService.login(dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "token", token
        ));
    }

    // 내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        String username = auth.getName(); // JwtAuthFilter에서 세팅된 주체
        return ResponseEntity.ok(userService.getProfile(username));
    }

    //내 프로필 부분 수정: null 필드는 무시됨
    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody UpdateUserRequestDto dto, Authentication auth) {
        String username = auth.getName();
        return ResponseEntity.ok(userService.updateProfile(username, dto));
    }

    // 비밀번호 변경
    @PostMapping("/me/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDto dto, Authentication auth) {
        String username = auth.getName();
        userService.changePassword(username, dto);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe(Authentication auth) {
        String username = auth.getName();
        userService.deleteAccount(username);
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }


}
