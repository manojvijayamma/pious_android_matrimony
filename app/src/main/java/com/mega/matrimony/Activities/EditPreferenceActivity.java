package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.MultiSelectionSpinner;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditPreferenceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Common common;
    SessionManager session;
    String pageTag = "";
    LinearLayout lay_basic, lay_reli, lay_edu, lay_loca;
    Button btn_basic, btn_reli, btn_loca, btn_edu;
    EditText et_expcts;
    Spinner spin_height_from, spin_height_to, spin_age_from, spin_age_to, spin_manglik, spin_income;
    MultiSelectionSpinner spin_looking, spin_complx, spin_body_type, spin_eat, spin_smok, spin_drink, spin_mtongue,
            spin_religion, spin_caste, spin_star, spin_residence, spin_country, spin_state, spin_city, spin_edu,
            spin_emp_in, spin_occupation, spin_designation;
    String look_id = "", complx_id = "", body_type_id = "", eat_id = "", smoke_id = "", drink_id = "", mtongue_id = "", fage_id = "", tage_id = "", incm_id = "",
            fhite_id = "", thite_id = "", reli_id = "", manglik_id = "", caste_id = "", star_id = "", resi_id = "", country_id = "", state_id = "",
            city_id = "", edu_id = "", emp_in_id = "", occu_id = "", desi_id = "";
    HashMap<String, String> fhite_map, thite_map, fage_map, tage_map, manglik_map, income_map;
    List<String> fhite_list_id, thite_list_id, fage_list_id, tage_list_id, manglik_list_id, incm_list_id;
    ProgressDialog pd;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_preference);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Preference");

        common = new Common(this);
        session = new SessionManager(this);

        btn_basic = (Button) findViewById(R.id.btn_basic);
        btn_basic.setOnClickListener(this);
        btn_reli = (Button) findViewById(R.id.btn_reli);
        btn_reli.setOnClickListener(this);
        btn_loca = (Button) findViewById(R.id.btn_loca);
        btn_loca.setOnClickListener(this);
        btn_edu = (Button) findViewById(R.id.btn_edu);
        btn_edu.setOnClickListener(this);

        lay_basic = (LinearLayout) findViewById(R.id.lay_basic);
        lay_reli = (LinearLayout) findViewById(R.id.lay_reli);
        lay_edu = (LinearLayout) findViewById(R.id.lay_edu);
        lay_loca = (LinearLayout) findViewById(R.id.lay_loca);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("pageTag")) {
                pageTag = b.getString("pageTag");
                switch (pageTag) {
                    case "basic":
                        toolbar.setTitle("Basic Preferences");
                        lay_basic.setVisibility(View.VISIBLE);
                        break;
                    case "religion":
                        toolbar.setTitle("Religion Preferences");
                        lay_reli.setVisibility(View.VISIBLE);
                        break;
                    case "education":
                        toolbar.setTitle("Education & Occupation Preferences");
                        lay_edu.setVisibility(View.VISIBLE);
                        break;
                    case "location":
                        toolbar.setTitle("Location Preferences");
                        lay_loca.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initData() throws JSONException {
        if (MyApplication.getSpinData() != null) {
            //spin_edu,spin_emp_in,spin_income,spin_occupation,spin_designation
            switch (pageTag) {
                case "education":
                    spin_edu = (MultiSelectionSpinner) findViewById(R.id.spin_edu);
                    List<String> edu_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("education_list"), "Education");
                    List<String> edu_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("education_list"));
                    spin_edu.setItems_string_id(edu_val, edu_ids, "Select Education");
                    spin_edu.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            edu_id = listToString(strings);
                        }
                    });
                    spin_edu.setSelection(0);

                    spin_emp_in = (MultiSelectionSpinner) findViewById(R.id.spin_emp_in);
                    List<String> emp_in_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("employee_in"), "Employed In");
                    List<String> emp_in_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("employee_in"));
                    spin_emp_in.setItems_string_id(emp_in_val, emp_in_ids, "Select Employed In");
                    spin_emp_in.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            emp_in_id = listToString(strings);
                        }
                    });
                    spin_emp_in.setSelection(0);

                    spin_occupation = (MultiSelectionSpinner) findViewById(R.id.spin_occupation);
                    List<String> occu_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("occupation_list"), "Occupation");
                    List<String> occu_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("occupation_list"));
                    spin_occupation.setItems_string_id(occu_val, occu_ids, "Select Occupation");
                    spin_occupation.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            occu_id = listToString(strings);
                        }
                    });
                    spin_occupation.setSelection(0);

                    spin_designation = (MultiSelectionSpinner) findViewById(R.id.spin_designation);
                    List<String> desi_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("designation_list"), "Designation");
                    List<String> desi_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("designation_list"));
                    spin_designation.setItems_string_id(desi_val, desi_ids, "Select Designation");
                    spin_designation.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            desi_id = listToString(strings);
                        }
                    });
                    spin_designation.setSelection(0);

                    spin_income = (Spinner) findViewById(R.id.spin_income);
                    incm_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
                    List<String> incm = common.getListFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
                    income_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
                    ArrayAdapter adp_incm = new ArrayAdapter<String>(this, R.layout.spinner_item, incm);
                    spin_income.setAdapter(adp_incm);
                    spin_income.setOnItemSelectedListener(this);

                    break;
                case "location":
                    spin_residence = findViewById(R.id.spin_residence);
                    List<String> resi_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("residence"), "Residence");
                    List<String> resi_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("residence"));
                    spin_residence.setItems_string_id(resi_val, resi_ids, "Select Residence");
                    spin_residence.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            resi_id = listToString(strings);
                        }
                    });
                    spin_residence.setSelection(0);

                    spin_state = (MultiSelectionSpinner) findViewById(R.id.spin_state);
                    spin_city = (MultiSelectionSpinner) findViewById(R.id.spin_city);
                    spin_country = (MultiSelectionSpinner) findViewById(R.id.spin_country);
                    List<String> country_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("country_list"), "Country");
                    List<String> country_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("country_list"));
                    spin_country.setItems_string_id(country_val, country_ids, "Select Country");
                    spin_country.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            country_id = listToString(strings);
                            AppDebugLog.print("country_id in initData : " + country_id);
                            if (country_id != null && !country_id.equals("0") && !country_id.equals("")) {
                                getDepedentList("state_list", country_id);
                            }
                            resetStateAndCity();
                        }
                    });
                    spin_country.setSelection(0);

                    List<String> state_val = new ArrayList<>();
                    state_val.add("Select State");
                    List<String> state_ids = new ArrayList<>();
                    state_ids.add("0");
                    spin_state.setItems_string_id(state_val, state_ids, "Select State");
                    spin_state.setSelection(0);
                    spin_state.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {

                        }

                        @Override
                        public void selectedStrings(List<String> strings) {

                        }
                    });

                    List<String> city_val = new ArrayList<>();
                    city_val.add("Select City");
                    List<String> city_ids = new ArrayList<>();
                    city_ids.add("0");
                    spin_city.setItems_string_id(city_val, city_ids, "Select City");
                    spin_city.setSelection(0);
                    spin_city.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {

                        }

                        @Override
                        public void selectedStrings(List<String> strings) {

                        }
                    });

                    break;
                case "religion":
                    spin_manglik = (Spinner) findViewById(R.id.spin_manglik);
                    manglik_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("manglik"),
                            "Manglik");
                    List<String> manglik = common.getListFromArray(MyApplication.getSpinData().getJSONArray("manglik"),
                            "Manglik");
                    manglik_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("manglik"),
                            "Manglik");
                    ArrayAdapter adp_manglik = new ArrayAdapter<String>(this, R.layout.spinner_item, manglik);
                    spin_manglik.setAdapter(adp_manglik);
                    spin_manglik.setOnItemSelectedListener(this);

                    spin_religion = (MultiSelectionSpinner) findViewById(R.id.spin_religion);
                    spin_caste = (MultiSelectionSpinner) findViewById(R.id.spin_caste);
                    List<String> reli_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("religion_list"), "Religion");
                    List<String> reli_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("religion_list"));
                    spin_religion.setItems_string_id(reli_val, reli_ids, "Select Religion");
                    spin_religion.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            reli_id = listToString(strings);
                            Log.d("ressel", reli_id);
                            if (reli_id != null && !reli_id.equals("0") && !reli_id.equals(""))
                                getDepedentList("caste_list", reli_id);
                            else {
                                List<String> caste_val = new ArrayList<>();
                                caste_val.add("Select Caste");
                                List<String> caste_ids = new ArrayList<>();
                                caste_ids.add("0");
                                spin_caste.setItems_string_id(caste_val, caste_ids, "Select Caste");
                                spin_caste.setSelection(0);
                            }
                        }
                    });
                    spin_religion.setSelection(0);

                    List<String> caste_val = new ArrayList<>();
                    caste_val.add("Select Caste");
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

                    spin_star = (MultiSelectionSpinner) findViewById(R.id.spin_star);
                    List<String> star_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("star_list"), "Star");
                    List<String> star_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("star_list"));
                    spin_star.setItems_string_id(star_val, star_ids, "Select Star");
                    spin_star.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            star_id = listToString(strings);
                            Log.d("ressel", star_id);
                        }
                    });
                    spin_star.setSelection(0);

                    break;
                case "basic":
                    et_expcts = (EditText) findViewById(R.id.et_expcts);

                    spin_looking = (MultiSelectionSpinner) findViewById(R.id.spin_looking);
                    List<String> looking_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("marital_status"), "Looking For");
                    List<String> looking_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("marital_status"));
                    spin_looking.setItems_string_id(looking_val, looking_ids, "Select Looking For");
                    spin_looking.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            look_id = listToString(strings);
                            Log.d("ressel", look_id);
                        }
                    });
                    spin_looking.setSelection(0);

                    spin_complx = (MultiSelectionSpinner) findViewById(R.id.spin_complx);
                    List<String> complx_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("complexion"), "Skin Tone");
                    List<String> complx_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("complexion"));
                    spin_complx.setItems_string_id(complx_val, complx_ids, "Select Skin Tone");
                    spin_complx.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            complx_id = listToString(strings);
                            Log.d("ressel", complx_id);
                        }
                    });
                    spin_complx.setSelection(0);

                    spin_height_from = (Spinner) findViewById(R.id.spin_height_from);
                    fhite_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("height_list"),
                            "From Height");
                    List<String> fhit = common.getListFromArray(MyApplication.getSpinData().getJSONArray("height_list"),
                            "From Height");
                    fhite_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("height_list"),
                            "From Height");
                    ArrayAdapter adp_hit = new ArrayAdapter<String>(this, R.layout.spinner_item, fhit);
                    spin_height_from.setAdapter(adp_hit);
                    spin_height_from.setOnItemSelectedListener(this);

                    spin_height_to = (Spinner) findViewById(R.id.spin_height_to);
                    thite_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("height_list"),
                            "To Height");
                    List<String> thit = common.getListFromArray(MyApplication.getSpinData().getJSONArray("height_list"),
                            "To Height");
                    thite_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("height_list"),
                            "From Height");
                    ArrayAdapter adp_thit = new ArrayAdapter<String>(this, R.layout.spinner_item, thit);
                    spin_height_to.setAdapter(adp_thit);
                    spin_height_to.setOnItemSelectedListener(this);

                    spin_age_from = (Spinner) findViewById(R.id.spin_age_from);
                    fage_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("age_rang"),
                            "From Age");
                    List<String> fage = common.getListFromArray(MyApplication.getSpinData().getJSONArray("age_rang"),
                            "From Age");
                    fage_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("age_rang"),
                            "From Age");
                    ArrayAdapter adp_fage = new ArrayAdapter<String>(this, R.layout.spinner_item, fage);
                    spin_age_from.setAdapter(adp_fage);
                    spin_age_from.setOnItemSelectedListener(this);

                    spin_age_to = (Spinner) findViewById(R.id.spin_age_to);
                    tage_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("age_rang"),
                            "To Age");
                    List<String> tage = common.getListFromArray(MyApplication.getSpinData().getJSONArray("age_rang"),
                            "To Age");
                    tage_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("age_rang"),
                            "To Age");
                    ArrayAdapter adp_tage = new ArrayAdapter<String>(this, R.layout.spinner_item, tage);
                    spin_age_to.setAdapter(adp_tage);
                    spin_age_to.setOnItemSelectedListener(this);

                    spin_body_type = (MultiSelectionSpinner) findViewById(R.id.spin_body_type);
                    List<String> body_type_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("bodytype"), "Body type");
                    List<String> body_type_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("bodytype"));
                    spin_body_type.setItems_string_id(body_type_val, body_type_ids, "Select Body type");
                    spin_body_type.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            body_type_id = listToString(strings);
                            Log.d("ressel", body_type_id);
                        }
                    });
                    spin_body_type.setSelection(0);

                    spin_eat = (MultiSelectionSpinner) findViewById(R.id.spin_eat);
                    List<String> eat_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("diet"), "Eating Habit");
                    List<String> eat_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("diet"));
                    spin_eat.setItems_string_id(eat_val, eat_ids, "Select Eating Habit");
                    spin_eat.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            eat_id = listToString(strings);
                            Log.d("ressel", eat_id);
                        }
                    });
                    spin_eat.setSelection(0);

                    spin_smok = (MultiSelectionSpinner) findViewById(R.id.spin_smok);
                    List<String> smok_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("smoke"), "Smoking Habit");
                    List<String> smok_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("smoke"));
                    spin_smok.setItems_string_id(smok_val, smok_ids, "Select Smoking Habit");
                    spin_smok.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            smoke_id = listToString(strings);
                            Log.d("ressel", smoke_id);
                        }
                    });
                    spin_smok.setSelection(0);

                    spin_drink = (MultiSelectionSpinner) findViewById(R.id.spin_drink);
                    List<String> drink_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("drink"), "Drinking Habit");
                    List<String> drink_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("drink"));
                    spin_drink.setItems_string_id(drink_val, drink_ids, "Select Drinking Habit");
                    spin_drink.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            drink_id = listToString(strings);
                            Log.d("ressel", drink_id);
                        }
                    });
                    spin_drink.setSelection(0);

                    spin_mtongue = (MultiSelectionSpinner) findViewById(R.id.spin_mtongue);
                    List<String> mtongue_val = common.getListFromArray(MyApplication.getSpinData()
                            .getJSONArray("mothertongue_list"), "Mother Tongue");
                    List<String> mtongue_ids = common.getListFromArrayId(MyApplication.getSpinData()
                            .getJSONArray("mothertongue_list"));
                    spin_mtongue.setItems_string_id(mtongue_val, mtongue_ids, "Select Mother Tongue");
                    spin_mtongue.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            mtongue_id = listToString(strings);
                            Log.d("ressel", mtongue_id);
                        }
                    });
                    spin_mtongue.setSelection(0);
                    break;
            }
            getMyProfile();
        } else {
            getList();
        }
    }

    private void getList() {
        pd = new ProgressDialog(EditPreferenceActivity.this);
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
            pd.dismiss();
        });
    }

    private void getDepedentList(final String tag, String id) {
        if (pd != null) {
            pd.dismiss();
        }
        pd = new ProgressDialog(EditPreferenceActivity.this);
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
                Log.d("resp", tag + "   ");
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {

                        if ("caste_list".equals(tag)) {
                            List<String> caste_val = common.getListFromArray(object.getJSONArray("data"), "Caste");
                            List<String> caste_ids = common.getListFromArrayId(object.getJSONArray("data"));
                            spin_caste.setItems_string_id(caste_val, caste_ids, "Select Caste");
                            spin_caste.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override
                                public void selectedIndices(List<Integer> indices) {
                                }

                                @Override
                                public void selectedStrings(List<String> strings) {
                                    caste_id = listToString(strings);
                                    Log.d("ressel", caste_id);
                                }
                            });
                            spin_caste.setSelection(caste_id.split(","));
                        } else if ("state_list".equals(tag)) {
                            List<String> state_val = common.getListFromArray(object.getJSONArray("data"), "State");
                            List<String> state_ids = common.getListFromArrayId(object.getJSONArray("data"));
                            spin_state.setItems_string_id(state_val, state_ids, "Select State");
                            spin_state.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override
                                public void selectedIndices(List<Integer> indices) {
                                }

                                @Override
                                public void selectedStrings(List<String> strings) {
                                    state_id = listToString(strings);
                                    Log.d("ressel", state_id);
                                    if (state_id != null && !state_id.equals("0") && !state_id.equals("")) {
                                        getDepedentList("city_list", state_id);
                                    }
                                    resetCity();
                                }
                            });
                            AppDebugLog.print("state id in getDepedentList : " + state_id);
                            if (state_id.length() > 0)
                                spin_state.setSelection(state_id.split(","));
                        } else if ("city_list".equals(tag)) {
                            List<String> city_val = common.getListFromArray(object.getJSONArray("data"), "City");
                            List<String> city_ids = common.getListFromArrayId(object.getJSONArray("data"));
                            spin_city.setItems_string_id(city_val, city_ids, "Select City");
                            spin_city.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                                @Override
                                public void selectedIndices(List<Integer> indices) {
                                }

                                @Override
                                public void selectedStrings(List<String> strings) {
                                    city_id = listToString(strings);
                                }
                            });
                            AppDebugLog.print("city id in getDepedentList: " + city_id);
                            if (city_id.length() > 0)
                                spin_city.setSelection(city_id.split(","));
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

    //13052019 reset state and city when country selected
    private void resetStateAndCity() {
        List<String> state_val = new ArrayList<>();
        state_val.add("Select State");
        List<String> state_ids = new ArrayList<>();
        state_ids.add("0");
        state_id = "";
        spin_state.setItems_string_id(state_val, state_ids, "Select State");
        spin_state.setSelection(0);

        resetCity();
    }

    //13052019 reset city when state selected
    private void resetCity() {
        List<String> city_val = new ArrayList<>();
        city_val.add("Select City");
        List<String> city_ids = new ArrayList<>();
        city_ids.add("0");
        city_id = "";
        spin_city.setItems_string_id(city_val, city_ids, "Select City");
        spin_city.setSelection(0);
    }

    private void getMyProfile() {
        pd = new ProgressDialog(EditPreferenceActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.get_my_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {
                        JSONObject data = object.getJSONObject("data");

                        switch (pageTag) {
                            case "education":
                                edu_id = data.getString("part_education");
                                emp_in_id = data.getString("part_employee_in");
                                occu_id = data.getString("part_occupation");
                                desi_id = data.getString("part_designation");
                                incm_id = data.getString("part_income");

                                spin_edu.setSelection(edu_id.split(","));
                                spin_emp_in.setSelection(emp_in_id.split(","));
                                spin_occupation.setSelection(occu_id.split(","));
                                spin_designation.setSelection(desi_id.split(","));
                                common.setSelection(spin_income, incm_list_id, incm_id);
                                break;
                            case "location":
                                country_id = data.getString("part_country_living");
                                spin_country.setSelection(country_id.split(","));
                                city_id = data.getString("part_city");
                                state_id = data.getString("part_state");
                                if (country_id != null && !country_id.equals("0") && !country_id.equals(""))
                                    getDepedentList("state_list", country_id);

                                if (state_id != null && !state_id.equals("0") && !state_id.equals(""))
                                    getDepedentList("city_list", state_id);

                                resi_id = data.getString("part_resi_status");
                                spin_residence.setSelection(resi_id.split(","));
                                break;
                            case "religion":
                                reli_id = data.getString("part_religion");
                                caste_id = data.getString("part_caste");
                                if (reli_id != null && !reli_id.equals("0") && !reli_id.equals(""))
                                    getDepedentList("caste_list", reli_id);

                                star_id = data.getString("part_star");
                                manglik_id = data.getString("part_manglik");
                                spin_religion.setSelection(reli_id.split(","));

                                spin_star.setSelection(star_id.split(","));
                                common.setSelection(spin_manglik, manglik_list_id, manglik_id);
                                break;
                            case "basic":
                                et_expcts.setText(data.getString("part_expect"));

                                fage_id = data.getString("part_frm_age");
                                tage_id = data.getString("part_to_age");
                                fhite_id = data.getString("part_height");
                                thite_id = data.getString("part_height_to");

                                common.setSelection(spin_age_from, fage_list_id, fage_id);
                                common.setSelection(spin_age_to, tage_list_id, tage_id);
                                common.setSelection(spin_height_from, fhite_list_id, fhite_id);
                                common.setSelection(spin_height_to, thite_list_id, thite_id);

                                look_id = data.getString("looking_for");
                                complx_id = data.getString("part_complexion");
                                body_type_id = data.getString("part_bodytype");
                                eat_id = data.getString("part_diet");
                                drink_id = data.getString("part_drink");
                                smoke_id = data.getString("part_smoke");
                                mtongue_id = data.getString("part_mother_tongue");

                                spin_looking.setSelection(look_id.split(","));
                                spin_complx.setSelection(complx_id.split(","));
                                spin_body_type.setSelection(body_type_id.split(","));
                                spin_eat.setSelection(eat_id.split(","));
                                spin_drink.setSelection(drink_id.split(","));
                                spin_smok.setSelection(smoke_id.split(","));
                                spin_mtongue.setSelection(mtongue_id.split(","));

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
                if (pd != null)
                    pd.dismiss();
                if(error.networkResponse!=null) {
             common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
}
            }
        });
    }

    private void validBasicData() {
        String expcts = et_expcts.getText().toString().trim();

        boolean isValid = true;

        if (look_id.equals("") || look_id.equals("0")) {
            common.spinnerSetError(spin_looking, "Please select Looking For");
            isValid = false;
        }
        if (fage_id.equals("") || fage_id.equals("0")) {
            common.spinnerSetError(spin_age_from, "Please select from age");
            isValid = false;
        }
        if (tage_id.equals("") || tage_id.equals("0")) {
            common.spinnerSetError(spin_age_to, "Please select to age");
            isValid = false;
        }
        if (fhite_id.equals("") || fhite_id.equals("0")) {
            common.spinnerSetError(spin_height_from, "Please select from height");
            isValid = false;
        }
        if (thite_id.equals("") || thite_id.equals("0")) {
            common.spinnerSetError(spin_height_to, "Please select to height");
            isValid = false;
        }

        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("looking_for", ischeckData(look_id));
            param.put("part_complexion", ischeckData(complx_id));
            param.put("part_frm_age", fage_id);
            param.put("part_to_age", tage_id);
            param.put("part_height", fhite_id);
            param.put("part_height_to", thite_id);
            param.put("part_bodytype", ischeckData(body_type_id));
            param.put("part_diet", ischeckData(eat_id));
            param.put("part_smoke", ischeckData(smoke_id));
            param.put("part_drink", ischeckData(drink_id));
            param.put("part_mother_tongue", ischeckData(mtongue_id));
            param.put("part_expect", ischeckData(expcts));
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            submitData(param);
        }
    }

    private void validReliData() {
        if (reli_id.equals("") || reli_id.equals("0")) {
            common.spinnerSetError(spin_religion, "Please select religion");
            return;
        }
        HashMap<String, String> param = new HashMap<>();
        param.put("part_religion", ischeckData(reli_id));
        param.put("part_caste", ischeckData(caste_id));
        param.put("part_manglik", ischeckData(manglik_id));
        param.put("part_star", ischeckData(star_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        submitData(param);
    }

    private void validcountryData() {

        HashMap<String, String> param = new HashMap<>();
        param.put("part_country_living", ischeckData(country_id));
        param.put("part_state", ischeckData(state_id));
        param.put("part_city", ischeckData(city_id));
        param.put("part_resi_status", ischeckData(resi_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        submitData(param);
    }

    private void validEduData() {

        HashMap<String, String> param = new HashMap<>();
        param.put("part_education", ischeckData(edu_id));
        param.put("part_occupation", ischeckData(occu_id));
        param.put("part_designation", ischeckData(desi_id));
        param.put("part_employee_in", ischeckData(emp_in_id));
        param.put("part_income", ischeckData(incm_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        submitData(param);
    }

    private void submitData(HashMap<String, String> param) {
        pd = new ProgressDialog(EditPreferenceActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        common.makePostRequest(Utils.edit_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", "reload");
                        returnIntent.putExtra("tabid", "pref");
                        setResult(RESULT_OK, returnIntent);
                        finish();
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

    private String ischeckData(String val) {
        if (val == null || val.equals("0")) {
            return "";
        }
        return val;
    }

    private String listToString(List<String> list) {
        String listString = "";

        for (String s : list) {
            listString += s + ",";// \t
        }

        listString = listString.replaceAll(",$", "");
        return listString;
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_basic:
                validBasicData();
                break;
            case R.id.btn_reli:
                validReliData();
                break;
            case R.id.btn_loca:
                validcountryData();
                break;
            case R.id.btn_edu:
                validEduData();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        switch (spinner.getId()) {
            case R.id.spin_height_from:
                fhite_id = fhite_map.get(spin_height_from.getSelectedItem().toString());
                break;
            case R.id.spin_height_to:
                thite_id = thite_map.get(spin_height_to.getSelectedItem().toString());
                break;
            case R.id.spin_age_from:
                fage_id = fage_map.get(spin_age_from.getSelectedItem().toString());
                break;
            case R.id.spin_age_to:
                tage_id = tage_map.get(spin_age_to.getSelectedItem().toString());
                break;
            case R.id.spin_manglik:
                manglik_id = manglik_map.get(spin_manglik.getSelectedItem().toString());
                break;
            case R.id.spin_income:
                incm_id = income_map.get(spin_income.getSelectedItem().toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
