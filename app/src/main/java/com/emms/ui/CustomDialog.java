package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;

/** 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class CustomDialog extends Dialog {
    private CustomDialog dialog=this;
    private Context context;
    private EditText work_num,approved_working_hours;
    private TextView work_name,work_description;
    private TextView comfirm_button;
    private DropEditText sub_task_equipment_num;
    private RelativeLayout relativelayout;
    private RelativeLayout IknowButtonLayout;
    public CustomDialog(Context context, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        initview();
    }
public void initview(){
    work_num=(EditText)findViewById(R.id.work_num);//添加情况下用户输入，修改情况下获取
    approved_working_hours=(EditText)findViewById(R.id.approved_working_hours);//根据work_num从数据库中查出
    work_name=(TextView)findViewById(R.id.work_name);//根据work_num从数据库中查出
    work_description=(TextView)findViewById(R.id.work_description);//根据work_num从数据库中查出
    sub_task_equipment_num=(DropEditText)findViewById(R.id.sub_task_equipment_num);//机台号，用列表中选择，列表数据从任务详细列表中传入
    comfirm_button=(TextView)findViewById(R.id.comfirm);//确定按钮，提交信息
    comfirm_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            comfirm_button_event();
        }
    });
}
  public void comfirm_button_event(){
        if(work_num.getText()==null&&approved_working_hours.getText()==null&&sub_task_equipment_num.getText()==null){
            //判断数据为空，提示用户数据不能为空，拒绝提交
            Toast.makeText(context,"请输入数据",Toast.LENGTH_LONG).show();
            return;
        }else {
            submitSubTaskData();
        }
    }
    public void submitSubTaskData(){
        HttpParams params=new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        //jsonObjectElement.set(Task.TASK_ID,);
       // jsonObjectElement.set();
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(context, "TaskItem", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }
}