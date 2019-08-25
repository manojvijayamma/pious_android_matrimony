package com.mega.matrimony.Fragments;

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
import com.android.volley.VolleyError;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mega.matrimony.Activities.ConversationActivity;
import com.mega.matrimony.Activities.OtherUserProfileActivity;
import com.mega.matrimony.Activities.PlanListActivity;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.Model.DashboardItem;
import com.mega.matrimony.R;
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

public class ViewedMyProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView lv_iviewed;

    List<DashboardItem> list = new ArrayList<>();
    Common common;
    SessionManager session;
    public ProgressDialog pd;
    boolean continue_request;
    TextView tv_no_data;
    ViewedAdapter adapter;
    int page = 0;
    Context context;
    private int placeHolder,photoProtectPlaceHolder;

    public ViewedMyProfileFragment() {
        // Required empty public constructor
    }

    public static ViewedMyProfileFragment newInstance(String param1, String param2) {
        ViewedMyProfileFragment fragment = new ViewedMyProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_viewed_my_profile, container, false);

        context = getActivity();
        common = new Common(getActivity());
        session = new SessionManager(getActivity());

        lv_iviewed = (ListView) view.findViewById(R.id.lv_iviewed);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }


        page = page + 1;
        getListData(page);

        adapter = new ViewedAdapter(getActivity(), list);
        lv_iviewed.setAdapter(adapter);

        lv_iviewed.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        pd.setCancelable(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(Utils.who_viewed_list + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    int total_count = object.getInt("total_count");
                    if (total_count != 0) {
                        tv_no_data.setVisibility(View.GONE);
                        lv_iviewed.setVisibility(View.VISIBLE);
                        continue_request = object.getBoolean("continue_request");
                        if (list.size() != total_count) {
                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                DashboardItem item = new DashboardItem();
                                item.setId(obj.getString("id"));
                                item.setMatri_id(obj.getString("matri_id"));
                                item.setUser_id(obj.getString("user_id"));
                                item.setName(obj.getString("username"));

//                                String about=common.getAge(obj.getString("birthdate"))+" Year, "+
//                                        checkData(obj.getString("height"))+
//                                        checkData(obj.getString("caste_name"))+checkData(obj.getString("religion_name"))+
//                                        checkData(obj.getString("city_name"))+checkData(obj.getString("country_name"));
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                String description = Common.getDetailsFromValue(common.getAge(obj.getString("birthdate"), sdf) + " Years, ", obj.getString("height"),
                                        obj.getString("caste_name"), obj.getString("religion_name"),
                                        obj.getString("city_name"), obj.getString("country_name"));
                                item.setAbout(description);

                                item.setImage_approval(obj.getString("photo1_approve"));
                                item.setImage(obj.getString("photo1"));
                                item.setPhoto_view_count(obj.getString("photo_view_count"));
                                item.setPhoto_view_status(obj.getString("photo_view_status"));

                                JSONArray action = obj.getJSONArray("action");
                                item.setAction(action.getJSONObject(0));

                                list.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        }

                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        lv_iviewed.setVisibility(View.GONE);
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

    private void likeRequest(final String tag, String matri_id, final int index) {
        pd = new ProgressDialog(context);
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
        pd = new ProgressDialog(context);
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

    public class ViewedAdapter extends ArrayAdapter<DashboardItem> {
        Context context;
        List<DashboardItem> list;
        Common common;

        public ViewedAdapter(Context context, List<DashboardItem> list) {
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
            }
       */
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


            tv_name.setText(item.getMatri_id().toUpperCase());

            //String about=item.getAbout()+"...<font color='#ff041a'>Read More</font>";//"27 Year,5'6'' Height,Gujarati-Hindu,Orlando-Canada | Computers IT Profile Created By Self...<font color='#ff041a'>Read More</font>";

            tv_detail.setText(Html.fromHtml(item.getAbout()));

            tv_detail.setOnClickListener(new View.OnClickListener() {
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


            btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyApplication.getPlan()) {
                        Intent i = new Intent(context, ConversationActivity.class);
                        i.putExtra("matri_id", item.getMatri_id());
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
                    setAction(item.getAction(), "is_like", "Yes");
                    likeRequest("Yes", item.getMatri_id(), position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    setAction(item.getAction(), "is_like", "No");
                    likeRequest("No", item.getMatri_id(), position);
                }
            });
            btn_block.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    setAction(item.getAction(), "is_block", "1");
                    blockRequest("add", item.getMatri_id());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    setAction(item.getAction(), "is_block", "0");
                    blockRequest("remove", item.getMatri_id());
                }
            });
            btn_interest.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(final LikeButton likeButton) {
                    setAction(item.getAction(), "is_interest", "interest");
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
                                interestRequest(item.getMatri_id(), btn.getText().toString().trim(), likeButton);
                            }

                            //common.showAlert("Interest","You sent Interest to This Profile.",R.drawable.check_fill_green);
                            // likeButton.setLiked(true);
                        }
                    });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    //setAction(item.getAction(),"is_interest","");
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

            tv_name.setOnClickListener(new View.OnClickListener() {
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

            btn_profile.setOnClickListener(new View.OnClickListener() {
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

            return rowView;
        }

        private void setAction(JSONObject jsonObject, String key, String value) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

}
