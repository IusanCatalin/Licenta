package com.example.emotiontracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.projects.alshell.vokaturi.EmotionProbabilities;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

public class SpeechEmotion {

    private Vokaturi vokaturiApi;
    private Context appContext;
    private EmotionProbabilities emotionProbabilities;
    private double neutrality;
    private double happiness;
    private double sadness;
    private double anger;
    private double fear;
    private File file;

    SpeechEmotion(Context context)
    {
        appContext = context;
        try {
            vokaturiApi = Vokaturi.getInstance(appContext);
        } catch (VokaturiException e) {
            e.printStackTrace();
            Log.w("EROARE_VOKATURI_API" , "EROARE VOKATURI INSTANCE");
        }
        try{
            vokaturiApi.startListeningForSpeech();
        }catch (VokaturiException | java.lang.IllegalStateException e){
            e.printStackTrace();
            Toast.makeText(appContext, "Already recording", Toast.LENGTH_SHORT).show();
            Log.w("EROARE_VOKATURI_API" , "EROARE VOKATURI LISTENING");
        }
    }

    public void stopListening()
    {
        try{
            emotionProbabilities = vokaturiApi.stopListeningAndAnalyze();
            file = vokaturiApi.getRecordedAudio();
        }catch (VokaturiException e){
            Log.w("EROARE_VOKATURI_API" , "EROARE VOKATURI STOPPING AND GETTING PROBABILITIES");
            Toast.makeText(RecordActivity.context, "Try to speak louder, audio quality was too bad", Toast.LENGTH_LONG).show();
        }catch( java.lang.IllegalStateException e){
            Toast.makeText(RecordActivity.context, "The audio was already analysed!", Toast.LENGTH_LONG).show();
        }
        try{
            emotionProbabilities.scaledValues(3);
            Log.w("SCORE_VOKATURI","Neutrality: " + emotionProbabilities.Neutrality);
            Log.w("SCORE_VOKATURI","Happiness: " + emotionProbabilities.Happiness);
            Log.w("SCORE_VOKATURI","Sadness: " + emotionProbabilities.Sadness);
            Log.w("SCORE_VOKATURI","Anger: " + emotionProbabilities.Anger);
            Log.w("SCORE_VOKATURI","Fear: " + emotionProbabilities.Fear);
            neutrality = emotionProbabilities.Neutrality;
            happiness = emotionProbabilities.Happiness;
            sadness = emotionProbabilities.Sadness;
            anger = emotionProbabilities.Anger;
            fear = emotionProbabilities.Fear;
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.w("EROARE VOKATURI", "NU AU FOST GASITE DATE(NULL)");
        }
    }

    public String[] getTopEmotions()
    {
        String[] top3 = {"", "", ""};
        double[] top = {anger, neutrality, fear, happiness, sadness};
        Arrays.sort(top);
        if (top[top.length - 1] == 0) {
            top3[0] = "No results found, audio unclear or low quality";
        } else{
            //first
            if (anger == top[top.length - 1]) {
                top3[0] = "anger";
            } else if (neutrality == top[top.length - 1]) {
                top3[0] = "neutrality";
            } else if (fear == top[top.length - 1]) {
                top3[0] = "fear";
            } else if (happiness == top[top.length - 1]) {
                top3[0] = "happiness";
            } else if (sadness == top[top.length - 1]) {
                top3[0] = "sadness";
            }
            //second
            if (anger == top[top.length - 2]) {
                top3[1] = "anger";
            } else if (neutrality == top[top.length - 2]) {
                top3[1] = "neutrality";
            } else if (fear == top[top.length - 2]) {
                top3[1] = "fear";
            } else if (happiness == top[top.length - 2]) {
                top3[1] = "happiness";
            } else if (sadness == top[top.length - 2]) {
                top3[1] = "sadness";
            }
            //third
            if (anger == top[top.length - 3]) {
                top3[2] = "anger";
            } else if (neutrality == top[top.length - 3]) {
                top3[2] = "neutrality";
            } else if (fear == top[top.length - 3]) {
                top3[2] = "fear";
            } else if (happiness == top[top.length - 3]) {
                top3[2] = "happiness";
            } else if (sadness == top[top.length - 3]) {
                top3[2] = "sadness";
            }
        }
        return top3;
    }

    public void saveAudioFile(String file_counter) throws FileNotFoundException {

        File f = new File(Environment.getExternalStorageDirectory() + "/EmotionTracker_Audios");
        File audioDirectory = new File(Environment.getExternalStorageDirectory()+"/EmotionTracker_Audios");
        if(f.isDirectory()) {
            File outputFile = new File(audioDirectory, "vokaturi_recording_"+file_counter+".mp3");
            Log.w("File CREATED:", "ADDED");
            Log.w("File CREATED:", "ADDED");
            Log.w("File CREATED:", "ADDED");
            FileOutputStream fos = new FileOutputStream(outputFile);
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                int bufferSize;
                byte[] buffer = new byte[512];
                while ((bufferSize = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferSize);
                }
                inputStream.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            audioDirectory.mkdirs();
            File outputFile = new File(audioDirectory, "vokaturi_recording_"+file_counter+".mp3");
            Log.w("File CREATED:", "ADDED");
            Log.w("File CREATED:", "ADDED");
            Log.w("File CREATED:", "ADDED");
            FileOutputStream fos = new FileOutputStream(outputFile);
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                int bufferSize;
                byte[] buffer = new byte[512];
                while ((bufferSize = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferSize);
                }
                inputStream.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
