package com.outfitly.outfit_recommender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendRequestDto {
    private Long userId;
    private String season; // 예: "SPRING"
    private String style;  // 예: "DATE"
}

