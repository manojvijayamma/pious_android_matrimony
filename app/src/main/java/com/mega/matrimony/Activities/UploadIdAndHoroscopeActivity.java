package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.mega.matrimony.Network.ConnectionDetector;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.AppDebugLog;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.mega.matrimony.retrofit.AppApiService;
import com.mega.matrimony.retrofit.ProgressRequestBody;
import com.mega.matrimony.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class UploadIdAndHoroscopeActivity extends AppCompatActivity implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {

    TextView tv_approval, tv_delete_id, tv_cancel, tv_gallary, tv_camera;
    Button btn_id, btn_horo;
    LinearLayout lay_id, lay_horo;
    ImageView img_horo, img_proof, img_edit_id, img_edit_horo;
    Common common;
    SessionManager session;
    ProgressDialog pd;
    String mCurrentPhotoPath, finalpath = "";
    String page_name = "";
    Uri resultUri;
    boolean isimageSelect = false;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    long totalSize = 0;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_id_and_horoscope);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload");

        session = new SessionManager(this);
        common = new Common(this);


        img_horo = (ImageView) findViewById(R.id.img_horo);
        img_proof = (ImageView) findViewById(R.id.img_proof);
        img_edit_id = (ImageView) findViewById(R.id.img_edit_id);
        img_edit_id.setOnClickListener(this);
        img_edit_horo = (ImageView) findViewById(R.id.img_edit_horo);
        img_edit_horo.setOnClickListener(this);
        tv_approval = (TextView) findViewById(R.id.tv_approval);
        tv_delete_id = (TextView) findViewById(R.id.tv_delete_id);
        tv_delete_id.setOnClickListener(this);
        btn_id = (Button) findViewById(R.id.btn_id);
        btn_id.setOnClickListener(this);
        btn_horo = (Button) findViewById(R.id.btn_horo);
        btn_horo.setOnClickListener(this);
        lay_id = (LinearLayout) findViewById(R.id.lay_id);
        lay_horo = (LinearLayout) findViewById(R.id.lay_horo);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey("page_tag")) {
                page_name = b.getString("page_tag");
                Log.d("TAG", "page_name  " + page_name);
                if (page_name.equals("id")) {
                    getSupportActionBar().setTitle("Upload ID");
                    lay_id.setVisibility(View.VISIBLE);
                    lay_horo.setVisibility(View.GONE);
                    url = Utils.upload_id_proof_photo;
                } else if (page_name.equals("horoscope")) {
                    getSupportActionBar().setTitle("Upload Horoscope");
                    lay_id.setVisibility(View.GONE);
                    lay_horo.setVisibility(View.VISIBLE);
                    url = Utils.upload_horoscope;
                }
            }
        }

        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_gallary = (TextView) findViewById(R.id.tv_gallary);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_gallary.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        // btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        // btnBottomSheet.setText("Expand Sheet");
                    }
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

        getMyprofile();

    }

    private void getMyprofile() {
        pd = new ProgressDialog(UploadIdAndHoroscopeActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        common.makePostRequest(Utils.get_my_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("myprof", response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject data = object.getJSONObject("data");

                    if (page_name.equals("id")) {
                        if (!data.getString("id_proof").equals("") && !data.getString("id_proof").equals("null")) {
                            Picasso.get().load(data.getString("id_proof")).placeholder(R.drawable.ic_id_proof_place_holder).error(R.drawable.ic_id_proof_place_holder).into(img_proof);
                            tv_delete_id.setVisibility(View.VISIBLE);
                        } else {
                            tv_delete_id.setVisibility(View.GONE);
                            img_proof.setImageResource(R.drawable.ic_id_proof_place_holder);
                        }
                    } else if (page_name.equals("horoscope")) {
                        if (!data.getString("horoscope_photo").equals("") && !data.getString("horoscope_photo").equals("null")) {
                            Picasso.get().load(data.getString("horoscope_photo")).placeholder(R.drawable.ic_horoscope_place_holder).error(R.drawable.ic_horoscope_place_holder).into(img_horo);
                        }else{
                            img_horo.setImageResource(R.drawable.ic_horoscope_place_holder);
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

    private void fromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "requestCode  " + requestCode + " resultCode  " + resultCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                resultUri = Uri.fromFile(new File(mCurrentPhotoPath));
                finalpath = resultUri.getPath();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isimageSelect = true;
                if (page_name.equals("id")) {
                    // btn_id.setText("Upload Id Proof");
                    img_proof.setImageBitmap(bitmap);
                } else if (page_name.equals("horoscope")) {
                    // btn_horo.setText("Upload Horoscope");
                    img_horo.setImageBitmap(bitmap);
                }

            } else if (requestCode == 2) {
                resultUri = data.getData();
                finalpath = getRealPathFromURI(resultUri);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isimageSelect = true;
                if (page_name.equals("id")) {
                    //btn_id.setText("Upload Id Proof");
                    img_proof.setImageBitmap(bitmap);
                } else if (page_name.equals("horoscope")) {
                    // btn_horo.setText("Upload Horoscope");
                    img_horo.setImageBitmap(bitmap);
                }
            }
        } else {
            isimageSelect = false;
            if (page_name.equals("id")) {
                //btn_id.setText("Select Id Proof");
                finalpath = "";
            } else if (page_name.equals("horoscope")) {
                //btn_horo.setText("Select Horoscope");
                finalpath = "";
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
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
        pd = new ProgressDialog(UploadIdAndHoroscopeActivity.this);
        pd.setTitle("Uploading...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setCancelable(false);
        pd.show();

        AppDebugLog.print("File path : " + finalpath);
        //File org = new File(finalpath);
        File profileOriginalImageCompressedFile = Common.getCompressedImageFile(this, new File(Common.getPath(this, resultUri)));
       // AppDebugLog.print("finalpath Mime Type : : " + getMimeType(finalpath));
       // AppDebugLog.print("profileOriginalImageCompressedFile Mime Type : : " + getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()));
        ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(finalpath), this);
        MultipartBody.Part originalFilePart = null;

        if (page_name.equals("id")) {
            originalFilePart = MultipartBody.Part.createFormData("id_proof", profileOriginalImageCompressedFile.getName(), originalFileBody);
        } else if (page_name.equals("horoscope")) {
            originalFilePart = MultipartBody.Part.createFormData("horoscope_photo", profileOriginalImageCompressedFile.getName(), originalFileBody);
        }

        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.KEY_USER_ID));
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Map<String, RequestBody> params = new HashMap<>();
        params.put("member_id", partParam1);
        params.put("user_agent", partParam2);
        params.put("csrf_new_matrimonial", partParam3);
        AppDebugLog.print("member_id:"+session.getLoginData(SessionManager.KEY_USER_ID) + " csrf_new_matrimonial:" + session.getLoginData(SessionManager.TOKEN));

        Call<JsonObject> call = null;
        if (page_name.equals("id")) {
            call = appApiService.uploadIdProof(originalFilePart, params);
        } else {
            call = appApiService.uploadHoroscopePhoto(originalFilePart, params);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                AppDebugLog.print("response in submitData : " + response);


                JsonObject data = response.body();
                AppDebugLog.print("response in submitData : " + response.body());

                if (data != null) {
                    common.showToast(data.get("errmessage").getAsString());
                } else {
                    Toast.makeText(UploadIdAndHoroscopeActivity.this, getString(R.string.err_msg_try_again_later), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UploadIdAndHoroscopeActivity.this, getString(R.string.err_msg_try_again_later), Toast.LENGTH_LONG).show();
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
        AppDebugLog.print("File Mime Type : " + type);
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
            pd.setProgress(100);
            pd.dismiss();
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
            case R.id.tv_delete_id:
                deleteAlert();
                break;
            case R.id.img_edit_id:
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.img_edit_horo:
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.btn_id:
                if (finalpath.equals("")) {
                    common.showToast("Please Select Id proof First ");
                } else {
                    uploadFileToServer();
                }
                /*if (btn_id.getText().toString().equals("Upload Id Proof")){
                  // uploadPhoto(Utils.upload_id_proof_photo);

                }*/
                break;
            case R.id.btn_horo:
                if (finalpath.equals("")) {
                    common.showToast("Please Select horoscope First ");
                } else {
                    uploadFileToServer();
                }
                /*if (btn_horo.getText().toString().equals("Upload Horoscope")){
                  //  uploadPhoto(Utils.upload_horoscope);

                }else if (btn_horo.getText().toString().equals("Select Horoscope")){
                    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }*/
                break;
            case R.id.tv_cancel:
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.tv_camera:
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                fromCamera();
                break;
            case R.id.tv_gallary:
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                fromGallary();
                break;
        }
    }

    private void deleteAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to delete id proof?");
        alert.setNegativeButton("No", null);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteId_api();
            }
        });
        alert.show();
    }

    private void deleteId_api() {
        pd = new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("delete_id_proof_photo", "delete");

        common.makePostRequest(Utils.delete_id_proof_photo, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();

                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        getMyprofile();
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
}
