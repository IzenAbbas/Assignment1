package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Snack;
import com.example.myapplication.data.SnackDatabaseHelper;
import com.example.myapplication.movie.Movie;

import java.util.List;

public class SnacksFragment extends Fragment {
    private static final String ARG_MOVIE = "arg_movie";
    private static final String ARG_SEATS_COUNT = "arg_seats_count";
    private static final String ARG_TICKET_PRICE = "arg_ticket_price";
    private static final String ARG_SEAT_IDS = "arg_seat_ids";

    private List<Snack> snacks;
    private TextView tvTotalPrice;
    private double totalSnackPrice = 0;

    public static SnacksFragment newInstance(Movie movie, int seatsCount, double ticketPrice, java.util.ArrayList<String> seatIds) {
        SnacksFragment fragment = new SnacksFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE, movie);
        args.putInt(ARG_SEATS_COUNT, seatsCount);
        args.putDouble(ARG_TICKET_PRICE, ticketPrice);
        args.putStringArrayList(ARG_SEAT_IDS, seatIds);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_snacks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvSnacks = view.findViewById(R.id.rv_snacks);
        tvTotalPrice = view.findViewById(R.id.tv_total_snack_price);
        AppCompatButton btnConfirm = view.findViewById(R.id.btn_confirm_snacks);

        SnackDatabaseHelper dbHelper = new SnackDatabaseHelper(requireContext());
        snacks = dbHelper.getAllSnacks();

        SnackAdapter adapter = new SnackAdapter(snacks, this::calculateTotal);
        rvSnacks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSnacks.setAdapter(adapter);

        view.findViewById(R.id.btn_snacks_back).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnConfirm.setOnClickListener(v -> navigateToSummary());
    }

    private void calculateTotal() {
        totalSnackPrice = 0;
        for (Snack snack : snacks) {
            totalSnackPrice += snack.getPrice() * snack.getQuantity();
        }
        tvTotalPrice.setText(String.format("$%.2f", totalSnackPrice));
    }

    private void navigateToSummary() {
        if (totalSnackPrice == 0) {
            android.widget.Toast.makeText(requireContext(), "atleast select a snack", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        Movie movie = (Movie) getArguments().getSerializable(ARG_MOVIE);
        int seatsCount = getArguments().getInt(ARG_SEATS_COUNT);
        double ticketPrice = getArguments().getDouble(ARG_TICKET_PRICE);
        java.util.ArrayList<String> seatIds = getArguments().getStringArrayList(ARG_SEAT_IDS);

        android.content.Intent intent = new android.content.Intent(requireContext(), TicketSummaryActivity.class);
        intent.putExtra("movie", movie);
        intent.putExtra("seats_count", seatsCount);
        intent.putExtra("seat_ids", seatIds);
        intent.putExtra("ticket_price", ticketPrice);
        intent.putExtra("snacks_total", totalSnackPrice);
        startActivity(intent);
    }
}
