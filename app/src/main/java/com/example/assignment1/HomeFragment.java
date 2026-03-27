package com.example.assignment1;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Now Showing");
                } else {
                    tab.setText("Coming Soon");
                }
            }
        }).attach();
        ImageView btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenu().add("View Last Booking");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showLastBooking();
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
    private void showLastBooking() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("BookingPrefs", Context.MODE_PRIVATE);
        String movieName = prefs.getString("movie_name", null);
        if (movieName == null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Last Booking")
                    .setMessage("No previous booking found.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            int numSeats = prefs.getInt("num_seats", 0);
            float totalPrice = prefs.getFloat("total_price", 0f);
            String message = "Movie: " + movieName + "\n"
                    + "Seats: " + numSeats + "\n"
                    + "Total Price: $" + String.format("%.0f", totalPrice);
            new AlertDialog.Builder(getContext())
                    .setTitle("Last Booking")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}
