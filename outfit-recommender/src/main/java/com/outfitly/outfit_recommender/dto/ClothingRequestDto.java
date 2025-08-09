package com.outfitly.outfit_recommender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClothingRequestDto {
    private String name;
    private String category;
    private String color;
    private List<String> seasons;
}
