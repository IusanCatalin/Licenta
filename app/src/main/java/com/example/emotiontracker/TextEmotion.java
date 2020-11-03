package com.example.emotiontracker;
import android.os.AsyncTask;
import android.text.PrecomputedText;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.InputStreamEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class TextEmotion extends AsyncTask<String, String, String> {

    float score_value;

    @Override
    protected String doInBackground(String... strings) {
        String text= strings[0];
        String json_response = "";
        String url = "https://westeurope.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";
        String final_url = url; //if url needs something more add here
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost request = new HttpPost(final_url);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Ocp-Apim-Subscription-Key", "f8defc68f5094442913691b2b3d25fe4");
        try {
            StringEntity reqEntity = new StringEntity("{\"documents\": [{\"language\": \"en\",\"id\": \"1\",\"text\": \""+text+"\"}]}");
            request.setEntity(reqEntity);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            Log.w("EROARE REQUEST", "ENCODING GRESIT");
        }
        try {
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String jsonString = EntityUtils.toString(entity).trim();
                if (jsonString.charAt(0) == '[') {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    json_response = json_response + (jsonArray.toString(2));
                } else if (jsonString.charAt(0) == '{') {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    json_response = json_response + (jsonObject.toString(2));
                } else {
                    json_response = json_response + (jsonString);
                }
            }
            String score_string = jsonTransform(json_response);
            score_value = Float.parseFloat(score_string);
        }catch(IOException | JSONException e){
                Log.w("EROARE", "EROARE HTTPS");
                e.printStackTrace();
        }
        return null;
    }

    private String jsonTransform(java.lang.String json) throws JSONException {
        Log.w("JSON STRING IS:", json);
        try {
            JSONObject reader = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));
            JSONArray documents = reader.getJSONArray("documents");
            JSONObject first = documents.getJSONObject(0);
            String score = first.getString("score");
            Log.w("EMOTION SCORE IS:", score);
            return score;
        } catch (java.lang.StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected float getEmotionScore()
    {
        return score_value;
    }

    @Override
    protected void onPreExecute() {
        //to do if needed
    }

    @Override
    protected void onPostExecute(String result) {
        //to do if needed

    }
}
