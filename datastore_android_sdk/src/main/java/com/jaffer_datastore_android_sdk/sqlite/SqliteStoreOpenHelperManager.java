/**
 * Adopted from ORMLite
 * 
 * Licensed under the agreement as set out in the License for ORMLite 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaffer_datastore_android_sdk.sqlite;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;

/**
 * This class is the same as {@link com.j256.ormlite.android.apptools.OpenHelperManager} 
 * except that it allows an application to use multiple {@link OrmLiteSqliteOpenHelper}.
 */
public final class SqliteStoreOpenHelperManager {
	
	/** The {@link Logger} to log debug messages. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SqliteStoreOpenHelperManager.class);
	
	/** A {@link Map} of {@link OpenHelperInstance} cached. */
	private static Map<Class<?>, OpenHelperInstance> helperMap = null;
	
	/**
	 * Lookup a OpenHelper instance from cache.
	 * 
	 * @param clazz The class of the OpenHelper
	 */
	private static synchronized OpenHelperInstance lookupInstance(Class<? extends SqliteStoreHelper> clazz) {
		if (helperMap == null) {
			helperMap = new HashMap<Class<?>, OpenHelperInstance>();
		}
		
		OpenHelperInstance instance = helperMap.get(clazz);
		if (instance == null) {
			return null;
		} else {
			return instance;
		}
	}
	
	/**
	 * Adds the given open helper instance of cache.
	 * 
	 * @param clazz the helper class
	 * @param instance the helper instance
	 */
	private static synchronized void addHelperToMap(Class<? extends SqliteStoreHelper> clazz, OpenHelperInstance instance) {
		if (helperMap == null) {
			helperMap = new HashMap<Class<?>, OpenHelperInstance>();
		}
		
		if (instance != null && clazz != null) {
			helperMap.put(clazz, instance);
		}
	}
	
	/**
	 * Removes cached instance of the given type from cache.
	 * 
	 * @param clazz The class of the instance to remove
	 */
	private static synchronized void removeHelperFromMap(Class<? extends SqliteStoreHelper> clazz) {
		if (helperMap != null) {
			helperMap.remove(clazz);
		}
	}
	
	/**
	 * Creates an instance of the open helper from the helper class. This has a usage counter on it so make sure
	 * all calls to this method have an associated call to {@link #releaseHelper()}. This should be called during an
	 * onCreate() type of method when the application or service is starting. The caller should then keep the helper
	 * around until it is shutting down when {@link #releaseHelper()} should be called.
	 * 
	 * @param <T> The helper class
	 * @param context the context at which the open helper runs in
	 * @param clazz the helper class
	 * @return  the open helper instance
	 */
	public static synchronized DatabaseOpenHelper getHelper(Context context, Class<? extends SqliteStoreHelper> clazz) {
		OpenHelperInstance instance = lookupInstance(clazz);
		if (instance == null) {
			instance = new OpenHelperInstance(clazz);
		}
		
		if (instance != null) {
			addHelperToMap(clazz, instance);
		}
		
		return instance.load(context);
	}
	
	/**
	 * Release the helper that was previously loaded by calling {@link #getHelper(Context, Class)}.
	 * This will decrement the reference count, close the helper and remove from cache if the 
	 * reference count reached 0 
	 * 
	 * @param clazz The class of the helper to release
	 */
	public static synchronized void releaseHelper(Class<? extends SqliteStoreHelper> clazz) {
		OpenHelperInstance instance = lookupInstance(clazz);
		if (instance != null) {
			// Release an instance and remove from cache if the number of 
			// references to the instance reached 0
			instance.release();
			if (instance.wasClosed) {
				removeHelperFromMap(clazz);
			}
		}
	}
	
	/**
	 * The cached OtemLiteSqliteOpenHelper instance.
	 */
	private static final class OpenHelperInstance {
		
		private int instanceCount = 0;
		private boolean wasClosed = false;
		private DatabaseOpenHelper helper = null;
		
		private final Class<? extends SqliteStoreHelper> helperClass;
		
		OpenHelperInstance(Class<? extends SqliteStoreHelper> clazz) {
			helperClass = clazz;
		}
		
		/**
		 * Construct the SqliteOpenHelper instance.
		 * 
		 * @param context the context at which the instance should be constructed
		 * @return The constructed instance
		 */
		private SqliteStoreHelper construct(Context context) {
			// Finds constructor for the SqliteStoreHelper class
			Constructor<?> constructor;
			try {
				constructor = helperClass.getConstructor(Context.class);
			} catch (Exception e) {
				throw new IllegalStateException(
						"Could not find constructor that hast just a (Context) argument for helper class "
								+ helperClass, e);
			}
			
			try {
				return (SqliteStoreHelper) constructor.newInstance(context);
			} catch (Exception e) {
				throw new IllegalStateException("Could not construct instance of helper class " + helperClass, e);
			}
		}
		
		synchronized void release() {
			instanceCount--;
			LOGGER.trace("Releasing helper {}, instance count = {}", helper, instanceCount);
			if (instanceCount <= 0) {
				if (helper != null) {
					LOGGER.trace("No instance referenced, closing helper {}", helper);
					helper.close();
					helper = null;
					wasClosed = true;
				}
				
				if (instanceCount < 0) {
					LOGGER.error("Too many calls to release helper, instance count = {}", instanceCount);
				}
			}
		}
		
		/**
		 * Loads the helper.
		 * 
		 * @param <T> The OrmLiteSqliteOpenHelper class
		 * @param context the context at which the instance runs in. 
		 * @return the OrmLiteSqliteOpenHelper instance
		 */
		synchronized DatabaseOpenHelper load(Context context) {
			if (helper == null) {
				if (wasClosed) {
					// This can happen if you are calling get/release and then get again
					LOGGER.info("Helper was already closed and is being re-opened");
				}
				
				if (context == null) {
					throw new IllegalArgumentException("Context argument is null");
				}
				
				Context appContext = context.getApplicationContext();
				SqliteStoreHelper storeHelper = construct(appContext);
				if (storeHelper.getPassword() == null) {
					helper = new SqliteDatabaseOpenHelper(context, storeHelper); 
				} else {
					helper = new SqlcipherDatabaseOpenHelper(context, storeHelper);
				}
				LOGGER.trace("No instance, created helper {}", helper);
				
				// Clears all cached DAO objects so that no object is cached against a
				// closed connection 
				BaseDaoImpl.clearAllInternalObjectCaches();
				DaoManager.clearDaoCache();
				instanceCount = 0;
			}
			
			instanceCount++;
			
			LOGGER.trace("Returning helper {}, instance count = {} ", helper, instanceCount);
			return helper;
		}
	}
	
	/** Prevent this class from being instantiated. */
	private SqliteStoreOpenHelperManager() {}

}
