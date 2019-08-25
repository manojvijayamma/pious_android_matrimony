package com.mega.matrimony.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mega.matrimony.Custom.EndlessRecyclerViewScrollListener;
import com.mega.matrimony.Custom.RecyclerItemTouchHelper;
import com.mega.matrimony.Model.QuickItem;
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
import java.util.Locale;

public class QuickMessageActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    RecyclerView recycler_inbox;
    EditText search_view;
    ProgressDialog pd;
    SwipeRefreshLayout swipe;
    Common common;
    SessionManager session;
    boolean continue_request;
    boolean isFirst = true;
    List<QuickItem> list = new ArrayList<>();
    //Message_Adapter adapter;
    MessageAdapter adapter;
    int page = 0;
    TextView tv_no_data;
    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    //private NotificationBroadcastReceiver mNotificationBroadcastReceiver = null;
    BroadcastReceiver receiver;
    private IntentFilter mIntentFilter = null;

    private int placeHolder,photoProtectPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_message);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message");

        session = new SessionManager(this);
        common = new Common(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        search_view = (EditText) findViewById(R.id.search_view);
        recycler_inbox = (RecyclerView) findViewById(R.id.recycler_inbox);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);

        // this.mNotificationBroadcastReceiver = new NotificationBroadcastReceiver();
        this.mIntentFilter = new IntentFilter(Utils.OUICK_TAG);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("body")) {
                Log.e("resp", b.getString("body") + "  ");
            }
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HandleData(intent);
            }
        };

        search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (adapter!=null){
                String text = search_view.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);
                // }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_inbox.setLayoutManager(mLayoutManager);
        recycler_inbox.setItemAnimator(new DefaultItemAnimator());
        recycler_inbox.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(getApplicationContext(), list);
        recycler_inbox.setAdapter(adapter);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                page = 0;
                page = page + 1;
                getMessage(page);
            }
        });

        page = page + 1;
        getMessage(page);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_inbox);

        recycler_inbox.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int pag, int totalItemsCount, RecyclerView view) {
                // Log.d("timezone",String.valueOf(pag));
                if (continue_request) {
                    page = page + 1;
                    getMessage(page);
                }
            }
        });
    }

    private void HandleData(Intent intent) {
        //HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("message");
        // Log.e(MyFirebaseMessagingService.TAG,"receive");
//        if (hashMap!=null){
//            if (hashMap.containsKey("data_msg")){
//                String message=hashMap.get("data_msg");
//                try {
//                    JSONObject obj=new JSONObject(message);
//                    Log.e("resp","chat   "+obj);
//                    if (obj.getString("sender").equals(matri_id)){
//                        //  Log.e("resp","chat   "+obj);
//                        QuickItem item=new QuickItem();
//                        //item.setId(obj.getString("id"));
//                        item.setId(obj.getString("id"));
//                        item.setOtherID(obj.getString("otherID"));
//                        item.setContent(obj.getString("content"));
//                        item.setSent_on(getDate(obj.getString("sent_on")));
//                        item.setUnread_count(obj.getString("unread_count"));
//                        item.setPhoto_url(obj.getString("photo_url"));
//
//                        NotificationUtils.clearNotifications(this);
//                    }else {
//                        //Intent i=new Intent(getApplicationContext(),ConversationActivity.class);
//                        //i.putExtra("matri_id",obj.getString("sender"));
//                        //startActivity(i);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        // NotificationUtils.clearNotifications(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            list.clear();
            page = 0;
            page = page + 1;
            getMessage(page);
        }
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

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> implements Filterable {
        private Context context;
        private List<QuickItem> list = null;
        private List<QuickItem> contactListFiltered;

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        contactListFiltered = list;
                    } else {
                        List<QuickItem> filteredList = new ArrayList<>();
                        for (QuickItem row : list) {
                            if (row.getOtherID().toLowerCase().contains(charString.toLowerCase()) || row.getContent().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        }
                        contactListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = contactListFiltered;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    contactListFiltered = (ArrayList<QuickItem>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_name, tv_msg, tv_date, tv_count;
            public ImageView img_profile;
            public RelativeLayout viewBackground, viewForeground;

            public MyViewHolder(View view) {
                super(view);
                tv_name = view.findViewById(R.id.tv_name);
                tv_msg = view.findViewById(R.id.tv_msg);
                tv_date = view.findViewById(R.id.tv_date);
                tv_count = view.findViewById(R.id.tv_count);
                img_profile = view.findViewById(R.id.img_profile);
                viewBackground = view.findViewById(R.id.view_background);
                viewForeground = view.findViewById(R.id.view_foreground);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_count.setVisibility(View.GONE);
                        Intent i = new Intent(context, ConversationActivity.class);
                        i.putExtra("matri_id", list.get(getPosition()).getOtherID());
                        startActivity(i);
                    }
                });
            }
        }

        public MessageAdapter(Context context, List<QuickItem> list) {
            this.context = context;
            this.list = list;
            this.contactListFiltered = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final QuickItem item = contactListFiltered.get(position);

            holder.tv_name.setText(item.getOtherID());
            if (!item.getUnread_count().equals("0"))
                holder.tv_count.setText(item.getUnread_count());
            else
                holder.tv_count.setVisibility(View.GONE);
            String msg = "";
            if (item.getContent().length() >= 20) {
                msg = item.getContent().substring(0, 20) + "...<font color=#a30412>Read more</font>";
            } else {
                msg = item.getContent();
            }
            holder.tv_msg.setText(Html.fromHtml(msg));
            holder.tv_date.setText(item.getSent_on());
            if (!item.getPhoto_url().equals(""))
                Picasso.get().load(item.getPhoto_url()).into(holder.img_profile);
            else
                holder.img_profile.setImageResource(R.drawable.placeholder);

            /*holder.img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,ConversationActivity.class);
                    i.putExtra("matri_id",item.getOtherID());
                    startActivity(i);
                }
            });
            holder.tv_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,ConversationActivity.class);
                    i.putExtra("matri_id",item.getOtherID());
                    startActivity(i);
                }
            });
            holder.tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,ConversationActivity.class);
                    i.putExtra("matri_id",item.getOtherID());
                    startActivity(i);
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return contactListFiltered.size();
        }

        public void removeItem(int position) {
            contactListFiltered.remove(position);
            notifyItemRemoved(position);
        }

        public void restoreItem(QuickItem item, int position) {
            contactListFiltered.add(position, item);
            // notify item added by position
            notifyItemInserted(position);
        }
    }

    private void deletemessge(final int position) {
        pd = new ProgressDialog(QuickMessageActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("other_id", list.get(position).getOtherID());

        common.makePostRequest(Utils.delete_user_message, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {
                        //list.remove(position);
                        adapter.removeItem(position);
                        common.showAlert("Delete", object.getString("message"), R.drawable.trash_red);
                        //adapter.remove(list.get(position));
                        if (list.size() == 0) {
                            tv_no_data.setVisibility(View.VISIBLE);
                            //lv_inbox.setVisibility(View.GONE);
                            recycler_inbox.setVisibility(View.GONE);
                        }
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

    private void getMessage(int page) {
        pd = new ProgressDialog(QuickMessageActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("page_number", String.valueOf(page));
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequestTime(Utils.newmessage_list, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                swipe.setRefreshing(false);
                isFirst = false;
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    continue_request = object.getBoolean("continue_request");
                    int total_count = object.getInt("total_count");
                    if (total_count != 0) {
                        tv_no_data.setVisibility(View.GONE);
                        recycler_inbox.setVisibility(View.VISIBLE);
                        //lv_inbox.setVisibility(View.VISIBLE);
                        if (total_count != list.size()) {

                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                QuickItem item = new QuickItem();

                                item.setId(obj.getString("id"));
                                item.setOtherID(obj.getString("otherID"));
                                item.setContent(obj.getString("content"));
                                item.setSent_on(getDate(obj.getString("sent_on")));
                                item.setUnread_count(obj.getString("unread_count"));
                                item.setPhoto_url(obj.getString("photo_url"));
                                list.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        recycler_inbox.setVisibility(View.GONE);
                        //lv_inbox.setVisibil+ity(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
            }
        }, error -> {
            isFirst = false;
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });

    }

    private String getDate(String time) {
        String outputPattern = "dd MMM yyyy hh:mm a";
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
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MessageAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            //String name = list.get(viewHolder.getAdapterPosition()).getSender();

            // backup of removed item for undo purpose
            final QuickItem deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            AlertDialog.Builder alert = new AlertDialog.Builder(QuickMessageActivity.this);
            //alert.setTitle("Delete Message");
            alert.setMessage("Are you sure you want to delete this message?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deletemessge(deletedIndex);
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    adapter.removeItem(viewHolder.getAdapterPosition());
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            alert.show();

            // remove the item from recycler view
            // adapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
           /* Snackbar snackbar = Snackbar
                    .make(lay_const, name + " removed", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();*/
        }
    }
}
