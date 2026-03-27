package com.example.assignment1;
public class Movie {
    private String title;
    private String genre;
    private int duration;
    private int posterResId;
    public Movie(String title, String genre, int duration, int posterResId) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.posterResId = posterResId;
    }
    public String getTitle() {
        return title;
    }
    public String getGenre() {
        return genre;
    }
    public int getDuration() {
        return duration;
    }
    public int getPosterResId() {
        return posterResId;
    }
}
