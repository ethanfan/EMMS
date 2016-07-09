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
import com.emms.record.NdefMessageParser;
import com.emms.record.ParsedNdefRecord;
import com.emms.ui.DropEditText;
import com.emms.ui.KProgressHUD;
import com.emms.ui.NFCDialog;
import com.emms.ui.TipsDialog;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.jaffer_datastore_android_sdk.datastore.DataElement;

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

    private DropEditText task_type,task_subtype,group,device_name;
    private EditText create_task ,task_description,device_num;
    private Button btn_sure;
    private ImageView create_task_action,device_num_action;
    private KProgressHUD hud;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private NdefMessage mNdefPushMessage;

    private AlertDialog mDialog;
    private static final DateFormat TIME_FORMAT = SimpleDateFormat.getDateTimeInstance();

    private int width ;

    public final static String FORM_TYPE ="formtype";
    public final static String FORM_CONTENT ="content";
    public final static int REQUEST_CODE =10000;
    public final static int TASK_TYPE = 1;
    public final static int TASK_SUBTYPE = 2;
    public final static int DEVICE_NAME = 3;
    public final static int GROUP = 4;
    public final static int CREATER = 5;
    public final static int DEVICE_NUM = 6;
    public final static int TASK_DESCRIPTION = 7;

    private int  nfctag =0;
    private ArrayList<String> datalist;

    private ArrayList<String> mDatalist = new ArrayList<String>();
    private NFCDialog nfcDialog;
    private Cursor cursor;
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
        mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
                "Message from NFC Reader :-)", Locale.ENGLISH, true) });
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

        String rawQuery = "select Team_ID,TeamName from Operator where Operator_ID=4204";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_ARTICLE, null);
//        GetUserList();
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(DataElement dataElement) {
                System.out.println(dataElement);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
        final  ArrayList<String> mList = new ArrayList<String>() {
            {
                add("维修");
                add("维护");
                add("搬车");
            }
        };

      final  ArrayList<String> m1List = new ArrayList<String>() {
            {
                add("平车");
                add("领车");
                add("搬车");
            }
        };

        final  ArrayList<String> m2List = new ArrayList<String>() {
            {
                add("普通搬车");
                add("特定搬车");
                add("大型设备调整");
            }
        };

        final  ArrayList<String> m3List = new ArrayList<String>() {
            {
                add("A");
                add("B");
                add("C");
                add("D");
                add("E");
            }
        };
        ViewTreeObserver vto = device_name.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                device_name.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                device_name.getHeight();
                width = device_name.getWidth() + 35;
                device_name.setDatas(mContext, width, m1List);
                task_type.setDatas(mContext, width, mList);
                task_subtype.setDatas(mContext, width, m2List);
                group.setDatas(mContext, width, m3List);
            }
        });

        task_type.getmEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                intent.putExtra(FORM_TYPE, TASK_TYPE);
                intent.putStringArrayListExtra(FORM_CONTENT, mList);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        group.getmEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTaskActivity.this,searchActivity.class);
                intent.putExtra(FORM_TYPE, GROUP);
                intent.putStringArrayListExtra(FORM_CONTENT, m3List);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        task_subtype.getmEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                intent.putExtra(FORM_TYPE, TASK_SUBTYPE);
                intent.putStringArrayListExtra(FORM_CONTENT, m2List);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        device_name.getmEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTaskActivity.this, searchActivity.class);
                intent.putExtra(FORM_TYPE, DEVICE_NAME);
                intent.putStringArrayListExtra(FORM_CONTENT, m1List);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.create_task));
        findViewById(R.id.edit_resume).setOnClickListener(this);

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

         nfcDialog =new NFCDialog(mContext,R.style.MyDialog){
            @Override
            public void dismissAction() {
                nfctag =0;
            }
        };
        create_task.setText("何邵勃");
        device_num.setText("AD0001528");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE)
        {
            if (resultCode==searchActivity.RESULT_CODE)
            {
                Bundle bundle=data.getExtras();
                String str=bundle.getString(searchActivity.BACK_CONTENT);
                int index =bundle.getInt(FORM_TYPE);
                switch (index){
                    case TASK_TYPE:
                        task_type.getmEditText().setText(str);
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

        }else if (id == R.id.create_task_action){
            if (mAdapter == null) {
                showMessage(R.string.error, R.string.no_nfc);
                return;
            }
            nfctag =CREATER;
            nfcDialog.show();
        } else if (id == R.id.device_num_action){
            if (mAdapter == null) {
                showMessage(R.string.error, R.string.no_nfc);
                return;
            }
            nfctag =DEVICE_NUM;
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
    private android.os.Handler mHandler =new android.os.Handler();
    private void createRequest() {
        hud.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
                TipsDialog tipsDialog =new TipsDialog(mContext,R.style.MyDialog);
                tipsDialog.show();
            }
        },1000);

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
            nfctag=0;
            create_task.setText("李伟");
            nfcDialog.dismiss();
            Toast.makeText(mContext,"刷卡成功",Toast.LENGTH_SHORT).show();
        }else if (nfctag == DEVICE_NUM){
            nfctag=0;
            device_num.setText("AB0001234");
            nfcDialog.dismiss();
            Toast.makeText(mContext,"刷卡成功",Toast.LENGTH_SHORT).show();
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
                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
                msgs = new NdefMessage[] { msg };
               
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
