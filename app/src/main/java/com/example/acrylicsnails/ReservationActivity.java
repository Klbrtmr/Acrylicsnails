package com.example.acrylicsnails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        db = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.listView);

        fetchBookedTimes();

        Button visszaButton = findViewById(R.id.cancel_button);
        visszaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReservation();
            }
        });

    }


    public void cancel(View view) {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    private void fetchBookedTimes() {
        CollectionReference timesRef = db.collection("times");
        timesRef.whereEqualTo("state", "booked")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<String> bookedTimesList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String time = document.getString("time");
                                bookedTimesList.add(time);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ReservationActivity.this, android.R.layout.simple_list_item_1, bookedTimesList);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        Log.e("ReservationActivity", "Error fetching booked times: " + task.getException());
                    }
                });
    }

    private void deleteReservation() {
        db = FirebaseFirestore.getInstance();

        CollectionReference timesRef = db.collection("times");

        timesRef.whereEqualTo("state", "booked")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Végigmegyünk az összes találaton
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                // Megkapjuk az adott dokumentum Firestore DocumentID-ját
                                String documentId = document.getId();

                                // Az időpont állapotát visszaállítjuk "available"-re
                                timesRef.document(documentId)
                                        .update("state", "available")
                                        .addOnSuccessListener(aVoid -> {
                                            // Sikeres törlés esetén frissítsd a listát vagy a megjelenítést
                                            refreshReservationList();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Hiba esetén jeleníts meg hibaüzenetet
                                            Toast.makeText(ReservationActivity.this, "Hiba történt a törlés során", Toast.LENGTH_SHORT).show();
                                            Log.e("ReservationActivity", "Error updating reservation: " + e.getMessage());
                                        });
                            }
                        }
                    } else {
                        // Hiba esetén jeleníts meg hibaüzenetet
                        Toast.makeText(ReservationActivity.this, "Hiba történt a törlés során", Toast.LENGTH_SHORT).show();
                        Log.e("ReservationActivity", "Error getting reservations: " + task.getException());
                    }
                });
    }

    private void refreshReservationList() {
        db = FirebaseFirestore.getInstance();

        CollectionReference timesRef = db.collection("times");

        timesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<String> timeList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String state = document.getString("state");
                                if (state != null && state.equals("booked")) {
                                    String time = document.getString("time");
                                    timeList.add(time);
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ReservationActivity.this, android.R.layout.simple_list_item_1, timeList);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(ReservationActivity.this, "Hiba történt az időpontok frissítése során", Toast.LENGTH_SHORT).show();
                        Log.e("ReservationActivity", "Error getting times: " + task.getException());
                    }
                });
    }

}