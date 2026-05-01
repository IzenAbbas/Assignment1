package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Booking;
import com.example.myapplication.movie.Movie;
import com.example.myapplication.movie.MovieImageLoader;
import com.example.myapplication.movie.MovieRepository;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookings;
    private final OnBookingCancelListener listener;

    public interface OnBookingCancelListener {
        void onCancelRequested(Booking booking);
    }

    public BookingAdapter(List<Booking> bookings, OnBookingCancelListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.movieName.setText(booking.getMovieName());
        holder.dateTime.setText(booking.getDateTime());
        holder.tickets.setText(booking.getSeats() + " Tickets");

        List<Movie> nowShowing = MovieRepository.getMovies(holder.itemView.getContext(), MovieRepository.CATEGORY_NOW_SHOWING);
        List<Movie> comingSoon = MovieRepository.getMovies(holder.itemView.getContext(), MovieRepository.CATEGORY_COMING_SOON);
        
        Movie movie = findMovieByTitle(booking.getMovieName(), nowShowing, comingSoon);
        if (movie != null && movie.getPosterUrl() != null) {
            MovieImageLoader.load(movie.getPosterUrl(), holder.poster);
        } else {
            holder.poster.setImageResource(R.drawable.image);
        }

        holder.cancelButton.setOnClickListener(v -> {
            if (listener != null) listener.onCancelRequested(booking);
        });
    }

    private Movie findMovieByTitle(String title, List<Movie> list1, List<Movie> list2) {
        for (Movie m : list1) {
            if (m.getTitle().equalsIgnoreCase(title)) return m;
        }
        for (Movie m : list2) {
            if (m.getTitle().equalsIgnoreCase(title)) return m;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView movieName, dateTime, tickets;
        ImageButton cancelButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.booking_poster);
            movieName = itemView.findViewById(R.id.booking_movie_name);
            dateTime = itemView.findViewById(R.id.booking_date_time);
            tickets = itemView.findViewById(R.id.booking_tickets);
            cancelButton = itemView.findViewById(R.id.btn_cancel_booking);
        }
    }
}
