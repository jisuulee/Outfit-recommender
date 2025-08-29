package com.outfitly.outfit_recommender.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private Integer age;
    private String gender;
    @Email private String email;
    private String currentPassword; // 현재 비밀번호 확인용
}
