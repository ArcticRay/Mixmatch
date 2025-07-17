package com.example.arcticray.mixmatch.controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.arcticray.mixmatch.service.SpotifyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SpotifyControllerTest {

    private MockMvc mvc;

    @Mock
    private SpotifyService service;

    @InjectMocks
    private SpotifyController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /api/spotify/search – 200 and JSON payload")
    void searchReturnsJson() throws Exception {
        // given
        ObjectNode fake = JsonNodeFactory.instance.objectNode()
                .put("foo", "bar");
        Mockito.when(service.searchTracks("beatles"))
                .thenReturn(fake);

        // when + then
        mvc.perform(get("/api/spotify/search")
                        .param("q", "beatles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.foo").value("bar"));
    }

    @Test
    @DisplayName("GET /api/spotify/search – service throws → 500 with message")
    void searchHandlesException() throws Exception {
        Mockito.when(service.searchTracks(anyString()))
                .thenThrow(new RuntimeException("ups"));

        mvc.perform(get("/api/spotify/search")
                        .param("q", "whatever")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("ups"));
    }

    @Test
    @DisplayName("GET /api/spotify/artist/tracks – 200 and list of track names")
    void artistTracksReturnsNames() throws Exception {
        // create an ArrayNode with 2 tracks
        ArrayNode tracks = JsonNodeFactory.instance.arrayNode();
        ObjectNode t1 = JsonNodeFactory.instance.objectNode().put("name", "Track A");
        ObjectNode t2 = JsonNodeFactory.instance.objectNode().put("name", "Track B");
        tracks.add(t1).add(t2);
        ObjectNode topTracksWrapper = JsonNodeFactory.instance.objectNode().set("tracks", tracks);

        Mockito.when(service.getArtistTopTracks("artist"))
                .thenReturn(topTracksWrapper);

        mvc.perform(get("/api/spotify/artist/tracks")
                        .param("name", "artist")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Track A"))
                .andExpect(jsonPath("$[1]").value("Track B"));
    }

    @Test
    @DisplayName("GET /api/spotify/artist/tracks – unknown artist → 404 with error")
    void artistTracksNotFound() throws Exception {
        Mockito.when(service.getArtistTopTracks("nobody"))
                .thenThrow(new IllegalArgumentException("No artist found: nobody"));

        mvc.perform(get("/api/spotify/artist/tracks")
                        .param("name", "nobody")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("No artist found")));
    }

    @Test
    @DisplayName("GET /api/spotify/test – returns top track name")
    void verifyConnectionReturnsTopTrack() throws Exception {
        ArrayNode tracks = JsonNodeFactory.instance.arrayNode();
        tracks.add(JsonNodeFactory.instance.objectNode().put("name", "Hit Song"));
        ObjectNode wrapper = JsonNodeFactory.instance.objectNode().set("tracks", tracks);

        Mockito.when(service.getArtistTopTracks("some"))
                .thenReturn(wrapper);

        mvc.perform(get("/api/spotify/test")
                        .param("name", "some")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Hit Song"));
    }

    @Test
    @DisplayName("GET /api/spotify/test – no tracks → 404")
    void verifyConnectionNoTracks() throws Exception {
        ObjectNode wrapper = JsonNodeFactory.instance.objectNode().set("tracks", JsonNodeFactory.instance.arrayNode());

        Mockito.when(service.getArtistTopTracks("empty"))
                .thenReturn(wrapper);

        mvc.perform(get("/api/spotify/test")
                        .param("name", "empty")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No tracks found")));
    }

    @Test
    @DisplayName("GET /api/spotify/test – service error → 500")
    void verifyConnectionServiceError() throws Exception {
        Mockito.when(service.getArtistTopTracks("error"))
                .thenThrow(new RuntimeException("FAIL"));

        mvc.perform(get("/api/spotify/test")
                        .param("name", "error")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Server error"));
    }
}
