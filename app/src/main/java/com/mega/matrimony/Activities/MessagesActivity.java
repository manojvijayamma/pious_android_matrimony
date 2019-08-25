package com.mega.matrimony.Activities;

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
import com.mega.matrimony.Fragments.MessageDraftFragment;
import com.mega.matrimony.Fragments.MessageInboxFragment;
import com.mega.matrimony.Fragments.MessageSentFragment;
import com.mega.matrimony.Fragments.MessageTrashFragment;
import com.mega.matrimony.R;
import com.mega.matrimony.Utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

   // ListView lv_msg;
   // private EditText search_view;
    Common common;
    SessionManager session;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message");

        common=new Common(this);
        session=new SessionManager(this);

        //lv_msg=(ListView)findViewById(R.id.lv_msg);

       // lv_msg.setAdapter(adp);

       // search_view=(EditText)findViewById(R.id.search_view);
       // common.setDrawableLeftEditText(R.drawable.search_pink,search_view);

        common=new Common(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MessageInboxFragment(), "Inbox");
        adapter.addFragment(new MessageSentFragment(), "Sent");
        adapter.addFragment(new MessageDraftFragment(), "Draft");
        adapter.addFragment(new MessageTrashFragment(), "Trash");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id ==android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
