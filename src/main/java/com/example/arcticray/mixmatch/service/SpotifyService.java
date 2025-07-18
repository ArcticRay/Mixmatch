package com.example.arcticray.mixmatch.service;

import com.example.arcticray.mixmatch.client.SpotifyClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;


@Service
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    public SpotifyService(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    public JsonNode searchTracks(String query) {
        return spotifyClient.searchTracks(query);
    }

    public JsonNode getArtistTopTracks(String artistName) {
        JsonNode artistSearch = spotifyClient.searchArtists(artistName);
        JsonNode items = artistSearch.path("artists").path("items");
        if (!items.isArray() || items.isEmpty()) {
            throw new IllegalArgumentException("Kein Interpret gefunden: " + artistName);
        }
        String artistId = items.get(0).get("id").asText();

        return spotifyClient.getTopTracks(artistId);
    }
}
