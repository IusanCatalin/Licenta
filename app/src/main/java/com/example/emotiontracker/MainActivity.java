package com.example.emotiontracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.conn.HttpInetSocketAddress;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public static int use_image=0;
    private TextView welcome_text;
    private TextView general_score;
    private TextView popup_file_name_view;
    private ImageView imageView;
    private String Username;
    private String image_counter;
    private String[] topEmotionsImage;
    private Bitmap bitmapFrontCam;
    private Double general_happinessScore=0.0;
    private Double image_happinessScore=0.0;
    private int memory_case;
    private Database mDatabase;
    private final static int CAMERA_PIC_REQUEST = 300;
    private final static int REQUEST_CAMERA_PERMISSION = 400;
    private final static int REQUEST_READ_EXTERNAL_STORAGE = 500;
    private final static int REQUEST_WRITE_EXTERNAL_STORAGE = 600;
    private final static int REQUEST_INTERNET = 700;
    private final static int RECORD_AUDIO = 800;
    private MediaPlayer mp = new MediaPlayer();
    public static String[] happy_texts;
    public static Context context;

    //private static final int RESULT_LOAD_IMAGE = 100;   // variables for pick image from gallery test
    //private static final int REQUEST_PERMISSION_CODE = 200;
    //private Bitmap final_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        general_score = findViewById(R.id.result_score_general);
        imageView = findViewById(R.id.imageView);
        welcome_text = findViewById(R.id.welcome_text);
        context = this;
        checkPermissions();
        checkUserName();
        mDatabase = new Database(context);
        happy_texts = readFromFile(this).split(System.getProperty("line.separator"));
    }
    /*
    function for testing a image from gallery
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
     */

    /*
    function for testing a image from gallery

     private void requestPermission() {
        ActivityCompat.requestPermissions (MainActivity.this,new String[]{ READ_EXTERNAL_STORAGE }, REQUEST_PERMISSION_CODE );
    }
    */

    /*
    function for testing a image from gallery
    public void getImage(View view) {
        if(checkPermission()) {
            Intent choosePhotoIntent = new Intent(Intent. ACTION_PICK , android.provider.MediaStore.Images.Media. EXTERNAL_CONTENT_URI );
            startActivityForResult(choosePhotoIntent, RESULT_LOAD_IMAGE );
        }
        else {
            requestPermission();
        }
    }
    */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
        function for testing a image from gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(photo);
            final_bitmap = photo;
        }
        */
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmapFrontCam = (Bitmap) data.getParcelableExtra("BitmapImage");
                } catch (Exception e) {
                    e.printStackTrace();
                    //to do if needed
                }
                imageView.setImageBitmap(bitmapFrontCam);
                if(imageView.getDrawable() != null)
                {
                    getEmotion();
                }
                else
                {
                    Toast.makeText(this, "Picture missing", Toast.LENGTH_SHORT)
                            .show();
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    public void getEmotion() {
        use_image = 1;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            bitmapFrontCam.compress(Bitmap.CompressFormat.JPEG, 100, output);
        }catch(java.lang.NullPointerException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(context, "You didn't take a photo!", Toast.LENGTH_LONG);
            toast.show();
        }
        InputStream photoInput = new ByteArrayInputStream(output.toByteArray());
        final GetEmotionCall emotionCall = new GetEmotionCall();
        emotionCall.execute(photoInput);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        topEmotionsImage = emotionCall.getResult();
                        String text_to_display="";
                        try {
                            for (String emotion : topEmotionsImage) {
                                text_to_display = text_to_display + emotion + " ";
                            }
                        }catch(NullPointerException e){
                            Log.w("ERAORE CITIT EMOTIONS", "NU A RETURNAT NICI UN STRING, PROBABIL EROARE DE KEY/SERVER" );
                            text_to_display="NO RESULT, ERROR";
                        }
                        if(topEmotionsImage[0] == "No results found, image unclear or low quality")
                        {
                            Log.w("IMAGINE NECLARA", "IMAGINE NECLARA" );
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.context, "Image unclear or low quality, try again!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            setImage_happinessScore(topEmotionsImage);
                        }
                    }
                },
                2000 // 1k = 1 sec, delay for getting the image processed
        );
        new java.util.Timer().schedule(
                new java.util.TimerTask(){
                    @Override
                    public void run() {
                        if(image_happinessScore > 0.1) //all images for the moment, testing, change 0.75 in real application
                        {
                            setPreferences();
                            saveImg(image_counter);
                        Log.w("FILE", "FILE CREATED");
                        }

                    }
                }, 3000
        );
    }

    public void onClickTakePicture(View view) {
        if (getFrontCameraId() == -1) {
            Log.w("CAMERA", "NO CAMERA FOUND");
            Log.w("CAMERA", "NO CAMERA FOUND");
        } else {
            Intent cameraIntent = new Intent();
            cameraIntent.setClass(this, FrontCamera.class);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
    }

    @SuppressWarnings("deprecation")
    int getFrontCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                return i;
        }
        return -1; // No front-facing camera found
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.INTERNET}, REQUEST_INTERNET);
        }
        if (ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
        }
    }

    public void recordSpeechActivity(View view)
    {
        Intent speechIntent = new Intent(MainActivity.this, RecordActivity.class);
        MainActivity.this.startActivity(speechIntent);
    }

    public void writeTextActivity(View view)
    {
        Intent textIntent = new Intent(MainActivity.this, TextActivity.class);
        MainActivity.this.startActivity(textIntent);
    }

    public void historyActivity(View view)
    {
        Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
        MainActivity.this.startActivity(historyIntent);
    }

    private void userNameAlert(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please write your name here");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user_name = input.getText().toString();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user_name", user_name);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void checkUserName()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_name = preferences.getString("user_name", "");
        Username = user_name;
        if (user_name == "")
            userNameAlert(context);

        welcome_text.setText("Welcome "+user_name+", please use one of the functions below.");
    }

    private void setImage_happinessScore(String[] topEmotions){
        for(int i =0; i < 3; i++)
        {
            if (i == 0)
            {
                if(topEmotions[i] == "anger")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "contempt")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "disgust")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "fear")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "happiness")
                    image_happinessScore = image_happinessScore + 1;
                else if (topEmotions[i] == "neutral")
                    image_happinessScore = image_happinessScore + 0.5;
                else if (topEmotions[i] == "sadness")
                    image_happinessScore = image_happinessScore + 0;
                else
                    image_happinessScore = image_happinessScore + 0.5;
            }
            else if (i == 1)
            {
                if(topEmotions[i] == "anger")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "contempt")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "disgust")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "fear")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "happiness")
                    image_happinessScore = image_happinessScore + 0.5;
                else if (topEmotions[i] == "neutral")
                    image_happinessScore = image_happinessScore + 0.25;
                else if (topEmotions[i] == "sadness")
                    image_happinessScore = image_happinessScore + 0;
                else
                    image_happinessScore = image_happinessScore + 0.25;
            }
            else
            {
                if(topEmotions[i] == "anger")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "contempt")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "disgust")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "fear")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "happiness")
                    image_happinessScore = image_happinessScore + 0.33;
                else if (topEmotions[i] == "neutral")
                    image_happinessScore = image_happinessScore + 0.16;
                else if (topEmotions[i] == "sadness")
                    image_happinessScore = image_happinessScore + 0;
                else
                    image_happinessScore = image_happinessScore + 0.16;
            }
        }
    }

    public void calculate_happiness(View view) {
        double d = 0.0;
        Log.w("HAPPINESS SCORE IMAGE", image_happinessScore.toString());
        Log.w("HAPPINESS SCORE SPEECH", Double.toString(RecordActivity.speech_happinessScore));
        Log.w("HAPPINESS SCORE TEXT", Double.toString(TextActivity.score_emotion_text));
        general_happinessScore = image_happinessScore + RecordActivity.speech_happinessScore + TextActivity.score_emotion_text;
        if( use_image == 1 ) {
            d = d + 1;
            use_image = 0;
        }
        if (RecordActivity.use_recording ==1) {
            d = d + 1;
            RecordActivity.use_recording = 0;
        }
        if (TextActivity.use_text == 1) {
            d = d + 1;
            TextActivity.use_text = 0;
        }
        general_happinessScore = general_happinessScore / d;
        general_happinessScore = general_happinessScore * 100;
        int final_value = general_happinessScore.intValue();
        general_score.setText(String.valueOf(final_value));
        mDatabase.createEntry(final_value);
        //reset values
        image_happinessScore = 0.0;
        RecordActivity.speech_happinessScore = 0.0;
        TextActivity.score_emotion_text = 0.0;
        general_happinessScore = 0.0;
    }

    public void memoriesActivity(View view) {
        Intent memoriesIntent = new Intent(MainActivity.this, MemoriesActivity.class);
        MainActivity.this.startActivity(memoriesIntent);
    }

    private String readFromFile(Context context) {
        String text = "";
        try {
            InputStream inputStream = context.openFileInput("happy_texts.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                text = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.w("TEXTUL CITIT ESTE",text);
        return text;
    }

    private void saveImg (String file_counter)
    {
        File f = new File(Environment.getExternalStorageDirectory() + "/EmotionTracker_Images");
        if(f.isDirectory())
        {
            File outputFile = new File(f, "vokaturi_image_"+file_counter+".png");
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                bitmapFrontCam.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            f.mkdirs();
            File outputFile = new File(f, "vokaturi_image_"+file_counter+".png");
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                bitmapFrontCam.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPreferences()
    {
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.image_file_counter), Context.MODE_PRIVATE);
        String defaultValue = "0";
        String counter = sharedPref.getString(getString(R.string.image_file_counter), defaultValue);
        image_counter = counter;
        SharedPreferences.Editor editor = sharedPref.edit();
        counter= String.valueOf(Integer.parseInt(counter)+1);
        editor.putString(getString(R.string.image_file_counter), counter);
        editor.apply();
    }

    public void show_happy_memory(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_main_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(memory_case==1) {
                    mp.pause();
                    mp.stop();
                    mp.reset();

                }
                popupWindow.dismiss();
                return true;
            }
        });
        Log.w("Textul file:", select_random_memory());
        popup_file_name_view = popupWindow.getContentView().findViewById(R.id.view_file_name);
        popup_file_name_view.setText(select_random_memory());
        if(memory_case == 1 || memory_case == 2)
        {
            MyClickListener lstn = new MyClickListener();
            popup_file_name_view.setOnClickListener(lstn);
        }
    }
    private String select_random_memory()
    {
        String selected_file = "no name";
        String path;
        File directory;
        File[] files;
        Random rand = new Random();
        int random = rand.nextInt(3);
        switch (random){
            case 0:
                //text memories
                memory_case=0;
                int array_size = happy_texts.length;
                int random_text = rand.nextInt(array_size);
                String selected_memory_text = happy_texts[random_text];
                selected_file = selected_memory_text;
                break;
            case 1:
                //audio memories
                memory_case = 1;
                ArrayList<String> audio_files = new ArrayList<String>();
                path = Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Audios";
                directory = new File(path);
                files = directory.listFiles();
                Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    audio_files.add(files[i].getName());
                }
                int random_audio = rand.nextInt(audio_files.size());
                String selected_memory_audio = audio_files.get(random_audio);
                selected_file = selected_memory_audio;
                break;
            case 2:
                //image memories
                memory_case = 2;
                ArrayList<String> image_files = new ArrayList<String>();
                path = Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Images";
                directory = new File(path);
                files = directory.listFiles();
                Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    image_files.add(files[i].getName());
                }
                int random_image = rand.nextInt(image_files.size());
                String selected_memory_image = image_files.get(random_image);
                selected_file = selected_memory_image;
                break;
        }
        return selected_file;
    }

    public class MyClickListener implements View.OnClickListener {
        private MySecondClickListener mSecondListener = new MySecondClickListener();
        public void onClick(View v){
            if (memory_case == 1){
                mSecondListener.onClick(v);
            }else{
                File imgFile = new  File(Environment.getExternalStorageDirectory().toString()+"/EmotionTracker_Images/"+popup_file_name_view.getText());
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Drawable d = new BitmapDrawable(getResources(), myBitmap);
                    ImagePopup imagePopup = new ImagePopup(context);
                    imagePopup.initiatePopup(d);
                    imagePopup.viewPopup();
                }
                else{
                    Log.w("File path error", "image not found in folder");
                }
            }
        }
    }

    public class MySecondClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
                String path = Environment.getExternalStorageDirectory().toString() + "/EmotionTracker_Audios";
                try {
                    if (mp.isPlaying()) {
                        mp.pause();
                        mp.stop();
                        mp.reset();
                    }
                    try {
                        mp.setDataSource(path + File.separator + popup_file_name_view.getText());
                    }catch (java.lang.IllegalStateException err){
                        err.printStackTrace();
                        mp.pause();
                        mp.stop();
                        mp.reset();
                        mp.setDataSource(path + File.separator + popup_file_name_view.getText());
                    }
                    mp.prepare();
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                        @Override
                        public void onPrepared(MediaPlayer mp){
                            mp.start();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
