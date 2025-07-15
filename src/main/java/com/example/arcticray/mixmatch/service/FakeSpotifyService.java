package com.example.arcticray.mixmatch.service;

import com.example.arcticray.mixmatch.model.Track;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FakeSpotifyService {
    public List<Track> getTracksFromPlaylist(String playlistId) {
        // playlistId – get real data soon
        List<Track> tracks = new ArrayList<>();

        tracks.add(new Track("1", "Dreams", "Fleetwood Mac", "Rumours", null, "spotify:track:1",
                0.55, 0.45, 0.65, 120.0, 0.1, 0.0, 0.05, 0.1));

        tracks.add(new Track("2", "Blinding Lights", "The Weeknd", "After Hours", null, "spotify:track:2",
                0.70, 0.80, 0.90, 171.0, 0.01, 0.0, 0.04, 0.08));

        tracks.add(new Track("3", "Numb", "Linkin Park", "Meteora", null, "spotify:track:3",
                0.55, 0.85, 0.40, 110.0, 0.02, 0.0, 0.05, 0.15));

        tracks.add(new Track("4", "Summertime Sadness", "Lana Del Rey", "Born to Die", null, "spotify:track:4",
                0.48, 0.58, 0.30, 123.0, 0.03, 0.0, 0.05, 0.12));

        return tracks;
    }
}
