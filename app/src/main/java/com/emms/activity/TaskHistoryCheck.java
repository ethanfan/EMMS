package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.fragment.AllTaskHistoryFragment;
import com.emms.fragment.FliterTaskHistoryFragment;
import com.emms.fragment.PendingCommandFragment;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/2.
 *
 */
public class TaskHistoryCheck extends NfcActivity implements View.OnClickListener,OnTabSelectListener {
    private Context mContext=this ;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private  String[] mTitles ;


    private TextView menuSearchTitle;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> taskClassList=new ArrayList<>();
    private ArrayList<ObjectElement> taskStatusList=new ArrayList<>();
    private ArrayList<ObjectElement> timeList=new ArrayList<>();
    private DropEditText task_class,task_status,time;
    private String taskClassCode="",taskStatusCode="",timeCode="";
    private ViewPager vp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history_check);
        if(!BaseData.setBaseData(mContext)){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            init();
                        }
                    });
                }
            },500);
        }else {
            init();
        }
        //taskStatusList=BaseData.getTaskStatus();
    }
    private void init(){
        mTitles =getResources().getStringArray(R.array.select_tab_task_history);
        for (int i =0;i< mTitles.length;i++) {
            if (i==0) {
                PendingCommandFragment pendingCommandFragment=PendingCommandFragment.newInstance(BaseData.getTaskClass(),BaseData.getTaskStatus());
                mFragments.add(pendingCommandFragment);
            }else if (i ==1){
                AllTaskHistoryFragment allTaskHistoryFragment=AllTaskHistoryFragment.newInstance(BaseData.getTaskClass(),BaseData.getTaskStatus());
                mFragments.add(allTaskHistoryFragment);
            }else if (i ==2){
                mFragments.add(FliterTaskHistoryFragment.newInstance(BaseData.getTaskClass(),BaseData.getTaskStatus()));
            }
        }
        View decorView = getWindow().getDecorView();
        vp = ViewFindUtils.find(decorView, R.id.vp);
        vp.setOffscreenPageLimit(2);
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        SlidingTabLayout tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
        tabLayout_2.setViewPager(vp);
        tabLayout_2.setOnTabSelectListener(this);
        //tabLayout_2.s

        initView();
        initSearchView();
        initData();
    }
    private  void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.taskHistory);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.search_filter).setOnClickListener(this);
        task_class=(DropEditText)findViewById(R.id.task_class) ;
        task_status=(DropEditText)findViewById(R.id.task_status) ;
        time=(DropEditText)findViewById(R.id.time) ;
        if(BaseData.getTaskClass().get(Task.REPAIR_TASK)!=null){
            task_class.getmEditText().setText(BaseData.getTaskClass().get(Task.REPAIR_TASK));
        }else {
        task_class.getmEditText().setText(R.string.repair);
        }
        taskClassCode=Task.REPAIR_TASK;
        time.getmEditText().setText(R.string.OneDay);
        timeCode="1";
        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.search_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                finish();
                break;
            }
            case R.id.filter:{
                if(findViewById(R.id.search_filter).getVisibility()==View.GONE||
                        findViewById(R.id.search_filter).getVisibility()==View.INVISIBLE){
                    //findViewById(R.id.search_filter).setVisibility(View.VISIBLE);
                    buttonAnim(true);
                }
                else {
                    //findViewById(R.id.search_filter).setVisibility(View.GONE);
                    buttonAnim(false);
                }
                break;
            }
            case R.id.search_filter:{
                break;
            }
            case R.id.search_button:{
                ((FliterTaskHistoryFragment)mFragments.get(2)).setData(taskClassCode,taskStatusCode,timeCode);
                ((FliterTaskHistoryFragment)mFragments.get(2)).getTaskHistory();
                vp.setCurrentItem(2,true);
                buttonAnim(false);
                break;
            }

        }
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
        mResultAdapter = new ResultListAdapter(mContext);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:{
                                    task_class.getmEditText().setText(searchResult);
                                    taskClassCode= DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;}

                                case 2:{
                                    task_status.getmEditText().setText(searchResult);
                                    taskStatusCode=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;
                                }
                                case 3:{
                                    time.getmEditText().setText(searchResult);
                                    timeCode=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("Time"));
                                }
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(R.string.error_occur,mContext);
                }
            }
        });
        initDropSearchView(null, task_class.getmEditText(), mContext.getResources().
                        getString(R.string.title_search_task_type),DataDictionary.DATA_NAME,
                1, R.string.getDataFail,task_class.getDropImage());
        initDropSearchView(null, task_status.getmEditText(), mContext.getResources().
                        getString(R.string.task_s), DataDictionary.DATA_NAME,
                2, R.string.getDataFail,task_status.getDropImage());
        initDropSearchView(null, time.getmEditText(), mContext.getResources().
                        getString(R.string.title_time),DataDictionary.DATA_NAME,
                3, R.string.getDataFail,time.getDropImage());
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
            final EditText condition, EditText subEditText,
            final String searchTitle, final String searchName, final int searTag , final int tips, ImageView imageView){
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
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag) {
                    case 1:{
                        searchDataLists.addAll(taskClassList);
                        break;
                    }
                    case 2:{
                        searchDataLists.addAll(taskStatusList);
                        break;}
                    case 3:{
                        searchDataLists.addAll(timeList);
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
                        ToastUtil.showToastShort(tips,mContext);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips,mContext);
                    }
                }
            }
        });
    }
    private void initData(){
        initTaskClassData();
        initTaskStatusData();
        intiTimeData();
    }
    private void initTaskClassData(){
          for(String key:BaseData.getTaskClass().keySet()){
              JsonObjectElement jsonObjectElement=new JsonObjectElement();
              jsonObjectElement.set(DataDictionary.DATA_CODE,key);
              jsonObjectElement.set(DataDictionary.DATA_NAME,BaseData.getTaskClass().get(key));
              if(!"T0301".equals(key)                 //过滤掉普通搬车，转款搬车，维护，搬车
                      &&!"T0302".equals(key)
                      &&!"T03".equals(key)
                      &&!Task.MAINTAIN_TASK.equals(key)){
              taskClassList.add(jsonObjectElement);
              }
          }
//        String rawQuery = "select * from DataDictionary " +
//                "where DataType = 'TaskClass' and PData_ID = 0";
//        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
//                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
//        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
//            @Override
//            public void onSuccess(DataElement element) {
//                if (element != null && element.isArray()
//                        && element.asArrayElement().size() > 0) {
//                    taskClassList.clear();
//                    for (int i = 0; i < element.asArrayElement().size(); i++) {
//                        taskClassList.add(element.asArrayElement().get(i).asObjectElement());
//                    }
//                } else {
//                    Toast.makeText(mContext, R.string.noData, Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                System.out.println(throwable.getMessage());
//            }
//        });
    }
    private void initTaskStatusData(){
//        JsonObjectElement jsonObjectElement0=new JsonObjectElement();
//        jsonObjectElement0.set(DataDictionary.DATA_NAME,getResources().getString(R.string.pending_orders));
//        jsonObjectElement0.set("Status",0);
//        JsonObjectElement jsonObjectElement1=new JsonObjectElement();
//        jsonObjectElement1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.start));
//        jsonObjectElement1.set("Status",1);
//        JsonObjectElement jsonObjectElement2=new JsonObjectElement();
//        jsonObjectElement2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.task_state_details_finish));
//        jsonObjectElement2.set("Status",2);
//        JsonObjectElement jsonObjectElement3=new JsonObjectElement();
//        jsonObjectElement3.set(DataDictionary.DATA_NAME,getResources().getString(R.string.cancel));
//        jsonObjectElement3.set("Status",3);
//        taskStatusList.add(jsonObjectElement0);
//        taskStatusList.add(jsonObjectElement1);
//        taskStatusList.add(jsonObjectElement2);
//        taskStatusList.add(jsonObjectElement3);
        for(String key:BaseData.getTaskStatus().keySet()){
            JsonObjectElement jsonObjectElement=new JsonObjectElement();
            jsonObjectElement.set(DataDictionary.DATA_CODE,key);
            jsonObjectElement.set(DataDictionary.DATA_NAME,BaseData.getTaskStatus().get(key));
            taskStatusList.add(jsonObjectElement);
        }
    }
    private void intiTimeData(){
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(DataDictionary.DATA_NAME,getResources().getString(R.string.OneDay));
        jsonObjectElement.set("Time",1);
        JsonObjectElement jsonObjectElement1=new JsonObjectElement();
        jsonObjectElement1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.TwoDay));
        jsonObjectElement1.set("Time",2);
        JsonObjectElement jsonObjectElement2=new JsonObjectElement();
        jsonObjectElement2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.OneWeek));
        jsonObjectElement2.set("Time",7);
        timeList.add(jsonObjectElement);
        timeList.add(jsonObjectElement1);
        timeList.add(jsonObjectElement2);
    }
    private void buttonAnim(final boolean showChannelFilterView) {
        if (showChannelFilterView) {
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.expand);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.search_filter).setVisibility(View.VISIBLE);
                }
            });
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim2.setInterpolator(lin);
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);
        } else {
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.collapse);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
                    findViewById(R.id.search_filter).setVisibility(View.GONE);
                }
            });
            LinearInterpolator lin = new LinearInterpolator();
            operatingAnim2.setInterpolator(lin);
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_TASKHISTORY:{
                if(resultCode==3){
                    ((PendingCommandFragment)mFragments.get(0)).doRefresh();
                }else if(resultCode==1) {
                    ((AllTaskHistoryFragment)mFragments.get(1)).doRefresh();
                }else if(resultCode==2){
                    ((FliterTaskHistoryFragment)mFragments.get(2)).doRefresh();
                }
                break;
            }
        }
    }
        //findViewById(R.id.btn_menu).clearAnimation();


    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
        @Override
        public void resolveNfcMessage(Intent intent) {

        }


}
