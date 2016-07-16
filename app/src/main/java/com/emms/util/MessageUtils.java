package com.emms.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by lenovo on 2016/7/16.
 */
public class MessageUtils {

    public static void showToast(final String toast, final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

}
