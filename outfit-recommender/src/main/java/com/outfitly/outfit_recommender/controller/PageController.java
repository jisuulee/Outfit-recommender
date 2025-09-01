package com.outfitly.outfit_recommender.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/wardrobe")
    public String wardrobe() {
        return "wardrobe";
    }

    @GetMapping("/recommend")
    public String recommend() {
        return "recommend";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/forgot-id")
    public String forgotId() {
        return "forgot-id";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/auth/find-id")
    public String findIdPage() {
        return "auth/find-id";
    }

    @GetMapping("/auth/reset-password")
    public String resetPwPage() {
        return "auth/reset-password";
    }
}
