package com.piratenahid.watandroid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    List<String> wordList;
    Button start, load;
    LinearLayout linearLayoutSettings, linearLayoutWat;
    TextView wat;
    EditText editTime;
    ScheduledFuture<?> t;
    ScheduledThreadPoolExecutor executor;
    Spinner set;
    int set_selected, time_selected;
    List<List<String>> Set;
    List<String> last_set;
    ArrayAdapter<Integer> setAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordList = new ArrayList<>();
        last_set = new ArrayList<>();
        load = findViewById(R.id.load);
        List<Integer> sets = new ArrayList<>();
        editTime = findViewById(R.id.edit_time);


        set_selected = 1;
        time_selected = 10;

        load.setOnClickListener(v -> {
            load.setVisibility(View.GONE);
            Set = getBatches(wordList, 80);
            for (int x = 0 ; x < Set.size() ; x++){
                sets.add(x+1);
                set.setAdapter(setAdapter);
            }
        });




              setAdapter  = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, sets);
        setAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        set = findViewById(R.id.spinner_set);
        set.setOnItemSelectedListener(this);

        start = findViewById(R.id.start_button);
        linearLayoutSettings = findViewById(R.id.settings_lin);
        linearLayoutWat = findViewById(R.id.wat_lin);
        wat = findViewById(R.id.wat);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("words");
        executor = new ScheduledThreadPoolExecutor(80);

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

        start.setOnClickListener(v -> {


            linearLayoutSettings.setVisibility(View.GONE);
            linearLayoutWat.setVisibility(View.VISIBLE);
            last_set = Set.get(set_selected);
            System.out.println(last_set);
            time_selected = Integer.parseInt(editTime.getText().toString());
            t = executor.scheduleAtFixedRate(new MyTask(), 0, time_selected, TimeUnit.SECONDS);



        });

         // no


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        set_selected = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class MyTask implements Runnable {
        private int attempt = 0;
        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        public void run() {

            showWat();





            if (++attempt > 80) {
                wat.setText("Finished");
                wat.setTextColor(R.color.purple_200);
                t.cancel(false);
            }
        }
    }

    public static <T> List<List<T>> getBatches(List<T> collection,int batchSize){
        int i = 0;
        List<List<T>> batches = new ArrayList<>();
        while(i<collection.size()){
            int nextInc = Math.min(collection.size()-i,batchSize);
            List<T> batch = collection.subList(i,i+nextInc);
            batches.add(batch);
            i = i + nextInc;
        }
        return batches;
    }

    private void showWat(){
        int y = (int) (Math.random()* last_set.size());
        String word = last_set.get(y);
        wat.setText(word);
        last_set.remove(word);
        System.out.println(last_set + "" + last_set.size());
    }



}