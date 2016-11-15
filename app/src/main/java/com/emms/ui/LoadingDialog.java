package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;

import com.emms.R;


/**
 * 
 * @author joyaether
 * 
 */
public class LoadingDialog extends Dialog {

    Handler handler = new Handler();
    static final int MAX_TIME = 10000;

    public LoadingDialog(Context context) {
        this(context, R.style.ProgressDialogWithoutFrame);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.progress_dialog);
    }

    @Override
    public void dismiss() {
        handler.removeCallbacksAndMessages(null);
        try {
            super.dismiss();
        } catch (Exception e) {
        	
        }
    }

    @Override
    public void show() {
        if (!isShowing()) {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();

                }
            }, MAX_TIME);
            super.show();
        }
    }

}
