package com.emms.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.emms.adapter.ResultListAdapter;
import com.emms.bean.WorkInfo;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.schema.Team;
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
    private EditText  approved_working_hours;
    private TextView work_name, work_description;
    private TextView comfirm_button;
    private DropEditText work_num,sub_task_equipment_num;
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    private ArrayList<ObjectElement> taskEquipment;
    private final String DATA_KEY_WORK_INFO = "workInfo";

    private static final int MSG_UPDATE_WORK_INFO = 10;
    private ObjectElement modifySubTask=null;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private String TaskId;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private DrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> workNumList=new ArrayList<ObjectElement>();
    public CustomDialog(Context context, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        initview();
        setCanceledOnTouchOutside(true);
        initSearchView();
    }
    public CustomDialog(Context context, int layout, int style,ObjectElement objectElement,ArrayList<ObjectElement> list) {
        super(context, style);
        this.context = context;
        this.modifySubTask=objectElement;
        this.taskEquipment=list;
        setContentView(layout);
        initview();
        setCanceledOnTouchOutside(true);
        initSearchView();
    }

    public void initview() {

      //  ((ViewGroup)findViewById(R.id.viewGroup)).setVisibility(View.GONE);
        findViewById(R.id.dismissView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        work_num = (DropEditText) findViewById(R.id.work_num);//添加情况下用户输入，修改情况下获取
        approved_working_hours = (EditText) findViewById(R.id.approved_working_hours);//根据work_num从数据库中查出
        work_name = (TextView) findViewById(R.id.work_name);//根据work_num从数据库中查出
        work_description = (TextView) findViewById(R.id.work_description);//根据work_num从数据库中查出
        sub_task_equipment_num = (DropEditText) findViewById(R.id.sub_task_equipment_num);//机台号，用列表中选择，列表数据从任务详细列表中传入
        comfirm_button = (TextView) findViewById(R.id.comfirm);//确定按钮，提交信息
        //若为修改状态，则有初始数据
        if(modifySubTask!=null){
            work_num.setText(DataUtil.isDataElementNull(modifySubTask.get("TaskItem_ID")));//待修改
           approved_working_hours.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkTime")));
            sub_task_equipment_num.setText(DataUtil.isDataElementNull(modifySubTask.get("Equipment_ID")));
            work_name.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkName")));
            work_description.setText(DataUtil.isDataElementNull(modifySubTask.get("DataDescr")));
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
        //setViewData();
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
        //如果是修改任务，传子任务ID,若添加子任务,传子任务ID=0
        if(modifySubTask==null){
        jsonObjectElement.set("TaskItem_ID",0);}
        else {
            jsonObjectElement.set("TaskItem_ID",modifySubTask.get("TaskItem_ID").toString());
        }
        jsonObjectElement.set(Task.TASK_ID,TaskId);

     //   jsonObjectElement.set("TaskItemName",work_name.getText().toString());
     //    jsonObjectElement.set("TaskItemDesc",work_description.getText().toString());
        jsonObjectElement.set("TaskItem_ID",0);
        // jsonObjectElement.set("Equipment_ID",sub_task_equipment_num.getText().toString());
        jsonObjectElement.set("Equipment_ID","124124");
        jsonObjectElement.set("WorkTimeCode",work_num.getText().toString());
        jsonObjectElement.set("PlanManhour",approved_working_hours.getText().toString());
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
        if(modifySubTask!=null){
            work_num.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkCode")));
            approved_working_hours.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkTime")));
            work_name.setText(DataUtil.isDataElementNull(modifySubTask.get("WorkName")));
            work_description.setText(DataUtil.isDataElementNull(modifySubTask.get("DataDescr")));
            sub_task_equipment_num.setText(DataUtil.isDataElementNull(modifySubTask.get("Equipment_ID")));
        }
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
    public void setData(ObjectElement objectElement){
        modifySubTask=objectElement;
    }
    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public ArrayList<ObjectElement> getTaskEquipment() {
        return taskEquipment;
    }

    public void setTaskEquipment(ArrayList<ObjectElement> taskEquipment) {
        this.taskEquipment = taskEquipment;
    }


    private void initSearchView() {
        initWorkNumListData();
        mDrawer_layout = (DrawerLayout) findViewById(R.id.search_page);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        searchBox = (EditText) findViewById(R.id.et_search);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    work_num.getmEditText().setText(searchResult);
                                    break;
                                case 2:
                                    sub_task_equipment_num.getmEditText().setText(searchResult);
                                    break;
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    Toast.makeText(context, "出错了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        initDropSearchView(null, work_num.getmEditText(), context.getResources().
                        getString(R.string.work_num), DataDictionary.DATA_CODE,
                1, "获取数据失败");
        initDropSearchView(null, sub_task_equipment_num.getmEditText(), context.getResources().
                        getString(R.string.title_search_equipment_nun), Equipment.EQUIPMENT_ID,
                2, "获取数据失败");
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                clearBtn.setVisibility(View.VISIBLE);
                mResultListView.setVisibility(View.VISIBLE);
                String itemName = mResultAdapter.getItemName();
                ArrayList<ObjectElement> result = search(keyword, itemName);
                if (result == null || result.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    mResultAdapter.changeData(result, itemName);
                }
            }
        });


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
            }
        });
    }
    private ArrayList<ObjectElement> search(String keyword,String  tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (searchDataLists.get(i).get(tagString).valueAsString().toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((Activity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        searchDataLists.clear();
                                        switch (searTag){
                                            case 1:
                                                searchDataLists.addAll(workNumList);
                                                break;
                                            case 2:
                                                searchDataLists.addAll(taskEquipment);
                                                break;

                                        }
                                        searchtag = searTag;
                                                if (condition != null) {
                                                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                                                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                                                        mResultAdapter.changeData(searchDataLists, searchName);
                                                        menuSearchTitle.setText(searchTitle);
                                                        menuSearchTitle.postInvalidate();
                                                        mDrawer_layout.postInvalidate();

                                                    } else {
                                                        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    if (searchDataLists.size() > 0) {
                                                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                                                        mResultAdapter.changeData(searchDataLists, searchName);
                                                        menuSearchTitle.setText(searchTitle);
                                                        menuSearchTitle.postInvalidate();
                                                        mDrawer_layout.postInvalidate();

                                                    } else {
                                                        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
                                                    }
                                                }



                                    }
                                });

                            }
                        }

                );
    }
    private void initWorkNumListData(){
        try {
            String rawQuery = "select * from DataDictionary where DataType='WorkTime' And DataCode like 'OHZK%' order by Data_ID asc";
            ListenableFuture<DataElement> elemt = ((AppAplication) ((Activity)context).getApplication()).getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {
                @Override
                public void onSuccess(DataElement dataElement) {
                    workNumList.clear();
                    if (dataElement != null && dataElement.isArray()
                            && dataElement.asArrayElement().size() > 0) {
                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
                            workNumList.add(dataElement.asArrayElement().get(i).asObjectElement());
                        }
                    } else {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "目前该设备没有工作编号", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "目前该设备没有工作编号", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}