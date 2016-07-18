package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.R;
import com.emms.activity.AppAplication;
import com.emms.bean.WorkInfo;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class CustomDialog extends Dialog {
    private CustomDialog dialog = this;
    private Context context;
    private EditText work_num, approved_working_hours;
    private TextView work_name, work_description;
    private TextView comfirm_button;
    private DropEditText sub_task_equipment_num;
    private RelativeLayout relativelayout;
    private RelativeLayout IknowButtonLayout;

    private Map<String, Object> dataMap = new HashMap<String, Object>();
    private final String DATA_KEY_WORK_INFO = "workInfo";

    private static final int MSG_UPDATE_WORK_INFO = 10;
    private ObjectElement modifySubTask=null;


    public CustomDialog(Context context, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        initview();
        setCanceledOnTouchOutside(true);
    }

    public void initview() {
        work_num = (EditText) findViewById(R.id.work_num);//添加情况下用户输入，修改情况下获取
        approved_working_hours = (EditText) findViewById(R.id.approved_working_hours);//根据work_num从数据库中查出
        work_name = (TextView) findViewById(R.id.work_name);//根据work_num从数据库中查出
        work_description = (TextView) findViewById(R.id.work_description);//根据work_num从数据库中查出
        sub_task_equipment_num = (DropEditText) findViewById(R.id.sub_task_equipment_num);//机台号，用列表中选择，列表数据从任务详细列表中传入
        comfirm_button = (TextView) findViewById(R.id.comfirm);//确定按钮，提交信息
        //若为修改状态，则有初始数据
        if(modifySubTask!=null){
           // work_num.setText(DataUtil.isDataElementNull(modifySubTask.get()));
        }

        work_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    //？禁用标准工时输入框
                } else {
                    // 此处为失去焦点时的处理内容
                    setWorkInfo(work_num.getText().toString());
                }


            }
        });
        comfirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirm_button_event();
            }
        });
        //修改子任务的情况下调用
        setViewData();
    }

    public void comfirm_button_event() {
        if (work_num.getText()==null
                ||approved_working_hours.getText()==null||
                work_num.getText().toString().trim().equals("") ||
                approved_working_hours.getText().toString().trim().equals("")) {
            //判断数据为空，提示用户数据不能为空，拒绝提交
            Toast.makeText(context, "请输入数据", Toast.LENGTH_LONG).show();
            return;
        } else {
            submitSubTaskData();
        }
    }

    public void submitSubTaskData() {
        HttpParams params = new HttpParams();
        JsonObjectElement jsonObjectElement = new JsonObjectElement();
        //jsonObjectElement.set(Task.TASK_ID,);
        //jsonObjectElement.set(TaskItemName,);
        // jsonObjectElement.set(TaskItemDesc,);
        // jsonObjectElement.set(Equipment_ID,);
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

    public void setViewData() {

    }

    private void setWorkInfo(String workCode) {

        ListenableFuture<DataElement> elemt = getWorkInfoByWorkCode(workCode);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                System.out.println(element);
                ArrayList<ObjectElement> workInfolist = new ArrayList<ObjectElement>();

                try {

                    if (element != null && element.isArray()
                            && element.asArrayElement().size() > 0) {
                        workInfolist.clear();
                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                            workInfolist.add(element.asArrayElement().get(i)
                                    .asObjectElement());
                        }
                    } else {
                        //Toast.makeText(context, "程序数据库出错", Toast.LENGTH_SHORT).show();

                    }

                    WorkInfo workInfo = new WorkInfo();
                    if (null != workInfolist && !workInfolist.isEmpty()) {
                        ObjectElement dataElement = workInfolist.get(0);
                        JsonObjectElement jsonObjectElement = new JsonObjectElement(dataElement.toJson());

                        workInfo.setWorkCode(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_CODE)));
                        workInfo.setWorkName(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                        workInfo.setApprovedWorkingHours(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_VALUE1)));
                        workInfo.setWorkDescr(nullToEmptyString(jsonObjectElement.get(DataDictionary.DATA_DESCR)));
                    }

                    dataMap.put(DATA_KEY_WORK_INFO, workInfo);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_WORK_INFO, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }

    //主线程中的handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {
                case MSG_UPDATE_WORK_INFO: {
                    updateWorkInfoView();
                    break;
                }

            }
        }

    };

    public String nullToEmptyString(DataElement o) {
        if (null == o || o.isNull()) {
            return "";
        }
        return o.valueAsString();
    }

    private <T> T getDataFromDataMap(String key, Class<T> cls) {
        Object valueObj = dataMap.get(key);

        T retData = null;
        if (null != valueObj) {
            retData = (T) valueObj;
        }

        return retData;
    }

    private void updateWorkInfoView() {
        WorkInfo workInfo = getDataFromDataMap(DATA_KEY_WORK_INFO, WorkInfo.class);
        if (null == workInfo) {
            return;
        }

//        work_num.setText(workInfo.getWorkCode());
        approved_working_hours.setText(workInfo.getApprovedWorkingHours());
        work_name.setText(workInfo.getWorkName());
        work_description.setText(workInfo.getWorkDescr());
    }

    //根据工作编号取工作信息
    private ListenableFuture<DataElement> getWorkInfoByWorkCode(String workCode) {
        // SqliteStore sqliteStore =  ((AppAplication) getApplication()).getSqliteStore();

        SqliteStore sqliteStore = ((AppAplication) context.getApplicationContext()).getSqliteStore();

        String rawQuery = "select DataCode,DataName,DataDescr,DataValue1 from Datadictionary where DataCode =" + "'" + workCode + "'";
        ListenableFuture<DataElement> elemt = sqliteStore.performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        return elemt;
    }
    private void setData(ObjectElement objectElement){
        modifySubTask=objectElement;
    }

}