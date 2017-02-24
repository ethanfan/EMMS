package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.ProcessingFragment;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class TaskListActivity extends NfcActivity implements OnTabSelectListener,View.OnClickListener{
    private Context mContext ;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private  String[] mTitles ;
    private String TaskClass;
    private String TaskSubClass;
    private SlidingTabLayout tabLayout_2;
    //private HashMap<String,Integer> TaskClass_Position_map=new HashMap<>()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_task);
        mContext = this;
        BaseData.setBaseData(mContext);
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        TaskSubClass=getIntent().getStringExtra(Task.TASK_SUBCLASS);
        initView();
        //getRepairTaskFromServer();
        mTitles =getResources().getStringArray(R.array.select_tab_status);
        for (int i =0;i< mTitles.length;i++) {
            if (i==0) {
                ProcessingFragment processingFragment=ProcessingFragment.newInstance(TaskClass,TaskSubClass);
                processingFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                       ChangeTaskNum(tag,num);
                    }
                    @Override
                    public void refreshProcessingFragment() {
                    }
                });
                mFragments.add(processingFragment);
            }else if (i ==1){
                PendingOrdersFragment pendingOrdersFragment=PendingOrdersFragment.newInstance(TaskClass,TaskSubClass);
                pendingOrdersFragment.setFactory(getLoginInfo().getFromFactory());
                pendingOrdersFragment.setOperatorID(String.valueOf(getLoginInfo().getId()));
                pendingOrdersFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                        ChangeTaskNum(tag,num);
                    }

                    @Override
                    public void refreshProcessingFragment() {
                        ((ProcessingFragment)mFragments.get(0)).doRefresh();
                    }
                });
                mFragments.add(pendingOrdersFragment);
            }else if (i ==2){
                mFragments.add(LinkedOrdersFragment.newInstance(TaskClass,TaskSubClass));
            }
        }
        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.vp);
        vp.setOffscreenPageLimit(2);
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);
       try{
           String[] taskNum=getIntent().getStringExtra("TaskNum").split("/");

           tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
           tabLayout_2.setViewPager(vp);
           tabLayout_2.setOnTabSelectListener(this);


           //   tabLayout_2.showMsg(2, 9);         //消息数量和位置
           //   tabLayout_2.setMsgMargin(2, 12, 10);

           tabLayout_2.showMsg(1, Integer.valueOf(taskNum[1]));
           tabLayout_2.setMsgMargin(1, 12, 10);

           tabLayout_2.showMsg(0, Integer.valueOf(taskNum[0]));
           tabLayout_2.setMsgMargin(0, 12, 10);
           //getSupportFragmentManager().
       }catch (Throwable throwable){
           CrashReport.postCatchedException(throwable);
       }
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
       switch (TaskClass){
           case Task.REPAIR_TASK:{
               ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.repair_task));
               break;
           }
           case Task.MAINTAIN_TASK:{
               if(TaskSubClass==null){
               ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.maintain_task));
               }
               else if(TaskSubClass.equals(Task.ROUTING_INSPECTION)){
                   ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.routingInspectionTask));
               }else {
                   ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.upkeepTask));
               }
               break;
           }
           case Task.MOVE_CAR_TASK:{
               ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.move_car_task));
               break;
           }
           case Task.OTHER_TASK:{
               ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.other_task));
               break;
           }
           case Task.TRANSFER_MODEL_TASK:{
               ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.transfer_model_task));
               break;
           }
           case Task.GROUP_ARRANGEMENT:{
               ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.GroupArrangementTask));
               break;
           }
       }

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

    @Override
    public void resolveNfcMessage(Intent intent) {
//        String action = intent.getAction();
//        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            String iccardID = NfcUtils.dumpTagData(tag);
//        }
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


    /*
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
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
    //  private ListenableFuture<DataElement> getRepairTaskContent(String status) {
        // RepairTask.clear();
   //     String rawQuery = "SELECT * FROM MAINTAIN WHERE STATUS=" + "\""+status+"\"";
  //      return  getSqliteStore().performRawQuery(rawQuery,
 //               EPassSqliteStoreOpenHelper.SCHEMA_MAINTAIN, null);
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
                       Toast.makeText(TaskListActivity.this,"获取维修任务信息失败，请重新获取",Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });*/
//    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.REQUEST_CODE_PROCESSING_ORDER_TASK_DETAIL:{
                if(resultCode==2){
                ((ProcessingFragment)mFragments.get(0)).doRefresh();
                ((PendingOrdersFragment)mFragments.get(1)).doRefresh();}
                break;
            }
        }
    }
    private void ChangeTaskNum(int tag,int num){
        tabLayout_2.showMsg(tag,num);
    }
    private void getTaskCountFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        //params.put("id",String.valueOf(getLoginInfo().getId()));
        // String s=SharedPreferenceManager.getUserName(this);
        HttpUtils.get(this, "TaskNum", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement json = new JsonObjectElement(t);
                    //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                    if (json.get("PageData") != null && json.get("PageData").asArrayElement() != null&&json.get("PageData").asArrayElement().size()>0) {
                        for (int i = 0; i < json.get("PageData").asArrayElement().size(); i++) {
                            //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                            //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectEl
                            if(DataUtil.isDataElementNull(json.get("PageData").asArrayElement().get(i).asObjectElement().get("DataCode")).equals(TaskClass)){
                                tabLayout_2.showMsg(1, json.get("PageData").asArrayElement().get(i).asObjectElement().get("S0").valueAsInt());
                                tabLayout_2.showMsg(0, json.get("PageData").asArrayElement().get(i).asObjectElement().get("S1").valueAsInt());
                            }
                        }
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.loadingFail,mContext);
                dismissCustomDialog();
            }
        });
    }
    private void getTaskByICcardID(String ICcardID){

    }
}
