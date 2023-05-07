package com.example.acrylicsnails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String PREF_KEY = LoginActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private EditText userEmailEt;
    private EditText passwordEt;
    private Button loginButton;
    private Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();
        int secret_key = bundle.getInt("SECRET_KEY");

        if (secret_key != SECRET_KEY){
            finish();
        }
        userEmailEt = findViewById(R.id.editTextUserEmail);
        passwordEt = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(view ->{
            Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_button);
            loginButton.startAnimation(animation);
            login(view);
        });

        signupButton.setOnClickListener(view -> {
            Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_button);
            signupButton.startAnimation(animation);
            goToRegist(view);
        });

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view){
        String email = userEmailEt.getText().toString();
        String passw = passwordEt.getText().toString();

        if(email.equals("") || passw.equals("")){
            Toast.makeText(LoginActivity.this, "You must fill in all fields!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()){
                Toast.makeText(LoginActivity.this, "Login was successful!", Toast.LENGTH_LONG).show();
                startShopping();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startShopping(){
        Intent intent = new Intent(this, HomeScreenActivity.class);

        finish();

        startActivity(intent);
    }

    public void goToRegist(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);

        startActivity(intent);
    }

    public void cancel(View view) {
        finish();
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userEmail", userEmailEt.getText().toString());
        editor.putString("password", passwordEt.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}