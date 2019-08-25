package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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


public class MessageSentFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    RecyclerView recycler_sent;
    EditText search_view;
    ProgressDialog pd;
    Context context;
    Common common;
    SessionManager session;
    boolean continue_request;
    List<Message_item> list=new ArrayList<>();
    MessageAdapter adapter;
    int page=0;
    TextView tv_no_data;
    ConstraintLayout lay_const;

    public MessageSentFragment() {
        // Required empty public constructor
    }

    public static MessageSentFragment newInstance(String param1, String param2) {
        MessageSentFragment fragment = new MessageSentFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_message_sent, container, false);

        search_view=(EditText)view.findViewById(R.id.search_view);
        recycler_sent=(RecyclerView)view.findViewById(R.id.recycler_sent);
        tv_no_data=(TextView) view.findViewById(R.id.tv_no_data);
        lay_const=(ConstraintLayout)view.findViewById(R.id.lay_const);

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
        recycler_sent.setLayoutManager(mLayoutManager);
        recycler_sent.setItemAnimator(new DefaultItemAnimator());
        recycler_sent.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(getActivity(), list);
        recycler_sent.setAdapter(adapter);

        page=page+1;
        getMessage(page);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_sent);

        recycler_sent.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
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
        param.put("mode","sent");
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        param.put("selected_val",list.get(position).getId());

        common.makePostRequest(Utils.update_status, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             //   Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("status").equals("success")){
                        //list.remove(position);
                        adapter.removeItem(position);
                        common.showAlert("Delete",object.getString("error_meessage"),R.drawable.trash_red);
                        //adapter.remove(list.get(position));
                        if (list.size()==0){
                            tv_no_data.setVisibility(View.VISIBLE);
                            recycler_sent.setVisibility(View.GONE);
                        }
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

    private void getMessage(int page){
        pd=new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        HashMap<String,String> param=new HashMap<>();
        param.put("mode","sent");
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
                        recycler_sent.setVisibility(View.VISIBLE);
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
                                item.setDisplay_name(obj.getString("receiver"));
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
                        recycler_sent.setVisibility(View.GONE);
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

    public class Message_Adapter extends ArrayAdapter<Message_item> {// implements CompoundButton.OnCheckedChangeListener

        Context context;
        private List<Message_item> list= null;
        private List<Message_item> filteredData;
        //private ItemFilter mFilter = new ItemFilter();

        public Message_Adapter(Context context, List<Message_item> list) {//,CheckBox check_all
            super(context, R.layout.message_item, list);
            this.context = context;
            this.list=list;
            this.filteredData = new ArrayList<Message_item>();
            this.filteredData.addAll(list);
            // Log.d("resp",this.filteredData.size()+" Message_Adapter "+list.size());
        }

        public View getView(final int position, View rowView, ViewGroup parent) {
            Holder holder;
            if (rowView == null) {
                holder = new Holder();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.message_item, null, true);

                holder.tv_name=(TextView)rowView.findViewById(R.id.tv_name);
                holder.tv_msg=(TextView)rowView.findViewById(R.id.tv_msg);
                holder.tv_date=(TextView)rowView.findViewById(R.id.tv_date);
                // holder.tv_count=(TextView)rowView.findViewById(R.id.tv_count);
                holder.img_profile=(ImageView) rowView.findViewById(R.id.img_profile);
                rowView.setTag(holder);
            }else {
                holder = (Holder) rowView.getTag();
            }

            final Message_item item=list.get(position);

            holder.tv_name.setText(item.getReceiver());
            String msg="";
            if (item.getContent().length()>25){
                msg=item.getContent().substring(0, 25)+"...<font color=#a30412>Read more</font>";
            }else {
                msg=item.getContent();
            }
            holder.tv_msg.setText(Html.fromHtml(msg));
            holder.tv_date.setText(item.getSent_on());

            return rowView;
        }

        class Holder {
            TextView tv_name,tv_msg,tv_date,tv_count;
            ImageView img_profile;
            //  CheckBox chk_mark;
        }

        void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            list.clear();
            if (charText.length() == 0) {
                list.addAll(filteredData);
                // Log.d("resp",list.size()+" if "+charText+" filteredData  "+filteredData.size());
            }else {
                // Log.d("resp",list.size()+" else "+charText);
                for (Message_item wp : filteredData) {
                    if (wp.getSender().toLowerCase(Locale.getDefault()).contains(charText)) {
                        list.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

}
