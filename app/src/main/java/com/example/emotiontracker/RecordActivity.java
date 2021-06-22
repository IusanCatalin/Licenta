package com.example.emotiontracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class RecordActivity extends AppCompatActivity {

    protected androidx.appcompat.widget.Toolbar toolbar;
    private SpeechEmotion speechEmotion;
    private String[] topEmotionsSpeech;
    private String audio_counter;
    private Button record_btn;
    public static double speech_happinessScore=0.0;
    public static Context context;
    public static Boolean user_recorded_audio=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        record_btn = findViewById(R.id.record_button);
        context = RecordActivity.this;
        setupUIViews();
        initToolbar();
    }


    public void startRecording(View view)
    {
        speechEmotion = new SpeechEmotion(context);
        record_btn.getBackground().setColorFilter(0xff888888, PorterDuff.Mode.MULTIPLY);
    }

    public void stopRecording(View view) throws FileNotFoundException {
        try {
            speechEmotion.stopListening();
            topEmotionsSpeech = speechEmotion.getTopEmotions();
            String text_to_display = "";
            try {
                for (String emotion : topEmotionsSpeech) {
                    text_to_display = text_to_display + emotion + " ";
                }
            } catch (NullPointerException e) {
                Log.w("EROARE CITIT VOKATURI", "NU A RETURNAT NICI UN STRING, PROBABIL EROARE DE CITIRE DATE");
            }
        }catch(java.lang.NullPointerException e)
        {
            Toast toast =Toast.makeText(this, "Please record something first", Toast.LENGTH_SHORT);
            toast.show();
        }
        try {
            set_speech_happinessScore(topEmotionsSpeech);
        }catch (java.lang.NullPointerException e){
            Toast toast =Toast.makeText(this, "Please record something first", Toast.LENGTH_SHORT);
            toast.show();
        }
        if(speech_happinessScore > 0.70) //change here with 0.70, for easier testing we will add all files for the moment
        {
            setPreferences();
            Log.w("AUDIO_COUNTER",audio_counter);
            speechEmotion.saveAudioFile(audio_counter);
        }
        record_btn.getBackground().clearColorFilter();
    }

    private void set_speech_happinessScore(String[] topEmotions){
        if(topEmotions[0].equals("No results found, audio unclear or low quality")) {
            speech_happinessScore = 0;
        }
        else {
            Toast.makeText(this, "Successfully added audio", Toast.LENGTH_LONG).show();
            user_recorded_audio = true;
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    if (topEmotions[i] == "anger")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "contempt")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "disgust")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "fear")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "happiness")
                        speech_happinessScore = speech_happinessScore + 1;
                    else if (topEmotions[i] == "neutral")
                        speech_happinessScore = speech_happinessScore + 0.5;
                    else if (topEmotions[i] == "sadness")
                        speech_happinessScore = speech_happinessScore + 0;
                    else
                        speech_happinessScore = speech_happinessScore + 0.5;
                } else if (i == 1) {
                    if (topEmotions[i] == "anger")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "contempt")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "disgust")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "fear")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "happiness")
                        speech_happinessScore = speech_happinessScore + 0.5;
                    else if (topEmotions[i] == "neutral")
                        speech_happinessScore = speech_happinessScore + 0.25;
                    else if (topEmotions[i] == "sadness")
                        speech_happinessScore = speech_happinessScore + 0;
                    else
                        speech_happinessScore = speech_happinessScore + 0.25;
                } else {
                    if (topEmotions[i] == "anger")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "contempt")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "disgust")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "fear")
                        speech_happinessScore = speech_happinessScore + 0;
                    else if (topEmotions[i] == "happiness")
                        speech_happinessScore = speech_happinessScore + 0.33;
                    else if (topEmotions[i] == "neutral")
                        speech_happinessScore = speech_happinessScore + 0.16;
                    else if (topEmotions[i] == "sadness")
                        speech_happinessScore = speech_happinessScore + 0;
                    else
                        speech_happinessScore = speech_happinessScore + 0.16;
                }
            }
        }
    }

    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Record Audio");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home : {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPreferences()
    {
        Context context = RecordActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.audio_file_counter), Context.MODE_PRIVATE);
        String defaultValue = "0";
        String counter = sharedPref.getString(getString(R.string.audio_file_counter), defaultValue);
        audio_counter = counter;
        SharedPreferences.Editor editor = sharedPref.edit();
        counter= String.valueOf(Integer.parseInt(counter)+1);
        editor.putString(getString(R.string.audio_file_counter), counter);
        editor.apply();
    }
}
