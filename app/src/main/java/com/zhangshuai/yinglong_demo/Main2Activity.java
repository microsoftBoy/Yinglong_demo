package com.zhangshuai.yinglong_demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhangshuai.yinglong_demo.adapter.MyRecyclerViewAdapter;
import com.zhangshuai.yinglong_demo.adapter.MyViewPagerAdapter;
import com.zhangshuai.yinglong_demo.bean.PhotoBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;

import static android.view.Gravity.CENTER;

public class Main2Activity extends AppCompatActivity {

    @BindView(R.id.rv)
    RecyclerView recyclerView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CircleIndicator indicator;

    @BindView(R.id.tabs)
    MultiPagerSlidingTabStrip tabs;
    @BindView(R.id.ll)
    LinearLayout ll;

    ArrayList<PhotoBean> photos = new ArrayList<>();
    private MyViewPagerAdapter myViewPagerAdapter;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, photos);
//        myRecyclerViewAdapter.setHasStableIds(true);
//        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setOnItemCliclListener(new MyRecyclerViewAdapter.OnItemCliclListener() {

            @Override
            public void onItemclick(View view, int position) {
                for (int i = 0; i < photos.size(); i++) {
                    PhotoBean photoBean = photos.get(i);
                    if (i == position){
                        photoBean.isSelecte = true;
                    }
                    else {
                        photoBean.isSelecte = false;
                    }
                }
                viewPager.setCurrentItem(position);
                myRecyclerViewAdapter.notifyDataSetChanged();
            }
        });

        myViewPagerAdapter = new MyViewPagerAdapter(this, photos);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        initTab();
        tabs.setViewPager(viewPager);

        initLL();

    }

    private void initLL() {
        TextView tab = new TextView(this);
        tab.setText("怎么回事");
        tab.setGravity(CENTER);

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.mipmap.rectangle_style);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams textLP = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tab.setId(tab.hashCode());
        textLP.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        textLP.setMargins(Utils.dip2px(getContext(),6),Utils.dip2px(getContext(),6),Utils.dip2px(getContext(),6),0);
        relativeLayout.addView(tab,textLP);

        RelativeLayout.LayoutParams ImgLP = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ImgLP.setMargins(Utils.dip2px(getContext(),16),10,Utils.dip2px(getContext(),16),0);
        int id = relativeLayout.getChildAt(0).getId();
        ImgLP.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		ImgLP.addRule(RelativeLayout.BELOW,relativeLayout.getChildAt(0).getId());
//        ImgLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(icon,ImgLP);
        ll.setGravity(CENTER);
        ll.addView(relativeLayout);
        ll.setPadding(24,24,24,0);

    }

    private Context getContext(){
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                photos.clear();
                ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(PhotoPicker
                        .KEY_SELECTED_PHOTOS);
                for (int i = 0; i <stringArrayListExtra.size() ; i++) {
                    PhotoBean photoBean = new PhotoBean();
                    photoBean.url = stringArrayListExtra.get(i);
                    if (i == 0){
                        photoBean.isSelecte = true;
                    }
                    photos.add(photoBean);
                }
                Log.i("_zs_", "onActivityResult: photos.size()=" + photos.size());
                myViewPagerAdapter.notifyDataSetChanged();
                myRecyclerViewAdapter.notifyDataSetChanged();
                indicator.setViewPager(viewPager);
                tabs.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.iv_camera)
    void selectPhotos() {
        addPhotos();
    }

    void addPhotos() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            for (int i = 0; i < photos.size(); i++) {
                PhotoBean photoBean = photos.get(i);
                if (i == position){
                    photoBean.isSelecte = true;
                }
                else {
                    photoBean.isSelecte = false;
                }
            }
            viewPager.setCurrentItem(position);
            myRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void initTab() {
        tabs.setShouldExpand(true);
        tabs.setAllCaps(false);
        tabs.setTabPaddingLeftRight(getResources().getDimensionPixelOffset(R.dimen.space_4));
        tabs.setTabPaddingTop(Utils.dip2px(this, 4));
        tabs.setTextSize(13);
        tabs.setTabTextSelectedSize(15);
        tabs.setTextColor(getResources().getColor(R.color.color_747474));
        tabs.setTabTextSelectedColor(getResources().getColor(R.color.color_two_e));
        tabs.setUnderlineHeight(getResources().getDimensionPixelOffset(R.dimen.space_1));
        tabs.setUnderlineColor(getResources().getColor(R.color.yellow));
        tabs.setDividerColor(Color.TRANSPARENT);
        tabs.setIndicatorHeight(getResources().getDimensionPixelOffset(R.dimen.space_1));
        tabs.setIndicatorColor(getResources().getColor(R.color.transparent));
        tabs.setTabIndicatorMaginTop(Utils.dip2px(this, 2));
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
