package com.emms;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ProgressBar;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.util.DataUtil;
import com.emms.util.DownloadCallback;
import com.emms.util.DownloadTask;

import org.restlet.data.Reference;

import java.io.File;


/**
 *
 * @author hung
 *
 */
public final class ConfigurationManager {

    private static final String FILE_NAME = "emms.apk";
    private static final String FIELD_VALUE_VERSION = "version";
    private static final String PARAM_NAME_FIELD_NAME = "paramName";
    private static final String CHECKING_PARAMETERS_FIELD_NAME = "checkingParameters";
    private static final String PARAM_VALUE_FIELD_NAME = "paramValue";
    private static final String PLATFORM_FIELD_NAME = "android";
    private ConfigurationTask task;
    private static ConfigurationManager manager;
    private static final String CONFIGURATION_FIELD_NAME = "configurations";
    public static final String RESOURCE_END_POINT_FIELD_NAME = "resourceEndpoint";
    public static final String CONTENT_RESOURCE_END_POINT_FIELDNAME = "contentResourceEndpoint";
    public static final String AUTHORIZATION_END_POINT = "authorizationEndpoint";
    public static final String REVOCATION_END_POINT = "revocationEndpoint";
    private static final String MESSAGE_FIELD_NAME = "messages";
    private static final String CONFIG_FILE_NAME = "config.json";
    public static final String CONFIG_VERSION_INFO = "versioninfo";
    private AlertDialog dialog ;
   // private DownloadTask downloadTask;


    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (manager == null) {
            manager = new ConfigurationManager();
        }

        return manager;
    }

    public void startToGetNewConfig(Context context) {
        task = new ConfigurationTask(context);
        task.execute((Void) null);
    }

    private void showDialog(final Context context, final ObjectElement element) {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (dialog == null || dialog.getContext() != context) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            context);
                    dialog = builder.create();
                }
                DataElement e = element.get("Header");
                if (e != null && e.isPrimitive()) {
                    dialog.setTitle(e.asPrimitiveElement()
                            .valueAsString());
                }

                e = element.get("Content");
                if (e != null && e.isPrimitive()) {
                    dialog.setMessage(e.asPrimitiveElement()
                            .valueAsString());
                }

                e = element.get("ConfirmButtonText");
                if (e != null&&e.isPrimitive()) {
//                    final DataElement clickEventUrl = element
//                            .asObjectElement().get("URL");
//                    final Reference url = new Reference(clickEventUrl.asPrimitiveElement().valueAsString());
                    String pathDir = FILE_NAME;
                    if (context.getExternalFilesDir(null) != null) {
                        pathDir = context.getExternalFilesDir(null).toString() + "/" + FILE_NAME;
                    }
                    final File file = new File(pathDir);
                    //final Reference destination = new Reference(file.getAbsolutePath());
//                    if (downloadTask == null) {
//                        downloadTask = new DownloadTask(context, url, destination);
//                    }
                    dialog.setButton(e.asPrimitiveElement()
                            .valueAsString(), new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            final DataElement clickEventUrl = element.get("URL");
//                            if (clickEventUrl != null
//                                    && clickEventUrl.isPrimitive()) {
                                //MobclickAgent.onEvent(context, "app_upgrade");
                                ProgressBar progressView = new ProgressBar(context);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setCancelable(false);
                                alertDialog.setView(progressView);
                                alertDialog.setTitle(R.string.downloading);
                                final Dialog d=alertDialog.create();
                                d.show();
                                HttpUtils.download(context, file.getAbsolutePath(), DataUtil.isDataElementNull(element.get("URL")), null, new HttpCallback() {
                                    @Override
                                    public void onSuccess(String t) {
                                        super.onSuccess(t);
                                        d.dismiss();
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int errorNo, String strMsg) {
                                        d.dismiss();
                                        super.onFailure(errorNo, strMsg);
                                    }
                                });

                            }
                        });
                }
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        });
                }





    private class ConfigurationTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public ConfigurationTask(Context context) {
            setContext(context);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpParams httpParams=new HttpParams();
            HttpUtils.getWithoutCookies(mContext, "System_Version/GetAppDownloadInfo", httpParams, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    if(t!=null) {
                        handleVersionUpdate(mContext, t);
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            });


             return null;
        }

        public Context getContext() {
            return mContext;
        }

        public void setContext(Context context) {
            this.mContext = context;
        }
    }

    /*
     * handle the version is it updated
     *
     * @param element must passing in the ObjectElement of "message"
     */
    private void handleVersionUpdate(Context context, String element) {
        JsonObjectElement json=new JsonObjectElement(element);
        ObjectElement data=json.get(Data.PAGE_DATA).asArrayElement().get(0).asObjectElement();
        int version=data.get("Version").valueAsInt();
        try {
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int CurrentVersion=packageInfo.versionCode;
            if(CurrentVersion<version){
                showDialog(context,data);
            }
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
