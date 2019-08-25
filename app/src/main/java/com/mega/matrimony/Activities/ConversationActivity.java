package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.mega.matrimony.Firebase.MyFirebaseMessagingService;
import com.mega.matrimony.Firebase.NotificationUtils;
import com.mega.matrimony.Model.ConversationItem;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationActivity extends AppCompatActivity {

    private TextView tv_page_title, tv_online;
    private EditText et_message;
    private ImageButton btn_send;
    private ListView lv_list;
    CircleImageView img_title_profile;
    String matri_id;
    ProgressDialog pd;
    SessionManager session;
    SwipeRefreshLayout swipe;
    Common common;
    List<ConversationItem> list = new ArrayList<>();
    ListAdapter adapter;
    boolean is_mark = false;
    Menu menu;
    //private NotificationBroadcastReceiver mNotificationBroadcastReceiver = null;
    private IntentFilter mIntentFilter = null;
    BroadcastReceiver receiver;
    JSONObject opposite_user_data;

    private int placeHolder,photoProtectPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_conversation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Message");

        common = new Common(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }


        //this.mNotificationBroadcastReceiver = new NotificationBroadcastReceiver();
        this.mIntentFilter = new IntentFilter(Utils.CHAT_TAG);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HandleData(intent);
            }
        };

        swipe = findViewById(R.id.swipe);
        lv_list = (ListView) findViewById(R.id.lv_list);
        img_title_profile = (CircleImageView) findViewById(R.id.img_title_profile);
        tv_page_title = (TextView) findViewById(R.id.tv_page_title);
        tv_online = (TextView) findViewById(R.id.tv_online);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        et_message = (EditText) findViewById(R.id.et_message);

        Bundle b = getIntent().getExtras();

        if (b != null) {
            // Log.e(MyFirebaseMessagingService.TAG,b.getParcelableArray("message").toString()+" Bundle");

            // if (b.containsKey("name")){
            //    tv_page_title.setText(b.getString("name"));
            //}

            if (b.containsKey("matri_id")) {
                matri_id = b.getString("matri_id");
                Log.e(MyFirebaseMessagingService.TAG, matri_id + " ");
            }
        }

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                getList();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = et_message.getText().toString().trim();
                if (!msg.isEmpty())
                    send_msg(msg);

                // Toast.makeText(getApplicationContext(), "Message send successful.", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new ListAdapter(this, list);
        lv_list.setAdapter(adapter);

        lv_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                // lv_list.setItemChecked(position,true);
                if (!list.get(position).isIs_checked()) {
                    is_mark = true;
                    list.get(position).setIs_checked(true);
                    menu.findItem(R.id.delete).setVisible(true);
                    menu.findItem(R.id.mark_all).setVisible(true);
                } else {
                    int sel = 0;
                    list.get(position).setIs_checked(false);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isIs_checked()) {
                            sel = sel + 1;
                            //is_mark=false;
                        }
                    }
                    if (sel == 0) {
                        is_mark = false;
                    }
                    if (!is_mark) {
                        menu.findItem(R.id.delete).setVisible(false);
                        menu.findItem(R.id.mark_all).setVisible(false);
                    }
                }
                adapter.notifyDataSetChanged();
                // Log.d("resp","checked "+position);
                return true;
            }
        });

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (is_mark) {
                    if (!list.get(position).isIs_checked()) {
                        is_mark = true;
                        list.get(position).setIs_checked(true);
                        menu.findItem(R.id.delete).setVisible(true);
                        menu.findItem(R.id.mark_all).setVisible(true);
                    } else {
                        int sel = 0;
                        list.get(position).setIs_checked(false);
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isIs_checked()) {
                                sel = sel + 1;
                            }
                        }
                        if (sel == 0) {
                            is_mark = false;
                        }

                        if (!is_mark) {
                            menu.findItem(R.id.delete).setVisible(false);
                            menu.findItem(R.id.mark_all).setVisible(false);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        getList();
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            if (b.containsKey("matri_id")) {
                if (!b.getString("matri_id").equals(matri_id)) {
                    matri_id = b.getString("matri_id");
                    // Log.e(MyFirebaseMessagingService.TAG,matri_id+" New");
                    if (is_mark) {
                        for (ConversationItem itm : list) {
                            itm.setIs_checked(false);
                        }
                        adapter.notifyDataSetChanged();
                        menu.findItem(R.id.delete).setVisible(false);
                        menu.findItem(R.id.mark_all).setVisible(false);
                        is_mark = false;
                    }
                    list.clear();
                    getList();
                }

            }
        }
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HandleData(intent);
        }
    }

    private void HandleData(Intent intent) {

        // String message = intent.getStringExtra("message");
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("message");
        if (hashMap != null) {
            if (hashMap.containsKey("data_msg")) {
                String message = hashMap.get("data_msg");
                try {
                    JSONObject obj = new JSONObject(message);
                    //Log.e("resp","chat   "+obj);
                    if (obj.getString("sender").equals(matri_id)) {
                        //  Log.e("resp","chat   "+obj);
                        ConversationItem item = new ConversationItem();
                        item.setId(obj.getString("id"));
                        item.setIs_checked(false);
                        item.setSender(obj.getString("sender"));
                        item.setReceiver(obj.getString("receiver"));
                        item.setContent(obj.getString("content"));
                        item.setSent_on(getDate(obj.getString("sent_on")));
                        item.setIs_sent_receive("receive");
                        item.setPhoto_url(opposite_user_data.getString("photo_url"));
                        list.add(item);
                        adapter.notifyDataSetChanged();
                        //lv_list.smoothScrollToPosition(adapter.getCount());
                        lv_list.setSelection(adapter.getCount() - 1);
                        NotificationUtils.clearNotifications(this);
                    } else {
                        //Intent i=new Intent(getApplicationContext(),ConversationActivity.class);
                        //i.putExtra("matri_id",obj.getString("sender"));
                        //startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.conversation_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, this.mIntentFilter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    private void getList() {
        pd = new ProgressDialog(ConversationActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("other_id", matri_id);

        common.makePostRequest(Utils.conversation, param, response -> {
            if (pd != null)
                pd.dismiss();
            swipe.setRefreshing(false);
            //Log.d("resp",response);
            try {
                JSONObject object = new JSONObject(response);
                opposite_user_data = object.getJSONObject("opposite_user_data");
                tv_page_title.setText(opposite_user_data.getString("matri_id"));
                if (!opposite_user_data.getString("photo_url").equals("")) {
                    Picasso.get().load(opposite_user_data.getString("photo_url")).into(img_title_profile);
                } else {
                    img_title_profile.setImageResource(R.drawable.placeholder);
                }

                String logged_in = opposite_user_data.getString("logged_in");

                if (logged_in.equals("1")) {
                    //Log.d("resp","logged_in    "+logged_in);
                    tv_online.setVisibility(View.VISIBLE);
                } else {
                    tv_online.setVisibility(View.GONE);
                }
                //Log.d("resp",object.getString("status")+" ");
                if (object.getString("status").equals("success")) {
                    int total_count = object.getInt("total_count");
                    //Log.d("resp",total_count+"");
                    if (total_count != 0) {
                        JSONArray data = object.getJSONArray("data");
                        //Log.d("resp",data.toString());
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            ConversationItem item = new ConversationItem();
                            item.setId(obj.getString("id"));
                            item.setIs_checked(false);
                            item.setSender(obj.getString("sender"));
                            item.setReceiver(obj.getString("receiver"));
                            item.setContent(obj.getString("content"));
                            item.setSent_on(getDate(obj.getString("sent_on")));
                            item.setIs_sent_receive(obj.getString("is_sent_receive"));
                            item.setPhoto_url(obj.getString("photo_url"));
                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        //lv_list.smoothScrollToPosition(adapter.getCount());
                        lv_list.setSelection(adapter.getCount() - 1);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }
        }, error -> {
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });

    }

    private void send_msg(String msg) {
        pd = new ProgressDialog(ConversationActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("msg_status", "sent");
        param.put("message", msg);
        param.put("receiver_id", matri_id);
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequest(Utils.send_message, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {
                        et_message.setText("");
                        //et_message.setFocusable(false);
                        list.clear();
                        getList();

                    } else {
                        common.showToast(object.getString("error_message"));
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

    class ListAdapter extends BaseAdapter {
        List<ConversationItem> list;

        Context context;
        LayoutInflater inflter;

        public ListAdapter(Context context, List<ConversationItem> list) {
            this.list = list;
            this.context = context;
            inflter = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public ConversationItem getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {

            ConversationItem item = list.get(position);
            if (item.getIs_sent_receive().equals("receive")) {
                view = inflter.inflate(R.layout.conversation_other, viewGroup, false);
            } else {
                view = inflter.inflate(R.layout.conversation_me, viewGroup, false);
            }
            if (item.isIs_checked()) {
                view.setBackgroundColor(getResources().getColor(R.color.mark_background));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.unmark_background));
            }
            CircleImageView img_profile = (CircleImageView) view.findViewById(R.id.img_profile);
            TextView tv_date = (TextView) view.findViewById(R.id.tv_date);
            TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);

            if (!item.getPhoto_url().equals(""))
                Picasso.get().load(item.getPhoto_url()).into(img_profile);

            tv_msg.setText(Html.fromHtml(item.getContent()));
            tv_date.setText(item.getSent_on());

            return view;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }


    }

    private String getDate(String time) {
        String outputPattern = "MMM dd, yyyy";//HH:mm a
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String nowPatern = "HH:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        SimpleDateFormat nowFormat = new SimpleDateFormat(nowPatern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            if (DateUtils.isToday(date.getTime())) {
                str = nowFormat.format(date);//"Today "+
            } else {
                str = outputFormat.format(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (is_mark) {
                for (ConversationItem itm : list) {
                    itm.setIs_checked(false);
                }
                adapter.notifyDataSetChanged();
                menu.findItem(R.id.delete).setVisible(false);
                menu.findItem(R.id.mark_all).setVisible(false);
                is_mark = false;
            } else {
                onBackPressed();
            }

        } else if (id == R.id.delete) {
            deleteMsg();
        } else if (id == R.id.mark_all) {
            for (ConversationItem itm : list) {
                itm.setIs_checked(true);
            }
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (is_mark) {
            for (ConversationItem itm : list) {
                itm.setIs_checked(false);
            }
            adapter.notifyDataSetChanged();
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.mark_all).setVisible(false);
            is_mark = false;
        } else {
            startActivity(new Intent(getApplicationContext(), QuickMessageActivity.class));
            finish();
        }
        //super.onBackPressed();
    }

    private void deleteMsg() {
        String selectedId = "";
        for (ConversationItem itm : list) {
            if (itm.isIs_checked())
                selectedId += itm.getId() + ",";
        }
        selectedId = selectedId.replaceAll(",$", "");
        Log.d("resp", selectedId);
        if (!selectedId.equals(""))
            deleteConApi(selectedId);
    }

    private void deleteConApi(final String selectedId) {
        pd = new ProgressDialog(ConversationActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("selected_val", selectedId);
        param.put("status", "delete");
        param.put("mode", "inbox");

        common.makePostRequest(Utils.delete_msg, param, response -> {
            if (pd != null)
                pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
               /* String [] arr=selectedId.split(",");
                for (int i=0;i<list.size();i++){
                    ConversationItem itm=list.get(i);
                    for (String a:arr){
                        if (itm.getId().equals(a)){
                            list.remove(i);
                        }
                    }
                }
                adapter.notifyDataSetChanged();*/
                    is_mark = false;
                    menu.findItem(R.id.delete).setVisible(false);
                    menu.findItem(R.id.mark_all).setVisible(false);
                    list.clear();
                    getList();
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }
        }, error -> {
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });

    }

}
