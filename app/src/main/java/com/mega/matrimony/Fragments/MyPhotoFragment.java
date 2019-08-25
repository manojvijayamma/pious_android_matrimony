package com.mega.matrimony.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MyPhotoFragment extends Fragment implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    //TextView lbl_photo_visi;
    Common common;
    SessionManager session;
    Context context;
    ProgressDialog pd;
    TextView tv_cancel, tv_gallary, tv_camera;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    //RadioGroup grp_visi;
    String mCurrentPhotoPath, finalpath_org, finalpath_crop;
    int image_id;
    Uri resultUri;
    long totalSize = 0;
    boolean isfirst = true;
    private ImageView img_one, img_two, img_three, img_four, img_five, img_six,
            img_plus_one, img_plus_two, img_plus_three, img_plus_four, img_plus_five, img_plus_six,
            img_edit_one, img_edit_two, img_edit_three, img_edit_four, img_edit_five, img_edit_six, img_cover, img_edit_cover, img_plus_cover;
    private int placeHolder,photoProtectPlaceHolder;

    public MyPhotoFragment() {
        // Required empty public constructor
    }

    public static MyPhotoFragment newInstance(String param1, String param2) {
        MyPhotoFragment fragment = new MyPhotoFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_photo, container, false);

        context = getActivity();
        session = new SessionManager(context);
        common = new Common(getActivity());

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.male;
        }


        img_cover = (ImageView) view.findViewById(R.id.img_cover);
        img_edit_cover = (ImageView) view.findViewById(R.id.img_edit_cover);
        img_edit_cover.setOnClickListener(this);
        img_plus_cover = (ImageView) view.findViewById(R.id.img_plus_cover);
        img_plus_cover.setOnClickListener(this);

        img_one = (ImageView) view.findViewById(R.id.img_one);
        img_two = (ImageView) view.findViewById(R.id.img_two);
        img_three = (ImageView) view.findViewById(R.id.img_three);
        img_four = (ImageView) view.findViewById(R.id.img_four);
        img_five = (ImageView) view.findViewById(R.id.img_five);
        img_six = (ImageView) view.findViewById(R.id.img_six);

        img_plus_one = (ImageView) view.findViewById(R.id.img_plus_one);
        img_plus_one.setOnClickListener(this);
        img_plus_two = (ImageView) view.findViewById(R.id.img_plus_two);
        img_plus_two.setOnClickListener(this);
        img_plus_three = (ImageView) view.findViewById(R.id.img_plus_three);
        img_plus_three.setOnClickListener(this);
        img_plus_four = (ImageView) view.findViewById(R.id.img_plus_four);
        img_plus_four.setOnClickListener(this);
        img_plus_five = (ImageView) view.findViewById(R.id.img_plus_five);
        img_plus_five.setOnClickListener(this);
        img_plus_six = (ImageView) view.findViewById(R.id.img_plus_six);
        img_plus_six.setOnClickListener(this);

        img_edit_one = (ImageView) view.findViewById(R.id.img_edit_one);
        img_edit_one.setOnClickListener(this);
        img_edit_two = (ImageView) view.findViewById(R.id.img_edit_two);
        img_edit_two.setOnClickListener(this);
        img_edit_three = (ImageView) view.findViewById(R.id.img_edit_three);
        img_edit_three.setOnClickListener(this);
        img_edit_four = (ImageView) view.findViewById(R.id.img_edit_four);
        img_edit_four.setOnClickListener(this);
        img_edit_five = (ImageView) view.findViewById(R.id.img_edit_five);
        img_edit_five.setOnClickListener(this);
        img_edit_six = (ImageView) view.findViewById(R.id.img_edit_six);
        img_edit_six.setOnClickListener(this);

        layoutBottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_gallary = (TextView) view.findViewById(R.id.tv_gallary);
        tv_camera = (TextView) view.findViewById(R.id.tv_camera);
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

       /* grp_visi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!isfirst){
                    RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(i);
                    String val="";
                    if (checkedRadioButton.getText().toString().equals("Hide for All")){
                        val="0";
                    }else if (checkedRadioButton.getText().toString().equals("Visible to only paid members")){
                        val="2";
                    }else if (checkedRadioButton.getText().toString().equals("Visible to All") ){
                        val="1";
                    }
                    changeVisiApi(val);
                }

            }
        });*/

        getMyProfile();

        return view;
    }

    private void getMyProfile() {
        pd = new ProgressDialog(context);
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
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                    if (object.getString("status").equals("success")) {
                        JSONObject data = object.getJSONObject("data");

                       /* String photo_view_status=data.getString("photo_view_status");
                        if (photo_view_status.equals("0")){
                            ((RadioButton)grp_visi.getChildAt(0)).setChecked(true);
                        }else if (photo_view_status.equals("1")){
                            ((RadioButton)grp_visi.getChildAt(2)).setChecked(true);
                        }else if (photo_view_status.equals("2")){
                            ((RadioButton)grp_visi.getChildAt(1)).setChecked(true);
                        }*/

                        String photo1 = data.getString("photo1");
                        String photo2 = data.getString("photo2");
                        String photo3 = data.getString("photo3");
                        String photo4 = data.getString("photo4");
                        String photo5 = data.getString("photo5");
                        String photo6 = data.getString("photo6");
                        String cover_photo = data.getString("cover_photo");

                        if (isValidImage(cover_photo)) {
                            Picasso.get().load(cover_photo).placeholder(R.drawable.ic_cover_place_holder).into(img_cover);
                            img_plus_cover.setVisibility(View.GONE);
                            img_edit_cover.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_cover.setVisibility(View.VISIBLE);
                            img_edit_cover.setVisibility(View.GONE);
                            img_cover.setImageResource(R.drawable.ic_cover_place_holder);
                        }

                        if (isValidImage(photo1)) {
                            Picasso.get().load(photo1).placeholder(placeHolder).error(placeHolder).into(img_one);
                            img_plus_one.setVisibility(View.GONE);
                            img_edit_one.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_one.setVisibility(View.VISIBLE);
                            img_edit_one.setVisibility(View.GONE);
                            img_one.setImageResource(placeHolder);
                        }
                        if (isValidImage(photo2)) {
                            Picasso.get().load(photo2).placeholder(placeHolder).error(placeHolder).into(img_two);
                            img_plus_two.setVisibility(View.GONE);
                            img_edit_two.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_two.setVisibility(View.VISIBLE);
                            img_edit_two.setVisibility(View.GONE);
                            img_two.setImageResource(placeHolder);
                        }
                        if (isValidImage(photo3)) {
                            Picasso.get().load(photo3).placeholder(placeHolder).error(placeHolder).into(img_three);
                            img_plus_three.setVisibility(View.GONE);
                            img_edit_three.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_three.setVisibility(View.VISIBLE);
                            img_edit_three.setVisibility(View.GONE);
                            img_three.setImageResource(placeHolder);
                        }
                        if (isValidImage(photo4)) {
                            Picasso.get().load(photo4).placeholder(placeHolder).error(placeHolder).into(img_four);
                            img_plus_four.setVisibility(View.GONE);
                            img_edit_four.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_four.setVisibility(View.VISIBLE);
                            img_edit_four.setVisibility(View.GONE);
                            img_four.setImageResource(placeHolder);
                        }
                        if (isValidImage(photo5)) {
                            Picasso.get().load(photo5).placeholder(placeHolder).error(placeHolder).into(img_five);
                            img_plus_five.setVisibility(View.GONE);
                            img_edit_five.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_five.setVisibility(View.VISIBLE);
                            img_edit_five.setVisibility(View.GONE);
                            img_five.setImageResource(placeHolder);
                        }
                        if (isValidImage(photo6)) {
                            Picasso.get().load(photo6).placeholder(placeHolder).error(placeHolder).into(img_six);
                            img_plus_six.setVisibility(View.GONE);
                            img_edit_six.setVisibility(View.VISIBLE);
                        } else {
                            img_plus_six.setVisibility(View.VISIBLE);
                            img_edit_six.setVisibility(View.GONE);
                            img_six.setImageResource(placeHolder);
                        }

                    }
                    isfirst = false;

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

    private boolean isValidImage(String url) {
        return !url.equals("") && !url.equals("null");
    }

    private void editClick(final int img_id, View v) {
        image_id = img_id;
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.photo_edit_menu);
        if (img_id == 1 || img_id == 0)
            popup.getMenu().getItem(2).setVisible(false);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        return true;
                    case R.id.delete:
                        deletePhotoAlert();
                        return true;
                    case R.id.profile:
                        setProfilePhotoApi();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void deletePhotoAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure you want to delete photo?");
        alert.setNegativeButton("No", null);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (image_id != 0) {
                    deletePhotoApi();
                } else {
                    deleteCoverPhotoApi();
                }

            }
        });
        alert.show();
    }

    private void deleteCoverPhotoApi() {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("delete_cover_photo", "delete");

        common.makePostRequest(Utils.delete_cover_photo, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        getMyProfile();
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

    private void deletePhotoApi() {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("photo_number", String.valueOf(image_id));
        param.put("delete_photo", "delete");

        common.makePostRequest(Utils.delete_photo, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        getMyProfile();
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

    private void setProfilePhotoApi() {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("photo_number", String.valueOf(image_id));
        param.put("set_profile", "set_profile");

        common.makePostRequest(Utils.set_profile_pic, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        getMyProfile();
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

    private void changeVisiApi(String val) {
        pd = new ProgressDialog(context);
        pd.setIndeterminate(true);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        param.put("photo_view_status", val);
        param.put("action", "photo_view_status");

        common.makePostRequest(Utils.photo_visibility_status, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"));
                    if (object.getString("status").equals("success")) {
                        getMyProfile();
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
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context, getActivity().getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("TAG", "requestCode  " + requestCode+" resultCode  "+resultCode);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {
                resultUri = Uri.fromFile(new File(mCurrentPhotoPath));
                finalpath_org = resultUri.getPath();
                Log.d("TAG", "resultUri  " + finalpath_org);

                if (image_id != 0)
                    cropImage(resultUri);
                else {
                    //new UploadFileToServer().execute();
                    uploadFileToServer();
                }

            } else if (requestCode == 2) {
                resultUri = data.getData();
                finalpath_org = getRealPathFromURI(resultUri);
                Log.d("TAG", "resultUri  " + finalpath_org);

                if (image_id != 0)
                    cropImage(resultUri);
                else {
                    //  new UploadFileToServer().execute();
                    uploadFileToServer();
                }
            } else if (requestCode == 3) {

                resultUri = UCrop.getOutput(data);
                finalpath_crop = resultUri.getPath();
                // Log.d("TAG", "  getPath "+resultUri.getPath());


                // new UploadFileToServer().execute();
                uploadFileToServer();
                //img_profile.setImageBitmap(bitmap);
            }
        } else {

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFileToServer() {
        if (!ConnectionDetector.isConnectingToInternet(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.err_msg_no_intenet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        // setting progress bar to zero
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setCancelable(false);
        pd.show();

        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.KEY_USER_ID));
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        Map<String, RequestBody> params = new HashMap<>();
        params.put("member_id", partParam1);
        params.put("user_agent", partParam2);
        params.put("csrf_new_matrimonial", partParam3);

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Call<JsonObject> call = null;
        if (image_id != 0) {
            String org_param = "profile_photo" + image_id + "_org";
            String crop_param = "profile_photo" + image_id + "_crop";
            AppDebugLog.print("org_param: " + org_param + "  crop_param   " + crop_param);

            File sourceFile_crop = new File(finalpath_crop);
            ProgressRequestBody cropFileBody = new ProgressRequestBody(sourceFile_crop, getMimeType(finalpath_crop), this);
            MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData(crop_param, sourceFile_crop.getName(), cropFileBody);

            //File org = new File(finalpath_org);
            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(getActivity(), new File(Common.getPath(getActivity(), resultUri)));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(finalpath_org), this);
            MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData(org_param, profileOriginalImageCompressedFile.getName(), originalFileBody);

            call = appApiService.uploadMyPhotoWithCrop(cropFilePart, originalFilePart, params);
        } else {
            //File org = new File(finalpath_org);
            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(getActivity(), new File(Common.getPath(getActivity(), resultUri)));
            ProgressRequestBody orgFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(finalpath_org), this);
            MultipartBody.Part orgFilePart = MultipartBody.Part.createFormData("cover_photo", profileOriginalImageCompressedFile.getName(), orgFileBody);

            call = appApiService.uploadMyPhotoWithoutCrop(orgFilePart, params);
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
                        getMyProfile();
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.err_msg_try_again_later), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.err_msg_something_went_wrong), Toast.LENGTH_LONG).show();
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

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void cropImage(Uri imagePath) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageExtension = "." + MimeTypeMap.getFileExtensionFromUrl(finalpath_org);

        UCrop uCrop = UCrop.of(imagePath, Uri.fromFile(new File(context.getCacheDir(), timeStamp + imageExtension)));
        uCrop.withAspectRatio(1, 1);
        // uCrop.withMaxResultSize(250,250);
        UCrop.Options options = new UCrop.Options();

        options.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white));
        options.setRootViewBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

        uCrop.withOptions(options);
        uCrop.start(context, this, 3);

    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.img_plus_one:
                image_id = 1;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_two:
                image_id = 2;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_three:
                image_id = 3;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_four:
                image_id = 4;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_five:
                image_id = 5;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_six:
                image_id = 6;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_cover:
                image_id = 0;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_edit_cover:
                editClick(0, view);
                break;
            case R.id.img_edit_one:
                editClick(1, view);
                break;
            case R.id.img_edit_two:
                editClick(2, view);
                break;
            case R.id.img_edit_three:
                editClick(3, view);
                break;
            case R.id.img_edit_four:
                editClick(4, view);
                break;
            case R.id.img_edit_five:
                editClick(5, view);
                break;
            case R.id.img_edit_six:
                editClick(6, view);
                break;

        }

    }

}
