package com.example.arcticray.mixmatch.service;

import com.example.arcticray.mixmatch.client.SpotifyClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

/**
 * SpotifyService – minimal façade over SpotifyClient.
 * Provides track search and top-track lookup; no caching or mapping.
 * TODO: Add caching, DTO mapping, and error normalization.
 */
@Service
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    public SpotifyService(SpotifyClient spotifyClient) {
        this.spotifyClient = spotifyClient;
    }

    /**
     * Search for tracks by query.
     * Currently returns raw JSON from client.
     *
     * @param query search term
     * @return JSON node with track results
     */
    public JsonNode searchTracks(String query) {
        return spotifyClient.searchTracks(query);
    }

    /**
     * Get top tracks for an artist name.
     * Throws IllegalArgumentException if no artist found.
     *
     * @param artistName artist name
     * @return JSON node with top-tracks payload
     * @throws IllegalArgumentException when artist not found
     * TODO:
     *   - Map to domain model instead of leaking JSON
     *   - Handle cases where Spotify payload changes
     */
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
