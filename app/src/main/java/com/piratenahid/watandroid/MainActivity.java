package com.piratenahid.watandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    List<String> wordList;
    Button start;
    LinearLayout linearLayoutSettings, linearLayoutWat;
    TextView wat;
    ScheduledFuture<?> t;
    ScheduledThreadPoolExecutor executor;
    Spinner set, time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordList = new ArrayList<String>();
        set = findViewById(R.id.spinner_set);
        time = findViewById(R.id.spinner_time);
        start = findViewById(R.id.start_button);
        linearLayoutSettings = findViewById(R.id.settings_lin);
        linearLayoutWat = findViewById(R.id.wat_lin);
        wat = findViewById(R.id.wat);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("words");
        executor = new ScheduledThreadPoolExecutor(15);




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
                List<List<String>> Set = getBatches(wordList, 80);
                linearLayoutSettings.setVisibility(View.GONE);
                linearLayoutWat.setVisibility(View.VISIBLE);
                System.out.println(Set);
                t = executor.scheduleAtFixedRate(new MyTask(), 0, 1, TimeUnit.SECONDS);



            }
        });

         // no


    }
    class MyTask implements Runnable {
        private int attempt = 0;
        @SuppressLint("ResourceAsColor")
        public void run() {
            int y = (int) (Math.random()* wordList.size());
            wat.setText(wordList.get(y));;
            if (++attempt > 5) {
                wat.setText("Finished");
                wat.setTextColor(R.color.purple_200);
                t.cancel(false);
            }
        }
    }

    public static <T> List<List<T>> getBatches(List<T> collection,int batchSize){
        int i = 0;
        List<List<T>> batches = new ArrayList<List<T>>();
        while(i<collection.size()){
            int nextInc = Math.min(collection.size()-i,batchSize);
            List<T> batch = collection.subList(i,i+nextInc);
            batches.add(batch);
            i = i + nextInc;
        }
        return batches;
    }

}