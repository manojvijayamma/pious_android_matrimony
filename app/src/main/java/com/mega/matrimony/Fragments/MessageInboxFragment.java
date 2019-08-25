package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Adapter.MessageAdapter;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Custom.EndlessRecyclerViewScrollListener;
import com.mega.matrimony.Model.Message_item;
import com.mega.matrimony.R;
import com.mega.matrimony.Custom.RecyclerItemTouchHelper;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessageInboxFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    //ListView lv_inbox;
    RecyclerView recycler_inbox;
    EditText search_view;
    ProgressDialog pd;
    Context context;
    Common common;
    SessionManager session;
    boolean continue_request;
    List<Message_item> list=new ArrayList<>();
    //Message_Adapter adapter;
    MessageAdapter adapter;
    int page=0;
    TextView tv_no_data;

    public MessageInboxFragment() {
        // Required empty public constructor
    }
    
    public static MessageInboxFragment newInstance(String param1, String param2) {
        MessageInboxFragment fragment = new MessageInboxFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context=getActivity();
        common=new Common(context);
        session=new SessionManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_message_inbox, container, false);

        search_view=(EditText)view.findViewById(R.id.search_view);
        recycler_inbox=(RecyclerView)view.findViewById(R.id.recycler_inbox);
        tv_no_data=(TextView) view.findViewById(R.id.tv_no_data);
       // lay_const=(ConstraintLayout)view.findViewById(R.id.lay_const);

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

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_inbox.setLayoutManager(mLayoutManager);
        recycler_inbox.setItemAnimator(new DefaultItemAnimator());
        recycler_inbox.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(getActivity(), list);
        recycler_inbox.setAdapter(adapter);

        page=page+1;
        getMessage(page);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_inbox);

        recycler_inbox.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int pag, int totalItemsCount, RecyclerView view) {
                // Log.d("timezone",String.valueOf(pag));
                if (continue_request){
                    page=page+1;
                    getMessage(page);
                }
            }
        });

        return view;
    }

    private void deletemessge(final int position){
        HashMap<String,String> param=new HashMap<>();
        param.put("status","delete");
        param.put("mode","inbox");
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        param.put("selected_val",list.get(position).getId());

        common.makePostRequest(Utils.update_status, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        //list.remove(position);
                        adapter.removeItem(position);
                        common.showAlert("Delete",object.getString("error_meessage"),R.drawable.trash_red);
                        //adapter.remove(list.get(position));
                        if (list.size()==0){
                            tv_no_data.setVisibility(View.VISIBLE);
                            //lv_inbox.setVisibility(View.GONE);
                            recycler_inbox.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    private void getMessage(int page){
        pd=new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        HashMap<String,String> param=new HashMap<>();
        param.put("mode","inbox");
        param.put("page_number",String.valueOf(page));
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequest(Utils.message_list, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    continue_request=object.getBoolean("continue_request");
                    int total_count=object.getInt("total_count");
                    if (total_count!=0){
                        tv_no_data.setVisibility(View.GONE);
                        recycler_inbox.setVisibility(View.VISIBLE);
                        //lv_inbox.setVisibility(View.VISIBLE);
                        if (total_count!=list.size()){

                            JSONArray data=object.getJSONArray("data");
                            for (int i=0;i<data.length();i++){
                                JSONObject obj=data.getJSONObject(i);
                                Message_item item=new Message_item();

                                item.setId(obj.getString("id"));
                                item.setContent(obj.getString("content"));
                                item.setRead_status(obj.getString("read_status"));
                                item.setReceiver(obj.getString("receiver"));
                                item.setReceiver_delete(obj.getString("receiver_delete"));
                                item.setSender(obj.getString("sender"));
                                item.setDisplay_name(obj.getString("sender"));
                                item.setSender_delete(obj.getString("sender_delete"));
                                item.setSent_on(common.changeDate(obj.getString("sent_on"),"MMM dd,yy hh:mm a"));
                                item.setSubject(obj.getString("subject"));
                                if (obj.getJSONArray("member_photo").length()!=0){
                                    String url=obj.getJSONArray("member_photo").getJSONObject(0).getString("photo1");
                                    item.setImage(url);
                                }else
                                    item.setImage("");
                                list.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        recycler_inbox.setVisibility(View.GONE);
                        //lv_inbox.setVisibility(View.GONE);
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
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MessageAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = list.get(viewHolder.getAdapterPosition()).getSender();

            // backup of removed item for undo purpose
            final Message_item deletedItem = list.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            AlertDialog.Builder alert=new AlertDialog.Builder(context);
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
