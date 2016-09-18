package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.fragment.LinkedVerifyFragment;
import com.emms.fragment.OverDueVerifyFragment;
import com.emms.fragment.PendingVerifyFragment;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.emms.util.ViewFindUtils;
import com.flyco.tablayout.OnPageSelectListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/24.
 *
 */
public class WorkloadVerifyActivity extends NfcActivity  implements OnTabSelectListener,View.OnClickListener{

    //fragment,未审核，已审核
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private  String[] mTitles ;
    private Context mContext=this;
    private ViewPager vp;

    private TextView menuSearchTitle;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> filterConditionList=new ArrayList<>();
    private DropEditText filterCondition;
    private EditText filterTime;
    private HashMap<String,Integer> filter_condition_map=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workload_verify);
        initView();
        initFragment();
        initData();
        initSearchView();
       // getCommandListFromServer();
    }
    private void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.workloadVerify);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.btn_sure_bg).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_sure_bg).setOnClickListener(this);
        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.search_button).setOnClickListener(this);
        filterCondition=(DropEditText)findViewById(R.id.filterCondition);
        filterTime=(EditText) findViewById(R.id.filter_time);
        filterTime.setInputType(EditorInfo.TYPE_CLASS_PHONE);
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
            case R.id.btn_sure_bg:{
                doSubmit();
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
            case R.id.search_button:{
                if(filterCondition.getText().equals("")){
                    ToastUtil.showToastLong(R.string.pleaseSelectCondition,mContext);
                    return;
                }
                int filterTimeLong=30;
                if(filter_condition_map.get(filterCondition.getText())==4){
                    if(filterTime.getText().toString().equals("")){
                        ToastUtil.showToastLong(R.string.pleaseInputFilterTime,mContext);
                        return;
                    }
                    if( !DataUtil.isNum(filterTime.getText().toString())
                        || !DataUtil.isInt(filterTime.getText().toString())
                        || Integer.parseInt(filterTime.getText().toString())<=0   ){
                        ToastUtil.showToastLong(R.string.pleaseInputIntegerLargeThanZero,mContext);
                        return;
                    }
                    filterTimeLong=Integer.parseInt(filterTime.getText().toString());
                }
                //TODO
                ((OverDueVerifyFragment)mFragments.get(2)).doRefresh(
                        filter_condition_map.get(filterCondition.getText()),filterTimeLong);
                buttonAnim(false);
                vp.setCurrentItem(2,true);
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
                    }
                    @Override
                    public void refreshProcessingFragment() {
                        ((LinkedVerifyFragment)mFragments.get(1)).doRefresh();
                    }
                });
                mFragments.add(pendingVerifyFragment);
            }else if (i ==1){
                LinkedVerifyFragment linkedVerifyFragment= LinkedVerifyFragment.newInstance();
                linkedVerifyFragment.setTaskNumInteface(new TaskNumInteface() {
                    @Override
                    public void ChangeTaskNumListener(int tag, int num) {
                    }

                    @Override
                    public void refreshProcessingFragment() {
                    }
                });
                mFragments.add(linkedVerifyFragment);
            }else {
                OverDueVerifyFragment overDueVerifyFragment= OverDueVerifyFragment.newInstance();
                mFragments.add(overDueVerifyFragment);
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
        tabLayout_2.setOnPageSelectListener(new OnPageSelectListener() {
            @Override
            public void onPageChange(int position) {
                if(position==0||position==1){
                    findViewById(R.id.btn_sure_bg).setVisibility(View.VISIBLE);
                    findViewById(R.id.search_filter).setVisibility(View.INVISIBLE);
                    findViewById(R.id.filter).setVisibility(View.GONE);
                }else {
                    findViewById(R.id.btn_sure_bg).setVisibility(View.GONE);
                    findViewById(R.id.filter).setVisibility(View.VISIBLE);
                }
            }
        });

    }
    private boolean tag= false;
    @Override
    public void onTabSelect(int position) {
    }

    @Override
    public void onTabReselect(int position) {

    }
    private void doSubmit(){
        if(vp.getCurrentItem()==0){
            ((PendingVerifyFragment)mFragments.get(0)).submitVerifyData();
        }else if(vp.getCurrentItem()==1){
            ((LinkedVerifyFragment)mFragments.get(1)).submitVerifyData();
        }else {
            ToastUtil.showToastLong(R.string.NoDataToSubmit,mContext);
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
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:{
                                    filterCondition.getmEditText().setText(searchResult);
                                    if(position==2){
                                        findViewById(R.id.filter_time_layout).setVisibility(View.VISIBLE);
                                    }else {
                                        ((TextView)findViewById(R.id.filter_time)).setText("");
                                        findViewById(R.id.filter_time_layout).setVisibility(View.GONE);
                                    }
                                    break;}
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastLong(R.string.error_occur,mContext);
                }
            }
        });
        initDropSearchView(null, filterCondition.getmEditText(), mContext.getResources().
                        getString(R.string.filterConditionTitle),DataDictionary.DATA_NAME,
                1, R.string.getDataFail,filterCondition.getDropImage());
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
                        searchDataLists.addAll(filterConditionList);
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
                        ToastUtil.showToastLong(tips,mContext);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastLong(tips,mContext);
                    }
                }
            }
        });
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
    private void initData(){
        JsonObjectElement data1=new JsonObjectElement();
        data1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.filterCondition1));
        JsonObjectElement data2=new JsonObjectElement();
        data2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.filterCondition2));
        JsonObjectElement data3=new JsonObjectElement();
        data3.set(DataDictionary.DATA_NAME,getResources().getString(R.string.filterCondition3));
        filterConditionList.add(data1);
        filterConditionList.add(data2);
        filterConditionList.add(data3);

        filter_condition_map.put(getResources().getString(R.string.filterCondition1),2);
        filter_condition_map.put(getResources().getString(R.string.filterCondition2),3);
        filter_condition_map.put(getResources().getString(R.string.filterCondition3),4);
    }
    private void buttonAnim(final boolean showChannelFilterView){
        if(showChannelFilterView){
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
                    //o.pause();
                    //findViewById(R.id.btn_menu).clearAnimation();

                }
            });
            //Animation operatingAnim = AnimationUtils.loadAnimation(ArticleListActivity.this, R.anim.channellistfilterbuttonanim);
            LinearInterpolator lin = new LinearInterpolator();
            //operatingAnim.setFillAfter(true);
            //findViewById(R.id.btn_menu).startAnimation(operatingAnim);
            operatingAnim2.setInterpolator(lin);
            //operatingAnim2.startNow();
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);
        }else{
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
                    //o.pause();


                }
            });
            //Animation operatingAnim = AnimationUtils.loadAnimation(ArticleListActivity.this, R.anim.channellistfilterbuttonanim2);
            LinearInterpolator lin = new LinearInterpolator();
            //operatingAnim.setFillAfter(true);
            // findViewById(R.id.btn_menu).startAnimation(operatingAnim);
            operatingAnim2.setInterpolator(lin);
            //operatingAnim2.startNow();
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);

        }

        //findViewById(R.id.btn_menu).clearAnimation();
    }
}
