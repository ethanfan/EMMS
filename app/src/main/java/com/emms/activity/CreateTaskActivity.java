package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.schema.Team;
import com.emms.ui.DropEditText;
import com.emms.ui.KProgressHUD;
import com.emms.ui.NFCDialog;
import com.emms.util.BuildConfig;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/7.
 *
 */
public class CreateTaskActivity extends NfcActivity implements View.OnClickListener {

    private Context mContext;

    private DropEditText task_type, task_subtype, group, device_name,simple_description,hasEquipment,targetOrganise;
    private EditText create_task, device_num,task_description;
    private TextView task_subtype_name_desc;
    private TextView menuSearchTitle;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private ListView mResultListView;
    private ResultListAdapter mResultAdapter;
    private EditText searchBox;
    private Button btn_sure;
    private ImageView create_task_action, device_num_action;
    private KProgressHUD hud;
    private String TaskClass;
    private HashMap<String,String> Task_Class_Name=new HashMap<>();
    private AlertDialog mDialog;
    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();

    public final static String FORM_TYPE = "formtype";
    public final static String FORM_CONTENT = "content";
    public final static int REQUEST_CODE = 10000;
    public final static int TASK_TYPE = 1;
    public final static int TASK_SUBTYPE = 2;
    public final static int DEVICE_NAME =5;
    public final static int GROUP = 4;
    public final static int CREATER = 3;
    public final static int DEVICE_NUM = 6;
    public final static int SIMPLE_DESCRIPTION = 7;
    public final static int TASK_WORKLOAD=8;
    public final static int HAS_EQUIPMENT=9;
    public final static int TARGET_ORGANISE=10;

    private ArrayList<String> mDeviceTypelist;

    private ArrayList<ObjectElement> mTaskType = new ArrayList<>();
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> mSubType = new ArrayList<>();
    private ArrayList<ObjectElement> mTeamNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> mDeviceNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> mDeviceNumlist = new ArrayList<>();
    private ArrayList<ObjectElement> mSimpleDescriptionList=new ArrayList<>();
    private ArrayList<ObjectElement> mWorkLoadNoList=new ArrayList<>();
    private ArrayList<ObjectElement> mHasEquipment=new ArrayList<>();
    private ArrayList<ObjectElement> mTargetGroup=new ArrayList<>();

    private ArrayList<ObjectElement> mGroupArrangeDescriptionList=new ArrayList<>();
    private String creatorId;


    private NFCDialog nfcDialog;
    private DrawerLayout mDrawer_layout;

    private int nfctag = 0;
    private int  searchtag =0;

    private String teamId ="";
    private String equipmentName ="";
    private String equipmentID = "";
    private HashMap<String,String> task_type_class=new HashMap<>();
    private boolean tag=false;
    private String DeviceName="";
    private String SimpleDescriptionCode="";
    private String WorkloadCode="";
    private String FromTask_ID;
    private ArrayList<String> Group_ID_List=new ArrayList<>();
    private HashMap<String,Integer> HasEquipment_map=new HashMap<>();
    private String IntentTaskSubClass;
    private String IntentTaskItem;
    private String targetOrganiseID;
    private String OperatorInfo;//若为从搬车任务或调车任务完成时创建对应任务时有值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        mContext = CreateTaskActivity.this;
        //TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        FromTask_ID=getIntent().getStringExtra("FromTask_ID");
        //getEquipmentClassTaskDescription();
        OperatorInfo=getIntent().getStringExtra("OperatorInfo");
        if(getIntent().getStringExtra("FromMeasurePointActivity")!=null){
        IntentTaskSubClass=getIntent().getStringExtra("TaskSubClass");}
        IntentTaskItem=getIntent().getStringExtra("TaskItem");
        initData();
        initView();
        initSearchView();
        initEvent();
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDialog = new AlertDialog.Builder(this).setNeutralButton(R.string.warning_message_confirm, null).create();
    }

    private void initSearchView() {
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        clearBtn.setVisibility(View.INVISIBLE);
        searchBox = (EditText) findViewById(R.id.et_search);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
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
        mResultAdapter = new ResultListAdapter(this);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case TASK_TYPE:
                                    getSubTaskType(searchResult);
                                    task_type.getmEditText().setText(searchResult);
                                    if(OperatorInfo==null
                                            &&Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                        resetCretor();
                                    }
                                    if(DataUtil.isDataElementNull(mResultAdapter.getItem(position).
                                            get(DataDictionary.DATA_CODE)).equals(Task.MOVE_CAR_TASK)){
                                        findViewById(R.id.target_group_layout).setVisibility(View.VISIBLE);
                                        if(OperatorInfo==null
                                                &&Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                            getTeamIdByOrganiseID("ALL", false);
                                        }
                                    }else {
                                        findViewById(R.id.target_group_layout).setVisibility(View.GONE);
                                        if(OperatorInfo==null
                                                &&Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext)) == RootUtil.ROOT_WARRANTY) {
                                            Operator operator = getLoginInfo(SharedPreferenceManager.getLoginData(mContext));
                                            if (null != operator) {
                                                getTeamId(operator);
                                            }
                                        }
                                    }
                                    if(DataUtil.isDataElementNull(mResultAdapter.getItem(position).
                                            get(DataDictionary.DATA_CODE)).equals(Task.GROUP_ARRANGEMENT)){
                                        if(mGroupArrangeDescriptionList.size()<=0) {
                                              getGroupArrangeSimpleDesList();
                                        }else {
                                            if(!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)){
                                                mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
                                            }
                                        }
                                    }else {
                                        if(mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)){
                                            mSimpleDescriptionList.removeAll(mGroupArrangeDescriptionList);
                                        }
                                    }
                                    break;
                                case TASK_SUBTYPE:
                                    task_subtype.getmEditText().setText(searchResult);
                                    break;
                                case GROUP:
                                    teamId =DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(Team.TEAM_ID));
                                    group.getmEditText().setText(searchResult);
                                    resetTeam();
                                    break;
                                case DEVICE_NAME:
                                    equipmentName =DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(Equipment.EQUIPMENT_NAME));
//                                    device_name.getmEditText().setSingleLine(true);
                                    device_name.getmEditText().setText(searchResult);
                                    DeviceName=DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(Equipment.EQUIPMENT_CLASS));
                                   // getSimpleDescription(DeviceName);
                                    resetDeviceName();
                                    initDeviceNum();
                                    break;
                                case DEVICE_NUM:
                                    device_num.setText(searchResult);
                                    equipmentID=DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(Equipment.EQUIPMENT_ID));
                                    resetDeviceNum();
                                    break;
                                case SIMPLE_DESCRIPTION:
                                    simple_description.getmEditText().setText(searchResult);
                                    SimpleDescriptionCode=DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(DataDictionary.DATA_CODE));
                                    //task_description.setText(searchResult);
                                    break;
                                case HAS_EQUIPMENT:
                                    hasEquipment.getmEditText().setText(searchResult);
                                    resetEquipment();
                                    if(HasEquipment_map.get(searchResult)==0){
                                        findViewById(R.id.equipment_name).setVisibility(View.GONE);
                                        findViewById(R.id.equipment_num).setVisibility(View.GONE);
                                    }else {
                                        findViewById(R.id.equipment_name).setVisibility(View.VISIBLE);
                                        findViewById(R.id.equipment_num).setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case TARGET_ORGANISE:
                                    targetOrganise.getmEditText().setText(searchResult);
                                    targetOrganiseID=DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(Team.TEAM_ID));
                                    break;
//                                case TASK_WORKLOAD:
//                                    standard_workload.getmEditText().setText(searchResult);
//                                    WorkloadCode=DataUtil.isDataElementNull(mResultAdapter.getItem(inPosition).get(DataDictionary.DATA_CODE));
//                                    break;
                            }
                            searchBox.setText("");
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(R.string.error_occur,mContext);
                }
            }
        });

        clearBtn.setOnClickListener(this);
        searchDataLists = new ArrayList<>();
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initEvent() {
//        getSimpleDescription("all");
       // initLoginData();

                // Toast.makeText(mContext, "目前该设备没有机台号", Toast.LENGTH_SHORT).show();
        Operator operator = getLoginInfo(SharedPreferenceManager.getLoginData(this));
        if(null !=  operator){
            creatorId = String.valueOf(operator.getId());
            resetCretor();
            getTeamId(operator);
        }
        //initSimpleDescription();
       // initWorkloadData();
        getTaskType();//获取任务类型 基本不用改

        initHasEquipmentData();
        initDropSearchView(null, hasEquipment.getmEditText(), getResources().
                        getString(R.string.IsHasEquipment), DataDictionary.DATA_NAME,
                HAS_EQUIPMENT, R.string.gettingDataPleaseWait,hasEquipment.getDropImage());
        hasEquipment.getmEditText().setText(DataUtil.isDataElementNull(mHasEquipment.get(0).get(DataDictionary.DATA_NAME)));

        initDropSearchView(null, task_type.getmEditText(), getResources().
                        getString(R.string.title_search_task_type), DataDictionary.DATA_NAME,
                TASK_TYPE, R.string.getDataFail,task_type.getDropImage());

        initDropSearchView(null, targetOrganise.getmEditText(), getResources().
                        getString(R.string.title_target_group), "TeamName",
                TARGET_ORGANISE, R.string.getDataFail,targetOrganise.getDropImage());
        task_type.getmEditText()
                .addTextChangedListener(
                        new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                task_subtype.getmEditText().setText("");
                                getSubTaskType(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        }
                );

//        task_subtype.getDropImage().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!task_type.getText().equals("")) {
//                            if (mSubType.size() > 0) {
//                                task_subtype.setDatas(mContext, mSubType, DataDictionary.DATA_NAME);
//                                task_subtype.showOnclik();
//                            }
//                        } else {
//                            Toast.makeText(mContext, "请先选择任务类型", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//            }
//        });
        initDropSearchView(task_type.getmEditText(), task_subtype.getmEditText(),
                getResources().
                        getString(R.string.title_search_task_subtype), DataDictionary.DATA_NAME, TASK_SUBTYPE, R.string.pleaseSelectTaskClass,task_subtype.getDropImage());

        initDropSearchView(group.getmEditText(), device_name.getmEditText(),
                getResources().
                        getString(R.string.title_search_equipment_name), Equipment.EQUIPMENT_NAME, DEVICE_NAME, R.string.pleaseSelectGroup,device_name.getDropImage());
//        group.getmEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //device_name.getmEditText().setText("");
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(!tag){
//                getDeviceName();}else{
//                    tag=false;
//                }
//            }
//        });
        simple_description.getmEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO
//                    if(s.toString().equals(getResources().getString(R.string.other))){
//                        findViewById(R.id.task_description_layout).setVisibility(View.VISIBLE);
////                        if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=7){
////                        findViewById(R.id.layout_standard_workload).setVisibility(View.VISIBLE);
////                        }
//                    }else{
//                        //standard_workload.getmEditText().setText("");
//                        task_description.setText("");
//                       // findViewById(R.id.layout_standard_workload).setVisibility(View.GONE);
//                        findViewById(R.id.task_description_layout).setVisibility(View.GONE);
//                        WorkloadCode="";
//                    }
            }
        });
        initDropSearchView(create_task, group.getmEditText(),
                getResources().
                        getString(R.string.title_search_group), Team.TEAMNAME, GROUP, R.string.pleaseScanICcard,group.getDropImage());

//       group.getDropImage().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!create_task.getText().toString().equals("") && mTeamNamelist.size() > 0) {
//                            group.setDatas(mContext, mTeamNamelist, Team.TEAMNAME);
//                            group.showOnclik();
//                            isSearchview =false ;
//                        } else {
//                            Toast.makeText(mContext, "请先扫描工卡获取创建人信息", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });
//            }
//        });


//        device_name.getDropImage().
//                setOnClickListener(
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (!group.getText().equals("")) {
//                                            if (mDeviceNamelist != null) {
//                                                if (mDeviceNamelist.size() > 0) {
//                                                    device_name.setDatas(mContext, mDeviceNamelist, Equipment.EQUIPMENT_NAME);
//                                                    device_name.showOnclik();
//                                                    isSearchview = false ;
//                                                }
//                                            } else {
//                                                Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        } else {
//                                            Toast.makeText(mContext, "请先选择组别", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    }
//                                });
//
//                            }
//
//                        }
//
//                );


//        device_name.getmEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(!tag){
//                    if (!s.toString().equals("")){
//                        initDeviceNum();
//                        }
//                    }else{
//                    if(!DeviceName.equals("")){
//                    tag=false;
//                    }
//                }
//            }
//        });

        initDropSearchView(device_name.getmEditText(), device_num,
                getResources().
                        getString(R.string.title_search_equipment_nun), Equipment.ASSETSID, DEVICE_NUM, R.string.pleaseSelectEquipment,(ImageView) findViewById(R.id.device_num_action));
        initDropSearchView(null,simple_description.getmEditText(),getResources().getString(R.string.simpleDescription),"DataName",SIMPLE_DESCRIPTION,R.string.NoEquipmentDescription,simple_description.getDropImage());
//        initDropSearchView(null, standard_workload.getmEditText(),
//                getResources().
//                        getString(R.string.work_num_dialog),DataDictionary.DATA_NAME, TASK_WORKLOAD, R.string.noWorkTimeData,standard_workload.getDropImage());

//        if(1 == mTeamNamelist.size()){
//            teamId =DataUtil.isDataElementNull(mTeamNamelist.get(0).get("Team_ID"));
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(0).get("TeamName")));
//                    getDeviceName();
//                }
//            });
//        }
    }

    private void initDeviceNum() {
//        if ( !isSearchview){
//            equipmentName = mDeviceNamelist.get(device_name.getSelectPosition()).get(Equipment.EQUIPMENT_NAME).valueAsString();
//        }
        try {
            String rawQuery = "SELECT * FROM Equipment WHERE EquipmentName=" + "'" + equipmentName
                    + "'" + " AND Organise_ID_use =" + teamId +" order by AssetsID asc";
            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {

                @Override
                public void onSuccess(DataElement dataElement) {
                    mDeviceNumlist.clear();
                    if (dataElement != null && dataElement.isArray()
                            && dataElement.asArrayElement().size() > 0) {
                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
                            if(!DataUtil.isDataElementNull(dataElement.asArrayElement().get(i).asObjectElement().get(Equipment.ASSETSID)).equals("")) {
                                mDeviceNumlist.add(dataElement.asArrayElement().get(i).asObjectElement());
                            }
                        }
                    }
                    if (mDeviceNumlist.size()==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.NoEquipmentNum,mContext);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }catch (Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToastShort(R.string.NoEquipmentNum,mContext);
                }
            });
        }

    }

    private boolean isSearchview ;
    /**
     * 判断是否是左侧菜单点击还是下拉菜单点击获取
     *  isSearchview   true是左侧菜单点击
     */
    private void getDeviceName() {
//        if (!isSearchview) {
//            teamId = mTeamNamelist.get(group.getSelectPosition()).get("Team_ID").valueAsString();
//        }
        String rawQuery ="select  distinct EquipmentName,EquipmentClass from Equipment where Organise_ID_Use ="+teamId+"  and EquipmentName is not null";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                System.out.println(element);
                mDeviceNamelist.clear();
                try {
                    if (element != null && element.isArray()
                            && element.asArrayElement().size() > 0) {
                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                            mDeviceNamelist.add(element.asArrayElement().get(i)
                                    .asObjectElement());
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.NoGroupEquipment,mContext);
                            }
                        });
                    }
                } catch (Exception e) {
                    CrashReport.postCatchedException(e);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

    }
//    private void initLoginData() {
//        Operator operator = getLoginInfo();
//        if(null !=  operator){
//            creatorId = String.valueOf(operator.getId());
//            getTeamId(operator);
//        }
//    }
    private void getTeamId(Operator operator) {
        if(OperatorInfo!=null){
            JsonObjectElement jsonObjectElement=new JsonObjectElement(OperatorInfo);
         creatorId=DataUtil.isDataElementNull(jsonObjectElement.get("Operator_ID"));
            create_task.setText(DataUtil.isDataElementNull(jsonObjectElement.get("Name")));
            getTeamIdByOrganiseID(DataUtil.isDataElementNull(jsonObjectElement.get("Organise_ID")),true);
        }else {
            if (null == operator) {
                //get userData from server
            } else {
                create_task.setText(operator.getName());  //创建人名
                getTeamIdByOrganiseID(operator.getOrganiseID(),false);
            }
        }
    }


    private void getTeamIdByOrganiseID(String organiseID,boolean isFromMovingCarOrShunting) {


//        String teamIDStr = "";
//        String teamNameStr = "";
        String rawQuery;
        if("ALL".equals(organiseID)){
            rawQuery="select Organise_ID Team_ID,OrganiseName TeamName  from BaseOrganise where OrganiseClass=0 and FromFactory='"+getLoginInfo().getFromFactory()+"' and OrganiseType>1";
        }else {
            if (Integer.valueOf(SharedPreferenceManager.getUserRoleID(this)) == RootUtil.ROOT_WARRANTY
                    || isFromMovingCarOrShunting) {
                rawQuery = "select Organise_ID Team_ID,OrganiseName TeamName  from BaseOrganise where Organise_ID in(" + organiseID + ") and OrganiseClass=0 and OrganiseType>1";
            } else {
                rawQuery = "select distinct b.[Organise_ID] Team_ID,b.[OrganiseName] TeamName from TaskOrganiseRelation a,BaseOrganise b" +
                        "        where a.[ServerTeam_ID] in (" + organiseID + ")" +
                        "        and a.[Team_ID]=b.[Organise_ID]" +
                        "        and b.[OrganiseClass]=0 and b.OrganiseType>1";
            }
        }
            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {

                @Override
                public void onSuccess(DataElement dataElement) {
                    mTeamNamelist.clear();
                    if (dataElement != null && dataElement.isArray()
                            && dataElement.asArrayElement().size() > 0) {
                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
                            mTeamNamelist.add(dataElement.asArrayElement().get(i).asObjectElement());
                            Group_ID_List.add(DataUtil.isDataElementNull(dataElement.asArrayElement().get(i).asObjectElement().get("Team_ID")));
                        }

                        if(1 == mTeamNamelist.size()
                                &&OperatorInfo==null){
                            teamId =DataUtil.isDataElementNull(mTeamNamelist.get(0).get("Team_ID"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    group.getmEditText().setText(DataUtil.isDataElementNull(mTeamNamelist.get(0).get("TeamName")));
                                    getDeviceName();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.NoOperatorGroup,mContext);
                            }
                        });

                    }

                }

                @Override
                public void onFailure(Throwable throwable) {
                    CrashReport.postCatchedException(throwable);
                }
            });





//        String teamID[] = teamIDStr.split(",");
//        String teamName[] = teamNameStr.split(",");
//        List<ObjectElement> array = new ArrayList<ObjectElement>();
//        for(int i=0;i<teamID.length;i++){
//            ObjectElement objectElement =  new JsonObjectElement();
//            objectElement.set(Team.TEAM_ID, teamID[i]);
//            objectElement.set(Team.TEAMNAME, teamName[i]);
//            mTeamNamelist.add(objectElement);
//        }



    }


    private void getTaskType() {
       DataUtil.getDataFromDataBase(mContext, "TaskClass", 0, new StoreCallback() {
                   @Override
                   public void success(DataElement element, String resource) {
                       System.out.println(element);
                       final HashMap<String,String> map=new HashMap<>();
                       if (element != null && element.isArray()
                               && element.asArrayElement().size() > 0) {
                           searchDataLists.clear();
                           for (int i = 0; i < element.asArrayElement().size(); i++) {
                               String s=DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE));
//                               if(s.equals(Task.REPAIR_TASK)&&Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=RootUtil.ROOT_WARRANTY){
//                                   element.asArrayElement().get(i).asObjectElement().set(DataDictionary.DATA_NAME,getResources().getString(R.string.GroupArrangement));
//                               }
                               if((!s.equals(Task.MAINTAIN_TASK)))
                               {//TODO
                                   if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))==RootUtil.ROOT_WARRANTY
                               ||OperatorInfo!=null) {
                                       //报修人屏蔽组内安排任务
                                           if("GEW".equals(getLoginInfo().getFromFactory())){
                                               //GEW工厂屏蔽搬车和调车
                                            if(Task.REPAIR_TASK.equals(s)) {
                                                mTaskType.add(element.asArrayElement().get(i).asObjectElement());
                                             }
                                           }else {
                                               if(Task.REPAIR_TASK.equals(s)
                                                       ||Task.MOVE_CAR_TASK.equals(s)
                                                       ||Task.TRANSFER_MODEL_TASK.equals(s))
                                               mTaskType.add(element.asArrayElement().get(i).asObjectElement());
                                           }
                                   }else {
                                        //维修工屏蔽搬车、调车、车间报修任务
                                           if("GEW".equals(getLoginInfo().getFromFactory())){
                                              if(Task.GROUP_ARRANGEMENT.equals(s)) {
                                                  mTaskType.add(element.asArrayElement().get(i).asObjectElement());
                                              }
                                           }else {
                                               if(Task.OTHER_TASK.equals(s)) {
                                                   mTaskType.add(element.asArrayElement().get(i).asObjectElement());
                                               }
                                           }

                                   }
                               }
                               task_type_class.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)),
                                       DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)));
//                        Task_Class_Name.put(element.asArrayElement().get(i).asObjectElement().get("DataCode").valueAsString(),
//                                element.asArrayElement().get(i).asObjectElement().get("DataName").valueAsString());
                               map.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                                       DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                           }
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   task_type.setDatas(mContext, mTaskType,DataDictionary.DATA_NAME);
                                   if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))==RootUtil.ROOT_WARRANTY) {
                                       //若为报修人角色，即UserRoleID==7,创建任务，默认为车间报修任务
                                       task_type.setText(map.get(Task.REPAIR_TASK));
                                   }else {
                                       if("GEW".equals(getLoginInfo().getFromFactory())){
                                       task_type.setText(map.get(Task.GROUP_ARRANGEMENT));
                                       getGroupArrangeSimpleDesList();
                                       }else {
                                           task_type.setText(map.get(Task.OTHER_TASK));
                                       }
                                   }
                                   if(getIntent().getStringExtra("FromMeasurePointActivity")!=null) {
                                       //从点巡检、保养任务中由于测点异常创建新任务调用此方法填充相应数据
                                       if("T0201".equals(IntentTaskSubClass)){
                                           CreateFromMeasurePoint(map.get("T0201"));
                                       }else {
                                           CreateFromMeasurePoint(map.get("T0202"));
                                       }
                                   }
                                   if(getIntent().getStringExtra(Constants.FLAG_CREATE_SHUNTING_TASK)!=null){
                                       //从搬车任务创建调车任务调用此方法填充相应数据
                                       CreateShuntingTask(map.get(Task.TRANSFER_MODEL_TASK));
                                   }
                                   if(getIntent().getStringExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK)!=null){
                                       //从调车任务创建搬车任务调用此方法填充相应数据
                                       CreateCarMovingTask(map.get(Task.MOVE_CAR_TASK));
                                   }
                                   //getSubTaskType(TaskClass);
                               }
                           });
                       } else {
                           ToastUtil.showToastShort(R.string.noData,mContext);
                       }
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               getDBDataLastUpdateTime();
                           }
                       });
                   }

                   @Override
                   public void failure(DatastoreException ex, String resource) {
                       ToastUtil.showToastShort(R.string.ErrorCauseByNoDataBase,mContext);
                   }
               });

    }

    private void initView() {
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.filter).setOnClickListener(this);
        ((ImageView)findViewById(R.id.filter)).setImageResource(R.mipmap.sync);
       if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(this))!=RootUtil.ROOT_WARRANTY
               &&OperatorInfo==null){
        ((TextView)findViewById(R.id.organise)).setText(R.string.ServerTeam);
        }
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.create_task));
        findViewById(R.id.edit_resume).setOnClickListener(this);

        task_subtype_name_desc = (TextView) findViewById(R.id.task_subtype_name_id);
        task_type = (DropEditText) findViewById(R.id.task_type);
        //task_type.setText(Task_Class_Name.get(TaskClass));

        simple_description=(DropEditText)findViewById(R.id.simple_description);
        task_description=(EditText)findViewById (R.id.task_description);
        task_subtype = (DropEditText) findViewById(R.id.task_subtype);
        group = (DropEditText) findViewById(R.id.group_id);
        device_name = (DropEditText) findViewById(R.id.device_name);
        targetOrganise=(DropEditText)findViewById(R.id.target_group);
        //simple_description=(DropEditText)findViewById(R.id.simple_description);
        hasEquipment=(DropEditText)findViewById(R.id.hasEquipment);

        create_task = (EditText) findViewById(R.id.create_task);
        device_num = (EditText) findViewById(R.id.device_num);
        //standard_workload = (DropEditText) findViewById(R.id.standard_workload);
        //task_description.setVisibility(View.GONE);
//        findViewById(R.id.standard_workload).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
//            }
//        });
        //create_task_action = (ImageView) findViewById(R.id.create_task_action);
        //device_num_action = (ImageView) findViewById(R.id.device_num_action);

        btn_sure = (Button) findViewById(R.id.sure);

        btn_sure.setOnClickListener(this);
        //create_task_action.setOnClickListener(this);
        //device_num_action.setOnClickListener(this);

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getResources().getString(R.string.waiting))
                .setCancellable(true);

        nfcDialog = new NFCDialog(mContext, R.style.MyDialog) {
            @Override
            public void dismissAction() {
                nfctag = 0;
            }

            @Override
            public void showAction() {

            }
        };

        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager imm =
//                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                   imm.hideSoftInputFromWindow(task_description.getWindowToken(),0);
                //SoftInputUtil.hideSoftInput(standard_workload,mContext);
            }
        });
        findViewById(R.id.task_description_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_description.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
    }

    private void getSubTaskType(String str) {

        try {
            int pos = 0;
            for (int i = 0; i < mTaskType.size(); i++) {
                if (DataUtil.isDataElementNull(mTaskType.get(i).get(DataDictionary.DATA_NAME)).equals(str)) {
                    pos = i;
                }
            }
            String pdataid = DataUtil.isDataElementNull(mTaskType.get(pos).get(DataDictionary.DATA_ID));
            String rawQuery = "select * from DataDictionary where " +
                    "DataType = 'TaskClass' and PData_ID=" + pdataid;
            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {



                @Override
                public void onSuccess(DataElement element) {
                    System.out.println(element);
                    mSubType = new ArrayList<>();
                    if (element != null && element.isArray()
                            && element.asArrayElement().size() > 0) {
                        mSubType.clear();
                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                            mSubType.add(element.asArrayElement().get(i).asObjectElement());
                            task_type_class.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("DataName")),
                                    DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("DataCode")));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                task_subtype.setVisibility(View.VISIBLE);
                                task_subtype_name_desc.setVisibility(View.VISIBLE);
                                findViewById(R.id.subTask).setVisibility(View.VISIBLE);
                                task_subtype.setDatas(mContext, mSubType, DataDictionary.DATA_NAME);

                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                task_subtype.setVisibility(View.GONE);
                                task_subtype_name_desc.setVisibility(View.GONE);
                                findViewById(R.id.subTask).setVisibility(View.GONE);
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println(throwable.getMessage());
                }
            });
        } catch (Exception e) {
            task_subtype.setVisibility(View.GONE);
            task_subtype_name_desc.setVisibility(View.GONE);
            findViewById(R.id.subTask).setVisibility(View.GONE);
            CrashReport.postCatchedException(e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_right_action) {
            finish();
        } else if (id == R.id.edit_resume) {
//            standard_workload.setFocusable(true);
//            standard_workload.setFocusableInTouchMode(true);
//            standard_workload.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);

        }
//        else if (id == R.id.create_task_action) {
//            if (mAdapter == null) {
//                showMessage(R.string.error, R.string.no_nfc);
//                return;
//            }
//            nfctag = CREATER;
//            nfcDialog.show();
//        }
//        else if (id == R.id.device_num_action) {
//            if (mAdapter == null) {
//                showMessage(R.string.error, R.string.no_nfc);
//                return;
//            }
//            nfctag = DEVICE_NUM;
//            nfcDialog.show();
//        }
        else if (id == R.id.iv_search_clear){
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
        } else if (id == R.id.sure) {
            createRequest();
        }else if(id==R.id.filter){
            getDBDataLastUpdateTime();
        }
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }


    private void createRequest() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String taskType = task_type.getText();
                String teamName = group.getText();
                String deviceName = device_name.getText();
                String createTask = create_task.getText().toString();

                String deviceNum = equipmentID;
                String taskSubType = null;
               // String standardWorkload = standard_workload.getText().toString();

                String simpledescription=simple_description.getText();
                if (View.VISIBLE == task_subtype.getVisibility()) {
                    taskSubType = task_subtype.getText();
                }

                if (taskType.equals("")) {
                    Toast.makeText(mContext, getResources().getString(R.string.tips_tasktype_post), Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (taskSubType != null) {
//                    if (taskSubType.equals("")) {
//                        Toast.makeText(mContext, getResources().getString(R.string.tips_subtype_post), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                if (createTask.equals("")) {
                    Toast.makeText(mContext, getResources().getString(R.string.tips_scan_operator_post), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (teamName.equals("")) {
                    Toast.makeText(mContext, getResources().getString(R.string.tips_team_type_post), Toast.LENGTH_SHORT).show();
                    return;
                }

             /*   if (deviceName.equals("")) {
                    Toast.makeText(mContext, getResources().getString(R.string.tips_device_name_post), Toast.LENGTH_SHORT).show();
                    return;
                }
              */

//                if (simpledescription.equals("")) {
//                    Toast.makeText(mContext, getResources().getString(R.string.tips_task_desc_post), Toast.LENGTH_SHORT).show();
//                    return;}
           //     else
                if(simpledescription==null||simpledescription.equals("")){
                    ToastUtil.showToastShort(R.string.tips_task_desc_post,mContext);
                    return;
                }
                if(simpledescription.equals(getResources().getString(R.string.other))){
//                    if(findViewById(R.id.task_description).getVisibility()==View.VISIBLE){
//                        simpledescription=task_description.getText().toString();}
                    //待修改，等待权限
                    //TODO
//                    if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=7) {
//                        if (standard_workload.getmEditText().getText().toString().equals("")){
//                            ToastUtil.showToastShort(R.string.NoWorkload,mContext);
//                            return;
//                        }
//                    }
                    simpledescription="";
                    SimpleDescriptionCode="Default";
                }else {
                    if (task_description.getText().toString().length() > 0) {
                        simpledescription += "\n";
                    }
                }
                simpledescription+=task_description.getText().toString();
                if( task_type_class.get(taskType)!=null&&task_type_class.get(taskType).equals(Task.MOVE_CAR_TASK)){
                    if(targetOrganise.getmEditText().getText().toString().equals("")){
                        ToastUtil.showToastShort(R.string.pleaseSelectTargetGroup,mContext);
                        return;
                    }
                }
//                hud.show();
                submitTask(taskType, taskSubType, "", deviceName, deviceNum, simpledescription);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        hud.dismiss();
//                        //  TipsDialog tipsDialog = new TipsDialog(mContext, R.style.MyDialog);
//                        //  tipsDialog.show();
//                    }
//                }, 1000);
            }
        });


    }

//    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
//        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
//
//        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
//        byte[] textBytes = text.getBytes(utfEncoding);
//
//        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
//        char status = (char) (utfBit + langBytes.length);
//
//        byte[] data = new byte[1 + langBytes.length + textBytes.length];
//        data[0] = (byte) status;
//        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
//        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
//
//        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
//    }

    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);

            getDataByICcardID(iccardID);



            }

       // }
    }
    private void getDataByICcardID(String iccardID){
        if (iccardID == null) {
            return;
        } else if (iccardID.equals("")) {
            return;
        }
//            if(IntentTaskSubClass!=null){
//                ToastUtil.showToastShort("不可修改设备",mContext);
//                return;
//            }
        if(nfcDialog!=null&&nfcDialog.isShowing()){
            nfcDialog.dismiss();
        }
//            if (nfctag == CREATER) {
//                nfctag = 0;
        //nfcDialog.dismiss();
        //Toast.makeText(mContext, "刷卡成功", Toast.LENGTH_SHORT).show();
//           } else if (nfctag == DEVICE_NUM) {
//                nfctag = 0;

        if(HasEquipment_map.get(hasEquipment.getText())==null||HasEquipment_map.get(hasEquipment.getText())==0){
            ToastUtil.showToastShort(R.string.pleaseSelectHasEquipment,mContext);
            return;
        }
        if(!teamId.equals("")) {
            getEquipmentNumByICcardId(iccardID);
        }else {
            ToastUtil.showToastShort(R.string.pleaseSelectGroupFirst,mContext);
        }
    }
    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final int tips,ImageView imageView){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        if(IntentTaskSubClass!=null&&searTag!=SIMPLE_DESCRIPTION){
//                                            ToastUtil.showToastShort("不可修改",mContext);
//                                            return;
//                                        }
                                        searchDataLists.clear();
                                        switch (searTag){
                                            case TASK_TYPE:
                                                searchDataLists.addAll(mTaskType);
                                                break;
                                            case TASK_SUBTYPE:
                                                searchDataLists.addAll(mSubType);
                                                break;
                                            case GROUP:
                                                searchDataLists.addAll(mTeamNamelist);
                                                break;
                                            case DEVICE_NAME:
                                                searchDataLists.addAll(mDeviceNamelist);
                                                break;

                                            case DEVICE_NUM:
                                                searchDataLists.addAll(mDeviceNumlist);
                                                break;
                                            case SIMPLE_DESCRIPTION:
                                                searchDataLists.addAll(mSimpleDescriptionList);
                                                break;
                                            case HAS_EQUIPMENT:
                                                searchDataLists.addAll(mHasEquipment);
                                                break;
//                                            case TASK_WORKLOAD:
//                                                    searchDataLists.addAll(mWorkLoadNoList);
//                                                break;
                                            case TARGET_ORGANISE:
                                                searchDataLists.addAll(mTargetGroup);
                                                break;
                                        }
                                        searchtag = searTag;
                                        if (condition != null) {
                                            if (!condition.getText().toString().equals("") && searchDataLists.size()>0) {
                                                mResultAdapter.changeData(searchDataLists, searchName);
                                                menuSearchTitle.setText(searchTitle);
                                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                                            } else {
                                                ToastUtil.showToastShort(tips,mContext);
                                            }
                                        }else {
                                            if ( searchDataLists.size() > 0) {
                                                mResultAdapter.changeData(searchDataLists, searchName);
                                                menuSearchTitle.setText(searchTitle);
                                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                                            } else {
                                                ToastUtil.showToastShort(tips,mContext);
                                            }
                                        }

                                    }
                                });

                            }
                        }

                );
        if(imageView!=null){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if(IntentTaskSubClass!=null&&searTag!=SIMPLE_DESCRIPTION){
//                            ToastUtil.showToastShort("不可修改",mContext);
//                            return;
//                        }
//                        if(BuildConfig.isDebug) {
//                            Intent it = new Intent(CreateTaskActivity.this, CaptureActivity.class);
//                            startActivityForResult(it, 1);
//                            return;
//                        }
                        searchDataLists.clear();
                        switch (searTag){
                            case TASK_TYPE:
                                searchDataLists.addAll(mTaskType);
                                break;
                            case TASK_SUBTYPE:
                                searchDataLists.addAll(mSubType);
                                break;
                            case GROUP:
                                searchDataLists.addAll(mTeamNamelist);
                                break;
                            case DEVICE_NAME:
                                searchDataLists.addAll(mDeviceNamelist);
                                break;

                            case DEVICE_NUM:
                            if(group.getmEditText().getText().toString().equals("")){
                                ToastUtil.showToastLong(R.string.pleaseSelectGroupFirst,mContext);
                                return;
                            }
                            Intent it = new Intent(CreateTaskActivity.this, CaptureActivity.class);
                            startActivityForResult(it, 1);
                            return;
                            case SIMPLE_DESCRIPTION:
                                searchDataLists.addAll(mSimpleDescriptionList);
                                break;
                            case HAS_EQUIPMENT:
                                searchDataLists.addAll(mHasEquipment);
                                break;
//                            case TASK_WORKLOAD:
//                                searchDataLists.addAll(mWorkLoadNoList);
//                                break;
                            case TARGET_ORGANISE:
                                searchDataLists.addAll(mTargetGroup);
                                break;
                        }
                        searchtag = searTag;
                        if (condition != null) {
                            if (!condition.getText().toString().equals("") && searchDataLists.size()>0) {
                                mResultAdapter.changeData(searchDataLists, searchName);
                                menuSearchTitle.setText(searchTitle);
                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                            } else {
                                ToastUtil.showToastShort(tips,mContext);
                            }
                        }else {
                            if ( searchDataLists.size() > 0) {
                                mResultAdapter.changeData(searchDataLists, searchName);
                                menuSearchTitle.setText(searchTitle);
                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                            } else {
                                ToastUtil.showToastShort(tips,mContext);
                            }
                        }

                    }
                });
            }
        });}
    }
    private void submitTask(String TaskType,String TaskSubType,String standardWorkload,String equipmentName
            ,String MachineCode,String TaskDescription){
        btn_sure.setEnabled(false);
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        if(StringUtils.isNotBlank(TaskSubType)){
            TaskType=TaskSubType;
        }
        //创建任务提交数据:创建人ID，任务类型"T01,T02"等，机台号（数组），任务描述，组别
        JsonObjectElement task=new JsonObjectElement();
        JsonObjectElement taskDetail=new JsonObjectElement();
        //获取创建人ID
        taskDetail.set("Applicant",creatorId);//任务发起人ID
        taskDetail.set("Task_ID",0);
        taskDetail.set("TaskDescr",TaskDescription);//任务描述
        if(task_type_class.get(TaskType)!=null) {//任务类型
            taskDetail.set("TaskClass", task_type_class.get(TaskType));
        }
        taskDetail.set("TaskApplicantOrg",teamId);//任务发起人组别
        taskDetail.set("Factory",getLoginInfo().getFromFactory());//任务发起人所属班组
        taskDetail.set("IsExsitTaskEquipment",HasEquipment_map.get(hasEquipment.getText()));//是否为设备相关任务
        //TODO
        if(task_type_class.get(TaskType).equals(Task.MOVE_CAR_TASK)){
            taskDetail.set("TargetTeam_ID",targetOrganiseID);
        }
        //TODO 先注释掉，待确认
        if(IntentTaskSubClass!=null){
            taskDetail.set("FromTask_ID",FromTask_ID);
            taskDetail.set("TaskClass",Task.MAINTAIN_TASK);
            taskDetail.set("TaskSubClass",IntentTaskSubClass);
                if(IntentTaskItem!=null) {
                    ArrayList<ObjectElement> TaskItemList=new ArrayList<>();
                    JsonObjectElement TaskItem=new JsonObjectElement();
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(IntentTaskItem);
                    TaskItem.set("MaintainItem_ID",jsonObjectElement.get("MaintainItem_ID").valueAsInt());
                    TaskItem.set("WorkTimeCode",DataUtil.isDataElementNull(jsonObjectElement.get("WorkTimeCode")));
                    TaskItemList.add(TaskItem);
                    JsonArrayElement jsonArrayElement=new JsonArrayElement(TaskItemList.toString());
                    task.set("TaskItem",jsonArrayElement);
                }
        }

        if(FromTask_ID!=null) {
            taskDetail.set("FromTask_ID", FromTask_ID);
        }
        if(!SimpleDescriptionCode.equals("")){
        taskDetail.set("TaskDescrCode",SimpleDescriptionCode);
        }

        //如果需要填写工作量的情况下
//        if(!WorkloadCode.equals("")){
//        JsonObjectElement workload=new JsonObjectElement();
//        workload.set("WorkTimeCode", WorkloadCode);
//            ArrayList<ObjectElement> workloadUpdateList=new ArrayList<>();
//            workloadUpdateList.add(workload);
//            JsonArrayElement updateList=new JsonArrayElement(workloadUpdateList.toString());
//            task.set("TaskItem",updateList);
//        }
        task.set("Task",taskDetail);
        if(!equipmentID.equals("")){
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set("Equipment_ID", MachineCode);
        jsonObjectElement.set("TaskEquipment_ID", 0);
        ArrayList<ObjectElement> list=new ArrayList<>();
        list.add(jsonObjectElement);
        JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
            task.set("TaskEquipment",jsonArrayElement);
        }
       //包装数据
      //填写创建人角色
        if(OperatorInfo!=null){
            task.set("UserRole_ID",RootUtil.ROOT_WARRANTY);
        }else {
            task.set("UserRole_ID",SharedPreferenceManager.getUserRoleID(this));
        }
        params.putJsonParams(task.toJson());
        HttpUtils.postWithoutCookie(this, "TaskCollection", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                btn_sure.setEnabled(true);
                if(t!=null) {
                    final JsonObjectElement data = new JsonObjectElement(t);
                    if (data.get(Data.SUCCESS).valueAsBoolean()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.SuccessCreateTask,mContext);
                                finish();
                            }
                        });
                    } else {
//                        if(!DataUtil.isDataElementNull(data.get("Msg")).equals("")){
//                            ToastUtil.showToastShort(DataUtil.isDataElementNull(data.get("Msg")),mContext);
//                        }else {
//                            ToastUtil.showToastShort(R.string.FailCreateTask,mContext);
//                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(mContext.getResources().getString(R.string.FailCreateTask)
                                        +","+DataUtil.isDataElementNull(data.get("Msg")),mContext);
                            }
                        });
                    }
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                btn_sure.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.FailCreateTaskCauseByTimeout,mContext);
                    }
                });
            }
        });
    }

    private void getSimpleDescription(String EquipmentClass){
        String DataValue2;
        if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=RootUtil.ROOT_WARRANTY){
            DataValue2="'01','03'";
        }else {
            DataValue2="'01','02'";
        }
        DataUtil.getDataFromDataBase(mContext, "EquipmentClassTrouble", EquipmentClass,DataValue2, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                ClearDescriptionList();
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for (int i=0;i<element.asArrayElement().size();i++){
                        mSimpleDescriptionList.add(element.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.noSimpleDescriptionData,mContext);
                    }
                });
            }
        });
    }
    private void getOrganiseNameAndEquipmentNameByEquipmentID(String equipmentID){
        String sql="select OrganiseName,EquipmentName,EquipmentClass,Organise_ID_Use from Equipment e,BaseOrganise b" +
                "    where Equipment_ID='" + equipmentID + "'" +
                "    and e.Organise_ID_Use=b.Organise_ID";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, new StoreCallback() {
            @Override
            public void success(final DataElement element, String resource) {
                if(element!=null){
                    if(element.isArray()){
                    if(element.asArrayElement().size()>0){
                        final ObjectElement data=element.asArrayElement().get(0).asObjectElement();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                tag=true;
                group.setText(DataUtil.isDataElementNull(data.get("OrganiseName")));
                device_name.setText(DataUtil.isDataElementNull(data.get("EquipmentName")));
                DeviceName = DataUtil.isDataElementNull(data.get("EquipmentClass"));
               // getTeamIdByOrganiseID(DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Organise_ID_Use")));
                teamId=DataUtil.isDataElementNull(data.get("Organise_ID_Use"));
                getSimpleDescription(DeviceName);
                simple_description.getmEditText().setText("");
                        }
                    });
                }
                    }
                }

            }

            @Override
            public void failure(DatastoreException ex, String resource) {

            }
        });
    }
    private void getEquipmentNumByICcardId(final String iccardID){
        String rawQuery = "SELECT * FROM Equipment WHERE  ICCardID ='" + iccardID + "'";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(DataElement dataElement) {
                if (dataElement != null && dataElement.isArray()
                        && dataElement.asArrayElement().size() > 0) {
                    final ObjectElement objectElement = dataElement.asArrayElement().get(0).asObjectElement();
                    //if (Group_ID_List.contains(DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")))) {
                    if (DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")).equals(teamId)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                device_num.setText(DataUtil.isDataElementNull(objectElement.get("AssetsID")));
                                equipmentName = DataUtil.isDataElementNull(objectElement.get("EquipmentName"));
                                device_name.getmEditText().setText(DataUtil.isDataElementNull(objectElement.get("EquipmentName")));
                                DeviceName = DataUtil.isDataElementNull(objectElement.get("EquipmentClass"));
                                //getTeamIdByOrganiseID(DataUtil.isDataElementNull(objectElement.get("Organise_ID_Use")));
                                tag = true;
                                resetDeviceName();


                            }
                        });
                        equipmentID = DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.getEquipmentNumSuccess, mContext);
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //ToastUtil.showToastShort(R.string.FailToScanEquipment, mContext);
                                showDataSyncDialog(R.string.FailToScanEquipment);
                            }
                        });
                    }
                    } else {
                             showDataSyncDialog(R.string.NoCardDetailDoYouNeedToSyncData);
                    }

            }

            @Override
            public void onFailure(Throwable throwable) {
                ToastUtil.showToastShort(R.string.NoCardDetail,mContext);
            }
        });

    }

    private void resetCretor(){
        teamId ="";
        group.getmEditText().setText("");
        equipmentName ="";
        device_name.getmEditText().setText("");
        DeviceName="";
        device_num.setText("");
        equipmentID="";
        simple_description.getmEditText().setText("");
        ClearDescriptionList();

    }

    private void resetTeam(){
        equipmentName ="";
        device_name.getmEditText().setText("");
        DeviceName="";
        device_num.setText("");
        equipmentID="";
        simple_description.getmEditText().setText("");
        ClearDescriptionList();
        getDeviceName();
    }
    private void resetEquipment(){
        equipmentName ="";
        device_name.getmEditText().setText("");
        DeviceName="";
        device_num.setText("");
        equipmentID="";
        simple_description.getmEditText().setText("");
        ClearDescriptionList();
    }
    private void resetDeviceName(){
        if(!tag) {
            device_num.setText("");
            equipmentID = "";
        }
        getSimpleDescription(DeviceName);
        simple_description.getmEditText().setText("");
        //mSimpleDescriptionList.clear();
        tag = false;

    }

    private void resetDeviceNum(){


    }

//    private void getApplicantByICcardID(String iccardID){
//        HttpParams params = new HttpParams();
//        params.put("ICCardID", iccardID);
//        showCustomDialog(R.string.loadingData);
//        HttpUtils.getWithoutCookies(this, "Token", params, new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//                try {
//                    JSONObject jsonObject = new JSONObject(t);
//                    int code = Integer.parseInt(jsonObject.get("Result").toString());
//                    boolean isSuccess = jsonObject.get("Success").equals(true);
//                    if ((code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS ||
//                            code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS_AUTO) && isSuccess) {
//
//                        String data=jsonObject.getString("Data");
//                        //  SharedPreferenceManager.setLoginData(mContext,jsonObject.get("Data").toString());
//                        Operator operator = getLoginInfo(data);
//                        if(null !=  operator){
//                            creatorId = String.valueOf(operator.getId());
//                            getTeamId(operator);
//                            resetCretor();
//
//                        }
//
//
//                    } else if (code == Constants.REQUEST_CODE_FROZEN_ACCOUNT) {
//                        ToastUtil.showToastShort(R.string.warning_message_frozen,mContext);
//                    }
//                    else if (code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_FAIL) {
//                        ToastUtil.showToastShort("无该卡信息",mContext);
//                   }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    ToastUtil.showToastShort(R.string.warning_message_error,mContext);
//                }
//                dismissCustomDialog();
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//                dismissCustomDialog();
//                ToastUtil.showToastShort(R.string.loadingFail,mContext);
//            }
//        });
//    }
    private void initSimpleDescription(){
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(DataDictionary.DATA_NAME,getResources().getString(R.string.other));
        jsonObjectElement.set(DataDictionary.DATA_CODE,"00");
        mSimpleDescriptionList.add(0,jsonObjectElement);
    }

    private void initHasEquipmentData(){
        JsonObjectElement data1=new JsonObjectElement();
        data1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.haveEquipment));
        JsonObjectElement data2=new JsonObjectElement();
        data2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.NothaveEquipment));
        mHasEquipment.add(data1);
        mHasEquipment.add(data2);
    }
    private void initData(){
//        {
//            Task_Class_Name.put(Task.REPAIR_TASK,getResources().getString(R.string.repair_tag));
//            Task_Class_Name.put(Task.MAINTAIN_TASK,getResources().getString(R.string.maintenance));
//            Task_Class_Name.put(Task.MOVE_CAR_TASK,getResources().getString(R.string.move_car));
//            Task_Class_Name.put(Task.OTHER_TASK,getResources().getString(R.string.other));
//        }
//        {
//            task_type_class.put(getResources().getString(R.string.repair_tag), Task.REPAIR_TASK);
//            task_type_class.put(getResources().getString(R.string.maintenance), Task.MAINTAIN_TASK);
//            task_type_class.put(getResources().getString(R.string.move_car), Task.MOVE_CAR_TASK);
//            task_type_class.put(getResources().getString(R.string.other), Task.OTHER_TASK);
//        }
        {
             HasEquipment_map.put(getResources().getString(R.string.haveEquipment),1);
            HasEquipment_map.put(getResources().getString(R.string.NothaveEquipment),0);
        }

        String sql;
        if(OperatorInfo!=null) {//OperatorInfo!=null即任务是从搬车或调车任务完成后，继续创建对应任务
            sql="select Organise_ID Team_ID,OrganiseName TeamName from BaseOrganise where OrganiseClass=0 and FromFactory='"+getLoginInfo().getFromFactory()+"' and OrganiseType>1";
        }else {
            sql= "select Organise_ID Team_ID,OrganiseName TeamName  from BaseOrganise where Organise_ID in(" + getLoginInfo().getOrganiseID() + ") and OrganiseClass=0 and OrganiseType>1";
        }
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                if(element!=null&&element.isArray()){
                    for(int i=0;i<element.asArrayElement().size();i++){
                        mTargetGroup.add(element.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
               ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,mContext);
            }
        });
    }
    private void CreateFromMeasurePoint(String TaskType){
        JsonObjectElement TaskEquipment=new JsonObjectElement(getIntent().getStringExtra("TaskEquipment"));
        device_num.setText(DataUtil.isDataElementNull(TaskEquipment.get(Equipment.ASSETSID)));
        equipmentName = DataUtil.isDataElementNull(TaskEquipment.get(Equipment.EQUIPMENT_NAME));
        device_name.getmEditText().setText(DataUtil.isDataElementNull(TaskEquipment.get(Equipment.EQUIPMENT_NAME)));
        equipmentID = DataUtil.isDataElementNull(TaskEquipment.get(Equipment.EQUIPMENT_ID));
        //TODO  改为维护
        task_type.setText(TaskType);
        DisableView();
        getOrganiseNameAndEquipmentNameByEquipmentID(equipmentID);
    }
    private void CreateShuntingTask(String TaskType){
        task_type.setText(TaskType);
        task_type.getmEditText().setEnabled(false);
        task_type.getDropImage().setEnabled(false);
    }
    private void CreateCarMovingTask(String TaskType){
        task_type.setText(TaskType);
        task_type.getmEditText().setEnabled(false);
        task_type.getDropImage().setEnabled(false);
        findViewById(R.id.target_group_layout).setVisibility(View.VISIBLE);
//        resetCretor();
//        getTeamIdByOrganiseID("ALL",false);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        switch (requestCode)
        {
            case 1:
                if (data != null)
                {
                    String result = data.getStringExtra("result");
                    if (result != null){
                        //ToastUtil.showToastLong(result,mContext);
                        getDataByICcardID(result);
                    }
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void showDataSyncDialog(final int resId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setMessage(resId);
                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDBDataLastUpdateTime();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }
    private void ClearDescriptionList(){
        mSimpleDescriptionList.clear();
        initSimpleDescription();
        if(!task_type.getText().equals("")&&task_type_class.get(task_type.getText())!=null
                &&task_type_class.get(task_type.getText()).equals(Task.GROUP_ARRANGEMENT)){
            if(!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
                mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
            }
        }
    }
    private void DisableView(){
        task_type.getmEditText().setEnabled(false);
        task_type.getDropImage().setEnabled(false);
        group.getmEditText().setEnabled(false);
        group.getDropImage().setEnabled(false);
        device_name.getmEditText().setEnabled(false);
        device_name.getDropImage().setEnabled(false);
        hasEquipment.getmEditText().setEnabled(false);
        hasEquipment.getDropImage().setEnabled(false);
    }
    private void getGroupArrangeSimpleDesList(){
        if(mGroupArrangeDescriptionList.size()<=0) {
            DataUtil.getDataFromDataBase(mContext, "EquipmentClassTrouble", "ZNAP000", "'01','02','03'", new StoreCallback() {
                @Override
                public void success(DataElement element, String resource) {
                    for (DataElement e : element.asArrayElement()) {
                        mGroupArrangeDescriptionList.add(e.asObjectElement());
                    }
                    if (!mSimpleDescriptionList.containsAll(mGroupArrangeDescriptionList)) {
                        mSimpleDescriptionList.addAll(mGroupArrangeDescriptionList);
                    }
                }

                @Override
                public void failure(DatastoreException ex, String resource) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToastLong(R.string.FailGetDataPleaseRestartApp, mContext);
                        }
                    });
                }
            });
        }
    }
}


