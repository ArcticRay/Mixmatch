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

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    private final SpotifyService service;

    public SpotifyController(SpotifyService service) {
        this.service = service;
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> search(@RequestParam("q") String query) {
        try {
            JsonNode result = service.searchTracks(query);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode err = JsonNodeFactory.instance.objectNode()
                    .put("error", e.getMessage());
            return ResponseEntity
                    .status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(err);
        }
    }

    @GetMapping(value = "/artist/tracks", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JsonNode> artistTracks(@RequestParam("name") String name) {
        try {
            JsonNode topTracks = service.getArtistTopTracks(name).get("tracks");
            ArrayNode names = JsonNodeFactory.instance.arrayNode();
            topTracks.forEach(track -> names.add(track.path("name").asText()));
            for(int i = 0; i < names.size(); i++) {
                System.out.println(names.get(i).asText());
            }
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(names);
        } catch (IllegalArgumentException e) {
            ObjectNode err = JsonNodeFactory.instance.objectNode()
                    .put("error", e.getMessage());
            return ResponseEntity
                    .status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(err);
        } catch (Exception e) {
            e.printStackTrace();
            ObjectNode err = JsonNodeFactory.instance.objectNode()
                    .put("error", "Serverfehler");
            return ResponseEntity
                    .status(500)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(err);
        }
    }

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verifyConnection(@RequestParam("name") String name) {
        try {
            JsonNode tracks = service.getArtistTopTracks(name).path("tracks");
            if (!tracks.isArray() || tracks.size() == 0) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("No tracks found for artist: " + name);
            }


            String topTrack = tracks.get(0).path("name").asText();
            return ResponseEntity
                    .ok()
                    .body(topTrack);

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