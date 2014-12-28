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
package org.fireflow.engine.entity.repository.impl;

import java.util.List;

import org.fireflow.engine.entity.repository.ResourceDescriptor;
import org.fireflow.engine.entity.repository.ResourceRepository;
import org.fireflow.model.resourcedef.ResourceDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceRepositoryImpl implements ResourceRepository {
	protected String id = null;
	protected String fileContent;
	protected String resourceFileName ;
	protected List<ResourceDef> resources = null;
	protected List<ResourceDescriptor> resourceDescriptors = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ResourceRepository#getResource(java.lang.String)
	 */
	public ResourceDef getResource(String resourceId) {
		if (resources==null)return null;
		for (ResourceDef rsc : resources){
			if (rsc.getId().equals(resourceId)){
				return rsc;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ResourceRepository#getResourceDescriptors()
	 */
	public List<ResourceDescriptor> getResourceDescriptors() {
		return resourceDescriptors;
	}
	
	public void setResourceDescriptors(List<ResourceDescriptor> descriptors){
		this.resourceDescriptors = descriptors;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ResourceRepository#getResourceFileContent()
	 */
	public String getResourceContent() {
		return fileContent;
	}
	
	public void setResourceContent(String content){
		fileContent = content;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ResourceRepository#getResourceFileName()
	 */
	public String getFileName() {
		return resourceFileName;
	}
	
	public void setFileName(String fileName){
		this.resourceFileName = fileName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ResourceRepository#getResources()
	 */
	public List<ResourceDef> getResources() {
		return resources;
	}
	
	public void setResources(List<ResourceDef> rscs){
		this.resources = rscs;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
