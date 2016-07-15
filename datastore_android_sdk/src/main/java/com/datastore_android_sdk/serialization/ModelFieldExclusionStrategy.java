/**
 * ModelFieldExclusionStrategy.java
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

import java.util.ArrayList;
import java.util.List;

/**
 * The exclusion strategy to use while de/serializing a data model.
 * 
 * @author Stanley Lam
 */
public class ModelFieldExclusionStrategy extends FieldExclusionStrategy {
	
	private final List<String> selectedFields;
	
	public ModelFieldExclusionStrategy(List<String> selectedFields) {
		this.selectedFields = selectedFields;
	}
	
	public ModelFieldExclusionStrategy(Iterable<String> selectedFields) {
		this.selectedFields = new ArrayList<String>();
		for (String field : selectedFields) {
			this.selectedFields.add(field);
		}
	}
	
	/* (non-Javadoc)
	 * @see FieldExclusionStrategy#shouldSkipField(String)
	 */
	@Override
	public boolean shouldSkipField(String serializedName) {
		// Exclude a field if it was not one of the selected fields
		return selectedFields.isEmpty() ? false : !selectedFields.contains(serializedName);
	}

}
