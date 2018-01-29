package com.zhangshuai.yinglong_demo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.zhangshuai.yinglong_demo.MultiPagerSlidingTabStrip;
import com.zhangshuai.yinglong_demo.R;
import com.zhangshuai.yinglong_demo.bean.PhotoBean;

import java.util.ArrayList;

/**
 * Created by zhangshuai on 2018-01-24.
 */

public class MyViewPagerAdapter extends PagerAdapter implements MultiPagerSlidingTabStrip.TitleIconTabProvider{

    private  ArrayList<PhotoBean> mData;
    private  Context mContext;
    private final LayoutInflater inflater;
    private final RequestManager requestManager;

    public MyViewPagerAdapter(Context context, ArrayList<PhotoBean> data) {
        mContext = context;
        mData = data;
        inflater = LayoutInflater.from(mContext);
        requestManager = Glide.with(mContext);
    }

    @Override
    public int getCount() {
//        if (mData != null){
//            return mData.size();
//        }else {
//            return 0;
//        }
        return mData.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        View inflate = inflater.inflate(R.layout.item_pager, null);
        ImageView iv_photo = inflate.findViewById(R.id.iv_photo);
        requestManager
                .load(mData.get(position).url)
                .into(iv_photo);
        container.addView(inflate);
        return inflate;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);

    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getPageIconResId(int position) {
        return R.mipmap.rectangle_style;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "哈哈哈哈";
    }
}
