/**
 * Copyright 2007-2008 非也
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
package org.fireflow.client.impl;

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

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.WorkflowQueryDelegate;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.server.support.UserXmlAdapter;


/**
 * @author chennieyun
 * 
 */
@XmlRootElement(name="workflowSession")
@XmlType(name="workflowSessionType",propOrder={"sessionId","currentUser"})
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowSessionLocalImpl implements WorkflowSession,RuntimeContextAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5596014590065796474L;

	@XmlTransient
	protected Map<String,Object> attributes = new HashMap<String,Object>();
	
	@XmlTransient
	protected Map<String,AssignmentHandler> dynamicAssignmentHandlers = new HashMap<String,AssignmentHandler>();
	
	@XmlTransient
	protected List<WorkItem> latestCreatedWorkItems = new ArrayList<WorkItem>();
	
	@XmlTransient
	protected RuntimeContext context = null;
	
	@XmlElement(name="currentUser")
	@XmlJavaTypeAdapter(value = UserXmlAdapter.class)  
	protected User currentUser = null;
	
	@XmlElement(name="sessionId")
	protected String sessionId = null;
	
	public String getSessionId(){
		return sessionId;
	}
	
	public void setSessionId(String id){
		sessionId = id;
	}

	public void clearAttributes() {
		this.attributes.clear();
		
	}

	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public Object removeAttribute(String name) {
		return this.attributes.remove(name);
	}

	public WorkflowSession setAttribute(String name, Object attr) {
		this.attributes.put(name, attr);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.api.WorkflowSession#getCurrentUser()
	 */
	public User getCurrentUser() {
		
		return this.currentUser;
	}
	
	public void setCurrentUser(User currentUser){
		this.currentUser  = currentUser;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowQuery(java.lang.Object)
	 */
//	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c,String processType) {
//		WorkflowQueryImpl<T> query = new WorkflowQueryImpl<T>(this,c,processType);
//		return query;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowStatement()
	 */
	public WorkflowStatement createWorkflowStatement(String processType) {
		WorkflowStatementLocalImpl statement = new WorkflowStatementLocalImpl(this);
		statement.setProcessType(processType);
		return statement;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getCurrentActivityInstance()
	 */
	public ActivityInstance getCurrentActivityInstance() {
		return (ActivityInstance)this.attributes.get(CURRENT_ACTIVITY_INSTANCE);
	}
	
	public void setCurrentActivityInstance(ActivityInstance activityInstance){
		this.setAttribute(CURRENT_ACTIVITY_INSTANCE, activityInstance);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getCurrentProcessInstance()
	 */
	public ProcessInstance getCurrentProcessInstance() {
		
		return (ProcessInstance)this.attributes.get(CURRENT_PROCESS_INSTANCE);
	}
	
	public void setCurrentProcessInstance(ProcessInstance processInstance){
		this.setAttribute(CURRENT_PROCESS_INSTANCE, processInstance);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getLatestCreatedWorkItems()
	 */
	public List<WorkItem> getLatestCreatedWorkItems() {
		
		return latestCreatedWorkItems;
	}
	
	public void setLatestCreatedWorkItems(List<WorkItem> workItems){
		if (workItems!=null){
			latestCreatedWorkItems.addAll(workItems);
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#putAllAttributes(java.util.Map)
	 */
	public WorkflowSession setAllAttributes(Map<String, Object> attributes) {
		if (attributes!=null){
			this.attributes.putAll(attributes);
		}
		return this;
	}
    /**
     * @param ctx
     */
    public void setRuntimeContext(RuntimeContext ctx){
    	this.context = ctx;
    }
    
    /**
     * @return
     */
    public RuntimeContext getRuntimeContext(){
    	return this.context;
    }

	/**
	 * 取得活动id等于activityId的动态分配句柄，并从session中将其删除。
	 * @param activityId
	 * @return
	 */
	public AssignmentHandler consumeDynamicAssignmentHandler(String activityId) {
		return dynamicAssignmentHandlers.remove(activityId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#setDynamicAssignmentHandler(java.lang.String, org.fireflow.engine.service.human.AssignmentHandler)
	 */
	public WorkflowSession setDynamicAssignmentHandler(String activityId,
			AssignmentHandler assignmentHandler) {
		dynamicAssignmentHandlers.put(activityId, assignmentHandler);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getAttributes()
	 */
	public Map<String, Object> getAllAttributes() {
		return this.attributes;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getDynamicAssignmentHandler()
	 */
	public Map<String, AssignmentHandler> getDynamicAssignmentHandler() {
		return this.dynamicAssignmentHandlers;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowQuery(java.lang.Class)
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c) {
		WorkflowQueryImpl<T> query = new WorkflowQueryImpl<T>(c);
		WorkflowQueryDelegate delegate = (WorkflowQueryDelegate)this.createWorkflowStatement(context.getDefaultProcessType());
		query.setQueryDelegate(delegate);
		return query;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowStatement()
	 */
	public WorkflowStatement createWorkflowStatement() {
		WorkflowStatementLocalImpl statement = new WorkflowStatementLocalImpl(this);
		statement.setProcessType(context.getDefaultProcessType());
		return statement;
	}
}
