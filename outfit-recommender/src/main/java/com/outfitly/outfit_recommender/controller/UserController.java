package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.LoginRequestDto;
import com.outfitly.outfit_recommender.dto.SignupRequestDto;
import com.outfitly.outfit_recommender.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
