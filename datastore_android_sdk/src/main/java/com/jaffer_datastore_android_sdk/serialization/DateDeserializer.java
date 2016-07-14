/**
 * DateDeserializer.java
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
package com.jaffer_datastore_android_sdk.serialization;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Deserializes milliseconds into a Java Date object.
 * 
 * @author Stanley Lam
 */
public class DateDeserializer implements JsonDeserializer<Date> {
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		String jsonDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
		Pattern pattern = Pattern.compile(jsonDateToMilliseconds);
		Matcher matcher = pattern.matcher(json.getAsJsonPrimitive().getAsString());
		String result = matcher.replaceAll("$2");

		try {
			return new Date(Long.valueOf(result));
		} catch (NumberFormatException ex) {
			throw new JsonParseException(ex);
		}
	}
}
