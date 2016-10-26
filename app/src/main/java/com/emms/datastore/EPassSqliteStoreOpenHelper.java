package com.emms.datastore;

import android.content.Context;
import android.util.Log;

import com.emms.schema.AlertRule;
import com.emms.schema.BaseOrganise;
import com.emms.schema.DataDictionary;
import com.emms.schema.DataRelation;
import com.emms.schema.DataType;
import com.emms.schema.Department;
import com.emms.schema.Equipment;
import com.emms.schema.Factory;
import com.emms.schema.Maintain;
import com.emms.schema.Operator;
import com.emms.schema.TaskOrganiseRelation;
import com.emms.schema.Team;
import com.emms.schema.TeamService;
import com.emms.util.SharedPreferenceManager;
import com.j256.ormlite.support.ConnectionSource;
import com.datastore_android_sdk.schema.Model;
import com.datastore_android_sdk.schema.Schema;
import com.datastore_android_sdk.sqlite.SqliteStoreHelper;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Defines the interfaces that a helper must implement to be notified when a
 * SQLite store is created or upgraded.
 *
 * 
 */
public class EPassSqliteStoreOpenHelper extends SqliteStoreHelper {
	// define the constant of table name
	public static final String SCHEMA_OPERATOR = Schema.getAttributes(
			Operator.class).getTableName();
	public static final String SCHEMA_DEPARTMENT = Schema.getAttributes(
			Department.class).getTableName();
	public static final String SCHEMA_ALERTRULE = Schema.getAttributes(
			AlertRule.class).getTableName();
	public static final String SCHEMA_DATADICTIONARY = Schema.getAttributes(
			DataDictionary.class).getTableName();
	public static final String SCHEMA_DATATYPE = Schema.getAttributes(
			DataType.class).getTableName();
	public static final String SCHEMA_TEAMSERVICE = Schema.getAttributes(
			TeamService.class).getTableName();
	public static final String SCHEMA_TEAM = Schema.getAttributes(
			Team.class).getTableName();
	public static final String SCHEMA_FACTORY = Schema.getAttributes(Factory.class)
			.getTableName();
	public static final String SCHEMA_EQUIPMENT = Schema.getAttributes(
			Equipment.class).getTableName();
	public static final String SCHEMA_MAINTAIN = Schema.getAttributes(
			Maintain.class).getTableName();
	public static final String SCHEMA_BASE_ORGANISE = Schema.getAttributes(
			BaseOrganise.class).getTableName();
	public static final String SCHEMA_TASK_ORGANISE_RELATION=Schema.getAttributes(
			TaskOrganiseRelation.class).getTableName();
	public static final String SCHEMA_DATA_RELATION=Schema.getAttributes(
			DataRelation.class).getTableName();


	/**
	 * A HashMap store a key pair a schema object class.
	 */
	private final Map<String, Class<? extends Model<?, ?>>> schema = new ConcurrentHashMap<String, Class<? extends Model<?, ?>>>() {
		private static final long serialVersionUID = 9084349657357243355L;
	};

	public EPassSqliteStoreOpenHelper(Context context) {
		super(context);
		setTables();
	}

	@Override
	public String getDatabaseName() {
		return new File(getContext().getExternalFilesDir(null), "EMMS.db")
					.getAbsolutePath();

	}

//	@Override
//	public String getDatabaseName() {
//		String region = EsquelPassRegion.getDefault(getContext()).toString();
//
//		return new File(getContext().getExternalFilesDir(null), region + ".db")
//				.getAbsolutePath();
//	}

	@Override
	public int getVersion() {
		return 1000;
	}

	@Override
	public Class<? extends Model<?, ?>> getSchema(String name) {
		return schema.get(name);
	}

	@Override
	public void onCreate(ConnectionSource connectionSource) {
//		try {
//			//create the Application Download State table for first time open the app.
//			TableUtils.createTableIfNotExists(connectionSource, ApplicationDownloadState.class);
//		} catch (Exception ex) {
//			Log.e("onCreate", ex.getMessage());
//
//		}
//			Collection<Class<? extends Model<?, ?>>> classes = schema.values();
//			for (Class<?> clazz : classes) {
//				try {
//					TableUtils.createTableIfNotExists(connectionSource, clazz);
//				} catch (Exception ex) {
//					Log.e("onCreate", ex.getMessage());
//
//				}
//			}
		
	}

	@Override
	public void onUpgrade(ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		Log.d("old version", oldVersion + "");
		Log.d("new version", newVersion + "");

		// Do nothing
	}

	private void setTables() {
		schema.put(SCHEMA_OPERATOR, Operator.class);
		schema.put(SCHEMA_DEPARTMENT, Department.class);
		schema.put(SCHEMA_ALERTRULE, AlertRule.class);
		schema.put(SCHEMA_DATADICTIONARY, DataDictionary.class);
		schema.put(SCHEMA_DATATYPE, DataType.class);
		schema.put(SCHEMA_TEAMSERVICE, TeamService.class);
		schema.put(SCHEMA_TEAM, Team.class);
		schema.put(SCHEMA_FACTORY, Factory.class);
		schema.put(SCHEMA_EQUIPMENT, Equipment.class);
		schema.put(SCHEMA_MAINTAIN,Maintain.class);
		schema.put(SCHEMA_BASE_ORGANISE,BaseOrganise.class);
		schema.put(SCHEMA_TASK_ORGANISE_RELATION,TaskOrganiseRelation.class);
		schema.put(SCHEMA_DATA_RELATION,DataRelation.class);
	}

}
