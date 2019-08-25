package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AllCmsActivity extends AppCompatActivity {

    TextView tv_about;
    Common common;
    SessionManager session;
    ProgressDialog pd;
    String cms_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cms);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CMS");

        common=new Common(this);
        session=new SessionManager(this);

        Bundle b=getIntent().getExtras();
        if (b!=null){
            if (b.containsKey("cmsTag")){
                switch (b.getString("cmsTag")){
                    case "privacy":
                        getSupportActionBar().setTitle("Privacy Policy");
                        cms_name="Privacy Policy";
                        break;
                    case "about":
                        getSupportActionBar().setTitle("About us");
                        cms_name="About Us";
                        break;
                    case "refund":
                        getSupportActionBar().setTitle("Refund Policy");
                        cms_name="Refund Policy";
                        break;
                    case "term":
                        getSupportActionBar().setTitle("Terms and Condition");
                        cms_name="Terms and Condition";
                        break;
                }
            }
        }

        tv_about=(TextView)findViewById(R.id.tv_about);
        getCMS();
    }

    private void getCMS(){
        pd=new ProgressDialog(AllCmsActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String,String> param=new HashMap<>();
        param.put("cms_name",cms_name);

        common.makePostRequest(Utils.all_cms, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        JSONObject data=object.getJSONObject("data");
                        String page_content=data.getString("page_content");
                        tv_about.setText(Html.fromHtml(page_content));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
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
