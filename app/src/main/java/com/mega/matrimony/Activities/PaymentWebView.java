package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PaymentWebView extends AppCompatActivity {

    String Total_amount,Method,Plan_id,plan_name;
    private WebView web_payment;
    SessionManager session;
    Common common;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        session=new SessionManager(this);
        common=new Common(this);

        web_payment=(WebView)findViewById(R.id.web_payment);
        web_payment.setWebViewClient(new MyBrowser());

        Bundle b=getIntent().getExtras();
        if (b!=null){
            if (b.containsKey("Total_amount")){
                Total_amount=b.getString("Total_amount");
            }
            if (b.containsKey("Method")){
                Method=b.getString("Method");
            }
            if (b.containsKey("Plan_id")){
                Plan_id=b.getString("Plan_id");
            }
            if (b.containsKey("plan_name")){
                plan_name=b.getString("plan_name");
            }
        }

        String url= Utils.payment_url+session.getLoginData(SessionManager.KEY_USER_ID)+"/"+Method+"/"+Plan_id+"/"+plan_name+
               "/"+Total_amount;
        Log.d("weburl",url);
        web_payment.getSettings().setLoadsImagesAutomatically(true);
        web_payment.getSettings().setJavaScriptEnabled(true);
        web_payment.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        web_payment.loadUrl(url);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(Utils.payment_fail)){
                common.showToast("Payment Failed");
                finish();
            }else if (url.equals(Utils.payment_success)){
                common.showToast("Payment SuccessActivity. Thank you");
                getCurrentPlan();

            }
            view.loadUrl(url);
            //Log.d("weburl",url);
            return true;
        }

    }

    private void getCurrentPlan(){
        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String,String> param=new HashMap<>();
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        common.makePostRequest(Utils.check_plan, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    MyApplication.setPlan(object.getBoolean("is_show"));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
                //pd.dismiss();
            }
        }, error -> {
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
            showAlert();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        showAlert();
    }

    private void showAlert(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to leave without making payment?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton("No",null);
        alert.show();
    }

}