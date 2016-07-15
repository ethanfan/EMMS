/**
 * QueryDeserializers.java
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import com.datastore_android_sdk.DatastoreException.QueryParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.reflect.TypeToken;


/**
 * The query deserializers for basic types.
 */
public final class QueryDeserializers {
	
	public static final QueryDeserializer<Object> DEFAULT = new QueryDeserializer<Object>() {
		@Override
		public Object deserialize(Object data, Type typeOfT) throws QueryParseException {
			return data;
		}
	};
	
	public static final QueryDeserializer<String> STRING = new QueryDeserializer<String>() {
		@Override
		public String deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return data.toString().replace("'", "\\'");
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory STRING_FACTORY = newFactory(String.class, STRING);
	
	public static final QueryDeserializer<Boolean> BOOLEAN = new QueryDeserializer<Boolean>() {
		@Override
		public Boolean deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Boolean.parseBoolean(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory BOOLEAN_FACTORY = newFactory(boolean.class, Boolean.class, BOOLEAN);
	
	public static final QueryDeserializer<Byte> BYTE = new QueryDeserializer<Byte>() {
		@Override
		public Byte deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Byte.parseByte(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory BYTE_FACTORY = newFactory(byte.class, Byte.class, BYTE);
	
	public static final QueryDeserializer<Short> SHORT = new QueryDeserializer<Short>() {
		@Override
		public Short deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Short.parseShort(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory SHORT_FACTORY = newFactory(short.class, Short.class, SHORT);
	
	public static final QueryDeserializer<Integer> INTEGER = new QueryDeserializer<Integer>() {
		@Override
		public Integer deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Integer.parseInt(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory INTEGER_FACTORY = newFactory(int.class, Integer.class, INTEGER);
	
	public static final QueryDeserializer<Long> LONG = new QueryDeserializer<Long>() {
		@Override
		public Long deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Long.parseLong(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory LONG_FACTORY = newFactory(long.class, Long.class, LONG);
	
	public static final QueryDeserializer<Float> FLOAT = new QueryDeserializer<Float>() {
		@Override
		public Float deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Float.parseFloat(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory FLOAT_FACTORY = newFactory(float.class, Float.class, FLOAT);
	
	public static final QueryDeserializer<Double> DOUBLE = new QueryDeserializer<Double>() {
		@Override
		public Double deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return Double.parseDouble(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory DOULBE_FACTORY = newFactory(double.class, Double.class, DOUBLE);
	
	public static final QueryDeserializer<Number> NUMBER = new QueryDeserializer<Number>() {
		@Override
		public Number deserialize(Object data, Type typeOfT) throws QueryParseException {
			return new LazilyParsedNumber(data.toString());
		}
	};
	
	public static final QueryDeserializerFactory NUMBER_FACTORY = newFactory(Number.class, NUMBER);
	
	public static final QueryDeserializer<BigDecimal> BIG_DECIMAL = new QueryDeserializer<BigDecimal>() {
		@Override
		public BigDecimal deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return new BigDecimal(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory BIG_DECIMAL_FACTORY = newFactory(BigDecimal.class, BIG_DECIMAL);
	
	public static final QueryDeserializer<BigInteger> BIG_INTEGER = new QueryDeserializer<BigInteger>() {
		@Override
		public BigInteger deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return new BigInteger(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory BIG_INTEGER_FACTORY = newFactory(BigInteger.class, BIG_INTEGER);
	
	public static final QueryDeserializer<URL> URL = new QueryDeserializer<URL>() {
		@Override
		public URL deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return new URL(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory URL_FACTORY = newFactory(URL.class, URL);
	
	public static final QueryDeserializer<URI> URI = new QueryDeserializer<URI>() {
		@Override
		public URI deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return new URI(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory URI_FACTORY = newFactory(URI.class, URI);
	
	public static final QueryDeserializer<InetAddress> INET_ADDRESS = new QueryDeserializer<InetAddress>() {
		@Override
		public InetAddress deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return InetAddress.getByName(data.toString());
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory INET_ADDRESS_FACTORY = newFactory(InetAddress.class, INET_ADDRESS);
	
	public static final QueryDeserializer<UUID> UUID = new QueryDeserializer<UUID>() {
		@Override
		public UUID deserialize(Object data, Type typeOfT) throws QueryParseException {
			try {
				return java.util.UUID.fromString(data.toString()); // NOPMD
			} catch (Exception e) {
				throw new QueryParseException(e);
			}
		}
	};
	
	public static final QueryDeserializerFactory UUID_FACTORY = newFactory(UUID.class, UUID);
	
	/**
	 * A derializer that deserializes a value into its appropriate enumeration type.
	 * 
	 * @param <T> the Java type of the enumeration
	 */
	public static final class EnumQueryDeserializer<T extends Enum<T>> extends QueryDeserializer<T> {
		private final Map<String, T> nameToConstant = new HashMap<String, T>();
		
		/**
		 * Returns a deserializer that deserializes an enumeration.
		 * 
		 * @param classOfT
		 *            the Java type of enumeration
		 */
		public EnumQueryDeserializer(Class<T> classOfT) {
			try {
				for (T constant : classOfT.getEnumConstants()) {
					String name = constant.name();
					SerializedName annotation = classOfT.getField(name).getAnnotation(SerializedName.class);
					if (annotation != null) {
						name = annotation.value();
					}
					nameToConstant.put(name, constant);
				}
			} catch (NoSuchFieldException e) {
				throw new AssertionError(e);
			}
		}
		
		@Override
		public T deserialize(Object data, Type typeOfT) throws QueryParseException {
			T result = nameToConstant.get(data.toString()); 
			if (result == null) {
				for (T constant : nameToConstant.values()) {
					if (data.toString().equalsIgnoreCase(constant.toString())) {
						result = constant;
						break;
					}
				}
			}
			return result;
		}
	}
	
	public static final QueryDeserializerFactory ENUM_FACTORY = newEnumTypeDeserializerFactory(); // NOPMD
	
	public static QueryDeserializerFactory newEnumTypeDeserializerFactory() {
		return new QueryDeserializerFactory() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public <T> QueryDeserializer<T> create(TypeToken<T> typeToken) {
				Class<? super T> rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
          return null;
        }
        if (!rawType.isEnum()) {
          rawType = rawType.getSuperclass(); // handle anonymous subclasses
        }
        return (QueryDeserializer<T>) new EnumQueryDeserializer(rawType);
			}
		};
	}
	
	public static final QueryDeserializer<Date> DATE = new QueryDeserializer<Date>() { // NOPMD
		private final String[] patterns = {
				"yyyy-MM-dd HH:mm:ss.SSSSSS",
				"yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
				"yyyy-MM-dd HH:mm:ss.SSSS",
				"yyyy-MM-dd'T'HH:mm:ss.SSSS",
				"yyyy-MM-dd'T'HH:mm:ssZ",
				"yyyy-MM-dd hh:mm:ss",
				"yyyy-MM-dd'T'hh:mm:ss"};
		@Override
		public Date deserialize(Object data, Type typeOfT) throws QueryParseException {
			for (String pattern : patterns) {
				try {
					SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
					format.setTimeZone(TimeZone.getTimeZone(Schema.UTC_TIME_ZONE));
					return format.parse(data.toString());
				} catch (Exception ignored) {
					// Ignores parse exceptions
				}
			}
			
			try {
				Long longDate = Long.parseLong(data.toString());
				return new Date(longDate);
			} catch (Exception e) {
				throw new QueryParseException("Invalid date format", e);
			}
		}
	};
	
	/**
	 * Creates a new deserializer factory with the given type.
	 * 
	 * @param type the type the deserializer should support
	 * @param deserializer the deserializer to use for the given type
	 * @return the deserializer if the type was supported, null otherwise
	 */
	public static <TT> QueryDeserializerFactory newFactory(
			final Class<TT> type,
			final QueryDeserializer<?> deserializer) {
		
		return new QueryDeserializerFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> QueryDeserializer<T> create(TypeToken<T> typeToken) {
				return typeToken.getRawType() == type ? (QueryDeserializer<T>) deserializer : null;
			}
		};
	}
	
	/**
	 * Creates a new deserializer factory with the unboxed and boxed type.
	 * 
	 * @param unboxed the unboxed type
	 * @param boxed the boxed type
	 * @param deserializer the deserializer to use for the given type
	 * @return the deserializer if the type was supported, null otherwise
	 */
	public static <TT> QueryDeserializerFactory newFactory(
			final Class<TT> unboxed,
			final Class<TT> boxed,
			final QueryDeserializer<TT> deserializer) {
		
		return new QueryDeserializerFactory() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> QueryDeserializer<T> create(TypeToken<T> typeToken) {
				Class<? super T> rawType = typeToken.getRawType();
				return rawType == unboxed || rawType == boxed ? (QueryDeserializer<T>) deserializer : null; // NOPMD
			}
		};
	}

	/** Prevent this class from being instantiated. */
	private QueryDeserializers() {}

}
