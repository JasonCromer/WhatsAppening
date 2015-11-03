package com.dev.cromer.jason.whatshappening.logic;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;

public class NotificationInstructionFactory implements DialogInterface.OnClickListener {

    private AlertDialog.Builder alertDialogBuilder;
    private ContextThemeWrapper themeWrapper;


    public NotificationInstructionFactory(Context context){
        //Pass in our activity context and set our theme dialog for the alert dialog
        themeWrapper = new ContextThemeWrapper(context, android.R.style.Theme_Dialog);

        //Use our theme wrapper to instantiate a new Alert Dialog builder
        alertDialogBuilder = new AlertDialog.Builder(themeWrapper);

        //Set our default icon
        setAlertIcon();
    }


    private void setAlertIcon(){
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
    }


    public void setAlertTitle(String title){

    }







    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
    






    /*
        TODO: dialogs for: Create new marker, search, and voting
     */

}
