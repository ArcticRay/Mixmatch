package com.example.arcticray.mixmatch.client;

import com.example.arcticray.mixmatch.config.SpotifyConfig;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * SpotifyClient – low-level Spotify Web API caller.
 * Fetches access tokens per request; returns raw JSON.
 * TODO: Centralize token management, add retry/backoff, and handle rate limits.
 */
@Component
public class SpotifyClient {

    private final WebClient apiClient;
    private final WebClient tokenClient;
    private final String clientId;
    private final String clientSecret;

    public SpotifyClient(WebClient spotifyApiClient,
                         WebClient spotifyTokenClient,
                         SpotifyConfig cfg) {
        this.apiClient    = spotifyApiClient;
        this.tokenClient  = spotifyTokenClient;
        this.clientId     = cfg.getClientId();
        this.clientSecret = cfg.getClientSecret();
    }

    /**
     * Retrieve a client-credentials token.
     *
     * @return bearer token
     * @throws IllegalStateException if token isn’t returned
     * TODO: Cache token until expiry instead of fetching every time.
     */
    private String getAccessToken() {
        String basic = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        JsonNode resp = tokenClient.post()
                .header("Authorization", "Basic " + basic)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (resp == null || resp.get("access_token") == null) {
            throw new IllegalStateException("Kein Access Token erhalten");
        }
        return resp.get("access_token").asText();
    }

    /**
     * Search tracks endpoint.
     *
     * @param query search query
     * @return raw JSON response
     */
    public JsonNode searchTracks(String query) {
        String token = getAccessToken();
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "track")
                        .queryParam("limit", "10")
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * Search artists endpoint.
     *
     * @param query artist name
     * @return raw JSON response
     */
    public JsonNode searchArtists(String query) {
        String token = getAccessToken();
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "artist")
                        .queryParam("limit", "1")
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * Get top tracks for given artist ID.
     *
     * @param artistId Spotify artist ID
     * @return raw JSON with top-tracks
     */
    public JsonNode getTopTracks(String artistId) {
        String token = getAccessToken();
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/artists/{id}/top-tracks")
                        .queryParam("market", "DE")
                        .build(artistId))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * Get audio features for a list of track IDs.
     *
     * @param ids list of Spotify track IDs
     * @return raw JSON with audio features
     */
    public JsonNode getAudioFeatures(List<String> ids) {
        String token  = getAccessToken();
        String joined = String.join(",", ids);
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/audio-features")
                        .queryParam("ids", joined)
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * Get current user’s playlists.
     *
     * @param limit  max items
     * @param offset paging offset
     * @return raw JSON with playlists
     */
    public JsonNode getCurrentUserPlaylists(int limit, int offset) {
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/playlists")
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build())
                .headers(h -> h.setBearerAuth(getAccessToken()))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    /**
     * Get tracks of a playlist.
     *
     * @param playlistId Spotify playlist ID
     * @param limit      max items
     * @param offset     paging offset
     * @return raw JSON with playlist tracks
     */
    public JsonNode getPlaylistTracks(String playlistId, int limit, int offset) {
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/playlists/{id}/tracks")
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build(playlistId))
                .headers(h -> h.setBearerAuth(getAccessToken()))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
