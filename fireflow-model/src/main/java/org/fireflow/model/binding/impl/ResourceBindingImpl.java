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
package org.fireflow.model.binding.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ResourceBindingImpl implements ResourceBinding {
	private static final String DEFAULT_ASSIGNMENT_HANDLER_CLASS_NAME= "org.fireflow.engine.invocation.impl.DefaultAssignmentHandler";
	private String name = null;
	private WorkItemAssignmentStrategy assignmentStrategy = WorkItemAssignmentStrategy.ASSIGN_TO_ANY;
	private List<String> administrators = new ArrayList<String>();
	private List<String> readers = new ArrayList<String>();
	private List<String> potentialOwners = new ArrayList<String>();
	private String assignmentHandlerBeanName = null;
	private String assignmentHandlerClassName = DEFAULT_ASSIGNMENT_HANDLER_CLASS_NAME;
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getAdministrators()
	 */
	public List<String> getAdministratorRefs() {
		return administrators;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setAdministrators(java.util.List)
	 */
	public void addAdministratorRef(String resourceDefId){
		this.administrators.add(resourceDefId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getName()
	 */
	public String getDisplayName() {
		return name;
	}
	
	public void setDisplayName(String nm){
		this.name = nm;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getPotentialOwners()
	 */
	public List<String> getPotentialOwnerRefs() {
		return this.potentialOwners;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#getReaders()
	 */
	public List<String> getReaderRefs() {
		return this.readers;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setPotentialOwners(java.util.List)
	 */
	public void addPotentialOwnerRef(String resourceId) {
		this.potentialOwners.add(resourceId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.binding.ResourceBinding#setReaders(java.util.List)
	 */
	public void addReaderRef(String resourceId) {
		this.readers.add(resourceId);

	}
	/**
	 * @return the assignmentStrategy
	 */
	public WorkItemAssignmentStrategy getAssignmentStrategy() {
		return assignmentStrategy;
	}
	/**
	 * @param assignmentStrategy the assignmentStrategy to set
	 */
	public void setAssignmentStrategy(WorkItemAssignmentStrategy assignmentStrategy) {
		this.assignmentStrategy = assignmentStrategy;
	}
	public String getAssignmentHandlerClassName() {
		return assignmentHandlerClassName;
	}
	public String getAssignmentHandlerBeanName() {
		return assignmentHandlerBeanName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAssignmentHandlerBeanName(String assignmentHandlerBeanName) {
		this.assignmentHandlerBeanName = assignmentHandlerBeanName;
	}
	public void setAssignmentHandlerClassName(String assignmentHandlerClassName) {
		this.assignmentHandlerClassName = assignmentHandlerClassName;
	}

}
