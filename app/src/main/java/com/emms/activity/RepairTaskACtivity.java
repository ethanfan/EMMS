package com.emms.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.emms.R;
import com.emms.fragment.LinkedOrdersFragment;
import com.emms.fragment.PendingOrdersFragment;
import com.emms.fragment.ProcessingFragment;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/20.
 */
public class RepairTaskACtivity extends BaseActivity implements OnTabSelectListener,View.OnClickListener{
    private Context mContext ;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private  String[] mTitles ;
    private MyPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_task);
        mContext = this;

        initView();

        mTitles =getResources().getStringArray(R.array.select_tab_status);
        for (int i =0;i< mTitles.length;i++) {
            if (i==0) {
                mFragments.add(new ProcessingFragment());
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

        SlidingTabLayout tabLayout_2 = ViewFindUtils.find(decorView, R.id.tl_2);
        tabLayout_2.setViewPager(vp);
        tabLayout_2.setOnTabSelectListener(this);


        tabLayout_2.showMsg(2, 9);         //消息数量和位置
        tabLayout_2.setMsgMargin(2, 12, 10);

        tabLayout_2.showMsg(1, 99);
        tabLayout_2.setMsgMargin(1, 12, 10);

        tabLayout_2.showMsg(0, 100);
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
}
