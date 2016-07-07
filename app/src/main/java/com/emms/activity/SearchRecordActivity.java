package com.emms.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.emms.R;

/**
 * Created by jaffer.deng on 2016/5/24.
 */
public class SearchRecordActivity extends BaseActivity implements View.OnClickListener{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);

        initView();
    }

    private void initView() {

    }

    @Override
    public void onClick(View v) {

    }
}
