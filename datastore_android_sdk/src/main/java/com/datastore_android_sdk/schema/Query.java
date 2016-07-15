/**
 * Query.java
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.datastore_android_sdk.reflect.Iterables;
import com.google.common.collect.Maps;

/**
 * A class that builds queries. This class is not thread safe. Make sure to
 * synchronize all calls.
 * 
 * Example:
 * {@code
 * 	Query query = new Query().fieldIsEqualTo("name", "batman").fieldIsLike("last_name", "Wayne");
 * }
 * 
 * <p>
 * You can operate on a particular field as many time as you want. The resulting
 * query will 'AND' all fields by default.
 * </p>
 */
public class Query implements Cloneable {
  
	/** The separator that separates values. */
	public static final String VALUE_SEPARATOR = ",";

	/** The separator that separates fields. */
	public static final String FIELD_SEPARATOR = "/";

	/** The high and low range in a 'BETWEEN' statement. */
	public static final String LOW_RANGE = "low";
	public static final String HIGH_RANGE = "high";
	
	/**
	 * The underlying map containing all query operations.
	 */
	private final Map<String, Map<String, ?>> arguments = new HashMap<String, Map<String, ?>>();
	private final Map<String, String> orderings = new LinkedHashMap<String, String>();
	private Map<String, ?> selectFields = null;
	private Map<String, ?> expandingFields = null;
	private String groupByColumnName = null;
	private boolean isDistinct = false;
  
	/**
	 * The range of results to be returned in the query.
	 */
	private Long limit = null;
	private Long offset = null;

	/** The operators that require an iterable operand. */
	private final Operator[] iterableOperators = { Operator.IN, Operator.NIN };
	
	/**
	 * An option to determine the direction of the ordering of the result.
	 */
	public static enum Ordering {
		DESCENDING("desc"), 
		ASCENDING("asc");

		private String name;

		/**
		 * Constructor.
		 * 
		 * @param name the name of the order direction
		 */
		Ordering(String name) {
			this.name = name;
		}

		/**
		 * Returns the {@link String} representation of the ordering direction.
		 * @return the {@link String} specifying the ordering direction
		 */
		public String toString() {
			return name;
		}
	}

	/**
	 * Operators to apply on a query to provide a subset of the resource. This
	 * implementation append the operator in the square bracket in name/value
	 * pairs in the URI
	 * 
	 * <p>
	 * Example: http://host:port/application/resource?column[operator]=value
	 * </p>
	 */
	public static enum Operator {
		EQ("eq"),
		LT("lt"), 
		GT("gt"), 
		LTE("lte"), 
		GTE("gte"), 
		NE("ne"), 
		IN("in"), 
		NIN("nin"), 
		NULL("null"),
		BETWEEN("between"), 
		LIKE("like"),
		NOP("nop");
		
		private String operator;

		/**
		 * Constructor.
		 * 
		 * @param operator the name of the query operation
		 */
		Operator(String operator) {
			this.operator = operator;
		}

		/**
		 * Returns an URI query string representation of the query operator.
		 * @return URI query string representation of the query operator
		 */
		public String getOperatorForURL() {
			switch(this) {
			case EQ:
				return "";
			default:
				return "[" + operator + "]";
			}
		}

		/**
		 * Returns the string representation of the query operator.
		 * 
		 * @return {@link String} representation of the query operator
		 */
		public String toString() {
			return operator;
		}
		
		/**
		 * Creates an {@link Operator} from the given string.
		 * 
		 * @param op
		 *            the name of the query operation
		 * @return an {@link Operator} identified by the given {@code op},
		 *         {@link Operator#NOP} if the given {@code op} is not a
		 *         supported operator
		 */
		public static Operator fromString(String op) {
			if (op != null) {
				for (Operator operator : Operator.values()) {
					if (operator.toString().equalsIgnoreCase(op)) {
						return operator;
					}
				}
			}
			return NOP;
		}
		
	}

	/**
	 * System query options are query parameters a client may specify to control
	 * the amount and order of the data that aa service returns for the resource
	 * identified by the URI. The names of all System Query Options are prefixed
	 * with a "$" character.
	 * 
	 * <p>
	 * This implementation extends the OData implementation to provide support for
	 * additional functions
	 * </p>
	 */
	public static enum Option {
		TOP("$top"), 
		SKIP("$skip"), 
		ORDERBY("$orderby"), 
		DISTINCT("$distinct"), 
		GROUPBY("$groupby"), 
		EXPAND("$expand"), 
		SELECT("$select");

		private String option;

		/**
		 * Constructor.
		 * 
		 * @param option the URI query string of the query keyword
		 */
		Option(String option) {
			this.option = option;
		}

		/**
		 * Returns the URI query string representation of the query option.
		 * 
		 * @return URI query string of the query option
		 */
		public String toString() {
			return option;
		}
	}
  
	/**
	 * An internal class to build the query part of an URI.
	 */
	private final class Builder {
  	
		private final StringBuilder builder = new StringBuilder();

		private boolean first = true;

		/**
		 * Constructor.
		 */
		Builder() {}
  	
		/**
		 * Encodes the key and value and then appends the parameter to the query
		 * string.
		 * 
		 * @param key
		 *            The key name of the parameter
		 * @param value
		 *            The value of the parameter
		 */
		void appendParameter(String key, String value) {
			if (!first) {
				builder.append('&');
			}
			try {
				builder.append(URLEncoder.encode(key, "UTF-8")).append('=')
						.append(URLEncoder.encode(value, "UTF-8"));
				first = false;
			} catch (UnsupportedEncodingException ignored) {
				// Ignores
			}
		}

		@Override
		public String toString() {
			return builder.toString();
		}

	}
  
	/**
	 * Constructor.
	 */
	public Query() {}
	
	@Override
	public Query clone() {
		Query cloned = new Query();
		
		cloned.arguments.putAll(getArguments());
		cloned.orderings.putAll(getOrderings());
		cloned.selectFields = getSelectFields() == null ? null : Maps.newHashMap(getSelectFields());
		cloned.expandingFields = getExpandingFields() == null ? null : Maps.newHashMap(getExpandingFields());
		cloned.groupByColumnName = getGroupBy() == null ? null : new String(getGroupBy());
		cloned.isDistinct = isResultDistinct();
		cloned.limit = getRange().get(HIGH_RANGE);
		cloned.offset = getRange().get(LOW_RANGE);
		
		return cloned;
	}
  
	/**
	 * Returns a URI query representation of this {@link Query}.
	 * 
	 * @return an URI query representation of this {@link Query}
	 */
	@Override
	public String toString() {
		Builder builder = new Builder();

		for (String fieldName : arguments.keySet()) {
			Map<String, ?> operations = arguments.get(fieldName);
			for (String operation : operations.keySet()) {
				Operator operator = Operator.fromString(operation);
				Object value = operations.get(operation);
				builder.appendParameter(
						fieldName + operator.getOperatorForURL(),
						getArgumentValue(value));
			}
		}

		if (offset != null) {
			builder.appendParameter(Option.SKIP.toString(), offset.toString());
		}
		if (limit != null) {
			builder.appendParameter(Option.TOP.toString(), limit.toString());
		}
		if (isDistinct) {
			builder.appendParameter(Option.DISTINCT.toString(),
					Boolean.TRUE.toString());
		}
		if (groupByColumnName != null) {
			builder.appendParameter(Option.GROUPBY.toString(), groupByColumnName);
		}

		for (String columnName : orderings.keySet()) {
			builder.appendParameter(Option.ORDERBY.toString(), columnName + " "
					+ orderings.get(columnName));
		}

		List<String> expandFields = getPaths(expandingFields);
		if (expandFields != null) {
			builder.appendParameter(Option.EXPAND.toString(),
					getArgumentValue(expandFields));
		}

		List<String> selectedFields = getPaths(selectFields);
		if (selectedFields != null) {
			builder.appendParameter(Option.SELECT.toString(),
					getArgumentValue(selectedFields));
		}

		return builder.toString();
	}
  
  
	/**
	 * Returns the path identified by the given {@code pathComponents}.
	 * 
	 * @param pathComponents
	 *            The components that defines a path
	 * @return A delimited string representation of the given
	 *         {@code pathComponents}
	 */
	@SuppressWarnings("unchecked")
	private List<String> getPaths(Map<String, ?> pathComponents) {
		List<String> result = null;

		if (pathComponents != null) {
			result = new ArrayList<String>();
			for (String name : pathComponents.keySet()) {
				Object value = pathComponents.get(name);

				if (value != null) {
					if (value instanceof Map) {
						List<String> components = getPaths((Map<String, ?>) value);
						if (components.isEmpty()) {
							result.add(name);
						} else {
							for (String component : components) {
								result.add(name + FIELD_SEPARATOR + component);
							}
						}
					} else {
						result.add(name + FIELD_SEPARATOR + value.toString());
					}
				} else {
					result.add(name);
				}
			}
		}

		return result;
	}
	
	/**
	 * Returns a {@link String} representation of the given {@code value}.
	 * 
	 * @param value
	 *          The value to return the string representation
	 * @return The {@link String} representation of the given {@code value},
	 *         {@code null} if the value is {@code null}
	 */
	public static String getArgumentValue(Object value) {
		return getArgumentValue(value, true);
	}
	
	/**
	 * Returns a {@link String} representation of the given {@code value}.
	 * 
	 * @param value
	 *            The value to return the string representation
	 * @param ignoreMapKeys
	 *            {@code true} to ignore keys in Map values, {@code false}
	 *            otherwise
	 * @return The {@link String} representation of the given {@code value},
	 *         {@code null} if the value is {@code null}
	 */	
	@SuppressWarnings("unchecked")
	public static String getArgumentValue(Object value, boolean ignoreMapKeys) {
		if (value != null) {
			if (value instanceof List) {
				// Construct a comma-separated string from the list
				boolean first = true;
				List<Object> vals = (List<Object>) value;
				StringBuilder builder = new StringBuilder();

				for (Object val : vals) {
					if (!first) {
						builder.append(',');
					}
					first = false;
					builder.append(getArgumentValue(val));
				}
				return builder.toString();
			} else if (value instanceof Map) {
				Map<?, ?> vals = (Map<?, ?>) value;
				StringBuilder builder = new StringBuilder();
				boolean first = true;
				
				if (!ignoreMapKeys) {
					for (Object key : vals.keySet()) {
						Object val = vals.get(key);
						if (!first) {
							builder.append(',');
						}
						first = false;
						builder.append(getArgumentValue(key));
						builder.append(':');
						builder.append(getArgumentValue(val));
					}
				} else {					
					for (Object val : vals.values()) {
						if (!first) {
							builder.append(',');
						}
						first = false;
						builder.append(getArgumentValue(val));
					}
				}
				return builder.toString();
			} else {
				return value.toString();
			}
		}
		return null;
	}
  
	/**
	 * Returns a Map containing all options on fields in a query.
	 * 
	 * @return a hash map containing query operations on each field
	 */
	public Map<String, Map<String, ?>> getArguments() {
		return arguments;
	}
	
	/**
	 * Returns a Map containing all ordering options on fields in a query.
	 * 
	 * @return a hash map containing ordering on the fields
	 */
	public Map<String, String> getOrderings() {
		return orderings;
	}
	
	/**
	 * Returns a Map containing the range of results to in a query.
	 * 
	 * @return a hash map containing the range
	 */
	public Map<String, Long> getRange() {
		Map<String, Long> range = new HashMap<String, Long>();
		range.put(LOW_RANGE, offset);
		range.put(HIGH_RANGE, limit);
		return range;
	}

	/**
	 * Returns the selected columns in the query or an empty list if none were
	 * specified.
	 * 
	 * @return A list of selected columns in the query
	 */
	public Map<String, ?> getSelectFields() {
		if (selectFields == null) {
			return Collections.emptyMap();
		}
		return selectFields;
	}
	
	/**
	 * Returns the columns selected in the query as a list of path names.
	 * 
	 * @return A list of path names selected in this query
	 */
	public List<String> getSelectFieldPaths() {
		return getPaths(getSelectFields());
	}

	/**
	 * Returns a map containing the column names that the foreign relationships
	 * should be expanded.
	 * 
	 * @return A hash map containing the column names to be expanded
	 */
	public Map<String, ?> getExpandingFields() {
		return expandingFields;
	}
	
	/**
	 * Returns the foreign relationships that should be expanded as a list of path names.
	 * 
	 * @return A list of path names of the foreign relationships
	 */
	public List<String> getExpandingFieldPaths() {
		return getPaths(getExpandingFields());
	}
	
	/**
	 * Returns a column name to be grouped by.
	 * 
	 * @return a column name to be grouped by
	 */
	public String getGroupBy() {
		return groupByColumnName;
	}
	
	/**
	 * Returns whether or not results are distinct.
	 * 
	 * @return {@code true} if results are distinct, {@code false} otherwise
	 */
	public boolean isResultDistinct() {
		return isDistinct;
	}
	
	/**
	 * This method lets you add a "LIMIT" to the query at once. Can be used to
	 * implement pagination in your app.
	 * 
	 * @param count
	 *            the maximum number of rows to be returned
	 * @return the new query that resulted from adding this operation
	 */
	public Query limitResultsTo(Long count) {
		limit = count;
		return this;
	}
	
	/**
	 * This method lets you add a "SKIP" to the query at once. Can be used to
	 * implement pagination in your app.
	 * 
	 * @param skip
	 *            the starting row of the output
	 * @return the new query that resulted from adding this operation
	 */
	public Query offsetResultsBy(Long skip) {
		offset = skip;
		return this;
	}
	
	/**
	 * Selects the given fields in the query.
	 * 
	 * @param fields
	 *            A hash map of column names that should be selected in the
	 *            query
	 * @return The new query that resulted from adding this operation
	 */
	public Query selectFields(HashMap<String, ?> fields) {
		selectFields = fields;
		return this;
	}
	
	/**
	 * Selects the given field in this query.
	 * 
	 * @param field
	 *            The field to select
	 * @return The new query that resulted from adding this operation
	 */
	public Query selectField(String field) {
		if (field != null) {
			if (selectFields == null) {
				selectFields = new HashMap<String, Map<String, ?>>();
			}
			addPathToMap(field, selectFields);
		}
		return this;
	}

	/**
	 * Expand the given foreign fields in the query.
	 * 
	 * @param fields
	 *          A hash map of column names that the foreign objects should be
	 *          refreshed in the query
	 * @return The new query that resulted from adding this operation
	 */
	public Query expandFields(HashMap<String, ?> fields) {
		expandingFields = fields;
		return this;
	}

	/**
	 * Expand the given {@code field} in this query.
	 * 
	 * @param field
	 *          The field path that should be expanded in this query
	 * @return The new query that resulted from adding this operation
	 */
	public Query expandField(String field) {
		if (field != null) {
			if (expandingFields == null) {
				expandingFields = new HashMap<String, Map<String, ?>>();
			}
			addPathToMap(field, expandingFields);
		}
		return this;
	}
	
	/**
	 * Adds the given {@code path} to the {@link Map} containing the path components.
	 * 
	 * @param path The path to add
	 * @param map The {@link Map} caching the path components
	 */
	@SuppressWarnings("unchecked")
	protected void addPathToMap(String path, Map<String, ?> map) {
		if (map != null && path != null) {
			Map<String, ?> fieldComponents = map;
			String[] pathComponents = path.split(FIELD_SEPARATOR);
			
			for (String fieldName : pathComponents) {
				if (!fieldComponents.containsKey(fieldName)) {
					HashMap<String, HashMap<String, ?>> components = (HashMap<String, HashMap<String, ?>>) fieldComponents;
					components.put(fieldName, new HashMap<String, Map<String, ?>>());
				}
				fieldComponents = (Map<String, ?>) fieldComponents.get(fieldName);
			}
		}
	}
	
	/**
	 * Add a "GROUP BY" to your query.
	 * 
	 * @param columnNameToGroup
	 *            the column name to be grouped by
	 * @return the new query that resulted from adding this operation
	 */
	public Query resultIsGroupBy(String columnNameToGroup) {
		groupByColumnName = columnNameToGroup;
		return this;
	}
	
	/**
	 * Add a "DISTINCT" clause to the query.
	 * 
	 * @param distinct
	 *            {@code true} to return distinct results, {@code false}
	 *            otherwise
	 * @return the new query that resulted from adding this operation
	 */
	public Query resultIsDistinct(boolean distinct) {
		this.isDistinct = distinct;
		return this;
	}
	
	/**
	 * add an "ORDER BY" to your query.
	 * 
	 * @param field
	 *            the field to order by
	 * @param ordering
	 *            the ordering of that field
	 * @return the new query that resulted from adding this operation
	 */
	public Query fieldIsOrderedBy(String field, Ordering ordering) {
		orderings.put(field, ordering.toString());
		return this;
	}
  
	/**
	 * adds a "=" to the query. test whether the given field's value is equal to
	 * the given value
	 * 
	 * @param field
	 *            the field whose value to test
	 * @param val
	 *            the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsEqualTo(String field, T val) {
		if (val == null) {
			return fieldIsNull(field);
		}

		return addQueryOptionForField(field, Operator.EQ, val);
	}
	
	/**
	 * adds a "<" to the query. test whether the given field's value is less than the given value.
	 * 
	 * @param <T> the Java type of the value
	 * @param field the field whose value to test
	 * @param val the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsLessThan(String field, T val) {
		return addQueryOptionForField(field, Operator.LT, val);
	}

	/**
	 * adds a ">" to the query. test whether the given field's value is greater
	 * than the given value
	 * 
	 * @param field
	 *            the field whose value to test
	 * @param val
	 *            the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsGreaterThan(String field, T val) {
		return addQueryOptionForField(field, Operator.GT, val);
	}
	
	/**
	 * same as {@link #fieldIsLessThan(String, Object)}, except applies "<=" instead of "<".
	 * 
	 * @param <T> the Java type of the value
	 * @param field the field whose value to test
	 * @param val the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsLessThanEqualTo(String field, T val) {
		return addQueryOptionForField(field, Operator.LTE, val);
	}

	/**
	 * same as {@link #fieldIsGreaterThan(String, Object)}, except applies ">="
	 * instead of ">".
	 * 
	 * @param field
	 *            the field whose value to test
	 * @param val
	 *            the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsGreaterThanEqualTo(String field, T val) {
		return addQueryOptionForField(field, Operator.GTE, val);
	}

	/**
	 * adds a "<>" to the query. test whether the given field's value is not
	 * equal to the given value
	 * 
	 * @param <T>
	 * @param field
	 *            the field whose value to test
	 * @param val
	 *            the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsNotEqualTo(String field, T val) {
		if (val == null) {
			return fieldIsNotNull(field);
		}
		return addQueryOptionForField(field, Operator.NE, val);
	}

	/**
	 * add an "IN" to your query. test whether the given field's value is in the
	 * given list of possible values
	 * 
	 * @param field
	 *            the field whose value to test
	 * @param values
	 *            the values against which to match
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsIn(String field, List<T> values) {
		return addQueryOptionForField(field, Operator.IN, values);
	}

	/**
	 * add a "NOT IN' to the query. test whether the given field's value is not
	 * in the given list of possible values
	 * 
	 * @param <T>
	 * @param field
	 *            the field whose value to test
	 * @param values
	 *            the values again which to match
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsNotIn(String field, List<T> values) {
		return addQueryOptionForField(field, Operator.NIN, values);
	}

	/**
	 * add a "NULL" to your query. test whether the given field's value is null
	 * 
	 * @param field
	 *            the field whose value to test
	 * @return the new query that resulted from adding this operation
	 */
	public Query fieldIsNull(String field) {
		return addQueryOptionForField(field, Operator.NULL, Boolean.TRUE);
	}

	/**
	 * add a "NULL" to your query. test whether the given field's value is not
	 * null
	 * 
	 * @param field
	 *            the field whose value to test
	 * @return the new query that resulted from adding this operation
	 */
	public Query fieldIsNotNull(String field) {
		return addQueryOptionForField(field, Operator.NULL, Boolean.FALSE);
	}

	/**
	 * add a "BETWEEN" to the query. test whether the given field's value is in
	 * between the given range
	 * 
	 * @param <T>
	 * @param field
	 *            the field whose value to test
	 * @param low
	 *            the low end of the range
	 * @param high
	 *            the high end of the range
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsBetween(String field, T low, T high) {
		Map<String, T> val = new HashMap<String, T>();
		val.put(LOW_RANGE, low);
		val.put(HIGH_RANGE, high);
		return addQueryOptionForField(field, Operator.BETWEEN, val);
	}

	/**
	 * add a "LIKE" to the query. test whether the given field's value matches
	 * the given value using the '%' pattern
	 * 
	 * @param <T>
	 * @param field
	 *            the field whose value to test
	 * @param val
	 *            the value against which to test
	 * @return the new query that resulted from adding this operation
	 */
	public <T> Query fieldIsLike(String field, T val) {
		return addQueryOptionForField(field, Operator.LIKE, val);
	}
  
	@SuppressWarnings("unchecked")
	private Query addQueryOptionForField(String field, Operator operator, Object value) {
		Map<String, Object> operation = (Map<String, Object>) arguments.get(field);
		if (operation == null) {
			operation = new HashMap<String, Object>();
			arguments.put(field, operation);
		}

		// To support querying multiple values to the same field with the same
		// operator, value in the query is always added as a List 
		Object val = operation.get(operator.toString());
		if (val == null) {
			val = new ArrayList<Object>();
		}
		if (val instanceof List) {
			List<Object> vals = (List<Object>) val;
			
			if (Iterables.isIterable(value) && !Arrays.asList(iterableOperators).contains(operator)) {
				vals.addAll(Arrays.asList(Iterables.asArray(value)));
			} else {
				vals.add(value);
			}
		}
		operation.put(operator.toString(), val);
		
		return this;
	}

}
