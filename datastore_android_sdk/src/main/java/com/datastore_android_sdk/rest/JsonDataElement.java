/**
 * JsonDataElement.java
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
package com.datastore_android_sdk.rest;

import com.google.gson.JsonElement;

/**
 * Defines the interfaces a JSON data must implement.
 */
interface JsonDataElement {
	
	/**
	 * Returns the underlying data object this element represents.
	 * 
	 * @return The underlying {@link JsonElement} this object represents
	 */
	JsonElement getData();

}
