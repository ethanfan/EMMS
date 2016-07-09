/** 
 * ResourceReleasingRepresentation.java
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

import org.restlet.representation.Representation;
import org.restlet.resource.Resource;
import org.restlet.util.WrapperRepresentation;

/**
 * Representation that wraps another representation and closes the parent
 * {@link Resource} when the representation is released.
 * 
 * @author Stanley Lam
 */
public class ResourceReleasingRepresentation extends WrapperRepresentation {
	
	/** The parent resource. */
	private final Resource resource;
	
	/**
     * Default constructor.
     * 
     * @param wrappedRepresentation
     *            The wrapped representation.
     * @param resource
     *            The parent resource.
     */
	public ResourceReleasingRepresentation(Representation wrappedRepresentation, Resource resource) {
		super(wrappedRepresentation);
		this.resource = resource; 
	}
	
	@Override
    public void release() {
        if (resource != null) {
        	resource.release();
        }
        
        super.release();
    }

}
