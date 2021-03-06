package com.mega.usnazrani.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mega.usnazrani.Activities.ConversationActivity;
import com.mega.usnazrani.Activities.OtherUserProfileActivity;
import com.mega.usnazrani.Activities.PlanListActivity;
import com.mega.usnazrani.Application.MyApplication;
import com.mega.usnazrani.Custom.NonScrollListView;
import com.mega.usnazrani.Custom.TouchImageView;
import com.mega.usnazrani.Model.DashboardItem;
import com.mega.usnazrani.R;
import com.mega.usnazrani.Utility.AppDebugLog;
import com.mega.usnazrani.Utility.ApplicationData;
import com.mega.usnazrani.Utility.Common;
import com.mega.usnazrani.Utility.SessionManager;
import com.mega.usnazrani.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecommendationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private NonScrollListView lv_recom;
    ProgressDialog pd;
    Context context;
    Common common;
    SessionManager session;
    List<DashboardItem> list;
    RecomdationAdapter adapter;
    private boolean shouldRefreshOnResume = false;
    private int placeHolder,photoProtectPlaceHolder;

    private TextView tv_no_data;

    public RecommendationFragment() {
        // Required empty public constructor
    }

    public static RecommendationFragment newInstance(String param1, String param2) {
        RecommendationFragment fragment = new RecommendationFragment();
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
        list = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendation, container, false);

        context = getActivity();
        common = new Common(context);
        session = new SessionManager(context);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }


        lv_recom = (NonScrollListView) view.findViewById(R.id.lv_recom);
        tv_no_data = view.findViewById(R.id.tv_no_data);

        AppDebugLog.print("in onCreateView of recomm");
        getData();

        adapter = new RecomdationAdapter(getActivity(), list);
        lv_recom.setAdapter(adapter);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // Log.d("frgchang",isVisibleToUser+"   ");
        if (isVisibleToUser) {
//            list.clear();
//            adapter.notifyDataSetChanged();
//            getData();
        }

        //super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDebugLog.print("in onResume of recomm");
        if (ApplicationData.getSharedInstance().isProfileChanged) {
            ApplicationData.getSharedInstance().isProfileChanged = false;
            if (pd != null)
                pd.dismiss();
            list.clear();
            adapter.notifyDataSetChanged();
            getData();
        }
        // getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        shouldRefreshOnResume = true;
//    }

    private void getData() {
        pd = new ProgressDialog(context);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(Utils.recent_login, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", "recomm");
                //Log.d("resp",response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("success")) {

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
                        if(list.size()==0){
                            tv_no_data.setVisibility(View.VISIBLE);
                        }else{
                            tv_no_data.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
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
        pd = new ProgressDialog(context);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_matri_id));

        common.makePostRequestTime(Utils.photo_password_request, param, new Response.Listener<String>() {
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

    private void likeRequest(final String tag, String matri_id, int index) {
        pd = new ProgressDialog(context);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequestTime(Utils.like_profile, param, new Response.Listener<String>() {
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
        pd = new ProgressDialog(context);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequestTime(Utils.send_interest, param, new Response.Listener<String>() {
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
        pd = new ProgressDialog(context);
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

        common.makePostRequestTime(Utils.block_user, param, new Response.Listener<String>() {
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
            /*switch (common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                    item.getPhoto_view_status())){
                case "male_password":
                    img_profile.setImageResource(R.drawable.photopassword_male);
                    break;
                case "female_password":
                    img_profile.setImageResource(R.drawable.photopassword_female);
                    break;
                case "male":
                    img_profile.setImageResource(R.drawable.male);
                    break;
                case "female":
                    img_profile.setImageResource(R.drawable.female);
                    break;
                case "url":
                    Picasso.get().load(item.getImage()).into(img_profile);
                    break;
            }*/

            tv_name.setText(item.getName().toUpperCase());

//            String about=item.getAge()+", "+item.getHeight()+", "+
//                    item.getReligion()+"...<font color='#ff041a'>Read More</font>";//"27 Year,5'6'' Height,Gujarati-Hindu,Orlando-Canada | Computers IT Profile Created By Self...<font color='#ff041a'>Read More</font>";

            String description = Common.getDetailsFromValue(item.getAge() + " Years, ", item.getHeight(),
                    item.getCaste(), item.getReligion(),
                    item.getCity(), item.getCountry());

            tv_detail.setText(Html.fromHtml(description));

            tv_detail.setOnClickListener(new View.OnClickListener() {
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

            btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.getPlan()) {
                        // context.startActivity(new Intent(context,ConversationActivity.class));
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
                        Picasso.get().load(item.getImage()).into(img_url);
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
                        Picasso.get().load(url).into(img_url);
                        dialog.show();
                    } else
                        Toast.makeText(context, "Password not match,Please try again.", Toast.LENGTH_LONG).show();

                }
            });
            alert.show();
        }

    }

}
