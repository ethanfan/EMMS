/** 
 * Range.java
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
package com.jaffer_datastore_android_sdk.datastore.data;

/**
 * Describes a range of items.
 * 
 * @author Stanley Lam
 */
public class Range extends org.restlet.data.Range {
	
	/**
     * Default constructor defining a range starting on the first byte and with
     * a maximum size, i.e. covering the whole entity.
     */
    public Range() {
        this(INDEX_FIRST, SIZE_MAX);
    }
    
    /**
     * Constructor defining a range starting on the first byte and with the
     * given size.
     * 
     * @param size
     *            Size of the range in number of bytes.
     */
    public Range(long size) {
        this(INDEX_FIRST, size);
    }

    /**
     * Constructor. Sets the name of the range unit as "bytes" by default.
     * 
     * @param index
     *            Index from which to start the range
     * @param size
     *            Size of the range in number of bytes.
     */
    public Range(long index, long size) {
        super(index, size);
        setUnitName("items");
    }

}
