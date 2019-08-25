package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.mega.matrimony.Custom.NonScrollListView;
import com.mega.matrimony.Model.PlanItem;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlanListActivity extends AppCompatActivity {

    private NonScrollListView lv_plan;
    List<PlanItem> list = new ArrayList<>();
    Plan_Adapter adapter;
    private Button btn_payment;
    SessionManager session;
    Common common;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payment Plan");

        common = new Common(this);
        session = new SessionManager(this);

        getPlanData();

        lv_plan = (NonScrollListView) findViewById(R.id.lv_plan);
        btn_payment = (Button) findViewById(R.id.btn_id);

        adapter = new Plan_Adapter(this, list);
        lv_plan.setAdapter(adapter);

        lv_plan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == position)
                        list.get(i).setIs_select(true);
                    else
                        list.get(i).setIs_select(false);
                }

                adapter.notifyDataSetChanged();
            }
        });

        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plan_amount = "";
                JSONObject object = null;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isIs_select()) {
                        plan_amount = list.get(i).getPrise();
                        object = list.get(i).getPlan_object();
                    }

                }
                if (object == null) {
                    Toast.makeText(getApplicationContext(), "Please Select Plan", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (plan_amount.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Please Contact To admin", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PlanListActivity.this, ContactUsActivity.class);
                    i.putExtra("page_tag", "form");
                    startActivity(i);
                } else {
                    Intent i = new Intent(PlanListActivity.this, MakePaymentsActivity.class);
                    i.putExtra("plan_data", object.toString());
                    startActivity(i);
                }

            }
        });

    }

    private void getPlanData() {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        common.makePostRequest(Utils.plan_list, new HashMap<String, String>(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {
                        JSONArray plan_data = object.getJSONArray("plan_data");
                        for (int i = 0; i < plan_data.length(); i++) {
                            JSONObject obj = plan_data.getJSONObject(i);
                            PlanItem item = new PlanItem();
                            item.setId(obj.getString("id"));
                            item.setPlan_name(obj.getString("plan_name"));
                            item.setCurrency(obj.getString("plan_amount_type"));
                            item.setPrise(obj.getString("plan_amount"));
                            item.setContact(obj.getString("plan_contacts"));
                            item.setDuration(obj.getString("plan_duration"));
                            item.setPlan_offers(obj.getString("plan_offers"));
                            item.setProfile_view(obj.getString("profile"));
                            item.setMessage(obj.getString("plan_msg"));
                            item.setPlan_object(obj);
                            item.setIs_select(false);

                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
            }
        }, error -> {
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });
    }

    private class Plan_Adapter extends ArrayAdapter<PlanItem> {

        Context context;
        List<PlanItem> list;
        int selectedPosition = 0;

        public Plan_Adapter(Context context, List<PlanItem> list) {
            super(context, R.layout.plan_item, list);
            this.context = context;
            this.list = list;
        }

        class ViewHolder {
            TextView tv_name, tv_prise, tv_profile_view, tv_contact, tv_duration, tv_message, tv_detail, tv_label_prise;
            RadioButton radio_plan;
        }

        public View getView(int position, View rowView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (rowView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.plan_item, null, true);
                viewHolder.tv_name = (TextView) rowView.findViewById(R.id.tv_name);
                viewHolder.tv_prise = (TextView) rowView.findViewById(R.id.tv_prise);
                viewHolder.tv_profile_view = (TextView) rowView.findViewById(R.id.tv_profile_view);
                viewHolder.tv_contact = (TextView) rowView.findViewById(R.id.tv_contact);
                viewHolder.tv_duration = (TextView) rowView.findViewById(R.id.tv_duration);
                viewHolder.tv_message = (TextView) rowView.findViewById(R.id.tv_message);
                viewHolder.tv_detail = (TextView) rowView.findViewById(R.id.tv_detail);
                viewHolder.tv_label_prise = (TextView) rowView.findViewById(R.id.tv_label_prise);
                viewHolder.radio_plan = (RadioButton) rowView.findViewById(R.id.radio_plan);

                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            viewHolder.radio_plan.setTag(position);

            PlanItem item = list.get(position);
            viewHolder.tv_name.setText(item.getPlan_name().toUpperCase());
            viewHolder.radio_plan.setChecked(item.isIs_select());
            viewHolder.tv_contact.setText(item.getContact());
            viewHolder.tv_prise.setText(item.getCurrency() + " " + item.getPrise());
            viewHolder.tv_label_prise.setText(item.getCurrency() + " " + item.getPrise());
            viewHolder.tv_profile_view.setText(item.getProfile_view());
            viewHolder.tv_duration.setText(item.getDuration() + " Days");
            viewHolder.tv_message.setText(item.getMessage());
            viewHolder.tv_detail.setText(item.getPlan_offers());
            //position == selectedPosition
           /* radio_plan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = (Integer)view.getTag();
                    notifyDataSetChanged();
                }
            });*/
            return rowView;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
