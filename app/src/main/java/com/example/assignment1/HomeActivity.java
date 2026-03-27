package com.example.assignment1;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppCompatButton btnBookMovie1 = findViewById(R.id.btnBookMovie1);
        AppCompatButton btnBookMovie2 = findViewById(R.id.btnBookMovie2);
        AppCompatButton btnBookMovie3 = findViewById(R.id.btnBookMovie3);
        AppCompatButton btnBookMovie4 = findViewById(R.id.btnBookMovie4);
        View.OnClickListener bookListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SeatSelectionActivity.class));
            }
        };
        btnBookMovie1.setOnClickListener(bookListener);
        btnBookMovie2.setOnClickListener(bookListener);
        btnBookMovie3.setOnClickListener(bookListener);
        btnBookMovie4.setOnClickListener(bookListener);
    }
}
