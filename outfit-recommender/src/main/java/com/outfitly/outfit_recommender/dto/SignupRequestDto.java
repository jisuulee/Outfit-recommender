package com.outfitly.outfit_recommender.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String name;
    private Integer age;
    private String gender;
    private String email;
    private String username;
    private String password;
}
