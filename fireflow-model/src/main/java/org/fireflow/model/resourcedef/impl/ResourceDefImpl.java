/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.model.resourcedef.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.model.AbstractModelElement;
import org.fireflow.model.data.Input;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.ResourceType;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceDefImpl extends AbstractModelElement implements ResourceDef {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2809502180615384347L;
	private ResourceType resourceType = ResourceType.USER;
	private String resolverBeanName = null;
	private String resolverClassName = null;
	private Map<String,String> extendedAttributes = new HashMap<String,String>();
	private String value = null;
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	// private String fileName = null;

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public String getResolverBeanName() {
		return resolverBeanName;
	}

	public void setResolverBeanName(String beanName) {
		this.resolverBeanName = beanName;
	}
	
	

	public String getResolverClassName() {
		return resolverClassName;
	}

	public void setResolverClassName(String resolverClassName) {
		this.resolverClassName = resolverClassName;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.model.resourcedef.ResourceDef#getExtendedAttributes()
	 */
	public Map<String, String> getExtendedAttributes() {
		return extendedAttributes;
	}
}
