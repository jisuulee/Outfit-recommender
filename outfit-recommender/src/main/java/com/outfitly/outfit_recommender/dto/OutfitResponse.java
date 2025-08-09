package com.outfitly.outfit_recommender.dto;

import java.util.List;

public record OutfitResponse(List<OutfitCandidate> outfits) {
}
