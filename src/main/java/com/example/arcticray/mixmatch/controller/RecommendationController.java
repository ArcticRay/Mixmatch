package com.example.arcticray.mixmatch.controller;

import com.example.arcticray.mixmatch.service.SpotifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final SpotifyService spotifyService;

    public RecommendationController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping
    public String getRecommendations() {

        return "Recommendation";
    }
}
