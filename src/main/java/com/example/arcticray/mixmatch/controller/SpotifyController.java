package com.example.arcticray.mixmatch.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.arcticray.mixmatch.service.SpotifyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Dummy controller for Spotify-related endpoints.
 * Provides basic JSON passthrough; no real business logic yet.
 * TODO: Replace with proper service layer, DTO mapping, and centralized error handling.
 */
@RestController
@RequestMapping("/api/spotify")
// @Profile("!prod") // Uncomment to disable in production
public class SpotifyController {

    private final SpotifyService service;

    public SpotifyController(SpotifyService service) {
        this.service = service;
    }

    /**
     * GET /api/spotify/search
     * Dummy search: returns raw JSON from SpotifyService.searchTracks().
     *
     * @param query the search query
     * @return JSON with search results or error JSON
     * TODO:
     *   - Map results to dedicated DTOs
     *   - Implement pagination and filtering
     *   - Use @ControllerAdvice for error responses
     */
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> search(@RequestParam("q") String query) {
        try {
            JsonNode result = service.searchTracks(query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // debug: print stacktrace; replace with proper logger
            e.printStackTrace();
            ObjectNode err = JsonNodeFactory.instance.objectNode()
                    .put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

    /**
     * GET /api/spotify/artist/tracks
     * Dummy endpoint: returns an array of track names from getArtistTopTracks().
     *
     * @param name the artist name
     * @return JSON array of track names or error JSON
     * @throws IllegalArgumentException if artist not found
     * TODO:
     *   - Move mapping logic into service layer
     *   - Replace System.out with a logger
     *   - Handle empty or malformed responses gracefully
     */
    @GetMapping(value = "/artist/tracks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> artistTracks(@RequestParam("name") String name) {
        try {
            JsonNode topTracks = service.getArtistTopTracks(name).path("tracks");
            ArrayNode names = JsonNodeFactory.instance.arrayNode();
            topTracks.forEach(track -> names.add(track.path("name").asText()));
            // debug: print each track name
            names.forEach(n -> System.out.println(n.asText()));
            return ResponseEntity.ok(names);
        } catch (IllegalArgumentException e) {
            ObjectNode err = JsonNodeFactory.instance.objectNode()
                    .put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode err = JsonNodeFactory.instance.objectNode()
                    .put("error", "Server error");
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

    /**
     * GET /api/spotify/test
     * Test endpoint—NOT for production.
     * Returns the top track name of the given artist.
     *
     * @param name the artist name
     * @return the top track name or an error message
     * @throws IllegalArgumentException if artist not found
     * TODO:
     *   - Remove or secure this endpoint in production
     *   - Integrate with the real recommendation flow
     */
    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyConnection(@RequestParam("name") String name) {
        try {
            JsonNode tracks = service.getArtistTopTracks(name).path("tracks");
            if (!tracks.isArray() || tracks.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No tracks found for artist: " + name);
            }
            String topTrack = tracks.get(0).path("name").asText();
            // debug: print the track ID; swap out for logger later
            String trackId = tracks.get(0).path("id").asText();
            System.out.println(trackId);
            return ResponseEntity.ok(topTrack);
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
