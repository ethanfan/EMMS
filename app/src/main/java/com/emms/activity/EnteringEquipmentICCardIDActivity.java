package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.ui.ChangeEquipmentDialog;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.ui.ExpandGridView;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.ui.ScrollViewWithListView;
import com.emms.util.AnimateFirstDisplayListener;
import com.emms.util.Bimp;
import com.emms.util.DataUtil;
import com.emms.util.FileUtils;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaffer.deng on 2016/6/22.
 */
public class EnteringEquipmentICCardIDActivity extends NfcActivity implements View.OnClickListener {
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private String TaskId;
    private TextView menuSearchTitle;
    private EditText searchBox,iccard_id;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private Context context=this;
    private ArrayList<ObjectElement> EquipmentList=new ArrayList<ObjectElement>();
    private DropEditText equipment_id;
    private String SelectItem="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_equipment_iccard);
        initView();
        initData();
        initSearchView();
    }
private void initView(){
    ((TextView)findViewById(R.id.tv_title)).setText(R.string.entering_equipment);
    findViewById(R.id.btn_right_action).setOnClickListener(this);
    findViewById(R.id.comfirm).setOnClickListener(this);
    //findViewById(R.id.equipment_id_scan).setOnClickListener(this);
    findViewById(R.id.iccard_scan).setOnClickListener(this);
    equipment_id=(DropEditText)findViewById(R.id.equipment_id);
    iccard_id=(EditText)findViewById(R.id.iccard_id);
}

    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
            iccard_id.setText(iccardID);

//            MessageUtils.showToast(iccardID,this);
        }
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
            case R.id.iccard_scan:{
                break;
            }

        }
    }
    private void submitEquipmentData(){
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        if(equipment_id.getText().equals("")){
            ToastUtil.showToastLong(R.string.pleaseInputEquipmentNum,this);
            return;
        }
        if(iccard_id.getText().toString().equals("")){
            ToastUtil.showToastLong(R.string.pleaseScanICcardNum,this);
            return;
        }
        submitData.set(Equipment.EQUIPMENT_ID,
                SelectItem);
        submitData.set("ICCardID",iccard_id.getText().toString());
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(this, "Equipment", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                ToastUtil.showToastLong(R.string.submitSuccess,context);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.submitFail,context);
            }
        });
    }
    private void initData(){
        String rawQuery="select ifnull(OracleID,'')OracleID,Equipment_ID from Equipment";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement element) {
                if(element!=null) {
                    if(element.asArrayElement().size()>0){
                    EquipmentList.clear();
                        for (int i=0;i<element.asArrayElement().size();i++){
                    EquipmentList.add(element.asArrayElement().get(i).asObjectElement());}
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
                SelectItem=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    equipment_id.getmEditText().setText(searchResult);
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
        initDropSearchView(null, equipment_id.getmEditText(), context.getResources().
                        getString(R.string.work_num_dialog), Equipment.ORACLE_ID,
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
                                                searchDataLists.addAll(EquipmentList);
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

}
