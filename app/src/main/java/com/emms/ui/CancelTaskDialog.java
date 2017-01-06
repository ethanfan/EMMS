package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.emms.R;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;

/**
 * Created by Administrator on 2016/8/26.
 *
 */
public class CancelTaskDialog extends Dialog{
    private Context context;
    private EditText cancelReason;
    private int TipsResID;
    public CancelTaskDialog(Context context) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);
        this.context = context;
        setContentView(R.layout.cancel_dialog);
       initview();
        TipsResID=R.string.NoCancelReason;
        setCancelable(false);
    }
    public void setDialogTitle(int resID){
        ((TextView)findViewById(R.id.DialogTitle)).setText(resID);
    }
    public void setTipsResID(int resID){
        TipsResID=resID;
    }
    private void initview(){
        cancelReason=(EditText)findViewById(R.id.CancelReason);
        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelReason.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancelReason.getText().toString().equals("")){
                    ToastUtil.showToastShort(TipsResID,context);
                    return;
                }
                taskCancelListener.submitCancel(cancelReason.getText().toString());
                dismiss();
            }
        });
    }

    public void setTaskCancelListener(TaskCancelListener taskCancelListener) {
        this.taskCancelListener = taskCancelListener;
    }

    private TaskCancelListener taskCancelListener;

}
