package com.example.assignment1;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
public class TicketSummaryFragment extends Fragment {
    private static final String ARG_MOVIE_TITLE = "movie_title";
    private static final String ARG_NUM_SEATS = "num_seats";
    private static final String ARG_TOTAL_PRICE = "total_price";
    public static TicketSummaryFragment newInstance(String movieTitle, int numSeats, double totalPrice) {
        TicketSummaryFragment fragment = new TicketSummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_TITLE, movieTitle);
        args.putInt(ARG_NUM_SEATS, numSeats);
        args.putDouble(ARG_TOTAL_PRICE, totalPrice);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket_summary, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String movieTitle = getArguments() != null ? getArguments().getString(ARG_MOVIE_TITLE, "Movie") : "Movie";
        int numSeats = getArguments() != null ? getArguments().getInt(ARG_NUM_SEATS, 2) : 2;
        double totalPrice = getArguments() != null ? getArguments().getDouble(ARG_TOTAL_PRICE, 0) : 0;
        TextView txtMovieName = view.findViewById(R.id.txtMovieName);
        TextView txtSeatsInfo = view.findViewById(R.id.txtSeatsInfo);
        TextView txtTicketPrice = view.findViewById(R.id.txtTicketPrice);
        TextView txtFinalTotal = view.findViewById(R.id.txtFinalTotal);
        LinearLayout btnBack = view.findViewById(R.id.btnBack);
        Button btnSendTicket = view.findViewById(R.id.btnSendTicket);
        txtMovieName.setText(movieTitle);
        txtSeatsInfo.setText(numSeats + " seats selected");
        txtTicketPrice.setText(String.format("$%.2f", totalPrice));
        txtFinalTotal.setText(String.format("$%.2f", totalPrice));
        saveBooking(movieTitle, numSeats, totalPrice);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        btnSendTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Ticket sent successfully!", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .popBackStack(null, getActivity().getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                }
            }
        });
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
