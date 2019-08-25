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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Activities.OtherUserProfileActivity;
import com.mega.matrimony.Activities.PlanListActivity;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.Model.PhotoPasswordBean;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
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

public class PhotoPasswordReceivedFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lv_exp_receive;
    Context context;
    List<PhotoPasswordBean> list = new ArrayList<>();
    Received_Adapter adapter;

    Common common;
    SessionManager session;
    ProgressDialog pd;
    boolean continue_request;
    TextView tv_no_data;
    int page = 0;
    private boolean isCreate = false;
    private int placeHolder,photoProtectPlaceHolder;

    public PhotoPasswordReceivedFragment() {
    }

    public static PhotoPasswordReceivedFragment newInstance(String param1, String param2) {
        PhotoPasswordReceivedFragment fragment = new PhotoPasswordReceivedFragment();
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
        isCreate = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // Log.d("frgchang",isVisibleToUser+"   ");
//        if (isVisibleToUser && isCreate) {
//            if (pd != null)
//                pd.dismiss();
//            list.clear();
//            adapter.notifyDataSetChanged();
//            page = 0;
//            page = page + 1;
//            getListData(page);
//        }

        //super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_password_received, container, false);

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


        lv_exp_receive = (ListView) view.findViewById(R.id.lv_exp_receive);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);

        page = page + 1;
        getListData(page);

        adapter = new Received_Adapter(getActivity(), list);
        lv_exp_receive.setAdapter(adapter);

        lv_exp_receive.setOnScrollListener(new AbsListView.OnScrollListener() {
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

        common.makePostRequest(Utils.photo_pass_request_received + page, param, response -> {
            if (pd != null)
                pd.dismiss();
            AppDebugLog.print("resp : " + response);
            try {
                JSONObject object = new JSONObject(response);
                continue_request = object.getBoolean("continue_request");
                int total_count = object.getInt("total_count");
                if (total_count != 0) {
                    tv_no_data.setVisibility(View.GONE);
                    lv_exp_receive.setVisibility(View.VISIBLE);
                    if (total_count != list.size()) {
                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            PhotoPasswordBean item = new PhotoPasswordBean();
                            item.setId(obj.getString("ph_reqid"));
                            item.setMatri_id(obj.getString("ph_requester_id"));
                            item.setAbout(obj.getString("ph_msg"));
                            item.setUser_id(obj.getString("user_id"));
                            item.setReceiver_response(obj.getString("receiver_response"));
                            item.setPh_receiver_id(obj.getString("ph_receiver_id"));
                            item.setPh_requester_id(obj.getString("ph_requester_id"));

                            item.setPhoto_view_count(obj.getString("photo_view_count"));
                            item.setPhoto_view_status(obj.getString("photo_view_status"));
                            item.setImage_approval(obj.getString("photo1_approve"));
                            if (obj.has("photo1"))
                                item.setImage(obj.getString("photo1"));
                            item.setDate(getDate(obj.getString("ph_reqdate")));
                            list.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    lv_exp_receive.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                common.showToast(getString(R.string.err_msg_try_again_later));
                e.printStackTrace();
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

    private class Received_Adapter extends ArrayAdapter<PhotoPasswordBean> {
        Context context;
        List<PhotoPasswordBean> list;

        public Received_Adapter(Context context, List<PhotoPasswordBean> list) {
            super(context, R.layout.photo_password, list);
            this.context = context;
            this.list = list;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.photo_received_password, null, true);

            TextView tv_name = rowView.findViewById(R.id.tv_name);
            TextView tv_date = rowView.findViewById(R.id.tv_date);
            TextView tv_about = rowView.findViewById(R.id.tv_about);

            TextView btnDelete = rowView.findViewById(R.id.btnDelete);
            ImageView btnInterest = rowView.findViewById(R.id.btnInterest);
            ImageView btnReject = rowView.findViewById(R.id.btnReject);

            ImageView img_profile = rowView.findViewById(R.id.img_profile);

            PhotoPasswordBean item = list.get(position);
            tv_name.setText(item.getMatri_id());

            if (item.getPhoto_view_status().equals("0")) {
                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                    img_profile.setImageResource(R.drawable.photopassword_male);
                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                    img_profile.setImageResource(R.drawable.photopassword_female);
                }
            } else if (item.getPhoto_view_status().equals("2") && MyApplication.getPlan()) {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                        img_profile.setImageResource(R.drawable.male);
                    } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                        img_profile.setImageResource(R.drawable.female);
                    }
                } else {
                    Picasso.get().load(item.getImage()).into(img_profile);
                }
            } else if (item.getPhoto_view_status().equals("2") && !MyApplication.getPlan()) {
                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                    img_profile.setImageResource(R.drawable.male);
                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                    img_profile.setImageResource(R.drawable.female);
                }
            } else if (item.getPhoto_view_status().equals("1")) {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                        img_profile.setImageResource(R.drawable.male);
                    } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                        img_profile.setImageResource(R.drawable.female);
                    }
                } else {
                    Picasso.get().load(item.getImage()).into(img_profile);
                }
            } else {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                        img_profile.setImageResource(R.drawable.male);
                    } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                        img_profile.setImageResource(R.drawable.female);
                    }
                } else {
                    Picasso.get().load(item.getImage()).into(img_profile);
                }

            }
            tv_date.setText(item.getDate());

            //String about=item.getAbout()+"...<font color='#ff041a'>Read More</font>";
            tv_about.setText(item.getAbout());

            if (item.getReceiver_response().equalsIgnoreCase("Pending")) {
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnDelete.setVisibility(View.GONE);
            }

            if (item.getReceiver_response().equalsIgnoreCase("Accepted")) {
                btnInterest.setImageResource(R.drawable.check_fill_green);
            } else {
                btnInterest.setImageResource(R.drawable.check_gray_fill);
            }

            AppDebugLog.print("response : " + item.getReceiver_response());
            if (item.getReceiver_response().equalsIgnoreCase("Rejected")) {
                btnReject.setImageResource(R.drawable.cancel_fill_red);
            } else {
                btnReject.setImageResource(R.drawable.cancel_fill_gray);
            }

            btnInterest.setOnClickListener(view13 -> {
                AppDebugLog.print("response : " + item.getReceiver_response());
                if (item.getReceiver_response().equalsIgnoreCase("Accepted")) {
                    common.showToast("You already Accepted Photo request of this user !!!");
                } else if (item.getReceiver_response().equalsIgnoreCase("Rejected")) {
                    statusChange("Accepted", item.getId());
                } else if (item.getReceiver_response().equalsIgnoreCase("Pending")) {
                    statusChange("Accepted", item.getId());
                }
            });

            btnReject.setOnClickListener(view14 -> {
                AppDebugLog.print("response : " + item.getReceiver_response());
                if (item.getReceiver_response().equalsIgnoreCase("Rejected")) {
                    common.showToast("You already Rejected Photo request of this user !!!");
                } else if (item.getReceiver_response().equalsIgnoreCase("Accepted")) {
                    statusChange("Rejected", item.getId());
                } else if (item.getReceiver_response().equalsIgnoreCase("Pending")) {
                    statusChange("Rejected", item.getId());
                }
            });

            btnDelete.setOnClickListener(view15 -> {
                deleteAlert(item.getId());
            });

            img_profile.setOnClickListener(view1 -> {
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
                    Picasso.get().load(item.getImage()).into(img_url);
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

            common.makePostRequest(Utils.photo_password_request, param, response -> {
                if (pd != null)
                    pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(context, object.getString("errmessage"), Toast.LENGTH_LONG).show();

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

        private void showFilterPopup(View v, final String id, final String receiver_response) {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.photo_password_more);

            if (receiver_response.equals("Accepted")) {
                popup.getMenu().findItem(R.id.accept).setTitle("Accepted");
                popup.getMenu().findItem(R.id.reject).setTitle("Reject");
                popup.getMenu().findItem(R.id.delete).setVisible(false);
            } else if (receiver_response.equals("Rejected")) {
                popup.getMenu().findItem(R.id.accept).setTitle("Accept");
                popup.getMenu().findItem(R.id.reject).setTitle("Rejected");
                popup.getMenu().findItem(R.id.delete).setVisible(false);
            } else if (receiver_response.equals("Pending")) {
                popup.getMenu().findItem(R.id.delete).setVisible(true);
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.accept:
                            if (item.getTitle().toString().equals("Accept"))
                                statusChange("Accepted", id);
                            return true;
                        case R.id.reject:
                            if (item.getTitle().toString().equals("Reject"))
                                statusChange("Rejected", id);
                            return true;
                        case R.id.delete:
                            deleteAlert(id);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }

    }

    private void statusChange(String tag, String id) {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("requester_id", id);
        param.put("status", tag);

        common.makePostRequest(Utils.reject_request, param, response -> {
            if (pd != null)
                pd.dismiss();
            try {
                AppDebugLog.print("response : " + response);
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("response"));
                if (object.getString("status").equals("success")) {
                    list.clear();
                    page = 0;
                    page = page + 1;
                    getListData(page);
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

    private void deleteAlert(final String id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure Delete this?");
        alert.setNegativeButton("No", null);
        alert.setPositiveButton("Yes", (dialogInterface, i) -> {
            pd = new ProgressDialog(context);
            pd.setIndeterminate(true);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();

            HashMap<String, String> param = new HashMap<>();
            param.put("requester_id", id);
            param.put("status", "receiver");

            common.makePostRequest(Utils.delete_request, param, response -> {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("response"));
                    if (object.getString("status").equals("success")) {
                        list.clear();
                        page = 0;
                        page = page + 1;
                        getListData(page);
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

        });
        alert.show();


    }

}

