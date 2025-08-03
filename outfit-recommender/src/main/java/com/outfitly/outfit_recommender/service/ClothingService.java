package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Color;
import com.outfitly.outfit_recommender.entity.enums.Season;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothingService {

    private final ClothingRepository clothingRepository;

    public Clothing saveClothing(ClothingRequestDto dto) {
        Clothing clothing = new Clothing();
        clothing.setName(dto.getName());
        clothing.setCategory(Category.valueOf(dto.getCategory().toUpperCase()));
        clothing.setColor(Color.valueOf(dto.getColor().toUpperCase()));
        clothing.setUserId(dto.getUserId());

        List<Season> seasonList = dto.getSeasons().stream()
                .map(s -> Season.valueOf(s.toUpperCase()))
                .collect(Collectors.toList());
        clothing.setSeasons(seasonList);

        return clothingRepository.save(clothing);
    }

    public List<Clothing> getUserClothes(Long userId) {
        return clothingRepository.findAllByUserId(userId);
    }
}
