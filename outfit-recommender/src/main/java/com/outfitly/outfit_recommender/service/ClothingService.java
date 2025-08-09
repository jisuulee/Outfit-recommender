package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Color;
import com.outfitly.outfit_recommender.entity.enums.Season;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClothingService {

    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;

    public Clothing saveClothing(String username, ClothingRequestDto dto) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        var clothing = Clothing.builder()
                .name(dto.getName())
                .category(Category.from(dto.getCategory()))
                .color(Color.from(dto.getColor()))
                .seasons(dto.getSeasons().stream().map(Season::from).toList())
                .user(user)
                .build();

        return clothingRepository.save(clothing);
    }

    public List<Clothing> getUserClothes(String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return clothingRepository.findAllByUserId(user.getId());
    }
}
