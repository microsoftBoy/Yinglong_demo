package com.zhangshuai.yinglong_demo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.zhangshuai.yinglong_demo.GlideCircleTransform;
import com.zhangshuai.yinglong_demo.R;
import com.zhangshuai.yinglong_demo.TransformDefaultTarget;
import com.zhangshuai.yinglong_demo.bean.PhotoBean;

import java.util.ArrayList;

/**
 * Created by zhangshuai on 2018-01-24.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHold> {


    private Context mContext;
    private ArrayList<PhotoBean> mData;
    private LayoutInflater inflater;
    private  RequestManager requestManager;

    public MyRecyclerViewAdapter(Context context, ArrayList<PhotoBean> data) {
        mContext = context;
        mData = data;
        inflater = LayoutInflater.from(mContext);
        requestManager = Glide.with(mContext);

    }


    @Override
    public MyViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.item_photo_rv, null);
        MyViewHold myViewHold = new MyViewHold(inflate);

        return myViewHold;
    }

    @Override
    public void onBindViewHolder(final MyViewHold holder, final int position) {
        PhotoBean photoBean = mData.get(position);

        String tag = (String) holder.iv_photo.getTag();
        if (!photoBean.url.equals(tag)){
            GlideCircleTransform transform = new GlideCircleTransform(mContext, 1, Color.rgb(147,147,137));

            TransformDefaultTarget transformDefaultTarget = new TransformDefaultTarget
                    (holder.iv_photo, R.mipmap.photo_person_default, transform);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.transform(transform)
                    .dontAnimate();
            requestManager.setDefaultRequestOptions(requestOptions);
//            requestManager.load(photoBean.url).into(holder.iv_photo);
            holder.iv_photo.setTag(photoBean.url);
        }



        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCliclListener != null){
                    onItemCliclListener.onItemclick(holder.ll_item,position);
                }
            }
        });
        if (photoBean.isSelecte){
            holder.rl_iv_bg.setBackgroundResource(R.drawable.photo_bg_round);
        }
        else {
            holder.rl_iv_bg.setBackgroundResource(R.drawable.photo_bg_transparent);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHold extends RecyclerView.ViewHolder {

        ImageView iv_photo;
        LinearLayout ll_item;
        RelativeLayout rl_iv_bg;

        public MyViewHold(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            ll_item = itemView.findViewById(R.id.ll_item);
            rl_iv_bg = itemView.findViewById(R.id.rl_iv_bg);
        }
    }

    public void setOnItemCliclListener(OnItemCliclListener onItemCliclListener) {
        this.onItemCliclListener = onItemCliclListener;
    }

    private OnItemCliclListener onItemCliclListener;

    public interface OnItemCliclListener{
        void onItemclick(View view,int position);
    }

}
