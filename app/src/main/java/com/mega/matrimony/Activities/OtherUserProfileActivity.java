package com.mega.matrimony.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.OtherUser;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.mega.matrimony.databinding.ActivityOtherUserProfileBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherUserProfileActivity extends AppCompatActivity implements View.OnClickListener, TabHost.OnTabChangeListener {

    TextView tv_basic_label, tv_basic_label_pref, tv_loca_label_pref, tv_edu_label_pref, tv_reli_label_pref, tv_pro_disc_label, tv_edu_label, tv_reli_label, tv_phy_label, tv_family_label,
            tv_locat_label, tv_pro_per, tv_photo_count, tv_cont_label;

    Common common;
    SessionManager session;
    ProgressDialog pd;
    TabHost host;

    private int deviceWidth = 0;

    @BindView(R.id.lay_main)
    LinearLayout lay_main;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.lay_basic)
    LinearLayout lay_basic;
    /*@BindView(R.id.lay_reli_pref)
    LinearLayout lay_reli_pref;
    @BindView(R.id.lay_edu_pref)
    LinearLayout lay_edu_pref;
    @BindView(R.id.lay_loca_pref)
    LinearLayout lay_loca_pref;*/
    @BindView(R.id.lay_basic_pref)
    LinearLayout lay_basic_pref;
    @BindView(R.id.lay_locat)
    LinearLayout lay_locat;
    @BindView(R.id.lay_about)
    LinearLayout lay_about;
    @BindView(R.id.lay_reli)
    LinearLayout lay_reli;
    @BindView(R.id.lay_edu)
    LinearLayout lay_edu;
    @BindView(R.id.lay_cont)
    LinearLayout lay_cont;
    @BindView(R.id.lay_physical)
    LinearLayout lay_physical;
    @BindView(R.id.lay_family)
    LinearLayout lay_family;

    @BindView(R.id.img_profile)
    ImageView img_profile;
    JSONArray photo_arr;
    OtherUser user;
    ActivityOtherUserProfileBinding binding;
    String other_id, other_matri_id, contact_view_security;
    boolean isProtected = false;

    private int placeHolder, photoProtectPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //setContentView(R.layout.activity_other_user_profile);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user_profile);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Profile");

        common = new Common(this);

        deviceWidth = Common.getDisplayWidth(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }
        img_profile.setImageResource(placeHolder);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("other_id")) {
                other_id = b.getString("other_id");
            }
        }

        host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("first");
        spec.setContent(R.id.tab1);
        spec.setIndicator(" My Profile");
        host.addTab(spec);

        spec = host.newTabSpec("second");
        spec.setContent(R.id.tab2);
        spec.setIndicator(" Preferences");
        host.addTab(spec);

        host.setCurrentTab(0);
        host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_selector);
        host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tabunselcolor);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            common.setDrawableLeftTextViewLefttab(R.drawable.user_fill_pink, tv);
            tv.setTextColor(Color.parseColor("#fa004a"));
        }
        TextView tv = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        common.setDrawableLeftTextViewLefttab(R.drawable.user_fill_pink, tv);

        TextView tv1 = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);

        common.setDrawableLeftTextViewLefttab(R.drawable.user_pink, tv1);

        host.setOnTabChangedListener(this);

        //tv_pro_disc=(TextView)findViewById(R.id.tv_pro_disc);

        tv_photo_count = (TextView) findViewById(R.id.tv_photo_count);
        tv_photo_count.setOnClickListener(this);

        tv_pro_per = (TextView) findViewById(R.id.tv_pro_per);
        tv_basic_label = (TextView) findViewById(R.id.tv_basic_label);
        tv_basic_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.card_pink, tv_basic_label);

        tv_basic_label_pref = (TextView) findViewById(R.id.tv_basic_label_pref);
        tv_basic_label_pref.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.family_pink, tv_basic_label_pref);

        tv_reli_label = (TextView) findViewById(R.id.tv_reli_label);
        tv_reli_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.religion_fill, tv_reli_label);

        //tv_reli_label_pref=(TextView)findViewById(R.id.tv_reli_label_pref);
        //tv_reli_label_pref.setOnClickListener(this);
        //common.setDrawableLeftTextViewLeft(R.drawable.religion_fill,tv_reli_label_pref);

        tv_pro_disc_label = (TextView) findViewById(R.id.tv_pro_disc_label);
        tv_pro_disc_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink, tv_pro_disc_label);

        tv_edu_label = (TextView) findViewById(R.id.tv_edu_label);
        tv_edu_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.edu_pink, tv_edu_label);

        //tv_edu_label_pref=(TextView)findViewById(R.id.tv_edu_label_pref);
        // tv_edu_label_pref.setOnClickListener(this);
        //common.setDrawableLeftTextViewLeft(R.drawable.edu_pink,tv_edu_label_pref);

        tv_phy_label = (TextView) findViewById(R.id.tv_phy_label);
        tv_phy_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.heart_fill_pink, tv_phy_label);

        tv_locat_label = (TextView) findViewById(R.id.tv_locat_label);
        tv_locat_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.pin_location, tv_locat_label);

        tv_cont_label = (TextView) findViewById(R.id.tv_cont_label);
        tv_cont_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink, tv_cont_label);

        // tv_loca_label_pref=(TextView)findViewById(R.id.tv_loca_label_pref);
        // tv_loca_label_pref.setOnClickListener(this);
        //common.setDrawableLeftTextViewLeft(R.drawable.pin_location,tv_loca_label_pref);

        tv_family_label = (TextView) findViewById(R.id.tv_family_label);
        tv_family_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.family_pink, tv_family_label);

        // img_basic.setOnClickListener(this);

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProtected) {
                    alertPhotoPassword();
                } else {
                    if (photo_arr.length() != 0) {
                        Intent intent = new Intent(getApplicationContext(), GallaryActivity.class);
                        intent.putExtra("imagePosition", 0);
                        intent.putExtra("imageArray", photo_arr.toString());
                        startActivity(intent);
                    }
                }

            }
        });

        getMyProfile();

        scroll.scrollTo(0, 0);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Bundle b = intent.getExtras();
        if (b != null) {
            if (b.containsKey("other_id")) {
                if (!other_id.equals(b.getString("other_id"))) {
                    other_id = b.getString("other_id");
                    getMyProfile();
                }

            }
        }
    }

    private void getMyProfile() {
        pd = new ProgressDialog(OtherUserProfileActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", other_id);
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.other_user_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {
                        JSONObject data = object.getJSONObject("data");

                        String weight;
                        if (checkField(data.getString("weight")).equals("N/A")) {
                            weight = "N/A";
                        } else {
                            weight = checkField(data.getString("weight"));
                        }

                        String photo_view_status = data.getString("photo_view_status");
                        String photo_view_count = data.getString("photo_view_count");

                        if (photo_view_status.equals("0")) {
                            if (photo_view_count.equals("0")) {
                                isProtected = true;
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    img_profile.setImageResource(R.drawable.photopassword_male);
                                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                                    img_profile.setImageResource(R.drawable.photopassword_female);
                                }
                            } else if (photo_view_count.equals("1")) {
                                if (!data.getString("photo1_approve").equals("UNAPPROVED")) {
                                    Picasso.get().load(data.getString("photo1")).into(img_profile);
                                } else {
                                    if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                        img_profile.setImageResource(R.drawable.male);
                                    } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                                        img_profile.setImageResource(R.drawable.female);
                                    }
                                }
                            }
                        } else if (photo_view_status.equals("2") && MyApplication.getPlan()) {
                            if (data.getString("photo1_approve").equals("UNAPPROVED")) {
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    img_profile.setImageResource(R.drawable.male);
                                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                                    img_profile.setImageResource(R.drawable.female);
                                }
                            } else {
                                if (!data.getString("photo1").equals("")) {
                                    Picasso.get()
                                            .load(data.getString("photo1"))
                                            .placeholder(placeHolder)
                                            .error(placeHolder)
                                            //.fit()
                                            .resize(deviceWidth, deviceWidth)
                                            .centerInside()
                                            .into(img_profile);
                                }
                            }
                        } else if (photo_view_status.equals("2") && !MyApplication.getPlan()) {
                            if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                img_profile.setImageResource(R.drawable.male);
                            } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                                img_profile.setImageResource(R.drawable.female);
                            }
                        } else if (photo_view_status.equals("1")) {
                            if (data.getString("photo1_approve").equals("UNAPPROVED")) {
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    img_profile.setImageResource(R.drawable.male);
                                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                                    img_profile.setImageResource(R.drawable.female);
                                }
                            } else {
                                if (!data.getString("photo1").equals("")) {
                                    Picasso.get()
                                            .load(data.getString("photo1"))
                                            .placeholder(placeHolder)
                                            .error(placeHolder)
                                            //.fit()
                                            .resize(deviceWidth, deviceWidth)
                                            .centerInside()
                                            .into(img_profile);
                                }
                            }
                        } else {
                            if (data.getString("photo1_approve").equals("UNAPPROVED")) {
                                if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
                                    img_profile.setImageResource(R.drawable.male);
                                } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
                                    img_profile.setImageResource(R.drawable.female);
                                }
                            } else {
                                if (!data.getString("photo1").equals("")) {
                                    Picasso.get()
                                            .load(data.getString("photo1"))
                                            .placeholder(placeHolder)
                                            .error(placeHolder)
                                            //.fit()
                                            .resize(deviceWidth, deviceWidth)
                                            .centerInside()
                                            .into(img_profile);
                                }
                            }

                        }

                        // if (!data.getString("photo1").equals("")){
                        //   if (!data.getString("photo1_approve").equals("UNAPPROVED"))
                        //       Picasso.with(getApplicationContext()).load(data.getString("photo1")).into(img_profile);
                        // }

                        contact_view_security = data.getString("contact_view_security");
                        Log.d("resp", contact_view_security);

                        user = new OtherUser();
                        TextView tv = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
                        tv.setText(" " + data.getString("username"));

                        other_matri_id = data.getString("matri_id");
                        user.setName(checkField(data.getString("username")));
                        user.setMarital_status(checkField(data.getString("marital_status")));
                        user.setM_tongue(checkField(data.getString("mtongue_name")));
                        user.setLanguage(checkField(data.getString("languages_known_str")));

                        if (!checkField(data.getString("height")).equals("N/A")) {
                            user.setHeight(common.calculateHeight(data.getString("height")));
                        } else
                            user.setHeight("N/A");
                        //user.setHeight(checkField(data.getString("height")));

                        //tv_photo_count
                        JSONArray fileds = data.getJSONArray("fileds");
                        photo_arr = fileds.getJSONObject(7).getJSONArray("value");
                        if (photo_arr.length() >= 2)
                            tv_photo_count.setText((photo_arr.length() - 1) + "+");
                        else
                            tv_photo_count.setVisibility(View.GONE);

                        user.setEmail(checkField(data.getString("email")));
                        user.setWeight(weight + " kg");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String age = common.getAge(data.getString("birthdate"), sdf) + " Years";
                        user.setDob(changeDate(data.getString("birthdate")));
                        user.setAge(age);
                        user.setReligion(checkField(data.getString("religion_name")));
                        user.setCaste(checkField(data.getString("caste_name")));
                        user.setSubcaste(checkField(data.getString("subcaste")));
                        user.setManglik(checkField(data.getString("manglik")));
                        user.setStar(checkField(data.getString("star_str")));
                        user.setHoroscope(checkField(data.getString("horoscope")));
                        user.setGothra(checkField(data.getString("gothra")));
                        user.setMoonsign(checkField(data.getString("moonsign_str")));
                        user.setAbout(checkField(data.getString("profile_text")));
                        user.setHobby(checkField(data.getString("hobby")));
                        user.setBirth_place(checkField(data.getString("birthplace")));
                        user.setBirth_time(checkField(data.getString("birthtime")));
                        user.setCreate_by(checkField(data.getString("profileby")));
                        user.setReferance(checkField(data.getString("reference")));
                        user.setEducation(checkField(data.getString("education_detail_str")));
                        user.setEmployee(checkField(data.getString("employee_in")));
                        user.setAnnual_income(checkField(data.getString("income")));
                        user.setOccupation(checkField(data.getString("occupation_name")));
                        user.setDesignation(checkField(data.getString("designation_name")));
                        user.setBody_type(checkField(data.getString("bodytype")));
                        user.setEat(checkField(data.getString("diet")));
                        user.setSmoke(checkField(data.getString("smoke")));
                        user.setDrink(checkField(data.getString("drink")));
                        user.setSkin(checkField(data.getString("complexion")));
                        user.setBlood_group(checkField(data.getString("blood_group")));
                        user.setCountry(checkField(data.getString("country_name")));
                        user.setState(checkField(data.getString("state_name")));
                        user.setCity(checkField(data.getString("city_name")));
                        user.setAddress(checkField(data.getString("address")));
                        user.setMobile(checkField(data.getString("mobile")));
                        user.setPhone(checkField(data.getString("phone")));
                        user.setTime_call(checkField(data.getString("time_to_call")));
                        user.setResidence(checkField(data.getString("residence")));
                        user.setFamily_type(checkField(data.getString("family_type")));
                        user.setFather_name(checkField(data.getString("father_name")));
                        user.setFather_occupation(checkField(data.getString("father_occupation")));
                        user.setMother_name(checkField(data.getString("mother_name")));
                        user.setMother_occupation(checkField(data.getString("mother_occupation")));
                        user.setFamily_status(checkField(data.getString("family_status")));
                        user.setNo_brother(checkField(data.getString("no_of_brothers")));
                        user.setNo_sister(checkField(data.getString("no_of_sisters")));
                        user.setNo_mari_brother(checkField(data.getString("no_of_married_brother")));
                        user.setNo_mari_sister(checkField(data.getString("no_of_married_sister")));
                        user.setAbout_family(checkField(data.getString("family_details")));

                        JSONArray partners_field = data.getJSONArray("partners_field");
                        displayPref(partners_field);
                        /*user.setLooking(checkField(data.getString("looking_for")));
                        user.setPart_complx(checkField(data.getString("part_complexion")));
                        user.setPart_age(data.getString("part_frm_age")+" to "+data.getString("part_to_age")+" Years");
                        user.setPart_height(data.getString("part_height")+" to "+data.getString("part_height_to"));
                        user.setPart_body_type(checkField(data.getString("part_bodytype")));
                        user.setPart_eat(checkField(data.getString("part_diet")));
                        user.setPart_smoke(checkField(data.getString("part_smoke")));
                        user.setPart_drink(checkField(data.getString("part_drink")));
                        user.setPart_tongue(checkField(data.getString("part_mother_tongue")));
                        user.setPart_expectation(checkField(data.getString("part_expect")));
                        user.setPart_religion(checkField(data.getString("part_religion")));
                        user.setPart_caste(checkField(data.getString("part_caste")));
                        user.setPart_manglik(checkField(data.getString("part_manglik")));
                        user.setPart_star(checkField(data.getString("part_star")));
                        user.setPart_country(checkField(data.getString("part_country_living")));
                        user.setPart_state(checkField(data.getString("part_state")));
                        user.setPart_city(checkField(data.getString("part_city")));
                        user.setPart_residence(checkField(data.getString("part_resi_status")));
                        user.setPart_education(checkField(data.getString("part_education")));
                        user.setPart_employee(checkField(data.getString("part_employee_in")));
                        user.setPart_occupation(checkField(data.getString("part_occupation")));
                        user.setPart_designation(checkField(data.getString("part_designation")));
                        user.setPart_annual_income(checkField(data.getString("part_income")));*/

                        binding.setUser(user);
                        // JSONArray fileds=data.getJSONArray("fileds");
                        // displayProfileData(fileds);
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

    public String changeDate(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM dd, yyyy";
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

    private void displayPref(JSONArray partners_field) throws JSONException {
        JSONArray array = partners_field.getJSONObject(0).getJSONArray("value");
        lay_basic_pref.removeAllViews();
        int pref = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String title = obj.getString("title");
            String type = obj.getString("type");
            String value = obj.getString("value");

            LinearLayout main = new LinearLayout(this);
            main.setOrientation(LinearLayout.HORIZONTAL);
            main.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            main.setBackgroundResource(R.drawable.underline_gray);

            LinearLayout submain = new LinearLayout(this);
            submain.setOrientation(LinearLayout.VERTICAL);
            submain.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView1.setPadding(20, 10, 10, 10);
            textView1.setText(title);
            textView1.setTextSize(16f);
//            textView1.setTextColor(Color.BLACK);
//            textView1.setTypeface(Typeface.DEFAULT_BOLD);
            submain.addView(textView1);

            TextView textView2 = new TextView(this);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView2.setTextColor(Color.BLACK);
            textView2.setTypeface(Typeface.DEFAULT_BOLD);

            String prefValue = "N/A";
            if (value != null && value.length() > 0) {
                prefValue = value;
            }
            textView2.setText(prefValue);
            textView2.setPadding(20, 10, 10, 10);
            textView2.setTextSize(15f);
            submain.addView(textView2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
            param.setMargins(0, 20, 0, 0);
            LinearLayout submain1 = new LinearLayout(this);
            submain1.setOrientation(LinearLayout.VERTICAL);
            submain1.setGravity(Gravity.CENTER);
            submain1.setLayoutParams(param);

            main.addView(submain);
            if (type.equals("Yes")) {
                pref = pref + 1;
                ImageView img = new ImageView(this);
                img.setImageResource(R.drawable.check_fill_green);
                img.setLayoutParams(new LinearLayout.LayoutParams(50, 50));

                submain1.addView(img);
                main.addView(submain1);
            }
            lay_basic_pref.addView(main);
        }
        tv_basic_label_pref.setText(" You match " + pref + " out of 9 Preferences");
    }

    private String checkField(String val) {
        if (val.equals("") || val.equals("null")) {
            return "N/A";
        }
        return val;
    }

    private void viewContact() {
        pd = new ProgressDialog(OtherUserProfileActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("receiver_matri_id", other_matri_id);

        common.makePostRequest(Utils.view_contact, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("success").equals("success")) {
                        lay_cont.setVisibility(lay_cont.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    } else
                        common.showToast(object.getString("errmessage"));

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

    private void alertPhotoPassword() {
        final String[] arr = new String[]{"We found your profile to be a good match. Please send me Photo password to proceed further.",
                "I am interested in your profile. I would like to view photo now, send me password."};
        final String[] selected = {"We found your profile to be a good match. Please send me Photo password to proceed further."};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

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
                sendRequest(selected[0]);
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

    private void sendRequest(String int_msg) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", other_matri_id);
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

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_basic_label:
                lay_basic.setVisibility(lay_basic.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_cont_label:
                if (lay_cont.getVisibility() == View.GONE) {
                    if (pd != null)
                        pd.dismiss();
                    viewContact();
                } else
                    lay_cont.setVisibility(lay_cont.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                //  }else {
                //      common.showToast("This member only shows his/her contact details if you have already sent him/her express interest, and he/she has accepted it.");
                //  }
                break;
            case R.id.tv_photo_count:
                if (!isProtected) {
                    if (photo_arr.length() != 0) {
                        Intent intent = new Intent(getApplicationContext(), GallaryActivity.class);
                        intent.putExtra("imagePosition", 0);
                        intent.putExtra("imageArray", photo_arr.toString());
                        startActivity(intent);
                    }
                }
                //lay_basic_pref.setVisibility(lay_basic_pref.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_locat_label:
                lay_locat.setVisibility(lay_locat.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_reli_label:
                lay_reli.setVisibility(lay_reli.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_pro_disc_label:
                lay_about.setVisibility(lay_about.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                // tv_pro_disc.setVisibility(tv_pro_disc.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_edu_label:
                lay_edu.setVisibility(lay_edu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_phy_label:
                lay_physical.setVisibility(lay_physical.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_family_label:
                lay_family.setVisibility(lay_family.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            // case R.id.img_basic:
            //    startActivity(new Intent(OtherUserProfileActivity.this,EditProfileActivity.class));
            //    break;
        }

    }

    @Override
    public void onTabChanged(String tabId) {
        //TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        //common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink,tv);
        switch (tabId) {
            case "first":
                host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tab_selector);
                host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tabunselcolor);

                TextView tv = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
                common.setDrawableLeftTextViewLefttab(R.drawable.user_fill_pink, tv);
                TextView tv1 = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);

                common.setDrawableLeftTextViewLefttab(R.drawable.user_pink, tv1);

                break;
            case "second":
                host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab_selector);
                host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tabunselcolor);

                TextView tv2 = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
                common.setDrawableLeftTextViewLefttab(R.drawable.user_pink, tv2);
                TextView tv3 = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);

                common.setDrawableLeftTextViewLefttab(R.drawable.user_fill_pink, tv3);
                break;

        }
    }

}
