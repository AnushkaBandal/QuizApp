package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private static final String TAG = "log";
    FirebaseAuth mAuth;
    private static final int REQUEST_CHECK_SETTINGS = 0;
    SharedPreferences prefs;
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    Button btnContinue;
    TextInputEditText nameField, rollnoField;
    String token;
    Context context;
    ProgressBar progressBar;
    String result;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();
        progressBar = (ProgressBar) findViewById(R.id.register_progress);
        progressBar.setVisibility(View.INVISIBLE);
        btnContinue = (Button)findViewById(R.id.signup_btn);
        nameField = (TextInputEditText)findViewById(R.id.textField_name);
        rollnoField = (TextInputEditText)findViewById(R.id.rollno_field);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameField.getText().toString().isEmpty()){
                    nameField.setError("Enter Your Full Name");
                    nameField.requestFocus();
                    return;
                } else {
                    registerUser();
                }
            }
        });
    }

    private void registerUser(){
        progressBar.setVisibility(View.VISIBLE);
        String strName, strPhone, strRollno;
        strName = nameField.getText().toString().trim();
        strRollno = rollnoField.getText().toString().trim();
        strPhone = getIntent().getStringExtra("PhoneNum");

        //store user data locally
        prefs = getSharedPreferences("com.codicts.quizapp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", strName);
        editor.putString("phone", strPhone);
        editor.putString("rollno", strRollno);
        editor.apply();


        // Create a new user with a first and last name
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", strName);
        userData.put("roll_no", strRollno);
        userData.put("phone", strPhone);

// Add a new document with a generated ID
        db.collection("users").document(FirebaseAuth.getInstance().getUid())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "onSuccess: successsss");
                        Intent intent = new Intent(Signup.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }
}