package com.mega.matrimony.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.mega.matrimony.Activities.SearchResultActivity;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.MultiSelectionSpinner;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdvanceSearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tv_height_label, tv_min_height, tv_max_height, tv_weight_label, tv_min_age, tv_max_age,
            label_loc, label_edu, label_advance, label_eat, label_apper, label_horo;
    Common common;
    SessionManager session;
    private CrystalRangeSeekbar range_height, range_age;
    private MultiSelectionSpinner spin_mari, spin_religion, spin_caste, spin_tongue, spin_country, spin_state, spin_city, spin_edu,
            spin_ocu, spin_emp, spin_incom, spin_eat, spin_drink, spin_smok, spin_complex, spin_bodytype, spin_star, spin_manglik;
    HashMap<String, String> height_map = new HashMap<>();
    String religion_id = "", caste_id = "", tongue_id = "", country_id = "", state_id = "", city_id = "", mari_id = "", height_from = "",
            height_to = "", age_from = "", age_to = "", edu_id = "", manglik_id = "", star_id = "", bodytype_id = "", complex_id = "", smok_id = "",
            drink_id = "", incom_id = "", emp_id = "", eat_id = "", ocu_id = "";
    CheckBox chk_photo;
    Context context;
    private Button btn_save_search, btn_search;
    ProgressDialog pd;

    public AdvanceSearchFragment() {
        // Required empty public constructor
    }

    public static AdvanceSearchFragment newInstance(String param1, String param2) {
        AdvanceSearchFragment fragment = new AdvanceSearchFragment();
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

        View view = inflater.inflate(R.layout.fragment_advance_search, container, false);
        common = new Common(getActivity());
        context = getActivity();
        session = new SessionManager(getActivity());

        try {
            intitView(view);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_save_search = (Button) view.findViewById(R.id.btn_save_search);
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
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("from_age", age_from);
        param.put("to_age", age_to);
        param.put("from_height", height_from);
        param.put("to_height", height_to);
        param.put("looking_for", ischeckData(mari_id));
        param.put("religion", ischeckData(religion_id));
        param.put("caste", ischeckData(caste_id));
        param.put("mothertongue", ischeckData(tongue_id));
        param.put("country", ischeckData(country_id));
        param.put("state", ischeckData(state_id));
        param.put("city", ischeckData(city_id));
        param.put("education", ischeckData(edu_id));
        param.put("occupation", ischeckData(ocu_id));
        param.put("employee_in", ischeckData(emp_id));
        param.put("income", ischeckData(incom_id));
        param.put("diet", ischeckData(eat_id));
        param.put("drink", ischeckData(drink_id));
        param.put("smoking", ischeckData(smok_id));
        param.put("complexion", ischeckData(complex_id));
        param.put("bodytype", ischeckData(bodytype_id));
        param.put("star", ischeckData(star_id));
        param.put("manglik", ischeckData(manglik_id));
        if (chk_photo.isChecked()) {
            param.put("photo_search", "photo_search");
        } else
            param.put("photo_search", "");


        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            param.put("gender", "Male");
        } else {
            param.put("gender", "Female");
        }

        Intent i = new Intent(context, SearchResultActivity.class);
        i.putExtra("searchData", Common.getJsonStringFromObject(param));
        //i.putExtra("searchData", param);
        startActivity(i);
    }

    private void intitView(View view) throws JSONException {

        if (MyApplication.getSpinData() != null) {

            chk_photo = (CheckBox) view.findViewById(R.id.chk_photo);

            label_loc = (TextView) view.findViewById(R.id.label_loc);
            common.setDrawableLeftTextViewLeft(R.drawable.pin_location, label_loc);
            label_edu = (TextView) view.findViewById(R.id.label_edu);
            common.setDrawableLeftTextViewLeft(R.drawable.edu_pink, label_edu);
            label_advance = (TextView) view.findViewById(R.id.label_advance);
            common.setDrawableLeftTextViewLeft(R.drawable.search_pink, label_advance);
            label_eat = (TextView) view.findViewById(R.id.label_eat);
            common.setDrawableLeftTextViewLeft(R.drawable.eat_pink, label_eat);
            label_apper = (TextView) view.findViewById(R.id.label_apper);
            common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink, label_apper);
            label_horo = (TextView) view.findViewById(R.id.label_horo);
            common.setDrawableLeftTextViewLeft(R.drawable.horo_pink, label_horo);

            spin_mari = (MultiSelectionSpinner) view.findViewById(R.id.spin_mari);
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
            spin_caste = (MultiSelectionSpinner) view.findViewById(R.id.spin_caste);
            spin_religion = (MultiSelectionSpinner) view.findViewById(R.id.spin_religion);
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
                    if (religion_id != null && !religion_id.equals("0")) {
                        getDepedentList("caste_list", religion_id);
                    } else {
                        List<String> caste_val = new ArrayList<>();
                        caste_val.add("Caste");
                        List<String> caste_ids = new ArrayList<>();
                        caste_ids.add("0");
                        spin_caste.setItems_string_id(caste_val, caste_ids, "Select Caste");
                        spin_caste.setSelection(0);
                    }
                    Log.d("ressel", religion_id);
                }
            });


            List<String> caste_val = new ArrayList<>();
            caste_val.add("Caste");
            List<String> caste_ids = new ArrayList<>();
            caste_ids.add("0");
            spin_caste.setItems_string_id(caste_val, caste_ids, "Select Caste");
            spin_caste.setSelection(0);
            spin_caste.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {

                }

                @Override
                public void selectedStrings(List<String> strings) {

                }
            });

            spin_tongue = (MultiSelectionSpinner) view.findViewById(R.id.spin_tongue);
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

            spin_country = (MultiSelectionSpinner) view.findViewById(R.id.spin_country);
            spin_state = (MultiSelectionSpinner) view.findViewById(R.id.spin_state);
            spin_city = (MultiSelectionSpinner) view.findViewById(R.id.spin_city);
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
                    if (country_id != null && !country_id.equals("0")) {
                        getDepedentList("state_list", country_id);
                    }
                    resetStateAndCity();
                }
            });

            List<String> state_val = new ArrayList<>();
            state_val.add("State");
            List<String> state_ids = new ArrayList<>();
            state_ids.add("0");
            spin_state.setItems_string_id(state_val, state_ids, "Select State");
            spin_state.setSelection(0);
//        spin_state.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
//            @Override
//            public void selectedIndices(List<Integer> indices) {
//
//            }
//
//            @Override
//            public void selectedStrings(List<String> strings) {
//                //13/05/2019
//                //added for fixing not clear when state selected select option
//                city_id = "";
//                spin_city.setSelection(0);
//                //end
//            }
//        });

            List<String> city_val = new ArrayList<>();
            city_val.add("City");
            List<String> city_ids = new ArrayList<>();
            city_ids.add("0");
            spin_city.setItems_string_id(city_val, city_ids, "Select City");
            spin_city.setSelection(0);
//        spin_city.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
//            @Override
//            public void selectedIndices(List<Integer> indices) {
//
//            }
//
//            @Override
//            public void selectedStrings(List<String> strings) {
//
//            }
//        });

            spin_manglik = (MultiSelectionSpinner) view.findViewById(R.id.spin_manglik);
            List<String> manglik_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("manglik"), "Manglik");
            List<String> manglik_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("manglik"));
            spin_manglik.setItems_string_id(manglik_val, manglik_ids, "Select Manglik");
            spin_manglik.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    manglik_id = listToString(strings);
                    Log.d("ressel", manglik_id);
                }
            });

            spin_star = (MultiSelectionSpinner) view.findViewById(R.id.spin_star);
            List<String> star_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("star_list"), "Star");
            List<String> star_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("star_list"));
            spin_star.setItems_string_id(star_val, star_ids, "Select Star");
            spin_star.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    star_id = listToString(strings);
                    Log.d("ressel", star_id);
                }
            });

            spin_bodytype = (MultiSelectionSpinner) view.findViewById(R.id.spin_bodytype);
            List<String> bodytype_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("bodytype"), "Body Type");
            List<String> bodytype_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("bodytype"));
            spin_bodytype.setItems_string_id(bodytype_val, bodytype_ids, "Select Body Type");
            spin_bodytype.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    bodytype_id = listToString(strings);
                    Log.d("ressel", bodytype_id);
                }
            });

            spin_complex = (MultiSelectionSpinner) view.findViewById(R.id.spin_complex);
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

            spin_smok = (MultiSelectionSpinner) view.findViewById(R.id.spin_smok);
            List<String> smok_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("smoke"), "Smoking Habit");
            List<String> smok_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("smoke"));
            spin_smok.setItems_string_id(smok_val, smok_ids, "Select Smoking Habit");
            spin_smok.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    smok_id = listToString(strings);
                    Log.d("ressel", smok_id);
                }
            });

            spin_drink = (MultiSelectionSpinner) view.findViewById(R.id.spin_drink);
            List<String> drink_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("drink"), "Drinking Habit");
            List<String> drink_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("drink"));
            spin_drink.setItems_string_id(drink_val, drink_ids, "Select Drinking Habit");
            spin_drink.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    drink_id = listToString(strings);
                    Log.d("ressel", drink_id);
                }
            });

            spin_eat = (MultiSelectionSpinner) view.findViewById(R.id.spin_eat);
            List<String> eat_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("diet"), "Eating Habit");
            List<String> eat_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("diet"));
            spin_eat.setItems_string_id(eat_val, eat_ids, "Select Eating Habit");
            spin_eat.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    eat_id = listToString(strings);
                    Log.d("ressel", eat_id);
                }
            });

            spin_incom = (MultiSelectionSpinner) view.findViewById(R.id.spin_incom);
            List<String> incom_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
            List<String> incom_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("income"));
            spin_incom.setItems_string_id(incom_val, incom_ids, "Select Annual Income");
            spin_incom.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    incom_id = listToString(strings);
                    Log.d("ressel", incom_id);
                }
            });

            spin_emp = (MultiSelectionSpinner) view.findViewById(R.id.spin_emp);
            List<String> emp_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("employee_in"), "Employee In");
            List<String> emp_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("employee_in"));
            spin_emp.setItems_string_id(emp_val, emp_ids, "Select Employee In");
            spin_emp.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    emp_id = listToString(strings);
                    Log.d("ressel", emp_id);
                }
            });

            spin_edu = (MultiSelectionSpinner) view.findViewById(R.id.spin_edu);
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

            spin_ocu = (MultiSelectionSpinner) view.findViewById(R.id.spin_ocu);
            List<String> ocu_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("occupation_list"), "Occupation");
            List<String> ocu_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("occupation_list"));
            spin_ocu.setItems_string_id(ocu_val, ocu_ids, "Select Occupation");
            spin_ocu.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                @Override
                public void selectedIndices(List<Integer> indices) {
                    //  Log.d("ressel",indices.toString());
                }

                @Override
                public void selectedStrings(List<String> strings) {
                    ocu_id = listToString(strings);
                    Log.d("ressel", ocu_id);
                }
            });

            tv_height_label = (TextView) view.findViewById(R.id.tv_height_label);
            common.setDrawableLeftTextViewLeft(R.drawable.height_pink, tv_height_label);

            tv_min_height = (TextView) view.findViewById(R.id.search_tv_min_height);
            tv_max_height = (TextView) view.findViewById(R.id.search_tv_max_height);

            range_height = (CrystalRangeSeekbar) view.findViewById(R.id.search_range_height);
            JSONArray arr = MyApplication.getSpinData().getJSONArray("height_list");
            JSONObject obj = arr.getJSONObject(0);
            JSONObject obj1 = arr.getJSONObject(arr.length() - 1);
            range_height.setMinStartValue(Float.parseFloat(obj.getString("id"))).
                    setMaxStartValue(Float.parseFloat(obj1.getString("id"))).apply();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                if (object.getString("id").equals("48")) {
                    height_map.put(object.getString("id"), "Below 4ft");
                } else if (object.getString("id").equals("85")) {
                    height_map.put(object.getString("id"), "Above 7ft");
                } else {
                    height_map.put(object.getString("id"), object.getString("val"));
                }
            }

            range_height.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                @Override
                public void valueChanged(Number minValue, Number maxValue) {
                    Log.d("resp", minValue + "   " + maxValue);

                    height_from = String.valueOf(minValue);
                    height_to = String.valueOf(maxValue);
                    tv_min_height.setText(disHeight(height_from));
                    tv_max_height.setText(disHeight(height_to));
                    // tv_min_height.setText(String.valueOf(new DecimalFormat("##.#").format(minValue))+" ft");
                    // tv_max_height.setText(String.valueOf(new DecimalFormat("##.#").format(maxValue))+" ft");
                    // String[] frm=height_from.split(".");
                    // String[] to=height_to.split(".");
                    // tv_min_height.setText(frm[0]+"ft "+frm[1]+"in");
                    // tv_max_height.setText(to[0]+"ft "+to[1]+"in");

                }
            });

            tv_weight_label = (TextView) view.findViewById(R.id.tv_weight_label);
            common.setDrawableLeftTextViewLeft(R.drawable.weight_pink, tv_weight_label);

            tv_min_age = (TextView) view.findViewById(R.id.search_tv_min_age);
            tv_max_age = (TextView) view.findViewById(R.id.search_tv_max_age);

            range_age = (CrystalRangeSeekbar) view.findViewById(R.id.search_range_age);

            range_age.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
                @Override
                public void valueChanged(Number minValue, Number maxValue) {
                    age_from = String.valueOf(minValue);
                    age_to = String.valueOf(maxValue);
                    tv_min_age.setText(String.valueOf(minValue) + " Years");
                    tv_max_age.setText(String.valueOf(maxValue) + " Years");
                }
            });
        } else {
            getList(view);
        }
    }

    private void getList(final View view) {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();
        common.makePostRequest(Utils.common_list, new HashMap<String, String>(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                    MyApplication.setSpinData(object);
                    intitView(view);

                    pd.dismiss();

                } catch (JSONException e) {
                    pd.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp", error.getMessage() + "   ");
                pd.dismiss();
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
        pd = new ProgressDialog(context);
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

                                //List<String> caste=common.getListFromArray(object.getJSONArray("data"),"Caste");
                                //caste_map=common.getMapFromArray(object.getJSONArray("data"),"Caste");
                                //ArrayAdapter adp_caste=new ArrayAdapter<String>(context,R.layout.spinner_item,caste);
                                //spin_caste.setAdapter(adp_caste);
                                break;
                            case "state_list":
                                List<String> state_val = common.getListFromArray(object.getJSONArray("data"), "State");
                                List<String> state_ids = common.getListFromArrayId(object.getJSONArray("data"));
                                spin_state.setItems_string_id(state_val, state_ids, "Select State");
                                spin_state.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                    @Override
                                    public void selectedIndices(List<Integer> indices) {
                                        //  Log.d("ressel",indices.toString());
                                    }

                                    @Override
                                    public void selectedStrings(List<String> strings) {
                                        state_id = listToString(strings);
                                        if (state_id != null && !state_id.equals("0")) {
                                            getDepedentList("city_list", state_id);
                                        }
                                        resetCity();
                                    }
                                });
                                //List<String> state=common.getListFromArray(object.getJSONArray("data"),"State");
                                //state_map=common.getMapFromArray(object.getJSONArray("data"),"State");
                                //ArrayAdapter adp_stat=new ArrayAdapter<String>(context,R.layout.spinner_item,state);
                                //spin_state.setAdapter(adp_stat);
                                break;
                            case "city_list":
                                List<String> city_val = common.getListFromArray(object.getJSONArray("data"), "City");
                                List<String> city_ids = common.getListFromArrayId(object.getJSONArray("data"));
                                spin_city.setItems_string_id(city_val, city_ids, "Select City");
                                spin_city.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                    @Override
                                    public void selectedIndices(List<Integer> indices) {
                                        //  Log.d("ressel",indices.toString());
                                    }

                                    @Override
                                    public void selectedStrings(List<String> strings) {
                                        city_id = listToString(strings);
                                        Log.d("ressel", city_id);
                                    }
                                });
                                //List<String> city=common.getListFromArray(object.getJSONArray("data"),"City");
                                //city_map=common.getMapFromArray(object.getJSONArray("data"),"City");
                                //ArrayAdapter adp_city=new ArrayAdapter<String>(context,R.layout.spinner_item,city);
                                //spin_city.setAdapter(adp_city);
                                break;
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp", error.getMessage() + "   ");
                pd.dismiss();
            }
        });

    }

    private void resetStateAndCity() {
        List<String> state_val = new ArrayList<>();
        state_val.add("State");
        List<String> state_ids = new ArrayList<>();
        state_ids.add("0");
        spin_state.setItems_string_id(state_val, state_ids, "Select State");
        spin_state.setSelection(0);
        state_id = "";

        resetCity();
    }

    private void resetCity() {
        List<String> city_val = new ArrayList<>();
        city_val.add("City");
        List<String> city_ids = new ArrayList<>();
        city_ids.add("0");
        spin_city.setItems_string_id(city_val, city_ids, "Select City");
        spin_city.setSelection(0);

        city_id = "";
    }

//    private void showAlert() {
//        AlertDialog.Builder alert = new AlertDialog.Builder(context);
//        alert.setTitle("Search Title");
//        alert.setMessage("Enter Title");
//        final EditText et = new EditText(context);
//        alert.setView(et);
//        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if (et.getText().toString().trim().equals("")) {
//                    common.showToast("Please enter Title");
//                    return;
//                }
//                Validadvance(et.getText().toString().trim());
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
                Validadvance(editText.getText().toString().trim());
            } else
                editText.setError("Please enter title");
        });

        alertDialog.show();
    }

    private void Validadvance(String name) {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("from_age", age_from);
        param.put("to_age", age_to);
        param.put("from_height", height_from);
        param.put("to_height", height_to);
        param.put("looking_for", ischeckData(mari_id));
        param.put("religion", ischeckData(religion_id));
        param.put("caste", ischeckData(caste_id));
        param.put("mothertongue", ischeckData(tongue_id));
        param.put("country", ischeckData(country_id));
        param.put("state", ischeckData(state_id));
        param.put("city", ischeckData(city_id));
        param.put("education", ischeckData(edu_id));
        param.put("occupation", ischeckData(ocu_id));
        param.put("employee_in", ischeckData(emp_id));
        param.put("income", ischeckData(incom_id));
        param.put("diet", ischeckData(eat_id));
        param.put("drink", ischeckData(drink_id));
        param.put("smoking", ischeckData(smok_id));
        param.put("complexion", ischeckData(complex_id));
        param.put("bodytype", ischeckData(bodytype_id));
        param.put("star", ischeckData(star_id));
        param.put("manglik", ischeckData(manglik_id));
        if (chk_photo.isChecked()) {
            param.put("photo_search", "photo_search");
        } else
            param.put("photo_search", "");
        param.put("save_search", name);
        param.put("search_page_nm", Utils.TYPE_SEARCH_ADVANCE);
        param.put("gender", session.getLoginData(SessionManager.KEY_GENDER));

        /*if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")){
            param.put("gender","Female");
        }else {
            param.put("gender","Male");
        }*/

        add_save(param);

    }

    private void add_save(HashMap<String, String> param) {
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.setIndeterminate(true);
        pd.show();
        common.makePostRequest(Utils.save_search, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errormessage"));
                    if (object.getString("status").equals("success")) {

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

    private String ischeckData(String val) {
        if (val == null || val.equals("0")) {
            return "";
        }
        return val;
    }

}
