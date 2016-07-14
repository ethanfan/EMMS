/** 
 * IterableRepresentation.java
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

import java.io.IOException;
import java.io.OutputStream;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;

/**
 * A representation of data.
 */
public abstract class JsonDataRepresentation extends WriterRepresentation {
	
	/** The maximum buffer size. */
	private static final int MAX_SIZE = 819200;
	
	/** A file backed buffer. */
	private FileBackedOutputStream buf;
	
	/** The number of data items in the representation. */
	private volatile long count = Representation.UNKNOWN_SIZE;

	/**
     * Constructor.
     */
	public JsonDataRepresentation() {
		super(MediaType.APPLICATION_JSON);
		setCharacterSet(CharacterSet.UTF_8);
	}
	
	/**
	 * Specifies the expected number of items if known, -1 otherwise.
	 * 
	 * @param expectedCount the expected number of items
	 */
	public void setCount(long expectedCount) {
		count = expectedCount;
	}
	
	/**
	 * Returns the number of items in the data
	 * 
	 * @return the number of items
	 */
	public long getCount() {
		return count;
	}
	
	/** 
	 * Returns the buffer to cache the representation.
	 * 
	 * @return a file backed buffer to cache the representation
	 */
	protected synchronized FileBackedOutputStream getBuffer() {
		if (buf == null) {
			buf = new FileBackedOutputStream(MAX_SIZE);
		} 
		return buf;
	}
	
	/**
	 * Reset the buffer
	 */
	protected synchronized void resetBuffer() {
		try {
			getBuffer().reset();
		} catch (IOException e) {
			buf = null;
		}
	}
	
	@Override
	public long getAvailableSize() {
		try {
			if (getSize() == Representation.UNKNOWN_SIZE) {				
				write(getBuffer());
				setSize(getBuffer().asByteSource().size());
			}
			return getSize();
		} catch (Exception e) {
			// Ignored
		}
		return Representation.UNKNOWN_SIZE;
	}
	
	@Override
    public void write(OutputStream outputStream) throws IOException {
		if (getSize() != Representation.UNKNOWN_SIZE) {
			// Copy buffer to the output stream
			ByteStreams.copy(getBuffer().asByteSource().openBufferedStream(), outputStream);
		} else {
			super.write(outputStream);
		}
	}

}
