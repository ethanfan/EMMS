/**
 * ForeignCollectionDeserializationContext.java
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

import com.j256.ormlite.dao.ForeignCollection;

/**
 * Context for deserialization of {@link ForeignCollection} that is passed to an adapter.
 * 
 * @author Stanley Lam
 */
public interface ForeignCollectionDeserializationContext {
	
	/**
	 * Returns the parent object of the foreign collection.
	 */
	Object getParent();
	
	/**
	 * Returns the name of the of the column.
	 * @return A string of the name of the column 
	 */
	String getColumnName();
	
	/**
	 * The name of the column in the object that we should order by.
	 * @return A string containing the name of the column in the object
	 * that the result set should be ordered by
	 */
	String getOrderColumnName();
	
	/**
	 * Specifies whether the order should be ascending (the default) or descending.
	 * @return true if order should be ascending, false otherwise
	 */
	boolean getOrderAscending();

}
