package com.example.acrylicsnails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private FirebaseAuth mAuth;
    EditText usernameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Bundle bundle = getIntent().getExtras();
//        int secret_key = bundle.getInt("SECRET_KEY");
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 99){
            finish();
        }

        usernameEditText = findViewById(R.id.usernameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        usernameEditText.setText(userName);
        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String userName = usernameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordAgainEditText.getText().toString();

        if (!email.matches("^[A-Za-z0-9]{3,}[@][a-z]{4,}[.][a-z]{2,}")){
            Toast.makeText(RegisterActivity.this, "The email format is not valid!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(passwordAgain)){
            Toast.makeText(RegisterActivity.this, "Password does not match!", Toast.LENGTH_LONG).show();
            return;
        }
        if(userName.equals("") || email.equals("") || password.equals("") || passwordAgain.equals("")) {
            Toast.makeText(RegisterActivity.this, "You must fill in all fields!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task->{
            if (task.isSuccessful()){
                Toast.makeText(RegisterActivity.this, "Successful registration!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, HomeScreenActivity.class);
                intent.putExtra("SECRET_KEY", SECRET_KEY);
                finish();
                startActivity(intent);
            }else {
            Toast.makeText(RegisterActivity.this, "Failed to create user: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}