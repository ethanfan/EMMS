package com.emms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.emms.R;
import com.emms.util.BuildConfig;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2016/9/18.
 *
 */
public class VersionInfoActivity extends NfcActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_info);
        initView();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.version_info);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            String versionName="v"+getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            switch (BuildConfig.appEnvironment){
                case DEVELOPMENT:{
                    versionName+="Development";
                    break;
                }
                case UAT:{
                    versionName+="UAT";
                    break;
                }
                case PROD:
                default:{
                    break;
                }
            }
            ((TextView) findViewById(R.id.version_num)).setText(versionName);
        }catch (Throwable e){
            CrashReport.postCatchedException(e);
        }
        String email="PengA@esquel.com(Andy)";
        ((TextView)findViewById(R.id.connect_email)).setText(email);
//        if(BuildConfig.isDebug){
//            ((ImageView)findViewById(R.id.downloadImage)).setImageResource(R.mipmap.download_image_test);
//        }
        switch (BuildConfig.appEnvironment){
            case DEVELOPMENT:{
                ((ImageView)findViewById(R.id.downloadImage)).setImageResource(R.mipmap.download_image_test);
                break;
            }
            case UAT:{
                ((ImageView)findViewById(R.id.downloadImage)).setImageResource(R.mipmap.emmsuat);
                break;
            }
            case PROD:
            default:{
                break;
            }
        }
    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    @Override
    public void onClick(View v) {

    }
}
