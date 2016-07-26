package com.emms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefRecord;
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
import com.emms.push.PushService;
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
import com.emms.util.SharedPreferenceManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minidev.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.xml.transform.TransformerFactory;

/**
 * Created by jaffer.deng on 2016/6/7.
 */
public class CreateTaskActivity extends NfcActivity implements View.OnClickListener {

    private Context mContext;

    private DropEditText task_type, task_subtype, group, device_name;
    private EditText create_task, task_description, device_num;
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
    public final static int TASK_DESCRIPTION = 7;


    private ArrayList<String> mDeviceTypelist;

    private ArrayList<ObjectElement> mTaskType = new ArrayList<ObjectElement>();
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> mSubType = new ArrayList<>();
    private ArrayList<ObjectElement> mTeamNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> mDeviceNamelist = new ArrayList<>();
    private ArrayList<ObjectElement> mDeviceNumlist = new ArrayList<>();
    private String creatorId;


    private NFCDialog nfcDialog;
    private DrawerLayout mDrawer_layout;

    private int nfctag = 0;
    private int  searchtag =0;

    private String teamId ="";
    private String equipmentName ="";
    private String equipmentID = "";
    private HashMap<String,String> task_type_class=new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        mContext = CreateTaskActivity.this;
        initView();
        initSearchView();
        initEvent();
        mDrawer_layout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();

    }

    private void initSearchView() {
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        searchBox = (EditText) findViewById(R.id.et_search);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSearchview = true ;
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case TASK_TYPE:
                                    getSubTaskType(searchResult);
                                    task_type.getmEditText().setText(searchResult);
                                    break;
                                case TASK_SUBTYPE:
                                    task_subtype.getmEditText().setText(searchResult);
                                    break;
                                case GROUP:
                                    teamId =mResultAdapter.getItem(inPosition).get(Team.TEAM_ID).valueAsString();
                                    group.getmEditText().setText(searchResult);
                                    break;
                                case DEVICE_NAME:
                                    equipmentName =mResultAdapter.getItem(inPosition).get(Equipment.EQUIPMENT_NAME).valueAsString();
//                                    device_name.getmEditText().setSingleLine(true);
                                    device_name.getmEditText().setText(searchResult);
                                    break;
                                case DEVICE_NUM:
                                    device_num.setText(searchResult);
                                    equipmentID=mResultAdapter.getItem(inPosition).get(Equipment.EQUIPMENT_ID).valueAsString();
                                    break;
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "出错了", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clearBtn.setOnClickListener(this);
        searchDataLists = new ArrayList<ObjectElement>();
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initEvent() {

        initLoginData();

        getTaskType();//获取任务类型 基本不用改
        initDropSearchView(null, task_type.getmEditText(), getResources().
                        getString(R.string.title_search_task_type), DataDictionary.DATA_NAME,
                TASK_TYPE, "获取数据失败");

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

        task_subtype.getDropImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!task_type.getText().equals("")) {
                            if (mSubType.size() > 0) {
                                task_subtype.setDatas(mContext, mSubType, DataDictionary.DATA_NAME);
                                task_subtype.showOnclik();
                            }
                        } else {
                            Toast.makeText(mContext, "请先选择任务类型", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        initDropSearchView(task_type.getmEditText(), task_subtype.getmEditText(),
                getResources().
                        getString(R.string.title_search_task_subtype), DataDictionary.DATA_NAME, TASK_SUBTYPE, "请先选择任务类型");

        initDropSearchView(group.getmEditText(), device_name.getmEditText(),
                getResources().
                        getString(R.string.title_search_equipment_name), Equipment.EQUIPMENT_NAME, DEVICE_NAME, "请先选择组别");
        group.getmEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                device_name.getmEditText().setText("");
                getDeviceName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initDropSearchView(create_task, group.getmEditText(),
                getResources().
                        getString(R.string.title_search_group), Team.TEAMNAME, GROUP, "请先扫描工卡获取创建人信息");

        group.getDropImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!create_task.getText().toString().equals("") && mTeamNamelist.size() > 0) {
                            group.setDatas(mContext, mTeamNamelist, Team.TEAMNAME);
                            group.showOnclik();
                            isSearchview =false ;
                        } else {
                            Toast.makeText(mContext, "请先扫描工卡获取创建人信息", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


        device_name.getDropImage().
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!group.getText().equals("")) {
                                            if (mDeviceNamelist != null) {
                                                if (mDeviceNamelist.size() > 0) {
                                                    device_name.setDatas(mContext, mDeviceNamelist, Equipment.EQUIPMENT_NAME);
                                                    device_name.showOnclik();
                                                    isSearchview = false ;
                                                }
                                            } else {
                                                Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Toast.makeText(mContext, "请先选择组别", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }

                        }

                );


        device_name.getmEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")){
                    initDeviceNum();
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initDropSearchView(device_name.getmEditText(), device_num,
                getResources().
                        getString(R.string.title_search_equipment_nun), Equipment.ORACLE_ID, DEVICE_NUM, "请先选择设备名称，或刷设备卡获取机台号");

    }

    private void initDeviceNum() {
        if ( !isSearchview){
            equipmentName = mDeviceNamelist.get(device_name.getSelectPosition()).get(Equipment.EQUIPMENT_NAME).valueAsString();
        }
        try {
            String rawQuery = "SELECT * FROM Equipment WHERE EquipmentName=" + "'" + equipmentName
                    + "'" + " AND Organise_ID_use =" + teamId ;
            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {

                @Override
                public void onSuccess(DataElement dataElement) {
                    mDeviceNumlist = new ArrayList<ObjectElement>();
                    if (dataElement != null && dataElement.isArray()
                            && dataElement.asArrayElement().size() > 0) {
                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
                            mDeviceNumlist.add(dataElement.asArrayElement().get(i).asObjectElement());
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "目前该设备没有机台号", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "目前该设备没有机台号", Toast.LENGTH_SHORT).show();
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
        if (!isSearchview) {
            teamId = mTeamNamelist.get(group.getSelectPosition()).get("Team_ID").valueAsString();
        }
        String rawQuery ="select distinct EquipmentName from Equipment where Organise_ID_use ="+teamId+" and EquipmentName is not null";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                System.out.println(element);
                mDeviceNamelist = new ArrayList<ObjectElement>();
                try {
                    if (element != null && element.isArray()
                            && element.asArrayElement().size() > 0) {
                        mDeviceNamelist.clear();
                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                            mDeviceNamelist.add(element.asArrayElement().get(i)
                                    .asObjectElement());
                        }
                    } else {
                        Toast.makeText(mContext, "程序数据库出错，请重新登陆", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

    }
    private void initLoginData() {
        Operator operator = getLoginInfo();
        if(null !=  operator){
            creatorId = String.valueOf(operator.getId());
            getTeamId(operator);
        }
    }
    private void getTeamId(Operator operator) {


        String teamIDStr = "";
        String teamNameStr = "";
        if (null == operator) {
            //get userData from server

        } else {
            create_task.setText(operator.getName());  //创建人名

            String rawQuery = "select Organise_ID Team_ID,OrganiseName TeamName  from BaseOrganise where Organise_ID in(" + operator.getOrganiseID() + ")";
            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {

                @Override
                public void onSuccess(DataElement dataElement) {
                    mTeamNamelist = new ArrayList<ObjectElement>();
                    if (dataElement != null && dataElement.isArray()
                            && dataElement.asArrayElement().size() > 0) {
                        for (int i = 0; i < dataElement.asArrayElement().size(); i++) {
                            mTeamNamelist.add(dataElement.asArrayElement().get(i).asObjectElement());
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "未找到操作员的分组", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }

                @Override
                public void onFailure(Throwable throwable) {
                    throwable.printStackTrace();
                    System.out.println(throwable.getMessage());
                }
            });
        }




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

        String rawQuery = "select * from DataDictionary " +
                "where DataType = 'TaskClass' and PData_ID = 0";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(DataElement element) {
                System.out.println(element);
                if (element != null && element.isArray()
                        && element.asArrayElement().size() > 0) {
                    searchDataLists.clear();
                    for (int i = 0; i < element.asArrayElement().size(); i++) {
                        mTaskType.add(element.asArrayElement().get(i).asObjectElement());
                        task_type_class.put(element.asArrayElement().get(i).asObjectElement().get("DataName").valueAsString(),
                                element.asArrayElement().get(i).asObjectElement().get("DataCode").valueAsString());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            task_type.setDatas(mContext, mTaskType,DataDictionary.DATA_NAME);
                        }
                    });
                } else {
                    Toast.makeText(mContext, "程序数据库出错，请重新登陆", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });


    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.create_task));
        findViewById(R.id.edit_resume).setOnClickListener(this);

        task_subtype_name_desc = (TextView) findViewById(R.id.task_subtype_name_id);
        task_type = (DropEditText) findViewById(R.id.task_type);
        task_subtype = (DropEditText) findViewById(R.id.task_subtype);
        group = (DropEditText) findViewById(R.id.group_id);
        device_name = (DropEditText) findViewById(R.id.device_name);

        create_task = (EditText) findViewById(R.id.create_task);
        device_num = (EditText) findViewById(R.id.device_num);
        task_description = (EditText) findViewById(R.id.edit_task_description);

        create_task_action = (ImageView) findViewById(R.id.create_task_action);
        device_num_action = (ImageView) findViewById(R.id.device_num_action);

        btn_sure = (Button) findViewById(R.id.sure);

        btn_sure.setOnClickListener(this);
        create_task_action.setOnClickListener(this);
        device_num_action.setOnClickListener(this);

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getResources().getString(R.string.waiting))
                .setCancellable(true);

        nfcDialog = new NFCDialog(mContext, R.style.MyDialog) {
            @Override
            public void dismissAction() {
                nfctag = 0;
            }
        };
    }

    private void getSubTaskType(String str) {

        try {
            int pos = 0;
            for (int i = 0; i < mTaskType.size(); i++) {
                if (mTaskType.get(i).get(DataDictionary.DATA_NAME).valueAsString().equals(str)) {
                    pos = i;
                }

            }
            String pdataid = mTaskType.get(pos).get(DataDictionary.DATA_ID).valueAsString();
            String rawQuery = "select * from DataDictionary where " +
                    "DataType = 'TaskClass' and PData_ID=" + pdataid;
            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                    EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
            Futures.addCallback(elemt, new FutureCallback<DataElement>() {



                @Override
                public void onSuccess(DataElement element) {
                    System.out.println(element);
                    mSubType = new ArrayList<ObjectElement>();
                    if (element != null && element.isArray()
                            && element.asArrayElement().size() > 0) {
                        mSubType.clear();
                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                            mSubType.add(element.asArrayElement().get(i).asObjectElement());
                            task_type_class.put(element.asArrayElement().get(i).asObjectElement().get("DataName").valueAsString(),
                                    element.asArrayElement().get(i).asObjectElement().get("DataCode").valueAsString());
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
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_right_action) {
            finish();
        } else if (id == R.id.edit_resume) {
            task_description.setFocusable(true);
            task_description.setFocusableInTouchMode(true);
            task_description.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);

        } else if (id == R.id.create_task_action) {
            if (mAdapter == null) {
                showMessage(R.string.error, R.string.no_nfc);
                return;
            }
            nfctag = CREATER;
            nfcDialog.show();
        } else if (id == R.id.device_num_action) {
            if (mAdapter == null) {
                showMessage(R.string.error, R.string.no_nfc);
                return;
            }
            nfctag = DEVICE_NUM;
            nfcDialog.show();
        }else if (id == R.id.iv_search_clear){
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
        } else if (id == R.id.sure) {
            createRequest();
        }
    }

    private void showMessage(int title, int message) {
        mDialog.setTitle(title);
        mDialog.setMessage(getText(message));
        mDialog.show();
    }

    private android.os.Handler mHandler = new android.os.Handler();

    private void createRequest() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String taskType = task_type.getText();
                String teamName = group.getText();
                String deviceName = device_name.getText();
                String createTask = create_task.getText().toString();

                String taskDesc = task_description.getText().toString();
                String deviceNum = equipmentID;
                String taskSubType = null;
                String description = task_description.getText().toString();
                if (View.VISIBLE == task_subtype.getVisibility()) {
                    taskSubType = task_subtype.getText();
                }

                if (taskType.equals("")) {
                    Toast.makeText(mContext, getResources().getString(R.string.tips_tasktype_post), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (taskSubType != null) {
                    if (taskSubType.equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.tips_subtype_post), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
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

                if (taskDesc.equals("")) {
                    Toast.makeText(mContext, getResources().getString(R.string.tips_task_desc_post), Toast.LENGTH_SHORT).show();
                    return;
                }
                hud.show();
                submitTask(taskType, taskSubType, teamId, deviceName, deviceNum, description);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hud.dismiss();
                        //  TipsDialog tipsDialog = new TipsDialog(mContext, R.style.MyDialog);
                        //  tipsDialog.show();
                    }
                }, 1000);
            }
        });


    }

    private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);

        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);


            if (iccardID == null) {
                return;
            } else if (iccardID.equals("")) {
                return;
            }
            if (nfctag == CREATER) {
                nfctag = 0;
                HttpParams params = new HttpParams();
                params.put("ICCardID", iccardID);

                HttpUtils.get(this, "Token", params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        try {
                            JSONObject jsonObject = new JSONObject(t);
                            int code = Integer.parseInt(jsonObject.get("Result").toString());
                            boolean isSuccess = jsonObject.get("Success").equals(true);
                            if ((code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS ||
                                    code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS_AUTO) && isSuccess) {

                                String data=jsonObject.getString("Data");
                                Operator operator = getLoginInfo(data);
                                if(null !=  operator){
                                    creatorId = String.valueOf(operator.getId());
                                    getTeamId(operator);
                                }


                            } else if (code == Constants.REQUEST_CODE_FROZEN_ACCOUNT) {
                                Toast.makeText(mContext, getResources().getString(R.string.warning_message_frozen), Toast.LENGTH_SHORT).show();
                            } else if (code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_FAIL) {
                                Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                    }
                });


                nfcDialog.dismiss();
                Toast.makeText(mContext, "刷卡成功", Toast.LENGTH_SHORT).show();
            } else if (nfctag == DEVICE_NUM) {
                nfctag = 0;

                String rawQuery = "SELECT * FROM Equipment WHERE  ICCardID ='" + iccardID + "'";
                ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                        EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
                Futures.addCallback(elemt, new FutureCallback<DataElement>() {

                    @Override
                    public void onSuccess(DataElement dataElement) {

                        if (dataElement != null && dataElement.isArray()
                                && dataElement.asArrayElement().size() > 0) {
                            ObjectElement objectElement = dataElement.asArrayElement().get(0).asObjectElement();
                            device_num.setText(objectElement.get(Equipment.ASSETSID).valueAsString());
                            nfcDialog.dismiss();
                            Toast.makeText(mContext, "刷卡成功", Toast.LENGTH_SHORT).show();

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "目前该设备没有机台号", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });





            }

        }
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final String tips){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
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

                                        }
                                        searchtag = searTag;
                                        if (condition != null) {
                                            if (!condition.getText().toString().equals("") && searchDataLists.size()>0) {
                                                mResultAdapter.changeData(searchDataLists, searchName);
                                                menuSearchTitle.setText(searchTitle);
                                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                                            } else {
                                                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            if ( searchDataLists.size() > 0) {
                                                mResultAdapter.changeData(searchDataLists, searchName);
                                                menuSearchTitle.setText(searchTitle);
                                                mDrawer_layout.openDrawer(Gravity.RIGHT);
                                            } else {
                                                Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                });

                            }
                        }

                );
    }
    private void submitTask(String TaskType,String TaskSubType,String teamId,String equipmentName
            ,String MachineCode,String TaskDescription){
        btn_sure.setEnabled(false);
        HttpParams params=new HttpParams();
        if(StringUtils.isNotBlank(TaskSubType)){
            TaskType=TaskSubType;
        }
        //创建任务提交数据:创建人ID，任务类型"T01,T02"等，机台号（数组），任务描述，组别
        JsonObjectElement task=new JsonObjectElement();
        JsonObjectElement taskDetail=new JsonObjectElement();
        //获取创建人ID
        taskDetail.set("Applicant",creatorId);
        taskDetail.set("Task_ID",0);
        taskDetail.set("TaskDescr",TaskDescription);
        taskDetail.set("TaskClass", task_type_class.get(TaskType));
        taskDetail.set("TaskApplicantOrg",teamId);

        JsonArray jsonArray=new JsonArray();
        JsonObject JsonObject=new JsonObject();
        JsonObject.addProperty("Equipment_ID", MachineCode);
        JsonObject.addProperty("TaskEquipment_ID", 0);
        jsonArray.add(JsonObject);

        JSONArray jsonArray1=new JSONArray();
        jsonArray1.add(JsonObject);

        JsonArrayElement jsonArrayElement=new JsonArrayElement(jsonArray);
        for(int i=0;i<jsonArrayElement.size();i++){

        }
       //包装数据
        task.set("Task",taskDetail);
        task.set("TaskEquipment",jsonArray.toString());


        params.putJsonParams(task.toJson());
        HttpUtils.post(this, "TaskCollection", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                btn_sure.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateTaskActivity.this, "任务创建成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                btn_sure.setEnabled(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateTaskActivity.this,"任务创建失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
