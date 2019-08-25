package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

public class ExpressSentFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lv_exp_sent;
    TextView tv_no_data;
    Spinner spin_exp_sent;
    Context context;
    List<ExpressItem> list = new ArrayList<>();
    Sent_Adapter adapter;
    Common common;
    SessionManager session;
    ProgressDialog pd;
    boolean continue_request;
    int page = 0;
    String tag = "all_sent";
    private int placeHolder,photoProtectPlaceHolder;

    public ExpressSentFragment() {
        // Required empty public constructor
    }

    public static ExpressSentFragment newInstance(String param1, String param2) {
        ExpressSentFragment fragment = new ExpressSentFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_express_sent, container, false);

        lv_exp_sent = (ListView) view.findViewById(R.id.lv_exp_sent);
        spin_exp_sent = (Spinner) view.findViewById(R.id.spin_exp_sent);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        //initData("All");

        List<String> lst = new ArrayList<>();
        lst.add("All Interest");
        lst.add("Interest Sent Accept");
        lst.add("Interest Sent Reject");
        lst.add("Interest Sent Pending");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, lst);
        spin_exp_sent.setAdapter(arrayAdapter);

        spin_exp_sent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                page = 0;
                page = page + 1;
                continue_request = true;
                list.clear();
                switch (position) {
                    case 0:
                        getData(page, "all_sent");
                        tag = "all_sent";
                        break;
                    case 1:
                        getData(page, "accept_sent");
                        tag = "accept_sent";
                        break;
                    case 2:
                        getData(page, "reject_sent");
                        tag = "reject_sent";
                        break;
                    case 3:
                        getData(page, "pending_sent");
                        tag = "pending_sent";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lv_exp_sent.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        //page=page+1;
        //getData(page,tag);

        adapter = new Sent_Adapter(getActivity(), list);
        lv_exp_sent.setAdapter(adapter);

        return view;
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
                        lv_exp_sent.setVisibility(View.VISIBLE);
                        if (total_count != list.size()) {
                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                ExpressItem item = new ExpressItem();
                                item.setId(obj.getString("id"));
                                item.setMatri_id(obj.getString("matri_id"));
                                item.setUser_id(obj.getString("user_id"));
                                //String about=common.getAge(obj.getString("birthdate"))+" Year, "+
                                //        checkData(common.calculateHeight(obj.getString("height")))+
                                //        checkData(obj.getString("caste_name"))+checkData(obj.getString("religion_name"))+
                                //        checkData(obj.getString("city_name"))+checkData(obj.getString("country_name"));

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
                        lv_exp_sent.setVisibility(View.GONE);
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


    private class Sent_Adapter extends ArrayAdapter<ExpressItem> {

        Context context;
        List<ExpressItem> list;

        public Sent_Adapter(Context context, List<ExpressItem> list) {
            super(context, R.layout.express_item, list);
            this.context = context;
            this.list = list;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.express_item, null, true);

            TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
            TextView tv_status = (TextView) rowView.findViewById(R.id.tv_status);
            final TextView tv_about = (TextView) rowView.findViewById(R.id.tv_about);

            TextView tv_bg_status = (TextView) rowView.findViewById(R.id.tv_bg_status);
            tv_bg_status.setVisibility(View.VISIBLE);
            ImageView img_more = (ImageView) rowView.findViewById(R.id.img_more);
            img_more.setVisibility(View.GONE);

            // LinearLayout lay_btn=(LinearLayout)rowView.findViewById(R.id.lay_btn);
            // lay_btn.setVisibility(View.GONE);

            ImageView img_profile = (ImageView) rowView.findViewById(R.id.img_profile);

            tv_bg_status.setText("Delete");
            tv_bg_status.setBackgroundResource(R.drawable.round_corner_red);

            final ExpressItem item = list.get(position);

            tv_bg_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteInterestAlert(item.getId(), position);
                }
            });

            tv_name.setText(item.getMatri_id());
            String msg = "";
            if (item.getAbout().length() > 40) {
                msg = item.getAbout().substring(0, 40) + "...<font color=#a30412>Read more</font>";
            } else {
                msg = item.getAbout();
            }
            tv_about.setText(Html.fromHtml(msg));
            tv_about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_about.setText(item.getAbout());
                }
            });

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
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                    tv_status.setText("Interest Sent");
                    break;
                case "Accepted":
                    tv_status.setText("Interest Sent Accepted");
                    //tv_bg_status.setText("Accepted");
                    //tv_bg_status.setBackgroundResource(R.drawable.round_courner_green);
                    break;
                case "Rejected":
                    tv_status.setText("Interest Sent Rejected");
                    // tv_bg_status.setText("Rejected");
                    // tv_bg_status.setBackgroundResource(R.drawable.round_background);
                    break;
                case "Pending":
                    tv_status.setText("Interest Sent Pending");
                    // tv_bg_status.setText("Pending");
                    // tv_bg_status.setBackgroundResource(R.drawable.round_gray);
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

            return rowView;
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

    private void deleteInterestAlert(final String id, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure you want to delete this interest?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> deleteApi(id, position));
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
                        lv_exp_sent.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
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


}
