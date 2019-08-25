package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Activities.SearchResultActivity;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class KeywordSearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    EditText et_keyword;
    Common common;
    SessionManager session;
    Context context;
    Button btn_save_search,btn_search;
    ProgressDialog pd;
    CheckBox checkBox;

    public KeywordSearchFragment() {
        // Required empty public constructor
    }

    public static KeywordSearchFragment newInstance(String param1, String param2) {
        KeywordSearchFragment fragment = new KeywordSearchFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_keyword_search, container, false);

        et_keyword=(EditText)view.findViewById(R.id.et_keyword);
        common.setDrawableLeftEditText(R.drawable.search_pink,et_keyword);
        checkBox=(CheckBox)view.findViewById(R.id.checkBox);
        btn_search=(Button)view.findViewById(R.id.btn_search);
        btn_save_search=(Button)view.findViewById(R.id.btn_save_search);
        btn_save_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchData();
            }
        });

        return view;
    }

    private void searchData() {
        final String key=et_keyword.getText().toString().trim();
        if (key.equals("")){
            et_keyword.setError("Keyword is require");
            return;
        }

        HashMap<String,String> param=new HashMap<>();
        param.put("member_id",session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("keyword",key);
        if (checkBox.isChecked()){
            param.put("photo_search","photo_search");
        }else
            param.put("photo_search","");

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")){
            param.put("gender","Male");
        }else {
            param.put("gender","Female");
        }


        Intent i=new Intent(context, SearchResultActivity.class);
        i.putExtra("searchData",Common.getJsonStringFromObject(param));
        //i.putExtra("searchData", param);
        startActivity(i);

    }

//    private void showAlert() {
//        final String key=et_keyword.getText().toString().trim();
//        if (key.equals("")){
//            et_keyword.setError("Keyword is require");
//            return;
//        }
//
//        AlertDialog.Builder alert=new AlertDialog.Builder(context);
//        alert.setTitle("SearchActivity Title");
//        alert.setMessage("Enter Title");
//        final EditText et=new EditText(context);
//        alert.setView(et);
//        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (et.getText().toString().trim().equals("")){
//                    common.showToast("Please enter Title");
//                    return;
//                }
//                Validadvance(et.getText().toString().trim(),key);
//            }
//        });
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        alert.show();
//    }

    private void showAlert() {
        final String key=et_keyword.getText().toString().trim();
        if (key.equals("")){
            et_keyword.setError("Keyword is require");
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_search, null);
        dialogBuilder.setView(dialogView);

        EditText editText = dialogView.findViewById(R.id.editText);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        AlertDialog alertDialog = dialogBuilder.create();

        btnCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        btnSave.setOnClickListener(view -> {
            if (editText.getText().length() > 0) {
                alertDialog.dismiss();
                Validadvance(editText.getText().toString().trim(),key);
            } else
                editText.setError("Please enter title");
        });

        alertDialog.show();
    }

    private void Validadvance(String name,String key) {
        HashMap<String,String> param=new HashMap<>();
        param.put("matri_id",session.getLoginData(SessionManager.KEY_matri_id));
        param.put("keyword",key);
        if (checkBox.isChecked()){
            param.put("photo_search","photo_search");
        }else
            param.put("photo_search","");
        param.put("save_search",name);
        param.put("search_page_nm", Utils.TYPE_SEARCH_KEYWORD);
        param.put("gender",session.getLoginData(SessionManager.KEY_GENDER));
        /*if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")){
            param.put("gender","Female");
        }else {
            param.put("gender","Male");
        }*/

        add_save(param);

    }

    private void add_save(HashMap<String,String> param) {
        pd=new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();
        common.makePostRequest(Utils.save_search, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object=new JSONObject(response);
                    common.showToast(object.getString("errormessage"));
                    if (object.getString("status").equals("success")){
                        et_keyword.setText("");
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
