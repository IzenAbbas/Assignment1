package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.data.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyBookingsFragment extends Fragment {

    private RecyclerView rvBookings;
    private BookingAdapter adapter;
    private final List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference bookingsRef;
    private ValueEventListener bookingsListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvBookings = view.findViewById(R.id.rv_my_bookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookingAdapter(bookingList, this::handleCancelRequest);
        rvBookings.setAdapter(adapter);

        view.findViewById(R.id.btn_my_bookings_back).setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            bookingsRef = FirebaseDatabase.getInstance().getReference("bookings").child(userId);
            attachBookingsListener();
        }
    }

    private void attachBookingsListener() {
        bookingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                    Booking booking = bookingSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        bookingList.add(booking);
                    }
                }
                Collections.reverse(bookingList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        bookingsRef.addValueEventListener(bookingsListener);
    }

    private void handleCancelRequest(Booking booking) {
        if (!isFutureBooking(booking.getDateTime())) {
            Toast.makeText(requireContext(), "Cannot cancel past bookings", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("No", null)
                .show();
    }

    private boolean isFutureBooking(String dateTimeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault());
            Date bookingDate = sdf.parse(dateTimeStr);
            if (bookingDate == null) return false;

            return bookingDate.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    private void cancelBooking(Booking booking) {
        bookingsRef.child(booking.getBookingId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bookingsRef != null && bookingsListener != null) {
            bookingsRef.removeEventListener(bookingsListener);
        }
    }
}
