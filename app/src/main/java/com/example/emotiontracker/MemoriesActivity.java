package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MemoriesActivity extends AppCompatActivity {

    protected androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories);
        setupUIViews();
        initToolbar();
    }

    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Happy Memories");
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

    public void text_memories(View view) {
        Intent intent = new Intent (this, TextMemories.class);
        startActivity(intent);
    }

    public void audio_memories(View view) {
        Intent intent = new Intent (this, AudioMemories.class);
        startActivity(intent);
    }

    public void image_memories(View view) {
        Intent intent = new Intent (this, ImageMemories.class);
        startActivity(intent);
    }
}
