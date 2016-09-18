package com.emms.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.util.DataUtil;

/**
 * Created by Administrator on 2016/9/11.
 *
 */
public class MeasurePointContentActivity extends NfcActivity  implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point_content);
        initView();
    }

    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.measure_point_content_input);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        JsonObjectElement objectElement= new JsonObjectElement(getIntent().getStringExtra("measure_point_detail"));
        ((TextView)findViewById(R.id.measure_point_id)).setText(DataUtil.isDataElementNull(objectElement.get("measure_point_id")));
        ((TextView)findViewById(R.id.measure_point_name)).setText(DataUtil.isDataElementNull(objectElement.get("measure_point_name")));
        ((TextView)findViewById(R.id.measure_point_type)).setText(DataUtil.isDataElementNull(objectElement.get("measure_point_type")));
        ((TextView)findViewById(R.id.measure_point_unit)).setText(DataUtil.isDataElementNull(objectElement.get("measure_point_unit")));
        ((TextView)findViewById(R.id.measure_point_range)).setText("10-20");
    }
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
}
