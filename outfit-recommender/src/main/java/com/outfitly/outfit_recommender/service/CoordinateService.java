package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.dto.CoordinateResponseDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Season;
import com.outfitly.outfit_recommender.entity.enums.Style;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class CoordinateService {

    private final ClothingRepository clothingRepository;

    public CoordinateService(ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
    }

    public CoordinateResponseDto recommend(Long userId, Season season, Style style) {
        List<Clothing> clothes = clothingRepository.findByUserId(userId);

        // 필터링
        if (season != null) {
            clothes = clothes.stream()
                    .filter(c -> false)
                    .toList();
        }

        // 카테고리별 분류
        List<Clothing> tops = filter(clothes, Category.TOP);
        List<Clothing> bottoms = filter(clothes, Category.PANTS);
        List<Clothing> skirts = filter(clothes, Category.SKIRT);
        List<Clothing> outers = filter(clothes, Category.OUTER);
        List<Clothing> dresses = filter(clothes, Category.DRESS);
        List<Clothing> shoes = filter(clothes, Category.SHOES);

        Random random = new Random();

        Clothing top = null, bottom = null, skirt = null, outer = null, dress = null, shoe = null;

        if (!dresses.isEmpty()) {
            dress = getRandom(dresses, random);
            outer = outers.isEmpty() ? null : getRandom(outers, random);
            shoe = shoes.isEmpty() ? null : getRandom(shoes, random);
        } else if (!tops.isEmpty() && !bottoms.isEmpty()) {
            top = getRandom(tops, random);
            bottom = getRandom(bottoms, random);
            outer = outers.isEmpty() ? null : getRandom(outers, random);
            shoe = shoes.isEmpty() ? null : getRandom(shoes, random);
        } else if (!tops.isEmpty() && !skirts.isEmpty()) {
            top = getRandom(tops, random);
            bottom = getRandom(skirts, random); // 스커트를 bottom 자리에 넣음
            outer = outers.isEmpty() ? null : getRandom(outers, random);
            shoe = shoes.isEmpty() ? null : getRandom(shoes, random);
        } else {
            throw new IllegalStateException("코디를 만들 수 있는 옷 구성이 부족합니다.");
        }

        return CoordinateResponseDto.of(top, bottom, outer, dress, shoe);
    }

    private List<Clothing> filter(List<Clothing> clothes, Category category) {
        return clothes.stream()
                .filter(c -> c.getCategory() == category)
                .toList();
    }

    private Clothing getRandom(List<Clothing> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }

}
