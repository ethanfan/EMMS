package com.emms.util;

public class BuildConfig {
	
	public enum ServerEndPoint {
		GAOMING,
		PRODUCTION,
		DEVELOPMENT,
		UAT,
		AZURE_UAT,
		GARMENT
	}
	
	public static final ServerEndPoint endPoint = ServerEndPoint.DEVELOPMENT;

	//Production
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
//DB下载地址
//	https://edpazure.esquel.cn/apps/hrcampus/prod/EMMS.zip

	public static String getConfigurationEndPoint() {
      switch (endPoint){
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
		  default:
			  return productionConfigurationEndPoint;
	  }

	}
	
	public static String getContentServerEndPoint() {

			return productionContentServerEndPoint;
	}
	
	public static String getServerAPIEndPoint() {
		switch (endPoint){
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
			default:
				return productionAPIEndPoint;
		}

	}

	public static String getConfigurationDownload() {

		return productionConfigurationEndPoint;

	}
}
