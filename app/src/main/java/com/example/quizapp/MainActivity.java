package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btnsubmit;
    EditText txtPhone, txtCode;
    private ProgressBar progressBar;
    TextInputLayout txtCodeLayout;
    private SharedPreferences preferences;
    String number;
    String token;
    FirebaseAuth mAuth;
    String PhoneNo;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PackageManager.PERMISSION_GRANTED);

        mAuth = FirebaseAuth.getInstance();
        btnsubmit = (Button)findViewById(R.id.btnSubmit);
        txtPhone = (EditText)findViewById(R.id.textPhone);
        txtCode = (EditText) findViewById(R.id.verifycode);
        txtCodeLayout = (TextInputLayout) findViewById(R.id.verifycode_layout);
        progressBar = (ProgressBar)findViewById(R.id.login_progress);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = txtPhone.getText().toString().trim();
                if(number.isEmpty() || number.length()<10){
                    txtPhone.setError("Valid Number is Required");
                    txtPhone.requestFocus();
                    return;
                }
                PhoneNo = "+91"+number;
                sendVerificationCode(PhoneNo);
            }
        });

        txtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() == 6) {
                    btnsubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            verifyCode(s.toString());
                        }
                    });
                } else {
                    btnsubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            txtCode.setError("Enter Code");
                            txtCode.requestFocus();
                            return;
                        }
                    });
                }
            }
        });
    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                20,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
        progressBar.setVisibility(View.VISIBLE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
//            Toast.makeText(LoginActivity.this,"Code Sent", Toast.LENGTH_LONG).show();
            txtCodeLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                txtCode.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
//            Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Error", e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    };

    private void verifyCode(String code){
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.VISIBLE);
                        if(task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Log.d("app", "onComplete: success");
                            Intent intent = new Intent(MainActivity.this, Signup.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("PhoneNum", PhoneNo);
                            startActivity(intent);
                        }
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}