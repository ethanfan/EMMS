package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.emms.R;

/**
 * Created by jaffer.deng on 2016/6/8.
 */
public class TipsDialog extends Dialog{
    Context context;


    public TipsDialog(Context context) {
        super(context);
        this.context =context;
    }

    public TipsDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public TipsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_tips);
        findViewById(R.id.comfig_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
