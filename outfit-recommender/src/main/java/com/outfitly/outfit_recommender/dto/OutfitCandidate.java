package com.outfitly.outfit_recommender.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * 모델이 반환하는 한 벌 코디.
 * 둘 중 하나의 형태만 허용:
 *  (A) top + bottom + shoes (+ outer?)
 *  (B) dress + shoes (+ outer?)
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutfitCandidate {
    private Long top;    // A 전용
    private Long bottom; // A 전용 (pants 또는 skirt)
    private Long dress;  // B 전용
    private Long outer;  // 선택
    private Long shoes;  // 필수

    public boolean isTypeA() { return top != null && bottom != null && dress == null; }
    public boolean isTypeB() { return dress != null && top == null && bottom == null; }
}
