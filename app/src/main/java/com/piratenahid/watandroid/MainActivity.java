package com.piratenahid.watandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    List<String> wordList;
    Button start;
    LinearLayout linearLayoutSettings, linearLayoutWat;
    TextView wat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordList = new ArrayList<String>();
        start = findViewById(R.id.start_button);
        linearLayoutSettings = findViewById(R.id.settings_lin);
        linearLayoutWat = findViewById(R.id.wat_lin);
        wat = findViewById(R.id.wat);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("words");



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String word = dataSnapshot.child("name").getValue(String.class);
                    Log.d("", word);
                    wordList.add(word);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutSettings.setVisibility(View.GONE);
                linearLayoutWat.setVisibility(View.VISIBLE);

                new Timer().scheduleAtFixedRate(new TimerTask()
                {
                    @Override
                    public void run() {
                        int y = (int) (Math.random()* wordList.size());
                        wat.setText(wordList.get(y));; // call your method
                    }
                }, 0, 1000);
            }
        });





    }
}