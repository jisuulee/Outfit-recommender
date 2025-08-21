package com.outfitly.outfit_recommender.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outfitly.outfit_recommender.dto.OutfitCandidate;
import com.outfitly.outfit_recommender.entity.Clothing;
import com.outfitly.outfit_recommender.entity.User;
import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.repository.ClothingRepository;
import com.outfitly.outfit_recommender.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoordinateService {

    private final ClothingRepository clothingRepository;
    private final UserRepository userRepository;
    private final OpenApiService openApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String recommendOutfit(String username, String season, String style) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 인벤토리 수집
        var tops    = clothingRepository.findByUserAndCategory(user, Category.TOP);
        var pants   = clothingRepository.findByUserAndCategory(user, Category.PANTS);
        var skirts  = clothingRepository.findByUserAndCategory(user, Category.SKIRT);
        var dresses = clothingRepository.findByUserAndCategory(user, Category.DRESS);
        var outers  = clothingRepository.findByUserAndCategory(user, Category.OUTER);
        var shoes   = clothingRepository.findByUserAndCategory(user, Category.SHOES);

        // 허용 ID 집합 (검증용)
        var allowedTop    = toIdSet(tops);
        var allowedBottom = union(toIdSet(pants), toIdSet(skirts)); // bottom = pants|skirt
        var allowedDress  = toIdSet(dresses);
        var allowedOuter  = toIdSet(outers);
        var allowedShoes  = toIdSet(shoes);

        // 프롬프트 생성 (id:이름 목록 + JSON 형식 강제)
        String prompt = buildPromptWithIds(style, season, tops, pants, skirts, dresses, outers, shoes);

        // 최대 2회 보정 재시도
        int attempts = 0;

        while (attempts < 3) {
            attempts++;
            String raw = openApiService.askGpt(prompt);

            List<OutfitCandidate> parsed = parseOutfits(raw); // JSON 배열 파싱
            List<OutfitCandidate> filtered = parsed.stream()
                    .filter(o -> isValid(o, allowedTop, allowedBottom, allowedDress, allowedOuter, allowedShoes))
                    .distinct()
                    .limit(3)
                    .toList();

            if (!filtered.isEmpty()) {
                try {
                    // ID -> 이름 매핑
                    // 필요한 모든 ID 수집
                    Set<Long> idSet = filtered.stream().flatMap(o -> {
                        List<Long> ids = new ArrayList<>();
                        if (o.getTop()    != null) ids.add(o.getTop());
                        if (o.getBottom() != null) ids.add(o.getBottom());
                        if (o.getDress()  != null) ids.add(o.getDress());
                        if (o.getOuter()  != null) ids.add(o.getOuter());
                        if (o.getShoes()  != null) ids.add(o.getShoes());
                        return ids.stream();
                    }).collect(Collectors.toSet());

                    // 한 번에 조회 후 map 구성 (JpaRepository 기본 메서드 활용)
                    Map<Long, Clothing> byId = clothingRepository.findAllById(idSet).stream()
                            .collect(Collectors.toMap(Clothing::getId, c -> c));

                    // 이름만 담은 간단 DTO로 변환
                    record OutfitSimple(String top, String bottom, String outer, String shoes, String dress) {}
                    List<OutfitSimple> result = filtered.stream().map(o -> new OutfitSimple(
                            name(byId.get(o.getTop())),
                            name(byId.get(o.getBottom())),
                            name(byId.get(o.getOuter())),
                            name(byId.get(o.getShoes())),
                            name(byId.get(o.getDress()))
                    )).toList();

                    // 4) JSON 문자열로 반환
                    return objectMapper.writeValueAsString(result);
                } catch (Exception ignored) { /* fall-through */ }
            }

            // 실패 → 보정 프롬프트로 재시도
            prompt = buildCorrectionPrompt(style, season, tops, pants, skirts, dresses, outers, shoes, parsed);
        }

        // 모든 시도 실패 시 사용자에게 적절한 메시지
        return """
               현재 보유 아이템으로 요청 조건을 만족하는 코디를 만들지 못했습니다.
               아이템을 추가하거나 조건(계절/스타일)을 변경해 다시 시도해주세요.
               """;
    }

    private String name(Clothing c) { return c == null ? null : c.getName(); }

    /* =================== Prompt Builders =================== */

    private String buildPromptWithIds(
            String style, String season,
            List<Clothing> tops, List<Clothing> pants, List<Clothing> skirts,
            List<Clothing> dresses, List<Clothing> outers, List<Clothing> shoes
    ) {
        return String.format("""
            아래는 사용자가 실제로 보유한 아이템 목록입니다. 각 항목은 "id:이름" 형식입니다.
            반드시 아래 목록의 id만 사용하여 코디를 구성하세요.
            아우터가 없으면 outer 키를 생략하세요.

            - top:   [%s]
            - pants: [%s]
            - skirt: [%s]
            - dress: [%s]
            - outer: [%s]
            - shoes: [%s]

            요청: "%s" 스타일로 "%s"에 어울리는 코디 3가지를 생성하세요.

            코디 규칙(둘 중 하나만 허용):
            (A) top + bottom + shoes (+ outer?)   // bottom은 pants 또는 skirt 중 택1
            (B) dress + shoes (+ outer?)          // dress 사용 시 top/bottom 사용 금지

            출력은 JSON 배열만 반환하세요. 각 원소는 아래 형식 중 하나여야 합니다.
            (A) {"top":<id>,"bottom":<id>,"shoes":<id>,"outer":<id?>}
            (B) {"dress":<id>,"shoes":<id>,"outer":<id?>}
            outer가 없으면 "outer" 키를 생략하세요.
            """,
                joinInventory(tops), joinInventory(pants), joinInventory(skirts),
                joinInventory(dresses), joinInventory(outers), joinInventory(shoes),
                style, season
        );
    }

    private String buildCorrectionPrompt(
            String style, String season,
            List<Clothing> tops, List<Clothing> pants, List<Clothing> skirts,
            List<Clothing> dresses, List<Clothing> outers, List<Clothing> shoes,
            List<OutfitCandidate> previous
    ) {
        // 간단한 보정: 이전 시도가 허용되지 않은 id 또는 규칙 위반이었다고 알리고 다시 생성 요구
        return buildPromptWithIds(style, season, tops, pants, skirts, dresses, outers, shoes) +
                "\n이전 응답은 허용되지 않은 id 또는 규칙 위반이 있었습니다. 규칙을 엄격히 지켜서 새로 생성하세요.";
    }

    /* =================== Parsing & Validation =================== */

    private List<OutfitCandidate> parseOutfits(String raw) {
        try {
            // 혹시 모델이 ```json ... ``` 감싸면 안의 JSON만 추출
            String json = extractJson(raw);
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String extractJson(String raw) {
        Pattern p = Pattern.compile("```json\\s*(\\[.*?])\\s*```", Pattern.DOTALL);
        Matcher m = p.matcher(raw);
        if (m.find()) return m.group(1).trim();

        // 코드블록이 아니라면 원문 전체에서 배열만 탐색
        Pattern p2 = Pattern.compile("(\\[\\s*\\{[\\s\\S]*}])");
        Matcher m2 = p2.matcher(raw);
        if (m2.find()) return m2.group(1).trim();
        return raw.trim();
    }

    private boolean isValid(
            OutfitCandidate o,
            Set<Long> allowedTop, Set<Long> allowedBottom, Set<Long> allowedDress,
            Set<Long> allowedOuter, Set<Long> allowedShoes
    ) {
        // 형태 체크: A 또는 B 중 하나만
        boolean a = o.isTypeA();
        boolean b = o.isTypeB();
        if (a == b) return false; // 둘 다 true or 둘 다 false → 탈락

        // 공통: shoes 필수 + 허용된 id
        if (o.getShoes() == null || !allowedShoes.contains(o.getShoes())) return false;

        // outer 있으면 허용된 id여야
        if (o.getOuter() != null && !allowedOuter.contains(o.getOuter())) return false;

        if (a) {
            // top/bottom 각각 허용 id 체크
            if (!allowedTop.contains(o.getTop())) return false;
            if (!allowedBottom.contains(o.getBottom())) return false;
        } else {
            // dress 허용 id 체크
            if (!allowedDress.contains(o.getDress())) return false;
        }
        return true;
    }

    /* =================== Helpers =================== */

    private Set<Long> toIdSet(List<Clothing> list) {
        return list.stream().map(Clothing::getId).collect(Collectors.toSet());
    }

    private Set<Long> union(Set<Long> a, Set<Long> b) {
        Set<Long> u = new HashSet<>(a);
        u.addAll(b);
        return u;
    }

    private String joinInventory(List<Clothing> list) {
        if (list.isEmpty()) return "";
        return list.stream()
                .map(c -> c.getId() + ":" + c.getName())
                .collect(Collectors.joining(", "));
    }
}
