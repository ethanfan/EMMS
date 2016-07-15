/**
 * ExceptionSerializer.java
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
package com.datastore_android_sdk.serialization;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes an exception into a JSON object.
 * 
 * @author Stanley Lam
 */
public class ExceptionSerializer implements JsonSerializer<Exception> {
  public JsonElement serialize(Exception src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		
		Throwable cause = src;
		do {
			if (cause == null || cause.getCause() == null) {
				break;
			}
			cause = cause.getCause();
		} while (true);
		
		String message = cause == null ? null : cause.getMessage();
		json.add("error", message == null ? JsonNull.INSTANCE : new JsonPrimitive(message));
		return json;
  }
}
