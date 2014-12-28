/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.client.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.WorkflowQueryDelegate;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.server.WorkflowEngineService;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowSessionRemoteImpl implements WorkflowSession {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5701766995913828138L;
	
	protected WorkflowEngineService workflowServer = null;
	protected String sessionId = null;
	protected User user = null;
	
	public String getSessionId(){
		return sessionId;
	}
	
	public void setSessionId(String id){
		sessionId = id;
	}
	
	public WorkflowEngineService getWorkflowServer(){
		return workflowServer;
	}
	public void setWorkflowServer(WorkflowEngineService server){
		this.workflowServer = server;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#getCurrentUser()
	 */
	public User getCurrentUser() {
		return user;
	}
	
	public void setCurrentUser(User u){
		this.user = u;
	}


//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowSession#getCurrentProcessInstance()
//	 */
//	@Override
//	public ProcessInstance getCurrentProcessInstance() {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowSession#getCurrentActivityInstance()
//	 */
//	@Override
//	public ActivityInstance getCurrentActivityInstance() {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowSession#getLatestCreatedWorkItems()
//	 */
//	@Override
//	public List<WorkItem> getLatestCreatedWorkItems() {
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowStatement(java.lang.String)
	 */
	public WorkflowStatement createWorkflowStatement(String processType) {
		WorkflowStatementRemoteImpl statement = new WorkflowStatementRemoteImpl(this,processType);
		statement.setWorkflowServer(workflowServer);
		return statement;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowStatement()
	 */
	public WorkflowStatement createWorkflowStatement() {
		WorkflowStatementRemoteImpl statement = new WorkflowStatementRemoteImpl(this,"FPDL");//TODO 默认为FPDL
		statement.setWorkflowServer(workflowServer);
		return statement;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowQuery(java.lang.Class, java.lang.String)
	 */
//	@Override
//	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(
//			Class<T> c, String processType) {
//		WorkflowQueryRemoteImpl<T> queryRemote = new WorkflowQueryRemoteImpl<T>();
//		queryRemote.setWorkflowServer(workflowServer);
//		return queryRemote;
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowSession#createWorkflowQuery(java.lang.Class)
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(
			Class<T> c) {
		WorkflowQueryImpl<T> queryRemote = new WorkflowQueryImpl<T>(c);
		InnerWorkflowQueryDelegate delegate = new InnerWorkflowQueryDelegate();
		
		queryRemote.setQueryDelegate(delegate);
		return queryRemote;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowSession#setDynamicAssignmentHandler(java.lang.String, org.fireflow.engine.invocation.AssignmentHandler)
//	 */
//	@Override
//	public WorkflowSession setDynamicAssignmentHandler(String activityId,
//			AssignmentHandler assignmentHandler) {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowSession#getDynamicAssignmentHandler()
//	 */
//	@Override
//	public Map<String, AssignmentHandler> getDynamicAssignmentHandler() {
//		return null;
//	}

	class InnerWorkflowQueryDelegate implements WorkflowQueryDelegate{

		/* (non-Javadoc)
		 * @see org.fireflow.client.WorkflowQueryDelegate#executeQueryList(org.fireflow.client.WorkflowQuery)
		 */
		public <T extends WorkflowEntity> List<T> executeQueryList(
				WorkflowQuery<T> q) {
			WorkflowQueryImpl queryImpl = (WorkflowQueryImpl)q;
			List<AbsWorkflowEntity> list = workflowServer.executeQueryList(sessionId, queryImpl);
			List<T> result = new ArrayList<T>();
			if (list!=null){
				for (AbsWorkflowEntity entity:list){
					result.add((T)entity);
				}
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see org.fireflow.client.WorkflowQueryDelegate#executeQueryCount(org.fireflow.client.WorkflowQuery)
		 */
		public <T extends WorkflowEntity> int executeQueryCount(
				WorkflowQuery<T> q) {
			WorkflowQueryImpl queryImpl = (WorkflowQueryImpl)q;
			return workflowServer.executeQueryCount(sessionId,queryImpl);
		}

		/* (non-Javadoc)
		 * @see org.fireflow.client.WorkflowQueryDelegate#getEntity(java.lang.String, java.lang.Class)
		 */
		@SuppressWarnings("unchecked")
		public <T extends WorkflowEntity> T getEntity(String entityId,
				Class<T> entityClass) {
			AbsWorkflowEntity entity = workflowServer.getEntity(sessionId, entityId, entityClass.getName());
			return (T)entity;
		}
		
	}
	
	
	public boolean isSessionValid(){
		return workflowServer.isSessionValid(this.getSessionId());
	}
}
