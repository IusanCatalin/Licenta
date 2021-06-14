package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.ceylonlabs.imageviewpopup.ImagePopup;

import java.io.File;
import java.util.ArrayList;

public class ImageMemories extends AppCompatActivity {

    protected androidx.appcompat.widget.Toolbar toolbar;
    private ListView listView;
    private ArrayList<String> image_files = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_memories);
        listView=(ListView) findViewById(R.id.listViewImage);
        setupUIViews();
        initToolbar();
        setImageFilesNames();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.single_item_image_memeories, image_files);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w("clicked", "image");
                String file_name = (String)listView.getItemAtPosition(position);
                showImage(file_name);

            }
        });
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

    public void setupUIViews()
    {
        toolbar = findViewById(R.id.history_toolbar);
    }

    private void initToolbar(){
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Image Memories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setImageFilesNames()
    {
        String path = Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Images";
        File directory = new File(path);
        File[] files = directory.listFiles();
        try {
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                image_files.add(files[i].getName());
            }
        }catch (java.lang.NullPointerException e)
        {
            Log.w("No images" , "to display");
        }
    }

    private void showImage(String file_name)
    {
        File imgFile = new  File(Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Images/"+file_name);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Drawable d = new BitmapDrawable(getResources(), myBitmap);
            ImagePopup imagePopup = new ImagePopup(this);
            imagePopup.initiatePopup(d);
            imagePopup.viewPopup();
            Log.w("pop-up", "show");
        }
        else{
            Log.w("File path error", "image not found in folder");
        }
    }
}
