package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.dto.ClothingUpdateRequestDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Color;
import com.outfitly.outfit_recommender.entity.enums.Season;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public Clothing update(User user, Long id, ClothingUpdateRequestDto dto) {
        Clothing c = clothingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 옷이 없거나 권한이 없습니다."));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            c.setName(dto.getName().trim());
        }
        if (dto.getCategory() != null) {
            c.setCategory(toCategory(dto.getCategory()));
        }
        if (dto.getColor() != null) {
            c.setColor(toColor(dto.getColor()));
        }
        if (dto.getSeasons() != null) {
            var list = new ArrayList<Season>();
            for (String s : dto.getSeasons()) {
                if (s == null || s.isBlank()) continue;
                list.add(toSeason(s));
            }
            c.getSeasons().clear();
            c.getSeasons().addAll(list);
        }
        return clothingRepository.save(c);
    }

    public void delete(User user, Long id) {
        Clothing c = clothingRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 옷이 없거나 권한이 없습니다."));
        clothingRepository.delete(c);
    }

    private static Category toCategory(String s) {
        try { return Category.valueOf(s.trim().toUpperCase(Locale.ROOT)); }
        catch (Exception e) { throw new IllegalArgumentException("잘못된 카테고리: " + s); }
    }

    private static Color toColor(String s) {
        try { return Color.valueOf(s.trim().toUpperCase(Locale.ROOT)); }
        catch (Exception e) { throw new IllegalArgumentException("잘못된 컬러: " + s); }
    }

    private static Season toSeason(String s) {
        try { return Season.valueOf(s.trim().toUpperCase(Locale.ROOT)); }
        catch (Exception e) { throw new IllegalArgumentException("잘못된 계절: " + s); }
    }
}
