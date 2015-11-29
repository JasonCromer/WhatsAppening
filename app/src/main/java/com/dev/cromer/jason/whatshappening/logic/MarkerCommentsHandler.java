package com.dev.cromer.jason.whatshappening.logic;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dev.cromer.jason.whatshappening.networking.NewCommentHttpRequest;
import com.dev.cromer.jason.whatshappening.networking.VolleyQueueSingleton;
import com.dev.cromer.jason.whatshappening.objects.MarkerCommentParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkerCommentsHandler {

    private Context applicationContext;
    private RequestQueue queue;
    private List<String> commentsList = new ArrayList<>();

    public MarkerCommentsHandler(Context appContext, RequestQueue queue){
        this.applicationContext = appContext;
        this.queue = queue;
    }

    public void postComment(MarkerCommentParams commentObject){

        if(!commentObject.getCommentString().isEmpty()){
            NewCommentHttpRequest commentHttpRequest = new NewCommentHttpRequest();
            commentHttpRequest.execute(commentObject);
        }
    }

    public ArrayList<String> getComments(String url){

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String commentsAsString = response;
                Log.d("onRESPONSE", response);

                if(commentsAsString != null){

                    //We remove the brackets and quotations
                    commentsAsString = commentsAsString.replace("[", "");
                    commentsAsString = commentsAsString.replace("]", "");
                    commentsAsString = commentsAsString.replace("\"", "");

                    //Remove any additional white space with the regex \\s*
                    commentsList = Arrays.asList(commentsAsString.split("\\s*, \\s*"));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayErrorMessage(error);
            }
        });

        Log.d("TAG", commentsList.get(0));
        queue.add(request);

        return new ArrayList<>(commentsList);
    }

    private void displayErrorMessage(VolleyError error){
        //Convert error to NetworkResponse to get details
        NetworkResponse errorResponse = error.networkResponse;
        String errorResponseString = "Sorry, we ran into some network issues";

        if(errorResponse != null && errorResponse.data != null){
            Toast.makeText(applicationContext, errorResponseString, Toast.LENGTH_LONG).show();
        }
    }

}
