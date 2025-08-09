package com.outfitly.outfit_recommender.entity;

import com.outfitly.outfit_recommender.entity.enums.Category;
import com.outfitly.outfit_recommender.entity.enums.Color;
import com.outfitly.outfit_recommender.entity.enums.Season;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clothing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name; // 옷 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Color color;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "clothing_seasons", joinColumns = @JoinColumn(name = "clothing_id"))
    @Column(nullable = false, name = "season")
    private List<Season> seasons = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable=false)
    private User user;
}
