package com.emms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.BaseActivity;
import com.emms.activity.CusActivity;
import com.emms.activity.TaskDetailsActivity;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;

/**
 * Created by Administrator on 2016/11/8.
 *
 */
public class TaskCompleteDialog extends Dialog {
    private Context context;

    public void setTaskClass(String taskClass) {
        TaskClass = taskClass;
    }

    private String TaskClass;
    public void setTask_ID(String task_ID) {
        Task_ID = task_ID;
    }

    private String Task_ID;
    public TaskCompleteDialog(Context context) {
        super(context);
        this.context=context;
        setContentView(R.layout.dialog_task_complete);
        initView();
    }

    public TaskCompleteDialog(Context context, int theme) {
        super(context, theme);
        this.context=context;
        setContentView(R.layout.dialog_task_complete);
        initView();
    }

    protected TaskCompleteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context=context;
        setContentView(R.layout.dialog_task_complete);
        initView();
    }
    private void initView(){
        EditText inputPassWord=(EditText)findViewById(R.id.password);
        inputPassWord.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        inputPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnclick();
            }
        });
    }
    private void doOnclick(){
        TextView account=(TextView)findViewById(R.id.account);
        TextView password=(TextView)findViewById(R.id.password);
        if(account.getText().toString().equals("")){
            ToastUtil.showToastShort(R.string.warning_message_no_user,context);
         return;
        }
        if(password.getText().toString().equals("")){
            ToastUtil.showToastShort(R.string.warning_message_no_password,context);
         return;
        }
        ((BaseActivity)context).showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        submitData.set("OperatorNo",account.getText().toString().toUpperCase());
        submitData.set("Password",password.getText().toString());
        submitData.set("task_id",Task_ID);
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(context, "TaskOperatorAPI/CheckUserRoleForICCardID", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.FailToCheckIDCauseByTimeOut,context);
                ((BaseActivity)context).dismissCustomDialog();
            }
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.SuccessToCheckID,context);
                        TaskComplete(jsonObjectElement.get(Data.PAGE_DATA));
                    }else {
                        ToastUtil.showToastShort(R.string.FailToCheckID,context);
                        ((BaseActivity)context).dismissCustomDialog();
                    }
                }else {
                    ((BaseActivity)context).dismissCustomDialog();
                }
            }
        });
    }
    private void TaskComplete(final DataElement dataElement){
        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set(Task.TASK_ID,Task_ID);
        params.putJsonParams(data.toJson());
            HttpUtils.post(context, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success")!=null&&
                            jsonObjectElement.get("Success").valueAsBoolean()){
                        dismiss();
                        ToastUtil.showToastShort(R.string.taskComplete,context);
                        if(jsonObjectElement.get("Tag")==null||"1".equals(DataUtil.isDataElementNull(jsonObjectElement.get("Tag")))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            if (TaskClass != null && TaskClass.equals(Task.TRANSFER_MODEL_TASK)) {
                                builder.setMessage(R.string.DoYouNeedToCreateACarMovingTask);
                            } else {
                                builder.setMessage(R.string.DoYouNeedToCreateAShuntingTask);
                            }
                            builder.setCancelable(false);
                            builder.setPositiveButton(R.string.sure, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(context, CusActivity.class);
                                    if (TaskClass != null && TaskClass.equals(Task.TRANSFER_MODEL_TASK)) {
                                        intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK, Constants.FLAG_CREATE_CAR_MOVING_TASK);
                                    } else {
                                        intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK, Constants.FLAG_CREATE_SHUNTING_TASK);
                                    }
                                    if (dataElement != null) {
                                        intent.putExtra("OperatorInfo", dataElement.toString());
                                    }

                                    intent.putExtra("FromTask_ID",
                                            Task_ID);
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton(R.string.cancel, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    context.startActivity(new Intent(context, CusActivity.class));
                                }
                            });
                            builder.show();
                        }
                    }else {
                       ToastUtil.showToastShort(R.string.canNotSubmitTaskComplete,context);
                    }
                }
                ((BaseActivity)context).dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.submitFail,context);
                ((BaseActivity)context).dismissCustomDialog();
            }
        });
    }
}
