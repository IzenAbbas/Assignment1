package com.example.assignment1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class SeatSelectionFragment extends Fragment {
    private static final String ARG_MOVIE_TITLE = "movie_title";
    private static final String ARG_MOVIE_GENRE = "movie_genre";
    private static final String ARG_IS_COMING_SOON = "is_coming_soon";
    private int selectedSeats = 0;
    private static final double PRICE_PER_SEAT = 12.00;
    public static SeatSelectionFragment newInstance(String movieTitle, String movieGenre, boolean isComingSoon) {
        SeatSelectionFragment fragment = new SeatSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_TITLE, movieTitle);
        args.putString(ARG_MOVIE_GENRE, movieGenre);
        args.putBoolean(ARG_IS_COMING_SOON, isComingSoon);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seat_selection, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String movieTitle = getArguments() != null ? getArguments().getString(ARG_MOVIE_TITLE, "Movie") : "Movie";
        String movieGenre = getArguments() != null ? getArguments().getString(ARG_MOVIE_GENRE, "") : "";
        boolean isComingSoon = getArguments() != null && getArguments().getBoolean(ARG_IS_COMING_SOON, false);
        TextView txtMovieTitle = view.findViewById(R.id.txtMovieTitle);
        TextView txtGenreInfo = view.findViewById(R.id.txtGenreInfo);
        txtMovieTitle.setText(movieTitle);
        txtGenreInfo.setText(movieGenre);
        LinearLayout nowShowingButtons = view.findViewById(R.id.nowShowingButtons);
        LinearLayout comingSoonButtons = view.findViewById(R.id.comingSoonButtons);
        LinearLayout seatGridLeft = view.findViewById(R.id.seatGridLeft);
        LinearLayout seatGridRight = view.findViewById(R.id.seatGridRight);
        if (isComingSoon) {
            disableAllSeats(seatGridLeft);
            disableAllSeats(seatGridRight);
            nowShowingButtons.setVisibility(View.GONE);
            comingSoonButtons.setVisibility(View.VISIBLE);
            Button btnWatchTrailer = view.findViewById(R.id.btnWatchTrailer);
            btnWatchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String searchQuery = movieTitle + " official trailer";
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery)));
                    startActivity(intent);
                }
            });
        } else {
            // Randomly assign booked and unavailable seats
            randomlyAssignSeats(seatGridLeft, seatGridRight);
            setupSeatClicks(seatGridLeft);
            setupSeatClicks(seatGridRight);
            nowShowingButtons.setVisibility(View.VISIBLE);
            comingSoonButtons.setVisibility(View.GONE);
            Button btnBookDirect = view.findViewById(R.id.btnBookDirect);
            Button btnProceedSnacks = view.findViewById(R.id.btnProceedSnacks);
            btnBookDirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedSeats == 0) selectedSeats = 2;
                    double totalPrice = selectedSeats * PRICE_PER_SEAT;
                    saveBooking(movieTitle, selectedSeats, totalPrice);
                    Toast.makeText(getContext(), "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
            btnProceedSnacks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedSeats == 0) selectedSeats = 2;
                    double ticketPrice = selectedSeats * PRICE_PER_SEAT;
                    SnacksFragment fragment = SnacksFragment.newInstance(movieTitle, selectedSeats, ticketPrice);
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        }
    }
    private void randomlyAssignSeats(LinearLayout seatGridLeft, LinearLayout seatGridRight) {
        // Collect all seat ImageViews
        List<ImageView> allSeats = new ArrayList<>();
        collectSeats(seatGridLeft, allSeats);
        collectSeats(seatGridRight, allSeats);
        int totalSeats = allSeats.size();
        int bookedCount = (int) (totalSeats * 0.20); // ~20% booked (green)
        int unavailableCount = (int) (totalSeats * 0.10); // ~10% unavailable (red)
        // Create a shuffled list of indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < totalSeats; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, new Random());
        // Assign booked seats (green, non-clickable)
        for (int i = 0; i < bookedCount && i < indices.size(); i++) {
            ImageView seat = allSeats.get(indices.get(i));
            seat.setImageResource(R.drawable.ic_seat_booked);
            seat.setTag("booked");
            seat.setEnabled(false);
            seat.setClickable(false);
        }
        // Assign unavailable seats (red, non-clickable)
        for (int i = bookedCount; i < bookedCount + unavailableCount && i < indices.size(); i++) {
            ImageView seat = allSeats.get(indices.get(i));
            seat.setImageResource(R.drawable.ic_seat_unavailable);
            seat.setTag("unavailable");
            seat.setEnabled(false);
            seat.setClickable(false);
        }
    }
    private void collectSeats(LinearLayout seatGrid, List<ImageView> seats) {
        for (int i = 0; i < seatGrid.getChildCount(); i++) {
            View row = seatGrid.getChildAt(i);
            if (row instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) row;
                for (int j = 0; j < rowLayout.getChildCount(); j++) {
                    View seat = rowLayout.getChildAt(j);
                    if (seat instanceof ImageView) {
                        seats.add((ImageView) seat);
                    }
                }
            }
        }
    }
    private void setupSeatClicks(LinearLayout seatGrid) {
        for (int i = 0; i < seatGrid.getChildCount(); i++) {
            View row = seatGrid.getChildAt(i);
            if (row instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) row;
                for (int j = 0; j < rowLayout.getChildCount(); j++) {
                    View seat = rowLayout.getChildAt(j);
                    if (seat instanceof ImageView) {
                        final ImageView seatView = (ImageView) seat;
                        // Only set click listeners on available seats
                        if (seatView.getTag() != null) continue; // Skip booked/unavailable
                        seatView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v.isSelected()) {
                                    v.setSelected(false);
                                    seatView.setImageResource(R.drawable.ic_seat_available);
                                    selectedSeats--;
                                } else {
                                    v.setSelected(true);
                                    seatView.setImageResource(R.drawable.ic_seat_selected);
                                    selectedSeats++;
                                }
                            }
                        });
                    }
                }
            }
        }
    }
    private void disableAllSeats(LinearLayout seatGrid) {
        for (int i = 0; i < seatGrid.getChildCount(); i++) {
            View row = seatGrid.getChildAt(i);
            if (row instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) row;
                for (int j = 0; j < rowLayout.getChildCount(); j++) {
                    View seat = rowLayout.getChildAt(j);
                    if (seat instanceof ImageView) {
                        ImageView seatView = (ImageView) seat;
                        seatView.setImageResource(R.drawable.ic_seat_booked);
                        seat.setEnabled(false);
                        seat.setClickable(false);
                    }
                }
            }
        }
    }
    private void saveBooking(String movieTitle, int numSeats, double totalPrice) {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("BookingPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("movie_name", movieTitle);
        editor.putInt("num_seats", numSeats);
        editor.putFloat("total_price", (float) totalPrice);
        editor.apply();
    }
}
