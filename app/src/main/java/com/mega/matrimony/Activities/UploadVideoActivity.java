package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadVideoActivity extends AppCompatActivity {//implements YouTubePlayer.OnInitializedListener

    private LinearLayout lay_link;
    private Button btn_embd,btn_upload;
   // private WebView web_video;
   private YouTubePlayerView youTubeView;
    private static final int RECOVERY_REQUEST = 1;
    private EditText et_url;
    Common common;
    SessionManager session;
    ProgressDialog pd;
    private Toolbar toolbar;
    String vid_url="";
    final String youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
     static String[] videoIdRegex = { "\\?vi?=([^&]*)","watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

         toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Video");

        common=new Common(this);
        session=new SessionManager(this);

        et_url=(EditText) findViewById(R.id.et_url);
        btn_upload=(Button)findViewById(R.id.btn_upload);
        lay_link=(LinearLayout)findViewById(R.id.lay_link);
        btn_embd=(Button)findViewById(R.id.btn_embd);

        btn_embd.setOnClickListener(view -> lay_link.setVisibility(lay_link.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));

        btn_upload.setOnClickListener(view -> {
            String url=et_url.getText().toString().trim();
            if (TextUtils.isEmpty(url)){
                et_url.setError("Please enter/paste youtube link here");
                return;
            }
            if (!isValidUrl(url)){
                et_url.setError("Your youtube link is not valid");
                return;
            }

            uploadUrl(url);

        });
        getMyprofile();
    }

    public String extractYTId(String ytUrl) {
        String youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(ytUrl);

        for(String regex : videoIdRegex) {
            Pattern compiledPattern = Pattern.compile(regex);
            Matcher matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain);

            if(matcher.find()){
                return matcher.group(1);
            }
        }

        return null;
    }

    public static String getYoutubeVideoId(String youtubeUrl){
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    private String youTubeLinkWithoutProtocolAndDomain(String url) {
        Pattern compiledPattern = Pattern.compile(youTubeUrlRegEx);
        Matcher matcher = compiledPattern.matcher(url);

        if(matcher.find()){
            return url.replace(matcher.group(), "");
        }
        return url;
    }

    private void getMyprofile(){
        pd=new ProgressDialog(UploadVideoActivity.this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String,String> param=new HashMap<>();
        param.put("member_id",session.getLoginData(SessionManager.KEY_USER_ID));
        common.makePostRequest(Utils.get_my_profile, param, response -> {
            pd.dismiss();
           // Log.d("myprof",response);
            try {
                JSONObject object=new JSONObject(response);
                JSONObject data=object.getJSONObject("data");

                if (!data.getString("video_url").equals("") && !data.getString("video_url").equals("null")){
                    //String html = "<iframe width=\"345\" height=\"200\" src=\""+data.getString("video_url")+"?width=450&height=260&results=60&dynamic=true\" ></iframe>";
                   // web_video.loadData(html, "text/html", null);
                    vid_url=getYoutubeVideoId(data.getString("video_url"));
                    Log.d("myprof",vid_url+"  "+data.getString("video_url"));
                    YouTubePlayerSupportFragment frag = (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
                    assert frag != null;
                    frag.initialize(getString(R.string.google_api_key), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                            //if (!wasRestored) {
                            Log.d("myprof",vid_url+"  onInitializationSuccess");
                                player.cueVideo(vid_url);
                           // }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
                            if (errorReason.isUserRecoverableError()) {
                                errorReason.getErrorDialog(UploadVideoActivity.this, RECOVERY_REQUEST).show();
                            } else {
                                String error = String.format(getString(R.string.player_error), errorReason.toString());
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                   // web_video.setVisibility(View.GONE);
                }
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

    private void uploadUrl(String url) {
        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        HashMap<String,String> param=new HashMap<>();
        param.put("member_id",session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("videoUrl",url);

        common.makePostRequest(Utils.add_video, param, response -> {
            pd.dismiss();
            try {
                JSONObject object=new JSONObject(response);
                common.showToast(object.getString("errmessage"));
                if (object.getString("status").equals("success")){
                    et_url.setText("");
                    recreate();
                }
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

    private boolean isValidUrl(String url) {

        if (url == null) {
            return false;
        }
        if (URLUtil.isValidUrl(url)) {
            // Check host of url if youtube exists
            Uri uri = Uri.parse(url);
            if ("www.youtube.com".equals(uri.getHost()) || "youtu.be".equals(uri.getHost())) {
                return true;
            }
        }
        // In other any case
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
