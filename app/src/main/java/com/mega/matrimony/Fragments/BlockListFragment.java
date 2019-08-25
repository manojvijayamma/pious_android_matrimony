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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Activities.OtherUserProfileActivity;
import com.mega.matrimony.Activities.PlanListActivity;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.Model.Block_item;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ListView lv_block;
    TextView tv_no_data;
    EditText et_block_id;
    Button btn_block;
    TextView lbl_block_visi;
    Common common;
    SessionManager session;
    List<Block_item> list = new ArrayList<>();
    Context context;
    BlockAdapter adp;
    ProgressDialog pd;
    boolean continue_request;
    int page = 0;
    private int placeHolder,photoProtectPlaceHolder;

    public BlockListFragment() {
    }

    public static BlockListFragment newInstance(String param1, String param2) {
        BlockListFragment fragment = new BlockListFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_list, container, false);

        common = new Common(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();

        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        lv_block = (ListView) view.findViewById(R.id.lv_block);
        lbl_block_visi = (TextView) view.findViewById(R.id.lbl_block_visi);
        et_block_id = (EditText) view.findViewById(R.id.et_block_id);
        btn_block = (Button) view.findViewById(R.id.btn_id);

        common.setDrawableLeftTextViewLeft(R.drawable.ban, lbl_block_visi);
        common.setDrawableLeftEditText(R.drawable.ban, et_block_id);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }


        page = page + 1;
        getListData(page);

        adp = new BlockAdapter(getActivity(), list);
        lv_block.setAdapter(adp);

        lv_block.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    if (pd != null)
                        pd.dismiss();
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_block_id.getText().toString().trim())) {
                    et_block_id.setError("Please enter matri id.");
                    return;
                }
                block_api("add", et_block_id.getText().toString().trim(), 0);
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
        common.makePostRequest(Utils.block_list + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);

                try {
                    JSONObject object = new JSONObject(response);
                    continue_request = object.getBoolean("continue_request");
                    int total_count = object.getInt("total_count");

                    if (total_count != 0) {
                        lv_block.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                        if (list.size() != total_count) {

                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                Block_item item = new Block_item();
                                item.setName(obj.getString("username"));
                                item.setMatri_id(obj.getString("matri_id"));
                                item.setUser_id(obj.getString("user_id"));
                                item.setImage(obj.getString("photo1"));
                                item.setImage_approval(obj.getString("photo1_approve"));
                                item.setPhoto_view_status(obj.getString("photo_view_status"));
                                item.setPhoto_view_count(obj.getString("photo_view_count"));

                                AppDebugLog.print("birthdate : " + obj.getString("birthdate"));
//                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                                String about=common.getAge(obj.getString("birthdate"),sdf)+" Years, "+
//                                       checkData(common.calculateHeight(obj.getString("height")))+
//                                       checkData(obj.getString("caste_name"))+checkData(obj.getString("religion_name"))+
//                                       checkData(obj.getString("city_name"))+checkData(obj.getString("country_name"));

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                String description = Common.getDetailsFromValue(common.getAge(obj.getString("birthdate"), sdf) + " Years, ", obj.getString("height"),
                                        obj.getString("caste_name"), obj.getString("religion_name"),
                                        obj.getString("city_name"), obj.getString("country_name"));

                                item.setDetail(description);
                                list.add(item);
                            }
                            adp.notifyDataSetChanged();
                        }
                    } else {
                        lv_block.setVisibility(View.GONE);
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

    private class BlockAdapter extends ArrayAdapter<Block_item> {

        List<Block_item> list;
        Context context;

        public BlockAdapter(Context context, List<Block_item> list) {
            super(context, R.layout.block_item, list);
            this.context = context;
            this.list = list;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.block_item, null, true);

            CircleImageView img_profile = (CircleImageView) rowView.findViewById(R.id.img_profile);
            TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
            TextView tv_about = (TextView) rowView.findViewById(R.id.tv_about);

            Button btn_block = (Button) rowView.findViewById(R.id.btn_id);
            common.setDrawableLeftButton(R.drawable.unlocked_white, btn_block);

            final Block_item item = list.get(position);
            tv_name.setText(item.getMatri_id());

            if (item.getPhoto_view_status().equals("0")) {
                img_profile.setImageResource(photoProtectPlaceHolder);
            } else if (item.getPhoto_view_status().equals("2") && MyApplication.getPlan()) {
                if (item.getImage_approval().equals("UNAPPROVED")) {
                        img_profile.setImageResource(placeHolder);
                } else {
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_profile);
                }
            } else if (item.getPhoto_view_status().equals("2") && !MyApplication.getPlan()) {
                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                    img_profile.setImageResource(R.drawable.male);
                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                    img_profile.setImageResource(R.drawable.female);
                }
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
            /*if (item.getImage_approval().equals("APPROVED")){
                Picasso.get().load(item.getImage()).into(img_profile);
            }else {
                img_profile.setImageResource(R.drawable.male);
            }*/
            tv_about.setText(Html.fromHtml(item.getDetail()));

            btn_block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unBlockUser("remove", item.getMatri_id(), position);
                }
            });

            img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
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

    private void unBlockUser(final String tag, final String id, final int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure you want to unblock this user?");
        alert.setTitle("Unblock User");
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                block_api(tag, id, pos);


            }
        });
        alert.show();
    }

    private void block_api(final String tag, String id, final int position) {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("blacklist_action", tag);
        if (tag.equals("add")) {
            param.put("blockuserid", id);
        } else
            param.put("unblockuserid", id);

        common.makePostRequest(Utils.block_user, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();

                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (!object.getString("status").equals("error")) {
                        if (tag.equals("remove")) {
                            list.remove(position);
                            if (list.size() == 0) {
                                lv_block.setVisibility(View.GONE);
                                tv_no_data.setVisibility(View.VISIBLE);
                            }
                            adp.notifyDataSetChanged();
                        } else {
                            et_block_id.setText("");
                            list.clear();
                            page = 0;
                            page = page + 1;
                            getListData(page);
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

}
