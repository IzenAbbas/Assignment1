package com.example.assignment1;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movieList;
    private Context context;
    private boolean isComingSoon;
    public MovieAdapter(Context context, List<Movie> movieList, boolean isComingSoon) {
        this.context = context;
        this.movieList = movieList;
        this.isComingSoon = isComingSoon;
    }
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.imgPoster.setImageResource(movie.getPosterResId());
        holder.txtTitle.setText(movie.getTitle());
        holder.txtGenreDuration.setText(movie.getGenre() + " / " + movie.getDuration() + " min");
        holder.btnBookSeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeatSelectionFragment fragment = SeatSelectionFragment.newInstance(
                        movie.getTitle(), movie.getGenre(), isComingSoon);
                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        holder.btnTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = movie.getTitle() + " official trailer";
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery)));
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return movieList.size();
    }
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitle;
        TextView txtGenreDuration;
        AppCompatButton btnBookSeats;
        AppCompatButton btnTrailer;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtGenreDuration = itemView.findViewById(R.id.txtGenreDuration);
            btnBookSeats = itemView.findViewById(R.id.btnBookSeats);
            btnTrailer = itemView.findViewById(R.id.btnTrailer);
        }
    }
}
