package com.mega.matrimony.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ContactSettingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView lbl_contact_visi;
    Common common;
    RadioGroup grp_visi;
    SessionManager session;
    Context context;
    Button btn_submit;
    ProgressDialog pd;

    public ContactSettingFragment() {
        // Required empty public constructor
    }

    public static ContactSettingFragment newInstance(String param1, String param2) {
        ContactSettingFragment fragment = new ContactSettingFragment();
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
        View view=inflater.inflate(R.layout.fragment_contact_setting, container, false);

        common=new Common(getActivity());
        session=new SessionManager(getActivity());
        context=getActivity();

        lbl_contact_visi=(TextView)view.findViewById(R.id.lbl_contact_visi);
        common.setDrawableLeftTextViewLeft(R.drawable.eye_pink,lbl_contact_visi);
        btn_submit=(Button)view.findViewById(R.id.btn_id);
        grp_visi=(RadioGroup)view.findViewById(R.id.grp_visi);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeContact();
            }
        });

        getMyProfile();

        return view;
    }

    private void getMyProfile() {
        pd=new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

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

                        String contact_view_security=data.getString("contact_view_security");
                        if (contact_view_security.equals("0")){
                            ((RadioButton)grp_visi.getChildAt(1)).setChecked(true);
                        }else if (contact_view_security.equals("1")){
                            ((RadioButton)grp_visi.getChildAt(0)).setChecked(true);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        });
    }

    private void changeContact() {
        int pos=grp_visi.getCheckedRadioButtonId();
        if (pos==-1){
            common.showToast("Please select contact visibility");
            return;
        }

        pd=new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        HashMap<String,String> param=new HashMap<>();
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        RadioButton btn=((RadioButton)grp_visi.findViewById(grp_visi.getCheckedRadioButtonId()));

        if (btn.getText().toString().equals("Show to all paid members")){
            param.put("contact_view_security","1");
        }else if(btn.getText().toString().equals("Show to only express interest accepted and paid members")){
            param.put("contact_view_security","0");
        }

        common.makePostRequest(Utils.contact_setting, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        });

    }


}
