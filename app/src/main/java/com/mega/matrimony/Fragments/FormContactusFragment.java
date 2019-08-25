package com.mega.matrimony.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class FormContactusFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    Context context;
    Common common;
    private EditText et_f_name,et_email,et_mobile,et_subject,et_about;
    private CountryCodePicker spin_code;
    private Button btn_submit;
    SessionManager session;
    private ProgressDialog pd;
    private CoordinatorLayout main;

    public FormContactusFragment() {
        // Required empty public constructor
    }

    public static FormContactusFragment newInstance(String param1, String param2) {
        FormContactusFragment fragment = new FormContactusFragment();
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
        common=new Common(getActivity());
        session=new SessionManager(getActivity());
        context=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_form_contactus, container, false);

        et_f_name=(EditText) view.findViewById(R.id.et_f_name);
        et_email=(EditText) view.findViewById(R.id.et_email);
        et_subject=(EditText) view.findViewById(R.id.et_subject);
        spin_code=(CountryCodePicker) view.findViewById(R.id.spin_code);
        et_mobile=(EditText) view.findViewById(R.id.et_mobile);
        et_about=(EditText) view.findViewById(R.id.et_about);
        btn_submit=(Button)view.findViewById(R.id.btn_id);
        main=(CoordinatorLayout) view.findViewById(R.id.main);

        common.setDrawableLeftEditText(R.drawable.user_pink,et_f_name);
        common.setDrawableLeftEditText(R.drawable.email,et_email);
        common.setDrawableLeftEditText(R.drawable.mobile_pink,et_mobile);
        common.setDrawableLeftEditText(R.drawable.user_pink,et_subject);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validData();
            }
        });

        return view;
    }

    private void validData() {
        String name=et_f_name.getText().toString().trim();
        String email=et_email.getText().toString().trim();
        String code=spin_code.getSelectedCountryCodeWithPlus();
        String mobile=et_mobile.getText().toString().trim();
        String subject=et_subject.getText().toString().trim();
        String message=et_about.getText().toString().trim();
        boolean isvalid=true;
        if (TextUtils.isEmpty(name)){
            et_f_name.setError("Please enter name");
            isvalid=false;
        }
        if (TextUtils.isEmpty(email)){
            et_email.setError("Please enter email");
            isvalid=false;
        }
        if (!common.isValidEmail(email)){
            et_email.setError("Please enter valid email");
            isvalid=false;
        }
        if (TextUtils.isEmpty(mobile)){
            et_mobile.setError("Please enter mobile number");
            isvalid=false;
        }
        if (mobile.length()<8){
            et_mobile.setError("Please enter valid mobile number");
            isvalid=false;
        }
        if (TextUtils.isEmpty(subject)){
            et_subject.setError("Please enter subject");
            isvalid=false;
        }
        if (TextUtils.isEmpty(message)){
            et_about.setError("Please enter message");
            isvalid=false;
        }
        if (isvalid){
            HashMap<String ,String > param=new HashMap<>();
            param.put("name",name);
            param.put("email",email);
            param.put("country_code",code);
            param.put("phone",mobile);
            param.put("subject",subject);
            param.put("description",message);
            submitData(param);

        }
    }

    private void submitData(HashMap<String ,String > param) {
        pd=new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();

        common.makePostRequest(Utils.contact_form, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp",response);
                try {
                    JSONObject object=new JSONObject(response);
                    //Snackbar.make(main,object.getString("errmessage"),Snackbar.LENGTH_LONG).show();
                    common.showToast(object.getString("errmessage"));
                  //  Toast.makeText(getActivity(),object.getString("errmessage")+"  ",Toast.LENGTH_LONG).show();
                    if (object.getString("status").equals("success")){
                        et_about.setText("");
                        et_f_name.setText("");
                        et_email.setText("");
                        et_subject.setText("");
                        et_mobile.setText("");
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
