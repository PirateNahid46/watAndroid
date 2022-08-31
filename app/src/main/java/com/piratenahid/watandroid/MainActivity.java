package com.piratenahid.watandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    List<String> wordList;
    Button start;
    LinearLayout linearLayoutSettings, linearLayoutWat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordList = new ArrayList<String>();
        start = findViewById(R.id.start_button);
        linearLayoutSettings = findViewById(R.id.settings_lin);
        linearLayoutWat = findViewById(R.id.wat_lin);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("words");



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String word = dataSnapshot.child("name").getValue(String.class);
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
            }
        });





    }
}