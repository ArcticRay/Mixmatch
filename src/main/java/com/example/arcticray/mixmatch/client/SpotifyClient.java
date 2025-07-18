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

@Component
public class SpotifyClient {

    private final WebClient apiClient;
    private final WebClient tokenClient;
    private final String clientId;
    private final String clientSecret;

    public SpotifyClient(WebClient spotifyApiClient, WebClient spotifyTokenClient, SpotifyConfig cfg) {
        this.apiClient = spotifyApiClient;
        this.tokenClient = spotifyTokenClient;
        this.clientId = cfg.getClientId();
        this.clientSecret = cfg.getClientSecret();
    }

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

    public JsonNode getAudioFeatures(List<String> ids) {
        String token = getAccessToken();
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
}
