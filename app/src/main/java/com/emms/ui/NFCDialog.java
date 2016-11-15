package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.emms.R;

/**
 * Created by jaffer.deng on 2016/6/17.
 *
 */
public abstract class NFCDialog extends Dialog {

    Context context;


    public NFCDialog(Context context) {
        super(context);
        this.context =context;
    }

    public NFCDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public NFCDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_nfc);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissAction();
    }

    @Override
    public void show() {
        super.show();
        showAction();
    }

    public abstract void dismissAction();
    public abstract void showAction();
}
