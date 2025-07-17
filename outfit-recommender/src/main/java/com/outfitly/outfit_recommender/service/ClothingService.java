package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.dto.ClothingResponseDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ClothingService {

    private final ClothingRepository clothingRepository;

    public void saveClothing(ClothingRequestDto dto) {
        Clothing clothing = Clothing.builder()
                .name(dto.name())
                .category(dto.category())
                .color(dto.color())
                .seasons(dto.seasons())
                .userId(dto.userId())
                .build();
        clothingRepository.save(clothing);
    }

    public List<ClothingResponseDto> getClothesByUser(Long userId) {
        return clothingRepository.findByUserId(userId).stream()
                .map(ClothingResponseDto::fromEntity)
                .toList();
    }
}
