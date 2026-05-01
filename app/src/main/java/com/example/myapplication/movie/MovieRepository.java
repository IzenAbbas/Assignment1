package com.example.myapplication.movie;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class MovieRepository {
    public static final String CATEGORY_NOW_SHOWING = "now_showing";
    public static final String CATEGORY_COMING_SOON = "coming_soon";

    private static final String TAG = "MovieRepository";
    private static final String MOVIES_ASSET = "movies.json";

    private static List<Movie> cachedMovies;

    private MovieRepository() {
    }

    public static ArrayList<Movie> getMovies(Context context, String category) {
        ArrayList<Movie> movies = new ArrayList<>();
        for (Movie movie : getAllMovies(context)) {
            if (category.equals(movie.getCategory())) {
                movies.add(movie);
            }
        }
        return movies;
    }

    private static List<Movie> getAllMovies(Context context) {
        if (cachedMovies != null) {
            return cachedMovies;
        }

        ArrayList<Movie> movies = new ArrayList<>();
        try {
            String json = readAsset(context.getAssets(), MOVIES_ASSET);
            JSONArray array = new JSONArray(json);
            for (int index = 0; index < array.length(); index++) {
                JSONObject item = array.getJSONObject(index);
                movies.add(new Movie(
                        item.getString("title"),
                        item.getString("genre"),
                        item.getString("duration"),
                        item.getString("posterUrl"),
                        item.getString("trailerUrl"),
                        item.getString("category")
                ));
            }
        } catch (Exception exception) {
            Log.e(TAG, "Failed to parse movies.json", exception);
        }

        cachedMovies = movies;
        return movies;
    }

    private static String readAsset(AssetManager assetManager, String fileName) throws IOException {
        try (InputStream inputStream = assetManager.open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }
}
