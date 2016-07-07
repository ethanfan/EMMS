package com.emms.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.emms.R;

public class MachineActivity extends BaseActivity implements View.OnClickListener{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);
//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        findViewById(R.id.searh_oldrecord).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MachineActivity.this,SearchRecordActivity.class));
    }
}
