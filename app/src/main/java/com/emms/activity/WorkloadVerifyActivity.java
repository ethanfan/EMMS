package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.LinkedVerifyFragment;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.PendingVerifyFragment;
import com.emms.fragment.ProcessingFragment;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/24.
 */
public class WorkloadVerifyActivity extends NfcActivity  implements OnTabSelectListener,View.OnClickListener{

    //fragment,未审核，已审核
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private  String[] mTitles ;
    private MyPagerAdapter mAdapter;
    private SlidingTabLayout tabLayout_2;
    private Context mContext=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workload_verify);
        initFragment();
        initView();
       // getCommandListFromServer();
    }
    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.workloadVerify);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
    }

//    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                finish();
                break;
            }
        }
    }
    private void initFragment(){
        mTitles =getResources().getStringArray(R.array.select_tab_verify_status);
        for (int i =0;i< mTitles.length;i++) {
            if (i==0) {
                PendingVerifyFragment pendingVerifyFragment= PendingVerifyFragment.newInstance();
                pendingVerifyFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                      //  ChangeTaskNum(tag,num);
                    }
                    @Override
                    public void refreshProcessingFragment() {
                    }
                });
                mFragments.add(pendingVerifyFragment);
            }else if (i ==1){
                LinkedVerifyFragment linkedVerifyFragment= LinkedVerifyFragment.newInstance();
                linkedVerifyFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                        //ChangeTaskNum(tag,num);
                    }

                    @Override
                    public void refreshProcessingFragment() {
                        ((ProcessingFragment)mFragments.get(0)).doRefresh();
                    }
                });
                mFragments.add(linkedVerifyFragment);
            }
        }
        View decorView = getWindow().getDecorView();
        ViewPager vp = ViewFindUtils.find(decorView, R.id.vp);
        vp.setOffscreenPageLimit(1);
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        vp.setAdapter(mAdapter);

        tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
        tabLayout_2.setViewPager(vp);
        tabLayout_2.setOnTabSelectListener(this);
    }
    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

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
}
