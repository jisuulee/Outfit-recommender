package com.outfitly.outfit_recommender.dto;

import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Color;
import com.outfitly.outfit_recommender.entity.enums.Season;

import java.util.List;

public record ClothingResponseDto(
        Long id,
        String name,
        Category category,
        Color color,
        List<Season> season
) {
    public static ClothingResponseDto fromEntity(Clothing clothing) {
        return new ClothingResponseDto(
                clothing.getId(),
                clothing.getName(),
                clothing.getCategory(),
                clothing.getColor(),
                clothing.getSeasons()
        );
    }
}
