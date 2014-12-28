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
package org.fireflow.pdl.fpdl.process.impl;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.pdl.fpdl.process.Import;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ImportImpl implements Import {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3733329124937924288L;
	
	private WorkflowProcess process = null;
	private String importType;
	private String location;
	private String id = null;
	private String name = null;
	private String displayName = null;
//	private List<T> contents = new ArrayList<T>();
	private Map<String,String> extendAttributes = new HashMap<String,String>();
	private String packageId = null;
	
	
	public ImportImpl(WorkflowProcess process){
		this.process = process;
	}
	
	/**
	 * @return the importType
	 */
	public String getImportType() {
		return importType;
	}
	/**
	 * @param importType the importType to set
	 */
	public void setImportType(String importType) {
		this.importType = importType;
	}
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/*
	public List<T> getContents(){
		return contents;
	}
	
	
	public T getContent(String id) {		
		if (this.importType == null)
			return null;
		for (T t : contents) {
			if (((ModelElement) t).getId().equals(id)) {
				return t;
			}
		}
		return null;
	}
	
	
	public void setContents(List<T> contents){
		this.contents = contents;
	}
	*/

	/**
	 * @return the packageId
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
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



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	
	
}
