package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.dto.ClothingResponseDto;
import com.outfitly.outfit_recommender.service.ClothingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes")
public class ClothingController {

    private final ClothingService clothingService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody @Valid ClothingRequestDto dto) {
        clothingService.saveClothing(dto);
        return ResponseEntity.ok("Clothing saved!");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ClothingResponseDto>> getClothes(@PathVariable Long userId) {
        List<ClothingResponseDto> clothes = clothingService.getClothesByUser(userId);
        return ResponseEntity.ok(clothes);
    }
}
