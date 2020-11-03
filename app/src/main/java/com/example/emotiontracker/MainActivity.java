package com.example.emotiontracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public TextView resultText;
    private ImageView imageView;
    private String[] topEmotionsImage;
    private Context context;
    Bitmap bitmapFrontCam;
    private final static int CAMERA_PIC_REQUEST = 300;
    private final static int REQUEST_CAMERA_PERMISSION = 400;
    private final static int REQUEST_READ_EXTERNAL_STORAGE = 500;
    private final static int REQUEST_WRITE_EXTERNAL_STORAGE = 600;
    private final static int REQUEST_INTERNET = 700;
    private final static int RECORD_AUDIO = 800;

    //private static final int RESULT_LOAD_IMAGE = 100;   // variables for pick image from gallery test
    //private static final int REQUEST_PERMISSION_CODE = 200;
    //private Bitmap final_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultText = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        context = this;
        checkPermissions();
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
            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    public void getEmotion(View view) {
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
                        resultText.setText(text_to_display);
                    }
                },
                10000 // 1k = 1 sec, delay for getting the image processed
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
}
