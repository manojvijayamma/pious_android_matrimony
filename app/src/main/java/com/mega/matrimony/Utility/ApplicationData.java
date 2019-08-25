package com.mega.matrimony.Utility;

import android.annotation.SuppressLint;


/**
 * All com temporary data stored in this class
 * Created by Nasirali on 02-02-2019.
 */


@SuppressLint("SimpleDateFormat")
public class ApplicationData {
    ///////////////////////
    //General objects
    //applicationData global object for use whole application
    private static ApplicationData sharedInstance;
    //file objects for create com directory on start com
    //////////////////////////
    public boolean isProfileChanged = false;

    private void initialize() {
        ///////////////////////
        //General objects & methods initialization

        //createDirectory();
        ///////////////////////

        /////////////////////////////
        //globally initilization of com objects, class & methods
        /////////////////////////////
    }

    private ApplicationData() {

    }

    public static ApplicationData getSharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new ApplicationData();
            sharedInstance.initialize();
        }
        return sharedInstance;
    }

}
