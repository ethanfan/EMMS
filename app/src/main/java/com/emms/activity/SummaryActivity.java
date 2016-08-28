package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
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
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.ListenableFuture;

import net.minidev.json.JSONUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */
public class SummaryActivity extends NfcActivity{
    private DropEditText type;
    private EditText description,repair_status;
    private Button comfirm;
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
    private ArrayList<ObjectElement> typeList=new ArrayList<ObjectElement>();
    private ObjectElement TaskDetail;
    private boolean TaskComplete=false;
    private String TaskTrouble_ID="";
    private String TaskClass=Task.REPAIR_TASK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
        if(getIntent().getStringExtra(Task.TASK_CLASS)!=null){
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);}
        getSummaryFromServer();
        initView();
        initSearchView();
    }
    public void initView(){
        //initTopToolbar
        if(TaskClass.equals(Task.REPAIR_TASK)){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.fault_summary);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.task_summary);
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
        comfirm=(Button)findViewById(R.id.comfirm);
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
                                    type.getmEditText().setText(searchResult);
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
        getFaultType();
        initDropSearchView(null, type.getmEditText(), context.getResources().
                        getString(R.string.faultType), DataDictionary.DATA_NAME,
                1, "获取数据失败",type.getDropImage());
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
            if (searchDataLists.get(i).get(tagString).valueAsString().toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips,final ImageView imageView){
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
        FaultSummary.set("TroubleType",type.getText().toString());
        FaultSummary.set("TroubleDescribe",description.getText().toString());
        FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
        httpParams.putJsonParams(FaultSummary.toJson());
        HttpUtils.post(this, "TaskTrouble", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get(Data.SUCCESS)!=null&&jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                        dismissCustomDialog();
                        ToastUtil.showToastLong(R.string.submitSuccess,context);
                        if (TaskComplete) {
//                            Intent intent = new Intent(context, CommandActivity.class);
//                            intent.putExtra("TaskComplete", true);
//                            intent.putExtra("TaskDetail", TaskDetail.toString());
//                            startActivity(intent);
                            TaskComplete();
                        }else{
                            finish();
                        }
                    }else {
                        ToastUtil.showToastLong(R.string.submit_Fail,context);
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                ToastUtil.showToastLong(R.string.submitFail,context);
            }
        });
    }

    private void getSummaryFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        httpParams.put("task_id", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpUtils.get(this, "TaskTroubleContent", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    setFaultData(t);
                 dismissCustomDialog();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JsonObjectElement FaultSummary=new JsonObjectElement();
            FaultSummary.set("TaskTrouble_ID",TaskTrouble_ID);
            FaultSummary.set("TroubleType",type.getText().toString());
            FaultSummary.set("TroubleDescribe",description.getText().toString());
            FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
            outPersistentState.putString("submitData",FaultSummary.toJson());
            outPersistentState.putString("TaskDetail",TaskDetail.toJson());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }
    private void getFaultType(){

       // typeList
    }
    private void setFaultData(String data){
        JsonObjectElement jsonObjectElement=new JsonObjectElement(data);
        if(jsonObjectElement!=null&&jsonObjectElement.get("PageData").asArrayElement().size()>0){
           final ObjectElement faultData=jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TaskTrouble_ID= DataUtil.isDataElementNull(faultData.get("TaskTrouble_ID"));
                    type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                    description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                    repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                }
            });
        }
    }
    private void initData(){
        String sql="select * from DataDictionary where DataType='EquipmentTroubleSort'";
        getSqliteStore().performRawQuery(sql, "DataDictionary", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for(int i=0;i<element.asArrayElement().size();i++){
                        typeList.add(element.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {

            }
        });
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void TaskComplete(){
        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        params.putJsonParams(data.toJson());
        HttpUtils.post(this, "TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement!=null&&jsonObjectElement.get("Success")!=null&&
                            jsonObjectElement.get("Success").valueAsBoolean()){
                        ToastUtil.showToastLong("任务完成",context);
                        startActivity(new Intent(context,CusActivity.class));
                    }else {
                        ToastUtil.showToastLong("无法提交任务完成，请检查任务信息",context);
                    }
                }}

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.submitFail,context);
            }
        });
    }
    private void DropSearch(final EditText condition,
               final String searchTitle,final String searchName,final int searTag ,final String tips){
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
