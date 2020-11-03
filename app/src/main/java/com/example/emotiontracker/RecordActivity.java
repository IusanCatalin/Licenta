package com.example.emotiontracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecordActivity extends AppCompatActivity {

    private SpeechEmotion speechEmotion;
    private Context context;
    private String[] topEmotionsSpeech;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);
        context = RecordActivity.this;
        resultText = findViewById(R.id.speech_tw);
    }

    public void startRecording(View view)
    {
        speechEmotion = new SpeechEmotion(context);
    }

    public void stopRecording(View view)
    {
        try {
            speechEmotion.stopListening();

            topEmotionsSpeech = speechEmotion.getTopEmotions();
            String text_to_display = "";
            try {
                for (String emotion : topEmotionsSpeech) {
                    text_to_display = text_to_display + emotion + " ";
                }
            } catch (NullPointerException e) {
                Log.w("ERAORE CITIT VOKATURI", "NU A RETURNAT NICI UN STRING, PROBABIL EROARE DE CITIRE DATE");
            }
            resultText.setText(text_to_display);
        }catch(java.lang.NullPointerException e)
        {
            Toast toast =Toast.makeText(this, "Please record something first", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
