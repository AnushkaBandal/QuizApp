package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    FirebaseFirestore db;
    TextView name, phone, rollno;
    Button startExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = FirebaseFirestore.getInstance();

        startExam = (Button) findViewById(R.id.btnContiue);
        name = (TextView) findViewById(R.id.text_username);
        phone = (TextView) findViewById(R.id.text_phno);
        rollno = (TextView) findViewById(R.id.text_rollno);

        Spinner dept_spinner = (Spinner) findViewById(R.id.dept_spinner);
        ArrayAdapter<CharSequence> dept_adapter = ArrayAdapter.createFromResource(this,
                R.array.dept, android.R.layout.simple_spinner_item);
        dept_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept_spinner.setAdapter(dept_adapter);

        Spinner year_spinner = (Spinner) findViewById(R.id.year_spinner);
        ArrayAdapter<CharSequence> year_adapter = ArrayAdapter.createFromResource(this,
                R.array.year, android.R.layout.simple_spinner_item);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        year_spinner.setAdapter(year_adapter);

        FloatingActionButton fab = findViewById(R.id.fab);

        getData();

        startExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dept = dept_spinner.getSelectedItem().toString();
                String year = year_spinner.getSelectedItem().toString();
                Intent intent = new Intent(Home.this, McqPage.class);
                intent.putExtra("dept", dept);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Logged Out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    void getData(){
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        name.setText(task.getResult().get("name").toString());
                        phone.setText(task.getResult().get("phone").toString());
                        rollno.setText(task.getResult().get("roll_no").toString());
                    }
                });
    }
}