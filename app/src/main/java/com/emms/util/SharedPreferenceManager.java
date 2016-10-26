package com.emms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * A class to manager the value store in {@link SharedPreferences}.
 *
 * 
 */
public final class SharedPreferenceManager {

	public static final String KEY_COOKIE = "cookie";

	public static final String USER_NAME = "username";

	public static final String PASS_WORD = "password";

	public static final String KEY_LAST_SYNC_DATA_DATE = "last-sync-data-date";

	public static final String USER_DATA_FROM_SERVER = "UserData";

	public static final String LOGIN_DATA="Data";

	public static final String FACTORY="Factory";

	public static final String MSG="Msg";

	public static final String USER_ROLE_ID="UserRole_ID";

	public static final String USER_MODULE_LIST="Module_ID_List";

	public static final String LANGUAGE="language";

	public static final String LANGUAGE_CHANGE="language_change";
	private SharedPreferenceManager() {

	}

	public static String getCookie(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(KEY_COOKIE, null);
	}

	public static void setCookie(Context context, String region) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_COOKIE, region).commit();
	}

	public static String getUserName(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_NAME, null);
	}

	public static void setUserName(Context context, String username) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_NAME, username).commit();
	}

	public static String getPassWord(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(PASS_WORD, null);
	}

	public static void setPassWord(Context context, String passWord) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(PASS_WORD, passWord).commit();
	}
	public static long getLastSyncDataDate(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getLong(KEY_LAST_SYNC_DATA_DATE, 0L);
	}

	public static void setLastSyncDataDate(Context context,
										   long lastSyncDataDate) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putLong(KEY_LAST_SYNC_DATA_DATE, lastSyncDataDate).commit();
	}

	public static String getUserData(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_DATA_FROM_SERVER, null);
	}

	public static void setUserData(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_DATA_FROM_SERVER, Data).commit();
	}
	public static String getLoginData(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(LOGIN_DATA, null);
	}

	public static void setLoginData(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(LOGIN_DATA, Data).commit();
	}

	public static void setMsg(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(MSG, Data).commit();
	}
	public static String getMsg(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(MSG, null);
	}

	public static void setUserRoleID(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_ROLE_ID, Data).commit();
	}
	public static String getUserRoleID(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_ROLE_ID, null);
	}
	public static void setUserModuleList(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(USER_MODULE_LIST, Data).commit();
	}
	public static String getUserModuleList(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(USER_MODULE_LIST, null);
	}

	public static String getFactory(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(FACTORY, null);
	}

	public static void setFactory(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(FACTORY, Data).commit();
	}

	public static String getLanguage(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getString(LANGUAGE, null);
	}

	public static void setLanguage(Context context, String Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putString(LANGUAGE, Data).commit();
	}

	public static boolean getLanguageChange(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		return pref.getBoolean(LANGUAGE_CHANGE, false);
	}

	public static void setLanguageChange(Context context, boolean Data) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		pref.edit().putBoolean(LANGUAGE_CHANGE, Data).commit();
	}
}
 