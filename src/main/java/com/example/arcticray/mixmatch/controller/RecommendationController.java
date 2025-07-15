package com.example.arcticray.mixmatch.controller;

import com.example.arcticray.mixmatch.service.FakeSpotifyService;
import com.example.arcticray.mixmatch.model.Track;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final FakeSpotifyService fakeSpotifyService;

    public RecommendationController(FakeSpotifyService fakeSpotifyService) {
        this.fakeSpotifyService = fakeSpotifyService;
    }

    @GetMapping
    public String getRecommendations() {
        List<Track> trackList = fakeSpotifyService.getTracksFromPlaylist("demo");
        String s = "";
        // real recommendation logic soon
        for(int i = 0; i < trackList.size(); i++) {
            System.out.println(trackList.get(i));
            s = s + trackList.get(i).getName();
        }
        return s;
    }
}
