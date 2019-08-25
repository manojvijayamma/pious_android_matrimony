package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.MyData;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.mega.matrimony.databinding.ActivityViewMyProfileBinding;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMyProfileActivity extends AppCompatActivity implements View.OnClickListener,TabHost.OnTabChangeListener {

    TextView tv_basic_label,tv_basic_label_pref,tv_loca_label_pref,tv_edu_label_pref,tv_reli_label_pref,tv_pro_disc_label,tv_edu_label,tv_reli_label,tv_phy_label,tv_family_label,
            tv_locat_label,tv_pro_per;

    private int deviceWidth = 0;

    Common common;
    SessionManager session;
    ProgressDialog pd;
    TabHost host;

    @BindView(R.id.lay_main)
    LinearLayout lay_main;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.lay_basic)
    LinearLayout lay_basic;
    @BindView(R.id.lay_reli_pref)
    LinearLayout lay_reli_pref;
    @BindView(R.id.lay_edu_pref)
    LinearLayout lay_edu_pref;
    @BindView(R.id.lay_loca_pref)
    LinearLayout lay_loca_pref;
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
    @BindView(R.id.lay_physical)
    LinearLayout lay_physical;
    @BindView(R.id.lay_family)
    LinearLayout lay_family;
    @BindView(R.id.img_basic)
    ImageView img_basic;
    @BindView(R.id.img_religion)
    ImageView img_religion;
    @BindView(R.id.img_prof)
    ImageView img_prof;
    @BindView(R.id.img_edu)
    ImageView img_edu;
    @BindView(R.id.img_life)
    ImageView img_life;
    @BindView(R.id.img_location)
    ImageView img_location;
    @BindView(R.id.img_family)
    ImageView img_family;
    @BindView(R.id.img_profile)
    ImageView img_profile;
    @BindView(R.id.img_basic_pref)
    ImageView img_basic_pref;
    @BindView(R.id.img_reli_pref)
    ImageView img_reli_pref;
    @BindView(R.id.img_loca_pref)
    ImageView img_loca_pref;
    @BindView(R.id.img_edu_pref)
    ImageView img_edu_pref;
    @BindView(R.id.img_edit)
    ImageView img_edit;
    @BindView(R.id.lay_t_chld)
    LinearLayout lay_t_chld;
    @BindView(R.id.lay_s_chld)
    LinearLayout lay_s_chld;
    //@BindView(R.id.img_photo)
    //ImageView img_photo;
    ActivityViewMyProfileBinding binding;
    MyData user;
    JSONArray photo_arr;
    ProgressBar circularProgressbar;
    private int placeHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_my_profile);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_my_profile);
        ButterKnife.bind(this);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        common=new Common(this);
        session=new SessionManager(this);

        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        deviceWidth = Common.getDisplayWidth(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.male;
        }
        img_profile.setImageResource(placeHolder);

        TabHost.TabSpec spec = host.newTabSpec("first");
        spec.setContent(R.id.tab1);
        spec.setIndicator(" My Profile");
        host.addTab(spec);

        spec = host.newTabSpec("second");
        spec.setContent(R.id.tab2);
        spec.setIndicator(" My Preferences");
        host.addTab(spec);

        host.setCurrentTab(0);
        host.getTabWidget().getChildAt(0) .setBackgroundResource(R.drawable.tab_selector);
        host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tabunselcolor);

        for(int i=0;i<host.getTabWidget().getChildCount();i++){
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink,tv);
            tv.setTextColor(Color.parseColor("#fa004a"));
        }
        TextView tv = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink,tv);

        TextView tv1 = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        common.setDrawableLeftTextViewLeft(R.drawable.user_pink,tv1);

        host.setOnTabChangedListener(this);

        tv_pro_per=(TextView)findViewById(R.id.tv_pro_per);
        tv_basic_label=(TextView)findViewById(R.id.tv_basic_label);
        tv_basic_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.card_pink,tv_basic_label);

        circularProgressbar=(ProgressBar)findViewById(R.id.circularProgressbar);

        tv_basic_label_pref=(TextView)findViewById(R.id.tv_basic_label_pref);
        tv_basic_label_pref.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.card_pink,tv_basic_label_pref);

        tv_reli_label=(TextView)findViewById(R.id.tv_reli_label);
        tv_reli_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.religion_fill,tv_reli_label);

        tv_reli_label_pref=(TextView)findViewById(R.id.tv_reli_label_pref);
        tv_reli_label_pref.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.religion_fill,tv_reli_label_pref);

        tv_pro_disc_label=(TextView)findViewById(R.id.tv_pro_disc_label);
        tv_pro_disc_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink,tv_pro_disc_label);

        tv_edu_label=(TextView)findViewById(R.id.tv_edu_label);
        tv_edu_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.edu_pink,tv_edu_label);

        tv_edu_label_pref=(TextView)findViewById(R.id.tv_edu_label_pref);
        tv_edu_label_pref.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.edu_pink,tv_edu_label_pref);

        tv_phy_label=(TextView)findViewById(R.id.tv_phy_label);
        tv_phy_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.heart_fill_pink,tv_phy_label);

        tv_locat_label=(TextView)findViewById(R.id.tv_locat_label);
        tv_locat_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.pin_location,tv_locat_label);

        tv_loca_label_pref=(TextView)findViewById(R.id.tv_loca_label_pref);
        tv_loca_label_pref.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.pin_location,tv_loca_label_pref);

        tv_family_label=(TextView)findViewById(R.id.tv_family_label);
        tv_family_label.setOnClickListener(this);
        common.setDrawableLeftTextViewLeft(R.drawable.family_pink,tv_family_label);

        img_profile.setOnClickListener(this);
        img_edit.setOnClickListener(this);
       // img_photo.setOnClickListener(this);
        img_basic.setOnClickListener(this);
        img_religion.setOnClickListener(this);
        img_prof.setOnClickListener(this);
        img_edu.setOnClickListener(this);
        img_life.setOnClickListener(this);
        img_location.setOnClickListener(this);
        img_family.setOnClickListener(this);

        img_basic_pref.setOnClickListener(this);
        img_reli_pref.setOnClickListener(this);
        img_loca_pref.setOnClickListener(this);
        img_edu_pref.setOnClickListener(this);

        getMyProfile();

        scroll.scrollTo(0,0);

    }

    private void getMyProfile() {
        pd=new ProgressDialog(ViewMyProfileActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String,String> param=new HashMap<>();
        param.put("member_id",session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(Utils.get_my_profile, param, response -> {
            pd.dismiss();
            try {
                AppDebugLog.print("resp : "+response);
                JSONObject object=new JSONObject(response);
                session.setUserData(SessionManager.TOKEN,object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    String weight;
                    if (checkField(data.getString("weight")).equals("N/A")){
                        weight="N/A";
                    }else {
                        weight=checkField(data.getString("weight"));
                    }

                    if (!data.getString("photo1").equals("")){
                        Picasso.get()
                                .load(data.getString("photo1"))
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                //.fit()
                                .resize(deviceWidth, deviceWidth)
                                .centerInside()
                                .into(img_profile);
                    }

                    circularProgressbar.setProgress(data.getInt("percentage"));
                    tv_pro_per.setText(data.getInt("percentage")+"%");

                    JSONArray fileds=data.getJSONArray("fileds");
                    photo_arr=fileds.getJSONObject(7).getJSONArray("value");

                    user=new MyData();
                    user.setName(checkField(data.getString("username")));
                    user.setMarital_status(checkField(data.getString("marital_status")));
                    user.setTotal_child(checkField(data.getString("total_children")));
                    user.setStatus_child(checkField(data.getString("status_children")));
                    user.setM_tongue(checkField(data.getString("mtongue_name")));
                    user.setLanguage(checkField(data.getString("languages_known_str")));

                    if (!checkField(data.getString("height")).equals("N/A")){
                        user.setHeight(common.calculateHeight(data.getString("height")));
                    }else
                        user.setHeight("N/A");

                    user.setWeight(weight+" kg");

                    if (data.getString("marital_status").equals("Unmarried")){
                        lay_s_chld.setVisibility(View.GONE);
                        lay_t_chld.setVisibility(View.GONE);
                    }else {
                        if (data.getString("total_children").equals("0")){
                            lay_s_chld.setVisibility(View.GONE);
                        }else {
                            lay_s_chld.setVisibility(View.VISIBLE);
                        }
                        lay_t_chld.setVisibility(View.VISIBLE);
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String age= common.getAge(data.getString("birthdate"),sdf)+" Years";
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

                    user.setLooking(checkField(data.getString("looking_for")));
                    user.setPart_complx(checkField(data.getString("part_complexion")));
                    user.setPart_age(data.getString("part_frm_age")+" to "+data.getString("part_to_age")+" Years");
                    String fhite="",thite="";
                    if (!checkField(data.getString("part_height")).equals("N/A")){
                        fhite=common.calculateHeight(data.getString("part_height"));
                    }
                    if (!checkField(data.getString("part_height_to")).equals("N/A")){
                        thite=common.calculateHeight(data.getString("part_height_to"));
                    }
                    if (thite.equals("")&&fhite.equals("")){
                        user.setPart_height("N/A");
                    }else {
                        user.setPart_height(fhite+" to "+thite);
                    }

                    user.setPart_body_type(checkField(data.getString("part_bodytype")));
                    user.setPart_eat(checkField(data.getString("part_diet")));
                    user.setPart_smoke(checkField(data.getString("part_smoke")));
                    user.setPart_drink(checkField(data.getString("part_drink")));
                    user.setPart_tongue(checkField(data.getString("part_mother_tongue_str")));
                    user.setPart_expectation(checkField(data.getString("part_expect")));
                    user.setPart_religion(checkField(data.getString("part_religion_str")));
                    user.setPart_caste(checkField(data.getString("part_caste_str")));
                    user.setPart_manglik(checkField(data.getString("part_manglik")));
                    user.setPart_star(checkField(data.getString("part_star_str")));
                    user.setPart_country(checkField(data.getString("part_country_living_str")));
                    user.setPart_state(checkField(data.getString("part_state_str")));
                    user.setPart_city(checkField(data.getString("part_city_str")));
                    user.setPart_residence(checkField(data.getString("part_resi_status")));
                    user.setPart_education(checkField(data.getString("part_education_str")));
                    user.setPart_employee(checkField(data.getString("part_employee_in")));
                    user.setPart_occupation(checkField(data.getString("part_occupation_str")));
                    user.setPart_designation(checkField(data.getString("part_designation_str")));
                    user.setPart_annual_income(checkField(data.getString("part_income")));

                    binding.setUser(user);
                   // JSONArray fileds=data.getJSONArray("fileds");
                   // displayProfileData(fileds);
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

    public String changeDate(String time) {
        String  outputPattern= "dd-MM-yyyy";//"MMM dd, yyyy";
        String  inputPattern= "yyyy-M-dd";
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

    private String checkField(String val){
        if (val.equals("") || val.equals("null")){
            return "N/A";
        }
        return val;
    }

    private void displayProfileData(JSONArray fileds) throws JSONException {
        if (fileds.length()!=0){
            TableLayout.LayoutParams table_param=new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int i=0;i<fileds.length();i++){
                JSONObject object=fileds.getJSONObject(i);

                TableLayout main_table=new TableLayout(this);
                main_table.setLayoutParams(table_param);
                TableRow header_row=new TableRow(this);
                header_row.setLayoutParams(table_param);
                TextView head=new TextView(this);
                head.setText(object.getString("name"));
                head.setTag(object.getString("id"));
                head.setTextColor(getResources().getColor(R.color.red));
                head.setTextSize(16);
                head.setTypeface(Typeface.DEFAULT_BOLD);
                head.setPadding(15,15,15,15);
                header_row.addView(head);
                //main_table.addView(header_row);
                JSONArray fld=object.getJSONArray("value");
                for (int k=0;k<fld.length();k++){
                    JSONObject obj=fld.getJSONObject(k);
                    TableRow fld_row=new TableRow(this);
                    fld_row.setLayoutParams(table_param);
                    fld_row.setOrientation(LinearLayout.HORIZONTAL);

                    for (int j=0;j<2;j++){
                        LinearLayout layout=new LinearLayout(this);
                        layout.setLayoutParams(table_param);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        TextView title=new TextView(this);
                        title.setText(obj.getString("title"));
                        TextView value=new TextView(this);
                        value.setText(obj.getString("value"));
                        value.setTextColor(getResources().getColor(R.color.black));

                        layout.addView(title);
                        layout.addView(value);
                        fld_row.addView(layout);

                    }
                    main_table.addView(fld_row);
                }
                lay_main.addView(main_table);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_profile:
                if (photo_arr.length()!=0){
                    Intent intent=new Intent(getApplicationContext(), GallaryActivity.class);
                    intent.putExtra("imagePosition",0);
                    intent.putExtra("imageArray",photo_arr.toString());
                    startActivity(intent);
                }
                break;
            case R.id.img_edit:
               // gotoEdit("basic");
                Intent i=new Intent(this, ManagePhotosActivity.class);
                startActivityForResult(i,7);

                break;
            case R.id.tv_basic_label:
                lay_basic.setVisibility(lay_basic.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_reli_label_pref:
                lay_reli_pref.setVisibility(lay_reli_pref.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_edu_label_pref:
                lay_edu_pref.setVisibility(lay_edu_pref.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_loca_label_pref:
                lay_loca_pref.setVisibility(lay_loca_pref.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.tv_basic_label_pref:
                lay_basic_pref.setVisibility(lay_basic_pref.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
            case R.id.img_basic:
                gotoEdit("basic");
                break;
            case R.id.img_religion:
                gotoEdit("religion");
                break;
            case R.id.img_prof:
                gotoEdit("profile");
                break;
            case R.id.img_edu:
                gotoEdit("education");
                break;
            case R.id.img_life:
                gotoEdit("life");
                break;
            case R.id.img_location:
                gotoEdit("location");
                break;
            case R.id.img_family:
                gotoEdit("family");
                break;

            case R.id.img_basic_pref:
                gotoEditPref("basic");
                break;
            case R.id.img_reli_pref:
                gotoEditPref("religion");
                break;
            case R.id.img_loca_pref:
                gotoEditPref("location");
                break;
            case R.id.img_edu_pref:
                gotoEditPref("education");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 7) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                String tabid=data.getStringExtra("tabid");
                if (result.equals("reload"))
                    getMyProfile();
                   //recreate();
                if (tabid.equals("my")){
                    host.setCurrentTab(0);
                    host.getTabWidget().getChildAt(0) .setBackgroundResource(R.drawable.tab_selector);
                    host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tabunselcolor);
                }else {
                    host.setCurrentTab(1);
                    host.getTabWidget().getChildAt(1) .setBackgroundResource(R.drawable.tab_selector);
                    host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tabunselcolor);
                }

            }

        }
    }

    private void gotoEdit(String tag){
        Intent i=new Intent(ViewMyProfileActivity.this, EditProfileActivity.class);
        i.putExtra("pageTag",tag);
        startActivityForResult(i,7);
    }

    private void gotoEditPref(String tag){
        Intent i=new Intent(ViewMyProfileActivity.this, EditPreferenceActivity.class);
        i.putExtra("pageTag",tag);
        startActivityForResult(i,7);
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
                common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink,tv);
                TextView tv1 = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
                common.setDrawableLeftTextViewLeft(R.drawable.user_pink,tv1);

                break;
            case "second":
                host.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.tab_selector);
                host.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.tabunselcolor);

                TextView tv2 = (TextView) host.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
                common.setDrawableLeftTextViewLeft(R.drawable.user_pink,tv2);
                TextView tv3 = (TextView) host.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
                common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink,tv3);
                break;

        }
    }
}
