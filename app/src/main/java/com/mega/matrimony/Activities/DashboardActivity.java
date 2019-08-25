package com.mega.matrimony.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mega.matrimony.Adapter.MenuListAdapter;
import com.mega.matrimony.Application.MyApplication;
import com.mega.matrimony.Fragments.DiscoverFragment;
import com.mega.matrimony.Fragments.RecommendationFragment;
import com.mega.matrimony.Model.MenuChild;
import com.mega.matrimony.Model.MenuGroup;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Utility.SessionManager;
import com.mega.matrimony.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tv_edit_profile, tv_name, tv_matri_id;
    LinearLayout lay_header;
    Common common;
    SessionManager session;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ExpandableListView list_menu;
    private Button btn_member;
    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog pd;
    ImageView img_profile;
    private int placeHolder,photoProtectPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_deshboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        common = new Common(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }



        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        tv_edit_profile = (TextView) headerView.findViewById(R.id.tv_edit_profile);
        tv_name = (TextView) headerView.findViewById(R.id.tv_name);
        tv_matri_id = (TextView) headerView.findViewById(R.id.tv_matri_id);
        img_profile = (ImageView) headerView.findViewById(R.id.img_profile);
        //lay_header=(LinearLayout) headerView.findViewById(R.id.lay_header);
        common.setDrawableLeftTextView(R.drawable.eye_white, tv_edit_profile);

        btn_member = (Button) findViewById(R.id.btn_member);

        initMenu();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        btn_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, PlanListActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        tv_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ViewMyProfileActivity.class));
                drawer.closeDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("dashresume", "onResume");
        getMyprofile();
    }

    private void getCurrentPlan() {
        // pd=new ProgressDialog(this);
        // pd.setMessage("Loading...");
        //  pd.setCancelable(false);
        //  pd.setIndeterminate(true);
        //  pd.show();

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_matri_id));
        common.makePostRequest(Utils.check_plan, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                //Log.d("resp","getCurrentPlan");
                try {
                    JSONObject object = new JSONObject(response);
                    MyApplication.setPlan(object.getBoolean("is_show"));

                } catch (JSONException e) {
                    if (pd != null)
                        pd.dismiss();
                    e.printStackTrace();
                }
                //  pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // pd.dismiss();
                if (pd != null)
                    pd.dismiss();
            }
        });
    }

    private void getMyprofile() {
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        common.makePostRequest(Utils.get_my_profile, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pd != null)
                    pd.dismiss();
                //Log.d("resp","getMyprofile");
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject data = object.getJSONObject("data");

                    tv_name.setText(data.getString("username"));
                    tv_matri_id.setText(data.getString("matri_id"));

                    if (!data.getString("photo1").equals(""))
                        Picasso.get().load(data.getString("photo1")).placeholder(placeHolder).error(placeHolder).into(img_profile);
                    else {
                        img_profile.setImageResource(placeHolder);
                    }
                    getCurrentPlan();

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DiscoverFragment(), "Recently Join");//DiscoverFragment
        adapter.addFragment(new RecommendationFragment(), "Recently Login");//RecommendationFragment
        viewPager.setAdapter(adapter);
    }

    private void initMenu() {
        list_menu = (ExpandableListView) findViewById(R.id.list_menu);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        final List<MenuGroup> list = new ArrayList<>();

        MenuGroup grp = new MenuGroup("Dashboard", R.drawable.home_pink, new ArrayList<MenuChild>(), "dashboars");
        list.add(grp);
        grp = new MenuGroup("Search", R.drawable.search_pink, new ArrayList<MenuChild>(), "search");
        list.add(grp);

        List<MenuChild> pro = new ArrayList<MenuChild>();
        pro.add(new MenuChild("View Profile", "view_profile"));
        pro.add(new MenuChild("Change Password", "change_password"));
        pro.add(new MenuChild("Manage Photos", "manage_photo"));
        pro.add(new MenuChild("Manage Account", "manage_account"));
        pro.add(new MenuChild("My Saved Search", "save_search"));
        pro.add(new MenuChild("Upload Video", "upload_video"));
        pro.add(new MenuChild("Upload ID Proof", "upload_id"));
        pro.add(new MenuChild("Upload Horoscope", "upload_horo"));
        pro.add(new MenuChild("Delete Account", "delete_account"));
        grp = new MenuGroup("My Profile", R.drawable.user_fill_pink, pro, "profile");
        list.add(grp);

        List<MenuChild> add = new ArrayList<MenuChild>();
        add.add(new MenuChild("Contact Us", "contact_us"));
        add.add(new MenuChild("Privacy Policy", "privacy_policy"));
        add.add(new MenuChild("Refund Policy", "refund_policy"));
        add.add(new MenuChild("Terms and Condition", "term_condition"));
        add.add(new MenuChild("About Us", "about"));
        add.add(new MenuChild("Report Misuse", "report"));
        grp = new MenuGroup("Additional Setting", R.drawable.setting_pink, add, "add_setting");
        list.add(grp);

        grp = new MenuGroup("Message", R.drawable.message_pink, new ArrayList<MenuChild>(), "message");
        list.add(grp);
        grp = new MenuGroup("Shortlisted", R.drawable.starfill_pink, new ArrayList<MenuChild>(), "shortlist");
        list.add(grp);
        grp = new MenuGroup("Custom Matches", R.drawable.custom_match, new ArrayList<MenuChild>(), "cust_match");
        list.add(grp);
        grp = new MenuGroup("Photo Request", R.drawable.photo_password, new ArrayList<MenuChild>(), "photo_password");
        list.add(grp);

        List<MenuChild> con = new ArrayList<MenuChild>();
        con.add(new MenuChild("Profile I Viewed", "profile_i_view"));
        con.add(new MenuChild("Viewed My Profile", "view_my_profile"));
        con.add(new MenuChild("Liked Profile", "like_profile"));
        grp = new MenuGroup("Contact", R.drawable.phone, con, "contact");
        list.add(grp);

        grp = new MenuGroup("Express Interest", R.drawable.smile, new ArrayList<MenuChild>(), "exp_intrst");
        list.add(grp);

        grp = new MenuGroup("Logout", R.drawable.logout, new ArrayList<MenuChild>(), "logout");
        list.add(grp);

        MenuListAdapter adapter = new MenuListAdapter(this, list);
        list_menu.setAdapter(adapter);

        list_menu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int group, int child, long l) {
                switch (list.get(group).getChildren().get(child).getClickTag()) {
                    case "view_profile":
                        startActivity(new Intent(DashboardActivity.this, ViewMyProfileActivity.class));
                        break;
                    case "change_password":
                        startActivity(new Intent(DashboardActivity.this, ChangePasswordActivity.class));
                        break;
                    case "manage_photo":
                        startActivity(new Intent(DashboardActivity.this, ManagePhotosActivity.class));
                        break;
                    case "manage_account":
                        startActivity(new Intent(DashboardActivity.this, ManageAccountActivity.class));
                        break;
                    case "save_search":
                        startActivity(new Intent(DashboardActivity.this, SavedSearchActivity.class));
                        break;
                    case "upload_video":
                        startActivity(new Intent(DashboardActivity.this, UploadVideoActivity.class));
                        break;
                    case "upload_id":
                        Intent id = new Intent(DashboardActivity.this, UploadIdAndHoroscopeActivity.class);
                        id.putExtra("page_tag", "id");
                        startActivity(id);
                        break;
                    case "upload_horo":
                        Intent horo = new Intent(DashboardActivity.this, UploadIdAndHoroscopeActivity.class);
                        horo.putExtra("page_tag", "horoscope");
                        startActivity(horo);
                        break;
                    case "delete_account":
                        startActivity(new Intent(DashboardActivity.this, DeleteProfileActivity.class));
                        break;
                    case "contact_us":
                        startActivity(new Intent(DashboardActivity.this, ContactUsActivity.class));
                        break;
                    case "privacy_policy":
                        Intent pri = new Intent(DashboardActivity.this, AllCmsActivity.class);
                        pri.putExtra("cmsTag", "privacy");
                        startActivity(pri);
                        break;
                    case "refund_policy":
                        Intent refund = new Intent(DashboardActivity.this, AllCmsActivity.class);
                        refund.putExtra("cmsTag", "refund");
                        startActivity(refund);
                        break;
                    case "term_condition":
                        Intent term = new Intent(DashboardActivity.this, AllCmsActivity.class);
                        term.putExtra("cmsTag", "term");
                        startActivity(term);
                        break;
                    case "about":
                        Intent about = new Intent(DashboardActivity.this, AllCmsActivity.class);
                        about.putExtra("cmsTag", "about");
                        startActivity(about);
                        break;
                    case "report":
                        startActivity(new Intent(DashboardActivity.this, ReportMissuseActivity.class));
                        break;
                    case "profile_i_view":
                        Intent iv = new Intent(DashboardActivity.this, ViewedProfileActivity.class);
                        iv.putExtra("pageTag", "i_viewed");
                        startActivity(iv);
                        break;
                    case "view_my_profile":
                        Intent mp = new Intent(DashboardActivity.this, ViewedProfileActivity.class);
                        mp.putExtra("pageTag", "my_profile");
                        startActivity(mp);
                        break;
                    case "like_profile":
                        startActivity(new Intent(DashboardActivity.this, LikeProfileActivity.class));
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        list_menu.setOnGroupClickListener((expandableListView, view, i, l) -> {
            switch (list.get(i).getClickTag()) {
                case "dashboars":
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case "search":
                    startActivity(new Intent(DashboardActivity.this, SearchActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                //case "profile":
                // break;
                //case "add_setting":
                //  break;
                case "message":
                    startActivity(new Intent(DashboardActivity.this, QuickMessageActivity.class));//MessagesActivity
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case "shortlist":
                    startActivity(new Intent(DashboardActivity.this, ShortlistedProfileActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case "cust_match":
                    startActivity(new Intent(DashboardActivity.this, CustomMatchActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case "photo_password":
                    startActivity(new Intent(DashboardActivity.this, PhotoPasswordActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                //case "contact":
                //    break;
                case "exp_intrst":
                    startActivity(new Intent(DashboardActivity.this, ExpressInterestActivity.class));
                    drawer.closeDrawer(GravityCompat.START);
                    break;
                case "logout":
                    conformLogout();
                    drawer.closeDrawer(GravityCompat.START);
                    break;
            }

            return false;
        });


    }

    private void conformLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
        alert.setMessage("Are you sure you want logout from this app?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                session.logoutUser();
            }
        });
        alert.setNegativeButton("No", null);
        alert.show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                startActivity(intent);
                finish();
                System.exit(0);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.deshboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
