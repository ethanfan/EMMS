/**
 * ForeignCollectionTypeAdapter.java
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
 * The type adapter for ORMLite {@link ForeignCollection} objects in GSON serialization/deserialization.
 * 
 * @param <T> the type of object in the {@link ForeignCollection}
 * @author Stanley Lam
 */
public abstract class ForeignCollectionTypeAdapter<T> extends DaoTypeAdapter<T> {

	abstract void setDeserializationContext(ForeignCollectionDeserializationContext context);
}
