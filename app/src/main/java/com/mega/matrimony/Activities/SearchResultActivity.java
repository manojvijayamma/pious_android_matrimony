package com.mega.matrimony.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.Model.DashboardItem;
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

public class SearchResultActivity extends AppCompatActivity {

    ListView lv_result;
    HashMap<String, String> param;
    Common common;
    List<DashboardItem> list = new ArrayList<>();
    SessionManager session;
    ProgressDialog pd;
    RecomdationAdapter adapter;
    boolean continue_request;
    int page = 0;
    TextView tv_no_data;
    private int placeHolder,photoProtectPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Result");

        common = new Common(this);
        session = new SessionManager(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey("searchData")) {
                param = Common.getBeanObjectFromJsonString(getIntent().getStringExtra("searchData"));
                //Log.d("resp",param.toString()+" ");
                AppDebugLog.print("param in SearchResultActivity : " + param.toString());
            }
        }

        lv_result = (ListView) findViewById(R.id.lv_result);
        tv_no_data = (TextView) findViewById(R.id.tv_no_data);

        adapter = new RecomdationAdapter(this, list);
        lv_result.setAdapter(adapter);

        page = page + 1;
        getData(page);

        lv_result.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (continue_request) {
                        if (pd != null)
                            pd.dismiss();
                        page = page + 1;
                        getData(page);
                    }
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

    }

    @Override
    public void onPause() {
        //Log.e("Exce","pause");
        if ((pd != null) && pd.isShowing())
            pd.dismiss();
        pd = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Log.e("Exce","onDestroy");
        if ((pd != null) && pd.isShowing())
            pd.dismiss();
        pd = null;
        super.onDestroy();
    }

    private void getData(int page) {
        pd = new ProgressDialog(SearchResultActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        common.makePostRequest(Utils.search_result + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                //Log.d("resp",response);
                try {
                    JSONObject object = new JSONObject(response);
                    int total_count = object.getInt("total_count");
                    getSupportActionBar().setTitle("Search Result (" + total_count + " Members)");
                    continue_request = object.getBoolean("continue_request");

                    if (total_count != 0) {

                        tv_no_data.setVisibility(View.GONE);
                        lv_result.setVisibility(View.VISIBLE);
                        if (total_count != list.size()) {
                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                DashboardItem item = new DashboardItem();
                                item.setName(obj.getString("matri_id"));
                                item.setImage(obj.getString("photo1"));
                                item.setImage_approval(obj.getString("photo1_approve"));
                                item.setAge(obj.getString("age"));
                                item.setHeight(obj.getString("height"));
                                item.setCaste(obj.getString("caste_name"));
                                item.setReligion(obj.getString("religion_name"));
                                item.setCity(obj.getString("city_name"));
                                item.setCountry(obj.getString("country_name"));
                                item.setDesignation(obj.getString("designation_name"));
                                item.setPhoto_protect(obj.getString("photo_protect"));
                                item.setPhoto_view_status(obj.getString("photo_view_status"));
                                item.setPhoto_password(obj.getString("photo_password"));
                                item.setId(obj.getString("id"));
                                item.setPhoto_view_count(obj.getString("photo_view_count"));
                                JSONArray action = obj.getJSONArray("action");
                                item.setAction(action.getJSONObject(0));
                                list.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        lv_result.setVisibility(View.GONE);
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

    public class RecomdationAdapter extends ArrayAdapter<DashboardItem> {
        Context context;
        List<DashboardItem> list;
        Common common;

        public RecomdationAdapter(Context context, List<DashboardItem> list) {
            super(context, R.layout.recomdation_item, list);
            this.context = context;
            this.list = list;
            common = new Common(context);
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.recomdation_item, null, true);

            final LikeButton btn_interest = (LikeButton) rowView.findViewById(R.id.btn_interest);
            LikeButton btn_like = (LikeButton) rowView.findViewById(R.id.btn_like);
            LikeButton btn_block = (LikeButton) rowView.findViewById(R.id.btn_id);
            LikeButton btn_chat = (LikeButton) rowView.findViewById(R.id.btn_chat);

            ImageView btn_profile = (ImageView) rowView.findViewById(R.id.btn_profile);
            ImageView img_profile = (ImageView) rowView.findViewById(R.id.img_profile);
            TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name);
            TextView tv_detail = (TextView) rowView.findViewById(R.id.tv_detail);

            final DashboardItem item = list.get(position);

            try {
                if (item.getAction().getString("is_like").equals("Yes"))
                    btn_like.setLiked(true);
                else
                    btn_like.setLiked(false);
                if (item.getAction().getInt("is_block") == 1)
                    btn_block.setLiked(true);
                else
                    btn_block.setLiked(false);

                if (!item.getAction().getString("is_interest").equals(""))
                    btn_interest.setLiked(true);
                else
                    btn_interest.setLiked(false);

            } catch (JSONException e) {
                e.printStackTrace();
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

            tv_name.setText(item.getName().toUpperCase());

//            String about=item.getAge()+", "+item.getHeight()+", "+
//                    item.getReligion()+"...<font color='#ff041a'>Read More</font>";//"27 Year,5'6'' Height,Gujarati-Hindu,Orlando-Canada | Computers IT Profile Created By Self...<font color='#ff041a'>Read More</font>";

            String description = Common.getDetailsFromValue(item.getAge(), item.getHeight(),
                    item.getCaste(), item.getReligion(),
                    item.getCity(), item.getCountry());
            tv_detail.setText(Html.fromHtml(description));

            btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.getPlan()) {
                        Intent i = new Intent(context, ConversationActivity.class);
                        i.putExtra("matri_id", item.getName());
                        startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to chat with this member.");
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }

                }
            });

            btn_like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    likeRequest("Yes", item.getName(), position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeRequest("No", item.getName(), position);
                }
            });
            btn_block.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    blockRequest("add", item.getName());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    blockRequest("remove", item.getName());
                }
            });
            btn_interest.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(final LikeButton likeButton) {
                    likeButton.setLiked(false);
                    LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                    //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                    final RadioGroup grp_interest = (RadioGroup) vv.findViewById(R.id.grp_interest);

                    final BottomSheetDialog dialog = new BottomSheetDialog(context);
                    dialog.setContentView(vv);
                    dialog.show();

                    Button send = (Button) vv.findViewById(R.id.btn_send_intr);
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            if (grp_interest.getCheckedRadioButtonId() != -1) {
                                RadioButton btn = (RadioButton) vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                interestRequest(item.getName(), btn.getText().toString().trim(), likeButton);
                            }

                            //common.showAlert("Interest","You sent Interest to This Profile.",R.drawable.check_fill_green);
                            // likeButton.setLiked(true);
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    likeButton.setLiked(true);
                    common.showToast("You already sent interest to this user.");
                }
            });

            img_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                        //common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                        //                            item.getPhoto_view_status()).equals("male_password") ||
                        //                            common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                        //                                    item.getPhoto_view_status()).equals("female_password")
                        alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
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
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
                            common.showToast("Please upgrade your membership to view this profile.");
                            context.startActivity(new Intent(context, PlanListActivity.class));
                        }
                    }
                }
            });

            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.getPlan()) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getId());
                        context.startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to view this profile.");
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }
                }
            });

            btn_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.getPlan()) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getId());
                        context.startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to view this profile.");
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }
                }
            });

            return rowView;
        }

        private void alertPhotoPassword(final String password, final String url, final String matri_id) {
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

        private void alertpassword(final String password, final String url) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Enter Password");
            final EditText edittext = new EditText(context);

            edittext.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edittext.setHint("Password");
            alert.setView(edittext);
            alert.setPositiveButton("I don't have password", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // alertPhotoPassword();
                    dialogInterface.dismiss();
                }
            });
            alert.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (edittext.getText().toString().trim().equals(password)) {
                        final Dialog dialog = new Dialog(context);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.show_image_alert);
                        TouchImageView img_url = (TouchImageView) dialog.findViewById(R.id.img_url);
                        Picasso.get().load(url).placeholder(placeHolder).error(placeHolder).into(img_url);
                        dialog.show();
                    } else
                        Toast.makeText(context, "Password not match,Please try again.", Toast.LENGTH_LONG).show();

                }
            });
            alert.show();
        }

    }

    private void sendRequest(String int_msg, String matri_id) {
        pd = new ProgressDialog(SearchResultActivity.this);
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
                if (pd != null)
                    pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_LONG).show();

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

    private void likeRequest(final String tag, String matri_id, int index) {
        pd = new ProgressDialog(SearchResultActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequest(Utils.like_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if (tag.equals("Yes")) {
                        common.showAlert("Like", object.getString("errmessage"), R.drawable.heart_fill_pink);
                    } else
                        common.showAlert("Unlike", object.getString("errmessage"), R.drawable.heart_gray_fill);
                    // common.showAlert("Interest",object.getString("errmessage"),R.drawable.check_fill_green);
                    if (object.getString("status").equals("success")) {

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

    private void interestRequest(String matri_id, String int_msg, final LikeButton button) {
        pd = new ProgressDialog(SearchResultActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequest(Utils.send_interest, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);

                    //common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        button.setLiked(true);
                        common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                    } else
                        common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_gray_fill);

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

    private void blockRequest(final String tag, String id) {
        pd = new ProgressDialog(SearchResultActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        if (tag.equals("remove")) {
            param.put("unblockuserid", id);
        } else
            param.put("blockuserid", id);

        param.put("blacklist_action", tag);

        common.makePostRequest(Utils.block_user, param, response -> {
            if (pd != null)
                pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("add")) {
                    common.showAlert("Block", object.getString("errmessage"), R.drawable.ban);
                } else
                    common.showAlert("Unblock", object.getString("errmessage"), R.drawable.ban_gry);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ((pd != null) && pd.isShowing())
            pd.dismiss();
        super.onBackPressed();
    }
}
