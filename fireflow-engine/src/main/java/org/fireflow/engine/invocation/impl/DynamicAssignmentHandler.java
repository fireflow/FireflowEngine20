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
package org.fireflow.engine.invocation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.server.support.UserListXmlAdapter;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="dynamicAssignmentHandler")
@XmlType(name="dynamicAssignmentHandlerType")
@XmlAccessorType(XmlAccessType.FIELD)
public class DynamicAssignmentHandler extends AbsAssignmentHandler implements AssignmentHandler {
	/**
	 * 
	 */
	@XmlTransient
	private static final long serialVersionUID = 675889705207600732L;

	@XmlElement(name="potentialOwners")
	@XmlJavaTypeAdapter(UserListXmlAdapter.class)
	private List<User> potentialOwners = new ArrayList<User>();
	
	@XmlElement(name="readers")
	@XmlJavaTypeAdapter(UserListXmlAdapter.class)
	private List<User> readers = new ArrayList<User>();;
	
	@XmlElement(name="assignmentStrategy")
	private WorkItemAssignmentStrategy assignmentStrategy = WorkItemAssignmentStrategy.ASSIGN_TO_ALL;
	

	/**
	 * 获得潜在的工作参与者列表
	 * @return
	 */
	public List<User> resolvePotentialOwners(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance){
		return this.potentialOwners;
	}
	
	public void setPotentialOwners(List<User> owners){
		potentialOwners.clear();
		this.potentialOwners.addAll(owners);
	}
	public List<User> getPotentialOwners(){
		return this.potentialOwners;
	}
	/**
	 * 获得抄送人列表
	 * @return
	 */
	public List<User> resolveReaders(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance){
		return this.readers;
	}
	
	public void setReaders(List<User> users){
		this.readers.clear();
		
		this.readers.addAll(users);
	}

	public List<User> getReaders(){
		return this.readers;
	}
	/**
	 * @return the assignmentStrategy
	 */
	public WorkItemAssignmentStrategy resolveAssignmentStrategy(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity) {
		return assignmentStrategy;
	}

	/**
	 * @param assignmentStrategy the assignmentStrategy to set
	 */
	public void setAssignmentStrategy(WorkItemAssignmentStrategy assignmentStrategy) {
		this.assignmentStrategy = assignmentStrategy;
	}
	
	public WorkItemAssignmentStrategy getAssignmentStrategy(){
		return this.assignmentStrategy;
	}

	@Override
	public List<User> resolveAdministrators(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Map<WorkItemProperty,Object> resolveWorkItemPropertyValues(){
		return new HashMap<WorkItemProperty,Object>();
	}
	
}
