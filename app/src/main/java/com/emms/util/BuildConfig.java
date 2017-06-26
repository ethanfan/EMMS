package com.emms.util;

import android.app.Application;
import android.content.Context;

import com.emms.activity.AppApplication;
import com.zxing.android.decoding.Intents;

public class BuildConfig {
    //打包时修改该值
	// UAT到清单文件中修改JPUSH_APPKEY为5e06ea48dfe4d377295cbff3
	// 测试环境到清单文件中修改JPUSH_APPKEY为8f158ccb3769786e8814d044
	//PROD环境到清单文件中修改JPUSH_APPKEY为d77ebacf4c4368c50e5d8081
    //UAT包需要把两个String.xml文件的app_name改为EMMSUAT,PROD包String.xml文件的app_name为EMMS
	//PROD打包release,UAT打包debug,DEVELOPMENT打包preview

	public enum APPEnvironment{
		DEVELOPMENT,
		UAT,
		PROD
	}
    public static APPEnvironment appEnvironment=APPEnvironment.UAT;
	//Production
	//UAT

//    public static  final String UATServer="http://42.159.202.12/emmswebapi/api/BaseOrganise/GetAllFactoriesAndURL";

	public static String getFactoryListUrl(Context context){
		if(SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")){
			return "http://10.20.252.20/emmswebapi/api/BaseOrganise/GetAllFactoriesAndURL";
		}else {
			return "http://42.159.202.12/emmswebapi/api/BaseOrganise/GetAllFactoriesAndURL";
		}
	}

	public static String getServerAPIEndPoint(Context context) {
		if(SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")){
			return SharedPreferenceManager.getInteranetUrl(context);
		}else {
			return SharedPreferenceManager.getExtranetUrl(context);
		}
	}


	public static void NetWorkSetting(Context context){

    }
}
