package com.outfitly.outfit_recommender.repository;

import com.outfitly.outfit_recommender.entity.Clothing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothingRepository extends JpaRepository<Clothing, Long> {
    List<Clothing> findByUserId(Long userId);
}
