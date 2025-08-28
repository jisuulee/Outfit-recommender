package com.outfitly.outfit_recommender.entity.enums;

public enum Color {
    WHITE, BLACK, GRAY, RED, PINK, ORANGE, YELLOW, IVORY, GREEN, MINT, SKYBLUE, BLUE, NAVY, PURPLE, BROWN, BEIGE, DENIM;
    public static Color from(String v){ return valueOf(v.trim().toUpperCase()); }
}
