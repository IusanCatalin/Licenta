package com.example.emotiontracker;

import android.content.Context;
import android.util.Log;

import com.projects.alshell.vokaturi.EmotionProbabilities;
import com.projects.alshell.vokaturi.Vokaturi;
import com.projects.alshell.vokaturi.VokaturiException;

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
        }catch (VokaturiException e){
            e.printStackTrace();
            Log.w("EROARE_VOKATURI_API" , "EROARE VOKATURI LISTENING");
        }
    }

    public void stopListening()
    {
        try{
            emotionProbabilities = vokaturiApi.stopListeningAndAnalyze();
        }catch (VokaturiException e){
            Log.w("EROARE_VOKATURI_API" , "EROARE VOKATURI STOPPING AND GETTING PROBABILITIES");
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
}
