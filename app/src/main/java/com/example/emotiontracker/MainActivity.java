package com.example.emotiontracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.android.material.navigation.NavigationView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected androidx.appcompat.widget.Toolbar toolbar;
    protected static ArrayList<String> events = new ArrayList<String>();
    protected static ArrayList<DatabaseItemEvents> events_database = new ArrayList<DatabaseItemEvents>();
    private TextView welcome_text;
    private TextView general_score;
    private TextView popup_file_name_view;
    private ImageView imageView;
    private Button take_picture;
    private Button record;
    private Button write_text;
    private Button calculate_happy;
    private String Username;
    private String image_counter;
    private String[] topEmotionsImage;
    private Bitmap bitmapFrontCam;
    private Double general_happinessScore = 0.0;
    private Double image_happinessScore = 0.0;
    private int memory_case;
    private boolean user_took_photo = false;
    private boolean all_permissions = true;
    private Database mDatabase;
    private Database database;
    private Events_ranking events_ranking = new Events_ranking();
    private final static int CAMERA_PIC_REQUEST = 300;
    private final static int REQUEST_CAMERA_PERMISSION = 400;
    private final static int REQUEST_READ_EXTERNAL_STORAGE = 500;
    private final static int REQUEST_WRITE_EXTERNAL_STORAGE = 600;
    private final static int REQUEST_INTERNET = 700;
    private final static int RECORD_AUDIO = 800;
    private final static int READ_CALENDAR = 900;
    private final static int WRITE_CALENDAR = 901;
    private final static int ALL_PERMISSIONS = 1;
    private MediaPlayer mp = new MediaPlayer();
    private ActionBarDrawerToggle toggle;
    public static String[] happy_texts;
    public static Context context;
    private String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };


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
        toolbar = findViewById(R.id.toolbar_front_camera);
        take_picture = findViewById(R.id.take_photo);
        record = findViewById(R.id.record);
        write_text = findViewById(R.id.btnText);
        calculate_happy = findViewById(R.id.calculate_happiness);
        context = this;
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSIONS);
        }
        checkUserName();
        mDatabase = new Database(context);
        happy_texts = readFromFile(this).split(System.getProperty("line.separator"));
        for(int i = 0; i < happy_texts.length; i++){
            Log.w("Line is:", happy_texts[i]);
        }
        initToolbar();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        dailyEventsRankings();
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
                if (imageView.getDrawable() != null) {
                    getEmotion();
                } else {
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
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            bitmapFrontCam.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (java.lang.NullPointerException e) {
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
                        String text_to_display = "";
                        try {
                            for (String emotion : topEmotionsImage) {
                                text_to_display = text_to_display + emotion + " ";
                            }
                        } catch (NullPointerException e) {
                            Log.w("ERAORE CITIT EMOTIONS", "NU A RETURNAT NICI UN STRING, PROBABIL EROARE DE KEY/SERVER");
                            Log.w("EROARE CITIT", e.toString());
                        }
                        try {
                            if (topEmotionsImage[0] == "No results found, image unclear or low quality") {
                                Log.w("IMAGINE", "IMAGINE NECLARA");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.context, "Image unclear or low quality, try again!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                setImage_happinessScore(topEmotionsImage);
                            }
                        }catch (java.lang.NullPointerException e){
                            Log.w("EROARE", e.toString());
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.context, "Server time-out, your internet may be too slow", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                },
                3000 // 1k = 1 sec, delay for getting the image processed, 2k was getting errors on slow internet, the slower the internet=bigger delay needed
        );
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Log.w("trying to save" , "img");
                        if (image_happinessScore > 0.70) //all images for the moment, testing, change 0.70 in real application
                        {
                            setPreferences();
                            saveImg(image_counter);
                            Log.w("FILE", "FILE CREATED");
                        }

                    }
                }, 4000
        );
    }

    public void onClickTakePicture(View view) {
        take_picture.setAlpha(0f);
        take_picture.animate().alpha(1f).setDuration(1500);
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

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void recordSpeechActivity(View view) {
        record.setAlpha(0f);
        record.animate().alpha(1f).setDuration(1500);
        Intent speechIntent = new Intent(MainActivity.this, RecordActivity.class);
        MainActivity.this.startActivity(speechIntent);
    }

    public void writeTextActivity(View view) {
        write_text.setAlpha(0f);
        write_text.animate().alpha(1f).setDuration(1500);
        Intent textIntent = new Intent(MainActivity.this, TextActivity.class);
        MainActivity.this.startActivity(textIntent);
    }

    public void historyActivity() {
        Intent historyIntent = new Intent(MainActivity.this, HistoryActivity.class);
        MainActivity.this.startActivity(historyIntent);
    }

    private void userNameAlert(Context context) {
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
                welcome_text.setText("Welcome " + user_name + ", please use at least one of the analyse methods below!");
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

    private void checkUserName() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_name = preferences.getString("user_name", "");
        Username = user_name;
        if (user_name == "")
            userNameAlert(context);

        welcome_text.setText("Welcome " + user_name + ", please use at least one of the analyse methods below!");
    }

    private void setImage_happinessScore(String[] topEmotions) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.context, "Successfully added photo", Toast.LENGTH_LONG).show();
            }
        });
        take_picture.getBackground().setColorFilter(0xff888888, PorterDuff.Mode.MULTIPLY);
        user_took_photo = true;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                if (topEmotions[i] == "anger")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "contempt")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "disgust")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "fear")
                    image_happinessScore = image_happinessScore + 0;
                else if (topEmotions[i] == "happiness") {
                    image_happinessScore = image_happinessScore + 1;
                    break;
                }
                else if (topEmotions[i] == "neutral")
                    image_happinessScore = image_happinessScore + 0.5;
                else if (topEmotions[i] == "sadness")
                    image_happinessScore = image_happinessScore + 0;
                else
                    image_happinessScore = image_happinessScore + 0.5;
            } else if (i == 1) {
                if (topEmotions[i] == "anger")
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
            } else {
                if (topEmotions[i] == "anger")
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
        calculate_happy.setAlpha(0f);
        calculate_happy.animate().alpha(1f).setDuration(1500);
        double d = 0.0;
        Log.w("HAPPINESS SCORE IMAGE", image_happinessScore.toString());
        Log.w("HAPPINESS SCORE SPEECH", Double.toString(RecordActivity.speech_happinessScore));
        Log.w("HAPPINESS SCORE TEXT", Double.toString(TextActivity.score_emotion_text));
        general_happinessScore = image_happinessScore + RecordActivity.speech_happinessScore + TextActivity.score_emotion_text;
        if (user_took_photo == true) {
            d = d + 1;
        }
        if (RecordActivity.user_recorded_audio == true) {
            d = d + 1;
        }
        if (TextActivity.user_used_text == true) {
            d = d + 1;
        }
        general_happinessScore = general_happinessScore / d;
        general_happinessScore = general_happinessScore * 100;
        int final_value = general_happinessScore.intValue();
        //general_score.setText(String.valueOf(final_value));
        ValueAnimator animator = ValueAnimator.ofInt(0, final_value);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                general_score.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
        mDatabase.createEntry(final_value);
        //reset values
        image_happinessScore = 0.0;
        RecordActivity.speech_happinessScore = 0.0;
        TextActivity.score_emotion_text = 0.0;
        general_happinessScore = 0.0;
        //show happy memory in case of low happiness score
        if(final_value < 40)
            show_happy_memory(view);
        //reset all buttons
        take_picture.getBackground().clearColorFilter();
        record.getBackground().clearColorFilter();
        write_text.getBackground().clearColorFilter();
        user_took_photo = false;
        RecordActivity.user_recorded_audio=false;
        TextActivity.user_used_text=false;
    }

    private String readFromFile(Context context) {
        String text = "";
        File texts = new File("/data/data/com.example.emotiontracker/files/happy_texts.txt");
        try {
            InputStream inputStream = context.openFileInput("happy_texts.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    if (texts.length() != 0) {
                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append("\n").append(receiveString);
                        }
                    }
                }catch (java.lang.NullPointerException file_exc){
                    file_exc.printStackTrace();
                    Log.w("text memories", "is empty");
                }
                inputStream.close();
                text = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        Log.w("TEXTUL CITIT ESTE", text);
        return text;
    }

    private void saveImg(String file_counter) {
        Log.w("Saving img" , "saving");
        File f = new File(Environment.getExternalStorageDirectory() + "/EmotionTracker_Images");
        if (f.isDirectory()) {
            File outputFile = new File(f, "vokaturi_image_" + file_counter + ".png");
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                bitmapFrontCam.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            f.mkdirs();
            File outputFile = new File(f, "vokaturi_image_" + file_counter + ".png");
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                bitmapFrontCam.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setPreferences() {
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.image_file_counter), Context.MODE_PRIVATE);
        String defaultValue = "0";
        String counter = sharedPref.getString(getString(R.string.image_file_counter), defaultValue);
        image_counter = counter;
        SharedPreferences.Editor editor = sharedPref.edit();
        counter = String.valueOf(Integer.parseInt(counter) + 1);
        editor.putString(getString(R.string.image_file_counter), counter);
        editor.apply();
    }

    public void show_happy_memory(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_main_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mp.isPlaying()) {
                    mp.pause();
                    mp.stop();
                    mp.reset();
                    Log.w("OPRIRE", "OPRIRE DIN TAP");
                }
                popupWindow.dismiss();
                return true;
            }
        });
        popup_file_name_view = popupWindow.getContentView().findViewById(R.id.view_file_name);
        popup_file_name_view.setText(select_random_memory());
        if(popup_file_name_view.getText() == "no name")
            popupWindow.dismiss();
        if (memory_case == 1 || memory_case == 2) {
            MyClickListener lstn = new MyClickListener();
            popup_file_name_view.setOnClickListener(lstn);
        }
    }

    private String select_random_memory() {
        String selected_file = "no name";
        String path;
        File directory;
        File[] files;
        Random rand = new Random();
        int random = rand.nextInt(3);
        switch (random) {
            case 0:
                //text memories
                memory_case = 0;
                int array_size = happy_texts.length;
                if(array_size != 0) {
                    int random_text = rand.nextInt(array_size);
                    String selected_memory_text = happy_texts[random_text];
                    selected_file = selected_memory_text;
                    break;
                }
            case 1:
                //audio memories
                memory_case = 1;
                ArrayList<String> audio_files = new ArrayList<String>();
                path = Environment.getExternalStorageDirectory().toString() + "/EmotionTracker_Audios";
                directory = new File(path);
                files = directory.listFiles();
                try {
                    for (int i = 0; i < files.length; i++) {
                        audio_files.add(files[i].getName());
                    }
                    int random_audio = rand.nextInt(audio_files.size());
                    String selected_memory_audio = audio_files.get(random_audio);
                    selected_file = selected_memory_audio;
                    break;
                } catch (java.lang.NullPointerException e) {
                    e.printStackTrace();
                    Log.w("no happy", "memories to show");
                }
            case 2:
                //image memories
                memory_case = 2;
                ArrayList<String> image_files = new ArrayList<String>();
                path = Environment.getExternalStorageDirectory().toString() + "/EmotionTracker_Images";
                directory = new File(path);
                files = directory.listFiles();
                try {
                    for (int i = 0; i < files.length; i++) {
                        image_files.add(files[i].getName());
                    }
                    int random_image = rand.nextInt(image_files.size());
                    String selected_memory_image = image_files.get(random_image);
                    selected_file = selected_memory_image;
                    break;
                }catch(java.lang.NullPointerException e){
                    e.printStackTrace();
                    Log.w("sad state", "nothing to show");
                }
        }
        return selected_file;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.item_one)
            historyActivity();
        else if (menuItem.getItemId() == R.id.item_two)
            text_memories();
        else if (menuItem.getItemId() == R.id.item_three)
            audio_memories();
        else if (menuItem.getItemId() == R.id.item_four)
            image_memories();
        else if (menuItem.getItemId() == R.id.add_event)
            add_new_event();
        else if (menuItem.getItemId() == R.id.events_rank)
            ranking_events();
        else{
            view_day_activities();
        }
        return false;
    }

    private void view_day_activities() {
        Calendar calendar = Calendar.getInstance();
        long startMillis = calendar.getTimeInMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(builder.build());
        startActivity(intent);
    }

    public void show_help(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.help_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mp.isPlaying()) {
                    mp.pause();
                    mp.stop();
                    mp.reset();
                    Log.w("OPRIRE", "OPRIRE DIN TAP");
                }
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public class MyClickListener implements View.OnClickListener {
        private MySecondClickListener mSecondListener = new MySecondClickListener();

        public void onClick(View v) {
            if (memory_case == 1) {
                mSecondListener.onClick(v);
            } else {
                File imgFile = new File(Environment.getExternalStorageDirectory().toString() + "/EmotionTracker_Images/" + popup_file_name_view.getText());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Drawable d = new BitmapDrawable(getResources(), myBitmap);
                    ImagePopup imagePopup = new ImagePopup(context);
                    imagePopup.initiatePopup(d);
                    imagePopup.viewPopup();
                } else {
                    Log.w("File path error", "image not found in folder");
                }
            }
        }
    }

    public class MySecondClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String path = Environment.getExternalStorageDirectory().toString() + "/EmotionTracker_Audios";
            try {
                if (mp.isPlaying()) {
                    mp.pause();
                    mp.stop();
                    mp.reset();
                    Log.w("OPRIRE", "OPRIRE RESET");
                }
                try {
                    mp.setDataSource(path + File.separator + popup_file_name_view.getText());
                } catch (java.lang.IllegalStateException err) {
                    err.printStackTrace();
                    mp.pause();
                    mp.stop();
                    mp.reset();
                    mp.setDataSource(path + File.separator + popup_file_name_view.getText());
                }
                mp.prepare();
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(){
        //Toolbar toolbar = findViewById(R.id.toolbar_main);
        //setSupportActionBar(toolbar);
        View drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, (DrawerLayout) drawer, toolbar, R.string.openDrawer, R.string.closeDrawer);
        ((DrawerLayout) drawer).addDrawerListener(toggle);
        getSupportActionBar().setTitle("Emotion Tracker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public void text_memories() {
        Intent text_intent = new Intent (MainActivity.this, TextMemories.class);
        MainActivity.this.startActivity(text_intent);
    }

    public void audio_memories() {
        Intent audio_intent = new Intent (MainActivity.this, AudioMemories.class);
        MainActivity.this.startActivity(audio_intent);
    }

    public void image_memories() {
        Intent image_intent = new Intent (MainActivity.this, ImageMemories.class);
        MainActivity.this.startActivity(image_intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(RecordActivity.user_recorded_audio){
            record.getBackground().setColorFilter(0xff888888, PorterDuff.Mode.MULTIPLY);
        }
        if(TextActivity.user_used_text){
            write_text.getBackground().setColorFilter(0xff888888, PorterDuff.Mode.MULTIPLY);
        }
    }

    private void add_new_event(){
        Intent intent_add_event = new Intent(Intent.ACTION_INSERT);
        intent_add_event.setData(CalendarContract.Events.CONTENT_URI);
        if(intent_add_event.resolveActivity(getPackageManager()) != null)
            startActivity(intent_add_event);
        else{
            Toast.makeText(context, "Google Calendar app not found" , Toast.LENGTH_LONG).show();

        }
    }

    public void ranking_events() {
        Intent ranking_intent = new Intent (MainActivity.this, Events_ranking.class);
        MainActivity.this.startActivity(ranking_intent);
    }

    private void dailyEventsRankings(){
        getDatabaseEvents(); // we still need it once to get the events list
        //wierd bug at times number
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        String actual_date = day + "/" + month + "/" + year;
        Context context = MainActivity.this;
        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.date), Context.MODE_PRIVATE);
        String defaultValue = "0";
        String last_date = sharedPref.getString(getString(R.string.date), defaultValue);
        Log.w("last date:", last_date);
        Log.w("actual date", actual_date);
        if(!actual_date.equals(last_date)){
            Log.w("dates different", "updating event ranks");
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.date), actual_date);
            editor.apply();
            getEvents();
            getDatabaseEvents();
            checkForExistingEvents();
            addNewEvents();
            getDatabaseEvents2(); // second time in case of new entries
        }
    }

    protected void getEvents() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Log.w("Date -1", String.valueOf(day));
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Calendar start = Calendar.getInstance();
        start.set(year, month, day, 0, 1);
        long startMillis = start.getTimeInMillis();
        Log.w("Start time", String.valueOf(startMillis));
        Log.w("daystart", String.valueOf(start.get(Calendar.DAY_OF_MONTH)));
        Log.w("daystart", String.valueOf(start.get(Calendar.MONTH)));
        Log.w("daystart", String.valueOf(start.get(Calendar.YEAR)));
        Calendar end = Calendar.getInstance();
        end.set(year, month, day, 23, 59);
        long endMillis = end.getTimeInMillis();
        Log.w("End time", String.valueOf(endMillis));
        Log.w("dayend", String.valueOf(end.get(Calendar.DAY_OF_MONTH)));
        Log.w("dayend", String.valueOf(end.get(Calendar.MONTH)));
        Log.w("dayend", String.valueOf(end.get(Calendar.YEAR)));
        Cursor cur = null;
        ContentResolver cr = this.getContentResolver();
        try {
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            String[] INSTANCE_PROJECTION = new String[]{CalendarContract.Instances.EVENT_ID,
                    CalendarContract.Instances.TITLE, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END};

            Uri uri = builder.build();
            cur = cr.query(uri, INSTANCE_PROJECTION, null, null, null);

            if (cur != null) {
                while (cur.moveToNext()) {
                    String title = cur.getString(cur.getColumnIndex(CalendarContract.Instances.TITLE));
                    Log.w("eveniment", title);
                    events.add(title);
                }
            }
        } catch (SecurityException e) {
            // no permission to read calendars
        } finally {
            if (cur != null)
                cur.close();
        }
        try{
            Log.w("Calendar events are:" , events.get(0));
        }catch (java.lang.IndexOutOfBoundsException e){
            Log.w("No new calendar events" , "No new events");
        }

    }

    protected void getDatabaseEvents() {
        database = new Database(this);
        events_database = database.readEventEntries();
        try {
            Log.w("events database:", events_database.get(events_database.size() - 1).title);
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            Log.w("events database" , "has 0 events");
        }
    }

    protected void getDatabaseEvents2() {
        database = new Database(this);
        events_database = database.readEventEntries();
        try {
            Log.w("2events database:", events_database.get(events_database.size() - 1).title);
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            Log.w("2events database" , "has 0 events");
        }
    }

    protected void addNewEvents(){
        int happy_score_prev_day = database.getPreviousDayEmotionScore();
        if(happy_score_prev_day == 0)
            happy_score_prev_day = 50; //we set it to 50 if the user didn't record his emotions in the previous day so it doesn't affect the activity score
        for( String e : events)
            database.createEntryEvents(e, happy_score_prev_day);
    }

    protected void checkForExistingEvents(){
        int happy_score_prev_day = database.getPreviousDayEmotionScore();
        if(happy_score_prev_day == 0)
            happy_score_prev_day = 50; //we set it to 50 if the user didn't record his emotions in the previous day so it doesn't affect the activity score
        for(String e : events)
            for(DatabaseItemEvents ed : events_database){
                if(e.equals(ed.title)){
                    database.edit_events(ed.title, (ed.score + happy_score_prev_day) / 2, ed.times+1); // not updating ok at same activity more than 1 per day
                }
            }
        getDatabaseEvents();
    }


}


