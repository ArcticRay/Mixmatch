package com.example.arcticray.mixmatch.controller;

import com.example.arcticray.mixmatch.service.SpotifyService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dummy controller for testing purposes.
 * Returns a random top track for the specified artist.
 * TODO: Replace with real recommendation logic.
 */
@RestController
@RequestMapping("/api/recommendations")
// @Profile("!prod") // Uncomment to disable in production
public class RecommendationController {
    private final SpotifyService spotifyService;

    public RecommendationController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    /**
     * Test endpoint—NOT production code!
     *
     * @param name Name of the artist
     * @return A random track name or an error message
     * @throws IllegalArgumentException if the artist isn’t found
     *
     * TODO:
     *  - Move this into a proper RecommendationService
     *  - Implement ranking and personalization logic
     *  - Remove System.out and switch to a proper logger
     */
    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyConnection(@RequestParam("name") String name) {
        try {
            JsonNode tracks = spotifyService.getArtistTopTracks(name).path("tracks");
            if (!tracks.isArray() || tracks.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No tracks found for artist: " + name);
            }

            int randomIndex = (int) (Math.random() * tracks.size());
            String randomTrack = tracks.get(randomIndex).path("name").asText();

            // Debug output; replace with logger or remove later
            String topTrack = tracks.get(0).path("name").asText();
            String trackId  = tracks.get(0).path("id").asText();
            System.out.println(trackId + " | " + topTrack + " | " + randomTrack);

            return ResponseEntity.ok(randomTrack);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error");
        }
    }
}