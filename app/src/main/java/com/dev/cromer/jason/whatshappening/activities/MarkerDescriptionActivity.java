package com.dev.cromer.jason.whatshappening.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.cromer.jason.whatshappening.R;
import com.dev.cromer.jason.whatshappening.logic.MarkerCommentsHandler;
import com.dev.cromer.jason.whatshappening.objects.MarkerCommentParams;
import com.dev.cromer.jason.whatshappening.objects.MarkerLikesPostRequestParams;
import com.dev.cromer.jason.whatshappening.networking.HttpGetRequest;
import com.dev.cromer.jason.whatshappening.networking.UpdateMarkerLikesHttpPostRequest;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MarkerDescriptionActivity extends AppCompatActivity implements View.OnClickListener,
                                EditText.OnEditorActionListener {

    private TextView markerDescriptionTextView;
    private TextView markerLikesTextView;
    private ImageButton likeButton;
    private String markerDescription = "";
    private String markerLikes = "";
    static ListView commentsListView;
    private String markerID;
    private boolean hasLiked = false;
    private SharedPreferences preferences;
    private FloatingActionButton floatingActionButton;
    private LayoutInflater layoutInflater;
    private EditText userComment;
    static PopupWindow popupWindow;

    //constants
    private static final int DEFAULT_LIKES = 0;
    private static final boolean DEFAULT_HAS_LIKED = false;
    private static final String LIKED_STRING = "like";
    private static final String DISLIKED_STRING = "dislike";
    private static final String GET_COMMENTS_ENDPOINT = "http://whatsappeningapi.elasticbeanstalk.com/api/get_comments/";
    private static final String POST_COMMENT_ENDPOINT = "http://whatsappeningapi.elasticbeanstalk.com/api/post_comment/";
    private static final String GET_LIKES_ENDPOINT = "http://whatsappeningapi.elasticbeanstalk.com/api/get_marker_likes/";
    private static final String GET_DESCRIPTION_ENDPOINT = "http://whatsappeningapi.elasticbeanstalk.com/api/get_marker_description/";
    private static final String UPDATE_LIKES_ENDPOINT = "http://whatsappeningapi.elasticbeanstalk.com/api/update_marker_likes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_description);

        markerLikesTextView = (TextView) findViewById(R.id.markerLikesTextView);
        markerDescriptionTextView = (TextView) findViewById(R.id.markerDescriptionTextView);
        likeButton = (ImageButton) findViewById(R.id.likeButton);
        commentsListView = (ListView) findViewById(R.id.commentsListView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        likeButton.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);

        //Get the ID from the marker thats been clicked on, on the map
        Intent thisIntent = getIntent();
        markerID = thisIntent.getStringExtra("MARKER_ID");

        //Get the shared preference to see if user has liked or disliked the post
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        checkUserLike();

        //getMarkerDescription and likes from database via passed in Marker ID
        getMarkerDescription();
        getMarkerLikes();

        //Set marker markerDescription and likes in their textviews
        displayMarkerDescription();
        displayMarkerLikes();

        //Display our comments
        setAndDisplayComments(commentsListView);
    }


    private void getMarkerDescription(){
        final String url = GET_DESCRIPTION_ENDPOINT + markerID;
        HttpGetRequest httpGetRequest = new HttpGetRequest();

        try{
            markerDescription = httpGetRequest.execute(url).get();

            //Remove quotations from markerDescription
            markerDescription = markerDescription.replace("\"", "");
        }
        catch(ExecutionException | InterruptedException | NullPointerException e){
            e.printStackTrace();
        }
    }


    private void displayMarkerDescription(){
        if(markerDescription != null){
            markerDescriptionTextView.setText(markerDescription);
        }
        else{
            markerDescriptionTextView.setText("");
        }
    }


    private void getMarkerLikes(){
        final String url = GET_LIKES_ENDPOINT + markerID;
        HttpGetRequest httpGetRequest = new HttpGetRequest();

        try{
            markerLikes = httpGetRequest.execute(url).get();
        }
        catch(ExecutionException | InterruptedException | NullPointerException e){
            e.printStackTrace();
        }
    }


    private void displayMarkerLikes(){
        if(markerLikes != null){
            markerLikesTextView.setText(markerLikes);
        }
        else{
            markerLikesTextView.setText(DEFAULT_LIKES);
        }
    }


    private void updateMarkerLikes(String likeType){
        final String url = UPDATE_LIKES_ENDPOINT + markerID;

        //Create an object containing necessary parameters for our post request
        MarkerLikesPostRequestParams params = new MarkerLikesPostRequestParams(url, likeType);

        UpdateMarkerLikesHttpPostRequest postRequest = new UpdateMarkerLikesHttpPostRequest();

        try{
            postRequest.execute(params).get();
        }
        catch (ExecutionException | InterruptedException | NullPointerException e){
            Toast.makeText(getApplicationContext(), "Cannot contact our server! Check your connection.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //Get and display likes after submitting like/dislike
        getMarkerLikes();
        displayMarkerLikes();
    }


    @Override
    public void onClick(View v) {
        if(v == floatingActionButton){
            //Inflate our custom layout
            inflatePopUpWindow(v);
        }
        if(v == likeButton && !hasLiked) {
            likeButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_black_24dp));
            updateMarkerLikes(LIKED_STRING);
            saveUserLike();
            hasLiked = true;
        }
        else if(v == likeButton){
            likeButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_black_24dp));
            updateMarkerLikes(DISLIKED_STRING);
            saveUserDislike();
            hasLiked = false;
        }
    }

    private void inflatePopUpWindow(View v){

        final int subtractFromTopPixels = 500;
        final int popupMarginX = 0;
        final int popupMarginY = 0;
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.popUpLayout);
        final View inflatedView = layoutInflater.inflate(R.layout.pop_up_comment, viewGroup, false);

        //Add listener to our EditText to allow window to close after the "done" button is pressed
        userComment = (EditText) inflatedView.findViewById(R.id.commentEditText);
        userComment.setOnEditorActionListener(this);

        //Get device screen size and make a new point that corresponds to it
        Display display = getWindowManager().getDefaultDisplay();
        final Point screenSize = new Point();

        //Assign screensize to our point
        display.getSize(screenSize);

        //Set height depending on screen size
        popupWindow = new PopupWindow(inflatedView, screenSize.x, screenSize.y - subtractFromTopPixels, true);

        //Set background (Round edges), and make focusable (keyboard on window press)
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pop_up_background));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        //Set custom animation (found in values/styles.xml folder
        popupWindow.setAnimationStyle(R.style.Animation);

        //Show the popup at bottom of screen with margin.
        //Params are (View parent, gravity, int x, int y);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, popupMarginX, popupMarginY);
    }

    private void saveUserLike(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(markerID, true);
        editor.apply();
    }

    private void saveUserDislike(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(markerID, false);
        editor.apply();
    }

    private void checkUserLike(){
        final boolean userHasLiked = preferences.getBoolean(markerID, DEFAULT_HAS_LIKED);
        if(userHasLiked){

            //Change image to the filled-in heart image
            likeButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_black_24dp));
            hasLiked = true;
        }
    }


    public void setAndDisplayComments(ListView listView){

        //Retrieve comments from database and assign result to our ArrayList
        final String getCommentsUrl = GET_COMMENTS_ENDPOINT + markerID;
        MarkerCommentsHandler commentsHandler = new MarkerCommentsHandler();
        ArrayList<String> commentList = commentsHandler.getComments(getCommentsUrl);

        //set our adapter with our list of comments to the listView
        listView.setAdapter(new ArrayAdapter<>(MarkerDescriptionActivity.this,
                R.layout.comment_item, R.id.commentTextView, commentList));
    }


    @Override
    public boolean onNavigateUp(){

        //Destroy our activity and return to top of the activity stack
        finish();
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){

            //Create objects for our POST request
            MarkerCommentsHandler commentsHandler = new MarkerCommentsHandler();
            final String postCommentUrl = POST_COMMENT_ENDPOINT + markerID;

            //Convert our EditText input to a String
            final String comment = userComment.getText().toString();

            //Post new comment to our database
            MarkerCommentParams commentParams = new MarkerCommentParams(comment, postCommentUrl);
            commentsHandler.postComment(commentParams);

            //Close our popupWindow
            popupWindow.dismiss();

            //Refresh our comment list
            setAndDisplayComments(commentsListView);

            return true;
        }
        return false;
    }
}
