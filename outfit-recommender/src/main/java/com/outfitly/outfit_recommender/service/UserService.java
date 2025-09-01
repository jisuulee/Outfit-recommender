package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.ChangePasswordRequestDto;
import com.outfitly.outfit_recommender.dto.SignupRequestDto;
import com.outfitly.outfit_recommender.dto.UpdateUserRequestDto;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.jwt.JwtUtil;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

        // 비밀번호 일치 확인
        if (!dto.getPassword().equals(dto.getPasswordConfirm()))
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");

        // 비밀번호 정책(8~20자, 대/소문자+숫자 중 2가지 이상)
        validatePasswordPolicy(dto.getPassword());

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

    // 비밀번호 정책
    private void validatePasswordPolicy(String pw) {
        if (pw.length() < 8 || pw.length() > 20)
            throw new IllegalArgumentException("비밀번호는 8~20자로 설정하세요.");
        int classes = 0;
        if (pw.matches(".*[a-z].*")) classes++;
        if (pw.matches(".*[A-Z].*")) classes++;
        if (pw.matches(".*\\d.*"))   classes++;
        if (pw.matches(".*[^a-zA-Z\\d].*")) classes++;
        if (classes < 2)
            throw new IllegalArgumentException("비밀번호는 대/소문자, 숫자, 특수문자 중 2가지 이상 조합을 권장합니다.");
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

    // 컨트롤러 응답용 프로필 DTO
    public record UserProfileDto(String name, Integer age, String gender, String email, String username) {}

    // 내 프로필 조회
    public UserProfileDto getProfile(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new UserProfileDto(u.getName(), u.getAge(), u.getGender(), u.getEmail(), u.getUsername());
    }

    // 내 프로필 부분 수정(null/빈 문자열 필드는 무시)
    public UserProfileDto updateProfile(String username, UpdateUserRequestDto dto) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            u.setName(dto.getName().trim());
        }
        if (dto.getAge() != null) {
            u.setAge(dto.getAge());
        }
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            u.setGender(dto.getGender().trim());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(u.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            u.setEmail(dto.getEmail().trim().toLowerCase(Locale.ROOT));
        }

        userRepository.save(u);
        return new UserProfileDto(
                u.getName(),
                u.getAge(),
                u.getGender(),
                u.getEmail(),
                u.getUsername()
        );
    }

    // 비밀번호 변경: 현재 비번 확인 → 새 비번 유효성 체크(간단) → 저장
    public void changePassword(String username, ChangePasswordRequestDto dto) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getCurrentPassword() == null || dto.getNewPassword() == null) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (!passwordEncoder.matches(dto.getCurrentPassword(), u.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (dto.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
        }

        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(u);
    }

    public String findUsernameByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getUsername)
                .orElse(null);
    }

    public void updatePasswordByEmail(String email, String newPlainPw) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("계정을 찾을 수 없습니다."));
        // 비밀번호 정책 체크(있다면 재사용)
        if (newPlainPw == null || newPlainPw.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        user.setPassword(passwordEncoder.encode(newPlainPw));
        userRepository.save(user);
    }


    // 회원 탈퇴: 연관 데이터 정책 확인 필요
    public void deleteAccount(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userRepository.delete(u);
    }

}
