package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.myapplication.data.Booking;
import com.example.myapplication.movie.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TicketSummaryActivity extends AppCompatActivity {

    private Movie movie;
    private int seatsCount;
    private java.util.List<String> seatIds;
    private double ticketPrice;
    private double snacksTotal;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_summary);

        movie = (Movie) getIntent().getSerializableExtra("movie");
        seatsCount = getIntent().getIntExtra("seats_count", 0);
        seatIds = getIntent().getStringArrayListExtra("seat_ids");
        ticketPrice = getIntent().getDoubleExtra("ticket_price", 0);
        snacksTotal = getIntent().getDoubleExtra("snacks_total", 0);
        totalAmount = ticketPrice + snacksTotal;

        TextView tvMovie = findViewById(R.id.tv_summary_movie);
        TextView tvSeats = findViewById(R.id.tv_summary_seats);
        TextView tvTicketPrice = findViewById(R.id.tv_summary_ticket_price);
        TextView tvSnacksTotal = findViewById(R.id.tv_summary_snacks_total);
        TextView tvTotal = findViewById(R.id.tv_summary_total);
        AppCompatButton btnSendTicket = findViewById(R.id.btn_send_ticket);

        tvMovie.setText(movie != null ? movie.getTitle() : "Unknown Movie");
        tvSeats.setText(String.valueOf(seatsCount));
        tvTicketPrice.setText(String.format(Locale.getDefault(), "$%.2f", ticketPrice));
        tvSnacksTotal.setText(String.format(Locale.getDefault(), "$%.2f", snacksTotal));
        tvTotal.setText(String.format(Locale.getDefault(), "$%.2f", totalAmount));

        findViewById(R.id.btn_summary_back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        btnSendTicket.setOnClickListener(v -> saveBookingAndShare());
    }

    private void saveBookingAndShare() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";
        String bookingId = FirebaseDatabase.getInstance().getReference().child("bookings").child(userId).push().getKey();
        String dateTime = "13.04.2027, 22:15";

        Booking booking = new Booking(bookingId, userId, movie != null ? movie.getTitle() : "Unknown", seatsCount, seatIds, totalAmount, dateTime);

        if (bookingId != null) {
            FirebaseDatabase.getInstance().getReference("bookings").child(userId).child(bookingId).setValue(booking)
                    .addOnSuccessListener(aVoid -> {
                        saveBookedSeatsToFirestore();
                        Toast.makeText(this, "Booking confirmed and saved!", Toast.LENGTH_SHORT).show();
                        shareTicket();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveBookedSeatsToFirestore() {
        if (movie == null || seatIds == null || seatIds.isEmpty()) return;

        String movieKey = movie.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "_");
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        
        com.google.firebase.firestore.DocumentReference docRef = db.collection("booked_seats").document(movieKey);

        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "anonymous";
        for (String seatId : seatIds) {
            updates.put("seats." + seatId, uid);
        }

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                docRef.update(updates);
            } else {
                java.util.Map<String, Object> initialData = new java.util.HashMap<>();
                java.util.Map<String, String> seatsMap = new java.util.HashMap<>();
                for (String seatId : seatIds) {
                    seatsMap.put(seatId, uid);
                }
                initialData.put("seats", seatsMap);
                docRef.set(initialData);
            }
        });
    }

    private void shareTicket() {
        String shareBody = "CineFast Ticket\n" +
                "Movie: " + (movie != null ? movie.getTitle() : "Unknown") + "\n" +
                "Seats: " + seatsCount + "\n" +
                "Total Price: " + String.format(Locale.getDefault(), "$%.2f", totalAmount);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share Ticket via");
        startActivity(shareIntent);
    }
}
