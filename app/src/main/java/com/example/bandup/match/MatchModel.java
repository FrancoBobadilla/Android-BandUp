package com.example.bandup.match;

public class MatchModel {
    private String matchId;
    private String title;
    private String publisher;
    private String description;
    private String instruments;
    private String genres;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstruments() {
        return instruments;
    }

    public void setInstruments(String instruments) {
        this.instruments = instruments;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
