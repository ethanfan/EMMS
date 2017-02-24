package com.emms.util;

import android.app.Application;
import android.content.Context;

import com.emms.activity.AppApplication;

public class BuildConfig {
    //打包时修改该值
	// UAT为true并到清单文件中修改JPUSH_APPKEY为5e06ea48dfe4d377295cbff3
	// 测试环境为false,并到清单文件中修改JPUSH_APPKEY为8f158ccb3769786e8814d044

    public static boolean isDebug=true;
	//Production
//	private static final String productionAPIEndPoint = "http://172.168.4.156:5005/api/";
//	private static final String productionConfigurationEndPoint = "http://172.168.4.156:5005/api/Token";
//	private static final String productionContentServerEndPoint = "http://172.168.4.156:5005/api/DataBase";
//	private static final String productionContentServerDownload = "http://172.168.4.156:5005/api/DataBase";
//	private static final String productionAPIEndPoint = "http://172.168.4.253/emmstestAPI/api/";
//	private static final String productionConfigurationEndPoint = "http://172.168.4.253/emmstestAPI/api/Token";
//	private static final String productionContentServerEndPoint = "http://172.168.4.253/emmstestAPI/api/DataBase";
//	private static final String productionContentServerDownload = "http://172.168.4.253/emmstestAPI/api/DataBase";
	private static final String productionAPIEndPoint = "http://devazure.esquel.cn:80/EMMS/api/";
	private static final String productionConfigurationEndPoint = "http://devazure.esquel.cn:80/EMMS/api/Token";
	private static final String productionContentServerEndPoint = "http://devazure.esquel.cn/EMMS//api/DataBase";
	private static final String productionContentServerDownload = "http://devazure.esquel.cn/EMMS//api/DataBase";

    //Development
	private static final String developmentAPIEndPoint = "http://devazure.esquel.cn:80/EMMS/api/";
	private static final String developmentConfigurationEndPoint = "http://devazure.esquel.cn:80/EMMS/api/Token";
	private static final String developmentContentServerEndPoint = "http://devazure.esquel.cn/EMMS//api/DataBase";
	private static final String developmentContentServerDownload = "http://devazure.esquel.cn/EMMS//api/DataBase";

    //UAT
	private static final String uatAPIEndPoint = "http://42.159.202.12:80/EMMSWebAPI/api/";
	private static final String uatConfigurationEndPoint = "http://42.159.202.12:80/EMMSWebAPI/api/Token";
	private static final String uatContentServerEndPoint = "http://42.159.202.12:80/EMMSWebAPI/api/DataBase";
	private static final String uatContentServerDownload = "http://42.159.202.12:80/EMMSWebAPI/api/DataBase";

	//Garment
	private static final String garmentAPIEndPoint = "http://10.20.252.20/emmswebapi/api/";
	private static final String garmentConfigurationEndPoint = "http://10.20.252.20/emmswebapi/api/Token";
	private static final String garmentContentServerEndPoint = "http://10.20.252.20/emmswebapi/api/DataBase";
	private static final String garmentContentServerDownload = "http://10.20.252.20/emmswebapi/api/DataBase";

	//GarmentTest
//	private static final String garmentTestAPIEndPoint = "http://192.168.4.63/emmsEGMtestAPI/";
//	private static final String garmentTestConfigurationEndPoint = "http://192.168.4.63/emmsEGMtestAPI/Token";
//	private static final String garmentTestContentServerEndPoint = "http://192.168.4.63/emmsEGMtestAPI/DataBase";
//	private static final String garmentTestContentServerDownload = "http://192.168.4.63/emmsEGMtestAPI/DataBase";
	private static final String garmentTestAPIEndPoint = "http://devazure.gfgl.esquel.com/EMMS/";
	private static final String garmentTestConfigurationEndPoint = "http://devazure.gfgl.esquel.com/EMMS/Token";
	private static final String garmentTestContentServerEndPoint = "http://devazure.gfgl.esquel.com/EMMS/DataBase";
	private static final String garmentTestContentServerDownload = "http://devazure.gfgl.esquel.com/EMMS/DataBase";
//DB下载地址
//	https://edpazure.esquel.cn/apps/hrcampus/prod/EMMS.zip




	public static String getConfigurationEndPoint() {
      switch (AppApplication.endPoint){
		  case DEVELOPMENT:{
			  return developmentConfigurationEndPoint;
		  }
		  case UAT:{
			  return uatConfigurationEndPoint;
		  }
		  case PRODUCTION:{
			  return productionConfigurationEndPoint;
		  }
		  case GARMENT:{
			  return garmentConfigurationEndPoint;
		  }
		  case GARMENTTEST:{
			  return garmentTestConfigurationEndPoint;
		  }
		  default:
			  return productionConfigurationEndPoint;
	  }

	}
	
	public static String getContentServerEndPoint() {

			return productionContentServerEndPoint;
	}
	
	public static String getServerAPIEndPoint() {
		switch (AppApplication.endPoint){
			case DEVELOPMENT:{
				return developmentAPIEndPoint;
			}
			case UAT:{
				return uatAPIEndPoint;
			}
			case PRODUCTION:{
				return productionAPIEndPoint;
			}
			case GARMENT:{
				return garmentAPIEndPoint;
			}
			case GARMENTTEST:{
				return garmentTestAPIEndPoint;
			}
			default:
				return productionAPIEndPoint;
		}

	}

	public static String getConfigurationDownload() {

		return productionConfigurationEndPoint;

	}
	public static void NetWorkSetting(Context context){
        if(BuildConfig.isDebug){
            if(SharedPreferenceManager.getNetwork(context)!=null){
                if(SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")){
                    AppApplication.endPoint= AppApplication.ServerEndPoint.GARMENTTEST;
                }else {
					AppApplication.endPoint= AppApplication.ServerEndPoint.DEVELOPMENT;
                }
            }else {
				AppApplication.endPoint= AppApplication.ServerEndPoint.DEVELOPMENT;
            }
//			AppApplication.endPoint= AppApplication.ServerEndPoint.PRODUCTION;
        }else {
            if(SharedPreferenceManager.getNetwork(context)!=null){
                if(SharedPreferenceManager.getNetwork(context).equals("InnerNetwork")){
					AppApplication.endPoint= AppApplication.ServerEndPoint.UAT;
                }else {
					AppApplication.endPoint= AppApplication.ServerEndPoint.UAT;
                }
            }else {
				AppApplication.endPoint= AppApplication.ServerEndPoint.UAT;
            }
        }
    }
}
