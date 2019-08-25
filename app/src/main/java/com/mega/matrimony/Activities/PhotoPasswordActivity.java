package com.mega.matrimony.Activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mega.matrimony.Utility.Common;
import com.mega.matrimony.Fragments.PhotoPasswordReceivedFragment;
import com.mega.matrimony.Fragments.PhotoPasswordSentFragment;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PhotoPasswordActivity extends AppCompatActivity {

    Common common;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager session=new SessionManager(this);
        if (!session.isLoggedIn()){
            startActivity(new Intent(this,LoginActivity.class));
            return;
        }
        setContentView(R.layout.activity_photo_password);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo Request");

        common=new Common(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Bundle b=getIntent().getExtras();
        if (b!=null){
            if (b.containsKey("ppassword_tag")){
                if (b.getString("ppassword_tag").equals("receive")){
                    viewPager.setCurrentItem(1);
                }
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle b=intent.getExtras();
        if (b!=null){
            if (b.containsKey("ppassword_tag")){
                if (b.getString("ppassword_tag").equals("receive")){
                    viewPager.setCurrentItem(1);
                }
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PhotoPasswordSentFragment(), "Sent");
        adapter.addFragment(new PhotoPasswordReceivedFragment(), "Received");
        viewPager.setAdapter(adapter);
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
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
            onBackPressed();
            // NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}