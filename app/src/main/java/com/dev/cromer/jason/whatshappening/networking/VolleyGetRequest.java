package com.dev.cromer.jason.whatshappening.networking;


import android.content.Context;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * This class performs a simple GET request, and uses a callback interface, named VolleyCallback
 * to have the implementing class receive the response from the server.
 **/

public class VolleyGetRequest implements Response.Listener<String>, Response.ErrorListener {

    //Application Context to persist RequestQueue and perform Toast messages
    private Context applicationContext;

    //String request object
    private StringRequest stringRequest;

    //RequestQueue object to add our requests to
    private RequestQueue requestQueue;

    //Our interface callback Object
    private VolleyCallback callback;

    //A TAG for our String request object
    private static final String STRING_REQUEST_TAG = "GetRequest";


    /*
        Constructor
        @param Context
     */
    public VolleyGetRequest(Context appContext){
        this.applicationContext = appContext;
    }


    //This method creates the request string, and the request queue
    private void createStringRequestObject(String url){
        stringRequest = new StringRequest(Request.Method.GET, url, this, this);
        stringRequest.setTag(STRING_REQUEST_TAG);
        requestQueue = Volley.newRequestQueue(applicationContext);
    }


    //This method makes the actual http request by queueing the string request
    public void makeRequest(VolleyCallback callback, String url){
        this.callback = callback;
        createStringRequestObject(url);

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


    //This method stops any of the requests in queue (typically called in onStop())
    public void cancelVolleyRequests(){
        if(requestQueue != null){
            requestQueue.cancelAll(STRING_REQUEST_TAG);
        }
    }

}
