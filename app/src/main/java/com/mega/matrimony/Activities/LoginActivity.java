package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Network.ConnectionDetector;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private String LOGIN_TAG = "login";
    private Button btn_signup, btn_login, login_button;
    private EditText et_user, et_password;
    private TextView tv_forgot;
    ProgressDialog pd;
    Common common;
    SessionManager session;
    TextInputLayout pass_input, id_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        common = new Common(this);
        session = new SessionManager(this);

        initData();

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null)
            session.setUserData(SessionManager.KEY_DEVICE_TOKEN, token);

        pass_input = (TextInputLayout) findViewById(R.id.pass_input);
        id_input = (TextInputLayout) findViewById(R.id.id_input);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        et_user = (EditText) findViewById(R.id.et_user);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_forgot = (TextView) findViewById(R.id.tv_forgot);

        common.setDrawableLeftEditText(R.drawable.user_pink, et_user);

        btn_login = (Button) findViewById(R.id.btn_id);
        btn_signup = (Button) findViewById(R.id.btn_signup);

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);
//        if (MyApplication.getSpinData() == null) {
//            getList();
//        }
    }

    private void getList() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        common.makePostRequest(Utils.common_list, new HashMap<String, String>(), response -> {
            Log.d("resp", "login");
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                MyApplication.setSpinData(object);
                initData();
                pd.dismiss();

            } catch (JSONException e) {
                pd.dismiss();
                e.printStackTrace();
            }

        }, error -> {
            if (pd != null)
                pd.dismiss();
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_id) {
            validData();
            //startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
        } else if (id == R.id.btn_signup) {
            startActivity(new Intent(LoginActivity.this, RegisterFirstActivity.class));
        } else if (id == R.id.tv_forgot) {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        }
    }

    private void validData() {
        String username = et_user.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        boolean isvalid = true;
        if (TextUtils.isEmpty(username)) {
            et_user.setError("Please enter email or matriId");
            isvalid = false;
        }
        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please enter password");
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
            return;
        }
        if (password.length() < 6) {
            et_password.setError("Please enter atleast 6 character");
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        }
        if (isvalid) {
            loginApi(username, password);
        }
    }

    private void loginApiFB(final JSONObject json_object) throws JSONException {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        pd = new ProgressDialog(LoginActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("fb_id", json_object.getString("id"));
        params.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));

        common.makePostRequest(Utils.login, params, response -> {
            //Log.d("resp",response);
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
                    session.setUserData(SessionManager.FB_ID, json_object.getString("id"));

                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));

                } else if (object.getString("status").equals("error")) {
                    Intent i = new Intent(getApplicationContext(), RegisterFirstActivity.class);
                    i.putExtra("fb_data", json_object.toString());
                    startActivity(i);
                }
                pd.dismiss();

            } catch (JSONException e) {
                pd.dismiss();
                e.printStackTrace();
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            pd.dismiss();
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
            }
        });

    }

    private void loginApi(final String username, final String password) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        Common.hideSoftKeyboard(this);
        pd = new ProgressDialog(LoginActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));
        //Log.d("resp",params.toString());

        common.makePostRequestWithTag(Utils.login, params, response -> {
            //Log.d("resp",response);
            if (pd != null)
                pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("token"));
                Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_SHORT).show();
                if (object.getString("status").equals("success")) {
                    JSONObject user_data = object.getJSONObject("user_data");

                    AppDebugLog.print("Gender in login : " + user_data.getString("gender"));
                    session.createLoginSession(username, password, user_data.getString("id"));
                    session.setUserData(SessionManager.KEY_EMAIL, user_data.getString("email"));
                    session.setUserData(SessionManager.KEY_username, user_data.getString("username"));
                    session.setUserData(SessionManager.KEY_GENDER, user_data.getString("gender"));
                    session.setUserData(SessionManager.KEY_matri_id, user_data.getString("matri_id"));
                    session.setUserData(SessionManager.KEY_PLAN_STATUS, user_data.getString("plan_status"));
                    session.setUserData(SessionManager.LOGIN_WITH, "local");

                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            if (pd != null)
                pd.dismiss();
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
            }
        }, LOGIN_TAG);

    }

    @Override
    public void onBackPressed() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        MyApplication.getInstance().cancelPendingRequests(LOGIN_TAG);
        startActivity(new Intent(LoginActivity.this, FirstActivity.class));
    }
}
