package com.example.myapplication.movie;

import java.io.Serializable;

public class Movie implements Serializable {
    private final String title;
    private final String genre;
    private final String duration;
    private final String posterUrl;
    private final String trailerUrl;
    private final String category;

    public Movie(String title, String genre, String duration, String posterUrl, String trailerUrl, String category) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.posterUrl = posterUrl;
        this.trailerUrl = trailerUrl;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getDuration() {
        return duration;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public String getCategory() {
        return category;
    }
}
