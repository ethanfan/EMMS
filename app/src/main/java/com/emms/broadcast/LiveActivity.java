package com.emms.broadcast;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/2/9.
 *
 */
public class LiveActivity extends AppCompatActivity {
    private static final String Tag="KeepLive";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Window window=getWindow();
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        WindowManager.LayoutParams params=window.getAttributes();
        params.x=0;
        params.y=0;
        params.height=1;
        params.width=1;
        window.setAttributes(params);
    }
}
