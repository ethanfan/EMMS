package com.emms.datastore;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Region;
import android.util.Log;


import com.emms.schema.ApplicationDownloadState;
import com.emms.schema.Operator;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.jaffer_datastore_android_sdk.schema.Model;
import com.jaffer_datastore_android_sdk.schema.Schema;
import com.jaffer_datastore_android_sdk.sqlite.SqliteStoreHelper;

import org.restlet.security.User;


/**
 * Defines the interfaces that a helper must implement to be notified when a
 * SQLite store is created or upgraded.
 *
 * 
 */
public class EPassSqliteStoreOpenHelper extends SqliteStoreHelper {
	// define the constant of table name
	public static final String SCHEMA_ARTICLE = Schema.getAttributes(
			Operator.class).getTableName();
//	public static final String SCHEMA_ARTICLE_CONTENT = Schema.getAttributes(
//			ArticleContent.class).getTableName();
//	public static final String SCHEMA_CATEGORY = Schema.getAttributes(
//			Category.class).getTableName();
//	public static final String SCHEMA_CATEGORY_CHANNEL = Schema.getAttributes(
//			CategoryChannel.class).getTableName();
//	public static final String SCHEMA_CHANNEL = Schema.getAttributes(
//			Channel.class).getTableName();
//	public static final String SCHEMA_CHANNEL_ARTICLE = Schema.getAttributes(
//			ChannelArticle.class).getTableName();
//	public static final String SCHEMA_LAST_SYNC_DATE = Schema.getAttributes(
//			LastSynchronizeDate.class).getTableName();
//	public static final String SCHEMA_USER = Schema.getAttributes(User.class)
//			.getTableName();
//	public static final String SCHEMA_USER_CHANNEL = Schema.getAttributes(
//			UserChannel.class).getTableName();
//	public static final String SCHEMA_LIKE = Schema.getAttributes(Like.class)
//			.getTableName();
//	public static final String SCHEMA_APPLICATION_DOWNLOAD_STATE = Schema.getAttributes(ApplicationDownloadState.class)
//			.getTableName();
//	public static final String SCHEMA_APPLICATION = Schema.getAttributes(Application.class)
//			.getTableName();
//	public static final String SCHEMA_REGION = Schema.getAttributes(Region.class).getTableName();
//	public static final String SCHEMA_ARTICLE_REGION = Schema.getAttributes(ArticleRegion.class).getTableName();

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
		return new File(getContext().getExternalFilesDir(null), "test4.db")
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
		schema.put(SCHEMA_ARTICLE, Operator.class);
//		schema.put(SCHEMA_ARTICLE_CONTENT, ArticleContent.class);
//		schema.put(SCHEMA_CATEGORY, Category.class);
//		schema.put(SCHEMA_CATEGORY_CHANNEL, CategoryChannel.class);
//		schema.put(SCHEMA_CHANNEL, Channel.class);
//		schema.put(SCHEMA_CHANNEL_ARTICLE, ChannelArticle.class);
//		schema.put(SCHEMA_APPLICATION_DOWNLOAD_STATE, ApplicationDownloadState.class);
//		schema.put(SCHEMA_APPLICATION, Application.class);
//		schema.put(SCHEMA_REGION, Region.class);
//		schema.put(SCHEMA_ARTICLE_REGION, ArticleRegion.class);
//		schema.put(SCHEMA_LAST_SYNC_DATE, LastSynchronizeDate.class);
//		schema.put(SCHEMA_LIKE, Like.class);
//		schema.put(SCHEMA_USER, User.class);
//		schema.put(SCHEMA_USER_CHANNEL, UserChannel.class);
	}

}
