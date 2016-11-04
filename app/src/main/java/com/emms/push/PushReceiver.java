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
import com.emms.R;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
			final Bundle bun=bundle;
			final Context ctx=context;
			if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH) {
				DataUtil.getDataFromLanguageTranslation(ctx, bun.getString(JPushInterface.EXTRA_ALERT), new StoreCallback() {
					@Override
					public void success(DataElement element, String resource) {
						if (element.isArray() && element.asArrayElement().size() > 0) {
							showInspectorRecordNotification(ctx, DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Translation_Display")));
						} else {
							showInspectorRecordNotification(ctx, bun.getString(JPushInterface.EXTRA_ALERT));
						}
					}
					@Override
					public void failure(DatastoreException ex, String resource) {
						showInspectorRecordNotification(ctx, bun.getString(JPushInterface.EXTRA_ALERT));
					}
				});
			}else {
				showInspectorRecordNotification(ctx, bun.getString(JPushInterface.EXTRA_ALERT));
			}
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

	//send msg to MainActivity
	private void processCustomMessage(final Context context, final Bundle bundle) {
		if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH) {
			DataUtil.getDataFromLanguageTranslation(context, bundle.getString(JPushInterface.EXTRA_MESSAGE), new StoreCallback() {
				@Override
				public void success(DataElement element, String resource) {
					if (element.isArray() && element.asArrayElement().size() > 0) {
						showInspectorRecordNotification(context, DataUtil.isDataElementNull(element.asArrayElement().get(0).asObjectElement().get("Translation_Display")));
					} else {
						showInspectorRecordNotification(context, bundle.getString(JPushInterface.EXTRA_MESSAGE));
					}
				}

				@Override
				public void failure(DatastoreException ex, String resource) {
					showInspectorRecordNotification(context, bundle.getString(JPushInterface.EXTRA_MESSAGE));
				}
			});
		}else {
			showInspectorRecordNotification(context, bundle.getString(JPushInterface.EXTRA_MESSAGE));
		}
		//JsonObjectElement jsonObjectElement = new JsonObjectElement(message);


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
