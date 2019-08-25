package com.mega.matrimony.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mega.matrimony.Utility.FontsOverride;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyApplication extends Application {

    public static final String TAG = MyApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;
    private static JSONObject spinData;
    private static boolean isApprove=true;
    private static boolean is_plan=false;
    private Activity currentActivity;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mInstance = this;
        setupActivityListener();
        //TODO Fixed Picasso error
        setPicassoSingleInstance();
        //FontsOverride.setDefaultFont(this, "DEFAULT", "font/roboto_regular.ttf");
        //FontsOverride.setDefaultFont(this, "MONOSPACE", "font/roboto_regular.ttf");
        //FontsOverride.setDefaultFont(this, "SERIF", "font/roboto_regular.ttf");
        //FontsOverride.setDefaultFont(this, "SANS_SERIF", "font/roboto_regular.ttf");
        FontsOverride.overrideFont(getApplicationContext(), "SERIF", "font/roboto_regular.ttf");

    }

    public void setPicassoSingleInstance() {
        Picasso.setSingletonInstance(new Picasso.Builder(context).build());
    }

    public void printHashKey(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                currentActivity = activity;
            }
            @Override
            public void onActivityStarted(Activity activity) {
                currentActivity = activity;
            }
            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
            }
            @Override
            public void onActivityPaused(Activity activity) {
                currentActivity = null;
            }
            @Override
            public void onActivityStopped(Activity activity) {
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public ViewGroup getCurrentView(){
       // ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        //omponentName cn = am.getRunningTasks(1).get(0).topActivity;

       // Log.e("resp",cn.toString());
       return currentActivity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public Activity getCurrentActivity(){
        return this.currentActivity;
    }

    public static boolean isApprove() {
        return isApprove;
    }

    public static void setApprove(boolean approve) {
        isApprove = approve;
    }

    public static JSONObject getSpinData() {
        return spinData;
    }

    public static void setSpinData(JSONObject spinData) {

        try {
            MyApplication.spinData = spinData.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setPlan(boolean is_plan){
        MyApplication.is_plan=is_plan;
    }

    public static boolean getPlan(){
        return is_plan;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
