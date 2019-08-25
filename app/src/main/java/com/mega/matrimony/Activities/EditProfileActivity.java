package com.mega.matrimony.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.MultiSelectionSpinner;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.rilixtech.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private final String KEY_BASIC = "basic";
    private final String KEY_RELIGION = "religion";
    private final String KEY_PROFILE = "profile";
    private final String KEY_EDUCATION = "education";
    private final String KEY_LIFE = "life";
    private final String KEY_LOCATION = "location";
    private final String KEY_FAMILY = "family";

    private EditText et_f_name, et_l_name, et_dob, et_about, et_hoby, et_birth_place, et_birth_time, et_sub_caste, et_gothra, et_address,
            et_mobile, et_phone, et_time_call, et_father_name, et_father_ocu, et_mother_name, et_mother_ocu, et_about_family;
    private Spinner spin_religion, spin_mari, spin_t_child, spin_child_status, spin_tongue, spin_height, spin_weight,
            spin_body, spin_eat, spin_smok, spin_drink, spin_skin, spin_blood,
            spin_created, spin_reference, spin_caste, spin_manglik, spin_star, spin_horo, spin_moon,
            spin_country, spin_state, spin_city, spin_residence, spin_emp_in, spin_income, spin_occupation, spin_designation,
            spin_family_type, spin_family_status, spin_no_bro, spin_no_mari_bro, spin_no_sis, spin_no_mari_sis;
    private CountryCodePicker spin_code;
    private MultiSelectionSpinner spin_lang, spin_edu;
    private RelativeLayout lay_child_status, lay_t_child;
    private Button btn_basic, btn_life, btn_about, btn_reli, btn_loca, btn_edu, btn_family;
    private Common common;
    private SessionManager session;
    private final Calendar myCalendar = Calendar.getInstance();
    private final Calendar mcurrentTime = Calendar.getInstance();
    private String pageTag = "";
    private LinearLayout lay_basic, lay_life, lay_about, lay_reli, lay_loca, lay_edu, lay_family;
    private HashMap<String, String> reli_map, caste_map, tongue_map, country_map, state_map, city_map, mari_map, total_child_map,
            status_child_map, emp_map, income_map, occu_map, desig_map, hite_map, weight_map, eat_map, smok_map, drink_map,
            body_map, skin_map, manglik_map, star_map, horo_map, moon_map, blood_map, created_map, reference_map, resi_map,
            family_type_map, family_status_map, no_bro_map, no_mari_bro_map, no_sis_map, no_mari_sis_map;
    private String religion_id = "", caste_id = "", tongue_id = "",
            country_id = "", state_id = "", city_id = "", mari_id = "", total_child_id = "", status_child_id,
            edu_id = "", emp_id = "", income_id = "", occu_id = "", desig_id = "", hite_id = "", weight_id = "", eat_id = "", smok_id = "", drink_id = "",
            body_id = "", skin_id = "", manglik_id = "", star_id = "", horo_id = "", moon_id = "", lang_id = "", blood_id = "", created_id = "",
            reference_id = "", resi_id = "", code_id = "", family_type_id = "", family_status_id = "", no_bro_id = "", no_mari_bro_id = "",
            no_sis_id = "", no_mari_sis_id = "";

    private ProgressDialog pd;
    private List<String> mari_list_id, tot_list_id, status_list_id, tong_list_id, hit_list_id, wite_list_id,
            body_list_id, eat_list_id, smok_list_id, drink_list_id, skin_list_id, blood_list_id, created_list_id, reference_list_id,
            reli_list_id, caste_list_id, man_list_id, star_list_id, horo_list_id, moon_list_id, contry_list_id, state_list_id,
            city_list_id, residence_list_id, emp_list_id, incm_list_id, ocu_list_id, desi_list_id, family_type_list_id,
            family_status_list_id, no_bro_list_id, no_mari_bro_list_id, no_sis_list_id, no_mari_sis_list_id;
    private SimpleDateFormat mFormat = null;
    private boolean isLoaded = false;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        common = new Common(this);
        session = new SessionManager(this);

        lay_basic = (LinearLayout) findViewById(R.id.lay_basic);
        lay_life = (LinearLayout) findViewById(R.id.lay_life);
        lay_about = (LinearLayout) findViewById(R.id.lay_about);
        lay_reli = (LinearLayout) findViewById(R.id.lay_reli);
        lay_loca = (LinearLayout) findViewById(R.id.lay_loca);
        lay_edu = (LinearLayout) findViewById(R.id.lay_edu);
        lay_family = (LinearLayout) findViewById(R.id.lay_family);

        lay_child_status = (RelativeLayout) findViewById(R.id.lay_child_status);
        lay_t_child = (RelativeLayout) findViewById(R.id.lay_t_child);

        btn_basic = (Button) findViewById(R.id.btn_basic);
        btn_basic.setOnClickListener(this);
        btn_life = (Button) findViewById(R.id.btn_life);
        btn_life.setOnClickListener(this);
        btn_about = (Button) findViewById(R.id.btn_about);
        btn_about.setOnClickListener(this);
        btn_reli = (Button) findViewById(R.id.btn_reli);
        btn_reli.setOnClickListener(this);
        btn_loca = (Button) findViewById(R.id.btn_loca);
        btn_loca.setOnClickListener(this);
        btn_edu = (Button) findViewById(R.id.btn_edu);
        btn_edu.setOnClickListener(this);
        btn_family = (Button) findViewById(R.id.btn_family);
        btn_family.setOnClickListener(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("pageTag")) {
                pageTag = b.getString("pageTag");
                switch (pageTag) {
                    case KEY_BASIC:
                        lay_basic.setVisibility(View.VISIBLE);
                        toolbar.setTitle("Basic Details");
                        break;
                    case KEY_RELIGION:
                        lay_reli.setVisibility(View.VISIBLE);
                        toolbar.setTitle("Religion Information");
                        break;
                    case KEY_PROFILE:
                        lay_about.setVisibility(View.VISIBLE);
                        toolbar.setTitle("About Us & Hobby");
                        break;
                    case KEY_EDUCATION:
                        lay_edu.setVisibility(View.VISIBLE);
                        toolbar.setTitle("Education & Occupation Information");
                        break;
                    case KEY_LIFE:
                        lay_life.setVisibility(View.VISIBLE);
                        toolbar.setTitle("Life Style Details");
                        break;
                    case KEY_LOCATION:
                        lay_loca.setVisibility(View.VISIBLE);
                        toolbar.setTitle("Location Details");
                        break;
                    case KEY_FAMILY:
                        lay_family.setVisibility(View.VISIBLE);
                        toolbar.setTitle("Family Details");
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

    private void getMyProfile() {
        if (pd == null || !pd.isShowing()) {
            pd = new ProgressDialog(EditProfileActivity.this);
            pd.setIndeterminate(true);
            pd.setMessage("Loading...");
            pd.setCancelable(true);
            pd.show();
        }

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.get_my_profile, param, response -> {
            pd.dismiss();
            // Log.d("resp",response);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    switch (pageTag) {
                        case KEY_BASIC:
                            et_f_name.setText(data.getString("firstname"));
                            et_l_name.setText(data.getString("lastname"));
                            //Log.d("EditProfileap",mari_map.toString());
                            //common.setSelection(spin_religion,"religion_list",data.getString("religion"));
                            mari_id = data.getString("marital_status");
                            total_child_id = data.getString("total_children");
                            status_child_id = data.getString("status_children");
                            tongue_id = data.getString("mother_tongue");
                            hite_id = data.getString("height");
                            weight_id = data.getString("weight");
                            lang_id = data.getString("languages_known");
                            if (!data.getString("birthdate").equals("") &&
                                    !data.getString("birthdate").equals("0000-00-00")) {
                                AppDebugLog.print("birthDate : " + data.getString("birthdate"));
                                String[] arr = data.getString("birthdate").split("-");
                                myCalendar.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));
                                //et_dob.setText(data.getString("birthdate"));
                                updateLabel();
                            }

                            common.setSelection(spin_mari, mari_list_id, mari_id);
                            common.setSelection(spin_t_child, tot_list_id, total_child_id);
                            common.setSelection(spin_child_status, status_list_id, status_child_id);
                            common.setSelection(spin_tongue, tong_list_id, tongue_id);
                            common.setSelection(spin_height, hit_list_id, hite_id);
                            common.setSelection(spin_weight, wite_list_id, weight_id);

                            spin_lang.setSelection(lang_id.split(","));

                            break;
                        case KEY_RELIGION:
                            et_sub_caste.setText(data.getString("subcaste"));
                            et_gothra.setText(data.getString("gothra"));

                            religion_id = data.getString("religion");
                            if (!religion_id.equals(""))
                                pd.dismiss();
                            caste_id = data.getString("caste");
                            manglik_id = data.getString("manglik");
                            star_id = data.getString("star");
                            horo_id = data.getString("horoscope");
                            moon_id = data.getString("moonsign");
                            Log.d("resp", caste_id + "  profile");
                            common.setSelection(spin_religion, reli_list_id, religion_id);

                            common.setSelection(spin_manglik, man_list_id, manglik_id);
                            common.setSelection(spin_caste, caste_list_id, caste_id);
                            common.setSelection(spin_star, star_list_id, star_id);
                            common.setSelection(spin_horo, horo_list_id, horo_id);
                            common.setSelection(spin_moon, moon_list_id, moon_id);

                            break;
                        case KEY_PROFILE:
                            et_about.setText(data.getString("profile_text"));
                            et_hoby.setText(data.getString("hobby"));
                            if (!data.getString("birthplace").equals("null"))
                                et_birth_place.setText(data.getString("birthplace"));
                            else
                                et_birth_place.setText("");
                            et_birth_time.setText(data.getString("birthtime"));

                            if (!data.getString("birthtime").equals("") &&
                                    !data.getString("birthtime").equals("00:00") &&
                                    !data.getString("birthtime").equals("Not Available")) {
                                try {
                                    String[] arr = data.getString("birthtime").split(" ");
                                    String[] arr1 = arr[0].split(":");
                                    mcurrentTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr1[0]));
                                    mcurrentTime.set(Calendar.MINUTE, Integer.parseInt(arr1[1]));
                                } catch (Exception e) {
                                    AppDebugLog.print("Exception in getMyProfile :" + e.getMessage());
                                }
                            }

                            created_id = data.getString("profileby");
                            reference_id = data.getString("reference");
                            common.setSelection(spin_created, created_list_id, created_id);
                            common.setSelection(spin_reference, reference_list_id, reference_id);
                            break;
                        case KEY_EDUCATION:
                            edu_id = data.getString("education_detail");
                            emp_id = data.getString("employee_in");
                            income_id = data.getString("income");
                            occu_id = data.getString("occupation");
                            desig_id = data.getString("designation");
                            spin_edu.setSelection(edu_id.split(","));

                            common.setSelection(spin_emp_in, emp_list_id, emp_id);
                            common.setSelection(spin_income, incm_list_id, income_id);
                            common.setSelection(spin_occupation, ocu_list_id, occu_id);
                            common.setSelection(spin_designation, desi_list_id, desig_id);

                            break;
                        case KEY_LIFE:
                            body_id = data.getString("bodytype");
                            eat_id = data.getString("diet");
                            smok_id = data.getString("smoke");
                            drink_id = data.getString("drink");
                            skin_id = data.getString("complexion");
                            blood_id = data.getString("blood_group");

                            common.setSelection(spin_body, body_list_id, body_id);
                            common.setSelection(spin_eat, eat_list_id, eat_id);
                            common.setSelection(spin_smok, smok_list_id, smok_id);
                            common.setSelection(spin_drink, drink_list_id, drink_id);
                            common.setSelection(spin_skin, skin_list_id, skin_id);
                            common.setSelection(spin_blood, blood_list_id, blood_id);
                            break;
                        case KEY_LOCATION:
                            country_id = data.getString("country_id");
                            if (country_id != null && !country_id.equals("0") && !country_id.equals("Select Country")) {
                                getDepedentList("state_list", country_id);
                            }
                            state_id = data.getString("state_id");

                            if (state_id != null && !state_id.equals("0") && !state_id.equals("Select State")) {
                                getDepedentList("city_list", state_id);
                            }
                            city_id = data.getString("city");
                            resi_id = data.getString("residence");
                            common.setSelection(spin_residence, residence_list_id, resi_id);
                            common.setSelection(spin_country, contry_list_id, country_id);

                            String[] arr_mob = data.getString("mobile").split("-");

                            if (!data.getString("address").equals("null"))
                                et_address.setText(data.getString("address"));
                            else
                                et_address.setText("");

                            if (arr_mob.length == 2) {
                                et_mobile.setText(arr_mob[1]);
                                spin_code.setCountryForPhoneCode(Integer.parseInt(arr_mob[0]));
                            }

                            et_phone.setText(data.getString("phone"));
                            et_time_call.setText(data.getString("time_to_call"));

                            break;
                        case KEY_FAMILY:
                            family_type_id = data.getString("family_type");
                            family_status_id = data.getString("family_status");
                            no_bro_id = data.getString("no_of_brothers");
                            no_mari_bro_id = data.getString("no_of_married_brother");
                            no_sis_id = data.getString("no_of_sisters");
                            no_mari_sis_id = data.getString("no_of_married_sister");

                            common.setSelection(spin_family_type, family_type_list_id, family_type_id);
                            common.setSelection(spin_family_status, family_status_list_id, family_status_id);
                            common.setSelection(spin_no_bro, no_bro_list_id, no_bro_id);

                            common.setSelection(spin_no_sis, no_sis_list_id, no_sis_id);

                            et_father_name.setText(data.getString("father_name"));
                            et_father_ocu.setText(data.getString("father_occupation"));
                            et_mother_name.setText(data.getString("mother_name"));
                            et_mother_ocu.setText(data.getString("mother_occupation"));
                            et_about_family.setText(data.getString("family_details"));

                            break;
                    }
                }
                Log.d("matre", "isLoaded   " + isLoaded);

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

    private void initData() throws JSONException {
        if (MyApplication.getSpinData() != null) {
            switch (pageTag) {
                case KEY_BASIC:
                    et_f_name = findViewById(R.id.et_f_name);
                    et_l_name = findViewById(R.id.et_l_name);
                    et_dob = findViewById(R.id.et_dob);
                    common.setDrawableLeftEditText(R.drawable.user_pink, et_f_name);
                    common.setDrawableLeftEditText(R.drawable.user_pink, et_l_name);
                    common.setDrawableLeftEditText(R.drawable.dob_pink, et_dob);

                    spin_mari = (Spinner) findViewById(R.id.spin_mari);
                    mari_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("marital_status"), "Marital Status");
                    List<String> mari = common.getListFromArray(MyApplication.getSpinData().getJSONArray("marital_status"), "Marital Status");
                    mari_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("marital_status"), "Marital Status");
                    ArrayAdapter adp_mari = new ArrayAdapter<String>(this, R.layout.spinner_item, mari);
                    spin_mari.setAdapter(adp_mari);
                    spin_mari.setOnItemSelectedListener(this);

                    spin_t_child = (Spinner) findViewById(R.id.spin_t_child);
                    tot_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("total_children"), "Total Children");
                    List<String> tot = common.getListFromArray(MyApplication.getSpinData().getJSONArray("total_children"), "Total Children");
                    total_child_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("total_children"), "Total Children");
                    ArrayAdapter adp_tot = new ArrayAdapter<String>(this, R.layout.spinner_item, tot);
                    spin_t_child.setAdapter(adp_tot);
                    spin_t_child.setOnItemSelectedListener(this);

                    spin_child_status = (Spinner) findViewById(R.id.spin_child_status);

                    status_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("status_children"), "Status Children");
                    List<String> status = common.getListFromArray(MyApplication.getSpinData().getJSONArray("status_children"), "Status Children");
                    status_child_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("status_children"), "Status Children");
                    ArrayAdapter adp_status = new ArrayAdapter<String>(this, R.layout.spinner_item, status);
                    spin_child_status.setAdapter(adp_status);
                    spin_child_status.setOnItemSelectedListener(this);

                    spin_tongue = (Spinner) findViewById(R.id.spin_tongue);
                    tong_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue");
                    List<String> tong = common.getListFromArray(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue");
                    tongue_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue");
                    ArrayAdapter adp_tong = new ArrayAdapter<String>(this, R.layout.spinner_item, tong);
                    spin_tongue.setAdapter(adp_tong);
                    spin_tongue.setOnItemSelectedListener(this);

                    spin_lang = (MultiSelectionSpinner) findViewById(R.id.spin_lang);
                    List<String> tong_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue");
                    List<String> tong_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("mothertongue_list"));
                    spin_lang.setItems_string_id(tong_val, tong_ids, "Select Language Known");
                    spin_lang.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
                        @Override
                        public void selectedIndices(List<Integer> indices) {
                            //  Log.d("ressel",indices.toString());
                        }

                        @Override
                        public void selectedStrings(List<String> strings) {
                            lang_id = listToString(strings);
                            Log.d("ressel", lang_id);
                        }
                    });

                    spin_height = (Spinner) findViewById(R.id.spin_height);
                    hit_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("height_list"), "Height");
                    List<String> hit = common.getListFromArray(MyApplication.getSpinData().getJSONArray("height_list"), "Height");
                    hite_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("height_list"), "Height");
                    ArrayAdapter adp_hit = new ArrayAdapter<String>(this, R.layout.spinner_item, hit);
                    spin_height.setAdapter(adp_hit);
                    spin_height.setOnItemSelectedListener(this);

                    spin_weight = (Spinner) findViewById(R.id.spin_weight);
                    wite_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("weight_list"), "Weight");
                    List<String> wite = common.getListFromArray(MyApplication.getSpinData().getJSONArray("weight_list"), "Weight");
                    weight_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("weight_list"), "Weight");
                    ArrayAdapter adp_wite = new ArrayAdapter<String>(this, R.layout.spinner_item, wite);
                    spin_weight.setAdapter(adp_wite);
                    spin_weight.setOnItemSelectedListener(this);

                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateLabel();
                        }

                    };

                    et_dob.setOnClickListener(v -> {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(EditProfileActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    });

                    break;
                case KEY_RELIGION://,
                    et_sub_caste = (EditText) findViewById(R.id.et_sub_caste);
                    common.setDrawableLeftEditText(R.drawable.state_pink, et_sub_caste);
                    et_gothra = (EditText) findViewById(R.id.et_gothra);
                    common.setDrawableLeftEditText(R.drawable.gotra_pink, et_gothra);

                    spin_religion = (Spinner) findViewById(R.id.spin_religion);
                    reli_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("religion_list"), "Religion");
                    List<String> reli = common.getListFromArray(MyApplication.getSpinData().getJSONArray("religion_list"), "Religion");
                    reli_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("religion_list"),
                            "Religion");
                    ArrayAdapter adp_reli = new ArrayAdapter<String>(this, R.layout.spinner_item, reli);
                    spin_religion.setAdapter(adp_reli);
                    spin_religion.setOnItemSelectedListener(this);

                    spin_caste = (Spinner) findViewById(R.id.spin_caste);
                    caste_list_id = new ArrayList<>();
                    caste_list_id.add("0");
                    List<String> caste = new ArrayList<>();
                    caste.add("Select Caste");
                    caste_map = new HashMap<>();
                    caste_map.put("Select Caste", "0");
                    ArrayAdapter adp_caste = new ArrayAdapter<String>(this, R.layout.spinner_item, caste);
                    spin_caste.setAdapter(adp_caste);
                    spin_caste.setOnItemSelectedListener(this);

                    spin_manglik = (Spinner) findViewById(R.id.spin_manglik);
                    man_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("manglik"), "Manglik");
                    List<String> man = common.getListFromArray(MyApplication.getSpinData().getJSONArray("manglik"), "Manglik");
                    manglik_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("manglik"), "Manglik");
                    ArrayAdapter adp_man = new ArrayAdapter<String>(this, R.layout.spinner_item, man);
                    spin_manglik.setAdapter(adp_man);
                    spin_manglik.setOnItemSelectedListener(this);

                    spin_star = (Spinner) findViewById(R.id.spin_star);
                    star_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("star_list"), "Star");
                    List<String> star = common.getListFromArray(MyApplication.getSpinData().getJSONArray("star_list"), "Star");
                    star_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("star_list"), "Star");
                    ArrayAdapter adp_star = new ArrayAdapter<String>(this, R.layout.spinner_item, star);
                    spin_star.setAdapter(adp_star);
                    spin_star.setOnItemSelectedListener(this);

                    spin_horo = (Spinner) findViewById(R.id.spin_horo);
                    horo_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("horoscope"), "Horoscope");
                    List<String> horo = common.getListFromArray(MyApplication.getSpinData().getJSONArray("horoscope"), "Horoscope");
                    horo_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("horoscope"), "Horoscope");
                    ArrayAdapter adp_horo = new ArrayAdapter<String>(this, R.layout.spinner_item, horo);
                    spin_horo.setAdapter(adp_horo);
                    spin_horo.setOnItemSelectedListener(this);

                    spin_moon = (Spinner) findViewById(R.id.spin_moon);
                    moon_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("moonsign_list"), "Moonsign");
                    List<String> moon = common.getListFromArray(MyApplication.getSpinData().getJSONArray("moonsign_list"), "Moonsign");
                    moon_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("moonsign_list"), "Moonsign");
                    ArrayAdapter adp_moon = new ArrayAdapter<String>(this, R.layout.spinner_item, moon);
                    spin_moon.setAdapter(adp_moon);
                    spin_moon.setOnItemSelectedListener(this);

                    break;
                case KEY_PROFILE:
                    et_about = (EditText) findViewById(R.id.et_about);
                    et_hoby = (EditText) findViewById(R.id.et_hoby);
                    common.setDrawableLeftEditText(R.drawable.hoby_pink, et_hoby);
                    et_birth_place = (EditText) findViewById(R.id.et_birth_place);
                    common.setDrawableLeftEditText(R.drawable.pin_location, et_birth_place);
                    et_birth_time = (EditText) findViewById(R.id.et_birth_time);
                    common.setDrawableLeftEditText(R.drawable.dob_pink, et_birth_time);

                    et_birth_time.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(EditProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    mcurrentTime.set(Calendar.HOUR, selectedHour);
                                    mcurrentTime.set(Calendar.MINUTE, selectedMinute);

                                    if (mFormat == null)
                                        mFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                                    et_birth_time.setText(Common.get12HrTime(selectedHour, selectedMinute));
                                }
                            }, hour, minute, false);
                            mTimePicker.setTitle("Select Time");
                            mTimePicker.show();

                        }
                    });

                    spin_created = (Spinner) findViewById(R.id.spin_created);
                    created_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("profileby"),
                            "Created By");
                    List<String> created = common.getListFromArray(MyApplication.getSpinData().getJSONArray("profileby"),
                            "Created By");
                    created_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("profileby"),
                            "Created By");
                    ArrayAdapter adp_created = new ArrayAdapter<String>(this, R.layout.spinner_item, created);
                    spin_created.setAdapter(adp_created);
                    spin_created.setOnItemSelectedListener(this);

                    spin_reference = (Spinner) findViewById(R.id.spin_reference);//
                    reference_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("reference"),
                            "Reference");
                    List<String> reference = common.getListFromArray(MyApplication.getSpinData().getJSONArray("reference"),
                            "Reference");
                    reference_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("reference"),
                            "Reference");
                    ArrayAdapter adp_reference = new ArrayAdapter<String>(this, R.layout.spinner_item, reference);
                    spin_reference.setAdapter(adp_reference);
                    spin_reference.setOnItemSelectedListener(this);

                    break;
                case KEY_EDUCATION:
                    spin_edu = (MultiSelectionSpinner) findViewById(R.id.spin_edu);
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
                        }
                    });
                    spin_emp_in = (Spinner) findViewById(R.id.spin_emp_in);
                    emp_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("employee_in"), "Employee In");
                    List<String> emp = common.getListFromArray(MyApplication.getSpinData().getJSONArray("employee_in"), "Employee In");
                    emp_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("employee_in"), "Employee In");
                    ArrayAdapter adp_emp = new ArrayAdapter<String>(this, R.layout.spinner_item, emp);
                    spin_emp_in.setAdapter(adp_emp);
                    spin_emp_in.setOnItemSelectedListener(this);

                    spin_income = (Spinner) findViewById(R.id.spin_income);
                    incm_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
                    List<String> incm = common.getListFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
                    income_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income");
                    ArrayAdapter adp_incm = new ArrayAdapter<String>(this, R.layout.spinner_item, incm);
                    spin_income.setAdapter(adp_incm);
                    spin_income.setOnItemSelectedListener(this);

                    spin_occupation = (Spinner) findViewById(R.id.spin_occupation);
                    ocu_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("occupation_list"), "Occupation");
                    List<String> ocu = common.getListFromArray(MyApplication.getSpinData().getJSONArray("occupation_list"), "Occupation");
                    occu_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("occupation_list"), "Occupation");
                    ArrayAdapter adp_ocu = new ArrayAdapter<String>(this, R.layout.spinner_item, ocu);
                    spin_occupation.setAdapter(adp_ocu);
                    spin_occupation.setOnItemSelectedListener(this);

                    spin_designation = (Spinner) findViewById(R.id.spin_designation);
                    desi_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("designation_list"), "Designation");
                    List<String> desi = common.getListFromArray(MyApplication.getSpinData().getJSONArray("designation_list"), "Designation");
                    desig_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("designation_list"), "Designation");
                    ArrayAdapter adp_desi = new ArrayAdapter<String>(this, R.layout.spinner_item, desi);
                    spin_designation.setAdapter(adp_desi);
                    spin_designation.setOnItemSelectedListener(this);

                    break;
                case KEY_LIFE:
                    spin_body = (Spinner) findViewById(R.id.spin_body);
                    body_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("bodytype"),
                            "Body Type");
                    List<String> body = common.getListFromArray(MyApplication.getSpinData().getJSONArray("bodytype"),
                            "Body Type");
                    body_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("bodytype"),
                            "Body Type");
                    ArrayAdapter adp_body = new ArrayAdapter<String>(this, R.layout.spinner_item, body);
                    spin_body.setAdapter(adp_body);
                    spin_body.setOnItemSelectedListener(this);

                    spin_eat = (Spinner) findViewById(R.id.spin_eat);
                    eat_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("diet"),
                            "Eating Habit");
                    List<String> eat = common.getListFromArray(MyApplication.getSpinData().getJSONArray("diet"),
                            "Eating Habit");
                    eat_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("diet"),
                            "Eating Habit");
                    ArrayAdapter adp_eat = new ArrayAdapter<String>(this, R.layout.spinner_item, eat);
                    spin_eat.setAdapter(adp_eat);
                    spin_eat.setOnItemSelectedListener(this);

                    spin_smok = (Spinner) findViewById(R.id.spin_smok);
                    smok_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("smoke"),
                            "Smoke Habit");
                    List<String> smok = common.getListFromArray(MyApplication.getSpinData().getJSONArray("smoke"),
                            "Smoke Habit");
                    smok_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("smoke"),
                            "Smoke Habit");
                    ArrayAdapter adp_smok = new ArrayAdapter<String>(this, R.layout.spinner_item, smok);
                    spin_smok.setAdapter(adp_smok);
                    spin_smok.setOnItemSelectedListener(this);

                    spin_drink = (Spinner) findViewById(R.id.spin_drink);
                    drink_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("drink"),
                            "Drink Habit");
                    List<String> drink = common.getListFromArray(MyApplication.getSpinData().getJSONArray("drink"),
                            "Drink Habit");
                    drink_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("drink"),
                            "Drink Habit");
                    ArrayAdapter adp_drink = new ArrayAdapter<String>(this, R.layout.spinner_item, drink);
                    spin_drink.setAdapter(adp_drink);
                    spin_drink.setOnItemSelectedListener(this);

                    spin_skin = (Spinner) findViewById(R.id.spin_skin);
                    skin_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("complexion"),
                            "Skin Tone");
                    List<String> skin = common.getListFromArray(MyApplication.getSpinData().getJSONArray("complexion"),
                            "Skin Tone");
                    skin_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("complexion"),
                            "Skin Tone");
                    ArrayAdapter adp_skin = new ArrayAdapter<String>(this, R.layout.spinner_item, skin);
                    spin_skin.setAdapter(adp_skin);
                    spin_skin.setOnItemSelectedListener(this);

                    spin_blood = (Spinner) findViewById(R.id.spin_blood);
                    blood_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("blood_group"),
                            "Blood Group");
                    List<String> blood = common.getListFromArray(MyApplication.getSpinData().getJSONArray("blood_group"),
                            "Blood Group");
                    blood_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("blood_group"),
                            "Blood Group");
                    ArrayAdapter adp_blood = new ArrayAdapter<String>(this, R.layout.spinner_item, blood);
                    spin_blood.setAdapter(adp_blood);
                    spin_blood.setOnItemSelectedListener(this);

                    break;
                case KEY_LOCATION:
                    spin_country = (Spinner) findViewById(R.id.spin_country);
                    contry_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("country_list"), "Country");
                    List<String> contry = common.getListFromArray(MyApplication.getSpinData().getJSONArray("country_list"), "Country");
                    country_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("country_list"), "Country");
                    ArrayAdapter adp_con = new ArrayAdapter<String>(this, R.layout.spinner_item, contry);
                    spin_country.setAdapter(adp_con);
                    spin_country.setOnItemSelectedListener(this);

                    spin_state = (Spinner) findViewById(R.id.spin_state);
                    state_list_id = new ArrayList<>();
                    state_list_id.add("0");
                    List<String> stat = new ArrayList<>();
                    stat.add("Select State");
                    state_map = new HashMap<>();
                    state_map.put("Select State", "0");
                    ArrayAdapter adp_stat = new ArrayAdapter<String>(this, R.layout.spinner_item, stat);
                    spin_state.setAdapter(adp_stat);
                    spin_state.setOnItemSelectedListener(this);

                    spin_city = (Spinner) findViewById(R.id.spin_city);
                    city_list_id = new ArrayList<>();
                    city_list_id.add("0");
                    List<String> cty = new ArrayList<>();
                    cty.add("Select City");
                    city_map = new HashMap<>();
                    city_map.put("Select City", "0");
                    ArrayAdapter adp_cty = new ArrayAdapter<String>(this, R.layout.spinner_item, cty);
                    spin_city.setAdapter(adp_cty);
                    spin_city.setOnItemSelectedListener(this);

                    spin_residence = (Spinner) findViewById(R.id.spin_residence);
                    residence_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("residence"),
                            "Residence");
                    List<String> resi = common.getListFromArray(MyApplication.getSpinData().getJSONArray("residence"),
                            "Residence");
                    resi_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("residence"),
                            "Residence");
                    ArrayAdapter adp_resi = new ArrayAdapter<String>(this, R.layout.spinner_item, resi);
                    spin_residence.setAdapter(adp_resi);
                    spin_residence.setOnItemSelectedListener(this);

                    et_address = (EditText) findViewById(R.id.et_address);
                    et_mobile = (EditText) findViewById(R.id.et_mobile);
                    common.setDrawableLeftEditText(R.drawable.mobile_pink, et_mobile);
                    et_phone = (EditText) findViewById(R.id.et_phone);
                    common.setDrawableLeftEditText(R.drawable.mobile_pink, et_phone);
                    et_time_call = (EditText) findViewById(R.id.et_time_call);
                    common.setDrawableLeftEditText(R.drawable.mobile_pink, et_time_call);
                    spin_code = (CountryCodePicker) findViewById(R.id.spin_code);

                    break;
                case KEY_FAMILY:
                    spin_family_type = (Spinner) findViewById(R.id.spin_family_type);//family_type
                    family_type_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("family_type"), "Family Type");
                    List<String> family_type = common.getListFromArray(MyApplication.getSpinData().getJSONArray("family_type"), "Family Type");
                    family_type_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("family_type"), "Family Type");
                    ArrayAdapter adp_family_type = new ArrayAdapter<String>(this, R.layout.spinner_item, family_type);
                    spin_family_type.setAdapter(adp_family_type);
                    spin_family_type.setOnItemSelectedListener(this);

                    spin_family_status = (Spinner) findViewById(R.id.spin_family_status);
                    family_status_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("family_status"), "Family Status");
                    List<String> family_status = common.getListFromArray(MyApplication.getSpinData().getJSONArray("family_status"), "Family Status");
                    family_status_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("family_status"), "Family Status");
                    ArrayAdapter adp_family_status = new ArrayAdapter<String>(this, R.layout.spinner_item, family_status);
                    spin_family_status.setAdapter(adp_family_status);
                    spin_family_status.setOnItemSelectedListener(this);

                    spin_no_bro = (Spinner) findViewById(R.id.spin_no_bro);
                    no_bro_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("no_of_brothers"), "No Of Brothers");
                    List<String> no_bro = common.getListFromArray(MyApplication.getSpinData().getJSONArray("no_of_brothers"), "No Of Brothers");
                    no_bro_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("no_of_brothers"), "No Of Brothers");
                    ArrayAdapter adp_no_bro = new ArrayAdapter<String>(this, R.layout.spinner_item, no_bro);
                    spin_no_bro.setAdapter(adp_no_bro);
                    spin_no_bro.setOnItemSelectedListener(this);

                    spin_no_mari_bro = (Spinner) findViewById(R.id.spin_no_mari_bro);
                    no_mari_bro_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("no_marri_brother"), "No Of Married Brothers");
                    List<String> no_mari_bro = common.getListFromArray(MyApplication.getSpinData().getJSONArray("no_marri_brother"), "No Of Married Brothers");
                    no_mari_bro_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("no_marri_brother"), "No Of Married Brothers");
                    ArrayAdapter adp_no_mari_bro = new ArrayAdapter<String>(this, R.layout.spinner_item, no_mari_bro);
                    spin_no_mari_bro.setAdapter(adp_no_mari_bro);
                    spin_no_mari_bro.setOnItemSelectedListener(this);

                    spin_no_sis = (Spinner) findViewById(R.id.spin_no_sis);
                    no_sis_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("no_of_brothers"), "No Of Sisters");
                    List<String> no_sis = common.getListFromArray(MyApplication.getSpinData().getJSONArray("no_of_brothers"), "No Of Sisters");
                    no_sis_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("no_of_brothers"), "No Of Sisters");
                    ArrayAdapter adp_no_sis = new ArrayAdapter<String>(this, R.layout.spinner_item, no_sis);
                    spin_no_sis.setAdapter(adp_no_sis);
                    spin_no_sis.setOnItemSelectedListener(this);

                    spin_no_mari_sis = (Spinner) findViewById(R.id.spin_no_mari_sis);
                    no_mari_sis_list_id = common.getListFromArray_id(MyApplication.getSpinData().getJSONArray("no_marri_sister"), "No Of Married Sisters");
                    List<String> no_mari_sis = common.getListFromArray(MyApplication.getSpinData().getJSONArray("no_marri_sister"), "No Of Married Sisters");
                    no_mari_sis_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("no_marri_sister"), "No Of Married Sisters");
                    ArrayAdapter adp_no_mari_sis = new ArrayAdapter<String>(this, R.layout.spinner_item, no_mari_sis);
                    spin_no_mari_sis.setAdapter(adp_no_mari_sis);
                    spin_no_mari_sis.setOnItemSelectedListener(this);

                    et_father_name = (EditText) findViewById(R.id.et_father_name);
                    common.setDrawableLeftEditText(R.drawable.user_pink, et_father_name);
                    et_father_ocu = (EditText) findViewById(R.id.et_father_ocu);
                    common.setDrawableLeftEditText(R.drawable.user_pink, et_father_ocu);
                    et_mother_name = (EditText) findViewById(R.id.et_mother_name);
                    common.setDrawableLeftEditText(R.drawable.user_pink, et_mother_name);
                    et_mother_ocu = (EditText) findViewById(R.id.et_mother_ocu);
                    common.setDrawableLeftEditText(R.drawable.user_pink, et_mother_ocu);
                    et_about_family = (EditText) findViewById(R.id.et_about_family);
                    break;
            }

            getMyProfile();
        } else {
            getList();
        }
    }

    private void getList() {
        pd = new ProgressDialog(EditProfileActivity.this);
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

    private String listToString(List<String> list) {
        String listString = "";

        for (String s : list) {
            listString += s + ",";// \t
        }

        listString = listString.replaceAll(",$", "");
        return listString;
    }

    private void validBasicData() {
        String fname = et_f_name.getText().toString().trim();
        String lname = et_l_name.getText().toString().trim();
        String dob = "";
        if (myCalendar != null) {
            String myFormat = Utils.BIRTH_DATE_UPLOAD_FORMAT; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            dob = sdf.format(myCalendar.getTime());
        }

        boolean isValid = true;
        if (TextUtils.isEmpty(fname)) {
            et_f_name.setError("Please enter first name");
            isValid = false;
        }
        if (TextUtils.isEmpty(lname)) {
            et_l_name.setError("Please enter last name");
            isValid = false;
        }
        if (mari_id == null || mari_id.equals("0")) {
            common.spinnerSetError(spin_mari, "Please select marital status");
            isValid = false;
        }
        if (mari_id != null && !mari_id.equals("Unmarried")) {
            if (total_child_id.equals("total")) {
                common.spinnerSetError(spin_t_child, "Please select total children");
                isValid = false;
            } else {
                if (!total_child_id.equals("0")) {
                    if (status_child_id.equals("0")) {
                        common.spinnerSetError(spin_child_status, "Please select children status");
                        isValid = false;
                    }
                }
            }
        }

        if (tongue_id == null || tongue_id.equals("") || tongue_id.equals("0")) {
            common.spinnerSetError(spin_tongue, "Please select mother tongue");
            isValid = false;
        }
        if (hite_id == null || hite_id.equals("") || hite_id.equals("0")) {
            common.spinnerSetError(spin_height, "Please select height");
            isValid = false;
        }
        if (weight_id == null || weight_id.equals("") || weight_id.equals("0")) {
            common.spinnerSetError(spin_weight, "Please select weight");
            isValid = false;
        }
        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("firstname", fname);
            param.put("lastname", lname);
            param.put("username", fname + " " + lname);
            param.put("marital_status", checkData(mari_id));
            param.put("total_children", total_child_id);
            param.put("status_children", checkData(status_child_id));
            param.put("mother_tongue", checkData(tongue_id));
            param.put("height", hite_id);
            param.put("weight", weight_id);
            param.put("languages_known", checkData(lang_id));
            param.put("birthdate", dob);//changeDate(
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            Log.d("resp", changeDate(dob) + "   " + dob);
            submitData(param);
        }
    }

    private void validLifeData() {
        HashMap<String, String> param = new HashMap<>();
        param.put("bodytype", checkData(body_id));
        param.put("diet", checkData(eat_id));
        param.put("smoke", checkData(smok_id));
        param.put("drink", checkData(drink_id));
        param.put("complexion", checkData(skin_id));
        param.put("blood_group", checkData(blood_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        submitData(param);
    }

    private void validProfileData() {
        boolean isValid = true;

        String about = et_about.getText().toString().trim();
        String hoby = et_hoby.getText().toString().trim();
        String location = et_birth_place.getText().toString().trim();
        String time = et_birth_time.getText().toString().trim();

        if (created_id == null || created_id.equals("") || created_id.equals("0")) {
            common.spinnerSetError(spin_created, "Please select created by");
            isValid = false;
        }
        if (reference_id == null || reference_id.equals("") || reference_id.equals("0")) {
            common.spinnerSetError(spin_reference, "Please select reference by");
            isValid = false;
        }

        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("profile_text", about);
            param.put("hobby", hoby);
            param.put("birthplace", location);
            param.put("birthtime", time);
            param.put("profileby", checkData(created_id));
            param.put("reference", checkData(reference_id));
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            submitData(param);
        }
    }

    private void validReliData() {
        String subcst = et_sub_caste.getText().toString().trim();
        String gothra = et_gothra.getText().toString().trim();
        boolean isValid = true;

        if (religion_id == null || religion_id.equals("") || religion_id.equals("0")) {
            common.spinnerSetError(spin_religion, "Please select religion");
            isValid = false;
        }
        if (caste_id == null || caste_id.equals("") || caste_id.equals("0")) {
            common.spinnerSetError(spin_caste, "Please select caste");
            isValid = false;
        }
        Log.d("resp", religion_id + "   " + caste_id);
        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("religion", checkData(religion_id));
            param.put("caste", checkData(caste_id));
            param.put("subcaste", subcst);
            param.put("manglik", checkData(manglik_id));
            param.put("star", checkData(star_id));
            param.put("horoscope", checkData(horo_id));
            param.put("gothra", gothra);
            param.put("moonsign", checkData(moon_id));
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            submitData(param);
        }
    }

    private void validLocaData() {
        String add = et_address.getText().toString().trim();
        String mobile = et_mobile.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String time_call = et_time_call.getText().toString().trim();
        code_id = spin_code.getSelectedCountryCodeWithPlus();

        boolean isValid = true;

        if (country_id == null || country_id.equals("") || country_id.equals("0")) {
            common.spinnerSetError(spin_country, "Please select country");
            isValid = false;
        }
        if (state_id == null ||  state_id.equals("") || state_id.equals("0")) {
            common.spinnerSetError(spin_state, "Please select state");
            isValid = false;
        }
        if (city_id == null ||  city_id.equals("") || city_id.equals("0")) {
            common.spinnerSetError(spin_city, "Please select city");
            isValid = false;
        }
        if (TextUtils.isEmpty(mobile) || mobile.length() < 8) {
            et_mobile.setError("Please enter valid mobile number");
            isValid = false;
        }

        if (phone.length() > 0 && phone.length() < 8) {
            et_phone.setError("Please enter valid phone number");
            isValid = false;
        }
        if (code_id == null ||  code_id.equals("")) {
            common.spinnerSetError(spin_city, "Please select country code");
            isValid = false;
        }
        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("country_id", checkData(country_id));
            param.put("state_id", checkData(state_id));
            param.put("city", checkData(city_id));
            param.put("address", add);
            param.put("country_code", code_id);
            param.put("mobile_num", mobile);
            param.put("phone", phone);
            param.put("time_to_call", time_call);
            param.put("residence", checkData(resi_id));
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            submitData(param);
        }
    }

    private void validEduData() {
        boolean isValid = true;
        if (edu_id == null ||  edu_id.equals("") || edu_id.equals("0")) {
            common.spinnerSetError(spin_edu, "Please select education");
            isValid = false;
        }
        if (occu_id == null || occu_id.equals("") || occu_id.equals("0")) {
            common.spinnerSetError(spin_occupation, "Please select occupation");
            isValid = false;
        }

        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("education_detail", checkData(edu_id));
            param.put("employee_in", checkData(emp_id));
            param.put("income", checkData(income_id));
            param.put("occupation", checkData(occu_id));
            param.put("designation", checkData(desig_id));
            param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
            submitData(param);
        }
    }

    private void validFamilyData() {

        String father_name = et_father_name.getText().toString().trim();
        String father_occupation = et_father_ocu.getText().toString().trim();
        String mother_name = et_mother_name.getText().toString().trim();
        String mother_occupation = et_mother_ocu.getText().toString().trim();
        String family_details = et_about_family.getText().toString().trim();

        HashMap<String, String> param = new HashMap<>();
        param.put("family_type", checkData(family_type_id));
        param.put("family_status", checkData(family_status_id));
        param.put("no_of_brothers", no_bro_id);
        param.put("no_of_married_brother", no_mari_bro_id);
        param.put("no_of_sisters", no_sis_id);
        param.put("no_of_married_sister", no_mari_sis_id);

        param.put("father_name", father_name);
        param.put("father_occupation", father_occupation);
        param.put("mother_name", mother_name);
        param.put("mother_occupation", mother_occupation);
        param.put("family_details", family_details);
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        submitData(param);

    }

    private String checkData(String val) {
        if (val.equals("0")) {
            return "";
        }
        return val;
    }

    public String changeDate(String time) {
        String inputPattern = Utils.BIRTH_DATE_FORMAT;
        String outputPattern = Utils.BIRTH_DATE_FORMAT;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void submitData(HashMap<String, String> param) {
        pd = new ProgressDialog(EditProfileActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();

        common.makePostRequest(Utils.edit_profile, param, response -> {
            pd.dismiss();
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"));
                if (object.getString("status").equals("success")) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "reload");
                    returnIntent.putExtra("tabid", "my");
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
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

    private void updateLabel() {
        String myFormat = Utils.BIRTH_DATE_FORMAT; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_dob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void disBro(String id) {
        List<String> val = new ArrayList<>();
        no_mari_bro_list_id.clear();
        switch (id) {
            case "0":
                val.add("No married brother");
                no_mari_bro_list_id.add("No married brother");
                break;
            case "1":
                val.add("No married brother");
                no_mari_bro_list_id.add("No married brother");
                val.add("One married brother");
                no_mari_bro_list_id.add("One married brother");
                break;
            case "2":
                val.add("No married brother");
                no_mari_bro_list_id.add("No married brother");
                val.add("One married brother");
                no_mari_bro_list_id.add("One married brother");
                val.add("Two married brother");
                no_mari_bro_list_id.add("Two married brother");
                break;
            case "3":
                val.add("No married brother");
                no_mari_bro_list_id.add("No married brother");
                val.add("One married brother");
                no_mari_bro_list_id.add("One married brother");
                val.add("Two married brother");
                no_mari_bro_list_id.add("Two married brother");
                val.add("Three married brother");
                no_mari_bro_list_id.add("Three married brother");
                break;
            case "4":
                val.add("No married brother");
                no_mari_bro_list_id.add("No married brother");
                val.add("One married brother");
                no_mari_bro_list_id.add("One married brother");
                val.add("Two married brother");
                no_mari_bro_list_id.add("Two married brother");
                val.add("Three married brother");
                no_mari_bro_list_id.add("Three married brother");
                val.add("Four married brother");
                no_mari_bro_list_id.add("Four married brother");
                break;
            case "4 +":
                val.add("No married brother");
                no_mari_bro_list_id.add("No married brother");
                val.add("One married brother");
                no_mari_bro_list_id.add("One married brother");
                val.add("Two married brother");
                no_mari_bro_list_id.add("Two married brother");
                val.add("Three married brother");
                no_mari_bro_list_id.add("Three married brother");
                val.add("Four married brother");
                no_mari_bro_list_id.add("Four married brother");
                val.add("Above four married brother");
                no_mari_bro_list_id.add("Above four married brother");
                break;
        }
        for (int k = 0; k < val.size(); k++) {
            no_mari_bro_map.put(val.get(k), no_mari_bro_list_id.get(k));
        }

        ArrayAdapter adp_no_mari_bro = new ArrayAdapter<String>(this, R.layout.spinner_item, val);
        spin_no_mari_bro.setAdapter(adp_no_mari_bro);
        common.setSelection(spin_no_mari_bro, no_mari_bro_list_id, no_mari_bro_id);
    }

    private void disSis(String id) {
        List<String> val = new ArrayList<>();
        no_mari_sis_list_id.clear();
        switch (id) {
            case "0":
                val.add("No married sister");
                no_mari_sis_list_id.add("No married sister");
                break;
            case "1":
                val.add("No married sister");
                no_mari_sis_list_id.add("No married sister");
                val.add("One married sister");
                no_mari_sis_list_id.add("One married sister");
                break;
            case "2":
                val.add("No married sister");
                no_mari_sis_list_id.add("No married sister");
                val.add("One married sister");
                no_mari_sis_list_id.add("One married sister");
                val.add("Two married sister");
                no_mari_sis_list_id.add("Two married sister");
                break;
            case "3":
                val.add("No married sister");
                no_mari_sis_list_id.add("No married sister");
                val.add("One married sister");
                no_mari_sis_list_id.add("One married sister");
                val.add("Two married sister");
                no_mari_sis_list_id.add("Two married sister");
                val.add("Three married sister");
                no_mari_sis_list_id.add("Three married sister");
                break;
            case "4":
                val.add("No married sister");
                no_mari_sis_list_id.add("No married sister");
                val.add("One married sister");
                no_mari_sis_list_id.add("One married sister");
                val.add("Two married sister");
                no_mari_sis_list_id.add("Two married sister");
                val.add("Three married sister");
                no_mari_sis_list_id.add("Three married sister");
                val.add("Four married sister");
                no_mari_sis_list_id.add("Four married sister");
                break;
            case "4 +":
                val.add("No married sister");
                no_mari_sis_list_id.add("No married sister");
                val.add("One married sister");
                no_mari_sis_list_id.add("One married sister");
                val.add("Two married sister");
                no_mari_sis_list_id.add("Two married sister");
                val.add("Three married sister");
                no_mari_sis_list_id.add("Three married sister");
                val.add("Four married sister");
                no_mari_sis_list_id.add("Four married sister");
                val.add("Above four married sister");
                no_mari_sis_list_id.add("Above four married sister");
                break;
        }
        for (int k = 0; k < val.size(); k++) {
            no_mari_sis_map.put(val.get(k), no_mari_sis_list_id.get(k));
        }

        ArrayAdapter adp_no_mari_bro = new ArrayAdapter<String>(this, R.layout.spinner_item, val);
        spin_no_mari_sis.setAdapter(adp_no_mari_bro);
        common.setSelection(spin_no_mari_sis, no_mari_sis_list_id, no_mari_sis_id);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        switch (spinner.getId()) {
            case R.id.spin_family_type:
                family_type_id = family_type_map.get(spin_family_type.getSelectedItem().toString());
                break;
            case R.id.spin_family_status:
                family_status_id = family_status_map.get(spin_family_status.getSelectedItem().toString());
                break;
            case R.id.spin_no_bro:
                no_bro_id = no_bro_map.get(spin_no_bro.getSelectedItem().toString());
                if (no_bro_id != null && !no_bro_id.equals("")) {
                    disBro(no_bro_id);
                }

                break;
            case R.id.spin_no_mari_bro:
                no_mari_bro_id = no_mari_bro_map.get(spin_no_mari_bro.getSelectedItem().toString());
                break;
            case R.id.spin_no_sis:
                no_sis_id = no_sis_map.get(spin_no_sis.getSelectedItem().toString());
                if (no_sis_id != null && !no_sis_id.equals("")) {
                    disSis(no_sis_id);
                }
                break;
            case R.id.spin_no_mari_sis:
                no_mari_sis_id = no_mari_sis_map.get(spin_no_mari_sis.getSelectedItem().toString());
                break;

            case R.id.spin_religion:
                religion_id = reli_map.get(spin_religion.getSelectedItem().toString());
                if (religion_id != null && !religion_id.equals("0") && !religion_id.equals("")) {
                    //caste_id="0";
                    spin_caste.setSelection(0);
                    getDepedentList("caste_list", religion_id);
                } else {
                    //13/05/2019
                    et_sub_caste.setText("");
                }
                break;
            case R.id.spin_caste:
                //if (!spin_caste.getSelectedItem().toString().equals("Select Caste"))
                caste_id = caste_map.get(spin_caste.getSelectedItem().toString());

                //13/05/2019
                if (caste_id == null || caste_id.equals("0") || caste_id.equals("")) {
                    et_sub_caste.setText("");
                }
                break;
            case R.id.spin_manglik:
                manglik_id = manglik_map.get(spin_manglik.getSelectedItem().toString());
                break;
            case R.id.spin_star:
                star_id = star_map.get(spin_star.getSelectedItem().toString());
                break;
            case R.id.spin_horo:
                horo_id = horo_map.get(spin_horo.getSelectedItem().toString());
                break;
            case R.id.spin_moon:
                moon_id = moon_map.get(spin_moon.getSelectedItem().toString());
                break;
            case R.id.spin_created:
                created_id = created_map.get(spin_created.getSelectedItem().toString());
                break;
            case R.id.spin_reference:
                reference_id = reference_map.get(spin_reference.getSelectedItem().toString());
                break;
            case R.id.spin_tongue:
                tongue_id = tongue_map.get(spin_tongue.getSelectedItem().toString());
                break;
            case R.id.spin_body:
                body_id = body_map.get(spin_body.getSelectedItem().toString());
                break;
            case R.id.spin_eat:
                eat_id = eat_map.get(spin_eat.getSelectedItem().toString());
                break;
            case R.id.spin_smok:
                smok_id = smok_map.get(spin_smok.getSelectedItem().toString());
                break;
            case R.id.spin_drink:
                drink_id = drink_map.get(spin_drink.getSelectedItem().toString());
                break;
            case R.id.spin_skin:
                skin_id = skin_map.get(spin_skin.getSelectedItem().toString());
                break;
            case R.id.spin_blood:
                blood_id = blood_map.get(spin_blood.getSelectedItem().toString());
                break;
            case R.id.spin_country:
                if (isLoaded) {
                    country_id = country_map.get(spin_country.getSelectedItem().toString());
                    if (country_id != null && (country_id.equals("") || !country_id.equals("0"))) {//&& !country_id.equals("Select Country")
                        Log.d("matre", "country_id   " + country_id);
                        // spin_state.setSelection(0);
                        //spin_city.setSelection(0);
                        getDepedentList("state_list", country_id);
                        //resetState();
                    } else {
                        //13/05/2019
                        //added for fixing not clear when country selected select option
                        state_id = "";
                        city_id = "";
                        spin_state.setSelection(0);
                        spin_city.setSelection(0);
                        //end
                    }
                }
                break;
            case R.id.spin_state:
                if (isLoaded) {
                    state_id = state_map.get(spin_state.getSelectedItem().toString());
                    if (state_id != null && (state_id.equals("") || !state_id.equals("0"))) {
                        // spin_city.setSelection(0);
                        Log.d("matre", "state_id   " + state_id);
                        getDepedentList("city_list", state_id);
                    } else {
                        //13/05/2019
                        //added for fixing not clear when state selected select option
                        city_id = "";
                        spin_city.setSelection(0);
                        //end
                    }
                }
                break;
            case R.id.spin_city:
                if (isLoaded) {
                    city_id = city_map.get(spin_city.getSelectedItem().toString());
                    Log.d("matre", "city_id   " + city_id);
                }
                break;
            case R.id.spin_mari:
                mari_id = mari_map.get(spin_mari.getSelectedItem().toString());
                if (mari_id != null && mari_id.equals("Unmarried")) {
                    spin_t_child.setEnabled(false);
                    spin_t_child.setSelection(0);
                    spin_child_status.setEnabled(false);
                    spin_child_status.setSelection(0);
                    lay_t_child.setVisibility(View.GONE);
                    lay_child_status.setVisibility(View.GONE);
                } else {
                    lay_t_child.setVisibility(View.VISIBLE);
                    lay_child_status.setVisibility(View.VISIBLE);
                    spin_t_child.setEnabled(true);
                    spin_child_status.setEnabled(true);
                }
                break;
            case R.id.spin_t_child:
                total_child_id = total_child_map.get(spin_t_child.getSelectedItem().toString());
                if (total_child_id != null && total_child_id.equals("0")) {
                    status_child_id = "0";
                    spin_child_status.setEnabled(false);
                    spin_child_status.setSelection(0);
                    lay_child_status.setVisibility(View.GONE);
                } else {
                    lay_child_status.setVisibility(View.VISIBLE);
                    spin_child_status.setEnabled(true);
                }

                break;
            case R.id.spin_child_status:
                status_child_id = status_child_map.get(spin_child_status.getSelectedItem().toString());
                break;
            case R.id.spin_residence:
                resi_id = resi_map.get(spin_residence.getSelectedItem().toString());
                break;
            case R.id.spin_emp_in:
                emp_id = emp_map.get(spin_emp_in.getSelectedItem().toString());
                break;
            case R.id.spin_income:
                income_id = income_map.get(spin_income.getSelectedItem().toString());
                break;
            case R.id.spin_occupation:
                occu_id = occu_map.get(spin_occupation.getSelectedItem().toString());
                break;
            case R.id.spin_designation:
                desig_id = desig_map.get(spin_designation.getSelectedItem().toString());
                break;
            case R.id.spin_height:
                hite_id = hite_map.get(spin_height.getSelectedItem().toString());
                break;
            case R.id.spin_weight:
                weight_id = weight_map.get(spin_weight.getSelectedItem().toString());
                break;

        }
    }

    private void getDepedentList(final String tag, final String id) {
        if (pd != null) {
            pd.dismiss();
        }
        pd = new ProgressDialog(EditProfileActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "");
        param.put("retun_for", "");

        common.makePostRequest(Utils.common_depedent_list, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.d("resp",tag+"   ");
                Log.d("matre", "getDepedentList   " + tag + "    " + id);
                isLoaded = true;
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {

                        if ("caste_list".equals(tag)) {
                            caste_list_id = common.getListFromArray_id(object.getJSONArray("data"), "Caste");
                            List<String> caste = common.getListFromArray(object.getJSONArray("data"), "Caste");
                            caste_map = common.getMapFromArray(object.getJSONArray("data"), "Caste");
                            ArrayAdapter adp_caste = new ArrayAdapter<String>(EditProfileActivity.this,
                                    R.layout.spinner_item, caste);
                            spin_caste.setAdapter(adp_caste);
                            // Log.d("resp",caste_id+"   ");
                            if (caste_id != null && !caste_id.equals("")) {
                                for (int i = 0; i < caste_list_id.size(); i++) {
                                    if (caste_list_id.get(i).equals(caste_id))
                                        spin_caste.setSelection(i);
                                }
                            }
                            //common.setSelection(spin_caste, caste_list_id, caste_id);

                        } else if ("state_list".equals(tag)) {
                            state_list_id = common.getListFromArray_id(object.getJSONArray("data"), "State");
                            List<String> state = common.getListFromArray(object.getJSONArray("data"), "State");
                            state_map = common.getMapFromArray(object.getJSONArray("data"), "State");
                            ArrayAdapter adp_state = new ArrayAdapter<String>(EditProfileActivity.this, R.layout.spinner_item, state);
                            spin_state.setAdapter(adp_state);
                            // Log.d("resp",state_id+"   ");
                            if (state_id !=null && !state_id.equals("")) {
                                for (int i = 0; i < state_list_id.size(); i++) {
                                    if (state_list_id.get(i).equals(state_id))
                                        spin_state.setSelection(i);
                                }
                            }
                            //resetCity();
                            //common.setSelection(spin_state,state_list_id,state_id);

                        } else if ("city_list".equals(tag)) {
                            city_list_id = common.getListFromArray_id(object.getJSONArray("data"), "City");

                            List<String> city = common.getListFromArray(object.getJSONArray("data"), "City");
                            city_map = common.getMapFromArray(object.getJSONArray("data"), "City");
                            if (city_map != null && city_map.size() > 0) {
                                ArrayAdapter adp_city = new ArrayAdapter<String>(EditProfileActivity.this, R.layout.spinner_item, city);
                                spin_city.setAdapter(adp_city);
                                // Log.d("resp",city_id+"   ");
                                if (city_id != null && !city_id.equals("")) {
                                    for (int i = 0; i < city_list_id.size(); i++) {
                                        if (city_list_id.get(i).equals(city_id))
                                            spin_city.setSelection(i);
                                    }
                                }
                            }
                            //common.setSelection(spin_city, city_list_id, city_id);

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
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_basic:
                validBasicData();
                break;
            case R.id.btn_life:
                validLifeData();
                break;
            case R.id.btn_about:
                validProfileData();
                break;
            case R.id.btn_reli:
                validReliData();
                break;
            case R.id.btn_loca:
                validLocaData();
                break;
            case R.id.btn_edu:
                validEduData();
                break;
            case R.id.btn_family:
                validFamilyData();
                break;
        }
    }

}
