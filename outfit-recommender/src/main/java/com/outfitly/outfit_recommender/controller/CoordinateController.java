package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.CoordinateResponseDto;
import com.outfitly.outfit_recommender.entity.enums.Season;
import com.outfitly.outfit_recommender.entity.enums.Style;
import com.outfitly.outfit_recommender.service.CoordinateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coordinates")
public class CoordinateController {

    private final CoordinateService coordinateService;

    @GetMapping("/recommend")
    public ResponseEntity<CoordinateResponseDto> recommend(
            @RequestParam Long userId,
            @RequestParam(required = false) Season season,
            @RequestParam(required = false) Style style
    ) {
        return ResponseEntity.ok(coordinateService.recommend(userId, season, style));
    }
}
