# MixMatch 🎧

**MixMatch** is a backend service that analyzes Spotify playlists and generates personalized music recommendations based on audio features like tempo, energy, and mood.

## Features

- Analyze user playlists via the Spotify Web API
- Extract and process audio features (e.g. danceability, valence, tempo)
- Generate new song recommendations based on the playlist fingerprint
- Clean REST API built with Spring Boot
- Structured code with testing, logging, and CI/CD (in progress)

## Tech Stack

- Java 17+
- Spring Boot 3
- Spotify Web API
- OAuth2 Authorization Code Flow
- Maven or Gradle (your choice)
- Docker + GitHub Actions (CI/CD)

## Getting Started

Clone the repo and run locally:

```bash
./mvnw clean install
./mvnw spring-boot:run
