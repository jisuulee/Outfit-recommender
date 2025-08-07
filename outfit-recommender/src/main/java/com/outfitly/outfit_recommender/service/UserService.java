package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.SignupRequestDto;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.jwt.JwtUtil;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    public String register(SignupRequestDto dto) {
        // 중복 확인
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // 저장
        User user = User.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(encodedPassword)
                .build();

        userRepository.save(user);
        return "회원가입 성공!";
    }

    // 로그인
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

}
