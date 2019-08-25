package com.mega.matrimony.Activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Network.ConnectionDetector;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import me.relex.circleindicator.CircleIndicator;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FirstActivity extends AppCompatActivity {

    private ViewPager viewPager;

    private int[] layouts;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Button btn_login, btn_register;
    private static final int PERMISSION_REQUEST_CODE = 200;
    ProgressDialog pd;
    SessionManager session;
    Common common;
    boolean doubleBackToExitPressedOnce = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.gradient_button);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            // window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStatusBarGradiant(this);
        setContentView(R.layout.activity_first);

        session = new SessionManager(this);
        common = new Common(this);

        btn_login = (Button) findViewById(R.id.btn_id);
        btn_register = (Button) findViewById(R.id.btn_register);

        viewPager = (ViewPager) findViewById(R.id.pager);

        layouts = new int[]{
                R.layout.slider_slide_1,
                R.layout.slider_slide_2,
                R.layout.slider_slide_3};

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);

//        String token=FirebaseInstanceId.getInstance().getToken();
//        if (token!=null)
//            session.setUserData(SessionManager.KEY_DEVICE_TOKEN,token);

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        btn_login.setOnClickListener(view -> startActivity(new Intent(FirstActivity.this, LoginActivity.class)));
        btn_register.setOnClickListener(view -> startActivity(new Intent(FirstActivity.this, RegisterFirstActivity.class)));

        if (!checkPermission()) {
            requestPermission();
        }

        getList();
    }

    private void getToken() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        pd = new ProgressDialog(FirstActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();


        StringRequest token = new StringRequest(Request.Method.GET, Utils.get_token, response -> {
            // Log.d("resp","tocken   "+response);
            // pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                getList();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            common.showToast(error.getMessage() + " ");
            pd.dismiss();
            //getToken();
        });
        RetryPolicy mRetryPolicy = new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        token.setRetryPolicy(mRetryPolicy);
        MyApplication.getInstance().addToRequestQueue(token);

    }

    private void getList() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        pd = new ProgressDialog(FirstActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        common.makePostRequest(Utils.common_list, new HashMap<String, String>(), response -> {
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                MyApplication.setSpinData(object);
//                if (session.isLoggedIn()){
//                    if (session.getLoginData(SessionManager.LOGIN_WITH).equals("facebook")){
//                        loginApiFB(session.getLoginData(SessionManager.FB_ID));
//                    }else {
//                        loginApi(session.getLoginData(SessionManager.KEY_LOGINID),session.getLoginData(SessionManager.KEY_PASSWORD));
//                    }
//                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });
    }

    private void loginApiFB(final String fb_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fb_id", fb_id);
        params.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));

        common.makePostRequest(Utils.login, params, response -> {
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("token"));
                Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_SHORT).show();
                if (object.getString("status").equals("success")) {
                    JSONObject user_data = object.getJSONObject("user_data");

                    session.createLoginSession("", "",
                            user_data.getString("id"));
                    session.setUserData(SessionManager.KEY_EMAIL, user_data.getString("email"));
                    session.setUserData(SessionManager.KEY_username, user_data.getString("username"));
                    session.setUserData(SessionManager.KEY_GENDER, user_data.getString("gender"));
                    session.setUserData(SessionManager.KEY_matri_id, user_data.getString("matri_id"));
                    session.setUserData(SessionManager.KEY_PLAN_STATUS, user_data.getString("plan_status"));
                    session.setUserData(SessionManager.LOGIN_WITH, "facebook");
                    session.setUserData(SessionManager.FB_ID, fb_id);

                    startActivity(new Intent(FirstActivity.this, DashboardActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp", error.getMessage() + "   ");
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });

    }

    private void loginApi(final String username, final String password) {

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));
        //Log.d("resp",params.toString()+"   "+token);

        common.makePostRequest(Utils.login, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("resp",response);
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("token"));
                    //Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_SHORT).show();
                    if (object.getString("status").equals("success")) {
                        JSONObject user_data = object.getJSONObject("user_data");

                        session.createLoginSession(username, password, user_data.getString("id"));
                        session.setUserData(SessionManager.KEY_EMAIL, user_data.getString("email"));
                        session.setUserData(SessionManager.KEY_username, user_data.getString("username"));
                        session.setUserData(SessionManager.KEY_GENDER, user_data.getString("gender"));
                        session.setUserData(SessionManager.KEY_matri_id, user_data.getString("matri_id"));
                        session.setUserData(SessionManager.KEY_PLAN_STATUS, user_data.getString("plan_status"));
                        session.setUserData(SessionManager.LOGIN_WITH, "local");

                        startActivity(new Intent(FirstActivity.this, DashboardActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp", error.getMessage() + "   ");
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA,
                ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(FirstActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
