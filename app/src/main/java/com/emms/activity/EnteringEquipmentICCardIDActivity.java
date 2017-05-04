package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Equipment;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/22.
 *
 */
public class EnteringEquipmentICCardIDActivity extends NfcActivity implements View.OnClickListener {
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private TextView menuSearchTitle;
    private EditText searchBox,iccard_id;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private Context context=this;
    private ArrayList<ObjectElement> EquipmentList=new ArrayList<>();
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
    findViewById(R.id.filter).setVisibility(View.VISIBLE);
    findViewById(R.id.filter).setOnClickListener(this);
    ((ImageView)findViewById(R.id.filter)).setImageResource(R.mipmap.sync);
    //findViewById(R.id.equipment_id_scan).setOnClickListener(this);
    findViewById(R.id.iccard_scan).setOnClickListener(this);
    equipment_id=(DropEditText)findViewById(R.id.equipment_id);
    iccard_id=(EditText)findViewById(R.id.iccard_id);
    findViewById(R.id.iccard_scan).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent it = new Intent(EnteringEquipmentICCardIDActivity.this, com.zxing.android.CaptureActivity.class);
            startActivityForResult(it, 1);
        }
    });
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

            checkICCardID(iccardID);
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
            case R.id.filter:{
                getDBDataLastUpdateTime();
                break;
            }

        }
    }
    private void submitEquipmentData(){
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        if(equipment_id.getText().equals("")){
            ToastUtil.showToastShort(R.string.pleaseInputEquipmentNum,this);
            return;
        }
        if(iccard_id.getText().toString().equals("")){
            ToastUtil.showToastShort(R.string.pleaseScanICcardNum,this);
            return;
        }
        showCustomDialog(R.string.submitData);
        submitData.set(Equipment.EQUIPMENT_ID,
                SelectItem);
        submitData.set("ICCardID",iccard_id.getText().toString());
        params.putJsonParams(submitData.toJson());
        HttpUtils.postWithoutCookie(this, "BaseDataAPI/ModifyEquipmentBang", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if(t!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()){
                                ToastUtil.showToastShort(R.string.submitSuccess,context);
                            }else {
                                if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()){
                                    ToastUtil.showToastShort(R.string.submit_Fail, context);
                                }else {
                                    TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                }
                            }
                        }
                    });
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
    private void initData(){
        String rawQuery="select AssetsID,Equipment_ID,ifnull(ICCardID,'') ICCardID from Equipment where AssetsID not null order by ICCardID asc";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement element) {
                if(element!=null) {
                    if(element.asArrayElement().size()>0){
                    EquipmentList.clear();
                        for (int i=0;i<element.asArrayElement().size();i++){
                            if(element.asArrayElement().get(i).asObjectElement().get(Equipment.ASSETSID)!=null&&
                                    !DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(Equipment.ASSETSID)).equals("")) {
                                if(!DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("ICCardID")).isEmpty()) {
                                    element.asArrayElement().get(i).asObjectElement().set("AssetsID",
                                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("AssetsID"))
                                                    +getString(R.string.IsBinding)  );
                                }
                                EquipmentList.add(element.asArrayElement().get(i).asObjectElement());
                            }
                        }
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
                String itemNam = mResultAdapter.getItemName();
                SelectItem=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    if(!DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("ICCardID")).equals("")){
                                        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                                        dialog.setMessage(R.string.ThisEquipmentIsBinding);
                                        dialog.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                SelectItem="";
                                                dialog.dismiss();
                                            }
                                        }).setPositiveButton(R.string.sure,new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        equipment_id.getmEditText().setText(searchResult);
                                                    }
                                                });
                                            }
                                        });
                                        dialog.show();
                                    }else {
                                        equipment_id.getmEditText().setText(searchResult);
                                    }
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
        initDropSearchView(null, equipment_id.getmEditText(), context.getResources().
                        getString(R.string.work_num_dialog), Equipment.ASSETSID,
                1, R.string.getDataFail,equipment_id.getDropImage());
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
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final int tips,ImageView imageView){
        subEditText.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DropSearch(condition,
                                        searchTitle,searchName,searTag ,tips);
                                }
        });
       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               DropSearch(condition,
                       searchTitle,searchName,searTag ,tips);
           }
       });
    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final int tips){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 1:
                if (data != null)
                {
                    final String result = data.getStringExtra("result");
                    if (result != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkICCardID(result);
                            }
                        });
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void checkICCardID(final String IC_Card_ID){
        String rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.ICCardID ='" + IC_Card_ID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
        getSqliteStore().performRawQuery(rawQuery, EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, new StoreCallback() {
            @Override
            public void success(final DataElement element, String resource) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                            if(!isFinishing()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                String message = getString(R.string.ICCardIsBinding)
                                        + "\n" + getString(R.string.equipment_name) + DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get(Equipment.EQUIPMENT_NAME))
                                        + "\n" + getString(R.string.equipment_num) + DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get(Equipment.ASSETSID));
                                builder.setMessage(message);
                                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }else {
                            iccard_id.setText(IC_Card_ID);
                        }
                    }
                });
            }
            @Override
            public void failure(DatastoreException ex, String resource) {
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         iccard_id.setText(IC_Card_ID);
                     }
                 });
            }
        });
    }
}
