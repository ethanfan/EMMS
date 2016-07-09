/** 
 * BufferedRepresentaion.java
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
package com.jaffer_datastore_android_sdk.representation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.engine.io.IoUtils;
import org.restlet.representation.Representation;
import org.restlet.util.WrapperRepresentation;

import com.google.common.io.Files;
import com.jaffer_datastore_android_sdk.util.TokenGenerator;


/**
 * Representation that buffers the wrapped {@link Representation}. This is
 * useful when you want to prevent chunk encoding from being used for dynamic
 * representations or when you want to reuse a transient representation several
 * times. <br>
 * <br>
 * This implementation buffers the wrapped {@link Representation} in memory if
 * its size is less than the threshold. Alternatively, if the size is larger
 * than the threshold, it buffers the wrapped {@link Representation} in
 * {@link File} <br>
 * <br>
 * By default, cache files are created in the system default temp directory with
 * predictable names. When using this implementation in an environment with
 * local, untrusted users, setRepository(File) MUST be used to configure a
 * repository location that is not publicly writable. In a Servlet container the
 * location identified by the {@code ServletContext} attribute
 * {@code javax.servlet.context.tempdir} may be used.
 */
public class BufferedRepresentation extends WrapperRepresentation {

	/**
	 * The default threshold above which buffer will be stored on disk.
	 */
	public static final long DEFAULT_SIZE_THRESHOLD = 10240;
	
	/** Length of the temporary cache file name prefix */
	protected final int MAX_PREFIX_LENGTH = 5;
	
	/**
	 * An enumeration of the types of buffers
	 */
	private enum Buffer {
		MEMORY,
		FILE,
		NONE;
	}
	
	/** The cached content as an array of bytes. */
	private volatile byte[] buffer;

	/** The cached content as a File */
	private volatile File cache;

	/** Indicates the type of cache the wrapped entity cached. */
	private volatile Buffer bufferType = Buffer.NONE;

	/** The maximum memory threshold */
	private volatile long maxMemoryBufferSize;

	/** The data repository */
	private volatile File repo;
  
	/**
	 * Creates a new instance of {@link BufferedRepresentation} with the given
	 * configuration
	 * 
	 * @param bufferedRepresentation
	 *          The {@link Representation} buffered
	 * @param repository
	 *          The data repository, which is the directory in which files will be
	 *          created, should the item size exceed the threshold.
	 */
	public BufferedRepresentation(Representation bufferedRepresentation, File repository) {
		this(bufferedRepresentation, DEFAULT_SIZE_THRESHOLD, repository);
	}
  
	/**
	 * Creates a new instance of {@link BufferedRepresentation} with the given
	 * maximum memory buffer
	 * 
	 * @param bufferedRepresentation
	 *            The {@link Representation} buffered
	 * @param maxBufferSize
	 *            The maximum memory buffer size
	 * @param repository
	 *            The data repository, which is the directory in which files
	 *            will be created, should the item size exceed the threshold.
	 */
	public BufferedRepresentation(Representation bufferedRepresentation, long maxBufferSize, File repository) {
		this(bufferedRepresentation, maxBufferSize, repository, false);
	}
	
	/**
	 * Creates a new instance of {@link BufferedRepresentation} with the given maximum memory buffer
	 * 
	 * @param bufferedRepresentation
	 *            The {@link Representation} buffered
	 * @param maxBufferSize
	 *            The maximum memory buffer size
	 * @param repository
	 *            The data repository, which is the directory in which files
	 *            will be created, should the item size exceed the threshold.
	 * @param buffer
	 */
	public BufferedRepresentation(Representation bufferedRepresentation, long maxBufferSize, File repository, boolean buffer) {
		super(bufferedRepresentation);
		maxMemoryBufferSize = maxBufferSize;
		repo = repository == null ? new File(System.getProperty("java.io.tmpdir")) : repository;
		
		// We buffer the wrapped representation if required
		if (buffer) {
			try {				
				buffer();
			} catch (IOException e) {
				// Ignored
			}
		}
	}
  
	/**
	 * Buffers the content of the wrapped entity.
	 * 
	 * @throws IOException
	 */
	private void buffer() throws IOException {
		if (!isBuffered()) {
			if (getWrappedRepresentation().isAvailable()) {
				long wrappedSize = getWrappedRepresentation().getSize();
				if (wrappedSize <= maxMemoryBufferSize && wrappedSize >= 0) {
					// Buffers the content into memory
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					getWrappedRepresentation().write(baos);
					baos.flush();
					setMemoryBuffer(baos.toByteArray());
					baos.close();
					setBufferType(Buffer.MEMORY);
				} else {
					// Buffers the content into file
					File buf = File.createTempFile(TokenGenerator.getInstance().generate(MAX_PREFIX_LENGTH), null, repo);
					InputStream is = getWrappedRepresentation().getStream();
					Files.asByteSink(buf).writeFrom(is);
					is.close();
					setFileBuffer(buf);
					setBufferType(Buffer.FILE);
				}
			}
			
			// We must exhaust the content. Without exhausting the content and
			// using http.keepAlive will result in socket timeout in subsequent
			// requests.
			getWrappedRepresentation().exhaust();
			getWrappedRepresentation().release();
		}
	}
	
	/**
	 * Returns the directory used to temporarily store files that are larger than
	 * the configured size threshold.
	 * 
	 * @return The directory in which temporary files will be located.
	 */
	public File getRepository() {
		return repo;
	}
	
	/**
	 * Sets the directory used to temporarily store files that are larger than the
	 * configured size threshold.
	 * 
	 * @param repository
	 *          The directory in which temporary files will be located.
	 */
	public void setRepository(File repository) {
		repo = repository;
	}
	
	/**
	 * Returns the size threshold beyond which files are written directly to disk.
	 * The default value is 10240 bytes.
	 * 
	 * @return The size threshold, in bytes.
	 */
	public long getThreshold() {
		return maxMemoryBufferSize;
	}
	
	/**
	 * Sets the size threshold beyond which files are written directly to disk.
	 * 
	 * @param threshold
	 *          The in-memory buffer size threshold, in bytes.
	 */
	public void setThreshold(long threshold) {
		maxMemoryBufferSize = threshold;
	}
	
	/**
	 * Sets the buffered content as an array of bytes.
	 * 
	 * @param buf
	 *          The buffered content as an array of bytes.
	 */
	protected void setMemoryBuffer(byte[] buf) {
		buffer = buf;
	}
	
	/**
	 * Returns the buffered content from memory.
	 * 
	 * @return The buffered content from memory.
	 */
	protected byte[] getMemoryBuffer() {
		return buffer;
	}
	
	/**
	 * Specifies the file buffer
	 * 
	 * @param buf The {@link File} that buffers the entity
	 */
	protected void setFileBuffer(File buf) {
		cache = buf;
	}
	
	/**
	 * Returns the buffered content from {@link File}
	 * 
	 * @return The buffered content from {@link File}
	 */
	protected File getFileBuffer() {
		return cache;
	}
	
	/**
	 * Indicates if the wrapped entity has been already buffered.
	 * 
	 * @return True if the wrapped entity has been already buffered.
	 */
	protected boolean isBuffered() {
		return bufferType != Buffer.NONE;
	}
	
	/**
	 * Specifies the type of buffer used to cache the wrapped entity
	 * 
	 * @param buffered
	 *          True if the wrapped entity has been already buffered.
	 */
	protected void setBufferType(Buffer type) {
		bufferType = type;
	}
	
	@Override
	public long getAvailableSize() {
		return getSize();
	}
	
	@Override
	public boolean isAvailable() {
		try {
			buffer();
		} catch (IOException e) {
			Context.getCurrentLogger().log(Level.SEVERE, "Unable to buffer the wrapped representation", e);
		}

		return isBuffered();
	}
  
  @Override
	public java.nio.channels.ReadableByteChannel getChannel() throws IOException {
		return IoUtils.getChannel(getStream());
	}
  
	@Override
	public Reader getReader() throws IOException {
		return IoUtils.getReader(getStream(), getCharacterSet());
	}
	
	@Override
	public InputStream getStream() throws IOException {
		buffer();
		
		InputStream result = null;
		switch (bufferType) {
		case MEMORY:
			byte[] memoryBuffer = getMemoryBuffer();
			if (memoryBuffer != null) {
				result = new ByteArrayInputStream(memoryBuffer);
			}
			break;
		case FILE:
			File buf = getFileBuffer();
			if (buf != null && buf.exists() && !buf.isDirectory()) {
				result = new FileInputStream(buf);
			}
			break;
		default:
			break;
		}
		return result;
	};
	
	@Override
	public long getSize() {
		// Read the content, store it and compute the size.
		try {
			buffer();
		} catch (IOException e) {
			Context.getCurrentLogger().log(Level.SEVERE, "Unable to buffer the wrapped representation", e);
		}

		long size = Representation.UNKNOWN_SIZE;
		switch (bufferType) {
		case MEMORY:
			byte[] memoryBuffer = getMemoryBuffer();
			if (memoryBuffer != null) {
				size = memoryBuffer.length;
			}
			break;
		case FILE:
			File buf = getFileBuffer();
			if (buf != null && buf.exists() && !buf.isDirectory()) {
				size = buf.length();
			}
			break;
		default:
			size = Representation.UNKNOWN_SIZE;
			break;
		}
		return size;
	}
	
	@Override
	public String getText() throws IOException {
		buffer();
		
		String result = null;
		switch (bufferType) {
		case MEMORY:
			byte[] memoryBuffer = getMemoryBuffer();
			if (memoryBuffer != null) {
				result = (getCharacterSet() != null) ? new String(memoryBuffer, getCharacterSet().toCharset().name()) : new String(memoryBuffer);
			}
			break;
		case FILE:
			File fileBuffer = getFileBuffer();
			if (fileBuffer != null && fileBuffer.isFile() && fileBuffer.exists()) {
				InputStream is = new FileInputStream(fileBuffer);
				byte[] buf = new byte[(int) getThreshold()];
				StringBuilder builder = new StringBuilder();
				String charsetName = getCharacterSet() != null ? getCharacterSet().toCharset().name() : null;
				int len;
				
				while ((len = is.read(buf)) != -1) {
					String str = charsetName == null ? new String(buf, 0, len) : new String(buf, 0, len, charsetName);
					builder.append(str);
				}
				is.close();
				result = builder.toString();
			}
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * Releases the cache file handle.
	 */
	@Override
	public void release() {
		if (getFileBuffer() != null) {
			try {
				IoUtils.delete(getFileBuffer(), true);
			} catch (Exception e) {
			}
		}
		setFileBuffer(null);
		super.release();
	}
	
	@Override
	public void write(OutputStream outputStream) throws IOException {
		buffer();
		
		switch (bufferType) {
		case MEMORY:
			byte[] memoryBuffer = getMemoryBuffer();
			if (memoryBuffer != null) {
				outputStream.write(memoryBuffer);
			}
			break;
		case FILE:
			File fileBuffer = getFileBuffer();
			if (fileBuffer != null && fileBuffer.isFile() && fileBuffer.exists()) {
				InputStream is = new FileInputStream(fileBuffer);
				byte[] buf = new byte[(int) getThreshold()];
				int len;
				
				try {					
					while ((len = is.read(buf)) != -1) {
						outputStream.write(buf, 0, len);
					}
				} finally {
					is.close();
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void write(WritableByteChannel writableChannel) throws IOException {
		buffer();

		switch (bufferType) {
		case MEMORY:
			byte[] memoryBuffer = getMemoryBuffer();
			if (memoryBuffer != null) {
				writableChannel.write(ByteBuffer.wrap(memoryBuffer));
			}
			break;
		case FILE:
			File fileBuffer = getFileBuffer();
			if (fileBuffer != null && fileBuffer.isFile() && fileBuffer.exists()) {
				InputStream is = new FileInputStream(fileBuffer);
				byte[] buf = new byte[(int) getThreshold()];
				int len;
				
				try {					
					while ((len = is.read(buf)) != -1) {
						writableChannel.write(ByteBuffer.wrap(buf, 0, len));
					}
				} finally {					
					is.close();
				}
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void write(Writer writer) throws IOException {
		buffer();
		
		switch (bufferType) {
		case MEMORY:
			byte[] memoryBuffer = getMemoryBuffer();
			if (memoryBuffer != null) {
				String str = (getCharacterSet() != null) ? new String(memoryBuffer, getCharacterSet().toCharset().name()) : new String(memoryBuffer);
				writer.write(str);
			}
			break;
		case FILE:
			File fileBuffer = getFileBuffer();
			if (fileBuffer != null && fileBuffer.isFile() && fileBuffer.exists()) {
				InputStream is = new FileInputStream(fileBuffer);
				byte[] buf = new byte[(int) getThreshold()];
				String charsetName = getCharacterSet() != null ? getCharacterSet().toCharset().name() : null;
				int len;
				
				try {					
					while ((len = is.read(buf)) != -1) {
						String str = charsetName == null ? new String(buf, 0, len) : new String(buf, 0, len, charsetName);
						writer.write(str);
					}
				} finally {
					is.close();
				}
			}
			break;
		default:
			break;
		}
	}

}
