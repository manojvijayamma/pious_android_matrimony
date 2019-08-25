package com.mega.matrimony.Activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mega.matrimony.retrofit.AppApiService;
import com.mega.matrimony.retrofit.RetrofitClient;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Network.ConnectionDetector;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashScreenActivity extends AppCompatActivity {
    private AVLoadingIndicatorView progressBar;
    private SessionManager session;
    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBar);
        session = new SessionManager(this);
        common = new Common(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        generateToken();
    }

    private void generateToken() {
        progressBar.setVisibility(View.VISIBLE);
        //volleyRequest.getResponseInJsonStringUsingVolley(GET_TOKEN_TAG,AppConstants.BASE_URL+AppConstants.GET_TOKEN,null);

        HashMap<String, String> param = new HashMap<>();
        param.put("appversion", Common.getAppVersionName(this));
        param.put("device_type", "android");
        //param.put("user-agent", "NI-AAPP");

        AppDebugLog.print("Params in getToken : "+param.toString());

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService service = retrofit.create(AppApiService.class);
        Call<JsonObject> call = service.getToken(param);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);
                AppDebugLog.print("response in generateToken : " + response.body());
                JsonObject data = response.body();
                if (data != null) {
                    if (data.has("tocken"))
                        session.setUserData(SessionManager.TOKEN, data.get("tocken").getAsString());

                    if (data.has("is_force_update") && data.get("is_force_update").getAsBoolean()) {
                        openForceUpdateDialog();
                        return;
                    }

                    startTimer();
                } else {
                    startTimer();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                AppDebugLog.print("error in generateToken : " + t.getMessage());
                //Utility.showToast(SplashAvtivity.this, getString(R.string.err_msg_try_again_later));
                progressBar.setVisibility(View.GONE);
                startTimer();
            }
        });
    }

//    private void generateToken() {
//        if (!ConnectionDetector.isConnectingToInternet(this)) {
//            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
//            progressBar.setVisibility(View.GONE);
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        StringRequest token = new StringRequest(Request.Method.GET, Utils.get_token, response -> {
//            progressBar.setVisibility(View.GONE);
//            try {
//                AppDebugLog.print("response in generateToken : " + response);
//                //AppDebugLog.print("current version in generateToken : "+getAppVersionName(this));
//                JSONObject object = new JSONObject(response);
//                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
//
//                if (object.has("is_force_update") && object.getBoolean("is_force_update")) {
//                    if (object.has("app_version")) {
//                        try {
//                            double version = Double.parseDouble(object.getString("app_version"));
//                            if (version > Double.parseDouble(getAppVersionName(this))) {
//                                openForceUpdateDialog();
//                                return;
//                            }
//                        } catch (Exception e) {
//                            AppDebugLog.print("Exception in getToken :" + e.getMessage());
//                        }
//                    }
//                }
//
//                if (object.has("tocken"))
//                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
//
//                startTimer();
//            } catch (JSONException e) {
//                startTimer();
//                e.printStackTrace();
//            }
//        }, error -> {
//            Log.d("resp", error.getMessage() + "   ");
//            //common.showToast(error.getMessage() + " ");
//            progressBar.setVisibility(View.GONE);
//            closeScreen();
//        });
//        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        token.setRetryPolicy(mRetryPolicy);
//        MyApplication.getInstance().addToRequestQueue(token);
//    }

    private void openForceUpdateDialog() {
        String updateUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available!!!")
                .setCancelable(false)
                .setMessage("Please update application for better use.")
                .setPositiveButton("Update", (dialog1, which) -> redirectStore(updateUrl))
//                .setNegativeButton("No, thanks", (dialog12, which) -> {
//                    finish();
//                })
                .create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void closeScreen() {
        Intent intent = null;
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            intent = new Intent(MyApplication.getInstance(), DashboardActivity.class);
            intent.putExtra("isFromSplash", true);
        } else {
            intent = new Intent(MyApplication.getInstance(), FirstActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void startTimer() {
        CountDownTimer lTimer = new CountDownTimer(300, 300) {
            public void onFinish() {
                closeScreen();
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            AppDebugLog.print("Exception In getAppVersionName : " + e.getLocalizedMessage());
        }
        return "1.0";
    }
}
