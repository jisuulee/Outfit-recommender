package com.outfitly.outfit_recommender.repository;

import com.outfitly.outfit_recommender.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username); // 로그인용
    Optional<User> findByEmail(String email);
}
