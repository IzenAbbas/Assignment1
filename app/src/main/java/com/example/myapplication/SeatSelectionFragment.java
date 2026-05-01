package com.example.myapplication;

import android.os.Bundle;
import android.view.Gravity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.movie.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeatSelectionFragment extends Fragment {
    private static final String ARG_MOVIE = "arg_movie";
    private static final int ROW_COUNT = 6;
    private static final int SEATS_PER_SIDE = 4;
    private static final int AISLE_COLUMNS = 1;
    private static final int TOTAL_COLUMNS = (SEATS_PER_SIDE * 2) + AISLE_COLUMNS;

    private final Map<String, AppCompatButton> seatButtons = new LinkedHashMap<>();
    private final java.util.List<String> mySelectedSeats = new java.util.ArrayList<>();

    private GridLayout seatGrid;
    private DatabaseReference seatRootReference;
    private ValueEventListener seatListener;
    private String currentUserId;
    private String currentMovieKey;
    private final java.util.List<String> confirmedBookedSeats = new java.util.ArrayList<>();
    private com.google.firebase.firestore.ListenerRegistration firestoreListener;

    public static SeatSelectionFragment newInstance(Movie movie) {
        SeatSelectionFragment fragment = new SeatSelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seat_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.seat_back_button);
        TextView titleView = view.findViewById(R.id.seat_movie_title);
        seatGrid = view.findViewById(R.id.seat_grid);

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        AppCompatButton btnProceedSnacks = view.findViewById(R.id.btn_proceed_snacks);
        btnProceedSnacks.setOnClickListener(v -> {
            countSelectedSeatsAndProceed();
        });

        AppCompatButton btnBookSeats = view.findViewById(R.id.btn_book_seats);
        btnBookSeats.setOnClickListener(v -> {
            bookSeatsDirectly();
        });

        Movie movie = getMovie();
        if (movie != null) {
            titleView.setText(movie.getTitle());
            currentMovieKey = buildMovieKey(movie.getTitle());
            currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
            seatRootReference = FirebaseDatabase.getInstance().getReference("seat_bookings").child(currentMovieKey);
            buildSeatGrid();
            attachSeatListener();
            attachFirestoreListener();
        }
    }

    @Override
    public void onDestroyView() {
        if (seatRootReference != null && seatListener != null) {
            seatRootReference.removeEventListener(seatListener);
        }
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
        seatListener = null;
        seatButtons.clear();
        seatGrid = null;
        super.onDestroyView();
    }

    @Nullable
    private Movie getMovie() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return null;
        }
        return (Movie) arguments.getSerializable(ARG_MOVIE);
    }

    private void buildSeatGrid() {
        if (seatGrid == null) {
            return;
        }

        seatGrid.removeAllViews();
        seatButtons.clear();
        seatGrid.setColumnCount(TOTAL_COLUMNS);

        for (int rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++) {
            String rowLabel = String.valueOf((char) ('A' + rowIndex));
            for (int columnIndex = 0; columnIndex < TOTAL_COLUMNS; columnIndex++) {
                if (columnIndex == SEATS_PER_SIDE) {
                    View aisle = new View(requireContext());
                    GridLayout.LayoutParams aisleParams = new GridLayout.LayoutParams();
                    aisleParams.width = dp(28);
                    aisleParams.height = dp(22);
                    aisle.setLayoutParams(aisleParams);
                    seatGrid.addView(aisle);
                    continue;
                }

                int seatNumber = columnIndex < SEATS_PER_SIDE ? columnIndex + 1 : columnIndex;
                String seatId = rowLabel + seatNumber;
                AppCompatButton seatButton = new AppCompatButton(requireContext());
                GridLayout.LayoutParams seatParams = new GridLayout.LayoutParams();
                seatParams.width = dp(22);
                seatParams.height = dp(22);
                if (columnIndex != 0 && columnIndex != SEATS_PER_SIDE + 1) {
                    seatParams.setMarginStart(dp(8));
                }
                if (rowIndex != 0) {
                    seatParams.topMargin = dp(8);
                }
                seatButton.setLayoutParams(seatParams);
                seatButton.setPadding(0, 0, 0, 0);
                seatButton.setMinWidth(0);
                seatButton.setMinHeight(0);
                seatButton.setAllCaps(false);
                seatButton.setText("");
                seatButton.setTag(seatId);
                seatButton.setContentDescription(seatId);
                seatButton.setBackgroundResource(R.drawable.bg_seat_available);
                seatButton.setOnClickListener(v -> onSeatClicked(seatId));
                seatButtons.put(seatId, seatButton);
                seatGrid.addView(seatButton);
            }
        }
    }

    private void attachSeatListener() {
        if (seatRootReference == null) {
            return;
        }

        seatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (Map.Entry<String, AppCompatButton> entry : seatButtons.entrySet()) {
                    String seatId = entry.getKey();
                    AppCompatButton seatButton = entry.getValue();
                    String ownerId = snapshot.child(seatId).getValue(String.class);
                    applySeatState(seatButton, ownerId, seatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        seatRootReference.addValueEventListener(seatListener);
    }

    private void attachFirestoreListener() {
        if (currentMovieKey == null) return;

        firestoreListener = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("booked_seats")
                .document(currentMovieKey)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null || !snapshot.exists()) return;

                    java.util.Map<String, Object> seats = (java.util.Map<String, Object>) snapshot.get("seats");
                    if (seats != null) {
                        confirmedBookedSeats.clear();
                        confirmedBookedSeats.addAll(seats.keySet());
                        
                        if (seatButtons != null) {
                            for (Map.Entry<String, AppCompatButton> entry : seatButtons.entrySet()) {
                                if (confirmedBookedSeats.contains(entry.getKey())) {
                                    entry.getValue().setBackgroundResource(R.drawable.bg_seat_booked);
                                    entry.getValue().setEnabled(false);
                                }
                            }
                        }
                    }
                });
    }

    private void onSeatClicked(String seatId) {
        if (seatRootReference == null || TextUtils.isEmpty(currentUserId) || confirmedBookedSeats.contains(seatId)) {
            return;
        }

        seatRootReference.child(seatId).runTransaction(new com.google.firebase.database.Transaction.Handler() {
            @NonNull
            @Override
            public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData currentData) {
                Object existingValue = currentData.getValue();
                if (existingValue == null) {
                    currentData.setValue(currentUserId);
                    return com.google.firebase.database.Transaction.success(currentData);
                }

                if (currentUserId.equals(existingValue.toString())) {
                    currentData.setValue(null);
                    return com.google.firebase.database.Transaction.success(currentData);
                }

                return com.google.firebase.database.Transaction.abort();
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
            }
        });
    }

    private void applySeatState(AppCompatButton seatButton, @Nullable String ownerId, String seatId) {
        if (seatButton == null) {
            return;
        }

        if (confirmedBookedSeats.contains(seatId)) {
            seatButton.setBackgroundResource(R.drawable.bg_seat_booked);
            seatButton.setEnabled(false);
            mySelectedSeats.remove(seatId);
            return;
        }

        seatButton.setEnabled(true);
        if (TextUtils.isEmpty(ownerId)) {
            seatButton.setBackgroundResource(R.drawable.bg_seat_available);
            return;
        }

        if (ownerId.equals(currentUserId)) {
            seatButton.setBackgroundResource(R.drawable.bg_seat_yours);
            if (!mySelectedSeats.contains(seatId)) {
                mySelectedSeats.add(seatId);
            }
        } else {
            seatButton.setBackgroundResource(R.drawable.bg_seat_booked);
            mySelectedSeats.remove(seatId);
        }
    }

    private void countSelectedSeatsAndProceed() {
        if (mySelectedSeats.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Please select at least one seat", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        Movie movie = getMovie();
        int seatsCount = mySelectedSeats.size();
        double ticketPrice = seatsCount * 10.0;

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, SnacksFragment.newInstance(movie, seatsCount, ticketPrice, new java.util.ArrayList<>(mySelectedSeats)))
                .addToBackStack(null)
                .commit();
    }

    private void bookSeatsDirectly() {
        if (mySelectedSeats.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Please select at least one seat", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        Movie movie = getMovie();
        int seatsCount = mySelectedSeats.size();
        double ticketPrice = seatsCount * 10.0;

        android.content.Intent intent = new android.content.Intent(requireContext(), TicketSummaryActivity.class);
        intent.putExtra("movie", movie);
        intent.putExtra("seats_count", seatsCount);
        intent.putExtra("seat_ids", new java.util.ArrayList<>(mySelectedSeats));
        intent.putExtra("ticket_price", ticketPrice);
        intent.putExtra("snacks_total", 0.0);
        startActivity(intent);
    }

    private String buildMovieKey(String title) {
        return title == null ? "movie" : title.toLowerCase().replaceAll("[^a-z0-9]+", "_");
    }

    private int dp(int value) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}
