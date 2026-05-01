package com.example.myapplication.movie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    public interface MovieActionListener {
        void onTrailerClicked(Movie movie);

        void onBookSeatsClicked(Movie movie);
    }

    private final ArrayList<Movie> movies = new ArrayList<>();
    private final MovieActionListener listener;

    public MovieAdapter(MovieActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Movie> items) {
        movies.clear();
        movies.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.movieTitle.setText(movie.getTitle());
        holder.movieSubtitle.setText(movie.getGenre() + " / " + movie.getDuration());
        holder.moviePoster.setImageResource(R.drawable.bg_movie_poster);
        holder.moviePoster.setContentDescription(holder.itemView.getContext().getString(R.string.home_movie_poster_desc, movie.getTitle()));
        MovieImageLoader.load(movie.getPosterUrl(), holder.moviePoster);

        holder.trailerButton.setContentDescription(holder.itemView.getContext().getString(R.string.home_play_trailer_desc, movie.getTitle()));
        holder.trailerButton.setOnClickListener(v -> listener.onTrailerClicked(movie));
        holder.bookButton.setOnClickListener(v -> listener.onBookSeatsClicked(movie));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView moviePoster;
        private final TextView movieTitle;
        private final TextView movieSubtitle;
        private final MaterialButton trailerButton;
        private final MaterialButton bookButton;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieTitle = itemView.findViewById(R.id.movie_title);
            movieSubtitle = itemView.findViewById(R.id.movie_subtitle);
            trailerButton = itemView.findViewById(R.id.movie_trailer_button);
            bookButton = itemView.findViewById(R.id.movie_book_button);
        }
    }
}
