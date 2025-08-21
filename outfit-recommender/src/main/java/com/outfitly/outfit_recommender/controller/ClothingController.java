package com.outfitly.outfit_recommender.controller;

import com.outfitly.outfit_recommender.dto.ClothingRequestDto;
import com.outfitly.outfit_recommender.dto.ClothingUpdateRequestDto;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.repository.UserRepository;
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
    private final UserRepository userRepository;

    // 현재 사용자 체크
    private User currentUser(Authentication auth) {
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

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

    // 옷 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ClothingUpdateRequestDto dto,
                                    Authentication auth) {
        var updated = clothingService.update(currentUser(auth), id, dto);
        return ResponseEntity.ok(updated);
    }

    // 옷 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        clothingService.delete(currentUser(auth), id);
        return ResponseEntity.noContent().build();
    }

}
