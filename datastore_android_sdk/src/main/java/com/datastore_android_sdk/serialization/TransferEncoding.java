/**
 * TransferEncoding.java
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
package com.datastore_android_sdk.serialization;

import org.restlet.data.Metadata;

/**
 * Modifier of the encoding applied to the transferred media. Used to
 * decode the underlying media
 * 
 * @author Stanley Lam
 */
public class TransferEncoding extends Metadata {
	
	/** No encoding performed. */ 
	public static final TransferEncoding $7BIT = new TransferEncoding(
			"7bit", 
			"No encoding performed");
	
	public static final TransferEncoding $8BIT = new TransferEncoding(
			"8bit", 
			"8 bit encoding");
	
	/** Binary encoding. */
	public static final TransferEncoding BINARY	= new TransferEncoding(
			"binary", 
			"Binary encoding");
	
	/** Base64 encoding. */
	public static final TransferEncoding BASE64	= new TransferEncoding(
			"base64", 
			"base64 encoding");
	
	/**
   * Returns the encoding associated to a name. If an existing constant exists
   * then it is returned, otherwise a new instance is created.
   * 
   * @param name
   *            The name.
   * @return The associated encoding.
   */
  public static TransferEncoding valueOf(final String name) {
  	TransferEncoding result = null;

      if (name != null && !name.equals("")) {
          if (name.equalsIgnoreCase(BASE64.getName())) {
              result = BASE64;
          } else if (name.equalsIgnoreCase(BINARY.getName())) {
              result = BINARY;
          } else if (name.equalsIgnoreCase($8BIT.getName())) {
              result = $8BIT;
          } else {
              result = $7BIT;
          }
      }

      return result;
  }
	
	/**
   * Constructor.
   * 
   * @param name
   *            The name.
   */
  public TransferEncoding(final String name) {
      this(name, "Encoding applied to the body");
  }

  /**
   * Constructor.
   * 
   * @param name
   *            The name.
   * @param description
   *            The description.
   */
  public TransferEncoding(final String name, final String description) {
      super(name, description);
  }
  
  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object object) {
      return object instanceof TransferEncoding
              && getName().equalsIgnoreCase(((TransferEncoding) object).getName());
  }

	@Override
	public Metadata getParent() {
		return equals($7BIT) ? null : $7BIT;
	}
	
	/** {@inheritDoc} */
  @Override
  public int hashCode() {
      return (getName() == null) ? super.hashCode() : getName().toLowerCase().hashCode();
  }

  /**
   * Indicates if a given encoding is included in the current one. The test is
   * true if both encodings are equal or if the given encoding is within the
   * range of the current one. For example, ALL includes all encodings. A null
   * encoding is considered as included into the current one.
   * <p>
   * Examples:
   * <ul>
   * <li>BASE64.includes($7BIT) -> true</li>
   * <li>$7BIT.includes(BASE64) -> false</li>
   * </ul>
   * 
   * @param included
   *            The encoding to test for inclusion.
   * @return True if the given encoding is included in the current one.
   * @see #isCompatible(Metadata)
   */
	@Override
	public boolean includes(Metadata included) {
		return equals($7BIT) || included == null || equals(included);
	}

}
