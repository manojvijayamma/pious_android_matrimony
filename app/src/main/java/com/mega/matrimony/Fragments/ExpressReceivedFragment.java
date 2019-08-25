package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Activities.OtherUserProfileActivity;
import com.mega.matrimony.Activities.PlanListActivity;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.Model.ExpressItem;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpressReceivedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lv_exp_receive;
    TextView tv_no_data;
    Spinner spin_exp_recv;
    Context context;
    List<ExpressItem> list = new ArrayList<>();
    Received_Adapter adapter;
    Common common;
    SessionManager session;
    ProgressDialog pd;
    boolean continue_request;
    int page = 0;
    String tag = "all_receive";
    private boolean isCreate = false;
    private int placeHolder,photoProtectPlaceHolder;


    public ExpressReceivedFragment() {
        // Required empty public constructor
    }

    public static ExpressReceivedFragment newInstance(String param1, String param2) {
        ExpressReceivedFragment fragment = new ExpressReceivedFragment();
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
        common = new Common(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();
        isCreate = true;

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_express_received, container, false);
        context = getActivity();


        lv_exp_receive = (ListView) view.findViewById(R.id.lv_exp_receive);
        spin_exp_recv = (Spinner) view.findViewById(R.id.spin_exp_recv);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);

        // initData("All");

        List<String> lst = new ArrayList<>();
        lst.add("All Received Interest");
        lst.add("Interest Received Accept");
        lst.add("Interest Received Reject");
        lst.add("Interest Received Pending");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, lst);
        spin_exp_recv.setAdapter(arrayAdapter);

        spin_exp_recv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                page = 0;
                page = page + 1;
                continue_request = true;
                list.clear();
                switch (position) {
                    case 0:
                        getData(page, "all_receive");
                        tag = "all_receive";
                        break;
                    case 1:
                        getData(page, "accept_receive");
                        tag = "accept_receive";
                        break;
                    case 2:
                        getData(page, "reject_receive");
                        tag = "reject_receive";
                        break;
                    case 3:
                        getData(page, "pending_receive");
                        tag = "pending_receive";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lv_exp_receive.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    if (continue_request) {
                        page = page + 1;
                        getData(page, tag);
                        if (pd != null)
                            pd.dismiss();
                    }

                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

        //page=page+1;
        //getData(page,tag);

        adapter = new Received_Adapter(getActivity(), list);
        lv_exp_receive.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // Log.d("frgchang",isVisibleToUser+"   ");
        if (isVisibleToUser && isCreate) {
//            if (pd!=null)
//                pd.dismiss();
//            list.clear();
//            adapter.notifyDataSetChanged();
//            page=0;
//            page=page+1;
//            getData(page,tag);
        }

        //super.setUserVisibleHint(isVisibleToUser);
    }

    private void getData(int page, String tag) {
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("exp_status", tag);

        common.makePostRequest(Utils.express_interest + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    int total_count = object.getInt("total_count");
                    continue_request = object.getBoolean("continue_request");

                    if (total_count != 0) {
                        tv_no_data.setVisibility(View.GONE);
                        lv_exp_receive.setVisibility(View.VISIBLE);
                        if (total_count != list.size()) {
                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                ExpressItem item = new ExpressItem();
                                item.setId(obj.getString("id"));
                                item.setMatri_id(obj.getString("matri_id"));
                                item.setUser_id(obj.getString("user_id"));

                                item.setAbout(obj.getString("message"));
                                item.setReceiver_response(obj.getString("receiver_response"));
                                item.setImage(obj.getString("photo1"));
                                item.setImage_approval(obj.getString("photo1_approve"));
                                item.setPhoto_view_status(obj.getString("photo_view_status"));
                                item.setPhoto_view_count(obj.getString("photo_view_count"));
                                list.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        lv_exp_receive.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
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

    private String checkData(String text) {
        if (!text.equals("") && !text.equals("null")) {
            return text + " , ";
        }
        return "";
    }

    private class Received_Adapter extends ArrayAdapter<ExpressItem> {

        Context context;
        List<ExpressItem> list;

        public Received_Adapter(Context context, List<ExpressItem> list) {
            super(context, R.layout.express_item, list);
            this.context = context;
            this.list = list;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.express_recieved_item, null, true);

            TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
            TextView tv_status = (TextView) rowView.findViewById(R.id.tv_status);
            TextView tv_about = (TextView) rowView.findViewById(R.id.tv_about);

            // TextView tv_accept_label=(TextView)rowView.findViewById(R.id.tv_accept_label);
            //TextView tv_decline_label=(TextView)rowView.findViewById(R.id.tv_decline_label);
            TextView btnDelete = rowView.findViewById(R.id.btnDelete);
//            btnDelete.setVisibility(View.GONE);

            //LinearLayout lay_btn=(LinearLayout)rowView.findViewById(R.id.lay_btn);
            // lay_btn.setVisibility(View.VISIBLE);

            ImageView btnInterest = rowView.findViewById(R.id.btnInterest);
            ImageView btnReject = rowView.findViewById(R.id.btnReject);

            //ImageView img_check=(ImageView) rowView.findViewById(R.id.img_check);
            //ImageView img_close=(ImageView) rowView.findViewById(R.id.img_close);

            //RelativeLayout lay_accept=(RelativeLayout)rowView.findViewById(R.id.lay_accept);
            // RelativeLayout lay_reject=(RelativeLayout)rowView.findViewById(R.id.lay_reject);

            ImageView img_profile = rowView.findViewById(R.id.img_profile);

            final ExpressItem item = list.get(position);
            tv_name.setText(item.getMatri_id());
            String msg = "";
            if (item.getAbout().length() > 30) {
                msg = item.getAbout().substring(0, 30) + "...<font color=#a30412>Read more</font>";
            } else {
                msg = item.getAbout();
            }
            tv_about.setText(Html.fromHtml(msg));
            tv_about.setOnClickListener(view16 -> tv_about.setText(item.getAbout()));

            if (item.getPhoto_view_status().equals("0")) {
                img_profile.setImageResource(photoProtectPlaceHolder);
            } else if (item.getPhoto_view_status().equals("2") && MyApplication.getPlan()) {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                        img_profile.setImageResource(placeHolder);
                } else {
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_profile);
                }
            } else if (item.getPhoto_view_status().equals("2") && !MyApplication.getPlan()) {
                img_profile.setImageResource(placeHolder);
            } else if (item.getPhoto_view_status().equals("1")) {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                    img_profile.setImageResource(placeHolder);
                } else {
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_profile);
                }
            } else {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                    img_profile.setImageResource(placeHolder);
                } else {
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_profile);
                }

            }

//            img_more.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showFilterPopup(view, item.getId(), item.getReceiver_response(),position);
//                }
//            });

            /*switch (common.imageCheck(item.getImage_approval())) {
                case "male":
                    img_profile.setImageResource(R.drawable.male);
                    break;
                case "female":
                    img_profile.setImageResource(R.drawable.female);
                    break;
                default:
                    Picasso.get().load(item.getImage()).into(img_profile);
                    break;
            }*/

            switch (item.getReceiver_response()) {
                case "All":
                    tv_status.setText("Interest Received");
                    break;
                case "Accepted":
                    tv_status.setText("Interest Received Accepted");
                    // tv_accept_label.setText("Accepted");
                    // tv_decline_label.setText("Decline");
                    // img_check.setImageResource(R.drawable.check_circle_fill);
                    // img_close.setImageResource(R.drawable.cancel_red);
                    break;
                case "Rejected":
                    tv_status.setText("Interest Received Rejected");
                    // tv_accept_label.setText("Accept");
                    //  tv_decline_label.setText("Declined");
                    // img_check.setImageResource(R.drawable.check_circle);
                    // img_close.setImageResource(R.drawable.cancel_fill_red);
                    break;
                case "Pending":
                    tv_status.setText("Interest Received Pending");
                    // tv_accept_label.setText("Accept");
                    //  tv_decline_label.setText("Decline");
//
                    break;
            }
            img_profile.setOnClickListener(view1 -> {
                if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                    //common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                    //item.getPhoto_view_status()).equals("male_password") ||
                    //common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                    //item.getPhoto_view_status()).equals("female_password")
                    alertPhotoPassword(item.getMatri_id());
                } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1")) {
                    final Dialog dialog = new Dialog(context);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setContentView(R.layout.show_image_alert);
                    TouchImageView img_url = (TouchImageView) dialog.findViewById(R.id.img_url);
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_url);
                    dialog.show();
                } else {
                    if (MyApplication.getPlan()) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getUser_id());
                        context.startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to view this profile.");
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }
                }
            });

            rowView.setOnClickListener(view12 -> {
                if (MyApplication.getPlan()) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getUser_id());
                    context.startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to view this profile.");
                    context.startActivity(new Intent(context, PlanListActivity.class));
                }
            });

            AppDebugLog.print("response : " + item.getReceiver_response());

            if (item.getReceiver_response().equalsIgnoreCase("Accepted")) {
                btnInterest.setImageResource(R.drawable.check_fill_green);
            } else {
                btnInterest.setImageResource(R.drawable.check_gray_fill);
            }

            if (item.getReceiver_response().equalsIgnoreCase("Rejected")) {
                btnReject.setImageResource(R.drawable.cancel_fill_red);
            } else {
                btnReject.setImageResource(R.drawable.cancel_fill_gray);
            }

            btnInterest.setOnClickListener(view13 -> {
                if (!item.getReceiver_response().equals("Accepted"))
                    updateAction(item.getId(), "accept");
                else
                    common.showToast("You already accepted this user.");
            });

            btnReject.setOnClickListener(view14 -> {
                if (!item.getReceiver_response().equals("Rejected"))
                    updateAction(item.getId(), "reject");
                else
                    common.showToast("You already rejected this user.");
            });

            btnDelete.setOnClickListener(view15 -> {
                deleteInterestAlert(item.getId(), position);
            });

            return rowView;
        }

        private void showFilterPopup(View v, final String id, final String receiver_response, final int pos) {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.photo_password_more);

            if (receiver_response.equals("Accepted")) {
                popup.getMenu().findItem(R.id.accept).setTitle("Accepted");
                popup.getMenu().findItem(R.id.reject).setTitle("Reject");
                //  popup.getMenu().findItem(R.id.delete).setVisible(false);
            } else if (receiver_response.equals("Rejected")) {
                popup.getMenu().findItem(R.id.accept).setTitle("Accept");
                popup.getMenu().findItem(R.id.reject).setTitle("Rejected");
                //popup.getMenu().findItem(R.id.delete).setVisible(false);
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.accept:
                            if (item.getTitle().toString().equals("Accept"))
                                updateAction(id, "accept");
                            return true;
                        case R.id.reject:
                            if (item.getTitle().toString().equals("Reject"))
                                updateAction(id, "reject");
                            return true;
                        case R.id.delete:
                            deleteInterestAlert(id, pos);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }

        private void deleteInterestAlert(final String id, final int position) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage("Are you sure you want to delete this interest?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteApi(id, position);
                }
            });
            alert.setNegativeButton("No", null);
            alert.show();
        }

        private void deleteApi(String id, final int position) {
            pd = new ProgressDialog(context);
            pd.setCancelable(false);
            pd.setMessage("Loading...");
            pd.setIndeterminate(true);
            pd.show();

            HashMap<String, String> param = new HashMap<>();
            param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));
            param.put("exp_status", tag);
            param.put("id", id);
            param.put("status", "delete");

            common.makePostRequest(Utils.action_update_status, param, response -> {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errormessage"));
                    if (object.getString("status").equals("success")) {
                        list.remove(position);
                        if (list.size() == 0) {
                            lv_exp_receive.setVisibility(View.GONE);
                            tv_no_data.setVisibility(View.VISIBLE);
                        }
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

        private void alertPhotoPassword(final String matri_id) {
            final String[] arr = new String[]{"We found your profile to be a good match. Please send me Photo password to proceed further.",
                    "I am interested in your profile. I would like to view photo now, send me password."};
            final String[] selected = {"We found your profile to be a good match. Please send me Photo password to proceed further."};
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

            alt_bld.setTitle("Photos View Request");
            alt_bld.setSingleChoiceItems(arr, 0, (dialog, item) -> {
                //dialog.dismiss();// dismiss the alertbox after chose option
                selected[0] = arr[item];
            });
            alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0], matri_id));
            alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
                //alertpassword(password,url);
            });
            AlertDialog alert = alt_bld.create();
            alert.show();

        }

        private void sendRequest(String int_msg, String matri_id) {
            pd = new ProgressDialog(context);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();

            HashMap<String, String> param = new HashMap<>();
            param.put("interest_message", int_msg);
            param.put("receiver_id", matri_id);
            param.put("requester_id", session.getLoginData(SessionManager.KEY_matri_id));

            common.makePostRequest(Utils.photo_password_request, param, response -> {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(context, object.getString("errmessage"), Toast.LENGTH_LONG).show();

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

    private void updateAction(String id, String status) {
        pd = new ProgressDialog(context);
        pd.setCancelable(true);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("id", id);
        param.put("exp_status", tag);
        param.put("status", status);
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.action_update_status, param, response -> {
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errormessage"));
                if (object.getString("status").equals("success")) {
                    page = 0;
                    page = page + 1;
                    continue_request = true;
                    list.clear();
                    getData(page, tag);
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
