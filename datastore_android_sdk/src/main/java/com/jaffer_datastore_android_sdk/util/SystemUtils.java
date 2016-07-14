/**
 * SystemUtils.java
 *
 * Copyright (c) 2008-2014 Joy Aether Limited. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * 
 * This unpublished material is proprietary to Joy Aether Limited.
 * All rights reserved. The methods and
 * techniques described herein are considered trade secrets
 * and/or confidential. Reproduction or distribution, in whole
 * or in part, is forbidden except by express written permission
 * of Joy Aether Limited.
 */
package com.jaffer_datastore_android_sdk.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

/**
 * A class containing utility methods related to the Android system.
 */
public class SystemUtils {
	
	/**
	 * The headset that state that indicates a headset is plugged in
	 */
	public static final int HEADSET_STATE_PLUGGED = 1;
	
	/**
	 * The headset that state that indicates a headset is unplugged in
	 */
	public static final int HEADSET_STATE_UNPLUGGED = 0;
	
	private SystemUtils() {
	};

	@TargetApi(11)
	public static void enableStrictMode() {
		if (SystemUtils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = 
				new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = 
				new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

			if (SystemUtils.hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
			}
			
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}
}
