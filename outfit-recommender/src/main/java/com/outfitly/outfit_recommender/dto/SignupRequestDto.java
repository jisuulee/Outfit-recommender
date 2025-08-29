package com.outfitly.outfit_recommender.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String name;
    private Integer age;
    private String gender;
    @Email private String email;
    private String username;
    private String password;
    private String passwordConfirm; // 비밀번호 2차 검증
}
