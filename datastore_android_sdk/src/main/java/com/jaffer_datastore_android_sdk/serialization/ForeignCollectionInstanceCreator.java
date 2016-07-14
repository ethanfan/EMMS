/**
 * ForeignCollectionInstanceCreator.java
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
import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;
import com.jaffer_datastore_android_sdk.schema.PassiveForeignCollection;

/**
 * Creates instances of {@link ForeignCollection} that does not maintain active
 * connection to the database. It only establishes connection to the database
 * when either {@link PassiveForeignCollection#updateAll()} or
 * {@link PassiveForeignCollection#updateAll(Dao)} is called.
 */
public class ForeignCollectionInstanceCreator {
	
	/** The underlying {@link ConncectionSource} the {@link ForeignCollection} uses. */
	private final ConnectionSource connectionSource;
	
	public ForeignCollectionInstanceCreator(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ForeignCollection<?> createInstance(
			Type type, 
			Object parent, 
			String columnName, 
			String orderColumnName, 
			boolean orderAscending) {
		
		try {
			Dao<?, ?> dao = (Dao<?, ?>) DaoManager.createDao(connectionSource, (Class<?>) type);
			FieldType fieldType = dao.findForeignFieldType((Class<?>) parent.getClass());
			return new PassiveForeignCollection(dao, parent, null, fieldType, orderColumnName, orderAscending);
		} catch (SQLException ex) {
			return null;
		}
	}

}
