package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.LoginRequestDto;
import com.outfitly.outfit_recommender.dto.SignupRequestDto;
import com.outfitly.outfit_recommender.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto) {
        String token = userService.login(dto.getUsername(), dto.getPassword());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body("로그인 성공");
    }

    @GetMapping("/me")
    public ResponseEntity<String> getMyName(Authentication authentication) {
        String username = authentication.getName(); // JWT에서 꺼낸 사용자 이름
        return ResponseEntity.ok("안녕하세요, " + username + "님!");
    }

}
