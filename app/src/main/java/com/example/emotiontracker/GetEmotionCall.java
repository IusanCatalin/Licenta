package com.example.emotiontracker;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.InputStreamEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import com.microsoft.projectoxford.face.contract.Face;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class GetEmotionCall extends AsyncTask<InputStream, String, Face[]> {
    private String[] top_emotions;
    private float anger = 0;
    private float contempt = 0;
    private float disgust = 0;
    private float fear = 0;
    private float happiness = 0;
    private float neutral = 0;
    private float sadness = 0;
    private float surprise = 0;

    @SuppressWarnings("deprecation")
    protected Face[] doInBackground(InputStream... params)
    {
        String json_response="";
        String url = "https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect";
        String requestParameters = "returnFaceId=true&returnFaceLandmarks=false" +
                "&returnFaceAttributes=age,gender," +
                "emotion";
        String final_url = url + "?" + requestParameters;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost request = new HttpPost(final_url);
        request.setHeader("Content-Type", "application/octet-stream");
        request.setHeader("Ocp-Apim-Subscription-Key", "0d70173169ab4f259e8b74d4cdc82b5d");
        InputStreamEntity reqEntity = new InputStreamEntity(params[0], -1);
        reqEntity.setContentType("image/jpeg");
        reqEntity.setChunked(true);
        request.setEntity(reqEntity);
        try {
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                String jsonString = EntityUtils.toString(entity).trim();
                if (jsonString.charAt(0) == '[') {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    json_response = json_response+(jsonArray.toString(2));
                }
                else if (jsonString.charAt(0) == '{') {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    json_response = json_response+(jsonObject.toString(2));
                } else {
                    json_response = json_response+(jsonString);
                }
            }
            JSONObject json_emotion = jsonTransform(json_response);
            getEmotions(json_emotion);
            top_emotions = getTopEmotions();
            Log.w("RESULT", "RESULTS");
            Log.w("ANGER", String.valueOf(anger));
            Log.w("CONTEMPT", String.valueOf(contempt));
            Log.w("DISGUST", String.valueOf(disgust));
            Log.w("FEAR", String.valueOf(fear));
            Log.w("HAPPINESS", String.valueOf(happiness));
            Log.w("NEUTRAL", String.valueOf(neutral));
            Log.w("SADNESS", String.valueOf(sadness));
            Log.w("SURPRISE", String.valueOf(surprise));
        } catch (IOException | JSONException e) {
            Log.w("EROARE" , "EROARE HTTPS");
            e.printStackTrace();
        }
        return new Face[0]; // returns practically nothing
    }

    @Override
    protected void onPreExecute() {
        //to do if needed
    }

    @Override
    protected void onPostExecute(Face[] result) {
        //to do if needed
    }

    protected String[] getResult()
    {
        return top_emotions;
    }

    private JSONObject jsonTransform(java.lang.String json) throws JSONException {
        Log.w("JSON STRING IS:", json);
        try {
            JSONObject reader = new JSONObject(json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1));

            JSONObject face = reader.getJSONObject("faceAttributes");
            JSONObject emotion = face.getJSONObject("emotion");
            return emotion;
        } catch (java.lang.StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getEmotions(JSONObject emotion) throws JSONException {
        try {
            anger = Float.parseFloat(emotion.getString("anger"));
            contempt = Float.parseFloat(emotion.getString("contempt"));
            disgust = Float.parseFloat(emotion.getString("disgust"));
            fear = Float.parseFloat(emotion.getString("fear"));
            happiness = Float.parseFloat(emotion.getString("happiness"));
            neutral = Float.parseFloat(emotion.getString("neutral"));
            sadness = Float.parseFloat(emotion.getString("sadness"));
            surprise = Float.parseFloat(emotion.getString("surprise"));
        }catch (java.lang.NullPointerException e){
            e.printStackTrace();
        }
    }

    private String[] getTopEmotions() {
        String[] top3 = {"", "", ""};
        float[] top = {anger, contempt, disgust, fear, happiness, neutral, sadness, surprise};
        Arrays.sort(top);
        if (top[top.length - 1] == 0) {
            {
                Log.w("No result", "image unclear");
                top3[0] = "No results found, image unclear or low quality";
            }
        } else {
            //first
            if (anger == top[top.length - 1]) {
                top3[0] = "anger";
            } else if (contempt == top[top.length - 1]) {
                top3[0] = "contempt";
            } else if (disgust == top[top.length - 1]) {
                top3[0] = "disgust";
            } else if (fear == top[top.length - 1]) {
                top3[0] = "fear";
            } else if (happiness == top[top.length - 1]) {
                top3[0] = "happiness";
            } else if (neutral == top[top.length - 1]) {
                top3[0] = "neutral";
            } else if (sadness == top[top.length - 1]) {
                top3[0] = "sadness";
            } else if (surprise == top[top.length - 1]) {
                top3[0] = "surprise";
            }
            //second
            if (anger == top[top.length - 2]) {
                top3[1] = "anger";
            } else if (contempt == top[top.length - 2]) {
                top3[1] = "contempt";
            } else if (disgust == top[top.length - 2]) {
                top3[1] = "disgust";
            } else if (fear == top[top.length - 2]) {
                top3[1] = "fear";
            } else if (happiness == top[top.length - 2]) {
                top3[1] = "happiness";
            } else if (neutral == top[top.length - 2]) {
                top3[1] = "neutral";
            } else if (sadness == top[top.length - 2]) {
                top3[1] = "sadness";
            } else if (surprise == top[top.length - 2]) {
                top3[1] = "surprise";
            }
            //third
            if (anger == top[top.length - 3]) {
                top3[2] = "anger";
            } else if (contempt == top[top.length - 3]) {
                top3[2] = "contempt";
            } else if (disgust == top[top.length - 3]) {
                top3[2] = "disgust";
            } else if (fear == top[top.length - 3]) {
                top3[2] = "fear";
            } else if (happiness == top[top.length - 3]) {
                top3[2] = "happiness";
            } else if (neutral == top[top.length - 3]) {
                top3[2] = "neutral";
            } else if (sadness == top[top.length - 3]) {
                top3[2] = "sadness";
            } else if (surprise == top[top.length - 3]) {
                top3[2] = "surprise";
            }
        }
        return top3;
    }
}
