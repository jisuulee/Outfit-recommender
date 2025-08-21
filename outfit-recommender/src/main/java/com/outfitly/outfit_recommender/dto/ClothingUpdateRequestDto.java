package com.outfitly.outfit_recommender.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class ClothingUpdateRequestDto {
    private String name;
    private String category;
    private String color;
    private List<String> seasons;
}
