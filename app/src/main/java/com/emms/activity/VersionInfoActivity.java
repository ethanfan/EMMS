package com.emms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.emms.R;

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
            String versionName=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.version_num)).setText(versionName);
        }catch (Exception e){}
        ((TextView)findViewById(R.id.connect_email)).setText("1436615204@qq.com(Andy)");

    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    @Override
    public void onClick(View v) {

    }
}
