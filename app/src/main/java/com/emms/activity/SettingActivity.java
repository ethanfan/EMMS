package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.schema.DataDictionary;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.BuildConfig;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/10.
 *
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
    private ArrayList<ObjectElement> FactoryList=new ArrayList<>();
    private ArrayList<ObjectElement> NetWorkList=new ArrayList<>();
    private ArrayList<ObjectElement> LanguageList=new ArrayList<>();
    private DropEditText Factory,NetWork,Language;
    private final int FACTORY_SETTING=1;
    private final int NETWORK_SETTING=2;
    private final int LANGUAGE_SETTING=3;
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
      //  findViewById(R.id.comfirm).setOnClickListener(this);
        ((TextView)findViewById(R.id.factory_tag)).setText(R.string.factory);
        ((TextView)findViewById(R.id.NetWork_tag)).setText(R.string.network);
        ((TextView)findViewById(R.id.Language_tag)).setText(R.string.LanguageSetting);
       // ((Button)findViewById(R.id.comfirm)).setText(R.string.sure);
        Factory=(DropEditText)findViewById(R.id.factory);
        NetWork=(DropEditText)findViewById(R.id.NetWork);
        Language=(DropEditText)findViewById(R.id.Language);
        Factory.getmEditText().setHint(R.string.select);
        NetWork.getmEditText().setHint(R.string.select);
        Language.getmEditText().setHint(R.string.select);
        String currentLanguage = "";
        LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(this);
        if (language == LocaleUtils.SupportedLanguage.ENGLISH) {
            currentLanguage = getString(R.string.english);
        } else if (language == LocaleUtils.SupportedLanguage.VIETNAMESE) {
            //currentLanguage = getString(R.string.vietnamese);
        } else {
            currentLanguage = getString(R.string.chinese);
        }
        Language.getmEditText().setText(currentLanguage);
        if(SharedPreferenceManager.getNetwork(context)!=null  &&
                 SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")){
            NetWork.getmEditText().setText(R.string.innerNetWork);
        }else {
            NetWork.getmEditText().setText(R.string.outerNetWork);
        }
        if(SharedPreferenceManager.getFactory(context)!=null){
            Factory.getmEditText().setText(SharedPreferenceManager.getFactory(context));
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
//            case R.id.comfirm:{
//                submitEquipmentData();
//                break;
//            }
//            case R.id.equipment_id_scan:{
//                break;
//            }

        }
    }
    private void submitEquipmentData(){
        if(Factory.getText().equals("")){
            ToastUtil.showToastShort(R.string.pleaseSelectFactory,this);
            return;
        }
        SharedPreferenceManager.setFactory(this,Factory.getText());
        ToastUtil.showToastShort(R.string.setting_su,this);
        finish();
    }
    private void initData(){
        initFactory();
        initNewWork();
        LanguageList.clear();
        JsonObjectElement language1=new JsonObjectElement();
        language1.set(DataDictionary.DATA_CODE,"zh");
        language1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.chinese));
        JsonObjectElement language2=new JsonObjectElement();
        language2.set(DataDictionary.DATA_CODE,"en");
        language2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.english));
        LanguageList.add(language1);
        LanguageList.add(language2);
    }
    private void initFactory(){
        String rawQuery="select * from BaseOrganise where  OrganiseType = 1";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement element) {
                if(element!=null) {
                    if(element.isArray()&&element.asArrayElement().size()>0){
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
    private void initNewWork(){
        NetWorkList.clear();
        JsonObjectElement json1=new JsonObjectElement();
        json1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.innerNetWork));
        json1.set(DataDictionary.DATA_CODE,"InnerNetwork");
        JsonObjectElement json2=new JsonObjectElement();
        json2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.outerNetWork));
        json2.set(DataDictionary.DATA_CODE,"OuterNetwork");
        NetWorkList.add(json1);
        NetWorkList.add(json2);
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
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case FACTORY_SETTING:
                                    Factory.getmEditText().setText(searchResult);
                                    SharedPreferenceManager.setFactory(context,searchResult);
                                    break;
                                case NETWORK_SETTING:
                                    NetWork.getmEditText().setText(searchResult);
                                    SharedPreferenceManager.setNetwork(context,
                                            DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE)));
                                    BuildConfig.NetWorkSetting(context);
                                    break;
                                case LANGUAGE_SETTING:
                                    Language.getmEditText().setText(searchResult);
                                    SharedPreferenceManager.setLanguageChange(context,true);
                                    LocaleUtils.SupportedLanguage language;
                                    if(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE)).equals("en")){
                                        language= LocaleUtils.SupportedLanguage.ENGLISH;

                                    }else {
                                        language= LocaleUtils.SupportedLanguage.CHINESE_SIMPLFIED;
                                    }
                                    //SharedPreferenceManager.setLanguage(context,language.toString());
                                    LocaleUtils.setLanguage(SettingActivity.this, language);
                                    SharedPreferenceManager.setLanguageChange(SettingActivity.this,true);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showCustomDialog(R.string.ChangeLanguagePleaseWait);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    initView();
                                                    initData();
                                                    initSearchView();
                                                    dismissCustomDialog();
                                                }
                                            },2000);
                                        }
                                    });
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
        initDropSearchView(null, Factory.getmEditText(), context.getResources().
                        getString(R.string.factoryTitle),"OrganiseName",
                FACTORY_SETTING, R.string.getDataFail,Factory.getDropImage());
        initDropSearchView(null,NetWork.getmEditText(),context.getResources().getString(R.string.networkTitle),DataDictionary.DATA_NAME,
                NETWORK_SETTING,R.string.getDataFail,NetWork.getDropImage());
        initDropSearchView(null,Language.getmEditText(),context.getResources().getString(R.string.LanguageTitle),DataDictionary.DATA_NAME,
                LANGUAGE_SETTING,R.string.getDataFail,Language.getDropImage());
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

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final int tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag) {
                    case FACTORY_SETTING:{
                        searchDataLists.addAll(FactoryList);
                        break;
                    }
                    case NETWORK_SETTING:{
                        searchDataLists.addAll(NetWorkList);
                        break;
                    }
                    case LANGUAGE_SETTING:{
                        searchDataLists.addAll(LanguageList);
                        break;
                    }
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
//    private void initLanguageSelect() {
//        View languageSelectView = new View(this);
//        TextView languageView = new TextView(this);
//        languageSelectView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                int selectedNumber = 0;
//                LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(SettingActivity.this);
//                if (language == LocaleUtils.SupportedLanguage.ENGLISH) {
//                    selectedNumber = 1;
//                } else if (language == LocaleUtils.SupportedLanguage.VIETNAMESE) {
//                    selectedNumber = 2;
//                } else {
//                    selectedNumber = 0;
//                }
//                openLanguageDialog(selectedNumber);
//            }
//
//        });
//        String currentLanguage = "";
//        LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(this);
//        if (language == LocaleUtils.SupportedLanguage.ENGLISH) {
//            currentLanguage = getString(R.string.english);
//        } else if (language == LocaleUtils.SupportedLanguage.VIETNAMESE) {
//            //currentLanguage = getString(R.string.vietnamese);
//        } else {
//            currentLanguage = getString(R.string.chinese);
//        }
//        languageView.setText(currentLanguage);
//    }

}
