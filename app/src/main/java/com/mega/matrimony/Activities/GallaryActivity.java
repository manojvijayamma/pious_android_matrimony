package com.mega.matrimony.Activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mega.matrimony.Custom.TouchImageView;
import com.mega.matrimony.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GallaryActivity extends AppCompatActivity {


    ViewPager pager_gallary;

    private TextView tv_img_count;
    CustomPagerAdapter adapter;
    int pos=0;
    JSONArray array;
    String[] images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);

        getSupportActionBar().setTitle("Gallery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        pager_gallary=(ViewPager)findViewById(R.id.pager_gallary);

        tv_img_count=(TextView)findViewById(R.id.tv_img_count);

        Bundle b=getIntent().getExtras();
        if (b!=null){
            if (b.containsKey("imagePosition")){
                pos=b.getInt("imagePosition");
            }
            if (b.containsKey("imageArray")){
                try {
                    array=new JSONArray(b.getString("imageArray"));
                    images=new String[array.length()];
                    for (int i=0;i<array.length();i++){
                        JSONObject object=array.getJSONObject(i);
                        images[i]=object.getString("value");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        adapter=new CustomPagerAdapter(this,images);
        pager_gallary.setAdapter(adapter);

        //if (pos!=0){
        pager_gallary.setCurrentItem(pos);
        // }

        pager_gallary.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
                String text=(position+1)+" Out of "+images.length;
                tv_img_count.setText(text);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        String[] img_arr;

        public CustomPagerAdapter(Context context, String[] arr) {
            this.mContext = context;
            this.img_arr=arr;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout =inflater.inflate(R.layout.gallary_image, collection, false);

            TouchImageView img=(TouchImageView)layout.findViewById(R.id.myZoomageView);
            // img.setImageResource(img_arr[position]);
            Picasso.get().load(img_arr[position]).placeholder(R.drawable.placeholder).into(img);

            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return img_arr.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
