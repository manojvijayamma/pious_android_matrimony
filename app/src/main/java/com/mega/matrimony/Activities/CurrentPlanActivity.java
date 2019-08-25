package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Model.CurrentPlanItem;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CurrentPlanActivity extends AppCompatActivity {

    private Button btn_upgrade;
    private TextView tv_no_data,tv_prise,tv_active_on,tv_exp_on,tv_duration,tv_message,tv_contact,tv_name;
   // private CardView lay_card;
    List<CurrentPlanItem> list=new ArrayList<>();
    currentAdapter adapter;
    SessionManager session;
    Common common;
    ProgressDialog pd;
    ListView lv_plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_plan);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Current Plan");

        session=new SessionManager(this);
        common=new Common(this);

        lv_plan=(ListView)findViewById(R.id.lv_plan);

        btn_upgrade=(Button)findViewById(R.id.btn_upgrade);

        //lay_card=(CardView)findViewById(R.id.lay_card);
        tv_no_data=(TextView)findViewById(R.id.tv_no_data);

        btn_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CurrentPlanActivity.this, PlanListActivity.class));
            }
        });

        adapter=new currentAdapter(this,list);
        lv_plan.setAdapter(adapter);

        getPlanData();

    }

    private void getPlanData() {
        pd=new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
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
                    if (object.getString("status").equals("success")){
                        lv_plan.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                        JSONArray data=object.getJSONArray("data");
                        for (int i=0;i<data.length();i++){
                            JSONObject obj=data.getJSONObject(i);

                            CurrentPlanItem item=new CurrentPlanItem();
                            item.setPlan_name(obj.getString("plan_name").toUpperCase());
                            item.setCurrency(obj.getString("currency"));
                            item.setPlan_amount(obj.getString("plan_amount"));
                            item.setPlan_activated(changeDate(obj.getString("plan_activated"),"MMM dd, yyyy"));
                            item.setPlan_expired(changeDate(obj.getString("plan_expired"),"MMM dd, yyyy"));
                            item.setPlan_duration(obj.getString("plan_duration")+" Days");

                            item.setMessage_used(obj.getString("message_used"));
                            item.setMessage(obj.getString("message"));

                            item.setContacts(obj.getString("contacts"));
                            item.setContacts_used(obj.getString("contacts_used"));

                            item.setChat(obj.getString("chat"));
                            item.setProfile(obj.getString("profile"));
                            item.setProfile_used(obj.getString("profile_used"));
                            item.setTax_percentage(obj.getString("tax_percentage"));
                            item.setTax_name(obj.getString("tax_name"));
                            item.setGrand_total(obj.getString("grand_total"));
                            item.setTax_amount(obj.getString("tax_amount"));
                            item.setDiscount_amount(obj.getString("discount_amount"));

                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();

                    }else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        lv_plan.setVisibility(View.GONE);
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

    public String changeDate(String time,String outputPattern) {

        //String outputPattern = "h:mm a,dd MMMM, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat( "yyyy-MM-dd");
        // inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        // TimeZone tz = TimeZone.getTimeZone(session.getLoginData(SessionManager.TIME_ZONE));
        // outputFormat.setTimeZone(tz);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    class currentAdapter extends ArrayAdapter<CurrentPlanItem>{
        Context context;
        List<CurrentPlanItem> list;
        public currentAdapter(Context context, List<CurrentPlanItem> list) {
            super(context, R.layout.current_plan_item, list);
            this.context=context;
            this.list=list;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.current_plan_item, null, true);

            TextView tv_prise=(TextView)convertView.findViewById(R.id.tv_prise);
            TextView tv_active_on=(TextView)convertView.findViewById(R.id.tv_active_on);
            TextView tv_exp_on=(TextView)convertView.findViewById(R.id.tv_exp_on);
            TextView tv_duration=(TextView)convertView.findViewById(R.id.tv_duration);
            TextView tv_message=(TextView)convertView.findViewById(R.id.tv_message);
            TextView tv_contact=(TextView)convertView.findViewById(R.id.tv_contact);
            TextView tv_name=(TextView)convertView.findViewById(R.id.tv_name);

            RelativeLayout lay_gst=(RelativeLayout)convertView.findViewById(R.id.lay_gst);

            TextView tv_chat=(TextView)convertView.findViewById(R.id.tv_chat);
            TextView tv_pro=(TextView)convertView.findViewById(R.id.tv_pro);
            TextView tv_gst=(TextView)convertView.findViewById(R.id.tv_gst);
            TextView tv_t_amt=(TextView)convertView.findViewById(R.id.tv_t_amt);
            TextView gst_label=(TextView)convertView.findViewById(R.id.gst_label);
            TextView tv_disc=(TextView)convertView.findViewById(R.id.tv_disc);

            CurrentPlanItem item=list.get(position);

            tv_name.setText(item.getPlan_name());
            tv_chat.setText(item.getChat());

            int pro_use=Integer.parseInt(item.getProfile_used());
            int pro_t=Integer.parseInt(item.getProfile());
            int avl_pro=pro_t-pro_use;
            String pro=avl_pro+" out of "+item.getProfile();
            tv_pro.setText(pro);

            if (item.getDiscount_amount().equals("0")){
                tv_disc.setText("N/A");
            }else {
                tv_disc.setText(item.getCurrency()+" "+item.getDiscount_amount());
            }


            String gst=item.getCurrency()+" "+item.getTax_amount();
            tv_gst.setText(gst);

            String tamt=item.getCurrency()+" "+item.getGrand_total();
            tv_t_amt.setText(tamt);

            if (item.getTax_name().equals("")){
                lay_gst.setVisibility(View.GONE);
            }else {
                lay_gst.setVisibility(View.VISIBLE);
                String gst_lbl=item.getTax_name()+" ("+item.getTax_percentage()+"%)";
                gst_label.setText(gst_lbl);
            }

            String prise=item.getCurrency()+" "+item.getPlan_amount();
            tv_prise.setText(prise);
            tv_active_on.setText(item.getPlan_activated());
            tv_exp_on.setText(item.getPlan_expired());
            tv_duration.setText(item.getPlan_duration());

            int msg_use=Integer.parseInt(item.getMessage_used());
            int msg_t=Integer.parseInt(item.getMessage());
            int avl_msg=msg_t-msg_use;
            String msg=avl_msg+" out of "+item.getMessage();
            tv_message.setText(msg);

            int cont_use=Integer.parseInt(item.getContacts_used());
            int cont_t=Integer.parseInt(item.getContacts());
            int avl_cont=cont_t-cont_use;
            String cont=avl_cont+" out of "+item.getContacts();
            tv_contact.setText(cont);

            return convertView;
        }
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
