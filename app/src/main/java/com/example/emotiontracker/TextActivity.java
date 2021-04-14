package com.example.emotiontracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class TextActivity extends AppCompatActivity {

    protected androidx.appcompat.widget.Toolbar toolbar;
    private String text;
    private EditText editText;
    public static double score_emotion_text;
    public static int use_text= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_activity);
        editText = findViewById(R.id.edit_text);

        setupUIViews();
        initToolbar();
    }

    public void analyse_text(View view) {
        text = editText.getText().toString();
        if (text.matches("")) {
            Toast toast = Toast.makeText(this, "Please write something first", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Log.w("Textul de analizat este", text);
            TextEmotion textEmotion = new TextEmotion();
            textEmotion.execute(text);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            score_emotion_text = textEmotion.getEmotionScore();
                        }
                    },
                    2000 // 1k = 1 sec, delay for getting the response from server
            );
            use_text = 1;

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Log.w("Score text initial", String.valueOf(score_emotion_text));
                            if(score_emotion_text> 0.75)
                                writeToFile(text, TextActivity.this);
                        }
                    },
                    2000 // 1k = 1 sec, delay for getting the response from server
            );
        }
    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("happy_texts.txt", MODE_APPEND));
            outputStreamWriter.write(data+'\n');
            outputStreamWriter.close();
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Journal");
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

}