package com.outfitly.outfit_recommender.repository;

import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.entity.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingRepository extends JpaRepository<Clothing, Long> {
    List<Clothing> findAllByUserId(Long userId);
    List<Clothing> findByUserAndCategory(User user, Category category);
    Optional<Clothing> findByIdAndUser(Long id, User user); // 해당 id 옷이 이 유저의 소유인지
    List<Clothing> findByIdIn(List<Long> ids); // 옷 id -> name

}
