package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PhotoPasswordFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    TextView lbl_photo_visi;//lbl_photo_pass
    //EditText et_password;
    Common common;
    SessionManager session;
    Context context;
    RadioGroup grp_visi;
    //TextView tv_remove_pass;
    ProgressDialog pd;
   // Button btn_submit;
    boolean isfirst=true;

    public PhotoPasswordFragment() {
        // Required empty public constructor
    }

    public static PhotoPasswordFragment newInstance(String param1, String param2) {
        PhotoPasswordFragment fragment = new PhotoPasswordFragment();
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
        View view=inflater.inflate(R.layout.fragment_photo_password, container, false);

        context =getActivity();
        session=new SessionManager(context);
        common=new Common(getActivity());

        //et_password=(EditText)view.findViewById(R.id.et_password);
        //tv_remove_pass=(TextView)view.findViewById(R.id.tv_remove_pass);
        lbl_photo_visi=(TextView)view.findViewById(R.id.lbl_photo_visi);
        common.setDrawableLeftTextViewLeft(R.drawable.eye_pink,lbl_photo_visi);

       /// btn_submit=(Button)view.findViewById(R.id.btn_submit);
        grp_visi=(RadioGroup)view.findViewById(R.id.grp_visi);

       // lbl_photo_pass=(TextView)view.findViewById(R.id.lbl_photo_pass);
        //common.setDrawableLeftTextViewLeft(R.drawable.password_pink,lbl_photo_pass);

        getMyProfile();

        grp_visi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!isfirst){
                    RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(i);
                    String val="";
                    if (checkedRadioButton.getText().toString().equals("Hide for All")){
                        val="0";
                    }else if (checkedRadioButton.getText().toString().equals("Visible to All") ){
                        val="1";
                    }else if (checkedRadioButton.getText().toString().equals("Visible to only paid members")){
                        val="2";
                    }
                    changeVisiApi(val);
                }

            }
        });

        /*tv_remove_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePhotoAlert();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass=et_password.getText().toString().trim();
                if (TextUtils.isEmpty(pass)){
                    et_password.setError("Please enter password.");
                    return;
                }
                if (pass.length()<6){
                    et_password.setError("Please enter valid password.");
                    return;
                }
                actionPassword("photo_password",pass);
            }
        });*/

        return view;
    }

    private void removePhotoAlert(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context);
        alert.setMessage("Are you sure remove photo password?");
        alert.setNegativeButton("No",null);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                actionPassword("remove","");
            }
        });
        alert.show();
    }

    private void actionPassword(final String tag, String pass){
        if(pd==null || pd.isShowing()) {
            pd = new ProgressDialog(context);
            pd.setIndeterminate(true);
            pd.setMessage("Loading...");
            pd.setCancelable(true);
            pd.show();
        }

        HashMap<String,String> param=new HashMap<>();
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        if (tag.equals("photo_password")){
            param.put("photo_password",pass);
        }
        param.put("action",tag);

        common.makePostRequest(Utils.photo_visibility_status, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")){
                        if (tag.equals("photo_password")){
                           // et_password.setText("");
                        }
                      //  getMyProfile();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void changeVisiApi(String val){
        if(pd==null || pd.isShowing()) {
            pd = new ProgressDialog(context);
            pd.setIndeterminate(true);
            pd.setMessage("Loading...");
            pd.setCancelable(true);
            pd.show();
        }

        HashMap<String,String> param=new HashMap<>();
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        param.put("photo_view_status",val);
        param.put("action","photo_view_status");

        common.makePostRequest(Utils.photo_visibility_status, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")){
                      //  getMyProfile();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
        if(pd==null || pd.isShowing()) {
            pd = new ProgressDialog(context);
            pd.setIndeterminate(true);
            pd.setMessage("Loading...");
            pd.setCancelable(true);
            pd.show();
        }

        HashMap<String,String> param=new HashMap<>();
        param.put("member_id",session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.get_my_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                //  Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN,object.getString("tocken"));
                    if (object.getString("status").equals("success")){
                        JSONObject data=object.getJSONObject("data");

                        String photo_view_status=data.getString("photo_view_status");
                        if (photo_view_status.equals("0")){
                            ((RadioButton)grp_visi.getChildAt(0)).setChecked(true);
                        }else if (photo_view_status.equals("1")){
                            ((RadioButton)grp_visi.getChildAt(1)).setChecked(true);
                        }else if (photo_view_status.equals("2")){
                            ((RadioButton)grp_visi.getChildAt(2)).setChecked(true);
                        }

                        String photo_protect=data.getString("photo_protect");
                        Log.d("resp",photo_protect+"  "+data.getString("photo_password"));
                        /*if (photo_protect.equals("Yes") && isValidString(data.getString("photo_password"))){
                            tv_remove_pass.setVisibility(View.VISIBLE);
                        }else
                            tv_remove_pass.setVisibility(View.GONE);*/

                    }
                    isfirst=false;

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

    private boolean isValidString(String url){
        return !url.equals("") && !url.equals("null");
    }

}
