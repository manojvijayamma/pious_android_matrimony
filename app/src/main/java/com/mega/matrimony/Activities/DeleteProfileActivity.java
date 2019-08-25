package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

public class DeleteProfileActivity extends AppCompatActivity {

    EditText et_about;
    Button btn_submit;
    Common common;
    SessionManager session;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delete Profile");

        common=new Common(this);
        session=new SessionManager(this);

        btn_submit=(Button)findViewById(R.id.btn_id);
        et_about=(EditText)findViewById(R.id.et_about);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRequest();
            }
        });

    }

    private void deleteRequest() {
        final String reason=et_about.getText().toString().trim();
        if (TextUtils.isEmpty(reason)){
            et_about.setError("Please enter reason");
            return;
        }
        pd=new ProgressDialog(DeleteProfileActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String,String> param=new HashMap<>();
        param.put("reason",reason);
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequest(Utils.delete_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")){
                        et_about.setText("");
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
