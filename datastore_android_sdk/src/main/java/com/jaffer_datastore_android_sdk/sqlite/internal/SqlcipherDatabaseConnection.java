/**
 * SqlcipherDatabaseConnection.java
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
package com.jaffer_datastore_android_sdk.sqlite.internal;

import java.sql.SQLException;
import java.sql.Savepoint;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;
import android.database.Cursor;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.GeneratedKeyHolder;
/**
 * Ciphered SQLite database connection on Android. This class is the same as
 * {@link com.j256.ormlite.android.AndroidDatabaseConnection} except that the
 * underlying database is a {@link SQLiteDatabase}
 */
public class SqlcipherDatabaseConnection implements DatabaseConnection {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlcipherDatabaseConnection.class);
	private static final String[] NO_STRING_ARGS = new String[0];
	
	/** The underlying SQLite database. */
	private final SQLiteDatabase db;
	private final boolean readWrite;
	private final boolean cancelQueriesEnabled;
	
	/**
	 * Creates a new instance of {@link SqlcipherDatabaseConnection} to a {@link SQLiteDatabase}.
	 * 
	 * @param db The database to create a connection with
	 * @param readWrite {@code true} if the connection is read and write, {@code false} otherwise
	 */
	public SqlcipherDatabaseConnection(SQLiteDatabase db, boolean readWrite, boolean cancelQueriesEnabled) {
		this.db = db;
		this.readWrite = readWrite;
		this.cancelQueriesEnabled = cancelQueriesEnabled;
		LOGGER.trace("{}: db {} opened, read-write = {}", this, db, readWrite);
	}
	
	/**
	 * Returns the database this connection establishes with. 
	 * 
	 * @return The database this connection established with
	 */
	protected SQLiteDatabase getDatabase() {
		return db;
	}

	@Override
	public boolean isAutoCommitSupported() {
		return true;
	}
	
	@Override
	public boolean isAutoCommit() throws SQLException {
		try {
			boolean inTransaction = getDatabase().inTransaction();
			LOGGER.trace("{}: in transaction is {}", this, inTransaction);
			// You have to explicitly commit your transactions, so this is sort of correct
			return !inTransaction;
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("Problems getting auto-commit from database", e);
		}
	}
	
	@Override
	public void setAutoCommit(boolean autoCommit) {
		/*
		 * Sqlite does not support auto-commit. The various JDBC drivers seem to implement it with the use of a
		 * transaction. That's what we are doing here.
		 */
		if (autoCommit) {
			if (getDatabase().inTransaction()) {
				getDatabase().setTransactionSuccessful();
				getDatabase().endTransaction();
			}
		} else {
			if (!getDatabase().inTransaction()) {
				getDatabase().beginTransaction();
			}
		}
	}
	
	@Override
	public Savepoint setSavePoint(String name) throws SQLException {
		try {
			getDatabase().beginTransaction();
			LOGGER.trace("{}: save-point set with name {}", this, name);
			return new OurSavePoint(name);
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("problems beginning transaction " + name, e);
		}
	}

	/**
	 * Return whether this connection is read-write or not (real-only).
	 */
	public boolean isReadWrite() {
		return readWrite;
	}
	
	@Override
	public void commit(Savepoint savepoint) throws SQLException {
		try {
			getDatabase().setTransactionSuccessful();
			getDatabase().endTransaction();
			if (savepoint == null) {
				LOGGER.trace("{}: transaction is successfuly ended", this);
			} else {
				LOGGER.trace("{}: transaction {} is successfuly ended", this, savepoint.getSavepointName());
			}
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("problems commiting transaction " + savepoint.getSavepointName(), e);
		}
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		try {
			// no setTransactionSuccessful() means it is a rollback
			getDatabase().endTransaction();
			if (savepoint == null) {
				LOGGER.trace("{}: transaction is ended, unsuccessfuly", this);
			} else {
				LOGGER.trace("{}: transaction {} is ended, unsuccessfuly", this, savepoint.getSavepointName());
			}
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("problems rolling back transaction " + savepoint.getSavepointName(), e);
		}
	}

	@Override
	public int executeStatement(String statementStr, int resultFlags) throws SQLException {
		return SqlcipherCompiledStatement.execSql(db, statementStr, statementStr, NO_STRING_ARGS);
	}
	
	public CompiledStatement compileStatement(String statement, StatementType type, FieldType[] argFieldTypes) {
		// resultFlags argument is not used in Android-land since the {@link Cursor} is bi-directional.
		CompiledStatement stmt = new SqlcipherCompiledStatement(statement, db, type, cancelQueriesEnabled);
		LOGGER.trace("{}: compiled statement got {}: {}", this, stmt, statement);
		return stmt;
	}

	@Override
	public CompiledStatement compileStatement(String statement, StatementType type, FieldType[] argFieldTypes, int resultFlags) {
		// resultFlags argument is not used in Android-land since the {@link Cursor} is bi-directional.
		return compileStatement(statement, type, argFieldTypes);
	}
	
	@Override
	public int insert(String statement, Object[] args, FieldType[] argFieldTypes, GeneratedKeyHolder keyHolder) throws SQLException {
		SQLiteStatement stmt = null;
		try {
			stmt = getDatabase().compileStatement(statement);
			bindArgs(stmt, args, argFieldTypes);
			long rowId = stmt.executeInsert();
			if (keyHolder != null) {
				keyHolder.addKey(rowId);
			}
			/*
			 * I've decided to not do the CHANGES() statement here like we do down
			 * below in UPDATE because we know that it worked (since it didn't throw)
			 * so we know that 1 is right.
			 */
			int result = 1;
			LOGGER.trace("{}: insert statement is compiled and executed, changed {}: {}", this, result, statement);
			return result;
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("inserting to database failed: " + statement, e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	@Override
	public int update(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
		return update(statement, args, argFieldTypes, "updated");
	}

	@Override
	public int delete(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
		// delete is the same as update
		return update(statement, args, argFieldTypes, "deleted");
	}
	
	@Override
	public <T> Object queryForOne(String statement, Object[] args, FieldType[] argFieldTypes, GenericRowMapper<T> rowMapper, ObjectCache objectCache) throws SQLException {
		Cursor cursor = null;
		try {
			cursor = getDatabase().rawQuery(statement, toStrings(args));
			AndroidDatabaseResults results = new AndroidDatabaseResults(cursor, objectCache);
			LOGGER.trace("{}: queried for one result: {}", this, statement);
			if (!results.first()) {
				return null;
			} else {
				T first = rowMapper.mapRow(results);
				if (results.next()) {
					return MORE_THAN_ONE;
				} else {
					return first;
				}
			}
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("queryForOne from database failed: " + statement, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	@Override
	public long queryForLong(String statement) throws SQLException {
		SQLiteStatement stmt = null;
		try {
			stmt = getDatabase().compileStatement(statement);
			long result = stmt.simpleQueryForLong();
			LOGGER.trace("{}: query for long simple query returned {}: {}", this, result, statement);
			return result;
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	@Override
	public long queryForLong(String statement, Object[] args, FieldType[] argFieldTypes) throws SQLException {
		Cursor cursor = null;
		try {
			cursor = getDatabase().rawQuery(statement, toStrings(args));
			AndroidDatabaseResults results = new AndroidDatabaseResults(cursor, null);
			long result;
			if (results.first()) {
				result = results.getLong(0);
			} else {
				result = 0L;
			}
			LOGGER.trace("{}: query for long raw query returned {}: {}", this, result, statement);
			return result;
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("queryForLong from database failed: " + statement, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public void close() throws SQLException {
		getDatabase().close();
		LOGGER.trace("{}: db {} closed", this, db);
	}

	@Override
	public void closeQuietly() {
		try {
			close();
		} catch (SQLException e) {
			// ignored
		}
	}
	
	@Override
	public boolean isClosed() throws SQLException {
		try {
			boolean isOpen = getDatabase().isOpen();
			LOGGER.trace("{}: db {} isOpen returned {}", this, db, isOpen);
			return !isOpen;
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("problems detecting if the database is closed", e);
		}
	}

	@Override
	public boolean isTableExists(String tableName) {
		Cursor cursor =
				getDatabase().rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
		try {
			boolean result;
			if (cursor != null && cursor.getCount() > 0) {
				result = true;
			} else {
				result = false;
			}
			LOGGER.trace("{}: isTableExists '{}' returned {}", this, tableName, result);
			return result;
		} finally {
			cursor.close();
		}
	}

	private int update(String statement, Object[] args, FieldType[] argFieldTypes, String label) throws SQLException {
		SQLiteStatement stmt = null;
		try {
			stmt = getDatabase().compileStatement(statement);
			bindArgs(stmt, args, argFieldTypes);
			stmt.execute();
		} catch (android.database.SQLException e) {
			throw SqlExceptionUtil.create("updating database failed: " + statement, e);
		} finally {
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		}
		int result;
		try {
			stmt = getDatabase().compileStatement("SELECT CHANGES()");
			result = (int) stmt.simpleQueryForLong();
		} catch (android.database.SQLException e) {
			// ignore the exception and just return 1
			result = 1;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		LOGGER.trace("{} statement is compiled and executed, changed {}: {}", label, result, statement);
		return result;
	}
	
	private void bindArgs(SQLiteStatement stmt, Object[] args, FieldType[] argFieldTypes) throws SQLException {
		if (args == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg == null) {
				stmt.bindNull(i + 1);
			} else {
				SqlType sqlType = argFieldTypes[i].getSqlType();
				switch (sqlType) {
					case STRING :
					case LONG_STRING :
					case CHAR :
						stmt.bindString(i + 1, arg.toString());
						break;
					case BOOLEAN :
					case BYTE :
					case SHORT :
					case INTEGER :
					case LONG :
						stmt.bindLong(i + 1, ((Number) arg).longValue());
						break;
					case FLOAT :
					case DOUBLE :
						stmt.bindDouble(i + 1, ((Number) arg).doubleValue());
						break;
					case BYTE_ARRAY :
					case SERIALIZABLE :
						stmt.bindBlob(i + 1, (byte[]) arg);
						break;
					case DATE :
						// this is mapped to a STRING under Android
					case BLOB :
						// this is only for derby serializable
					case BIG_DECIMAL :
						// this should be handled as a STRING
						throw new SQLException("Invalid Android type: " + sqlType);
					case UNKNOWN :
					default :
						throw new SQLException("Unknown sql argument type: " + sqlType);
				}
			}
		}
	}
	
	private String[] toStrings(Object[] args) {
		if (args == null || args.length == 0) {
			return null;
		}
		String[] strings = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg == null) {
				strings[i] = null;
			} else {
				strings[i] = arg.toString();
			}
		}

		return strings;
	}
	
	/**
	 * An internal class to store a save point.
	 */
	private static class OurSavePoint implements Savepoint {

		private final String name;

		public OurSavePoint(String name) {
			this.name = name;
		}

		public int getSavepointId() {
			return 0;
		}

		public String getSavepointName() {
			return name;
		}
	}

}
