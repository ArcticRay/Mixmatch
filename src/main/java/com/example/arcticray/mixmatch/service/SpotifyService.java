package com.example.arcticray.mixmatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SpotifyService {

    private final WebClient apiClient;    // für /v1 Aufrufe
    private final WebClient tokenClient;  // für Token-Requests
    private final String clientId;
    private final String clientSecret;

    public SpotifyService(
            WebClient spotifyApiClient,
            WebClient spotifyTokenClient,
            com.example.arcticray.mixmatch.config.SpotifyConfig cfg
    ) {
        this.apiClient    = spotifyApiClient;
        this.tokenClient  = spotifyTokenClient;
        this.clientId     = cfg.getClientId();
        this.clientSecret = cfg.getClientSecret();
    }

    private String getAccessToken() {
        String basic = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret)
                        .getBytes(StandardCharsets.UTF_8));

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

    public JsonNode searchTracks(String query) {
        String token = getAccessToken();

        Mono<JsonNode> resp = apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "track")
                        .queryParam("limit", "10")
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class);

        return resp.block();
    }

    public JsonNode getArtistTopTracks(String artistName) {
        String token = getAccessToken();

        // Search Artist
        JsonNode artistSearch = apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", artistName)
                        .queryParam("type", "artist")
                        .queryParam("limit", 1)
                        .build())
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode items = artistSearch.path("artists").path("items");
        if (!items.isArray() || items.size() == 0) {
            throw new IllegalArgumentException("Kein Interpret gefunden: " + artistName);
        }
        String artistId = items.get(0).get("id").asText();

        // Get Top Tracks
        JsonNode topTracks = apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/artists/{id}/top-tracks")
                        .queryParam("market", "DE")
                        .build(artistId))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return topTracks;
    }
}
