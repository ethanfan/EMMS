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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/25.
 */
public class SummaryActivity extends BaseActivity{
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
        getSummaryFromServer();
        initView();
        initSearchView();

    }
    public void initView(){
        //initTopToolbar
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.fault_summary);
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
                    Intent intent=new Intent(context,CommandActivity.class);
                    intent.putExtra("TaskComplete",true);
                    intent.putExtra("TaskDetail",TaskDetail.toString());
                    startActivity(intent);
                }
            });
        }
    }
    private void initSearchView() {
        mDrawer_layout = (CustomDrawerLayout) findViewById(R.id.search_page);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer_layout.setBackgroundColor(Color.parseColor("#00000000"));
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
                        getString(R.string.faultType), DataDictionary.DATA_CODE,
                1, "获取数据失败");
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

                );
    }
    private void submitFaultSummaryToServer(){
        showCustomDialog(R.string.submitData);
      HttpParams httpParams=new HttpParams();
        JsonObjectElement  FaultSummary=new JsonObjectElement();
        FaultSummary.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));

        //如果存在TaskTrouble_ID则填对应，否则填0
        FaultSummary.set("TaskTrouble_ID",0);
       // FaultSummary.set("TroubleType",type.getText().toString());
        FaultSummary.set("TroubleType","1234");
        FaultSummary.set("TroubleDescribe",description.getText().toString());
        FaultSummary.set("MaintainDescribe",repair_status.getText().toString());
        httpParams.putJsonParams(FaultSummary.toJson());
        HttpUtils.post(this, "TaskTrouble", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
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
            FaultSummary.set("TaskTrouble_ID",0);
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
                    type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                    description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                    repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                }
            });
        }
    }
}
