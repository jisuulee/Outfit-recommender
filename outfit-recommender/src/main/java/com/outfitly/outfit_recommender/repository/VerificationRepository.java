package com.outfitly.outfit_recommender.repository;

import com.outfitly.outfit_recommender.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Integer> {
    // 가장 최신 요청 1건
    Optional<Verification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, String purpose);
}
