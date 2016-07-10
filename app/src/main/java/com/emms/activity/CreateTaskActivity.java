package com.emms.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.renderscript.Element;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.record.NdefMessageParser;
import com.emms.record.ParsedNdefRecord;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.ui.DropEditText;
import com.emms.ui.KProgressHUD;
import com.emms.ui.NFCDialog;
import com.emms.ui.TipsDialog;
import com.emms.util.SharedPreferenceManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.jaffer_datastore_android_sdk.datastore.DataElement;
import com.jaffer_datastore_android_sdk.datastore.ObjectElement;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jaffer.deng on 2016/6/7.
 */
public class CreateTaskActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;

    private DropEditText task_type, task_subtype, group, device_name;
    private EditText create_task, task_description, device_num;
    private TextView task_subtype_name_desc;
    private Button btn_sure;
    private ImageView create_task_action, device_num_action;
    private KProgressHUD hud;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;

    private AlertDialog mDialog;
    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();

    public final static String FORM_TYPE = "formtype";
    public final static String FORM_CONTENT = "content";
    public final static String SELECTINDEX = "indexlist";
    public final static int REQUEST_CODE = 10000;
    public final static int TASK_TYPE = 1;
    public final static int TASK_SUBTYPE = 2;
    public final static int DEVICE_NAME = 3;
    public final static int GROUP = 4;
    public final static int CREATER = 5;
    public final static int DEVICE_NUM = 6;
    public final static int TASK_DESCRIPTION = 7;

    private int nfctag = 0;
    private ArrayList<String> mDeviceTypelist;

    private ArrayList<ObjectElement> mDeviceType = new ArrayList<ObjectElement>();
    private ArrayList<String> mSubType;
    private ArrayList<String> mTeamNamelist;
    private ArrayList<String> mDeviceNamelist;
    private NFCDialog nfcDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        mContext = CreateTaskActivity.this;
        initView();
        initEvent();

        resolveIntent(getIntent());
        mDialog = new AlertDialog.Builder(this).setNeutralButton("Ok", null).create();
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNdefPushMessage = new NdefMessage(new NdefRecord[]{newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true)});
//        HttpUtils.get(this, "", new HttpParams(), new HttpCallback() {
//            @Override
//            public void onSuccess(String t) {
//                super.onSuccess(t);
//            }
//
//            @Override
//            public void onFailure(int errorNo, String strMsg) {
//                super.onFailure(errorNo, strMsg);
//            }
//
//            @Override
//            public void onSuccessInAsync(byte[] t) {
//                super.onSuccessInAsync(t);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showWirelessSettingsDialog();
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
            mAdapter.disableForegroundNdefPush(this);
        }
    }

    private void initEvent() {

        getTaskType();
        getOperator("4204");
        group.getmEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getDeviceName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        device_name.getDropImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTeamNamelist.size() > 0) {
                    group.showOnclik();
                } else {
                    Toast.makeText(mContext, "请先选择组别", Toast.LENGTH_SHORT).show();
                }
            }
        });

        device_name.getmEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTeamNamelist.size() > 0) {
                    Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                    intent.putExtra(FORM_TYPE, DEVICE_NAME);
                    intent.putStringArrayListExtra(FORM_CONTENT, mDeviceNamelist);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(mContext, "请先选择组别", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getDeviceName(String groupName) {
        String rawQuery = "select distinct EquipmentClass,EquipmentName from Equipment where UseTeam_ID in (select Team_ID from Team where TeamName =\"" + groupName + "\")";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                System.out.println(element);
                mDeviceNamelist = new ArrayList<String>();
                if (element != null && element.isArray()
                        && element.asArrayElement().size() > 0) {
                    mDeviceNamelist.clear();
                    for (int i = 0; i < element.asArrayElement().size(); i++) {
                        mDeviceNamelist.add(element.asArrayElement().get(i).asObjectElement().get(Equipment.EQUIPMENT_NAME).valueAsString());
                    }
                } else {
                    Toast.makeText(mContext, "程序数据库出错，请重新登陆", Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = element.asObjectElement().get(Operator.NAME).valueAsString();
                        if (name != null) {
                            create_task.setText(name);
                        }
                        device_name.setDatas(mContext, mDeviceNamelist);

                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

    }

    private void getOperator(String operatorId) {

        String rawQuery = "select Name,Team_ID,TeamName from Operator where Operator_ID=" + operatorId;
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                System.out.println(element);
                mTeamNamelist = new ArrayList<String>();
                if (element != null && element.isArray()
                        && element.asArrayElement().size() > 0) {
                    mTeamNamelist.clear();
                    String teamName = element.asArrayElement().get(0).asObjectElement().get(Operator.TEAM_NAME).valueAsString();
                    String a[] = teamName.split(",");
                    for (int i = 0; i < a.length; i++) {
                        mTeamNamelist.add(a[i]);
                    }
                } else {
                    Toast.makeText(mContext, "程序数据库出错，请重新登陆", Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String name = element.asArrayElement().get(0).asObjectElement().get(Operator.NAME).valueAsString();
                        if (name != null) {
                            create_task.setText(name);
                        }

                        group.setDatas(mContext, mTeamNamelist);
                        group.getmEditText().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                                intent.putExtra(FORM_TYPE, GROUP);
                                intent.putStringArrayListExtra(FORM_CONTENT, mTeamNamelist);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }

    private void getTaskType() {
//        String rawQuery = "select Team_ID,TeamName from Operator where Operator_ID=" +OperaterId;
        String rawQuery = "select * from DataDictionary where DataType = \"TaskClass\" and PData_ID is null";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(DataElement element) {
                System.out.println(element);
                mDeviceTypelist = new ArrayList<String>();
                if (element != null && element.isArray()
                        && element.asArrayElement().size() > 0) {
                    mDeviceTypelist.clear();
                    for (int i = 0; i < element.asArrayElement().size(); i++) {
                        mDeviceType.add(element.asArrayElement().get(i).asObjectElement());
                        mDeviceTypelist.add(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME).valueAsString());

                    }

                } else {
                    Toast.makeText(mContext, "程序数据库出错，请重新登陆", Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        task_type.setDatas(mContext, mDeviceTypelist);
                        task_type.getmEditText().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                                intent.putExtra(FORM_TYPE, TASK_TYPE);
                                intent.putStringArrayListExtra(FORM_CONTENT, mDeviceTypelist);
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        });

                    }
                });
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
        group = (DropEditText) findViewById(R.id.group);
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

        device_num.setText("AD0001528");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == searchActivity.RESULT_CODE) {
                Bundle bundle = data.getExtras();
                String str = bundle.getString(searchActivity.BACK_CONTENT);
                int index = bundle.getInt(FORM_TYPE);
                switch (index) {
                    case TASK_TYPE:
                        int posi = bundle.getInt(SELECTINDEX);
                        task_type.getmEditText().setText(str);
                        try {
                            String pdataid = mDeviceType.get(posi).get(DataDictionary.DATA_ID).valueAsString();
                            String rawQuery = "select * from DataDictionary where DataType = \"TaskClass\" and PData_ID=" + pdataid;
                            ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                                    EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
                            Futures.addCallback(elemt, new FutureCallback<DataElement>() {

                                @Override
                                public void onSuccess(DataElement element) {
                                    System.out.println(element);
                                    mSubType = new ArrayList<String>();
                                    if (element != null && element.isArray()
                                            && element.asArrayElement().size() > 0) {
                                        mSubType.clear();
                                        for (int i = 0; i < element.asArrayElement().size(); i++) {
                                            mSubType.add(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME).valueAsString());
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                task_subtype.setVisibility(View.VISIBLE);
                                                task_subtype_name_desc.setVisibility(View.VISIBLE);
                                                task_subtype.setDatas(mContext, mSubType);
                                                task_subtype.getmEditText().setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                                                        intent.putExtra(FORM_TYPE, TASK_SUBTYPE);
                                                        intent.putStringArrayListExtra(FORM_CONTENT, mSubType);
                                                        startActivityForResult(intent, REQUEST_CODE);
                                                    }
                                                });

                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                task_subtype.setVisibility(View.GONE);
                                                task_subtype_name_desc.setVisibility(View.GONE);
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
                            e.printStackTrace();
                        }
                        break;
                    case TASK_SUBTYPE:
                        task_subtype.getmEditText().setText(str);
                        break;
                    case GROUP:
                        group.getmEditText().setText(str);
                        break;
                    case DEVICE_NAME:
                        device_name.getmEditText().setText(str);
                        break;
                }

            }
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
        hud.show();
        String taskType = task_type .getText();
        String teamName = group .getText();
        String deviceName = device_name .getText();
        String createTask = create_task.getText().toString();
        String taskDesc = task_description .getText().toString();
        String deviceNum = device_num .getText().toString();
        String taskSubType = null;
        if (View.VISIBLE ==task_subtype.getVisibility()){
            taskSubType = task_subtype.getText();
        }

        if (taskType.equals(getResources().getString(R.string.select))){
            Toast.makeText(mContext,getResources().getString(R.string.tips_tasktype_post),Toast.LENGTH_SHORT).show();
            return;
        }
        if (taskSubType !=null) {
            if (taskSubType.equals(getResources().getString(R.string.select))) {
                Toast.makeText(mContext, getResources().getString(R.string.tips_subtype_post), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (createTask.equals("")){
            Toast.makeText(mContext,getResources().getString(R.string.tips_scan_operator_post),Toast.LENGTH_SHORT).show();
            return;
        }

        if (teamName.equals(getResources().getString(R.string.select))){
            Toast.makeText(mContext,getResources().getString(R.string.tips_team_type_post),Toast.LENGTH_SHORT).show();
            return;
        }

        if (deviceName.equals(getResources().getString(R.string.select))){
            Toast.makeText(mContext,getResources().getString(R.string.tips_device_name_post),Toast.LENGTH_SHORT).show();
            return;
        }


        if (deviceNum.equals(getResources().getString(R.string.scan))){
            Toast.makeText(mContext,getResources().getString(R.string.tips_device_num_post),Toast.LENGTH_SHORT).show();
            return;
        }

        if (taskDesc.equals(getResources().getString(R.string.task_description_hint))){
            Toast.makeText(mContext,getResources().getString(R.string.tips_task_desc_post),Toast.LENGTH_SHORT).show();
            return;
        }

        //
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hud.dismiss();
                    TipsDialog tipsDialog = new TipsDialog(mContext, R.style.MyDialog);
                    tipsDialog.show();
                }
            }, 1000);

    }

    void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();
        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
        }
        if (nfctag == CREATER) {
            nfctag = 0;
            create_task.setText("李伟");
            nfcDialog.dismiss();
            Toast.makeText(mContext, "刷卡成功", Toast.LENGTH_SHORT).show();
        } else if (nfctag == DEVICE_NUM) {
            nfctag = 0;
            device_num.setText("AB0001234");
            nfcDialog.dismiss();
            Toast.makeText(mContext, "刷卡成功", Toast.LENGTH_SHORT).show();
        }

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

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};

            }
            // Setup the views
            buildTagViews(msgs);

        }
    }

    private void showWirelessSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.nfc_disabled);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        return;
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }


    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }


}
