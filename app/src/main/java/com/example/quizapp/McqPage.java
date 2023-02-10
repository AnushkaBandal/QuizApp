package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class McqPage extends AppCompatActivity {
    FirebaseFirestore db;
    Integer[] intArray = { 1, 2, 3, 4, 5 };
    List<Integer> intList = Arrays.asList(intArray);

    RadioGroup radioGroup;
    RadioButton a, b, c, d, radioAns;
    TextView que;
    Button next, prev, submit;
    int cnt = 1, score = 0;
    String[] arrAns = new String[6];
    String[] arrCorrAns = new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq_page);
        db = FirebaseFirestore.getInstance();
        Collections.shuffle(intList);
        intList.toArray(intArray);

        que = (TextView) findViewById(R.id.question);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        a = (RadioButton) findViewById(R.id.a);
        b = (RadioButton) findViewById(R.id.b);
        c = (RadioButton) findViewById(R.id.c);
        d = (RadioButton) findViewById(R.id.d);
        next = (Button) findViewById(R.id.btnNext);
        prev = (Button) findViewById(R.id.btnPrevious);
        submit = (Button) findViewById(R.id.submitMCQ);

        if(cnt == 1)
            getData(String.valueOf(cnt));

        int ans = radioGroup.getCheckedRadioButtonId();
        Log.e("tag", "onCreate: " + ans );


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cnt>=1 && cnt<6){
                    int selected = radioGroup.getCheckedRadioButtonId();
                    radioAns = (RadioButton) findViewById(selected);
                    arrAns[cnt] = radioAns.getText().toString();
                    ++cnt;
                    radioGroup.clearCheck();
                    getData(String.valueOf(cnt));
                    prev.setEnabled(true);
                }

                if(cnt == 5){
                    next.setEnabled(false);

                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cnt>=1 && cnt<6){
                    int selected = radioGroup.getCheckedRadioButtonId();
                    radioAns = (RadioButton) findViewById(selected);
                    arrAns[cnt] = radioAns.getText().toString();
                    --cnt;
                    radioGroup.clearCheck();
                    getData(String.valueOf(cnt));
                    next.setEnabled(true);
                }

                if(cnt == 1){
//                    prev.setVisibility(View.INVISIBLE);
                    prev.setEnabled(false);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selected = radioGroup.getCheckedRadioButtonId();
                radioAns = (RadioButton) findViewById(selected);
                arrAns[cnt] = radioAns.getText().toString();
                radioGroup.clearCheck();
                for(int i=1;i<6;i++){
                    if(arrAns[i].equals(arrCorrAns[i])){
                        score = score + 1;
                    }
                }

                SharedPreferences pref;
                pref = getSharedPreferences("com.codicts.quizapp", MODE_PRIVATE);
                String name = pref.getString("name", "User Name");
                String rollno = pref.getString("rollno", "0");
                String dept = getIntent().getStringExtra("dept");
                String year = getIntent().getStringExtra("year");

                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("user_id", FirebaseAuth.getInstance().getUid());
                userData.put("roll_no", rollno);
                userData.put("dept", dept);
                userData.put("year", year);
                userData.put("score", score);

                UUID uuid = UUID.randomUUID();
                String randomUUIDString = uuid.toString();

                db.collection("result")
                        .document(randomUUIDString)
                        .set(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("TAG", "onSuccess: data saved" );
                            }
                        });

                Intent intent = new Intent(McqPage.this, Result.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });
    }



    void getData(String doc){
        db.collection("questions")
                .document(doc)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        que.setText(task.getResult().get("ques").toString());
                        a.setText(task.getResult().get("a").toString());
                        b.setText(task.getResult().get("b").toString());
                        c.setText(task.getResult().get("c").toString());
                        d.setText(task.getResult().get("d").toString());
                        arrCorrAns[cnt] = task.getResult().get("ans").toString();
                    }
                });
    }
}