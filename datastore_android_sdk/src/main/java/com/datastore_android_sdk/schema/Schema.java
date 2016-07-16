/**
 * Schema.java
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
package com.datastore_android_sdk.schema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * This is the class to retrieve information on individual data model(s).
 * 
 * @author Stanley Lam
 */
public final class Schema {
	
	public static final String UTC_TIME_ZONE = "UTC";
	
	/** A {@link Map} of data model attributes cached. */
	private static final Map<Type, ModelAttributes> ATTRS_CACHED = 
		Collections.synchronizedMap(new HashMap<Type, ModelAttributes>());
	
	/** Prevent this class from being instantiated. */
	private Schema() {}
	
	/**
	 * Returns the attribute for {@code} raw.
	 * 
	 * @param raw
	 *          The type whose attribute to return
	 * @return The ModelAttributes object for the given type
	 */
	public static ModelAttributes getAttributes(Class<?> raw) {
		ModelAttributes attributes = ATTRS_CACHED.get(raw);
		if (attributes != null) {
			return attributes;
		}
		
		attributes = new ModelAttributes(raw);
		ATTRS_CACHED.put(raw, attributes);
		return attributes;
	}
	
	/**
	 * Returns the current date time in the predefined time zone.
	 * @return The current Date in the given time zone
	 * @deprecated Time zone of the schema is set during initialization of the
	 *             application. Use new Date() instead
	 */
	@Deprecated
	public static Date getCurrentDate() {
		TimeZone.setDefault(TimeZone.getTimeZone(UTC_TIME_ZONE));
		return new Date();
	}

	/**
	 * Clears the cached attribute.
	 */
	public static void clearCache() {
		ATTRS_CACHED.clear();
	}

}
