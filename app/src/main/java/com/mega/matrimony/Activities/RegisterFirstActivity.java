package com.mega.matrimony.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Custom.MultiSelectionSpinner;
import com.mega.matrimony.Network.ConnectionDetector;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.mega.matrimony.retrofit.AppApiService;
import com.mega.matrimony.retrofit.ProgressRequestBody;
import com.mega.matrimony.retrofit.RetrofitClient;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RegisterFirstActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ProgressRequestBody.UploadCallbacks {
    private static final String STEP_1 = "first";
    private static final String STEP_2 = "second";
    private static final String STEP_3 = "third";
    private static final String STEP_4 = "four";
    private static final String STEP_5 = "five";
    private static final String STEP_6 = "six";

    CountryCodePicker spin_code;
    Button btn_male, btn_female, btn_login, btn_first_submit, btn_second_prev, btn_second_submit,
            btn_third_submit, btn_third_prev, btn_four_submit, btn_four_prev, btn_five_submit, btn_five_prev, btn_choose,
            btn_six_submit, btn_six_prev, btn_fb_signup;
    EditText et_f_name, et_l_name, et_email, et_password, et_mobile, et_dob, et_sub_caste, et_gothra, et_hoby, et_about;
    Common common;
    Spinner spin_religion, spin_caste, spin_tongue, spin_country, spin_state, spin_city, spin_mari, spin_t_child, spin_child_status,
            spin_emp_in, spin_income, spin_occupation, spin_designation, spin_height, spin_weight, spin_eat, spin_smok, spin_drink,
            spin_body, spin_skin, spin_manglik, spin_star, spin_horo, spin_moon;
    MultiSelectionSpinner spin_edu;
    LinearLayout lay_second, lay_first, lay_third, lay_four, lay_five, lay_six, layoutBottomSheet;
    RelativeLayout lay_t_child, lay_status_child;
    ScrollView ragi_scroll;
    BottomSheetBehavior sheetBehavior;
    final Calendar myCalendar = Calendar.getInstance();
    private TextView tv_cancel, tv_gallary, tv_camera;
    String mCurrentPhotoPath, page_name = STEP_1, religion_id = "", caste_id = "", tongue_id = "", gender = "Male", country_code = "+91",
            ragister_id = "", country_id = "", state_id = "", city_id = "", mari_id = "", total_child_id = "", status_child_id = "",
            edu_id = "", emp_id = "", income_id = "", occu_id = "", desig_id = "", hite_id = "", weight_id = "", eat_id = "", smok_id = "", drink_id = "",
            body_id = "", skin_id = "", manglik_id = "", star_id = "", horo_id = "", moon_id = "", org_path, crop_path, fb_id = "";
    ImageView img_profile;
    HashMap<String, String> reli_map, caste_map, tongue_map, country_map, state_map, city_map, mari_map, total_child_map,
            status_child_map, edu_map, emp_map, income_map, occu_map, desig_map, hite_map, weight_map, eat_map, smok_map, drink_map,
            body_map, skin_map, manglik_map, star_map, horo_map, moon_map, image_map;
    ProgressDialog pd;
    SessionManager session;
    Uri resultUri;
    long totalSize = 0;
    boolean isImageSelect = false;
    DatePickerDialog.OnDateSetListener date;
    // CallbackManager callbackManager;
    TextInputLayout pass_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_first);

        common = new Common(this);
        session = new SessionManager(this);

        image_map = new HashMap<>();

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token != null)
            session.setUserData(SessionManager.KEY_DEVICE_TOKEN, token);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        lay_t_child = (RelativeLayout) findViewById(R.id.lay_t_child);
        lay_status_child = (RelativeLayout) findViewById(R.id.lay_status_child);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_gallary = (TextView) findViewById(R.id.tv_gallary);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        //  btn_fb_signup = (Button) findViewById(R.id.btn_fb_signup);
        et_f_name = (EditText) findViewById(R.id.et_f_name);
        et_l_name = (EditText) findViewById(R.id.et_l_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        pass_input = (TextInputLayout) findViewById(R.id.pass_input);

        tv_gallary.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });

        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("fb_data")) {
                try {
                    JSONObject obj = new JSONObject(b.getString("fb_data"));
                    et_email.setText(obj.getString("email"));
                    String[] arr = obj.getString("name").split(" ");
                    et_f_name.setText(arr[0]);
                    et_l_name.setText(arr[1]);
                    fb_id = obj.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fromGallary() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);

    }

    private void fromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "requestCode  " + requestCode + " resultCode  " + resultCode);
        //   callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {
                Uri des = Uri.fromFile(new File(mCurrentPhotoPath));
                org_path = des.getPath();
                final InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(des);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    image_map.put("profile_photo_org", encodedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                cropImage(des);
            } else if (requestCode == 2) {
                Uri selectedImageUri = data.getData();
                org_path = getRealPathFromURI(selectedImageUri);
                final InputStream imageStream;
                try {

                    imageStream = getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    image_map.put("profile_photo_org", encodedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                cropImage(selectedImageUri);
            } else if (requestCode == 3) {

                resultUri = UCrop.getOutput(data);
                crop_path = resultUri.getPath();

                final InputStream imageStream;
                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    // image_map.put("profile_photo_crop",encodedImage);
                    isImageSelect = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img_profile.setImageBitmap(bitmap);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void cropImage(Uri imagePath) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageExtension = "." + MimeTypeMap.getFileExtensionFromUrl(org_path);

        UCrop uCrop = UCrop.of(imagePath, Uri.fromFile(new File(getCacheDir(), timeStamp + imageExtension)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(250, 250);
        UCrop.Options options = new UCrop.Options();

        options.setToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        options.setToolbarWidgetColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        options.setRootViewBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        uCrop.withOptions(options);
        uCrop.start(this, 3);

    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //yyyy-M-dd
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_dob.setText(sdf.format(myCalendar.getTime()));
    }

    private void initData() throws JSONException {
        if (MyApplication.getSpinData() != null) {
            ragi_scroll = findViewById(R.id.ragi_scroll);
            lay_first = findViewById(R.id.lay_first);
            lay_second = findViewById(R.id.lay_second);
            lay_third = findViewById(R.id.lay_third);
            lay_four = findViewById(R.id.lay_four);
            lay_five = findViewById(R.id.lay_five);
            lay_six = findViewById(R.id.lay_six);

            btn_choose = findViewById(R.id.btn_choose);
            btn_choose.setOnClickListener(this);
            common.setDrawableLeftButton(R.drawable.edit_white, btn_choose);

            btn_six_submit = findViewById(R.id.btn_six_submit);
            btn_six_submit.setOnClickListener(this);
            btn_six_prev = findViewById(R.id.btn_six_prev);
            btn_six_prev.setOnClickListener(this);

            btn_five_submit = findViewById(R.id.btn_five_submit);
            btn_five_submit.setOnClickListener(this);
            btn_five_prev = findViewById(R.id.btn_five_prev);
            btn_five_prev.setOnClickListener(this);

            btn_four_submit = findViewById(R.id.btn_four_submit);
            btn_four_submit.setOnClickListener(this);
            btn_four_prev = findViewById(R.id.btn_four_prev);
            btn_four_prev.setOnClickListener(this);

            btn_third_submit = (Button) findViewById(R.id.btn_third_submit);
            btn_third_submit.setOnClickListener(this);
            btn_third_prev = (Button) findViewById(R.id.btn_third_prev);
            btn_third_prev.setOnClickListener(this);

            btn_second_prev = (Button) findViewById(R.id.btn_second_prev);
            btn_second_prev.setOnClickListener(this);
            btn_second_submit = (Button) findViewById(R.id.btn_second_submit);
            btn_second_submit.setOnClickListener(this);
            btn_first_submit = (Button) findViewById(R.id.btn_first_submit);
            btn_login = (Button) findViewById(R.id.btn_login);
            btn_first_submit.setOnClickListener(this);
            btn_login.setOnClickListener(this);

            btn_male = (Button) findViewById(R.id.btn_male);
            btn_female = (Button) findViewById(R.id.btn_female);
            btn_male.setOnClickListener(this);
            btn_female.setOnClickListener(this);
            spin_code = (CountryCodePicker) findViewById(R.id.spin_code);
            spin_code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected(Country country) {
                    country_code = country.getPhoneCode();
                    Log.d("resp", country_code + "  ");
                }
            });

            spin_moon = (Spinner) findViewById(R.id.spin_moon);
            List<String> moon = common.getListFromArray(MyApplication.getSpinData().getJSONArray("moonsign_list"), "Moonsign");
            moon_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("moonsign_list"), "Moonsign");
            ArrayAdapter adp_moon = new ArrayAdapter<String>(this, R.layout.spinner_item, moon);
            spin_moon.setAdapter(adp_moon);
            spin_moon.setOnItemSelectedListener(this);

            spin_horo = (Spinner) findViewById(R.id.spin_horo);
            List<String> horo = common.getListFromArray(MyApplication.getSpinData().getJSONArray("horoscope"), "Horoscope");
            horo_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("horoscope"), "Horoscope");
            ArrayAdapter adp_horo = new ArrayAdapter<String>(this, R.layout.spinner_item, horo);
            spin_horo.setAdapter(adp_horo);
            spin_horo.setOnItemSelectedListener(this);

            spin_star = (Spinner) findViewById(R.id.spin_star);
            List<String> star = common.getListFromArray(MyApplication.getSpinData().getJSONArray("star_list"), "Star");
            star_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("star_list"), "Star");
            ArrayAdapter adp_star = new ArrayAdapter<String>(this, R.layout.spinner_item, star);
            spin_star.setAdapter(adp_star);
            spin_star.setOnItemSelectedListener(this);

            spin_manglik = (Spinner) findViewById(R.id.spin_manglik);
            List<String> man = common.getListFromArray(MyApplication.getSpinData().getJSONArray("manglik"), "Manglik");
            manglik_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("manglik"), "Manglik");
            ArrayAdapter adp_man = new ArrayAdapter<String>(this, R.layout.spinner_item, man);
            spin_manglik.setAdapter(adp_man);
            spin_manglik.setOnItemSelectedListener(this);

            spin_height = (Spinner) findViewById(R.id.spin_height);
            List<String> hit = common.getListFromArray(MyApplication.getSpinData().getJSONArray("height_list"), "Height*");
            hite_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("height_list"), "Height*");
            ArrayAdapter adp_hit = new ArrayAdapter<String>(this, R.layout.spinner_item, hit);
            spin_height.setAdapter(adp_hit);
            spin_height.setOnItemSelectedListener(this);

            spin_weight = (Spinner) findViewById(R.id.spin_weight);
            List<String> wite = common.getListFromArray(MyApplication.getSpinData().getJSONArray("weight_list"), "Weight*");
            weight_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("weight_list"), "Weight*");
            ArrayAdapter adp_wite = new ArrayAdapter<String>(this, R.layout.spinner_item, wite);
            spin_weight.setAdapter(adp_wite);
            spin_weight.setOnItemSelectedListener(this);

            spin_eat = (Spinner) findViewById(R.id.spin_eat);
            List<String> eat = common.getListFromArray(MyApplication.getSpinData().getJSONArray("diet"), "Eating Habits*");
            eat_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("diet"), "Eating Habits*");
            ArrayAdapter adp_eat = new ArrayAdapter<String>(this, R.layout.spinner_item, eat);
            spin_eat.setAdapter(adp_eat);
            spin_eat.setOnItemSelectedListener(this);

            spin_smok = (Spinner) findViewById(R.id.spin_smok);
            List<String> smok = common.getListFromArray(MyApplication.getSpinData().getJSONArray("smoke"), "Smoking*");
            smok_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("smoke"), "Smoking*");
            ArrayAdapter adp_smok = new ArrayAdapter<String>(this, R.layout.spinner_item, smok);
            spin_smok.setAdapter(adp_smok);
            spin_smok.setOnItemSelectedListener(this);

            spin_drink = (Spinner) findViewById(R.id.spin_drink);
            List<String> drink = common.getListFromArray(MyApplication.getSpinData().getJSONArray("drink"), "Drinking*");
            drink_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("drink"), "Drinking*");
            ArrayAdapter adp_drink = new ArrayAdapter<String>(this, R.layout.spinner_item, drink);
            spin_drink.setAdapter(adp_drink);
            spin_drink.setOnItemSelectedListener(this);

            spin_body = (Spinner) findViewById(R.id.spin_body);
            List<String> body = common.getListFromArray(MyApplication.getSpinData().getJSONArray("bodytype"), "Body Type*");
            body_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("bodytype"), "Body Type*");
            ArrayAdapter adp_body = new ArrayAdapter<String>(this, R.layout.spinner_item, body);
            spin_body.setAdapter(adp_body);
            spin_body.setOnItemSelectedListener(this);

            spin_skin = (Spinner) findViewById(R.id.spin_skin);
            List<String> skin = common.getListFromArray(MyApplication.getSpinData().getJSONArray("complexion"), "Skin Tone*");
            skin_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("complexion"), "Skin Tone*");
            ArrayAdapter adp_skin = new ArrayAdapter<String>(this, R.layout.spinner_item, skin);
            spin_skin.setAdapter(adp_skin);
            spin_skin.setOnItemSelectedListener(this);

            spin_edu = (MultiSelectionSpinner) findViewById(R.id.spin_edu);
            // List<String> edu=common.getListFromArray(MyApplication.getSpinData().getJSONArray("education_list"));
            //edu_map=common.getMapFromArray(MyApplication.getSpinData().getJSONArray("education_list"));
            List<String> edu_val = common.getListFromArray(MyApplication.getSpinData().getJSONArray("education_list"), "Education*");
            List<String> edu_ids = common.getListFromArrayId(MyApplication.getSpinData().getJSONArray("education_list"));
            spin_edu.setItems_string_id(edu_val, edu_ids, "Education*");
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
            //ArrayAdapter adp_edu=new ArrayAdapter<String>(this,R.layout.spinner_item,edu);
            //spin_edu.setAdapter(adp_edu);
            //spin_edu.setOnItemSelectedListener(this);

            spin_emp_in = (Spinner) findViewById(R.id.spin_emp_in);
            List<String> emp = common.getListFromArray(MyApplication.getSpinData().getJSONArray("employee_in"), "Employee In*");
            emp_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("employee_in"), "Employee In*");
            ArrayAdapter adp_emp = new ArrayAdapter<String>(this, R.layout.spinner_item, emp);
            spin_emp_in.setAdapter(adp_emp);
            spin_emp_in.setOnItemSelectedListener(this);

            spin_income = (Spinner) findViewById(R.id.spin_income);
            List<String> incm = common.getListFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income*");
            income_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("income"), "Annual Income*");
            ArrayAdapter adp_incm = new ArrayAdapter<String>(this, R.layout.spinner_item, incm);
            spin_income.setAdapter(adp_incm);
            spin_income.setOnItemSelectedListener(this);

            spin_occupation = (Spinner) findViewById(R.id.spin_occupation);
            List<String> ocu = common.getListFromArray(MyApplication.getSpinData().getJSONArray("occupation_list"), "Occupation*");
            occu_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("occupation_list"), "Occupation*");
            ArrayAdapter adp_ocu = new ArrayAdapter<String>(this, R.layout.spinner_item, ocu);
            spin_occupation.setAdapter(adp_ocu);
            spin_occupation.setOnItemSelectedListener(this);

            spin_designation = (Spinner) findViewById(R.id.spin_designation);
            List<String> desi = common.getListFromArray(MyApplication.getSpinData().getJSONArray("designation_list"), "Designation*");
            desig_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("designation_list"), "Designation*");
            ArrayAdapter adp_desi = new ArrayAdapter<String>(this, R.layout.spinner_item, desi);
            spin_designation.setAdapter(adp_desi);
            spin_designation.setOnItemSelectedListener(this);

            spin_country = (Spinner) findViewById(R.id.spin_country);
            List<String> contry = common.getListFromArray(MyApplication.getSpinData().getJSONArray("country_list"), "Country*");
            country_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("country_list"), "Country*");
            ArrayAdapter adp_con = new ArrayAdapter<String>(this, R.layout.spinner_item, contry);
            spin_country.setAdapter(adp_con);
            spin_country.setOnItemSelectedListener(this);

            spin_state = (Spinner) findViewById(R.id.spin_state);
            List<String> stat = new ArrayList<>();
            stat.add("State*");
            state_map = new HashMap<>();
            state_map.put("State*", "0");
            ArrayAdapter adp_stat = new ArrayAdapter<String>(this, R.layout.spinner_item, stat);
            spin_state.setAdapter(adp_stat);
            spin_state.setOnItemSelectedListener(this);

            spin_city = (Spinner) findViewById(R.id.spin_city);
            List<String> cty = new ArrayList<>();
            cty.add("City*");
            city_map = new HashMap<>();
            city_map.put("City*", "0");
            ArrayAdapter adp_cty = new ArrayAdapter<String>(this, R.layout.spinner_item, cty);
            spin_city.setAdapter(adp_cty);
            spin_city.setOnItemSelectedListener(this);


            spin_mari = (Spinner) findViewById(R.id.spin_mari);
            List<String> mari = common.getListFromArray(MyApplication.getSpinData().getJSONArray("marital_status"), "Marital Status*");
            mari_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("marital_status"), "Marital Status*");
            ArrayAdapter adp_mari = new ArrayAdapter<String>(this, R.layout.spinner_item, mari);
            spin_mari.setAdapter(adp_mari);
            spin_mari.setSelection(1);
            spin_mari.setOnItemSelectedListener(this);

            spin_t_child = (Spinner) findViewById(R.id.spin_t_child);
            List<String> tot = common.getListFromArray(MyApplication.getSpinData().getJSONArray("total_children"), "Total Children");
            total_child_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("total_children"), "Total Children");
            ArrayAdapter adp_tot = new ArrayAdapter<String>(this, R.layout.spinner_item, tot);
            spin_t_child.setAdapter(adp_tot);
            spin_t_child.setOnItemSelectedListener(this);

            spin_child_status = (Spinner) findViewById(R.id.spin_child_status);
            List<String> status = common.getListFromArray(MyApplication.getSpinData().getJSONArray("status_children"), "Status Children");
            status_child_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("status_children"), "Status Children");
            ArrayAdapter adp_status = new ArrayAdapter<String>(this, R.layout.spinner_item, status);
            spin_child_status.setAdapter(adp_status);
            spin_child_status.setOnItemSelectedListener(this);

            spin_religion = (Spinner) findViewById(R.id.spin_religion);
            List<String> reli = common.getListFromArray(MyApplication.getSpinData().getJSONArray("religion_list"), "Religion*");
            reli_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("religion_list"), "Religion*");
            ArrayAdapter adp_reli = new ArrayAdapter<String>(this, R.layout.spinner_item, reli);
            spin_religion.setAdapter(adp_reli);
            spin_religion.setOnItemSelectedListener(this);

            spin_caste = (Spinner) findViewById(R.id.spin_caste);
            List<String> caste = new ArrayList<>();
            caste.add("Caste*");
            caste_map = new HashMap<>();
            caste_map.put("Caste*", "0");
            ArrayAdapter adp_caste = new ArrayAdapter<String>(this, R.layout.spinner_item, caste);
            spin_caste.setAdapter(adp_caste);
            spin_caste.setOnItemSelectedListener(this);

            spin_tongue = (Spinner) findViewById(R.id.spin_tongue);
            List<String> tong = common.getListFromArray(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue*");
            tongue_map = common.getMapFromArray(MyApplication.getSpinData().getJSONArray("mothertongue_list"), "Mother Tongue*");
            ArrayAdapter adp_tong = new ArrayAdapter<String>(this, R.layout.spinner_item, tong);
            spin_tongue.setAdapter(adp_tong);
            spin_tongue.setOnItemSelectedListener(this);


            et_mobile = (EditText) findViewById(R.id.et_mobile);
            et_dob = (EditText) findViewById(R.id.et_dob);
            et_dob.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Date d = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        d = sdf.parse("31/12/2000");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DatePickerDialog dialog = new DatePickerDialog(RegisterFirstActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                    dialog.getDatePicker().setMaxDate(d.getTime());
                    dialog.show();
                }
            });

            et_dob.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!et_dob.getText().toString().equals("")) {
                        et_dob.setError(null);
                    }
                }
            });
            et_sub_caste = (EditText) findViewById(R.id.et_sub_caste);
            et_gothra = (EditText) findViewById(R.id.et_gothra);
            et_hoby = (EditText) findViewById(R.id.et_hoby);
            et_about = (EditText) findViewById(R.id.et_about);

            common.setDrawableLeftEditText(R.drawable.user_pink, et_f_name);
            common.setDrawableLeftEditText(R.drawable.user_pink, et_l_name);
            common.setDrawableLeftEditText(R.drawable.email, et_email);
            common.setDrawableLeftEditText(R.drawable.mobile_pink, et_mobile);
            common.setDrawableLeftEditText(R.drawable.dob_pink, et_dob);
            common.setDrawableLeftEditText(R.drawable.state_pink, et_sub_caste);
            common.setDrawableLeftEditText(R.drawable.gotra_pink, et_gothra);
            common.setDrawableLeftEditText(R.drawable.hoby_pink, et_hoby);
            //common.setDrawableLeftEditText(R.drawable.about_pink,et_about);

            spin_code.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected(Country country) {
                    Log.d("chng", country.getPhoneCode() + "  " + country.getIso() + "   " + country.getName());
                }
            });

            common.setDrawableLeftButton(R.drawable.male_white, btn_male);
            common.setDrawableLeftButton(R.drawable.female_gray, btn_female);
        } else {
            getList();
        }
    }

    private void getList() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }
        pd = new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();
        common.makePostRequest(Utils.common_list, new HashMap<String, String>(), response -> {
            if (pd != null)
                pd.dismiss();
            //Log.d("resp","list   "+response);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                MyApplication.setSpinData(object);
                initData();
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("resp", error.getMessage() + "   ");
                if (pd != null)
                    pd.dismiss();
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
                }
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

    @Override
    public void onBackPressed() {

        switch (page_name) {
            case STEP_1:
                finish();
                break;
            case STEP_2:
                ragi_scroll.scrollTo(0, 0);
                lay_second.setVisibility(View.GONE);
                lay_first.setVisibility(View.VISIBLE);
                page_name = STEP_1;
                break;
            case STEP_3:
                ragi_scroll.scrollTo(0, 0);
                lay_third.setVisibility(View.GONE);
                lay_second.setVisibility(View.VISIBLE);
                page_name = STEP_2;
                break;
            case STEP_4:
                ragi_scroll.scrollTo(0, 0);
                lay_third.setVisibility(View.VISIBLE);
                lay_four.setVisibility(View.GONE);
                page_name = STEP_3;
                break;
            case STEP_5:
                ragi_scroll.scrollTo(0, 0);
                lay_five.setVisibility(View.GONE);
                lay_four.setVisibility(View.VISIBLE);
                page_name = STEP_4;
                break;
            case STEP_6:
                ragi_scroll.scrollTo(0, 0);
                lay_five.setVisibility(View.VISIBLE);
                lay_six.setVisibility(View.GONE);
                page_name = STEP_5;
                break;
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btn_male) {
            gender = "Male";
            btn_male.setBackgroundResource(R.drawable.round_background);
            btn_female.setBackgroundResource(R.drawable.radio_unselected);
            btn_male.setTextColor(getResources().getColor(R.color.White));
            btn_female.setTextColor(getResources().getColor(R.color.black));

            common.setDrawableLeftButton(R.drawable.male_white, btn_male);
            common.setDrawableLeftButton(R.drawable.female_gray, btn_female);
        } else if (id == R.id.btn_female) {
            gender = "Female";
            btn_female.setBackgroundResource(R.drawable.round_background);
            btn_male.setBackgroundResource(R.drawable.radio_unselected);
            btn_female.setTextColor(getResources().getColor(R.color.White));
            btn_male.setTextColor(getResources().getColor(R.color.black));

            common.setDrawableLeftButton(R.drawable.male_grey, btn_male);
            common.setDrawableLeftButton(R.drawable.female_white, btn_female);
        } else if (id == R.id.btn_first_submit) {
            validFirst();
        } else if (id == R.id.btn_second_prev) {
            ragi_scroll.scrollTo(0, 0);
            lay_second.setVisibility(View.GONE);
            lay_first.setVisibility(View.VISIBLE);
            page_name = STEP_1;
        } else if (id == R.id.btn_second_submit) {
            validSecond();
        } else if (id == R.id.btn_third_submit) {
            validThird();
        } else if (id == R.id.btn_third_prev) {
            ragi_scroll.scrollTo(0, 0);
            lay_third.setVisibility(View.GONE);
            lay_second.setVisibility(View.VISIBLE);
            page_name = STEP_2;
        } else if (id == R.id.btn_four_submit) {
            validFour();
        } else if (id == R.id.btn_four_prev) {
            ragi_scroll.scrollTo(0, 0);
            lay_third.setVisibility(View.VISIBLE);
            lay_four.setVisibility(View.GONE);
            page_name = STEP_3;
        } else if (id == R.id.btn_five_submit) {
            validFive();

        } else if (id == R.id.btn_five_prev) {
            ragi_scroll.scrollTo(0, 0);
            lay_five.setVisibility(View.GONE);
            lay_four.setVisibility(View.VISIBLE);
            page_name = STEP_4;
        } else if (id == R.id.btn_six_submit) {
            // if (image_map.containsKey("profile_photo_crop")){
            //     if (image_map.get("profile_photo_crop").equals("")){
            //          Toast.makeText(getApplicationContext(),"Please image first",Toast.LENGTH_LONG).show();
            //          return;
            //     }
            // }
            if (!isImageSelect) {
                Toast.makeText(getApplicationContext(), "Please select image first", Toast.LENGTH_LONG).show();
                return;
            }
            image_map.put("id", ragister_id);
            // new UploadFileToServer().execute();
            uploadFileToServer();
            //submitRagister(Utils.register_step,"six",image_map);
        } else if (id == R.id.btn_six_prev) {
            ragi_scroll.scrollTo(0, 0);
            lay_five.setVisibility(View.VISIBLE);
            lay_six.setVisibility(View.GONE);
            page_name = STEP_5;
        } else if (id == R.id.btn_login) {
            startActivity(new Intent(RegisterFirstActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.tv_cancel) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (id == R.id.tv_camera) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fromCamera();
        } else if (id == R.id.tv_gallary) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fromGallary();
        } else if (id == R.id.btn_choose) {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        }

    }

    private void validFive() {
        boolean isvalid = true;
        String sub_caste = et_sub_caste.getText().toString().trim();
        String gothra = et_gothra.getText().toString().trim();
        String hobby = et_hoby.getText().toString().trim();
        String about = et_about.getText().toString().trim();

//        if (TextUtils.isEmpty(sub_caste)) {
//            et_sub_caste.setError("Please enter sub-caste");
//            isvalid = false;
//        }
        //if (manglik_id.equals("0")){
        //     common.spinnerSetError(spin_manglik,"Please select manglik");
        //     isvalid=false;
        // }
        //  if (star_id.equals("0")){
        //     common.spinnerSetError(spin_star,"Please select star");
        //     isvalid=false;
        // }
        // if (horo_id.equals("0")){
        //     common.spinnerSetError(spin_horo,"Please select horoscope");
        //     isvalid=false;
        // }
        //  if (TextUtils.isEmpty(gothra)){
        //      et_gothra.setError("Please enter gothra");
        //     isvalid=false;
        // }
        //if (moon_id.equals("0")){
        //    common.spinnerSetError(spin_moon,"Please select moonsign");
        //     isvalid=false;
        // }
        if (TextUtils.isEmpty(hobby)) {
            et_hoby.setError("Please enter hobby");
            isvalid = false;
        }
        if (TextUtils.isEmpty(about)) {
            et_about.setError("Please enter about yourself");
            isvalid = false;
        }
        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("subcaste", sub_caste);
            param.put("manglik", ischeckData(manglik_id));
            param.put("star", ischeckData(star_id));
            param.put("horoscope", ischeckData(horo_id));
            param.put("gothra", ischeckData(gothra));
            param.put("moonsign", ischeckData(moon_id));
            param.put("profile_text", about);
            param.put("hobby", hobby);
            param.put("id", ragister_id);
            submitRagister(Utils.register_step, STEP_5, param);
        }
    }

    private void validFour() {
        boolean isvalid = true;
        if (hite_id == null || hite_id.equals("0")) {
            common.spinnerSetError(spin_height, "Please select height");
            isvalid = false;
        }
        if (weight_id == null || weight_id.equals("0")) {
            common.spinnerSetError(spin_weight, "Please select weight");
            isvalid = false;
        }
        if (eat_id == null || eat_id.equals("0")) {
            common.spinnerSetError(spin_eat, "Please select eating habits");
            isvalid = false;
        }
        if (smok_id == null || smok_id.equals("0")) {
            common.spinnerSetError(spin_smok, "Please select smoking");
            isvalid = false;
        }
        if (drink_id == null || drink_id.equals("0")) {
            common.spinnerSetError(spin_drink, "Please select drinking");
            isvalid = false;
        }
        if (body_id == null || body_id.equals("0")) {
            common.spinnerSetError(spin_body, "Please select body type");
            isvalid = false;
        }
        if (skin_id == null || skin_id.equals("0")) {
            common.spinnerSetError(spin_skin, "Please select skin tone");
            isvalid = false;
        }
        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("height", hite_id);
            param.put("weight", weight_id);
            param.put("diet", eat_id);
            param.put("smoke", smok_id);
            param.put("drink", drink_id);
            param.put("bodytype", body_id);
            param.put("complexion", skin_id);
            param.put("id", ragister_id);
            submitRagister(Utils.register_step, STEP_4, param);
        }
    }

    private void validThird() {
        boolean isvalid = true;
        if (edu_id == null || edu_id.equals("0") || edu_id.equals("")) {
            common.spinnerSetError(spin_edu, "Please select education");
            isvalid = false;
        }
        if (emp_id == null || emp_id.equals("0") || emp_id.equals("")) {
            common.spinnerSetError(spin_emp_in, "Please select employment");
            isvalid = false;
        }
        if (income_id == null || income_id.equals("0") || income_id.equals("")) {
            common.spinnerSetError(spin_income, "Please select annual income");
            isvalid = false;
        }
        if (occu_id == null || occu_id.equals("0") || occu_id.equals("")) {
            common.spinnerSetError(spin_occupation, "Please select occupation");
            isvalid = false;
        }
        if (desig_id == null || desig_id.equals("0") || desig_id.equals("")) {
            common.spinnerSetError(spin_designation, "Please select designation");
            isvalid = false;
        }

        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("education_detail", edu_id);
            param.put("employee_in", emp_id);
            param.put("income", income_id);
            param.put("occupation", occu_id);
            param.put("designation", desig_id);
            param.put("id", ragister_id);
            submitRagister(Utils.register_step, STEP_3, param);
        }

    }

    private void validSecond() {
        boolean isvalid = true;
        if (country_id == null || country_id.equals("0")) {
            common.spinnerSetError(spin_country, "Please select country");
            isvalid = false;
        }
        if (state_id == null || state_id.equals("0")) {
            common.spinnerSetError(spin_state, "Please select state");
            isvalid = false;
        }
        if (city_id == null || city_id.equals("0")) {
            common.spinnerSetError(spin_city, "Please select city");
            isvalid = false;
        }
        if (mari_id == null || mari_id.equals("0")) {
            common.spinnerSetError(spin_mari, "Please select marital status");
            isvalid = false;
        } else {
            if (mari_id != null && !mari_id.equals("Unmarried")) {
                if (total_child_id == null || total_child_id.equals("total")) {
                    common.spinnerSetError(spin_t_child, "Please select total children");
                    isvalid = false;
                } else {
                    if (!total_child_id.equals("0")) {
                        if (status_child_id == null || status_child_id.equals("0")) {
                            common.spinnerSetError(spin_child_status, "Please select children status");
                            isvalid = false;
                        }
                    }
                }
            }
        }

        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("country_id", country_id);
            param.put("state_id", state_id);
            param.put("city", city_id);
            param.put("marital_status", mari_id);
            param.put("total_children", checkValue(total_child_id));
            param.put("status_children", checkValue(status_child_id));
            param.put("id", ragister_id);
            submitRagister(Utils.register_step, STEP_2, param);
        }
    }

    private void validFirst() {
        String fname = et_f_name.getText().toString().trim();
        String lname = et_l_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String dob = et_dob.getText().toString().trim();
        String mobile = et_mobile.getText().toString().trim();
        country_code = spin_code.getSelectedCountryCodeWithPlus();

        boolean isvalid = true;
        if (TextUtils.isEmpty(fname)) {
            et_f_name.setError("Please enter first name");
            isvalid = false;
        }
        if (TextUtils.isEmpty(lname)) {
            et_l_name.setError("Please enter last name");
            isvalid = false;
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Please enter email");
            isvalid = false;
        } else {
            if (!common.isValidEmail(email)) {
                et_email.setError("Please enter valid email");
                isvalid = false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please enter password");
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        } else
            pass_input.setPasswordVisibilityToggleEnabled(true);

        if (password.length() < 6) {
            et_password.setError("Please enter atleast 6 characters");
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        } else
            pass_input.setPasswordVisibilityToggleEnabled(true);

        if (TextUtils.isEmpty(mobile)) {
            et_mobile.setError("Please enter mobile number");
            isvalid = false;
        } else {
            if (mobile.length() < 10) {
                et_mobile.setError("Please enter valid mobile number");
                isvalid = false;
            }
        }

        if (TextUtils.isEmpty(dob)) {
            et_dob.setError("Please enter date of birth");
            isvalid = false;
        }
        if (religion_id == null || religion_id.equals("0")) {
            common.spinnerSetError(spin_religion, "Please select religion");
            isvalid = false;
        }
        if (caste_id == null || caste_id.equals("0")) {
            common.spinnerSetError(spin_caste, "Please select caste");
            isvalid = false;
        }
        if (tongue_id == null || tongue_id.equals("0")) {
            common.spinnerSetError(spin_tongue, "Please select mother tongue");
            isvalid = false;
        }

        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("firstname", fname);
            param.put("lastname", lname);
            param.put("email", email);
            param.put("password", password);
            param.put("country_code", country_code);
            param.put("mobile_number", mobile);
            param.put("birthdate", changeDate(dob));
            param.put("religion", ischeckData(religion_id));
            param.put("caste", ischeckData(caste_id));
            param.put("mother_tongue", ischeckData(tongue_id));
            param.put("gender", gender);
            param.put("id", ragister_id);
            param.put("fb_id", fb_id);
            param.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));
            //Log.d("resp",param.toString());
            submitRagister(Utils.register_first, STEP_1, param);
        }

    }

    private String ischeckData(String val) {
        if (val == null || val.equals("0")) {
            return "";
        }
        return val;
    }

    public String changeDate(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "yyyy-M-dd";
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

    private String checkValue(String val) {
        if (val == null || val.equals("0") || val.equals("total")) {
            return "";
        } else
            return val;
    }

    private void uploadFileToServer() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, getString(R.string.err_msg_no_intenet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        // setting progress bar to zero
        pd = new ProgressDialog(RegisterFirstActivity.this);
        pd.setTitle("Uploading...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setCancelable(false);
        pd.show();

        //File org = new File(org_path);
        File profileOriginalImageCompressedFile = Common.getCompressedImageFile(this, new File(Common.getPath(this, resultUri)));
        ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(org_path), this);
        MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData("profil_photo_org", profileOriginalImageCompressedFile.getName(), originalFileBody);

        // MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData("profil_photo", crop.getName(), RequestBody.create(MediaType.parse("image/*"), crop));
        //  MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData("profil_photo_org", org.getName(), RequestBody.create(MediaType.parse("image/*"), org));

        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), ragister_id);
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Call<JsonObject> call = null;
        if (page_name.equalsIgnoreCase(STEP_6)) {
            File crop = new File(crop_path);
            ProgressRequestBody cropFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(crop_path), this);
            MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData("profil_photo", crop.getName(), cropFileBody);

            Map<String, RequestBody> params = new HashMap<>();
            params.put("id", partParam1);
            params.put("user_agent", partParam2);
            params.put("csrf_new_matrimonial", partParam3);

            call = appApiService.uploadPhoto(cropFilePart, originalFilePart, params);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                JsonObject data = response.body();
                AppDebugLog.print("response in submitData : " + response.body());

                if (data != null) {
                    common.showToast(data.get("errmessage").getAsString());
                    if (data.get("status").getAsString().equals("success")) {
                        isImageSelect = false;
                        if (page_name.equalsIgnoreCase(STEP_6)) {
                            Intent i = new Intent(getApplicationContext(), SuccessActivity.class);
                            i.putExtra("ragistered_id", ragister_id);
                            startActivity(i);
                        }
                    }
                } else {
                    Toast.makeText(RegisterFirstActivity.this, getString(R.string.err_msg_try_again_later), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegisterFirstActivity.this, getString(R.string.err_msg_something_went_wrong), Toast.LENGTH_LONG).show();
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    // url = file path or whatever suitable URL you want.
    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onProgressUpdate(int percentage) {
        // set current progress
        if (pd != null && pd.isShowing()) {
            pd.setProgress(percentage);
        }

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        //set finish progress
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    private void submitRagister(String url, final String tag, HashMap<String, String> param) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        Common.hideSoftKeyboard(this);

        pd = new ProgressDialog(RegisterFirstActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        common.makePostRequest(url, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), object.getString("errmessage"), Toast.LENGTH_SHORT).show();
                    if (object.getString("status").equals("success")) {
                        switch (tag) {
                            case STEP_1:
                                ragister_id = object.getString("id");
                                ragi_scroll.scrollTo(0, 0);
                                lay_first.setVisibility(View.GONE);
                                lay_second.setVisibility(View.VISIBLE);
                                page_name = STEP_2;
                                break;
                            case STEP_2:
                                ragi_scroll.scrollTo(0, 0);
                                lay_third.setVisibility(View.VISIBLE);
                                lay_second.setVisibility(View.GONE);
                                page_name = STEP_3;
                                break;
                            case STEP_3:
                                ragi_scroll.scrollTo(0, 0);
                                lay_four.setVisibility(View.VISIBLE);
                                lay_third.setVisibility(View.GONE);
                                page_name = STEP_4;
                                break;
                            case STEP_4:
                                ragi_scroll.scrollTo(0, 0);
                                lay_four.setVisibility(View.GONE);
                                lay_five.setVisibility(View.VISIBLE);
                                page_name = STEP_5;
                                break;
                            case STEP_5:
                                ragi_scroll.scrollTo(0, 0);
                                lay_six.setVisibility(View.VISIBLE);
                                lay_five.setVisibility(View.GONE);
                                page_name = STEP_6;
                                break;
                            case STEP_6:
                                Intent i = new Intent(getApplicationContext(), SuccessActivity.class);
                                i.putExtra("ragistered_id", ragister_id);
                                startActivity(i);
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
                if (pd != null)
                    pd.dismiss();
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Spinner spinner = (Spinner) adapterView;
        switch (spinner.getId()) {
            case R.id.spin_religion:
                religion_id = reli_map.get(spin_religion.getSelectedItem().toString());
                resetCaste();
                if (religion_id != null && !religion_id.equals("0")) {
                    getDepedentList("caste_list", religion_id);
                }
                break;
            case R.id.spin_caste:
                caste_id = caste_map.get(spin_caste.getSelectedItem().toString());
                break;
            case R.id.spin_tongue:
                tongue_id = tongue_map.get(spin_tongue.getSelectedItem().toString());
                break;
            case R.id.spin_country:
                country_id = country_map.get(spin_country.getSelectedItem().toString());
                resetStateAndCity();
                if (country_id != null && !country_id.equals("0")) {
                    getDepedentList("state_list", country_id);
                }
                break;
            case R.id.spin_state:
                state_id = state_map.get(spin_state.getSelectedItem().toString());
                resetCity();
                if (state_id != null && !state_id.equals("0")) {
                    getDepedentList("city_list", state_id);
                }
                break;
            case R.id.spin_city:
                city_id = city_map.get(spin_city.getSelectedItem().toString());
                break;
            case R.id.spin_mari:
                mari_id = mari_map.get(spin_mari.getSelectedItem().toString());
                if (mari_id == null || mari_id.equals("Unmarried") || mari_id.equals("0")) {
                    //lay_t_child,lay_status_child
                    spin_t_child.setEnabled(false);
                    spin_t_child.setSelection(0);
                    spin_child_status.setEnabled(false);
                    spin_child_status.setSelection(0);
                    lay_t_child.setVisibility(View.GONE);
                    lay_status_child.setVisibility(View.GONE);
                } else {
                    spin_t_child.setEnabled(true);
                    spin_child_status.setEnabled(true);
                    lay_t_child.setVisibility(View.VISIBLE);
                    lay_status_child.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.spin_t_child:
                total_child_id = total_child_map.get(spin_t_child.getSelectedItem().toString());
                if (total_child_id == null || total_child_id.equals("0")) {
                    status_child_id = "0";
                    spin_child_status.setEnabled(false);
                    spin_child_status.setSelection(0);
                    lay_status_child.setVisibility(View.GONE);
                } else {
                    spin_child_status.setEnabled(true);
                    lay_status_child.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.spin_child_status:
                status_child_id = status_child_map.get(spin_child_status.getSelectedItem().toString());
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
            case R.id.spin_eat:
                eat_id = eat_map.get(spin_eat.getSelectedItem().toString());
                break;
            case R.id.spin_smok:
                smok_id = smok_map.get(spin_smok.getSelectedItem().toString());
                break;
            case R.id.spin_drink:
                drink_id = drink_map.get(spin_drink.getSelectedItem().toString());
                break;
            case R.id.spin_body:
                body_id = body_map.get(spin_body.getSelectedItem().toString());
                break;
            case R.id.spin_skin:
                skin_id = skin_map.get(spin_skin.getSelectedItem().toString());
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
        }
    }

    private void getDepedentList(final String tag, String id) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            Toast.makeText(this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        pd = new ProgressDialog(RegisterFirstActivity.this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "");
        param.put("retun_for", "");

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
                                List<String> caste = common.getListFromArray(object.getJSONArray("data"), "Caste*");
                                caste_map = common.getMapFromArray(object.getJSONArray("data"), "Caste*");
                                ArrayAdapter adp_caste = new ArrayAdapter<String>(RegisterFirstActivity.this, R.layout.spinner_item, caste);
                                spin_caste.setAdapter(adp_caste);
                                break;
                            case "state_list":
                                List<String> state = common.getListFromArray(object.getJSONArray("data"), "State*");
                                state_map = common.getMapFromArray(object.getJSONArray("data"), "State*");
                                ArrayAdapter adp_stat = new ArrayAdapter<String>(RegisterFirstActivity.this, R.layout.spinner_item, state);
                                spin_state.setAdapter(adp_stat);
                                break;
                            case "city_list":
                                List<String> city = common.getListFromArray(object.getJSONArray("data"), "City*");
                                city_map = common.getMapFromArray(object.getJSONArray("data"), "City*");
                                ArrayAdapter adp_city = new ArrayAdapter<String>(RegisterFirstActivity.this, R.layout.spinner_item, city);
                                spin_city.setAdapter(adp_city);
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
                if (pd != null)
                    pd.dismiss();
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode));
                }
            }
        });

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void resetStateAndCity() {
        List<String> state = new ArrayList<>();
        state.add("State*");
        state_map = new HashMap<>();
        state_map.put("State*", "0");
        spin_state.setSelection(0);
        ArrayAdapter adp_stat = new ArrayAdapter<String>(RegisterFirstActivity.this, R.layout.spinner_item, state);
        spin_state.setAdapter(adp_stat);
        state_id = "";

        resetCity();
    }

    private void resetCity() {
        List<String> city = new ArrayList<>();
        city.add("City*");
        city_map = new HashMap<>();
        city_map.put("City*", "0");
        ArrayAdapter adp_city = new ArrayAdapter<String>(RegisterFirstActivity.this, R.layout.spinner_item, city);
        spin_city.setAdapter(adp_city);
        spin_city.setSelection(0);
        city_id = "";
    }

    private void resetCaste() {
        List<String> caste = new ArrayList<>();
        caste.add("Caste*");
        caste_map = new HashMap<>();
        caste_map.put("Caste*", "0");
        ArrayAdapter adp_caste = new ArrayAdapter<String>(RegisterFirstActivity.this, R.layout.spinner_item, caste);
        spin_caste.setAdapter(adp_caste);
        spin_caste.setSelection(0);
        caste_id = "";
    }
}
