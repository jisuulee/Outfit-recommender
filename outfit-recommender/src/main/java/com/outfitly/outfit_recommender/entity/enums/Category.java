package com.outfitly.outfit_recommender.entity.enums;

public enum Category {
    TOP,
    OUTER,
    PANTS,
    SKIRT,
    DRESS,
    SHOES;
    public static Category from(String v){ return valueOf(v.trim().toUpperCase()); }
}
