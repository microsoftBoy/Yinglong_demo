package com.zhangshuai.yinglong_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_camera)
    ImageView iv_camera;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;
    @BindView(R.id.ll_photos)
    LinearLayout ll_photos;
    private ArrayList<String> photos = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Configuration config;
    private UploadManager uploadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initQiniuSdk();
        layoutInflater = LayoutInflater.from(this);

        startActivity(new Intent(this,Main2Activity.class));

    }

    private void initView() {
        if (photos.size() == 0){
            iv_camera.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
        }
        else {
            iv_camera.setVisibility(View.GONE);
            ll_content.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @OnClick(R.id.iv_camera)
    void selectPhotos(){
        addPhotos();
    }
    void addPhotos(){
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                photos.clear();
                photos.addAll(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
                Log.i("_zs_", "onActivityResult: photos.size()="+photos.size());
            }
            if (photos.size()>0){
                addPhotosView();
            }
        }
    }

    private void addPhotosView(){
        ll_photos.removeAllViews();
        if (photos.size()>4){
            for (int i = 0; i < 3; i++) {
                View inflate = layoutInflater.inflate(R.layout.item_photo, null);
                ImageView iv_photo = inflate.findViewById(R.id.iv_photo);
                Glide.with(this)
                        .load(photos.get(i))
                        .into(iv_photo);
                ll_photos.addView(inflate);
            }
            View more = layoutInflater.inflate(R.layout.item_more_photo, null);
            ImageView iv_photo = more.findViewById(R.id.iv_photo);
            TextView tv_num = more.findViewById(R.id.tv_num);
            Glide.with(this)
                    .load(photos.get(3))
                    .into(iv_photo);
            tv_num.setText(photos.size()+"张");
            ll_photos.addView(more);
            View add = layoutInflater.inflate(R.layout.item_photo, null);
            ImageView iv_photo_add = add.findViewById(R.id.iv_photo);
            iv_photo_add.setBackground(getResources().getDrawable(R.mipmap.photo_add));
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhotos();
                }
            });
            ll_photos.addView(add);
        }
        else {
            for (int i = 0; i < photos.size(); i++) {
                View inflate = layoutInflater.inflate(R.layout.item_photo, null);
                ImageView iv_photo = inflate.findViewById(R.id.iv_photo);
                Glide.with(this)
                        .load(photos.get(i))
                        .into(iv_photo);
                ll_photos.addView(inflate);
            }
            final View add = layoutInflater.inflate(R.layout.item_photo, null);
            ImageView iv_photo_add = add.findViewById(R.id.iv_photo);
            iv_photo_add.setBackground(getResources().getDrawable(R.mipmap.photo_add));
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhotos();
                }
            });
            ll_photos.addView(add);
        }
    }

    @OnClick(R.id.bt_upload)
    void bt_upload(){
//        for (int i = 0; i < photos.size(); i++) {
//            //此处调用七牛上传图片接口即可
//            String s = photos.get(i);
//            upLoadImg(s);
//        }
        startActivity(new Intent(this,Main2Activity.class));
    }

    private void initQiniuSdk() {
        config = new Configuration.Builder()
                .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
                .recorder(null, null)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone0)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
// 重用uploadManager。一般地，只需要创建一个uploadManager对象
        uploadManager = new UploadManager(config);

    }
    private File file = null;
    private String token = "mWzmRbD1k2EDBZnLOypHZ3ayKbwD1jPCQX0PQ_zS:5UfP9aVye1-ElHTg-S-zirFN-m0" +
            "=:eyJzY29wZSI6InFpbml1LWRlbW8iLCJkZWFkbGluZSI6MTUxNjc0MDQwOH0=";
    ArrayList<String> keys = new ArrayList<String>();
    private void upLoadImg(String path) {
        Log.i("_zs_", "upLoadImg: ");
        file = new File(path);
        uploadManager.put(file, null, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    Log.i("qiniu", "Upload Success");
                    keys.add(response.optString("key"));
                    if (keys.size() == photos.size()){
                        //表示已经上传完了，调用submit接口

                    }

//                    Toast.makeText(MainActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
                } else {
                    Log.i("qiniu", "Upload Fail");
                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
//                    Toast.makeText(MainActivity.this, "Upload Fail", Toast.LENGTH_LONG).show();
                }
                Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                Log.i("qiniu", key + ": " + percent);
            }
        }, null));
    }

}
