package com.example.arcticray.mixmatch.model;

public class Track {
    private String id;
    private String name;
    private String artist;
    private String album;
    private String previewUrl;
    private String uri;

    // Audio Features
    private double danceability;
    private double energy;
    private double valence;
    private double tempo;
    private double acousticness;
    private double instrumentalness;
    private double speechiness;
    private double liveness;

    // no args
    public Track(){

    }

    // all args
    public Track(String id, String name, String artist, String album, String previewUrl, String uri,
                 double danceability, double energy, double valence, double tempo,
                 double acousticness, double instrumentalness, double speechiness, double liveness) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.previewUrl = previewUrl;
        this.uri = uri;
        this.danceability = danceability;
        this.energy = energy;
        this.valence = valence;
        this.tempo = tempo;
        this.acousticness = acousticness;
        this.instrumentalness = instrumentalness;
        this.speechiness = speechiness;
        this.liveness = liveness;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public String getUri() {
        return uri;
    }

    public double getDanceability() {
        return danceability;
    }

    public double getEnergy() {
        return energy;
    }

    public double getValence() {
        return valence;
    }

    public double getTempo() {
        return tempo;
    }

    public double getAcousticness() {
        return acousticness;
    }

    public double getInstrumentalness() {
        return instrumentalness;
    }

    public double getSpeechiness() {
        return speechiness;
    }

    public double getLiveness() {
        return liveness;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }

    public void setInstrumentalness(double instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public void setSpeechiness(double speechiness) {
        this.speechiness = speechiness;
    }

    public void setLiveness(double liveness) {
        this.liveness = liveness;
    }
}
