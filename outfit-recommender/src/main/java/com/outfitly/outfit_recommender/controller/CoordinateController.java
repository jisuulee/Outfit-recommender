package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.RecommendRequestDto;
import com.outfitly.outfit_recommender.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coordinates")
public class CoordinateController {

    private final CoordinateService coordinateService;

    @PostMapping("/recommend")
    public ResponseEntity<String> recommendOutfit(@RequestBody RecommendRequestDto requestDto) {
        String result = coordinateService.recommendOutfit(requestDto.getUserId(), requestDto.getSeason(), requestDto.getStyle());
        return ResponseEntity.ok(result);
    }
}
