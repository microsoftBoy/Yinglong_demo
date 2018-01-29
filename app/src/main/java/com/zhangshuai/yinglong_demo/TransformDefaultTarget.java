package com.zhangshuai.yinglong_demo;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class TransformDefaultTarget extends ImageViewTarget<Drawable> {
    BitmapTransformation transformation;
    int mDrawable;

    public TransformDefaultTarget(ImageView view, @DrawableRes int drawable, BitmapTransformation
            transformation) {
        super(view);
        this.transformation = transformation;
        this.mDrawable = drawable;
    }

    @Override
    protected void setResource(Drawable resource) {
        view.setImageDrawable(resource);
    }

    @Override
    public void onLoadFailed(Drawable errorDrawable) {
        /*Glide.with(view.getContext().getApplicationContext()).load(mDrawable).dontAnimate()
                .transform(, transformation).into(view);*/
        RequestManager requestManager = Glide.with(view.getContext().getApplicationContext());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions
                .dontAnimate()
                .transform(transformation);
        requestManager.setDefaultRequestOptions(requestOptions)
                .load(mDrawable)
                .into(view);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
//        Glide.with(view.getContext().getApplicationContext()).load(mDrawable).dontAnimate()
//                .transform(new CenterCrop(view.getContext()),transformation).into(view);
        view.setImageResource(mDrawable);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
