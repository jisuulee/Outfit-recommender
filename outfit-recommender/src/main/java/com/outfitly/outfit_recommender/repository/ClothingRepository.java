package com.outfitly.outfit_recommender.repository;

import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClothingRepository extends JpaRepository<Clothing, Long> {
    List<Clothing> findAllByUserId(Long userId);
    List<Clothing> findByUserAndCategory(User user, Category category);
}
