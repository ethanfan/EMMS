package com.emms.util;

public class BuildConfig {
	
	public enum ServerEndPoint {
		GAOMING,
		PRODUCTION,
		DEVELOPMENT,
		UAT,
		AZURE_UAT
	
	}
	
	public static final ServerEndPoint endPoint = ServerEndPoint.UAT;

	
	private static final String productionAPIEndPoint = "http://devazure.esquel.cn:80/EMMS/api/";
	private static final String productionConfigurationEndPoint = "http://devazure.esquel.cn:80/EMMS/api/Token";
	private static final String productionContentServerEndPoint = "http://devazure.esquel.cn/EMMS//api/DataBase";
	private static final String productionContentServerDownload = "http://devazure.esquel.cn/EMMS//api/DataBase";

	public static String getConfigurationEndPoint() {

		return productionConfigurationEndPoint;

	}
	
	public static String getContentServerEndPoint() {

			return productionContentServerEndPoint;
	}
	
	public static String getServerAPIEndPoint() {

				return productionAPIEndPoint;	
	}

	public static String getConfigurationDownload() {

		return productionConfigurationEndPoint;

	}
}
