package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.service.ClothingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/clothing")
public class ClothingController {

    private final ClothingService clothingService;

    @PostMapping
    public ResponseEntity<?> addClothing(@RequestBody ClothingRequestDto dto) {
        Clothing saved = clothingService.saveClothing(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserClothing(@PathVariable Long userId) {
        return ResponseEntity.ok(clothingService.getUserClothes(userId));
    }
}
