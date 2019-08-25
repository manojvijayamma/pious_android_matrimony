package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Custom.NonScrollListView;
import com.mega.matrimony.Model.paymentItem;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MakePaymentsActivity extends AppCompatActivity {

    String plan_name="",plan_id="";
    private TextView tv_name,tv_duration,tv_amount,tv_tax,tv_discount,tv_total,tv_label_tex,tv_bank_label,tv_disc_label;
    private LinearLayout lay_two,lay_code;
    private EditText et_code;
    LinearLayout lay_bank;
    //private RadioButton rad_biz,rad_payu,rad_paypal,rad_cc;
    //private RadioGroup rad_grp;
    private NonScrollListView lv_method;
    Common common;
    private Button btn_pay,btn_redeem;
    JSONObject plan_data;
    float total_amt;
    ProgressDialog pd;
    SessionManager session;
    String tax_applicable,service_tax;
    List<paymentItem> list=new ArrayList<>();
    MethodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payments);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Make Payment");

        common=new Common(this);
        session=new SessionManager(this);

        tv_duration=(TextView)findViewById(R.id.tv_duration);
        tv_amount=(TextView)findViewById(R.id.tv_amount);
        tv_tax=(TextView)findViewById(R.id.tv_tax);
        tv_discount=(TextView)findViewById(R.id.tv_discount);
        tv_total=(TextView)findViewById(R.id.tv_total);
        tv_bank_label=(TextView)findViewById(R.id.tv_bank_label);
        tv_disc_label=(TextView)findViewById(R.id.tv_disc_label);
        tv_label_tex=(TextView)findViewById(R.id.tv_label_tex);//,
        lay_two=(LinearLayout)findViewById(R.id.lay_two);
        lay_code=(LinearLayout)findViewById(R.id.lay_code);

        et_code=(EditText) findViewById(R.id.et_code);
        btn_pay=(Button) findViewById(R.id.btn_pay);
        btn_redeem=(Button) findViewById(R.id.btn_redeem);
        lv_method=(NonScrollListView)findViewById(R.id.lv_method);
        lay_bank=(LinearLayout)findViewById(R.id.lay_bank);
        /*rad_grp=(RadioGroup) findViewById(R.id.rad_grp);
        rad_biz=(RadioButton)findViewById(R.id.rad_biz);
        common.setDrawableRightRadio(R.drawable.pay_biz,rad_biz);
        rad_payu=(RadioButton)findViewById(R.id.rad_payu);
        common.setDrawableRightRadio(R.drawable.payu,rad_payu);
        rad_paypal=(RadioButton)findViewById(R.id.rad_paypal);
        common.setDrawableRightRadio(R.drawable.pay_pal,rad_paypal);
        rad_cc=(RadioButton)findViewById(R.id.rad_cc);
        common.setDrawableRightRadio(R.drawable.cc_ave,rad_cc);*/

        tv_name=(TextView)findViewById(R.id.tv_name);

        Bundle b=getIntent().getExtras();
        if (b!=null){
            if (b.containsKey("plan_data")){

                try {
                    plan_data=new JSONObject(b.getString("plan_data"));
                    plan_name=plan_data.getString("plan_name");
                    plan_id=plan_data.getString("id");
                    tv_name.setText(plan_name.toUpperCase());
                    tv_duration.setText(plan_data.getString("plan_duration")+" Days");
                    tv_amount.setText(plan_data.getString("plan_amount_type")+" "+plan_data.getString("plan_amount"));
                    tv_discount.setText(plan_data.getString("plan_amount_type")+" 0");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("paymentnamre",plan_data+"  ");
            }
        }

        btn_redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=et_code.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    et_code.setError("Please enter code");
                    return;
                }
                checkCoupan(code);
            }
        });

        getData();

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String method="";
                for (int i=0;i<list.size();i++){
                    if (list.get(i).isSelected()){
                        method=list.get(i).getName();
                    }
                }
                if (method.isEmpty()){
                    common.showToast("Please select payment method first");
                    return;
                }

                Intent i=new Intent(getApplicationContext(),PaymentWebView.class);
                i.putExtra("Total_amount",String.valueOf(total_amt));
                i.putExtra("Method",method);
                i.putExtra("Plan_id",plan_id);
                i.putExtra("plan_name",plan_name);
                startActivity(i);

            }
        });

    }

    private void getPaymentMethod(){
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        common.makePostRequest(Utils.get_payment_method, new HashMap<String, String>(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        JSONArray plan_data=object.getJSONArray("plan_data");
                        for (int i=0;i<plan_data.length();i++){
                            JSONObject obj=plan_data.getJSONObject(i);
                            String name=obj.getString("name");
                            String logo=obj.getString("logo");
                            if (name.equals("BankDetails")){
                                lay_bank.setVisibility(View.VISIBLE);
                                tv_bank_label.setText(name);
                                tv_disc_label.setText(obj.getString("description"));
                            }else {
                                paymentItem item=new paymentItem(name,logo);
                                list.add(item);
                            }
                        }
                        adapter=new MethodAdapter(MakePaymentsActivity.this,list);
                        lv_method.setAdapter(adapter);
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

    private void getData() {
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        common.makePostRequest(Utils.site_data, new HashMap<String ,String >(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp",response);
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    JSONObject config_data=object.getJSONObject("config_data");
                    tax_applicable=config_data.getString("tax_applicable");
                    if (tax_applicable.equals("Yes")){
                        lay_two.setVisibility(View.VISIBLE);
                        service_tax=config_data.getString("service_tax");
                        float amt=Float.parseFloat(plan_data.getString("plan_amount"));
                        float tax_total=(amt*Float.parseFloat(service_tax))/100;

                        tv_tax.setText(plan_data.getString("plan_amount_type")+" "+tax_total);
                        total_amt=tax_total+amt;

                        tv_total.setText(plan_data.getString("plan_amount_type")+" "+new DecimalFormat("##.##").format(total_amt));
                        tv_label_tex.setText(config_data.getString("tax_name")+" ("+service_tax+"%)");

                    }else {
                        total_amt=Float.parseFloat(plan_data.getString("plan_amount"));
                        tv_total.setText(plan_data.getString("plan_amount_type")+" "+new DecimalFormat("##.##").format(total_amt));

                        lay_two.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
                getPaymentMethod();

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

    private void checkCoupan(final String code){
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String,String> param=new HashMap<>();
        param.put("user_id",session.getLoginData(SessionManager.KEY_USER_ID));
        try {
            param.put("plan_id",plan_data.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        param.put("couponcode",code);
        common.makePostRequest(Utils.check_coupan, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("message"));
                    if (object.getString("status").equals("success")){

                        String discount_amount=object.getString("discount_amount");

                        float amt=Float.parseFloat(plan_data.getString("plan_amount"));
                        float disc_total=amt-Float.parseFloat(discount_amount);
                        if (tax_applicable.equals("Yes")){
                            float tax_total=(disc_total*Float.parseFloat(service_tax))/100;
                            total_amt=tax_total+disc_total;
                            tv_tax.setText(plan_data.getString("plan_amount_type")+" "+tax_total);
                        }else {
                            total_amt=total_amt-Float.parseFloat(discount_amount);
                        }
                        tv_discount.setText(plan_data.getString("plan_amount_type")+" "+discount_amount+" ("+code+")");
                        tv_discount.setTextColor(Color.GREEN);

                        tv_total.setText(plan_data.getString("plan_amount_type")+" "+new DecimalFormat("##.##").format(total_amt));

                        et_code.setText("");
                        lay_code.setVisibility(View.GONE);

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

    public class MethodAdapter extends ArrayAdapter<paymentItem> {
        Context context;
        List<paymentItem> list;
        Common common;
        public MethodAdapter(Context context,List<paymentItem> list) {
            super(context, R.layout.method_item, list);
            this.context = context;
            this.list=list;
            common=new Common(context);
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.method_item, null, true);

            ImageView img_logo = (ImageView) rowView.findViewById(R.id.img_logo);
            TextView tv_name=(TextView) rowView.findViewById(R.id.tv_name);
            final RadioButton rad_method=(RadioButton)rowView.findViewById(R.id.rad_method);

            final paymentItem item=list.get(position);

            rad_method.setChecked(item.isSelected());
            tv_name.setText(item.getName());
            if (!item.getLogo().isEmpty())
                Picasso.get().load(item.getLogo()).into(img_logo);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i=0;i<list.size();i++){
                        if (i==position)
                            list.get(i).setSelected(true);
                        else
                            list.get(i).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });

            return rowView;
        }

    }

}
