package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.service.ClothingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/clothing")
public class ClothingController {

    private final ClothingService clothingService;

    // 옷 추가하기
    @PostMapping
    public ResponseEntity<?> addClothing(@RequestBody @Valid ClothingRequestDto dto,
                                         Authentication authentication) {
        String username = authentication.getName(); // JWT에서 꺼냄
        var saved = clothingService.saveClothing(username, dto);
        return ResponseEntity.ok(saved);
    }

    // 옷 조회
    @GetMapping("/me")
    public ResponseEntity<?> myClothes(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(clothingService.getUserClothes(username));
    }
}
