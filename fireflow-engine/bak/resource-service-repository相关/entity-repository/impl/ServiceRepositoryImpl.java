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

import org.fireflow.engine.entity.repository.ServiceDescriptor;
import org.fireflow.engine.entity.repository.ServiceRepository;
import org.fireflow.model.servicedef.ServiceDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServiceRepositoryImpl implements
		ServiceRepository {
	private String id = null;
	private List<ServiceDescriptor> serviceDescriptors = null;
	private String serviceFileName = null;
	private String serviceFileContent = null;
	private List<ServiceDef> services = null;
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ServiceRepository#getService()
	 */
	public ServiceDef getService(String serviceId) {
		if (services==null)return null;
		for (ServiceDef svc : services){
			if (svc.getId().equals(serviceId)){
				return svc;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.repository.ServiceRepository#getServiceContent()
	 */
	public String getServiceContent() {
		return serviceFileContent;
	}

	public void setServiceContent(String content){
		this.serviceFileContent = content;
	}

	/**
	 * @return the serviceFileName
	 */
	public String getFileName() {
		return serviceFileName;
	}

	/**
	 * @param serviceFileName the serviceFileName to set
	 */
	public void setFileName(String serviceFileName) {
		this.serviceFileName = serviceFileName;
	}

	/**
	 * @return the serviceDescriptors
	 */
	public List<ServiceDescriptor> getServiceDescriptors() {
		return serviceDescriptors;
	}

	/**
	 * @param serviceDescriptors the serviceDescriptors to set
	 */
	public void setServiceDescriptors(List<ServiceDescriptor> serviceDescriptors) {
		this.serviceDescriptors = serviceDescriptors;
	}

	/**
	 * @return the services
	 */
	public List<ServiceDef> getServices() {
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(List<ServiceDef> services) {
		this.services = services;
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
