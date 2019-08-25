package com.mega.matrimony.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.MultiSelectionSpinner;
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

public class CustomMatchActivity extends AppCompatActivity {
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    private MultiSelectionSpinner spin_mari, spin_complex, spin_tongue, spin_religion, spin_caste, spin_country, spin_edu;
    private TextView tv_min_height, tv_max_height, search_tv_min_age, search_tv_max_age;
    private CrystalRangeSeekbar range_height, search_range_age;
    private Button btn_save_search;
    Common common;
    SessionManager session;
    String mari_id = "", religion_id = "", tongue_id = "", country_id = "", edu_id = "", height_from = "",
            height_to = "", complex_id = "", caste_id = "", age_from, age_to;
    HashMap<String, String> height_map = new HashMap<>();
    ProgressDialog pd;
    List<DashboardItem> list = new ArrayList<>();
    int page = 0;
    ListView lv_match;
    boolean continue_request;
    TextView tv_no_data;
    ListAdapter adapter;
    private ImageView btnClose;
    private Toolbar toolbar;
    private int placeHolder,photoProtectPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_match);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Custom Matches");

        common = new Common(this);
        session = new SessionManager(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        lv_match = findViewById(R.id.lv_match);
        tv_no_data = findViewById(R.id.tv_no_data);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        btnClose = findViewById(R.id.btnClose);

        btnClose.setOnClickListener(view -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            toolbar.setVisibility(View.VISIBLE);
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        toolbar.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        toolbar.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        toolbar.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btn_save_search = (Button) findViewById(R.id.btn_save_search);
        spin_mari = (MultiSelectionSpinner) findViewById(R.id.spin_mari);
        spin_complex = (MultiSelectionSpinner) findViewById(R.id.spin_complex);
        spin_tongue = (MultiSelectionSpinner) findViewById(R.id.spin_tongue);
        spin_religion = (MultiSelectionSpinner) findViewById(R.id.spin_religion);
        spin_caste = (MultiSelectionSpinner) findViewById(R.id.spin_caste);
        spin_country = (MultiSelectionSpinner) findViewById(R.id.spin_country);
        spin_edu = (MultiSelectionSpinner) findViewById(R.id.spin_edu);
        tv_min_height = (TextView) findViewById(R.id.search_tv_min_height);
        tv_max_height = (TextView) findViewById(R.id.search_tv_max_height);

        search_tv_min_age = (TextView) findViewById(R.id.search_tv_min_age);
        search_tv_max_age = (TextView) findViewById(R.id.search_tv_max_age);

        lv_match.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        getListData(page);
                    }
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_save_search.setOnClickListener(view -> checkData());

        adapter = new ListAdapter(CustomMatchActivity.this, list);
        lv_match.setAdapter(adapter);

    }

    private void checkData() {
        if (mari_id.equals("") || mari_id.equals(",") || mari_id.equals("0")) {
            common.showToast("Please select looking for.");
            return;
        }
        if (religion_id.equals("") || religion_id.equals(",") || religion_id.equals("0")) {
            common.showToast("Please select religion.");
            return;
        }
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        HashMap<String, String> param = new HashMap<>();
        param.put("looking_for", isValid(mari_id));
        param.put("part_frm_age", isValid(age_from));
        param.put("part_to_age", isValid(age_to));
        param.put("part_height", isValid(height_from));
        param.put("part_height_to", isValid(height_to));
        param.put("part_complexion", isValid(complex_id));
        param.put("part_mother_tongue", isValid(tongue_id));
        param.put("part_religion", isValid(religion_id));
        param.put("part_caste", isValid(caste_id));
        param.put("part_country_living", isValid(country_id));
        param.put("part_education", isValid(edu_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        Log.d("resp", param.toString());
        submitData(param);
    }

    private String isValid(String val) {
        if (val.equals("0") || val.equals("null")) {
            return "";
        }
        return val;
    }

    private void submitData(HashMap<String, String> param) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        common.makePostRequest(Utils.save_matches, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
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

    private void getMyProfile() {
        if (pd == null || !pd.isShowing()) {
            pd = new ProgressDialog(CustomMatchActivity.this);
            pd.setIndeterminate(true);
            pd.setMessage("Loading...");
            pd.setCancelable(false);
            pd.show();
        }

        final HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.get_my_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                //Log.d("resp",response);
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {
                        JSONObject data = object.getJSONObject("data");

                        mari_id = data.getString("looking_for");
                        height_from = data.getString("part_height");
                        height_to = data.getString("part_height_to");
                        complex_id = data.getString("part_complexion");
                        tongue_id = data.getString("part_mother_tongue");
                        religion_id = data.getString("part_religion");
                        caste_id = data.getString("part_caste");
                        country_id = data.getString("part_country_living");
                        edu_id = data.getString("part_education");
                        age_from = data.getString("part_frm_age");
                        age_to = data.getString("part_to_age");

                        search_range_age.setMinStartValue(Float.parseFloat(age_from))
                                .setMaxStartValue(Float.parseFloat(age_to)).apply();

                        spin_mari.setSelection(data.getString("looking_for").split(","));
                        range_height.setMinStartValue(Float.parseFloat(data.getString("part_height")))
                                .setMaxStartValue(Float.parseFloat(data.getString("part_height_to"))).apply();
                        spin_complex.setSelection(data.getString("part_complexion").split(","));
                        spin_tongue.setSelection(data.getString("part_mother_tongue").split(","));
                        spin_religion.setSelection(data.getString("part_religion").split(","));
                        if (!data.getString("part_religion").equals("") &&
                                !data.getString("part_religion").equals("null")) {
                            getDepedentList("caste_list", data.getString("part_religion"));
                            pd.dismiss();
                        }

                        spin_country.setSelection(data.getString("part_country_living").split(","));
                        spin_edu.setSelection(data.getString("part_education").split(","));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later));
                }
                page = page + 1;
                getListData(page);

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

    private void getListData(int page) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setCancelable(true);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.search_now + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    int total_count = object.getInt("total_count");
                    getSupportActionBar().setTitle("Custom Matches (" + total_count + " Members)");
                    continue_request = object.getBoolean("continue_request");

                    if (total_count != 0) {
                        tv_no_data.setVisibility(View.GONE);
                        lv_match.setVisibility(View.VISIBLE);
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
                        lv_match.setVisibility(View.GONE);
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

    private void sendRequest(String int_msg, String matri_id) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
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
                    Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_LONG).show();

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

    private void likeRequest(final String tag, String matri_id, int index) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequest(Utils.like_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

    private void interestRequest(String matri_id, String int_msg, final LikeButton button) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequest(Utils.send_interest, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

    private void blockRequest(final String tag, String id) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        if (tag.equals("remove")) {
            param.put("unblockuserid", id);
        } else
            param.put("blockuserid", id);

        param.put("blacklist_action", tag);

        common.makePostRequest(Utils.block_user, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

    public class ListAdapter extends ArrayAdapter<DashboardItem> {
        Context context;
        List<DashboardItem> list;
        Common common;

        public ListAdapter(Context context, List<DashboardItem> list) {
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

//            String about = item.getAge() + ", " + item.getHeight() + ", " +
//                    item.getReligion() + "...<font color='#ff041a'>Read More</font>";//"27 Year,5'6'' Height,Gujarati-Hindu,Orlando-Canada | Computers IT Profile Created By Self...<font color='#ff041a'>Read More</font>";

            String description = Common.getDetailsFromValue(item.getAge(), item.getHeight(),
                    item.getCaste(), item.getReligion(),
                    item.getCity(), item.getCountry());

            tv_detail.setText(Html.fromHtml(description));

            btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.getPlan()) {
                        context.startActivity(new Intent(context, ConversationActivity.class));
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

            img_profile.setOnClickListener(view1 -> {
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
            });

            tv_name.setOnClickListener(view12 -> {
                if (MyApplication.getPlan()) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getId());
                    context.startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to view this profile.");
                    context.startActivity(new Intent(context, PlanListActivity.class));
                }
            });

            btn_profile.setOnClickListener(view13 -> {
                if (MyApplication.getPlan()) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getId());
                    context.startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to view this profile.");
                    context.startActivity(new Intent(context, PlanListActivity.class));
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

    private void initData() throws JSONException {
        if (MyApplication.getSpinData() != null) {
            search_range_age = findViewById(R.id.search_range_age);

            search_range_age.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                age_to = String.valueOf(maxValue);
                age_from = String.valueOf(minValue);

                search_tv_max_age.setText(maxValue + " Years");
                search_tv_min_age.setText(minValue + " Years");
            });

            range_height = (CrystalRangeSeekbar) findViewById(R.id.search_range_height);
            AppDebugLog.print("MyApplication.getSpinData() : " + MyApplication.getSpinData().getJSONArray("height_list"));
            JSONArray arr = MyApplication.getSpinData().getJSONArray("height_list");
            JSONObject obj = arr.getJSONObject(1);
            JSONObject obj1 = arr.getJSONObject(arr.length() - 1);
            range_height.setMinStartValue(Float.parseFloat(obj.getString("id"))).
                    setMaxStartValue(Float.parseFloat(obj1.getString("id"))).apply();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                if (i == 0) {

                } else if (object.getString("id").equals("85")) {
                    height_map.put(object.getString("id"), "Above 7ft");
                } else {
                    height_map.put(object.getString("id"), object.getString("val"));
                }
            }

            range_height.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {

                height_from = String.valueOf(minValue);
                height_to = String.valueOf(maxValue);
                tv_min_height.setText(disHeight(height_from));
                tv_max_height.setText(disHeight(height_to));
            });

            List<String> mari_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("marital_status"), "Marital Status");
            List<String> mari_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("marital_status"));
            spin_mari.setItems_string_id(mari_val, mari_ids, "Select Marital Status");
            spin_mari.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    mari_id = listToString(strings);
                    Log.d("ressel", mari_id);
                }
            });

            List<String> reli_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("religion_list"), "Religion");
            List<String> reli_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("religion_list"));
            spin_religion.setItems_string_id(reli_val, reli_ids, "Select Religion");
            spin_religion.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    religion_id = listToString(strings);
                    if (religion_id != null && !religion_id.equals("0"))
                        getDepedentList("caste_list", religion_id);
                    Log.d("ressel", religion_id);
                }
            });

            List<String> complex_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("complexion"), "Complexion");
            List<String> complex_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("complexion"));
            spin_complex.setItems_string_id(complex_val, complex_ids, "Select Complexion");
            spin_complex.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    complex_id = listToString(strings);
                    Log.d("ressel", complex_id);
                }
            });

            List<String> caste_val = new ArrayList<>();
            caste_val.add("Select Caste");
            List<String> caste_ids = new ArrayList<>();
            caste_ids.add("0");
            spin_caste.setItems_string_id(caste_val, caste_ids, "Select Caste");


            List<String> tong_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue");
            List<String> tong_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("mothertongue_list"));
            spin_tongue.setItems_string_id(tong_val, tong_ids, "Select Mother Tongue");
            spin_tongue.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    tongue_id = listToString(strings);
                    Log.d("ressel", tongue_id);
                }
            });


            List<String> contryval = common.getListFromArray(MyApplication.getSpinData().getJSONArray("country_list"), "Country");
            List<String> contry_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("country_list"));
            spin_country.setItems_string_id(contryval, contry_ids, "Select Country");
            spin_country.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    country_id = listToString(strings);
                    Log.d("ressel", country_id);
                }
            });


            List<String> edu_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("education_list"), "Education");
            List<String> edu_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("education_list"));
            spin_edu.setItems_string_id(edu_val, edu_ids, "Select Education");
            spin_edu.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    edu_id = listToString(strings);
                    Log.d("ressel", edu_id);
                }
            });
            getMyProfile();
        } else {
            getList();
        }
    }

    private void getList() {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        common.makePostRequest(Utils.common_list, new HashMap<String, String>(), response -> {
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                MyApplication.setSpinData(object);
                initData();
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            if (pd != null)
                pd.dismiss();
            if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
        });
    }

    private String disHeight(String val) {
        return height_map.get(val);
    }

    private String listToString(List<String> list) {
        String listString = "";

        for (String s : list) {
            listString += s + ",";// \t
        }

        listString = listString.replaceAll(",$", "");
        return listString;
    }

    private void getDepedentList(final String tag, String id) {
        pd = new ProgressDialog(CustomMatchActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "multi");
        param.put("retun_for", "json");

        common.makePostRequest(Utils.common_depedent_list, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response + "   ");
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {

                        switch (tag) {
                            case "caste_list":
                                List<String> caste_val = common.getListFromArray(object.getJSONArray("data"), "Caste");
                                List<String> caste_ids = common.getListFromArrayId(object.getJSONArray("data"));
                                spin_caste.setItems_string_id(caste_val, caste_ids, "Select Caste");
                                spin_caste.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                    @Override
                                    public void selectedIndices(List<Integer> indices) {
                                        //  Log.d("ressel",indices.toString());
                                    }

                                    @Override
                                    public void selectedStrings(List<String> strings) {
                                        caste_id = listToString(strings);
                                        Log.d("ressel", caste_id);
                                    }
                                });
                                if (!caste_id.equals("")) {
                                    spin_caste.setSelection(caste_id.split(","));
                                }
                                break;
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
                Log.d("resp", error.getMessage() + "   ");
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_match_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.filter) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            toolbar.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }

}
