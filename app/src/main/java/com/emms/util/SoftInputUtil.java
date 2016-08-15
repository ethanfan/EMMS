package com.emms.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Administrator on 2016/8/14.
 */
public class SoftInputUtil {
    public static void hideSoftInput(View v, Context context){
        InputMethodManager imm =
                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
       // while(true){
        imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        //imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
    }

}
