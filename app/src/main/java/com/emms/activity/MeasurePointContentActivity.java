package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.MeasurePoint;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
/**
 * Created by Administrator on 2016/9/11.
 *
 */
public class MeasurePointContentActivity extends NfcActivity  implements View.OnClickListener{
    private LineChartView lineChart;
    private List<PointValue> mPointValues = new ArrayList<>();
    private List<AxisValue> mAxisXValues = new ArrayList<>();
    private List<AxisValue> mAxisYValues = new ArrayList<>();
    private HashMap<String,String> MeasurePointType=new HashMap<>();
    private Context context=this;
    private DropEditText MeasurePointValue;
    private WebView webView;

    private ArrayList<ObjectElement> MeasureValueList=new ArrayList<>();
    private HashMap<String,String> MeasureValueMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point_content);
        initData();
        initView();
        getMeasurePointHistory();
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
    //initSearchView();
    }
    private JsonObjectElement measure_point_detail;
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.measure_point_content_input);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
         measure_point_detail= new JsonObjectElement(getIntent().getStringExtra("measure_point_detail"));
        ((TextView)findViewById(R.id.measure_point_id)).setText(DataUtil.isDataElementNull(measure_point_detail.get("MaintainPoint_ID")));
        ((TextView)findViewById(R.id.measure_point_name)).setText(DataUtil.isDataElementNull(measure_point_detail.get("TaskItemName")));
        ((TextView)findViewById(R.id.measure_point_type)).setText(MeasurePointType.get(DataUtil.isDataElementNull(measure_point_detail.get("PointType"))));
        ((TextView)findViewById(R.id.measure_point_unit)).setText(DataUtil.isDataElementNull(measure_point_detail.get("Unit")));
        //((TextView)findViewById(R.id.measure_point_range)).setText("10-20");
        if(measure_point_detail.get("Value")!=null&&measure_point_detail.get("Value").isArray()) {
            if(measure_point_detail.get("Value").asArrayElement().size()>0) {
                ObjectElement value = measure_point_detail.get("Value").asArrayElement().get(0).asObjectElement();
                String s = DataUtil.isDataElementNull(value.get("MinValue")) + "-"
                        + DataUtil.isDataElementNull(value.get("MaxValue"));
                ((TextView) findViewById(R.id.measure_point_range)).setText(s);
            }
        }
//        if(DataUtil.isDataElementNull(measure_point_detail.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
//            findViewById(R.id.measure_point_result).setVisibility(View.VISIBLE);
//            findViewById(R.id.measureValue).setVisibility(View.GONE);
//        }
//        if(measure_point_detail.get("IsResultSubmit").valueAsBoolean()){
//         findViewById(R.id.measure_point_result).setVisibility(View.GONE);
//         findViewById(R.id.measureValue).setVisibility(View.GONE);
//         findViewById(R.id.comfirm).setVisibility(View.INVISIBLE);
//         findViewById(R.id.measure_point_result_text).setVisibility(View.VISIBLE);
//         ((TextView)findViewById(R.id.measure_point_result_text)).setText(
//         DataUtil.isDataElementNull(measure_point_detail.get("ResultValue")));
//        }
        //MeasurePointValue=(DropEditText)findViewById(R.id.measure_point_result);
        lineChart = (LineChartView)findViewById(R.id.lineChart);
//        ((EditText)findViewById(R.id.measureValue)).addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                SubmitData=s.toString();
//            }
//        });
//        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SubmitDataToServer();
//            }
//        });

    }
    private void initData(){
        String sql="select * from DataDictionary where DataType = 'MaintainPointDataType'";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for(int i=0;i<element.asArrayElement().size();i++){
                    ObjectElement jsonObjectElement=element.asArrayElement().get(i).asObjectElement();
                  MeasureValueList.add(jsonObjectElement);
                    MeasureValueMap.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)),
                            DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)));
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                ToastUtil.showToastLong(R.string.FailToGetData,context);
            }
        });

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
    /**
     * 设置X 轴的显示
     */
//    private void getAxisXLables(){
//        for (int i = 0; i < date.length; i++) {
//            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
//        }
//    }
//    /**
//     * 图表的每个点的显示
//     */
//    private void getAxisPoints() {
//        for (int i = 0; i < score.length; i++) {
//            mPointValues.add(new PointValue(i, score[i]));
//        }
//    }
        private void initLineChart(){
            Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
            List<Line> lines = new ArrayList<>();
            line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
            line.setCubic(false);//曲线是否平滑，即是曲线还是折线
            line.setFilled(false);//是否填充曲线的面积
            line.setHasLabels(true);//曲线的数据坐标是否加上备注
           // line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
            line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
            line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
            lines.add(line);
            LineChartData data = new LineChartData();
            data.setLines(lines);
            data.setBaseValue(0);

            //坐标轴
            Axis axisX = new Axis(); //X轴
            axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
            axisX.setTextColor(Color.BLACK);  //设置字体颜色
            //axisX.setName("测点");  //表格名称
            axisX.setTextSize(10);//设置字体大小
            axisX.setMaxLabelChars(1); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
            axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
            data.setAxisXBottom(axisX); //x 轴在底部
            //data.setAxisXTop(axisX);  //x 轴在顶部
            axisX.setHasLines(true); //x 轴分割线

            // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
            Axis axisY = new Axis();  //Y轴
            axisY.setTextColor(Color.BLACK);
            //axisY.setName("date");//y轴标注
//            if(DataUtil.isDataElementNull(measure_point_detail.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
//                mAxisYValues.add(new AxisValue(0).setLabel("正常"));
//                mAxisYValues.add(new AxisValue(1).setLabel("不正常"));
//                axisX.setValues(mAxisYValues);
//            }
            axisY.setTextSize(10);//设置字体大小
            data.setAxisYLeft(axisY);  //Y轴设置在左边
            //data.setAxisYRight(axisY);  //y轴设置在右边


            //设置行为属性，支持缩放、滑动以及平移
            lineChart.setInteractive(false);
            lineChart.setZoomType(ZoomType.HORIZONTAL);
            lineChart.setMaxZoom((float) 1);//最大放大比例
            lineChart.setContainerScrollEnabled(false, ContainerScrollType.HORIZONTAL);
            lineChart.setLineChartData(data);
            //lineChart.setVisibility(View.VISIBLE);
            /**注：下面的7，10只是代表一个数字去类比而已
             * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
             */
            Viewport v = new Viewport(lineChart.getMaximumViewport());
            v.left = 0;
            v.right= 7;
            lineChart.setCurrentViewport(v);
        }
    private void getMeasurePointHistory(){
        HttpParams params=new HttpParams();
        params.put("taskItem_id",DataUtil.isDataElementNull(new JsonObjectElement(getIntent().getStringExtra("measure_point_detail")).get("TaskItem_ID")));
        HttpUtils.get(this, "TaskMaintain/GetMaintainPointList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement data = new JsonObjectElement(t);
                    if (data.get("PageData").asArrayElement().size() > 0) {
                        int i = 0;
                        float a = 0;
                        ArrayList<String> datalist = new ArrayList<>();
                        ArrayList<Integer> indexlist = new ArrayList<>();
                        String dataString = "";
                        String indexString = "";
                        for (; i < data.get("PageData").asArrayElement().size(); i++) {
//                        mAxisXValues.add(new AxisValue(i).setLabel(String.valueOf(i)));//获取x轴的标注
////                        if(!DataUtil.isDataElementNull(measure_point_detail.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
//                            mPointValues.add(new PointValue(i,
//                                    data.get("PageData").asArrayElement().get(i).asObjectElement().get("ResultValue").valueAsFloat()));
////                        }else {
////                            mPointValues.add(new PointValue(i,0));
////                        }
//                        if(!DataUtil.isFloat(DataUtil.isDataElementNull(data.get("PageData").asArrayElement().get(i).asObjectElement().get("ResultValue")))){
//                            return;
//                        }
//                        a=data.get("PageData").asArrayElement().get(i).asObjectElement().get("ResultValue").valueAsFloat();
//                    }
//                    mAxisXValues.add(new AxisValue(i+1).setLabel(String.valueOf(i+1)));//获取x轴的标注
////                        if(!DataUtil.isDataElementNull(measure_point_detail.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
//                    mPointValues.add(new PointValue(i+1,a-0.5f));
//                    initLineChart();//初始化
                            ObjectElement json = data.get(Data.PAGE_DATA).asArrayElement().get(i).asObjectElement();
                            datalist.add(DataUtil.isDataElementNull(json.get("ResultValue")).equals("") ? "0" : DataUtil.isDataElementNull(json.get("ResultValue")));
                            indexlist.add(i);
                            dataString += DataUtil.isDataElementNull(json.get("ResultValue")).equals("") ? "0" : DataUtil.isDataElementNull(json.get("ResultValue")) + ",";
                            indexString += i + ",";
                        }
                        if (indexString.endsWith(",")) {
                            indexString = indexString.substring(0, indexString.length() - 1);
                        }
                        if (dataString.endsWith(",")) {
                            dataString = dataString.substring(0, dataString.length() - 1);
                        }
                        //webView.loadUrl("javascript:showContent([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20],[5, 20, 36, 10, 10,5, 20, 36, 10, 10,5, 20, 36, 10, 10,5, 20, 36, 10, 10])");
                        webView.loadUrl("javascript:showContent([" + indexString + "],[" + dataString + "])");
                    }
                }
//                mAxisXValues.add(new AxisValue(1).setLabel(String.valueOf(1)));//获取x轴的标注
////                        if(!DataUtil.isDataElementNull(measure_point_detail.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
//                mPointValues.add(new PointValue(1,1.0f));

            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

}
