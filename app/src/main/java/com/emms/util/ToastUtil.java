package com.emms.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/4.
 *
 */
public class ToastUtil {
    /**
     * Show the toast with the resource id in short time
     * @param resId The ID of the Resource to be showed
     * @param context The Context of the Applicant or Activity
     */
    public static void showToastShort(int resId, Context context){
        Toast toast=Toast.makeText(context,resId,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    /**
     * Show the toast with the charsquence in long time
     * @param string  The charsquence to be showed
     * @param context The Context of the Applicant or Activity
     */
    public static void showToastShort(String string, Context context){
        Toast toast=Toast.makeText(context,string,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static void showToastLong(int resId, Context context){
        Toast toast=Toast.makeText(context,resId,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }
    public static void showToastLong(String string, Context context){
        Toast toast=Toast.makeText(context,string,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

}
