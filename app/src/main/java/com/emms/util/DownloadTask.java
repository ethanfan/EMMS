package com.emms.util;

import android.content.Context;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * This class is handling to download the file.
 * 
 * @author joyaether
 */
public class DownloadTask {

	private static final int BUFFER_SIZE = 1024;

	private Context context;
	private Reference sourceRef;
	private Reference desRef;
	private DownloadCallback mCallback;
	private boolean isRunning = false;

	/** The executor service to spawn threads for downloading remote resources. */
	private ExecutorService mExecutor;

	public DownloadTask(Context context, Reference source, Reference destination) {
		setContext(context);
		setSourceReference(source);
		setDestinationReference(destination);

		mExecutor = Executors.newCachedThreadPool();
	}

	/**
	 * Perform the download operation.
	 */
	public void download() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				File file = null;
				try {
					setRunning(true);
					ClientResource client = new ClientResource(
							getSourceReference());
					Representation result = client.get(MediaType.ALL);

					file = new File(DataUtil.getDBDirPath(getContext()),
							getDestinationReference().getLastSegment());
					saveFile(result.getStream(), file.getAbsolutePath());
				} catch (Exception e) {
					setRunning(false);
					if (file != null && file.exists()) {
						file.delete();
					}
					if (getCallback() != null) {
						getCallback().fail(e);
					}
					return;
				}
				setRunning(false);
				if (getCallback() != null) {
					getCallback().success(true);
				}
			}

		};

		mExecutor.submit(runnable);
	}

	/**
	 *
	 * @param in inputStream
	 * @param desPath String
	 * @return boolean
	 * @throws IOException
	 */
	private boolean saveFile(InputStream in, String desPath) throws IOException {
		File folder = new File(DataUtil.getDBDirPath(getContext()), "");
		File file = new File(desPath);
		if (!folder.exists()) {
			folder.mkdirs();
			file.createNewFile();
		}

		OutputStream out = null;
		File targetFile = new File(desPath);
		if (!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs();
		}
		out = new FileOutputStream(targetFile);
		byte[] buffer = new byte[BUFFER_SIZE];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		out.close();
		return true;
	}

	/**
	 * @return the sourceRef
	 */
	public Reference getSourceReference() {
		return sourceRef;
	}

	/**
	 * @param source
	 *            the sourceRef to set
	 */
	public void setSourceReference(Reference source) {
		this.sourceRef = source;
	}

	/**
	 * @return the desRef
	 */
	public Reference getDestinationReference() {
		return desRef;
	}

	/**
	 * @param destination
	 *            the desRef to set
	 */
	public void setDestinationReference(Reference destination) {
		this.desRef = destination;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Returns the callback to be invoked after the download task completed.
	 * 
	 * @return the callback to be invoked
	 */
	public DownloadCallback getCallback() {
		return mCallback;
	}

	/**
	 * Specifies the callback to be invoked after the download task completed.
	 * 
	 * @param callback
	 *            the callback to be invoked after the download task completed
	 */
	public void setCallback(DownloadCallback callback) {
		this.mCallback = callback;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
