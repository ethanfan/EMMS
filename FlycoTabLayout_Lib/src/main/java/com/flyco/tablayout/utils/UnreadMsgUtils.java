package com.flyco.tablayout.utils;


import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.widget.MsgView;

/**
 * 未读消息提示View,显示小红点或者带有数字的红点:
 * 数字一位,圆
 * 数字两位,圆角矩形,圆角是高度的一半
 * 数字超过两位,显示99+
 */
public class UnreadMsgUtils {
    public static void show(MsgView msgView, int num) {
        if (msgView == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) msgView.getLayoutParams();
        DisplayMetrics dm = msgView.getResources().getDisplayMetrics();

        if (num <= 0) {//圆点,设置默认宽高
            msgView.setVisibility(View.GONE);
        } else {
            msgView.setVisibility(View.VISIBLE);
            lp.height = (int) (20 * dm.density);
            if (num > 0 && num < 10) {//圆
                lp.width = (int) (20 * dm.density);
                msgView.setPadding(0, 0, 0, (int) (3 * dm.density));
                msgView.setText(num + "");
            } else if (num > 9 && num < 100) {//圆角矩形,圆角是高度的一半,设置默认padding
                lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                msgView.setPadding(0, 0, 0, (int) (3 * dm.density));
                msgView.setText(num + "");
            } else {//数字超过两位,显示99+
                lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                msgView.setPadding(0, 0, 0, (int) (3 * dm.density));
                msgView.setText("99+");
            }
            msgView.setLayoutParams(lp);
        }
    }

    public static void setSize(MsgView rtv, int size) {
        if (rtv == null) {
            return;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rtv.getLayoutParams();
        lp.width = size;
        lp.height = size;
        rtv.setLayoutParams(lp);
    }
}
