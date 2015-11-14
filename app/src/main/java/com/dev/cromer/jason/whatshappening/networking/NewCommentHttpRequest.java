package com.dev.cromer.jason.whatshappening.networking;


import android.os.AsyncTask;
import android.util.Log;

import com.dev.cromer.jason.whatshappening.objects.MarkerCommentParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewCommentHttpRequest extends AsyncTask<MarkerCommentParams, Void, Void> {

    static HttpURLConnection httpURLConnection;
    static String data;
    static OutputStream outputStream;
    static OutputStreamWriter outputStreamWriter;
    static BufferedWriter bufferedWriter;
    static InputStreamReader inputStreamReader;
    static BufferedReader bufferedReader;
    static StringBuilder stringBuilder;
    static String inputLine;

    //constants
    private static final String REQUEST_METHOD = "POST";
    private static final String REQUEST_PROPERTY_JSON = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";

    @Override
    protected Void doInBackground(MarkerCommentParams... params) {

        //Get passed in arguments from params object (which is a list by default)
        String comment = params[0].getCommentString();
        String httpURL = params[0].getUrl();

        Log.d("TAG IN HTTP REQUEST...", comment);
        try{
            //Connect
            httpURLConnection = (HttpURLConnection) (new URL(httpURL).openConnection());
            httpURLConnection.setDoOutput(true);

            //Set headers
            httpURLConnection.setRequestProperty("Content-Type", REQUEST_PROPERTY_JSON);
            httpURLConnection.setRequestProperty("Accept", REQUEST_PROPERTY_JSON);
            httpURLConnection.setRequestMethod(REQUEST_METHOD);
            httpURLConnection.connect();

            //Create new JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("comment", comment);
            data = jsonObject.toString();

            //Write data to output stream
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream, CHARACTER_ENCODING);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(data);
            bufferedWriter.close();
            outputStreamWriter.close();
            outputStream.close();

            //Read incoming response
            inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), CHARACTER_ENCODING);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuilder = new StringBuilder();

            while((inputLine = bufferedReader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            bufferedReader.close();
            inputStreamReader.close();
        }
        catch(IOException | JSONException e){
            e.printStackTrace();
        }

        return null;
    }
}
