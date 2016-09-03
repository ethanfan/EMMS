package com.emms.push;

import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.emms.activity.CusActivity;
import com.emms.activity.LoginActivity;
import com.emms.util.SharedPreferenceManager;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView tv = new TextView(this);
//        tv.setText("用户自定义打开的Activity");
//        Intent intent = getIntent();
//        if (null != intent) {
//	        Bundle bundle = getIntent().getExtras();
//	        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
//	        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
//	        tv.setText("Title : " + title + "  " + "Content : " + content);
//        }
//        addContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        if(SharedPreferenceManager.getCookie(this)!=null){
            startActivity(new Intent(this, CusActivity.class));
        }else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

}
