package com.emms.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class PushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	public static ArrayList<String> PushTagOrAliasList=new ArrayList<>();
    public static ArrayList<String> PushMessageHistory=new ArrayList<>();
	@Override
	public void onReceive(Context context, Intent intent) {
		//showInspectorRecordNotification(context,"asdfds");
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(notifactionId);
			processCustomMessage(context, bundle);
			//showInspectorRecordNotification(context,bundle.getString(JPushInterface.EXTRA_ALERT));
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

        	//打开自定义的Activity
        	Intent i = new Intent(context, TestActivity.class);
        	i.putExtras(bundle);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA)==null) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
			}
		}
		return sb.toString();
	}

	//send msg
	private synchronized void processCustomMessage(final Context context, final Bundle bundle) {
		try {






//			final String msg=bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			JsonObjectElement jsonObjectElement=new JsonObjectElement(msg);
//			final HashMap<String,String> msgMap=new HashMap<>();
//			msgMap.put("MainMessage",DataUtil.isDataElementNull(jsonObjectElement.get("MainMessage")));
//			msgMap.put("MonirMessage",DataUtil.isDataElementNull(jsonObjectElement.get("MonirMessage")));
//			String sql;
//			if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context) == LocaleUtils.SupportedLanguage.ENGLISH) {
//				sql="select distinct Message_ID,"
//						+" (case when Translation_Display is null then MessageContent"
//						+" when Translation_Display ='' then MessageContent"
//						+" else Translation_Display end) MessageContent"
//						+" FROM (select  d.[Message_ID],(select"
//						+" LT.[Translation_Display]"
//						+" from Language_Translation  LT"
//						+" where d.[MessageContent]=LT.[Translation_Code]"
//						+" and LT.[Translation_Display] is not null"
//						+" AND LT.[Translation_Display] <>''"
//						+" AND LT.[Language_Code] ='en-US'"
//						+" order by LT.Translation_ID asc limit 1"
//						+" ) Translation_Display,d.[MessageContent]"
//						+" from TaskMessage d"
//						+" where d.Message_ID in ("+DataUtil.isDataElementNull(jsonObjectElement.get("MainMessage"))+","+ DataUtil.isDataElementNull(jsonObjectElement.get("MonirMessage"))+")) a";
//			}else {
//				sql="select * from TaskMessage TM where TM.Message_ID in ("
//						+DataUtil.isDataElementNull(jsonObjectElement.get("MainMessage"))
//						+","+DataUtil.isDataElementNull(jsonObjectElement.get("MonirMessage"))+")";
//			}
//			((AppApplication)context.getApplicationContext()).getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_TASK_MESSAGE, new StoreCallback() {
//				@Override
//				public void success(DataElement element, String resource) {
//					if(element!=null&&element.isArray()){
//						HashMap<String,String> msgData=new HashMap<>();
//						for(int i=0;i<element.asArrayElement().size();i++){
//							msgData.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("Message_ID")),
//									DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get("MessageContent")));
//						}
//						showInspectorRecordNotification(context, String.format(msgData.get(msgMap.get("MainMessage")),msgData.get(msgMap.get("MonirMessage"))));
//					}
//				}
//
//				@Override
//				public void failure(DatastoreException ex, String resource) {
//
//				}
//			});
			if(PushMessageHistory==null){
				PushMessageHistory=new ArrayList<>();
			}
			final String msg=bundle.getString(JPushInterface.EXTRA_MESSAGE);
			JsonObjectElement jsonObjectElement=new JsonObjectElement(msg);
			//用于过滤不属于当前登录人的信息
			if(!PushTagOrAliasList.contains(DataUtil.isDataElementNull(jsonObjectElement.get("Operator_ID")))
					&&!PushTagOrAliasList.contains(DataUtil.isDataElementNull(jsonObjectElement.get("Organise_ID")))){
				return;
			}
			//用于过滤重复信息（MainMessage+Task_ID唯一）
			String Mes=DataUtil.isDataElementNull(jsonObjectElement.get("MainMessage"))+DataUtil.isDataElementNull(jsonObjectElement.get("Task_ID"));
//			if(PushMessageHistory.contains(Mes)){
//				if(!Mes.contains("转单")){//特殊处理A转给B,B再转给A,A再转给B等情况
//				return;
//				}
//			}else {
//				PushMessageHistory.add(Mes);
//			}
			for(String s:PushMessageHistory){
				if(Mes.contains("转单")){
					break;
				}
				if(Mes.equals(s)){
					return;
				}
			}
			PushMessageHistory.add(Mes);
			//////
			Intent intent=new Intent();
			intent.setAction("RefreshTaskNum");
			context.sendBroadcast(intent);
			final String message=DataUtil.isDataElementNull(jsonObjectElement.get("MainMessage"));
			if( (LocaleUtils.getLanguage(context)!=null
					&&  LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH )
					|| LocaleUtils.SupportedLanguage.ENGLISH == LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())  ) {
				DataUtil.getDataFromLanguageTranslation(context, message, new StoreCallback() {
					@Override
					public void success(DataElement element, String resource) {
						if (element.isArray() && element.asArrayElement().size() > 0) {
							showInspectorRecordNotification(context, DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Translation_Display")));
						} else {
							showInspectorRecordNotification(context, message);
						}
					}
					@Override
					public void failure(DatastoreException ex, String resource) {
						showInspectorRecordNotification(context, message);
					}
				});
			}else {
				showInspectorRecordNotification(context, message);
			}
		}catch (Throwable throwable){
			throwable.printStackTrace();
		}
	}
	private void showInspectorRecordNotification(Context context,String message) {
		RemoteViews customView = new RemoteViews(context.getPackageName(), R.layout.customer_notitfication_layout);
		customView.setImageViewResource(R.id.icon, R.drawable.ic_emms);
		customView.setTextViewText(R.id.text, message);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

		mBuilder.setContent(customView)
				.setContentIntent(getDefalutIntent(context,PendingIntent.FLAG_UPDATE_CURRENT))
				.setWhen(System.currentTimeMillis())
				.setTicker("")
				.setPriority(Notification.DEFAULT_VIBRATE)
				.setOngoing(false)
				.setSmallIcon(R.drawable.ic_emms);
		Notification notify = mBuilder.build();
		notify.contentView = customView;
		notify.flags |= Notification.FLAG_AUTO_CANCEL; // 点击通知后通知栏消失
		notify.defaults |=Notification.DEFAULT_ALL;
		notify.vibrate= new long[]{0,100,1100,2100,4100};
		// 通知id需要唯一，要不然会覆盖前一条通知
		int notifyId = (int) System.currentTimeMillis();
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyId, notify);
	}
	private PendingIntent getDefalutIntent(Context context,int flags) {
		Intent transferIntent = new Intent(context, TestActivity.class);
		transferIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 第二个参数不能写死，可以写一个随机数或者是时间毫秒数 保证唯一
		return PendingIntent.getActivity(context, (int)(Math.random() * 100), transferIntent, flags);
	}

}
