package com.outfitly.outfit_recommender.service;

import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoordinateService {

    private final ClothingRepository clothingRepository;
    private final OpenApiService openApiService;

    public CoordinateService(ClothingRepository clothingRepository, OpenApiService openApiService) {
        this.clothingRepository = clothingRepository;
        this.openApiService = openApiService;
    }

    public String recommendOutfit(Long userId, String season, String style) {
        String prompt = buildPrompt(userId, season, style);
        return openApiService.askGpt(prompt);
    }

    private String buildPrompt(Long userId, String season, String style) {
        List<Clothing> tops = clothingRepository.findByUserIdAndCategory(userId, Category.TOP);
        List<Clothing> pants = clothingRepository.findByUserIdAndCategory(userId, Category.PANTS);
        List<Clothing> outers = clothingRepository.findByUserIdAndCategory(userId, Category.OUTER);
        List<Clothing> shoes = clothingRepository.findByUserIdAndCategory(userId, Category.SHOES);

        String topStr = toCommaSeparated(tops);
        String pantStr = toCommaSeparated(pants);
        String outerStr = toCommaSeparated(outers);
        String shoeStr = toCommaSeparated(shoes);

        return String.format("""
                    사용자가 가지고 있는 옷 목록:
                    
                        - 상의: %s
                        - 하의: %s
                        - 아우터: %s
                        - 신발: %s
                        
                        위에 제공된 목록의 옷만 사용해서
                        %s룩 스타일로 %s에 어울리는 옷 조합 3가지를 추천해 주세요
                        
                        예시:[상의 - 옷이름, 하의 - 옷이름, 아우터 - 옷이름 또는 없음, 신발 - 옷이름]
                """, topStr, pantStr, outerStr, shoeStr, style, season);
    }

    private String toCommaSeparated(List<Clothing> clothes) {
        if (clothes.isEmpty()) return "없음";
        return clothes.stream()
                .map(Clothing::getName)
                .collect(Collectors.joining(", "));
    }
}
