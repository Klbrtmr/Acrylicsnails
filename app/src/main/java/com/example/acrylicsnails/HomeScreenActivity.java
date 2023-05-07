package com.example.acrylicsnails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class HomeScreenActivity extends AppCompatActivity {
    private static final String LOG_TAG = HomeScreenActivity.class.getName();
    private static final String PREF_KEY = HomeScreenActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 100;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        mAuth = FirebaseAuth.getInstance();

        CardView cardView = findViewById(R.id.card_view_booking);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBooking();
            }
        });
        CardView cardView2 = findViewById(R.id.my_appointments);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeAppointment();
            }
        });


    }

    private void seeAppointment(){
        Intent intent = new Intent(this, ReservationActivity.class);
        finish();
        startActivity(intent);
    }

    private void startBooking(){
        Intent intent = new Intent(this, BookingActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}