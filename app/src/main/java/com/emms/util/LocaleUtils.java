package com.emms.util;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class LocaleUtils {
	
	private static final String KEY_LANGUAGE = "current-lang";
	
	public enum SupportedLanguage {
		CHINESE_SIMPLFIED(Locale.CHINESE.toString()),
		ENGLISH(Locale.ENGLISH.toString()),
		VIETNAMESE(new Locale("vi").toString());
		
		String code;
		
		SupportedLanguage(String language) {
			code = language;
		}
		
		public static SupportedLanguage getSupportedLanguage(String code) {
			
			if (CHINESE_SIMPLFIED.code.equals(code)) {
				return CHINESE_SIMPLFIED;
			} else if (VIETNAMESE.code.equals(code)) {
				return VIETNAMESE;
			} else if (ENGLISH.code.equals(code)){
				return ENGLISH;
			}
			return null;
		}
		
		public String getCode() {
			return code;
		}
	}
	
	public static final void setLanguage(Context context, SupportedLanguage language) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		pref.edit().putString(KEY_LANGUAGE, language.code).commit();
		
		Locale locale = new Locale(language.code);
	    Locale.setDefault(locale);

	    Configuration config = context.getResources().getConfiguration();
	    config.locale = locale;

	    context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

	}
	
	public static final SupportedLanguage getLanguage(Context context) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String language = pref.getString(KEY_LANGUAGE, null);	
		return SupportedLanguage.getSupportedLanguage(language);
	}

}
