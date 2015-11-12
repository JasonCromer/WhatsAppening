package com.dev.cromer.jason.whatshappening.objects;


public class MarkerLikesPostRequestParams {

    String url;
    String likeType;

    public MarkerLikesPostRequestParams(String url, String likeType){
        this.url = url;
        this.likeType = likeType;
    }

    public String getUrl(){
        return this.url;
    }

    public String getLikeType(){
        return this.likeType;
    }
}
