package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.movie.MoviePagerAdapter;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    private TextView sectionTitle;
    private ViewPager2 viewPager;
    private MaterialButton todayButton;
    private MaterialButton tomorrowButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sectionTitle = view.findViewById(R.id.home_section_title);
        todayButton = view.findViewById(R.id.home_today_button);
        tomorrowButton = view.findViewById(R.id.home_tomorrow_button);
        viewPager = view.findViewById(R.id.home_view_pager);

        MoviePagerAdapter adapter = new MoviePagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDateSelection(position);
                updateSectionTitle(position);
            }
        });

        todayButton.setOnClickListener(v -> viewPager.setCurrentItem(0, true));
        tomorrowButton.setOnClickListener(v -> viewPager.setCurrentItem(1, true));

        view.findViewById(R.id.btn_home_menu).setOnClickListener(this::showPopupMenu);

        updateDateSelection(0);
        updateSectionTitle(0);
    }

    private void updateDateSelection(int position) {
        boolean todaySelected = position == 0;

        todayButton.setBackgroundTintList(getResources().getColorStateList(todaySelected ? R.color.accent_red : R.color.surface_chip, requireContext().getTheme()));
        tomorrowButton.setBackgroundTintList(getResources().getColorStateList(todaySelected ? R.color.surface_chip : R.color.accent_red, requireContext().getTheme()));

        todayButton.setTextColor(getResources().getColor(todaySelected ? R.color.text_primary : R.color.text_secondary, requireContext().getTheme()));
        tomorrowButton.setTextColor(getResources().getColor(todaySelected ? R.color.text_secondary : R.color.text_primary, requireContext().getTheme()));

        todayButton.setIconResource(todaySelected ? R.drawable.ic_chip_selected : R.drawable.ic_chip_unselected);
        tomorrowButton.setIconResource(todaySelected ? R.drawable.ic_chip_unselected : R.drawable.ic_chip_selected);

        todayButton.setIconTintResource(todaySelected ? R.color.text_primary : R.color.text_secondary);
        tomorrowButton.setIconTintResource(todaySelected ? R.color.text_secondary : R.color.text_primary);
    }

    private void updateSectionTitle(int position) {
        if (sectionTitle == null) {
            return;
        }
        sectionTitle.setText(position == 0 ? R.string.home_now_showing : R.string.home_coming_soon);
    }

    private void showPopupMenu(View view) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(requireContext(), view);
        popup.getMenu().add("View Last Booking");
        popup.getMenu().add("My Bookings");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("View Last Booking")) {
                viewLastBooking();
            } else if (item.getTitle().equals("My Bookings")) {
                openMyBookings();
            }
            return true;
        });
        popup.show();
    }

    private void viewLastBooking() {
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) return;

        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Fetching last booking...");
        progressDialog.show();

        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("bookings")
                .child(userId)
                .orderByChild("dateTime")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        if (snapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                                com.example.myapplication.data.Booking booking = bookingSnapshot.getValue(com.example.myapplication.data.Booking.class);
                                if (booking != null) {
                                    showBookingDialog(booking);
                                }
                            }
                        } else {
                            new android.app.AlertDialog.Builder(requireContext())
                                    .setTitle("Last Booking")
                                    .setMessage("No previous booking found.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                        progressDialog.dismiss();
                        android.widget.Toast.makeText(requireContext(), "Error: " + error.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBookingDialog(com.example.myapplication.data.Booking booking) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Last Booking")
                .setMessage("Movie: " + booking.getMovieName() + "\n" +
                        "Seats: " + booking.getSeats() + "\n" +
                        "Total Price: $" + String.format(java.util.Locale.getDefault(), "%.2f", booking.getTotalPrice()))
                .setPositiveButton("OK", null)
                .show();
    }

    private void openMyBookings() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, new MyBookingsFragment())
                .addToBackStack(null)
                .commit();
    }
}
