package com.dev.cromer.jason.whatshappening.networking;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * This class is a test to determine performance optimizations, clearer implementation, and
 * better management to respond to activity lifecycle changes.
 **/

public class VolleyGetRequest implements Response.Listener<String>, Response.ErrorListener {

    //Application Context to persist RequestQueue and perform Toast messages
    private Context applicationContext;

    //URL for requests
    private String stringURL;

    //String request object
    private StringRequest stringRequest;

    //RequestQueue object to add our requests to
    private RequestQueue requestQueue;

    //Our interface callback Object
    private VolleyCallback callback;


    public VolleyGetRequest(Context appContext, String url){
        this.applicationContext = appContext;
        this.stringURL = url;
    }

    private void createStringRequestObject(){
        stringRequest = new StringRequest(Request.Method.GET, stringURL, this, this);
        requestQueue = Volley.newRequestQueue(applicationContext);
    }

    public void makeRequest(VolleyCallback callback){
        this.callback = callback;
        createStringRequestObject();

        if(stringRequest != null){
            requestQueue.add(stringRequest);
        }
    }


    @Override
    public void onErrorResponse(VolleyError error){
        //Convert error to NetworkResponse to get details
        NetworkResponse errorResponse = error.networkResponse;
        String errorResponseString = "Sorry, we ran into some network issues";

        if(errorResponse != null && errorResponse.data != null){
            Toast.makeText(applicationContext, errorResponseString, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(String response){
        //Make sure our response is an integer greater or equal to 0
        callback.onSuccess(response);
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }
}
