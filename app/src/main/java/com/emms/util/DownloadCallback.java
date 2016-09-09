package com.emms.util;

/**
 * Define listener interfaces you can implement to be notified after an
 * operation is performed.
 * 
 * @author joyaether
 * 
 */
public interface DownloadCallback {
	/**
	 * Called when an operation is performed successfully.
	 */
	void success(boolean hasUpdate);

	/**
	 * Called if there were error performing the operation.
	 * 
	 * @param e
	 *            The exception caused
	 */
	void fail(Exception e);

}
