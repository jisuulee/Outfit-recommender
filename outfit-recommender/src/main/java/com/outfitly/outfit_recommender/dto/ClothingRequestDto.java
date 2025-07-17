package com.outfitly.outfit_recommender.dto;

import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Color;
import com.outfitly.outfit_recommender.entity.enums.Season;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ClothingRequestDto(
        @NotNull String name,
        @NotNull Category category,
        @NotNull Color color,
        @NotNull List<Season> seasons,
        @NotNull Long userId
) {
}
