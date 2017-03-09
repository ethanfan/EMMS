package com.emms.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.adapter.commandAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.ui.ChangeEquipmentDialog;
import com.emms.ui.EquipmentCompleteListener;
import com.emms.ui.EquipmentSummaryDialog;
import com.emms.ui.ExpandGridView;
import com.emms.ui.HorizontalListView;
import com.emms.ui.MyListView;
import com.emms.ui.NFCDialog;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.ui.ScrollViewWithListView;
import com.emms.util.AnimateFirstDisplayListener;
import com.emms.util.BaseData;
import com.emms.util.Bimp;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.FileUtils;
import com.emms.util.ListViewUtility;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaffer.deng on 2016/6/22.
 * 任务详情界面，用以显示任务的设备，图片,参与人，总结，评分，工作量等，并提供设备状态修改动作入口
 * 是app的核心界面之一
 */
public class TaskDetailsActivity extends NfcActivity implements View.OnClickListener {

    public class ViewHolder {
        TextView deviceCountTextView;
        TextView dealCountTextView;
    }
    private TextView fault_type,fault_description,repair_status;
    private ViewHolder mHolder = new ViewHolder();
    private ScrollViewWithListView mListview;
    private ExpandGridView noScrollgridview;
    private GridAdapter adapter;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas=new ArrayList<>();
    private Context mContext=this;
    private PopMenuTaskDetail popMenuTaskDetail;
    private String TaskSubClass;
   // private ChangeEquipmentDialog changeEquipmentDialog;
    private String TaskDetail = null;//任务详细
    private String TaskClass=null;//任务类型
    private Long taskId = null;//任务ID
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private Map<String, Object> deviceCountMap = new HashMap<>();//设备数量
    private ArrayList<String> TaskDeviceIdList = new ArrayList<>();//设备ID列表
    private Map<String, String> Task_DeviceId_TaskEquipmentId = new HashMap<>();//设备ID对应TaskEquipmentID映射
    private Map<String,String> TaskDeviceID_Name=new HashMap<>();
    private int TaskStatus=-1;
    private boolean getEquipmentListFail=false;//约束网络访问是否成功的tag
    //0-开始，1-暂停，2-领料，3-待料，4-结束

    private final String STATUS_DONE = "2";
    private String Main_person_in_charge_Operator_id;//任务主负责人ID
    private HashMap<String, String> taskEquipmentStatus = new HashMap<>();
    private HashMap<String, String> Equipment_Operator_Status_Name_ID_map= new HashMap<>();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类

    private static final int MSG_UPDATE_DEVICE_SUM_INFO = 10;
    private HashMap<String,HashMap<String,Integer>>TaskEquipment_OperatorID_Status=new HashMap<>();//任务设备参与人状态map
    private HashMap<String,String> Euqipment_ID_STATUS_map=new HashMap<>();
    private boolean isTaskHistory=false;
    private boolean HasTaskEquipment=true;
    private ArrayList<String> OrganiseList=new ArrayList<>();
    private String FromFragment;
    private NFCDialog nfcDialog;
    private boolean nfcDialogTag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        TaskDetail = getIntent().getStringExtra("TaskDetail");
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        taskId = Long.valueOf(getIntent().getStringExtra(Task.TASK_ID));
        TaskStatus=getIntent().getIntExtra("TaskStatus",-1);
        isTaskHistory=getIntent().getBooleanExtra("isTaskHistory",false);
        TaskSubClass=getIntent().getStringExtra(Task.TASK_SUBCLASS);
        FromFragment=getIntent().getStringExtra("FromFragment");
        if(TaskDetail!=null) {
            JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
            if(jsonObjectElement.get("IsExsitTaskEquipment")!=null) {
                HasTaskEquipment = jsonObjectElement.get("IsExsitTaskEquipment").valueAsBoolean();
            }
            if(jsonObjectElement.get("OperatorOrganise_ID")!=null){
                //OrganiseList任务创建人的所属组别
                Collections.addAll(OrganiseList,DataUtil.isDataElementNull(jsonObjectElement.get("OperatorOrganise_ID")).split(","));
            }
        }
        //初始化imageLoader
        options = new DisplayImageOptions.Builder().cacheInMemory(false) // 设置下载的图片是否缓存在内存中
//                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE)
                .showImageOnLoading(R.mipmap.bg_btn)
                // .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(TaskDetailsActivity.this));

        DataUtil.getDataFromDataBase(this, "TaskEquipmentStatus", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for(int i=0;i<element.asArrayElement().size();i++){
                    taskEquipmentStatus.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                }
                DataUtil.getDataFromDataBase(mContext, "TaskOperatorStatus", new StoreCallback() {
                    @Override
                    public void success(DataElement element, String resource) {
                        for(int i=0;i<element.asArrayElement().size();i++){
                            Equipment_Operator_Status_Name_ID_map.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                                    DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initView();
                                initDatas();
                                //initEvent();
                            }
                        });
                    }
                    @Override
                    public void failure(DatastoreException ex, String resource) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,mContext);
                            }
                        });
                    }
                });
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,mContext);
                    }
                });
            }
        });
    }
    private void initEvent() {
        Bimp.bmp.clear();
        taskAdapter = new TaskAdapter(datas) {

            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
              //  if (convertView == null) {
                    convertView = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.item_order_details, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.id_participant);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num_details);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name_details);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_start_time_details);
                    holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time_details);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state_details);
                    holder.listView=(MyListView) convertView.findViewById(R.id.equipment_opeartor_list);
              //      convertView.setTag(holder);
              //  } else {
              //      holder = (TaskViewHolder) convertView.getTag();
             //   }
                //显示设备参与人状态
                if(datas.get(position).get("TaskEquipmentOperatorList")!=null&&
                        datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().size()>0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv_creater.setVisibility(View.GONE);
                            final ArrayList<ObjectElement> obj=new ArrayList<>();
                            for(int j=0;j< datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().size();j++){
                                obj.add( datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().get(j).asObjectElement());
                            }
                            if(holder.listView.getAdapter()!=null){
                                ((TaskAdapter)holder.listView.getAdapter()).notifyDataSetChanged();
                            }
                            else {
                                holder.listView.setAdapter(new TaskAdapter(obj) {
                                    @Override
                                    public View getCustomView(View convertView1, int position1, ViewGroup parent1) {
                                        TaskViewHolder holder1;
                                        if (convertView1 == null) {
                                            holder1 = new TaskViewHolder();
                                            convertView1 = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.item_equipment_operator_status, parent1, false);
                                            holder1.tv_creater = (TextView) convertView1.findViewById(R.id.operator_name);
                                            holder1.tv_task_state = (TextView) convertView1.findViewById(R.id.operator_status);
                                           convertView1.setTag(holder1);
                                       } else {
                                            holder1 = (TaskViewHolder) convertView1.getTag();
                                       }
                                        holder1.tv_creater.setText(DataUtil.isDataElementNull(obj.get(position1).get("Name")));
                                        holder1.tv_task_state.setText(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(obj.get(position1).get("Status"))));
                                        return convertView1;
                                    }
                                });
                             }
                            ListViewUtility.setListViewHeightBasedOnChildren(holder.listView);
                        }
                    });
                }else{
                    holder.tv_creater.setVisibility(View.VISIBLE);
                    holder.tv_creater.setText(getResources().getString(R.string.no_creater));
                }
                holder.tv_device_num.setText(DataUtil.isDataElementNull(datas.get(position).get("AssetsID")));
                holder.tv_device_name.setText(DataUtil.isDataElementNull(datas.get(position).get(Equipment.EQUIPMENT_NAME)));
                //String createTime = LongToDate.longPointDate(datas.get(position).get(Maintain.CREATED_DATE_FIELD_NAME).valueAsLong());
                String createTime = DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("StartTime")));
                holder.tv_create_time.setText(createTime);
                //String endTime = LongToDate.longPointDate(datas.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsLong());

                String equipmentStatus = DataUtil.isDataElementNull(datas.get(position).get("Status"));

                String endTime = "";
                if (STATUS_DONE.equals(equipmentStatus)) {
                    endTime = DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("FinishTime")));
                }
                holder.tv_end_time.setText(endTime);
                //  holder.tv_task_state.setText(equipmentStatus);
                holder.tv_task_state.setText(taskEquipmentStatus.get(equipmentStatus));
                if(TaskSubClass!=null&&TaskClass!=null&&TaskStatus==1&&TaskClass.equals(Task.MAINTAIN_TASK)) {
                    //维护任务且为处理中状态的情况下初始化，点击进入测点列表
                    convertView.setOnClickListener(new AdapterView.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!DataUtil.isDataElementNull(datas.get(position).get("Status")).equals("0")) {
                                //设备状态为有人参与操作的情况下调用
                                Intent intent = new Intent(mContext, MeasurePointActivity.class);
                                intent.putExtra(Task.TASK_ID, taskId.toString());
                                intent.putExtra("TaskEquipment", datas.get(position).toString());
                                intent.putExtra("isMainPersonInTask", RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()), Main_person_in_charge_Operator_id));
                                intent.putExtra("EquipmentStatus", STATUS_DONE.equals(DataUtil.isDataElementNull(datas.get(position).get("Status"))));
                                if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                    intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                }
                                startActivity(intent);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastShort(R.string.pleaseScanEquipmentCard,mContext);
                                    }
                                });
                            }
                        }
                    });
                }
                return convertView;
            }

        };
        mListview.setAdapter(taskAdapter);
    }
    private MyListView TaskOperatorListView;
    private ArrayList<ObjectElement> TaskOperatorList=new ArrayList<>();
    private boolean isGetTaskOperatorListSuccess=false;
    private TaskAdapter TaskOperatorAdapter;
    private void initDatas() {
        //任务有设备
        if(HasTaskEquipment) {
            initEvent();
            getTaskEquipmentFromServerByTaskId();
        }else {
            //任务无设备
            findViewById(R.id.NoEquipmentLayout).setVisibility(View.VISIBLE);
            TaskOperatorListView=(MyListView)findViewById(R.id.taskOperatorStatus);

            TaskOperatorAdapter= new TaskAdapter(TaskOperatorList) {
                @Override
                public View getCustomView(View convertView, int position1, ViewGroup parent1) {
                    TaskViewHolder holder1;
                    if (convertView == null) {
                        holder1 = new TaskViewHolder();
                        convertView = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.item_equipment_operator_status, parent1, false);
                        holder1.tv_creater = (TextView) convertView.findViewById(R.id.operator_name);
                        holder1.tv_task_state = (TextView) convertView.findViewById(R.id.operator_status);
                        convertView.setTag(holder1);
                    } else {
                        holder1 = (TaskViewHolder) convertView.getTag();
                    }
                    holder1.tv_creater.setText(DataUtil.isDataElementNull(TaskOperatorList.get(position1).get("Name")));
                    holder1.tv_task_state.setText(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(TaskOperatorList.get(position1).get("Status"))));
                    return convertView;
                }
            };
            TaskOperatorListView.setAdapter(TaskOperatorAdapter);
            getTaskOperatorStatus();
            if(RootUtil.rootStatus(TaskStatus,1)
                    && getIntent().getStringExtra("FromProcessingFragment")!=null) {
                findViewById(R.id.changeTaskOperatorStatus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isGetTaskOperatorListSuccess) {
                            getTaskOperatorStatus();
                            return;
                        }
                        boolean tag = false;
                        int TaskOperator_ID = 0;
                        int TaskOperator_Status=0;
                        for (int i = 0; i < TaskOperatorList.size(); i++) {
                            if (DataUtil.isDataElementNull(TaskOperatorList.get(i).get(Operator.OPERATOR_ID)).equals(String.valueOf(getLoginInfo().getId()))) {
                                tag = true;
                                TaskOperator_ID = TaskOperatorList.get(i).get("TaskOperator_ID").valueAsInt();
                                TaskOperator_Status=TaskOperatorList.get(i).get("Status").valueAsInt();
                                break;
                            }
                        }
                        if (tag) {
                            if(TaskOperator_Status==1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastLong(R.string.CanNotChangeOperatorStatus,mContext);
                                    }
                                });
                                return;
                            }
                            ChangeEquipmentDialog dialog = new ChangeEquipmentDialog(mContext, R.layout.dialog_equipment_status, R.style.MyDialog,
                                    RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId())
                                            , Main_person_in_charge_Operator_id),
                                    true, true,TaskSubClass!=null,null,TaskOperator_Status,0);
                            dialog.setTaskOperatorID(TaskOperator_ID);
                            dialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                @Override
                                public void onsubmit() {
                                    getTaskOperatorStatus();
                                }
                            });
                            dialog.show();
                        } else {
                            ChangeTaskOperatorStatus();
                        }
                    }
                });
            }else {
                findViewById(R.id.changeTaskOperatorStatus).setVisibility(View.GONE);
            }
        }
        getTaskAttachmentDataFromServerByTaskId();
    }

    private void initView() {

        mHolder.deviceCountTextView = (TextView) findViewById(R.id.device_count);
        mHolder.dealCountTextView = (TextView) findViewById(R.id.deal_count);

        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.task_details));
        ImageView menuImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        if(RootUtil.rootStatus(TaskStatus,1)&&!isTaskHistory){
        menuImageView.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_bar_left).setVisibility(View.VISIBLE);
        }else{
            menuImageView.setVisibility(View.GONE);
        }
        menuImageView.setOnClickListener(this);
        mListview = (ScrollViewWithListView) findViewById(R.id.problem_count);
        noScrollgridview = (ExpandGridView) findViewById(R.id.picture_containt);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        {
            JsonObjectElement taskDetail = new JsonObjectElement(TaskDetail);
            ((TextView) findViewById(R.id.task_group)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.ORGANISE_NAME)));
            ((TextView) findViewById(R.id.task_ID)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_ID)));
            ((TextView) findViewById(R.id.task_start_time)).setText(DataUtil.utc2Local(DataUtil.isDataElementNull(taskDetail.get(Task.START_TIME))));
            ((TextView) findViewById(R.id.task_create_time)).setText(DataUtil.utc2Local(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT_TIME))));
            ((TextView) findViewById(R.id.task_creater)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT)));
            ((TextView) findViewById(R.id.task_description)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_DESCRIPTION)));
            ((TextView) findViewById(R.id.target_group)).setText(DataUtil.isDataElementNull(taskDetail.get("TargetTeam")));
            if(TaskClass!=null&&TaskClass.equals(Task.MOVE_CAR_TASK)){
                findViewById(R.id.target_group_tag).setVisibility(View.VISIBLE);
                findViewById(R.id.target_group).setVisibility(View.VISIBLE);
            }
            String checkStatus=DataUtil.isDataElementNull(taskDetail.get("CheckStatus"));
            if(isTaskHistory) {
                if ("2".equals(checkStatus)
                        || "3".equals(checkStatus)
                        || "3".equals(DataUtil.isDataElementNull(taskDetail.get("Status")))) {
                    findViewById(R.id.task_verify_person_tag).setVisibility(View.VISIBLE);
                    findViewById(R.id.task_verify_person).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.task_verify_person)).setText(DataUtil.isDataElementNull(taskDetail.get("CheckOperator")));
//                    ((TextView)findViewById(R.id.task_verify_time)).setText(DataUtil.isDataElementNull(taskDetail.get("CheckOperator")));
                    if(!"3".equals(checkStatus)) {
                        findViewById(R.id.task_verify_reason_tag).setVisibility(View.VISIBLE);
                        findViewById(R.id.task_verify_reason).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.task_verify_reason)).setText(DataUtil.isDataElementNull(taskDetail.get("Summary")));
                    }
                }
            }
            Main_person_in_charge_Operator_id = DataUtil.isDataElementNull(taskDetail.get("MainOperator_ID"));
        }
        adapter = new GridAdapter(this);
//        adapter.update1();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (arg2 == dataList.size()) {
                    if(TaskStatus!=1||isTaskHistory){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.OnlyDealingTaskCanAddPhoto,mContext);
                            }
                        });
                        return;
                    }
                    if(dataList.size()>=5){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.pictureNumLimit,mContext);
                            }
                        });
                        return;
                    }
                    new PopupWindows(mContext, noScrollgridview);
                } else {
                    ImageView image = (ImageView) arg1.findViewById(R.id.item_grida_image);
                    imageClick(image);

//                    Intent intent = new Intent(mContext,
//                            PhotoActivity.class);
//                    intent.putExtra("ID", arg2);
//                    startActivity(intent);
                }
            }
        });
        noScrollgridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                 if(isTaskHistory){
                     return true;
                 }
                //弹出确认删除图片对话框，点击确认后删除图片
                if(position!=dataList.size()){
                new AlertDialog.Builder(mContext).setTitle(R.string.makeSureDeletePicture)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePictureFromServer((String) dataList.get(position).get("TaskAttachment_ID"),dataList.get(position));
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                             dialog.dismiss();
                    }
                }).show();
                }
                return true;
            }
        });
        popMenuTaskDetail = new PopMenuTaskDetail(this, 310, TaskDetail,TaskClass) {

            @Override
            public void onEventDismiss() {

            }
        };
        nfcDialog=new NFCDialog(mContext,R.style.MyDialog) {
            @Override
            public void dismissAction() {
                nfcDialogTag=false;
            }

            @Override
            public void showAction() {
                nfcDialogTag=true;
            }
        };
        if (mAdapter!=null&&mAdapter.isEnabled()) {
            popMenuTaskDetail.setHasNFC(true);
        }
        popMenuTaskDetail.setNfcDialog(nfcDialog);
        popMenuTaskDetail.setIs_Main_person_in_charge_Operator_id(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),Main_person_in_charge_Operator_id));
        String[] mTitles = getResources().getStringArray(R.array.menu_list);
        if(TaskClass!=null) {
            switch (TaskClass) {
                case Task.MAINTAIN_TASK:{
                    mTitles=getResources().getStringArray(R.array.menu_list_maintain);
                    break;
                }
                case Task.MOVE_CAR_TASK:{
                    mTitles=getResources().getStringArray(R.array.menu_list_move_car);
                    break;
                }
                case Task.TRANSFER_MODEL_TASK:{
                    mTitles=getResources().getStringArray(R.array.menu_list_move_car);
                    break;
                }
                case Task.GROUP_ARRANGEMENT:{
                    mTitles=getResources().getStringArray(R.array.menu_list_group_arrange);
                    break;
                }
                default:
                    mTitles= getResources().getStringArray(R.array.menu_list);
                    break;
            }
        }
        popMenuTaskDetail.addItems(mTitles);
        popMenuTaskDetail.setHasEquipment(HasTaskEquipment);
        popMenuTaskDetail.setOnTaskDetailRefreshListener(new PopMenuTaskDetail.OnTaskDetailRefreshListener() {
            @Override
            public void onRefresh() {
                if(HasTaskEquipment) {
                    getTaskEquipmentFromServerByTaskId();
                }else {
                    getTaskOperatorStatus();
                }
            }
        });

        if(TaskClass!=null&&!TaskClass.equals(Task.REPAIR_TASK)){
            findViewById(R.id.serchDeviceHistory).setVisibility(View.GONE);
        }
        findViewById(R.id.serchDeviceHistory).setOnClickListener(this);
        //维修任务并且是已接单任务的情况下加载并显示故障总结
        initViewWhenTaskClassIsRepairAndTaskStatusIsComplete();

    }

    protected void onRestart() {
//        adapter.update1();
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        int id_click = v.getId();
        if (id_click == R.id.btn_right_action) {
            finish();
        } else if (id_click == R.id.btn_bar_left_action) {

            popMenuTaskDetail.showAsDropDown(v);
        }else if (id_click==R.id.serchDeviceHistory){
            searchDeviceHistory();
        }
    }


    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private boolean shape;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return dataList.size() + 1;
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

//        public void setSelectedPosition(int position) {
//            selectedPosition = position;
//        }
//
//        public int getSelectedPosition() {
//            return selectedPosition;
//        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);

            if (position == dataList.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.mipmap.icon_addpic_unfocused));

                // String addImageUrl =  "mipmap://" + R.mipmap.icon_addpic_unfocused;
                String imgUrl = "drawable://" + R.drawable.icon_addpic_unfocused;
                //addImageUrlToDataList(imgUrl);
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            } else {
                String imgUrl = (String) dataList.get(position).get("imageUrl");
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

    }

    private void addImageUrlToDataList(String path,String ID) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("imageUrl", path);
        dataMap.put("TaskAttachment_ID",ID);
        dataList.add(dataList.size(), dataMap);
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
//            Button bt2 = (Button) view
//                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });

            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {

        File dir = new File(mContext.getExternalFilesDir(null) + "/btp/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } catch (Exception e) {
            CrashReport.postCatchedException(e);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:{
                if (resultCode == -1) {
                    //Bimp.drr.add(path);

                    //将图片地址增加到图片列表
                    Bimp.drr.add(path);
                    String path = Bimp.drr.get(Bimp.max);
                    System.out.println(path);
                    try {
                        Bitmap bm = Bimp.revitionImageSize(path);
                        Bimp.bmp.add(bm);
                        String fileName = path.substring(
                                path.lastIndexOf("/") + 1,
                                path.lastIndexOf("."));
                        FileUtils.saveBitmap(mContext, bm, "" + fileName);
                        Bimp.max += 1;

                        //压缩目录的路径--在saveBitmap方法中写死了的
                        String SDPATH = mContext.getExternalFilesDir(null)
                                + "/btp/formats/";

                        addImageUrlToDataList("file://" + SDPATH + fileName + ".JPEG","0");
                        if (null != adapter) {
//                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (IOException e) {
                        CrashReport.postCatchedException(e);
                    }


                    //在此上传图片到服务器;
                    submitPictureToServer(path);
                }
                break;}
            case Constants.REQUEST_CODE_EXCHANGE_ORDER:{
                if(resultCode==1){
                setResult(2);
                finish();
                }
                break;
            }
            case Constants.REQUEST_CODE_TASK_DETAIL_TO_CAPTURE_ACTIVITY:{
                if(resultCode==Constants.RESULT_CODE_CAPTURE_ACTIVITY_TO_TASK_DETAIL){
                    if (data != null)
                    {
                        String result = data.getStringExtra("result");
                        if (result != null){
                            //ToastUtil.showToastLong(result,mContext);
                            addTaskEquipment(result,true);
                        }
                    }
                }
                break;
            }
        }
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap b
     * @return b
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            CrashReport.postCatchedException(e);
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                CrashReport.postCatchedException(e);
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data b
     * @return b
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void submitPictureToServer(String path) {
        try {
            Bitmap bitmap = Bimp.revitionImageSize(path);
            String base64 = bitmapToBase64(bitmap);
            HttpParams params = new HttpParams();
            JsonObjectElement jsonObjectElement = new JsonObjectElement();
            jsonObjectElement.set(Task.TASK_ID, taskId);
            jsonObjectElement.set("TaskAttachment_ID", 0);
            jsonObjectElement.set("ImgBase64", base64);
            jsonObjectElement.set("AttachmentType", "jpg");
            params.putJsonParams(jsonObjectElement.toJson());
            HttpUtils.post(this, "TaskAttachment", params, new HttpCallback() {
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                     ToastUtil.showToastShort(R.string.FailSubmitPictureCauseByTimeOut,mContext);
                }
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    JsonObjectElement json = new JsonObjectElement(t);
                    if(json.get("Success").valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.SuccessSubmitPicture,mContext);
                        getTaskAttachmentDataFromServerByTaskId();
                    }else{
                        ToastUtil.showToastShort(R.string.FailSubmitPicture,mContext);
                    }
                }
            });
            //上传String
        } catch (IOException e) {
            Log.e("IOException",e.toString());
        }
    }

    private HashMap<String,ObjectElement> TaskEquipment=new HashMap<>();
    private void getTaskEquipmentFromServerByTaskId() {

        if (null == taskId) {
            return;
        }
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        params.put("task_id", taskId.toString());
        params.put("pageSize",1000);
        params.put("pageIndex",1);
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        HttpUtils.get(mContext, "TaskAPI/GetTaskDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (t != null) {
                    ArrayElement jsonArrayElement = new JsonArrayElement(t);
                    //resetData
                    datas.clear();
                    int dealDeviceCount = 0;
                    boolean taskComplete=true;
                    TaskDeviceIdList.clear();
                    TaskDeviceID_Name.clear();
                    Task_DeviceId_TaskEquipmentId.clear();
                    Euqipment_ID_STATUS_map.clear();
                    TaskEquipment_OperatorID_Status.clear();
                    popMenuTaskDetail.setTaskComplete(false);
                    popMenuTaskDetail.setEquipmentNum(jsonArrayElement.size());
                    /////
                        if (jsonArrayElement.size() > 0) {

                            for (int i = 0; i < jsonArrayElement.size(); i++) {
                                if(!DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status")).equals(STATUS_DONE)){
                                    taskComplete=false;
                                }
                                datas.add(jsonArrayElement.get(i).asObjectElement());
                                //TaskEquipment——Map,key为EquipmentID,值为对应Equipment详细信息
                                TaskEquipment.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskEquipment_ID")),jsonArrayElement.get(i).asObjectElement());
                               //EquipmentID列表
                                TaskDeviceIdList.add(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)));
                                //TaskDeviceID_Name——Map,key为Equipment,值为EquipmentName
                                TaskDeviceID_Name.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                        DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_NAME)));
                                //Task_DeviceId_TaskEquipmentId——Map,key为EuqipmentID,值为在任务的中的TaskEquipmentID
                                Task_DeviceId_TaskEquipmentId.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                        DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskEquipment_ID")));
                                //Euqipment_ID_STATUS_map——Map,key为EquipmentID,值为对应的状态值
                                String equipmentStatus = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status"));
                                Euqipment_ID_STATUS_map.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                        equipmentStatus);
                              if(jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList")!=null&&
                                      jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().size()>0) {
                                 HashMap<String,Integer> Equipment_OperatorID_Status=new HashMap<>();
                                  for (int j = 0; j < jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().size(); j++) {
                                      ObjectElement json = jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().get(j).asObjectElement();
                                      Equipment_OperatorID_Status.put(DataUtil.isDataElementNull(json.get("Operator_ID")), Integer.valueOf(DataUtil.isDataElementNull(json.get("Status"))));
                                  }
                                  //TaskEquipment_OperatorID_Status——Map,key为EquipemntID,值为Map——key为设备参与人OperatorID,值为状态值
                                  TaskEquipment_OperatorID_Status.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                          Equipment_OperatorID_Status);
                              }
                                if (STATUS_DONE.equals(equipmentStatus)) {
                                    dealDeviceCount++;
                                }
                            }
                        }else{
                              if(popMenuTaskDetail!=null){
                                 popMenuTaskDetail.setTaskComplete(true);
                                 }
                        }
                    if(popMenuTaskDetail!=null){
                        popMenuTaskDetail.setTaskComplete(taskComplete);
                    }
                    if (null != taskAdapter) {
//                            adapter.setData(dataList);
                        taskAdapter.notifyDataSetChanged();
                    }

                    deviceCountMap.put("deviceCount", String.valueOf(jsonArrayElement.size()));
                    deviceCountMap.put("dealCount", String.valueOf(dealDeviceCount));
                    //在这里刷新设备汇总数据
                    Message message = new Message();
                    message.what = MSG_UPDATE_DEVICE_SUM_INFO;
                    mHandler.sendMessage(message);
                    getEquipmentListFail=false;
                    }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      dismissCustomDialog();
                      getEquipmentListFail=true;
                      ToastUtil.showToastShort(R.string.FailGetEquipmentList,mContext);
                  }
              });
            }
        });
    }

    private void getTaskAttachmentDataFromServerByTaskId() {
        if (null == taskId) {
            return;
        }
        HttpParams params = new HttpParams();
        params.put("task_id",taskId.toString());
        HttpUtils.get(mContext, "TaskAPI/GetTaskImgsList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    ArrayElement jsonArrayElement = jsonObjectElement.get("PageData").asArrayElement();
                    if (jsonArrayElement != null && jsonArrayElement.size() > 0) {
                        dataList.clear();
                        for (int i = 0; i < jsonArrayElement.size(); i++) {
                            String path = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("FileName"));
                            addImageUrlToDataList(path,DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskAttachment_ID")));
                        }
                        //在这里刷新图片列表
                        if (null != adapter) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }


    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
        if(nfcDialogTag){//搬车任务情况下，点击右上角菜单任务完成，显示扫卡对话框，并扫卡的情况下调用
            showCustomDialog(R.string.submitData);
            HttpParams params=new HttpParams();
            JsonObjectElement submitData=new JsonObjectElement();
            submitData.set("ICCardID",iccardID);
            submitData.set(Task.TASK_ID,String.valueOf(taskId));
            params.putJsonParams(submitData.toJson());
            HttpUtils.post(mContext, "TaskOperatorAPI/CheckUserRoleForICCardID", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    dismissCustomDialog();
                    if(t!=null){
                        JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                        if(jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()){
                            ToastUtil.showToastShort(R.string.SuccessToCheckID,mContext);
                            TaskComplete(jsonObjectElement.get(Data.PAGE_DATA));
                        }else {
                            ToastUtil.showToastShort(R.string.FailToCheckID,mContext);
                        }
                    }
                    if(nfcDialog!=null&&nfcDialog.isShowing()){
                        nfcDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    ToastUtil.showToastShort(R.string.FailToCheckIDCauseByTimeOut,mContext);
                    dismissCustomDialog();
                }
            });
            return;
        }
            if(isTaskHistory){
                return;
            }
            if(TaskStatus!=1){
                ToastUtil.showToastShort(R.string.OnlyDealingTaskCanAddEquipment,this);
                return;
            }
            if(!HasTaskEquipment){
                ToastUtil.showToastShort(R.string.error_add_equipment,mContext);
                return;
            }
            addTaskEquipment(iccardID,false);
        }
    }

    private ChangeEquipmentDialog changeEquipmentDialog=null;
    private AlertDialog AddEquipmentDialog=null;
    private void addTaskEquipment(String iccardID,boolean isQRCode) {
        if(getEquipmentListFail){
            ToastUtil.showToastShort(R.string.ReGetEquipmentList,this);
            getTaskEquipmentFromServerByTaskId();
            return;
        }
        String rawQuery;
        if(isQRCode){
            rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.AssetsID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
        }else {
            rawQuery = "SELECT * FROM Equipment e,BaseOrganise b WHERE  e.ICCardID ='" + iccardID + "' and e.[Organise_ID_Use]=b.[Organise_ID]";
        }
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(DataElement dataElement) {

                if (dataElement != null && dataElement.isArray()
                        && dataElement.asArrayElement().size() > 0) {
                    final ObjectElement objectElement = dataElement.asArrayElement().get(0).asObjectElement();
                    //进行判断，若任务未有该设备号，添加
                    if(!TaskDeviceIdList.contains(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))){
                        if(TaskSubClass!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToastShort(R.string.can_not_add_equipment,mContext);
                                }
                            });
                            return;
                        }
                        final String DialogMessage=getString(R.string.AreYouSureToAddEquipment)
                                +"\n"+getString(R.string.equipment_name)+DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_NAME))
                                +"\n"+getString(R.string.equipment_num)+DataUtil.isDataElementNull(objectElement.get(Equipment.ASSETSID))
                                +"\n"+getString(R.string.belongGroup)+DataUtil.isDataElementNull(objectElement.get(BaseOrganise.ORGANISENAME));
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           if(AddEquipmentDialog==null||!AddEquipmentDialog.isShowing()) {
                               AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                               builder.setMessage(DialogMessage);
                               builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               postTaskEquipment(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)));
                                           }
                                       });
                                   }
                               }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                   }
                               });
                               AddEquipmentDialog=builder.create();
                               AddEquipmentDialog.show();
                           }
                       }
                   });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).equals(STATUS_DONE)){
                                    ToastUtil.showToastShort(R.string.CanNotChangeEquipmentStatus,mContext);
                                    return;
                                }

                                //如果操作员未加入该设备，添加为处理中
                                   if(TaskEquipment_OperatorID_Status.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))!=null){
                                       if(!TaskEquipment_OperatorID_Status.get(
                                            DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).containsKey(String.valueOf(getLoginInfo().getId()))){
                                        postTaskOperatorEquipment(0,DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                                Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                      return;
                                       }
                                    }else {
                                       postTaskOperatorEquipment(0,DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                               Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                   return;
                                   }
                                ////
                                boolean isOneOperator=false;
                                if(TaskEquipment_OperatorID_Status.get(
                                        DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).size()==1){
                                    isOneOperator=true;
                                }
                                int OperatorStatus=TaskEquipment_OperatorID_Status.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).get(String.valueOf(getLoginInfo().getId()));
//                                if(OperatorStatus==1
//                                        &&!RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),
//                                        Main_person_in_charge_Operator_id)){
//                                      runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                            ToastUtil.showToastLong(R.string.CanNotChangeOperatorStatus,mContext);
//                                            }
//                                        });
//                                        return;
//                                 }
                              //  if(changeEquipmentDialog==null) {
                                if(changeEquipmentDialog==null||!changeEquipmentDialog.isShowing()){
                                    changeEquipmentDialog = new ChangeEquipmentDialog(TaskDetailsActivity.this, R.layout.dialog_equipment_status, R.style.MyDialog,
                                            RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),
                                            Main_person_in_charge_Operator_id),
                                            isOneOperator,
                                            false,
                                            TaskSubClass != null,
                                            DataUtil.isDataElementNull(objectElement.get(Equipment.ASSETSID)),
                                            OperatorStatus,
                                            Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))))
                                    );
                                    changeEquipmentDialog.setDatas(String.valueOf(taskId), DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)),
                                            Task_DeviceId_TaskEquipmentId.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                    // changeEquipmentDialog.setMainPersonInChargeOperatorId(Main_person_in_charge_Operator_id.equals(String.valueOf(getLoginInfo().getId())));
                                    // changeEquipmentDialog.setMainPersonInChargeOperatorId(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),Main_person_in_charge_Operator_id));
                                    changeEquipmentDialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                        @Override
                                        public void onsubmit() {
                                            getTaskEquipmentFromServerByTaskId();
                                        }
                                    });
                                    changeEquipmentDialog.setEquipmentCompleteListener(new EquipmentCompleteListener() {
                                        @Override
                                        public void EquipmentComplete(boolean isComplete) {
                                            //若已从某个任务创建的该任务，则不再触发dialog,如调车任务创建的搬车任务
                                            JsonObjectElement TaskData=new JsonObjectElement(TaskDetail);
                                            if(!"0".equals(DataUtil.isDataElementNull(TaskData.get("FromTask_ID")))){
                                                return;
                                            }
                                            //TODO
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                                                    builder.setCancelable(false);
                                                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    if(TaskClass!=null){
                                                        switch (TaskClass){
                                                            case Task.MOVE_CAR_TASK:{
                                                                builder.setMessage(R.string.DoYouNeedToCreateAShuntingTask);
                                                                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        try {
                                                                            Intent intent = new Intent(mContext, CreateTaskActivity.class);
                                                                            intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK, Constants.FLAG_CREATE_SHUNTING_TASK);
                                                                            JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                                                            JsonObjectElement detail = new JsonObjectElement(TaskDetail);
                                                                            jsonObjectElement.set("Name", DataUtil.isDataElementNull(detail.get("Applicant")));
                                                                            jsonObjectElement.set("Operator_ID", DataUtil.isDataElementNull(detail.get("ApplicantID")));
                                                                            jsonObjectElement.set("Organise_ID", DataUtil.isDataElementNull(detail.get("TargetTeam_ID")));
                                                                            jsonObjectElement.set("Organise_Name",DataUtil.isDataElementNull(detail.get("TargetTeam")));
                                                                            intent.putExtra("OperatorInfo", jsonObjectElement.toString());
                                                                            intent.putExtra("EquipmentInfo", objectElement.toJson());
                                                                            intent.putExtra("FromTask_ID",
                                                                                    String.valueOf(taskId));
                                                                            startActivity(intent);
                                                                        }catch (Exception e){
                                                                            CrashReport.postCatchedException(e);
                                                                        }
                                                                    }
                                                                });
                                                                builder.show();
                                                                break;
                                                            }
                                                            case Task.TRANSFER_MODEL_TASK:{
                                                                builder.setMessage(R.string.DoYouNeedToCreateACarMovingTask);
                                                                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        try {
                                                                            Intent intent = new Intent(mContext, CreateTaskActivity.class);
                                                                            intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK, Constants.FLAG_CREATE_CAR_MOVING_TASK);
                                                                            JsonObjectElement jsonObjectElement = new JsonObjectElement();
                                                                            JsonObjectElement detail = new JsonObjectElement(TaskDetail);
                                                                            jsonObjectElement.set("Name", DataUtil.isDataElementNull(detail.get("Applicant")));
                                                                            jsonObjectElement.set("Operator_ID", DataUtil.isDataElementNull(detail.get("ApplicantID")));
                                                                            jsonObjectElement.set("Organise_ID", DataUtil.isDataElementNull(detail.get("TaskApplicantOrg_ID")));
                                                                            jsonObjectElement.set("Organise_Name",DataUtil.isDataElementNull(detail.get("TaskApplicantOrg")));
                                                                            intent.putExtra("OperatorInfo", jsonObjectElement.toString());
                                                                            intent.putExtra("EquipmentInfo", objectElement.toJson());
                                                                            intent.putExtra("FromTask_ID",
                                                                                    String.valueOf(taskId));
                                                                            startActivity(intent);
                                                                        }catch (Exception e){
                                                                            CrashReport.postCatchedException(e);
                                                                        }
                                                                    }
                                                                });
                                                                builder.show();
                                                                break;
                                                            }
                                                            default:{
                                                                break;
                                                            }
                                                        }

                                                    }
                                                }
                                            });

                                        }
                                    });
                                    int EquipmentStatus;
                                    EquipmentStatus = Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                    changeEquipmentDialog.setEquipemntStatus(EquipmentStatus);
                                    changeEquipmentDialog.show();
                                }
                            }
                        });
                    }
                } else {
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


    }

    private void postTaskEquipment(String equipmentID) {

        HttpParams params = new HttpParams();
        JsonObjectElement taskEquepment = new JsonObjectElement();
//创建任务提交数据：任务创建人，任务类型“T01”那些，几台号（数组），
        taskEquepment.set(Task.TASK_ID, taskId);
        //若任务未有设备，则输入为0，表示添加
        taskEquepment.set("TaskEquipment_ID", 0);
        //若已有设备，申请状态变更
        taskEquepment.set("Equipment_ID", equipmentID);
        taskEquepment.set("Status",0);
        params.putJsonParams(taskEquepment.toJson());

        HttpUtils.post(this, "TaskEquipmentAPI/AddTaskEquipment", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if(t!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                                if (jsonObjectElement.get(Data.SUCCESS) != null && jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()) {
                                    ToastUtil.showToastShort(R.string.AddEquipmentSuccess, mContext);
                                    getTaskEquipmentFromServerByTaskId();
                                } else {
                                    if (DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                                        ToastUtil.showToastShort(R.string.err_add_task_equipment, mContext);
                                    } else {
                                        TipsUtil.ShowTips(mContext, DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                    }
                                }
                            }catch (Exception e){
                                CrashReport.postCatchedException(e);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
               getTaskEquipmentFromServerByTaskId();
               ToastUtil.showToastShort(R.string.err_add_task_equipment,mContext);
                    }
                });
            }
        });
    }

    // 点击放大图片
    public void imageClick(View v) {
        Bitmap bmp;
        if (v instanceof LinearLayout) {
            LinearLayout tmpV = (LinearLayout) v;
            bmp = ((BitmapDrawable) tmpV.getBackground()).getBitmap();
        } else {
            ImageView image = (ImageView) v;
            bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
        }

        ShowBigImageActivity.saveTmpBitmap(bmp);
        Intent showBigImageIntent = new Intent(TaskDetailsActivity.this,
                ShowBigImageActivity.class);

        startActivity(showBigImageIntent);
    }

    //主线程中的handler
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {
                case MSG_UPDATE_DEVICE_SUM_INFO: {

                    //设备数数
                    mHolder.deviceCountTextView.setText(String.valueOf(deviceCountMap.get("deviceCount")));
                    //已处理数量
                    mHolder.dealCountTextView.setText(String.valueOf(deviceCountMap.get("dealCount")));

                    break;
                }

            }
        }

    };
    private void deletePictureFromServer(String picture,final Map<String,Object> data){
        HttpParams params=new HttpParams();
        HttpUtils.post(this, "TaskAttachment/TaskAttachmentDelete?TaskAttachment_ID="+picture
                , params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get(Data.SUCCESS).valueAsBoolean()){
                        dataList.remove(data);
                        adapter.notifyDataSetChanged();
                        ToastUtil.showToastShort(R.string.deletePictureSuccess,mContext);
                    }else{
                        ToastUtil.showToastShort(R.string.deletePictureFail,mContext);
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                ToastUtil.showToastShort(R.string.deletePicture_fail,mContext);
                super.onFailure(errorNo, strMsg);
            }
        });

    }
    private void searchDeviceHistory(){
        if(TaskDeviceIdList!=null){
        if(TaskDeviceIdList.size()==0){
            ToastUtil.showToastShort(R.string.pleaseAddEquipment,mContext);
        }else if(TaskDeviceIdList.size()==1){
            Intent intent=new Intent(this,EquipmentHistory.class);
            intent.putExtra(Equipment.EQUIPMENT_ID,TaskDeviceIdList.get(0));
            intent.putExtra(Equipment.EQUIPMENT_NAME,TaskDeviceID_Name.get(TaskDeviceIdList.get(0)));
            startActivity(intent);
        }else{
            ArrayList<ObjectElement> s=new ArrayList<>();
            for(int i=0;i< TaskDeviceIdList.size();i++){
                if(TaskDeviceID_Name.get(TaskDeviceIdList.get(i))!=null){
                JsonObjectElement jsonObjectElement=new JsonObjectElement();
                jsonObjectElement.set(Equipment.EQUIPMENT_NAME,TaskDeviceID_Name.get(TaskDeviceIdList.get(i)));
                s.add(jsonObjectElement);}
            }
            final EquipmentSummaryDialog equipmentSummaryDialog=new EquipmentSummaryDialog(this,s);
            equipmentSummaryDialog.show();
            equipmentSummaryDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent(mContext,EquipmentHistory.class);
                    // intent.putExtra(Equipment.EQUIPMENT_ID,TaskDeviceIdList.get(position));
                     intent.putExtra(Equipment.EQUIPMENT_NAME,DataUtil.isDataElementNull(equipmentSummaryDialog.getList().get(position).get(Equipment.EQUIPMENT_NAME)));
                     startActivity(intent);
                    equipmentSummaryDialog.dismiss();
                }
            });
        }
        }
    }


    private void postTaskOperatorEquipment(int status,String EquipmentId,final String TaskEquipmentId){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        // JsonObjectElement TaskOperatorDataToSubmit=new JsonObjectElement();
        //   TaskOperatorDataToSubmit.set("task_id",Integer.valueOf(TaskId));
        //   TaskOperatorDataToSubmit.set("equipment_id",Integer.valueOf(EquipmentId));
        //   TaskOperatorDataToSubmit.set("TaskEquipment_ID",Integer.valueOf(TaskEquipmentId));
        //   TaskOperatorDataToSubmit.set("status",status);
        //   params.putJsonParams(TaskOperatorDataToSubmit.toJson());
        HttpUtils.post(this, "TaskOperatorAPI/MotifyTaskOperatorStatus?task_id="+taskId+"&equipment_id="+EquipmentId+"&status="+status,
                params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        if(t!=null){
                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("Success").valueAsBoolean()){
                                getTaskEquipmentFromServerByTaskId();
                                ToastUtil.showToastShort(R.string.SuccessToChangeStatus,mContext);
                                if(TaskSubClass!=null&&TaskClass!=null&&TaskStatus==1&&TaskClass.equals(Task.MAINTAIN_TASK)) {
                                    Intent intent = new Intent(mContext, MeasurePointActivity.class);
                                    intent.putExtra(Task.TASK_ID, taskId.toString());
                                    intent.putExtra("TaskEquipment", TaskEquipment.get(TaskEquipmentId).toString());
                                    intent.putExtra("isMainPersonInTask", RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()), Main_person_in_charge_Operator_id));
                                    intent.putExtra("EquipmentStatus", false);
                                    if (TaskSubClass != null && !TaskSubClass.equals("")) {
                                        intent.putExtra(Task.TASK_SUBCLASS, TaskSubClass);
                                    }
                                    startActivity(intent);
                                }
                            }else {
                                if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).equals("")){
                                    ToastUtil.showToastShort(R.string.CanNotChangeStatus,mContext);
                                }else {
                                    TipsUtil.ShowTips(mContext,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                }
                                getTaskEquipmentFromServerByTaskId();
                            }
                        }
                        dismissCustomDialog();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        ToastUtil.showToastShort(R.string.failToChangeStatus,mContext);
                        dismissCustomDialog();
                    }
                });
    }
    private void getSummaryFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        httpParams.put("task_id", String.valueOf(taskId));
        HttpUtils.get(this, "TaskTroubleAPI/GetTaskTroubleList", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("PageData")!=null
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                        final ObjectElement faultData=jsonObjectElement.get("PageData").asArrayElement().get(0).asObjectElement();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fault_type.setText(DataUtil.isDataElementNull(faultData.get("TroubleType")));
                                fault_description.setText(DataUtil.isDataElementNull(faultData.get("TroubleDescribe")));
                                repair_status.setText(DataUtil.isDataElementNull(faultData.get("MaintainDescribe")));
                            }
                        });
                    }
                    dismissCustomDialog();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }
    private void initViewWhenTaskClassIsRepairAndTaskStatusIsComplete(){
        fault_type=(TextView)findViewById(R.id.fault_type);
        fault_description=(TextView)findViewById(R.id.fault_description);
        repair_status=(TextView)findViewById(R.id.repair_status);
        //任务是已完成情况下调用，显示任务总结，工作量分配，任务评价模块
        if(TaskStatus>=2){
            findViewById(R.id.task_complete).setVisibility(View.VISIBLE);
            if(TaskClass.equals(Task.REPAIR_TASK)){
            findViewById(R.id.fault_summary).setVisibility(View.VISIBLE);
            getSummaryFromServer();
            }else if(TaskClass.equals(Task.OTHER_TASK)
                    ||TaskClass.equals(Task.TRANSFER_MODEL_TASK)
                    ||TaskClass.equals(Task.GROUP_ARRANGEMENT)){
                findViewById(R.id.fault_summary).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.fault_title)).setText(R.string.task_summary);
                ((TextView)findViewById(R.id.fault_description_tag)).setText(R.string.task_summary_tag);
                findViewById(R.id.fault_type_tag).setVisibility(View.GONE);
                findViewById(R.id.fault_type).setVisibility(View.GONE);
                findViewById(R.id.repair_status_tag).setVisibility(View.GONE);
                findViewById(R.id.repair_status).setVisibility(View.GONE);
                getSummaryFromServer();
            }
            //待权限
            //TODO
            //如果角色为非报修人并且为非搬车或者非调车情况下显示工作量分配
            switch (DataUtil.isDataElementNull(BaseData.getConfigData().get(BaseData.TASK_DETAIL_SHOW_WORKLOAD_ACTION))){
                case "1":{
                    findViewById(R.id.workload).setVisibility(View.GONE);
                    break;
                }
                default:{
                    if( Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=7 ) {//若为EGM，则无工作量模块
                        if ( TaskClass.equals(Task.MOVE_CAR_TASK)
                                ||TaskClass.equals(Task.TRANSFER_MODEL_TASK)){
                            findViewById(R.id.workload).setVisibility(View.GONE);
                        }else {
                            initWorkload();
                        }
                    }else {
                        findViewById(R.id.workload).setVisibility(View.GONE);
                    }
                    break;
                }
            }

//            if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))!=7
//                    && !Factory.FACTORY_EGM.equals(getLoginInfo().getFromFactory())) {//若为EGM，则无工作量模块
//                if ( TaskClass.equals(Task.MOVE_CAR_TASK)
//                        ||TaskClass.equals(Task.TRANSFER_MODEL_TASK)){
//                    findViewById(R.id.workload).setVisibility(View.GONE);
//                }else {
//                    initWorkload();
//                }
//            }else {
//                findViewById(R.id.workload).setVisibility(View.GONE);
//            }
            //非维护任务显示任务评价
            if(TaskClass!=null&&!TaskClass.equals(Task.MAINTAIN_TASK)) {
                findViewById(R.id.Command_layout).setVisibility(View.VISIBLE);
                initTaskCommand();
            }
    }
    }
    private TaskAdapter WorkloadAdapter;
    private ArrayList<ObjectElement> workloadData=new ArrayList<>();
    private MyListView workloadList;
    private void initWorkload(){
        //workload_num
        //workloadList
        workloadList=(MyListView) findViewById(R.id.workloadList);
        WorkloadAdapter=new TaskAdapter(workloadData) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_taskdetail_workload, parent, false);
                    holder = new TaskViewHolder();
                    //显示6个内容，组别，报修人，状态，保修时间,开始时间，任务描述
                    holder.tv_group = (TextView) convertView.findViewById(R.id.name);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.workload_value);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //待修改
                holder.tv_group.setText(DataUtil.isDataElementNull(workloadData.get(position).get("OperatorName")));
                String s=String.valueOf((int)(Float.valueOf(DataUtil.isDataElementNull(workloadData.get(position).get("Coefficient"))) * 100))+"%";
                holder.warranty_person.setText(s);
                return convertView;
            }
        };
        workloadList.setAdapter(WorkloadAdapter);
        getWorkLoadFromServer();
    }
    private void getWorkLoadFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        httpParams.put("task_id", String.valueOf(taskId));
        HttpUtils.get(this, "TaskWorkload", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if(t!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            SetViewData(jsonObjectElement);
                        }
                    });
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }
    private void SetViewData(ObjectElement ViewData){
        String s=DataUtil.isDataElementNull(ViewData.get("Workload"))+getResources().getString(R.string.hours);
        ((TextView)findViewById(R.id.workload_num)).setText(s);
        if(ViewData.get("TaskOperator")!=null&&ViewData.get("TaskOperator").asArrayElement().size()>0) {
            for (int i = 0; i < ViewData.get("TaskOperator").asArrayElement().size(); i++) {
                workloadData.add(ViewData.get("TaskOperator").asArrayElement().get(i).asObjectElement());
            }
            WorkloadAdapter.notifyDataSetChanged();
            ListViewUtility.setListViewHeightBasedOnChildren(workloadList);
        }
    }
    private ArrayList<Integer> response_speed_list=new ArrayList<>();
    private ArrayList<Integer> service_attitude_list=new ArrayList<>();
    private ArrayList<Integer> repair_speed_list=new ArrayList<>();
    private commandAdapter response_speed_adapter,service_attitude_adapter,repair_speed_adapter;
    private HorizontalListView response_speed,service_attitude,repair_speed;
    private HashMap<String,Integer> command=new HashMap<>();
    private int TaskEvaluation_ID=0;
    private void initTaskCommand(){
        //response_speed
        //service_attitude
        //repair_speed
         response_speed=(HorizontalListView)findViewById(R.id.response_speed);
         service_attitude=(HorizontalListView)findViewById(R.id.service_attitude);
         repair_speed=(HorizontalListView)findViewById(R.id.repair_speed);
        for(int i=0;i<5;i++){
            response_speed_list.add(0);
            service_attitude_list.add(0);
            repair_speed_list.add(0);
        }
        command.put("response_speed",0);
        command.put("service_attitude",0);
        command.put("repair_speed",0);
         response_speed_adapter=new commandAdapter(this,response_speed_list);
        response_speed.setAdapter(response_speed_adapter);

         service_attitude_adapter=new commandAdapter(this,service_attitude_list);
        service_attitude.setAdapter(service_attitude_adapter);

         repair_speed_adapter=new commandAdapter(this,repair_speed_list);
        repair_speed.setAdapter(repair_speed_adapter);
        //TODO
        //条件1：可评价任务；条件2未评价任务，条件3报修人或者维修工班组长，条件4属于报修人班组
        if( getIntent().getStringExtra("IsEvaluated")!=null
                &&getIntent().getStringExtra("IsEvaluated").equals("0")
                &&TaskStatus>=2
                && (Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))==RootUtil.ROOT_WARRANTY
              ||Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))<5    )
                //&&OrganiseList.contains(getLoginInfo().getOrganiseID())
                ){
            for(int i=0;i<getLoginInfo().getOrganiseID().split(",").length;i++){
                if(OrganiseList.contains(getLoginInfo().getOrganiseID().split(",")[i])){
//                    if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(mContext))==RootUtil.ROOT_WARRANTY){
//                        if(TaskDetail!=null&&!TaskDetail.equals("")) {
//                            JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
//                            if(DataUtil.isDataElementNull(jsonObjectElement.get(Task.APPLICANT)).equals(getLoginInfo().getName())){
//                                findViewById(R.id.submitCommand).setVisibility(View.VISIBLE);
//                                initListViewOnItemClickEvent();
//                                findViewById(R.id.submitCommand).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        postTaskCommandToServer();
//                                    }
//                                });
//                                break;
//                            }
//                        }
//                    }else {
                        findViewById(R.id.submitCommand).setVisibility(View.VISIBLE);
                        initListViewOnItemClickEvent();
                        findViewById(R.id.submitCommand).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postTaskCommandToServer();
                            }
                        });
                        break;
                    //}
                }
            }
        }
        //initListViewOnItemClickEvent();
        getTaskCommandFromServer();
    }
    private void getTaskCommandFromServer(){
        HttpParams params=new HttpParams();
        params.put("task_id", String.valueOf(taskId));
            HttpUtils.get(this, "TaskEvaluationAPI/GetTaskEvaluationInfo", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast toast=Toast.makeText(mContext,getResources().getString(R.string.getCommandFail),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement CommandData=new JsonObjectElement(t);
                    if(CommandData.get("PageData")!=null&&CommandData.get("PageData").isArray()){
                        if(CommandData.get("PageData").asArrayElement().size()>0){
                        TaskEvaluation_ID=CommandData.get("PageData").asArrayElement().get(0).asObjectElement().get("TaskEvaluation_ID").valueAsInt();
                            ObjectElement objectElement=CommandData.get("PageData").asArrayElement().get(0).asObjectElement();
                            setCommandData(objectElement.get("RespondSpeed").valueAsInt(),"response_speed",response_speed_list,response_speed_adapter);
                            setCommandData(objectElement.get("ServiceAttitude").valueAsInt(),"service_attitude",service_attitude_list,service_attitude_adapter);
                            setCommandData(objectElement.get("MaintainSpeed").valueAsInt(),"repair_speed",repair_speed_list,repair_speed_adapter);
                        }
                    }
                }
            }
        });
    }
    public void setCommandData(int num,String key,final ArrayList<Integer> numList,final commandAdapter cAdapter){
        command.put(key,num);
        for(int i=0;i<5;i++){
            if(i<num){
                numList.set(i,1);
            }else {
                numList.set(i,0);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cAdapter.setDatas(numList);
                cAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initListViewOnItemClickEvent(){
        response_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"response_speed",response_speed_list,response_speed_adapter);
            }
        });
        service_attitude.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"service_attitude",service_attitude_list,service_attitude_adapter);
            }
        });
        repair_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"repair_speed",repair_speed_list,repair_speed_adapter);
            }
        });
    }
    private void postTaskCommandToServer(){
        //TODO
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement submitCommandData=new JsonObjectElement();
        submitCommandData.set(Task.TASK_ID,String.valueOf(taskId));
        submitCommandData.set("RespondSpeed",command.get("response_speed"));
        submitCommandData.set("ServiceAttitude",command.get("service_attitude"));
        submitCommandData.set("MaintainSpeed",command.get("repair_speed"));
        //若已有，则对应，否则为0
        submitCommandData.set("TaskEvaluation_ID",TaskEvaluation_ID);
        ArrayList<ObjectElement> submiData=new ArrayList<>();
        submiData.add(submitCommandData);
        JsonArrayElement jsonArrayElement=new JsonArrayElement(submiData.toString());
        params.putJsonParams(jsonArrayElement.toJson());
//        params.putJsonParams(submitCommandData.toJson());
        HttpUtils.post(this, "TaskEvaluationAPI/TaskEvaluationList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if(t!=null){
                    ToastUtil.showToastShort(R.string.commandSuccess,mContext);
                    // TaskComplete(iccardID);
                    if(FromFragment!=null){
                        switch (FromFragment) {
                            case "0":
                                setResult(3);
                                break;
                            case "1":
                                setResult(1);
                                break;
                            default:
                                setResult(2);
                                break;
                        }
                    }
                    finish();
                }else {
                    ToastUtil.showToastShort(R.string.submitFail,mContext);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.submitFail,mContext);
                dismissCustomDialog();
            }
        });
    }
    private void getTaskOperatorStatus(){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        params.put("task_id",taskId.toString());
        HttpUtils.get(mContext, "TaskOperatorAPI/GetTaskOperatorDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement data=new JsonObjectElement(t);
                    if(data.get(Data.SUCCESS).valueAsBoolean()){
                        //TODO
                        //TaskOperatorList.add()
                        //TaskOperatorListView
                        TaskOperatorList.clear();
                        boolean taskComplete=true;
                        for(int i=0;i<data.get("PageData").asArrayElement().size();i++ ){
                            TaskOperatorList.add(data.get("PageData").asArrayElement().get(i).asObjectElement());
                            if(data.get("PageData").asArrayElement().get(i).asObjectElement().get("Status").valueAsInt()!=1){
                                taskComplete=false;
                            }
                        }
                        popMenuTaskDetail.setTaskComplete(taskComplete);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TaskOperatorAdapter.notifyDataSetChanged();
                               ListViewUtility.setListViewHeightBasedOnChildren(TaskOperatorListView);
                            }
                        });
                        isGetTaskOperatorListSuccess=true;
                    }else {
                        isGetTaskOperatorListSuccess=false;
                        ToastUtil.showToastShort(R.string.FailGetTaskOperatorList,mContext);
                    }
                }else {
                    isGetTaskOperatorListSuccess=false;
                    ToastUtil.showToastShort(R.string.FailGetTaskOperatorList,mContext);
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                isGetTaskOperatorListSuccess=false;
                ToastUtil.showToastShort(R.string.FailGetTaskOperatorListCauseByTimeOut,mContext);
            }
        });
    }
    private void ChangeTaskOperatorStatus(){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        submitData.set("TaskOperator_ID",0);
        submitData.set("Status",0);
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(mContext, "TaskOperatorAPI/MotifyTaskOperatorStatusForSimple", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if(t!=null){
                    JsonObjectElement data=new JsonObjectElement(t);
                    if(!data.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.CanNotChangeStatus,mContext);
                    }
                }
                getTaskOperatorStatus();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.failToChangeStatus,mContext);
                dismissCustomDialog();
            }
        });
    }
    private void TaskComplete(final DataElement dataElement){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set(Task.TASK_ID,String.valueOf(taskId));
        params.putJsonParams(data.toJson());
        HttpUtils.post(this, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success")!=null&&
                            jsonObjectElement.get("Success").valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.taskComplete,mContext);
                        if(jsonObjectElement.get("Tag")==null || "1".equals(DataUtil.isDataElementNull(jsonObjectElement.get("Tag")))) {//Tag为1即需要弹出对话框询问用户是否需要创建新任务
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage(R.string.DoYouNeedToCreateAShuntingTask);
                            builder.setCancelable(false);
                            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(mContext, CusActivity.class);
                                    intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK, Constants.FLAG_CREATE_SHUNTING_TASK);
                                    if (dataElement != null) {
                                        intent.putExtra("OperatorInfo", dataElement.toString());
                                    }
                                    intent.putExtra("FromTask_ID",
                                            String.valueOf(taskId));
                                    mContext.startActivity(intent);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mContext.startActivity(new Intent(mContext, CusActivity.class));
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToastShort(R.string.canNotSubmitTaskComplete,mContext);
                            }
                        });
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.submitFail,mContext);
                    }
                });
            }
        });
    }
}
