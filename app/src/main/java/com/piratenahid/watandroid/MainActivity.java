package com.piratenahid.watandroid;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView wat, noOfWordText;
    EditText editTime, noOfWordEdit;
    Switch sound, serial;
    ScheduledFuture<?> t;
    ScheduledThreadPoolExecutor executor;
    Spinner set;
    int set_selected, time_selected;
    List<List<String>> Set;
    List<String> last_set;
    ArrayAdapter<Integer> setAdapter;
    int noOfWords;
    MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.this.overridePendingTransition(
                R.anim.animate_windmill_enter,
                R.anim.animate_zoom_exit
        );
        wordList = new ArrayList<>();
        last_set = new ArrayList<>();
        load = findViewById(R.id.load);
        sound = findViewById(R.id.sound_switch);
        serial = findViewById(R.id.serial_switch);
        List<Integer> sets = new ArrayList<>();
        editTime = findViewById(R.id.edit_time);
        noOfWordEdit = findViewById(R.id.noOfWords);
        noOfWordText = findViewById(R.id.noOfWordsText);
        load.setText("Loading");
        load.setEnabled(false);


        set_selected = 1;
        time_selected = 10;
        noOfWords = 80;

        load.setOnClickListener(v -> {
            noOfWords = Integer.parseInt(noOfWordEdit.getText().toString());
            noOfWordEdit.setVisibility(View.GONE);
            noOfWordText.setVisibility(View.GONE);
            load.setVisibility(View.GONE);
            linearLayoutSettings.setVisibility(View.VISIBLE);
            Set = getBatches(wordList, noOfWords+1);
            for (int x = 0 ; x < Set.size() ; x++){
                sets.add(x+1);
                set.setAdapter(setAdapter);
            }
        });
        mp = MediaPlayer.create(this, R.raw.sound);




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
                load.setEnabled(true);
                load.setText("Go");

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

            showWat(attempt+1);





            if (++attempt > noOfWords) {
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

    private void showWat(int attempt){
        if(sound.isChecked()){
            mp.start();
        }

        int y = (int) (Math.random()* last_set.size());
        String word = last_set.get(y);
        String finalWord;
        if(serial.isChecked()){
            finalWord = attempt+". " + word;
        }
        else {
            finalWord = word;
        }
        wat.setText(finalWord);
        last_set.remove(word);
        System.out.println(last_set + "" + last_set.size());
    }



}