/**
 * $Class.java
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
package com.jaffer_datastore_android_sdk.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

/**
 * Unility class that reflects a field.
 */
public final class $Class {
	
	/** A list of primitive types. */
	private static final Class<?>[] PRIMITIVE_TYPES = {
		int.class,
		long.class,
		short.class,
	    float.class,
	    double.class,
	    byte.class,
	    boolean.class,
	    char.class,
	    Integer.class,
	    Long.class,
	    Short.class,
	    Float.class,
	    Double.class,
	    Byte.class,
	    Boolean.class,
	    Character.class };
	
	/** Prevent other classes from instantiating this class. */
	private $Class() {
	}
	
	/**
	 * Returns whether the given {@code target} is a primitive object.
	 * 
	 * @param target
	 *            The object to determine whether it is a primitive type
	 * @return {@code true} if the given object is of Java primitive types,
	 *         {@code false} otherwise
	 */
	public static boolean isPrimitive(Object target) {
		Class<?> classOfPrimitive = target.getClass();
		for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
			if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the value of the {@link Field} represented by {@link name}. 
	 * The value is automatically wrapped in an object if it has a primitive type.
	 * 
	 * @param name the name of the field
	 * @param obj object from which the represented field's value is to be extracted
	 * @return the value of the represented field in the object {@code obj}; primitive values 
	 * are wrapped in an appropriate object before being returned
	 */
	public static Object getFieldValue(String name, Object obj) {
		try {
			Class<?> rawType = obj.getClass();
			TypeToken<?> typeToken = TypeToken.get(rawType);
			while (rawType != Object.class) {
	  		Field[] fields = rawType.getDeclaredFields();
	  		for (Field field : fields) {
	  			if (field.getName().equals(name)) {
	  				field.setAccessible(true);
	  				return field.get(obj);
	  			}
	  		}
	  		
	  		typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
				rawType = typeToken.getRawType();
			}
		} catch (Exception ex) {
			// Ignored
		}
		return null;
	}
	
	/**
	 * Invokes the underlying method represented by the {@link Method} with given {@code name}, on the specified object with the 
	 * specified parameters. Individual parameters are automatically unwrapped to match primitive formal parameters, and both 
	 * primitive and reference parameters are subject to method invocation conversions as necessary.
	 * 
	 * @param name the name of the method
	 * @param obj the object the underlying method is invoked from
	 * @param args the arguments used for the method call
	 * @return the result of dispatching the method represented by this object on obj with parameters args
	 */
	public static Object invokeMethod(String name, Object obj, Object... args) {
		try {
			Class<?> rawType = obj.getClass();
			TypeToken<?> typeToken = TypeToken.get(rawType);
			while (rawType != Object.class) {
				Method[] methods = rawType.getDeclaredMethods();
				for (Method method : methods) {
					if (method.getName().equals(name)) {
						method.setAccessible(true);
						return method.invoke(obj, args);
					}
				}
				
				typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
				rawType = typeToken.getRawType();
			}
		} catch (Exception ex) {
			// Ignored
		}
		return null;
	}

}
