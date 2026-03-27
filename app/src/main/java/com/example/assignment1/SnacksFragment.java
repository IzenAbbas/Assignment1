package com.example.assignment1;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
public class SnacksFragment extends Fragment {
    private static final String ARG_MOVIE_TITLE = "movie_title";
    private static final String ARG_NUM_SEATS = "num_seats";
    private static final String ARG_TICKET_PRICE = "ticket_price";
    private SnackAdapter snackAdapter;
    private TextView txtFinalTotal;
    public static SnacksFragment newInstance(String movieTitle, int numSeats, double ticketPrice) {
        SnacksFragment fragment = new SnacksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOVIE_TITLE, movieTitle);
        args.putInt(ARG_NUM_SEATS, numSeats);
        args.putDouble(ARG_TICKET_PRICE, ticketPrice);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_snacks, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String movieTitle = getArguments() != null ? getArguments().getString(ARG_MOVIE_TITLE, "Movie") : "Movie";
        int numSeats = getArguments() != null ? getArguments().getInt(ARG_NUM_SEATS, 2) : 2;
        double ticketPrice = getArguments() != null ? getArguments().getDouble(ARG_TICKET_PRICE, 24.00) : 24.00;
        ListView listSnacks = view.findViewById(R.id.listSnacks);
        txtFinalTotal = view.findViewById(R.id.txtFinalTotal);
        Button btnConfirmOrder = view.findViewById(R.id.btnConfirmOrder);
        List<Snack> snackList = new ArrayList<>();
        snackList.add(new Snack("Popcorn", "Large / Buttered", 8.99, R.drawable.popcorn));
        snackList.add(new Snack("Nachos", "With Cheese Dip", 7.99, R.drawable.nachos));
        snackList.add(new Snack("Soft Drink", "Large / Any Flavor", 5.99, R.drawable.soft_drink));
        snackList.add(new Snack("Candy Mix", "Assorted Candies", 6.99, R.drawable.candy_mix));
        snackList.add(new Snack("Hot Dog", "With Toppings", 7.99, R.drawable.hotdog));
        snackAdapter = new SnackAdapter(getContext(), snackList);
        listSnacks.setAdapter(snackAdapter);
        updateTotal(ticketPrice);
        snackAdapter.setOnQuantityChangedListener(new SnackAdapter.OnQuantityChangedListener() {
            @Override
            public void onQuantityChanged() {
                updateTotal(ticketPrice);
            }
        });
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double totalPrice = ticketPrice + snackAdapter.getTotalPrice();
                TicketSummaryFragment fragment = TicketSummaryFragment.newInstance(
                        movieTitle, numSeats, totalPrice);
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
    private void updateTotal(double ticketPrice) {
        double total = ticketPrice + snackAdapter.getTotalPrice();
        txtFinalTotal.setText(String.format("Payable: $%.2f", total));
    }
}
