package com.mega.matrimony.Utility;


import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.firebase.iid.FirebaseInstanceId;

import com.mega.matrimony.Activities.LoginActivity;
import com.mega.matrimony.Application.MyApplication;


public class SessionManager {
    SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AndroidHivePref";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_PASSWORD = "password";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_LOGINID = "loginId";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_username = "userName";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_matri_id = "Matri_id";
    public static final String KEY_PLAN_STATUS = "plan_status";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_DEVICE_TOKEN = "device_token";
    public static final String TOKEN = "token";
    public static final String HEADER_TOKEN = "headertoken";
    public static final String TIME_ZONE = "timezone";
    public static final String LOGIN_WITH = "login_with";
    public static final String FB_ID= "fb_id";
    public static final String GOOGLE_ID = "google_id";
    public static final String DEVICE_ID = "Muslim_Firebase_id";
    public static final String SKIP_ID = "skip_id";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email,String password,String user_id){//String password,
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        //editor.putString(KEY_PASSWORD, password);

        // Storing email in pref
        editor.putString(KEY_LOGINID, email);
        editor.putString(KEY_USER_ID, user_id);
        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    public String getLoginData(String key){
        return pref.getString(key,"");
    }

    public void setUserData(String key,String value){
        editor.putString(key, value);
        editor.commit();
    }

//    public void logoutUser(){
//
//        editor.clear();
//        editor.commit();
//
//        Intent i = new Intent(_context, LoginActivity.class);
//
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        _context.startActivity(i);
//    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        //TODO DELETE FCM Token
        new Thread(() -> {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        //TODO END DELETE FCM Token

        //TODO Generate new FCM Token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            new SessionManager(MyApplication.getInstance()).setUserData(KEY_DEVICE_TOKEN,newToken);
            AppDebugLog.print("newToken in logout : "+ newToken);
        });
        //TODO END Generate new FCM Token

//        MyApplication.setSpinData();

        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}