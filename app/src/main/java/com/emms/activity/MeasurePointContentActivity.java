package com.emms.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.MeasurePoint;
import com.emms.util.DataUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;


/**
 * Created by Administrator on 2016/9/11.
 *
 */
public class MeasurePointContentActivity extends NfcActivity  implements View.OnClickListener{

    private HashMap<String,String> MeasurePointType=new HashMap<>();
    private WebView webView;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point_content);
        initData();
        initView();
        webView = (WebView)findViewById(R.id.chart);
        webView.getSettings().setJavaScriptEnabled(true);//支持js
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("file:///android_asset/echart.html");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            public void onPageFinished(WebView view, String url){
               // view.loadUrl("javascript:showContent([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20],[5, 20, 36, 10, 10,5, 20, 36, 10, 10,5, 20, 36, 10, 10,5, 20, 36, 10, 10])");
            }
        });
        getMeasurePointHistory();
    //initSearchView();
    }

    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.measure_point_content_input);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        JsonObjectElement measure_point_detail = new JsonObjectElement(getIntent().getStringExtra("measure_point_detail"));
        ((TextView)findViewById(R.id.measure_point_id)).setText(DataUtil.isDataElementNull(measure_point_detail.get("MaintainItem_ID")));
        ((TextView)findViewById(R.id.measure_point_name)).setText(DataUtil.isDataElementNull(measure_point_detail.get("TaskItemName")));
        if(MeasurePointType.get(DataUtil.isDataElementNull(measure_point_detail.get("PointType")))!=null) {
            ((TextView) findViewById(R.id.measure_point_type)).setText(MeasurePointType.get(DataUtil.isDataElementNull(measure_point_detail.get("PointType"))));
        }
        ((TextView)findViewById(R.id.measure_point_unit)).setText(DataUtil.isDataElementNull(measure_point_detail.get("Unit")));
        ((TextView)findViewById(R.id.measure_point_range)).setText(DataUtil.isDataElementNull(measure_point_detail.get("MaintainStandard")));
        //((TextView)findViewById(R.id.measure_point_range)).setText("10-20");
//        if(measure_point_detail.get("Value")!=null&& measure_point_detail.get("Value").isArray()) {
//            if(measure_point_detail.get("Value").asArrayElement().size()>0) {
//                ObjectElement value = measure_point_detail.get("Value").asArrayElement().get(0).asObjectElement();
//                String s = DataUtil.isDataElementNull(value.get("MinValue")) + "-"
//                        + DataUtil.isDataElementNull(value.get("MaxValue"));
//                ((TextView) findViewById(R.id.measure_point_range)).setText(s);
//            }
//        }


    }
    private void initData(){
        MeasurePointType.put(MeasurePoint.UPKEEP_POINT,getResources().getString(R.string.MPT01));
        MeasurePointType.put(MeasurePoint.PROCESS_MEASURE_POINT,getResources().getString(R.string.MPT02));
        MeasurePointType.put(MeasurePoint.OBVERSE_MEASURE_POINT,getResources().getString(R.string.MPT03));
        MeasurePointType.put(MeasurePoint.CHECK_POINT,getResources().getString(R.string.MPT04));
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

    private void getMeasurePointHistory(){
        HttpParams params=new HttpParams();
        params.put("taskItem_id",DataUtil.isDataElementNull(new JsonObjectElement(getIntent().getStringExtra("measure_point_detail")).get("TaskItem_ID")));
        HttpUtils.get(this, "MaintainAPI/GetMaintainPointList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    try{
                        JsonObjectElement data = new JsonObjectElement(t);
                        if (data.get("PageData").isArray()&&data.get("PageData").asArrayElement().size() > 0) {
                            String dataString = "";
                            String indexString = "";
                            for (int i=0; i < data.get("PageData").asArrayElement().size(); i++) {
                                ObjectElement json = data.get(Data.PAGE_DATA).asArrayElement().get(i).asObjectElement();
                                dataString += DataUtil.isDataElementNull(json.get("ResultValue")).equals("") ? "0" : DataUtil.isDataElementNull(json.get("ResultValue")) + ",";
                                indexString += i + ",";
                            }
                            if (indexString.endsWith(",")) {
                                indexString = indexString.substring(0, indexString.length() - 1);
                            }
                            if (dataString.endsWith(",")) {
                                dataString = dataString.substring(0, dataString.length() - 1);
                            }
                      webView.loadUrl("javascript:showContent([" + indexString + "],[" + dataString + "])");
                        }
                    }catch (Throwable throwable){
                        CrashReport.postCatchedException(throwable);
                    }
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

}
