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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.mega.matrimony.Activities.OtherUserProfileActivity;
import com.mega.matrimony.Activities.PlanListActivity;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.Model.ExpressItem;
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

public class PhotoPasswordSentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lv_exp_sent;
    Context context;
    List<ExpressItem> list = new ArrayList<>();
    Sent_Adapter adapter;

    Common common;
    SessionManager session;
    ProgressDialog pd;
    boolean continue_request;
    TextView tv_no_data;
    int page = 0;

    private int placeHolder,photoProtectPlaceHolder;


    public PhotoPasswordSentFragment() {
        // Required empty public constructor
    }

    public static PhotoPasswordSentFragment newInstance(String param1, String param2) {
        PhotoPasswordSentFragment fragment = new PhotoPasswordSentFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo_password_sent, container, false);

        context = getActivity();
        common = new Common(getActivity());
        session = new SessionManager(getActivity());

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }

        lv_exp_sent = (ListView) view.findViewById(R.id.lv_exp_sent);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);

        page = page + 1;
        getListData(page);

        adapter = new Sent_Adapter(getActivity(), list);
        lv_exp_sent.setAdapter(adapter);

        lv_exp_sent.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (continue_request) {
                        page = page + 1;
                        getListData(page);
                    }
                    //if (pd!=null)
                    //    pd.dismiss();
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

        return view;
    }

    private void getListData(int page) {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequest(Utils.photo_pass_request_send + page, param, response -> {
            pd.dismiss();
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                continue_request = object.getBoolean("continue_request");
                int total_count = object.getInt("total_count");
                if (total_count != 0) {
                    tv_no_data.setVisibility(View.GONE);
                    lv_exp_sent.setVisibility(View.VISIBLE);
                    if (total_count != list.size()) {
                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            ExpressItem item = new ExpressItem();
                            item.setId(obj.getString("ph_reqid"));
                            item.setMatri_id(obj.getString("ph_requester_id"));
                            item.setPh_receiver_id(obj.getString("ph_receiver_id"));
                            item.setAbout(obj.getString("ph_msg"));
                            item.setReceiver_response(obj.getString("receiver_response"));
                            item.setPhoto_view_count(obj.getString("photo_view_count"));
                            item.setPhoto_view_status(obj.getString("photo_view_status"));
                            item.setImage_approval(obj.getString("photo1_approve"));
                            item.setImage(obj.getString("photo1"));
                            item.setDate(getDate(obj.getString("ph_reqdate")));
                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    lv_exp_sent.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
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

    private String getDate(String time) {
        String outputPattern = "MMM dd, yyyy";
        String inputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

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

    private class Sent_Adapter extends ArrayAdapter<ExpressItem> {

        Context context;
        List<ExpressItem> list;

        public Sent_Adapter(Context context, List<ExpressItem> list) {
            super(context, R.layout.photo_password, list);
            this.context = context;
            this.list = list;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.photo_password, null, true);

            TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
            TextView tv_date = (TextView) rowView.findViewById(R.id.tv_date);
            TextView tv_about = (TextView) rowView.findViewById(R.id.tv_about);
            ImageView img_more = (ImageView) rowView.findViewById(R.id.img_more);
            TextView tv_bg_status = (TextView) rowView.findViewById(R.id.tv_bg_status);
            tv_bg_status.setVisibility(View.VISIBLE);

            // ConstraintLayout lay_btn=(ConstraintLayout)rowView.findViewById(R.id.lay_btn);
            img_more.setVisibility(View.GONE);

            ImageView img_profile = (ImageView) rowView.findViewById(R.id.img_profile);

            final ExpressItem item = list.get(position);
            tv_name.setText(item.getPh_receiver_id());

            if (item.getReceiver_response().equals("Pending")) {
                tv_bg_status.setVisibility(View.VISIBLE);
            } else {
                tv_bg_status.setVisibility(View.GONE);
            }

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
            tv_date.setText(item.getDate());

            //String about=item.getAbout()+"...<font color='#ff041a'>Read More</font>";
            tv_about.setText(item.getAbout());

            tv_bg_status.setOnClickListener(view1 -> deleteAlert(item.getId(), position));

            img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                        //common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                        //item.getPhoto_view_status()).equals("male_password") ||
                        //common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                        //item.getPhoto_view_status()).equals("female_password")
                        alertPhotoPassword(item.getImage(), item.getMatri_id());
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
                }
            });

            return rowView;
        }

        private void alertPhotoPassword(final String url, final String matri_id) {
            final String[] arr = new String[]{"We found your profile to be a good match. Please send me Photo password to proceed further.",
                    "I am interested in your profile. I would like to view photo now, send me password."};
            final String[] selected = {"We found your profile to be a good match. Please send me Photo password to proceed further."};
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

            alt_bld.setTitle("Photos View Request");
            alt_bld.setSingleChoiceItems(arr, 0, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    //dialog.dismiss();// dismiss the alertbox after chose option
                    selected[0] = arr[item];
                }
            });
            alt_bld.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendRequest(selected[0], matri_id);
                }
            });
            alt_bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //alertpassword(password,url);
                }
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

            common.makePostRequest(Utils.photo_password_request, param, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pd.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);
                        Toast.makeText(context, object.getString("errmessage"), Toast.LENGTH_LONG).show();

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

    }

    private void deleteAlert(final String id, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure Delete this?");
        alert.setNegativeButton("No", null);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (pd == null || !pd.isShowing()) {
                    pd = new ProgressDialog(context);
                    pd.setIndeterminate(true);
                    pd.setMessage("Loading...");
                    pd.setCancelable(true);
                    pd.show();
                }

                HashMap<String, String> param = new HashMap<>();
                // param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
                param.put("requester_id", id);
                param.put("status", "sender");

                common.makePostRequest(Utils.delete_request, param, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.d("resp", response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("errmessage"))
                                common.showToast(object.getString("errmessage"));
                            if (object.getString("status").equals("success")) {
                                list.remove(position);
                                if (list.size() == 0) {
                                    lv_exp_sent.setVisibility(View.GONE);
                                    tv_no_data.setVisibility(View.VISIBLE);
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
        });
        alert.show();


    }

}
