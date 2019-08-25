package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText et_old_password,et_new_password,et_con_password;
    private TextInputLayout input_old,input_new,input_con;
    private Button btn_submit;
    SessionManager session;
    ProgressDialog pd;
    Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        session=new SessionManager(this);
        common=new Common(this);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");

        input_old=(TextInputLayout) findViewById(R.id.input_old);
        input_new=(TextInputLayout) findViewById(R.id.input_new);
        input_con=(TextInputLayout) findViewById(R.id.input_con);

        et_old_password=(EditText)findViewById(R.id.et_old_password);
        et_new_password=(EditText)findViewById(R.id.et_new_password);
        et_con_password=(EditText)findViewById(R.id.et_con_password);
        btn_submit=(Button)findViewById(R.id.btn_id);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });

        et_old_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)){
                    et_old_password.setError("Please enter password");
                    input_old.setPasswordVisibilityToggleEnabled(false);
                    return;
                }else {
                    input_old.setPasswordVisibilityToggleEnabled(true);
                }
            }
        });

        et_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)){
                    et_new_password.setError("Please enter password");
                    input_new.setPasswordVisibilityToggleEnabled(false);
                    return;
                }else {
                    input_new.setPasswordVisibilityToggleEnabled(true);
                }
            }
        });

        et_con_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)){
                    et_con_password.setError("Please enter password");
                    input_con.setPasswordVisibilityToggleEnabled(false);
                    return;
                }else {
                    input_con.setPasswordVisibilityToggleEnabled(true);
                }
            }
        });

    }

    private void validData() {
        String old=et_old_password.getText().toString().trim();
        String newpass=et_new_password.getText().toString().trim();
        String con=et_con_password.getText().toString().trim();
        boolean isvalid=true;
        if (TextUtils.isEmpty(old)){
            et_old_password.setError("Please enter old password");
            input_old.setPasswordVisibilityToggleEnabled(false);
            isvalid=false;
        }
        if (TextUtils.isEmpty(newpass)){
            et_new_password.setError("Please enter new password");
            input_new.setPasswordVisibilityToggleEnabled(false);
            isvalid=false;
        }
        if (newpass.length()<6){
            et_new_password.setError("Please enter atleast 6 character");
            input_new.setPasswordVisibilityToggleEnabled(false);
            isvalid=false;
        }
        if (TextUtils.isEmpty(con)){
            et_con_password.setError("Please enter confirm password");
            input_con.setPasswordVisibilityToggleEnabled(false);
            isvalid=false;
        }
        if (con.length()<6){
            et_con_password.setError("Please enter atleast 6 character");
            input_con.setPasswordVisibilityToggleEnabled(false);
            isvalid=false;
        }
        if (!newpass.equals(con)){
            et_con_password.setError("New password and confirm password not match");
            input_con.setPasswordVisibilityToggleEnabled(false);
            isvalid=false;
        }

        if (isvalid){
            HashMap<String,String> param=new HashMap<>();
            param.put("old_pass",old);
            param.put("new_pass",newpass);
            param.put("cnfm_pass",con);
            param.put("member_id",session.getLoginData(SessionManager.KEY_USER_ID));
            changeApi(param);
        }

    }

    private void changeApi(final HashMap<String,String> param) {

        pd=new ProgressDialog(ChangePasswordActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        common.makePostRequest(Utils.change_password, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (!object.getString("status").equals("error")){

                        session.setUserData(SessionManager.KEY_PASSWORD,param.get("cnfm_pass"));
                        et_con_password.setText("");
                        et_old_password.setText("");
                        et_new_password.setText("");
                    }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
