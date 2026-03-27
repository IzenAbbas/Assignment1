package com.example.assignment1;
import android.content.Intent;
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
public class SeatSelectionFragment extends Fragment {
    private static final String ARG_MOVIE_TITLE = "movie_title";
    private static final String ARG_MOVIE_GENRE = "movie_genre";
    private static final String ARG_IS_COMING_SOON = "is_coming_soon";
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
            nowShowingButtons.setVisibility(View.VISIBLE);
            comingSoonButtons.setVisibility(View.GONE);
            Button btnBookDirect = view.findViewById(R.id.btnBookDirect);
            Button btnProceedSnacks = view.findViewById(R.id.btnProceedSnacks);
            btnBookDirect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
            });
            btnProceedSnacks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SnacksActivity.class));
                }
            });
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
                        seat.setEnabled(false);
                        seat.setAlpha(0.3f);
                        seat.setClickable(false);
                    }
                }
            }
        }
    }
}
