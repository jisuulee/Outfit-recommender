package com.outfitly.outfit_recommender.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDto {
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
