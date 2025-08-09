package com.outfitly.outfit_recommender.entity.enums;

public enum Season {
    SPRING, SUMMER, FALL, WINTER, ALL;
    public static Season from(String v){ return valueOf(v.trim().toUpperCase()); }
}
