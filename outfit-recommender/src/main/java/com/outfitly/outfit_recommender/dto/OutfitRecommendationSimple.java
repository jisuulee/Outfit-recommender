package com.outfitly.outfit_recommender.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 옷 이름만 내려주는 간단한 dto
public class OutfitRecommendationSimple {
    private String top;
    private String bottom;
    private String outer;
    private String shoes;
    private String dress;
}
