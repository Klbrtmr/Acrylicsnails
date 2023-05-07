package com.example.acrylicsnails;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {
    private FirebaseFirestore db;
//    private RadioGroup radioGroup;
//    private RadioButton selectedRadioButton;

    private ListView listView;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        listView = findViewById(R.id.listView);

        populateTimeList();
        setupRestoreButton();

        Button foglalasButton = findViewById(R.id.foglalasButton);
        foglalasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBooking();
            }
        });

        Button visszaButton = findViewById(R.id.cancel_button);
        visszaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });
    }

    private void populateTimeList() {
        CollectionReference timesRef = db.collection("times");
        timesRef.whereEqualTo("state", "available")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<String> timeList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String time = document.getString("time");
                                timeList.add(time);
                            }
                            adapter.clear();
                            adapter.addAll(timeList);
                            adapter.notifyDataSetChanged();


                            ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_list_item_1, timeList);
                            listView.setAdapter(adapter);
                        }
                    } else {
                        Log.e("BookingActivity", "Error getting available times: " + task.getException());
                    }
                });
    }

    private void restoreSelectedTimeInSpinner(String selectedTime) {
        String time = selectedTime.split(" ")[0];
        adapter.add(time + " (booked)");
        adapter.notifyDataSetChanged();
    }

    private void startBooking() {
        String selectedTime = spinner.getSelectedItem().toString();
        foglalasEsemeny(selectedTime);
    }


    private void foglalasEsemeny(String selectedTime) {
        CollectionReference timesRef = db.collection("times");
        Query query = timesRef.whereEqualTo("time", selectedTime);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    String documentId = document.getId();

                    timesRef.document(documentId)
                            .update("state", "booked")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(BookingActivity.this, "Időpont lefoglalva", Toast.LENGTH_SHORT).show();
                                refreshTimeList();
                                removeSelectedTimeFromSpinner(selectedTime);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(BookingActivity.this, "Hiba történt a foglalás során", Toast.LENGTH_SHORT).show();
                                Log.e("BookingActivity", "Error updating time: " + e.getMessage());
                            });
                }
            } else {
                Toast.makeText(BookingActivity.this, "Hiba történt a foglalás során", Toast.LENGTH_SHORT).show();
                Log.e("BookingActivity", "Error getting times: " + task.getException());
            }
        });
    }

    private void removeSelectedTimeFromSpinner(String selectedTime) {
        String time = selectedTime.split(" ")[0];
        adapter.remove(time + " (booked)");
        adapter.notifyDataSetChanged();
    }

    private void restoreAllTimes() {
        CollectionReference timesRef = db.collection("times");
        timesRef.whereEqualTo("state", "booked")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String documentId = document.getId();
                                timesRef.document(documentId)
                                        .update("state", "available")
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(BookingActivity.this, "Az összes időpont visszaállítva", Toast.LENGTH_SHORT).show();
                                            populateTimeList(); // Frissítsd a listát a visszaállítás után
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(BookingActivity.this, "Hiba történt az időpontok visszaállítása során", Toast.LENGTH_SHORT).show();
                                            Log.e("BookingActivity", "Error updating time: " + e.getMessage());
                                        });
                            }
                        }
                    } else {
                        Log.e("BookingActivity", "Error getting booked times: " + task.getException());
                    }
                });
    }

    private void setupRestoreButton() {
        Button restoreButton = findViewById(R.id.restoreButton);
        restoreButton.setOnClickListener(v -> {
            restoreAllTimes();
        });
    }

    private void refreshTimeList() {
        populateTimeList();
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }
}





























    /*private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        // Időpontok lekérése Firestore-ból
        CollectionReference timesRef = db.collection("times");
        timesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String time = document.getString("time");

                        // RadioButton létrehozása és hozzáadása a RadioGroup-hoz
                        RadioButton radioButton = new RadioButton(BookingActivity.this);
                        radioButton.setText(time);
                        radioGroup.addView(radioButton);
                    }
                }
            } else {
                Log.e("BookingActivity", "Error getting times: " + task.getException());
            }
        });
    }*/


    /*
    private static final String LOG_TAG = BookingActivity.class.getName();
    private static final String PREF_KEY = BookingActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private FirebaseAuth mAuth;

    private RadioGroup radioGroup;
    private Button selectButton;
    private Button cancelButton;
    private ListView listView;

    private List<String> items;
    private ArrayAdapter<String> adapter2;

    private RadioButton selectedRadioButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Bundle bundle = getIntent().getExtras();
        int secret_key = bundle.getInt("SECRET_KEY");

        if (secret_key != SECRET_KEY){
            finish();
        }

        Resources res = getResources();
        String[] itemList = res.getStringArray(R.array.nails_type);

        Spinner spinner = findViewById(R.id.nailstype_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemList);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = itemList[position];
                Toast.makeText(getApplicationContext(), "Kiválasztott elem: " + selectedValue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                finish();
            }
        });


    }

*/
