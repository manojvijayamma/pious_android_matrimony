package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.mega.matrimony.Network.ConnectionDetector;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText et_email;
    Button btn_reset;
    Common common;
    SessionManager session;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");

        common=new Common(this);
        session=new SessionManager(this);

        et_email=(EditText)findViewById(R.id.et_email);
        btn_reset=(Button) findViewById(R.id.btn_reset);
        common.setDrawableLeftEditText(R.drawable.email,et_email);

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });

    }

    private void validData() {
        String email=et_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            et_email.setError("Please enter email");
            return;
        }
        if (!isValidEmail(email)){
            et_email.setError("Please enter valid email");
            return;
        }
        forgotApi(email);
    }

    private void forgotApi(String email) {
        if(!ConnectionDetector.isConnectingToInternet(this)){
            Toast.makeText(this,"Please check your internet connection!",Toast.LENGTH_LONG).show();
            return;
        }

        pd=new ProgressDialog(ForgotPasswordActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        Common.hideSoftKeyboard(this);

        HashMap<String, String> params = new HashMap<>();
        params.put("username", email);

        common.makePostRequest(Utils.forgot, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp",response);
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN,object.getString("token"));
                    if (!object.getString("status").equals("error")){
                        et_email.setText("");
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });

    }

    public static boolean isValidEmail(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
           finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
