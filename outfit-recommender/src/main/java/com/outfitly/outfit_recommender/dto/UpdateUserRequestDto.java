package com.outfitly.outfit_recommender.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private Integer age;
    private String gender;
    private String email;
}
