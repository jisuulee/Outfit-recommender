package com.outfitly.outfit_recommender.dto;

import com.outfitly.outfit_recommender.entity.Clothing;

public record CoordinateResponseDto(
        ClothingResponseDto top,
        ClothingResponseDto bottom,
        ClothingResponseDto outer,
        ClothingResponseDto dress,
        ClothingResponseDto shoes
) {
    public static CoordinateResponseDto of(
            Clothing top,
            Clothing bottom,
            Clothing outer,
            Clothing dress,
            Clothing shoes
    ) {
        return new CoordinateResponseDto(
                top != null ? ClothingResponseDto.fromEntity(top) : null,
                bottom != null ? ClothingResponseDto.fromEntity(bottom) : null,
                outer != null ? ClothingResponseDto.fromEntity(outer) : null,
                dress != null ? ClothingResponseDto.fromEntity(dress) : null,
                shoes != null ? ClothingResponseDto.fromEntity(shoes) : null
        );
    }
}

