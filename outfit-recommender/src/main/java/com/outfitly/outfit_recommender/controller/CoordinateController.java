package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.RecommendRequestDto;
import com.outfitly.outfit_recommender.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coordinates")
public class CoordinateController {

    private final CoordinateService coordinateService;

    // 코디 추천 받기
    @PostMapping("/recommend")
    public ResponseEntity<String> recommend(@RequestBody RecommendRequestDto dto,
                                            Authentication authentication) {
        String username = authentication.getName();
        String result = coordinateService.recommendOutfit(username, dto.getSeason(), dto.getStyle());
        return ResponseEntity.ok(result);
    }
}
