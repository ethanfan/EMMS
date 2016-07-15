package com.emms.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.ProcessingFragment;
import com.emms.httputils.HttpUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/20.
 */
public class RepairTaskACtivity extends BaseActivity implements OnTabSelectListener,View.OnClickListener{
    private Context mContext ;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private  String[] mTitles ;
    private MyPagerAdapter mAdapter;
    private ArrayList<ObjectElement> RepairTask=new ArrayList<ObjectElement>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_task);
        mContext = this;

        initView();
        getRepairTaskFromServer();
        getRepairTaskContent("完成");
        mTitles =getResources().getStringArray(R.array.select_tab_status);
        for (int i =0;i< mTitles.length;i++) {
            if (i==0) {
                ProcessingFragment processingFragment=new ProcessingFragment();
                processingFragment.setDatas(getRepairTaskContent("完成"));
                mFragments.add(processingFragment);
            }else if (i ==1){
                mFragments.add(new PendingOrdersFragment());
            }else if (i ==2){
                mFragments.add(new LinkedOrdersFragment());
            }
        }

        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.vp);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
        String[] taskNum=getIntent().getStringExtra("TaskNum").split("/");

        SlidingTabLayout tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
        tabLayout_2.setViewPager(vp);
        tabLayout_2.setOnTabSelectListener(this);


     //   tabLayout_2.showMsg(2, 9);         //消息数量和位置
     //   tabLayout_2.setMsgMargin(2, 12, 10);

        tabLayout_2.showMsg(1, Integer.valueOf(taskNum[1]));
        tabLayout_2.setMsgMargin(1, 12, 10);

        tabLayout_2.showMsg(0, Integer.valueOf(taskNum[0]));
        tabLayout_2.setMsgMargin(0, 12, 10);
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.repair_task));
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void onClick(View v) {
        int click_id = v.getId();
        if (click_id ==R.id.btn_right_action){
            finish();
        }
    }

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

    private void getRepairTaskNumber(){

    }
    private void getRepairTaskFromServer(){
        HttpParams params=new HttpParams();
     //   params.put("Operator_ID", SharedPreferenceManager.getUserName(this));
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        Log.e("returnString","dd");
        HttpUtils.get(this, "Task", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e("returnString",t);
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
            }
        });
    }
    private ListenableFuture<DataElement> getRepairTaskContent(String status) {
        // RepairTask.clear();
        String rawQuery = "SELECT * FROM MAINTAIN WHERE STATUS=" + "\""+status+"\"";
        return  getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_MAINTAIN, null);
        /*Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement dataElement) {
               if(dataElement!=null&&dataElement.asArrayElement().size()>0){
                   for(DataElement obj:dataElement.asArrayElement()){
                       RepairTask.add(obj.asObjectElement());
                   }
                   Log.e("RepairTask",RepairTask.toString());

               }
            }

            @Override
            public void onFailure(Throwable throwable) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(RepairTaskACtivity.this,"获取维修任务信息失败，请重新获取",Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });*/
    }
   /* private ArrayList<ObjectElement> getRepairTaskList(String status){
        ListenableFuture<DataElement> data=getRepairTaskContent(status);
        Futures.addCallback(data, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement dataElement) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }*/
}
