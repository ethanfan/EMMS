package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.ui.NFCDialog;
import com.emms.ui.TaskCompleteDialog;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/7/25.
 *
 */
public class SummaryActivity extends NfcActivity{
    private DropEditText type;
    private EditText description,repair_status;
    private Context context=this;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> typeList=new ArrayList<>();
    private ObjectElement TaskDetail;
    private boolean TaskComplete=false;
    private String TaskTrouble_ID="";
    private String TaskClass=Task.REPAIR_TASK;
    private String TroubleType;
    private HashMap<String,String> map=new HashMap<>();
    private NFCDialog nfcDialog;
    private boolean nfcDialogTag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
        if(getIntent().getStringExtra(Task.TASK_CLASS)!=null){
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        }
        initView();
        initSearchView();
        nfcDialog=new NFCDialog(context,R.style.MyDialog) {
            @Override
            public void dismissAction() {
                nfcDialogTag=false;
            }

            @Override
            public void showAction() {
                nfcDialogTag=true;
            }
        };
    }
    public void initView(){
        //initTopToolbar
        if(Task.REPAIR_TASK.equals(TaskClass)){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.fault_summary);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.task_summary);
            findViewById(R.id.ccccc).setVisibility(View.GONE);
            findViewById(R.id.layout2).setVisibility(View.GONE);
            findViewById(R.id.status_tag).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.description_tag)).setText(R.string.task_other_summary_tag);
        }
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        type=(DropEditText)findViewById(R.id.type);
        description=(EditText)findViewById(R.id.description);
        repair_status=(EditText)findViewById(R.id.repair_status);
        Button comfirm = (Button) findViewById(R.id.comfirm);
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFaultSummaryToServer();
            }
        });
        if(TaskComplete) {
            comfirm.setVisibility(View.GONE);
            findViewById(R.id.footer_toolbar).setVisibility(View.VISIBLE);
            //initFooterToolbar
            findViewById(R.id.preStep).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            findViewById(R.id.nextStep).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //待写
                    submitFaultSummaryToServer();

                }
            });
            ((Button)findViewById(R.id.nextStep)).setText(R.string.taskComplete);
        }
        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        findViewById(R.id.layout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repair_status.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
    }
    private void initSearchView() {
        initData();
        searchBox = (EditText) findViewById(R.id.et_search);
        mDrawer_layout = (CustomDrawerLayout) findViewById(R.id.search_page);
        mDrawer_layout.setCloseDrawerListener(new CloseDrawerListener() {
            @Override
            public void close() {
                searchBox.setText("");
            }
        });
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer_layout.setBackgroundColor(Color.parseColor("#00000000"));
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    type.getmEditText().setText(searchResult);
                                    TroubleType=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(R.string.error_occur,context);
                }
            }
        });
        initDropSearchView(null, type.getmEditText(), context.getResources().
                        getString(R.string.faultType), DataDictionary.DATA_NAME,
                1, R.string.getDataFail,type.getDropImage());
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer_layout.closeDrawer(Gravity.RIGHT);
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
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final int tips,final ImageView imageView){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DropSearch(condition,
                                searchTitle,searchName,searTag ,tips);
                            }
                        }
                );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropSearch(condition,
                        searchTitle,searchName,searTag ,tips);
            }
        });
    }
    private void submitFaultSummaryToServer(){
        if(TaskClass.equals(Task.REPAIR_TASK)) {
            if (type.getText().trim().equals("")) {
                ToastUtil.showToastShort(R.string.NoFaultSummaryType,context);
                return;
            }
            if(description.getText().toString().trim().equals("")){
                ToastUtil.showToastShort(R.string.NoFaultSummary,context);
                return;
            }
            if (repair_status.getText().toString().trim().equals("")) {
                ToastUtil.showToastShort(R.string.NoRepairStatus,context);
                return;
            }
        }else {
            if (description.getText().toString().trim().equals("")) {
                ToastUtil.showToastShort(R.string.NoTaskSummary, context);
                return;
            }
        }
      showCustomDialog(R.string.submitData);
      HttpParams httpParams=new HttpParams();
        JsonObjectElement  FaultSummary=new JsonObjectElement();
        FaultSummary.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));

        //如果存在TaskTrouble_ID则填对应，否则填0
        if(TaskTrouble_ID.equals("")){
        FaultSummary.set("TaskTrouble_ID",0);}
        else{
            FaultSummary.set("TaskTrouble_ID",Integer.valueOf(TaskTrouble_ID));
        }
       // FaultSummary.set("TroubleType",type.getText().toString());
        FaultSummary.set("TroubleType",TroubleType);
        FaultSummary.set("TroubleDescribe",description.getText().toString());
        FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
        httpParams.putJsonParams(FaultSummary.toJson());
        HttpUtils.post(this, "TaskTrouble", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    try{
                        JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                        if(jsonObjectElement.get(Data.SUCCESS)!=null&&jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                            dismissCustomDialog();
                            ToastUtil.showToastShort(R.string.submitSuccess,context);
                            if (TaskComplete) {
//                            Intent intent = new Intent(context, CommandActivity.class);
//                            intent.putExtra("TaskComplete", true);
//                            intent.putExtra("TaskDetail", TaskDetail.toString());
//                            startActivity(intent);
                                if(TaskClass!=null&&TaskClass.equals(Task.TRANSFER_MODEL_TASK)){
                                if(mAdapter!=null&&mAdapter.isEnabled()) {
                                    if (nfcDialog != null && !nfcDialog.isShowing()) {
                                        nfcDialog.show();
                                    }
                                }else {
                                    TaskCompleteDialog taskCompleteDialog=new TaskCompleteDialog(context,R.style.MyDialog);
                                    taskCompleteDialog.setTask_ID(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
                                    taskCompleteDialog.setTaskClass(TaskClass);
                                    taskCompleteDialog.show();
                                }}else {
                                    TaskComplete(null);
                                }
                            }else{
                                finish();
                            }
                        }else {
                            ToastUtil.showToastShort(R.string.submit_Fail,context);
                        }
                    }catch (Throwable throwable){
                        ToastUtil.showToastShort(R.string.AppError,context);
                        CrashReport.postCatchedException(throwable);
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                ToastUtil.showToastShort(R.string.submitFail,context);
            }
        });
    }

    private void getSummaryFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        httpParams.put("task_id", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpUtils.get(this, "TaskTroubleAPI/GetTaskTroubleList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
                        JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                        if(jsonObjectElement.get("PageData").isArray() && jsonObjectElement.get("PageData").asArrayElement().size()>0){
                            final ObjectElement faultData=jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TaskTrouble_ID= DataUtil.isDataElementNull(faultData.get("TaskTrouble_ID"));
                                    type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                    TroubleType=map.get(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                    description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                                    repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                                }
                            });
                        }
                    }catch (Throwable throwable){
                        CrashReport.postCatchedException(throwable);
                    }finally {
                        dismissCustomDialog();
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.loadingFail,context);
                dismissCustomDialog();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JsonObjectElement FaultSummary=new JsonObjectElement();
            FaultSummary.set("TaskTrouble_ID",TaskTrouble_ID);
            FaultSummary.set("TroubleType",type.getText());
            FaultSummary.set("TroubleDescribe",description.getText().toString());
            FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
            outPersistentState.putString("submitData",FaultSummary.toJson());
            outPersistentState.putString("TaskDetail",TaskDetail.toJson());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initData(){
        DataUtil.getDataFromDataBase(context, "EquipmentTroubleSort", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for(int i=0;i<element.asArrayElement().size();i++){
                        typeList.add(element.asArrayElement().get(i).asObjectElement());
                        map.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getSummaryFromServer();
                        }
                    });
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,context);
                       }
                   });
            }
        });
    }

    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
            if (nfcDialogTag) {
                showCustomDialog(R.string.submitData);
                HttpParams params = new HttpParams();
                JsonObjectElement submitData = new JsonObjectElement();
                submitData.set("ICCardID", iccardID);
                submitData.set(Task.TASK_ID, DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
                params.putJsonParams(submitData.toJson());
                HttpUtils.post(context, "TaskOperatorAPI/CheckUserRoleForICCardID", params, new HttpCallback() {
                    @Override
                    public void onSuccess(final String t) {
                        super.onSuccess(t);
                        dismissCustomDialog();
                        if (t != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                                    if (jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                                        ToastUtil.showToastShort(R.string.SuccessToCheckID, context);
                                        TaskComplete(jsonObjectElement.get(Data.PAGE_DATA));
                                    } else {
                                        ToastUtil.showToastShort(R.string.FailToCheckID, context);
                                    }
                                }
                            });
                        }
                        if (nfcDialog != null && nfcDialog.isShowing()) {
                            nfcDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        ToastUtil.showToastShort(R.string.FailToCheckIDCauseByTimeOut, context);
                        dismissCustomDialog();
                    }
                });
            }
        }
    }
    private void TaskComplete(final DataElement dataElement){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        params.putJsonParams(data.toJson());
        HttpUtils.post(this, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if(t!=null){
                    final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success")!=null&&
                            jsonObjectElement.get("Success").valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.taskComplete,context);
                        if(TaskClass!=null
                                &&TaskClass.equals(Task.TRANSFER_MODEL_TASK)
                                &&(jsonObjectElement.get("Tag")==null||"1".equals(DataUtil.isDataElementNull(jsonObjectElement.get("Tag"))))
                                ){//Tag为1即需要弹出对话框询问用户是否需要创建新任务
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setMessage(R.string.DoYouNeedToCreateACarMovingTask);
                            builder.setCancelable(false);
                            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent=new Intent(context, CusActivity.class);
                                    intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK,Constants.FLAG_CREATE_CAR_MOVING_TASK);
                                    if(dataElement!=null){
                                        intent.putExtra("OperatorInfo",dataElement.toString());
                                    }
                                    intent.putExtra("FromTask_ID",
                                            DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
                                    context.startActivity(intent);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    context.startActivity(new Intent(context,CusActivity.class));
                                }
                            });
                            builder.show();
                        }else {
                            startActivity(new Intent(context,CusActivity.class));
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()){
                                    ToastUtil.showToastShort(R.string.canNotSubmitTaskComplete,context);
//                                }else {
//                                    ToastUtil.showToastLong(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")),context);
//                                }
                            }
                        });
                    }
                }
            dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.submitFail,context);
                    }
                });
            }
        });
    }
    private void DropSearch(final EditText condition,
               final String searchTitle,final String searchName,final int searTag ,final int tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag){
                    case 1:
                        searchDataLists.addAll(typeList);
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
                        ToastUtil.showToastShort(tips,context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips,context);
                    }
                }

            }
        });
    }


    private void getTaskEquipmentFromServerByTaskId() {
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        params.put("task_id", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        params.put("pageSize",1000);
        params.put("pageIndex",1);
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        HttpUtils.get(context, "TaskDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    ArrayElement jsonArrayElement = new JsonArrayElement(t);
                    if(jsonArrayElement.size()==0){
                        initData();
                    }else {
                        initData();
                    }
                    dismissCustomDialog();
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                initData();
                dismissCustomDialog();
               // ToastUtil.showToastShort(R.string.FailGetEquipmentList,context);
            }
        });
    }
}
