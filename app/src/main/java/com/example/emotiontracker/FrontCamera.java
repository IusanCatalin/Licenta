package com.example.emotiontracker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Matrix;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class FrontCamera extends AppCompatActivity {
    private Camera mCamera = null;
    private CameraPreview mPreview;
    Button captureButton;
    public static final int MEDIA_TYPE_IMAGE = 1;
    static Context con;
    Bitmap bitmap;
    Bitmap mainbitmap;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_camera);
        captureButton = (Button) findViewById(R.id.capture);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        con = getApplicationContext();
        try {
            mCamera = getCameraInstance();
        } catch (Exception e) {
            // to do if needed
            e.printStackTrace();
        }
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.gc();
            bitmap = null;
            BitmapWorkerTask task = new BitmapWorkerTask(data);
            task.execute(0);
        }
    };

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<byte[]> dataf;
        private int data = 0;
        public BitmapWorkerTask(byte[] imgdata) {
            dataf = new WeakReference<byte[]>(imgdata);
        }
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            ResultActivity(dataf.get());
            return mainbitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mainbitmap != null) {
                Intent i = new Intent();
                i.putExtra("BitmapImage", mainbitmap);
                setResult(-1, i);
                finish();
            }
        }
    }

    public void ResultActivity(byte[] data) {
        mainbitmap = null;
        mainbitmap = decodeSampledBitmapFromResource(data, 200, 200);
        mainbitmap=RotateBitmap(mainbitmap,270);
        mainbitmap=flip(mainbitmap);
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    @SuppressWarnings("deprecation")
    public static Camera getCameraInstance() {
        Camera c = null;
        Log.d("No of cameras", Camera.getNumberOfCameras() + "");
        Log.d("No of cameras", Camera.getNumberOfCameras() + "");
        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camNo, camInfo);

            if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                c = Camera.open(camNo);
                c.setDisplayOrientation(90);
            }
        }
        return c;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            setContentView(R.layout.front_camera);
            captureButton = (Button) findViewById(R.id.capture);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            con = getApplicationContext();
            try {
                mCamera = getCameraInstance();
            } catch (Exception e) {
                Log.w("CAMERA" , "EROARE CAMERA");
                Log.w("CAMERA" , "EROARE CAMERA");
                // to do if needed
                e.printStackTrace();
            }
            mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview);
        }
    }

    @SuppressWarnings("deprecation")
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    Bitmap flip(Bitmap d)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d;
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

}
