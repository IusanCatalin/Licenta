package com.example.emotiontracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TextActivity extends AppCompatActivity {

    String text;
    EditText editText;
    TextView result;
    float score_emotion_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_activity);
        editText = findViewById(R.id.edit_text);
        result = findViewById(R.id.result);
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
                            result.setText(String.valueOf(score_emotion_text));
                        }
                    },
                    10000 // 1k = 1 sec, delay for getting the response for server
            );
        }
    }
}
