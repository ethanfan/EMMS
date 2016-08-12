package com.emms.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ToastUtil {
    public static void showToastLong(int resId, Context context){
        Toast toast=Toast.makeText(context,resId,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    public static void showToastLong(String string, Context context){
        Toast toast=Toast.makeText(context,string,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
}
