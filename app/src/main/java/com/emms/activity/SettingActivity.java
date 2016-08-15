package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Equipment;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/10.
 */
public class SettingActivity extends NfcActivity implements View.OnClickListener{
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
    private Context context=this;
    private ArrayList<ObjectElement> FactoryList=new ArrayList<ObjectElement>();
    private DropEditText Factory;
    private String SelectItem="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
        initSearchView();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.setting);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.comfirm).setOnClickListener(this);
        Factory=(DropEditText)findViewById(R.id.factory);
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_right_action:{
                finish();
                break;
            }
            case R.id.comfirm:{
                submitEquipmentData();
                break;
            }
//            case R.id.equipment_id_scan:{
//                break;
//            }

        }
    }
    private void submitEquipmentData(){
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        if(Factory.getText().equals("")){
            ToastUtil.showToastLong(R.string.pleaseSelectFactory,this);
            return;
        }
        SharedPreferenceManager.setFactory(this,Factory.getText().toString());
        ToastUtil.showToastLong("设置成功",this);
        finish();

    }
    private void initData(){
        String rawQuery="select * from BaseOrganise where  OrganiseType = 1";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement element) {
                if(element!=null) {
                    if(element.asArrayElement().size()>0){
                        FactoryList.clear();
                        for (int i=0;i<element.asArrayElement().size();i++){
                            FactoryList.add(element.asArrayElement().get(i).asObjectElement());}
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }
    private void initSearchView() {
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
                String itemNam = mResultAdapter.getItemName();
                SelectItem= DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    Factory.getmEditText().setText(searchResult);
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
        initDropSearchView(null, Factory.getmEditText(), context.getResources().
                        getString(R.string.factoryTitle),"OrganiseName",
                1, "获取数据失败");
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
                // initData(s.toString());
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
        subEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                searchDataLists.clear();
                                switch (searTag) {
                                    case 1:{
                                        searchDataLists.addAll(FactoryList);
                                        break;
                                    }}
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
                });
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
