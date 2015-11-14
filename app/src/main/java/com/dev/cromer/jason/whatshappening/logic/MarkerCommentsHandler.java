package com.dev.cromer.jason.whatshappening.logic;



import com.dev.cromer.jason.whatshappening.networking.HttpGetRequest;
import com.dev.cromer.jason.whatshappening.networking.NewCommentHttpRequest;
import com.dev.cromer.jason.whatshappening.objects.MarkerCommentParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MarkerCommentsHandler {

    public MarkerCommentsHandler(){
    }

    public void postComment(MarkerCommentParams commentObject){

        if(!commentObject.getCommentString().isEmpty()){
            NewCommentHttpRequest commentHttpRequest = new NewCommentHttpRequest();
            commentHttpRequest.execute(commentObject);
        }
    }

    public ArrayList<String> getComments(String url){
        HttpGetRequest getRequest = new HttpGetRequest();
        String commentsAsString;
        List<String> commentsList = new ArrayList<>();

        try{
            //The return is a Comma-seperated list as a String
            commentsAsString = getRequest.execute(url).get();

            if(commentsAsString != null){

                //We remove the brackets and quotations
                commentsAsString = commentsAsString.replace("[", "");
                commentsAsString = commentsAsString.replace("]", "");
                commentsAsString = commentsAsString.replace("\"", "");

                //Remove any additional white space with the regex \\s*
                commentsList = Arrays.asList(commentsAsString.split("\\s*, \\s*"));
            }
        }
        catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        return new ArrayList<>(commentsList);
    }


}
