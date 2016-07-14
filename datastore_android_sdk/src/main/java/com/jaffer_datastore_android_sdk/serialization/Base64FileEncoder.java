/** 
 * Base64FileEncoder.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.restlet.data.CharacterSet;
import org.restlet.data.Disposition;
import org.restlet.data.Header;
import org.restlet.engine.header.ContentType;
import org.restlet.engine.header.DispositionWriter;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.engine.header.HeaderUtils;
import org.restlet.engine.io.WriterOutputStream;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.service.MetadataService;
import org.restlet.util.Series;

import android.util.Log;

import com.google.common.io.BaseEncoding;
import com.jaffer_datastore_android_sdk.datastore.Build;


/**
 * Writes a base64 encoded file to a stream. 
 */
public class Base64FileEncoder {
	
	static final String TAG = Base64FileEncoder.class.getSimpleName();
	
	/** The default read buffer size. */
	static final int BUFFER_SIZE = 3 * 1024;
	
	/** The Content headers supported. */
	public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	
	/** The file to encode. */
	private final File file;
	
	/**
	 * Creates a new instance that encodes a {@code file} to a stream.
	 *  
	 * @param file the {@link File} to encode
	 */
	public Base64FileEncoder(File file) {
		if (file == null) {
			throw new NullPointerException("file == null");
		}
		this.file = file;
	}
	
	/**
	 * Returns the file to encode.
	 * 
	 * @return the file to encode
	 */
	protected File getFile() {
		return file;
	}
	
	/**
	 * Base64 encodes the given {@code file} to a stream.
	 * 
	 * @param file the file to encode
	 * @throws IOException if an error occurs while encoding the file
	 */
	public void encode(Writer writer) throws IOException {
		if (getFile().exists()) {		
			// Create entity headers
			MetadataService metadata = new MetadataService();
			String extension = FilenameUtils.getExtension(getFile().getName());
			extension = extension == null ? null : extension.toLowerCase(Locale.ENGLISH);
			
			// The output stream should be written in UTF-8 as a JSON
			WriterOutputStream wos = new WriterOutputStream(writer, CharacterSet.UTF_8) {
				
				@Override
				public void close() throws IOException {
					// Do not close the underlying writer
					if (Build.DEBUG) {
						Log.i(TAG, "Closing the output stream...");
					}
				}
				
			};
			Representation entity = new FileRepresentation(getFile(), metadata.getMediaType(extension));
			Disposition disposition = new Disposition(Disposition.TYPE_ATTACHMENT);
			String filename = URLEncoder.encode(getFile().getName(), CharacterSet.UTF_8.getName());
			disposition.setFilename(filename);
			entity.setDisposition(disposition);
			
			Series<Header> headers = new Series<Header>(Header.class);
			// Create Content-Type header
			if (entity.getMediaType() != null) {
				HeaderUtils.addHeader(
						HeaderConstants.HEADER_CONTENT_TYPE, 
						ContentType.writeHeader(entity), 
						headers);
			}
			// Create Content-Disposition header
			if (entity.getDisposition() != null && !Disposition.TYPE_NONE.equals(entity.getDisposition().getType())) {
				HeaderUtils.addHeader(
						HeaderConstants.HEADER_CONTENT_DISPOSITION, 
						DispositionWriter.write(entity.getDisposition()), 
						headers);
			}
			// Create Content-Transfer-Encoding header
			headers.add(HEADER_CONTENT_TRANSFER_ENCODING, TransferEncoding.BASE64.toString());
			
			// Write the entity header to the JSON string
			for (Header header : headers) {
				HeaderUtils.writeHeaderLine(header, wos);
				if (Build.DEBUG) {
					Log.i(TAG, header.toString());
				}
			}
			HeaderUtils.writeCRLF(wos);
			
			// Serialize the file as a base64 encoded string
			InputStream is = new FileInputStream(file);
			
			// Encode data in chunk
			// Chunk size must be multiple of 3 in order to avoid padding within output
			int bytesRead;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = is.read(buffer)) > 0) {
				writer.write(BaseEncoding.base64().encode(Arrays.copyOfRange(buffer, 0, bytesRead)));
			}
			
			wos.close();
			is.close();
		}
	}

}
