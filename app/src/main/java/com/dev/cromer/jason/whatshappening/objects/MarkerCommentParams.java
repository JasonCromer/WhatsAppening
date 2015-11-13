package com.dev.cromer.jason.whatshappening.objects;


public class MarkerCommentParams {

    String commentString;
    String url;

    public MarkerCommentParams(String comment, String url){
        this.commentString = comment;
        this.url = url;
    }

    public String getCommentString(){
        return commentString;
    }

    public String getUrl(){
        return url;
    }
}
