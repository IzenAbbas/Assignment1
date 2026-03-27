package com.example.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SeatSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        Button btnBookDirect = findViewById(R.id.btnBookDirect);
        Button btnProceedSnacks = findViewById(R.id.btnProceedSnacks);

        btnBookDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeatSelectionActivity.this, TicketSummaryActivity.class));
            }
        });

        btnProceedSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SeatSelectionActivity.this, SnacksActivity.class));
            }
        });
    }
}
