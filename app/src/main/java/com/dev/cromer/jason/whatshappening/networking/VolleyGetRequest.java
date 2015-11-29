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


    /*
        Constructor
        @param Context
        @param String
     */
    public VolleyGetRequest(Context appContext, String url){
        this.applicationContext = appContext;
        this.stringURL = url;
    }


    //This method creates the request string, and the request queue
    private void createStringRequestObject(){
        stringRequest = new StringRequest(Request.Method.GET, stringURL, this, this);
        requestQueue = Volley.newRequestQueue(applicationContext);
    }


    //This method makes the actual http request by queueing the string request
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
        //callback to receive response in a class that implements VolleyCallback
        callback.onSuccess(response);
    }


    //An interface for calling the onSuccess method
    public interface VolleyCallback{
        void onSuccess(String result);
    }
}
