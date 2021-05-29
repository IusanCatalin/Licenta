package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class AudioMemories extends AppCompatActivity {

    protected androidx.appcompat.widget.Toolbar toolbar;
    private ArrayList<String> audio_files = new ArrayList<String>();
    private ListView listView;
    private MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_memories);
        setupUIViews();
        initToolbar();
        setAudioFilesNames();
        listView=(ListView) findViewById(R.id.listViewAudio);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.single_item_audio_memories, audio_files);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String file_name = (String)listView.getItemAtPosition(position);
                playAudio(file_name);
            }
        });
    }

    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Audio memories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home : {
                onBackPressed();
                stopAudio();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAudioFilesNames()
    {
        String path = Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Audios";
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            audio_files.add(files[i].getName());
        }
    }

    private void playAudio(String fileName){
        Log.w("File name to open:", fileName);
        String path = Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Audios";
        //String file_name_test = "file_example_MP3_1MG.mp3"; //used for emulator
        try {
            if(mp.isPlaying())
            {
                mp.pause();
                mp.stop();
                mp.reset();
            }
            else{
                mp.pause();
                mp.stop();
                mp.reset();
                mp = null;
            }
            if(mp == null){
                mp = new MediaPlayer();
            }
            mp.setDataSource(path + File.separator + fileName);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudio(){
        mp.pause();
        mp.stop();
        mp.reset();
        mp = null;
    }
}
