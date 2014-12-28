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
package org.fireflow.server;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowQueryImpl;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.impl.WorkflowStatementLocalImpl;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.context.TransactionTemplateAware;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.impl.AbsActivityInstance;
import org.fireflow.engine.entity.runtime.impl.AbsProcessInstance;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.model.InvalidModelException;
import org.fireflow.server.support.MapConvertor;
import org.fireflow.server.support.ObjectWrapper;
import org.fireflow.server.support.PropertiesConvertor;
import org.fireflow.server.support.ScopeBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowEngineServiceInternalImpl implements
		WorkflowEngineService, RuntimeContextAware, EngineModule,
		TransactionTemplateAware {
	private static final Log log = LogFactory
			.getLog(WorkflowEngineServiceInternalImpl.class);
	
	protected static final String SESSION_CACHE = "SESSION_CACHE";
	protected RuntimeContext runtimeContext = null;
	protected int sessionToIdleSeconds = 30*60;//最大空闲时间，30分钟，
	protected int maxSessions = 50;//可并行存在的session数量
	protected TransactionTemplate springTransactionTemplate = null;
	/**
	 * 登录成功后，WorkflowSession被缓存。
	 * 目前采用EhCache实现。
	 * 
	 */
	protected CacheManager  cacheManager = null;

	
	public WorkflowEngineServiceInternalImpl(){
	}
	
	public void init(RuntimeContext runtimeContext)throws EngineException{
		try {
			cacheManager = CacheManager.create();

			/*
			 * CacheConfiguration config = new CacheConfiguration(); config
			 * .setName(SESSION_CACHE);
			 * config.setMaxElementsInMemory(maxSessions);
			 * config.setMemoryStoreEvictionPolicyFromObject
			 * (MemoryStoreEvictionPolicy.LFU); config.setOverflowToDisk(false);
			 * config.setEternal(false); config.setTimeToLiveSeconds(0);
			 * config.setTimeToIdleSeconds(sessionToIdleSeconds);
			 * config.setDiskPersistent(false);
			 */

			Cache cache = new Cache(SESSION_CACHE, maxSessions, false, false,
					0, sessionToIdleSeconds);
			cacheManager.addCache(cache);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	


	public TransactionTemplate getTransactionTemplate() {
		return springTransactionTemplate;
	}

	public void setTransactionTemplate(
			TransactionTemplate springTransactionTemplate) {
		this.springTransactionTemplate = springTransactionTemplate;
	}

	public int getMaxSessions() {
		return maxSessions;
	}

	public void setMaxSessions(int maxSessions) {
		this.maxSessions = maxSessions;
	}

	public int getSessionToIdleSeconds() {
		return sessionToIdleSeconds;
	}

	public void setSessionToIdleSeconds(int sessionToIdleSeconds) {
		this.sessionToIdleSeconds = sessionToIdleSeconds;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.server.WorkflowServer#login(java.lang.String, java.lang.String)
	 */
	public WorkflowSessionLocalImpl login(String userName, String password) throws EngineException{
		//OUSystemConnector是一个与流程语言无关的Module，
		OUSystemConnector connector = runtimeContext.getDefaultEngineModule(OUSystemConnector.class);
		if (connector==null){
			throw new EngineException("Fire Workflow 引擎没有配置正确的组织机构链接器模块。");
		}
		
		User u = connector.login(userName, password);
		if (u==null){
			throw new EngineException("用户名或者密码错误；登录Fire Workflow失败。");
		}
		
		WorkflowSessionLocalImpl session = (WorkflowSessionLocalImpl)WorkflowSessionFactory.createWorkflowSession(runtimeContext, u);
		if (cacheManager!=null){
			Cache cache = cacheManager.getCache(SESSION_CACHE);
			if (cache!=null){
				cache.put(new Element(session.getSessionId(),session));
			}
			
		}

		
		return session;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.server.WorkflowServer#uploadProcessXml(java.lang.String, org.fireflow.engine.entity.repository.ProcessDescriptor)
	 */
	@SuppressWarnings("unchecked")
	public ProcessDescriptorImpl uploadProcessXml(String sessionId,
			final String processXml,final int version)throws EngineException{
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessDescriptorImpl descriptor = (ProcessDescriptorImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status) {
				try {
					return statement.uploadProcessXml(processXml, version);
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				}
			}
			
		});
		return descriptor;
	}
	

	public String getWorkflowProcessXml(
			String sessionId,
			String processId,
			int processVersion,
			String processType)throws EngineException{
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessKey pk = new ProcessKey(processId,processVersion,processType);
		return statement.getWorkflowProcessXml(pk);
	}

	public AbsWorkflowEntity getEntity(String sessionId,String entityId,String entityClassName){

		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		
		if (entityClassName==null || entityClassName.trim().equals("")){
			return null;
		}
		if (entityId==null || entityId.trim().equals("")) {
			return null;
		}
		
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		if (ProcessInstance.class.getName().equals(entityClassName.trim())){
			return (AbsProcessInstance)statement.getEntity(entityId, ProcessInstance.class);
		}
		else if (ActivityInstance.class.getName().equals(entityClassName.trim())){
			return (AbsActivityInstance)statement.getEntity(entityId, ActivityInstance.class);
		}
		else if (WorkItem.class.getName().equals(entityClassName.trim())){
			return (AbsWorkItem)statement.getEntity(entityId, WorkItem.class);
		}
		
		//TODO 待补充
		
		return null;
	}
	
	public List<AbsWorkflowEntity> executeQueryList(String sessionId,WorkflowQueryImpl q){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();

		List result = statement.executeQueryList(q);
		
		return result;
	}
	
	public int executeQueryCount(String sessionId,
			WorkflowQueryImpl q){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();

		int i = statement.executeQueryCount(q);
		
		return i;
	}
	
	protected WorkflowSessionLocalImpl validateSession(String sessionId){
		if (cacheManager!=null){
			Cache cache = cacheManager.getCache(SESSION_CACHE);
			if (cache!=null){
				try{
					Element cacheElement = cache.get(sessionId);
					if (cacheElement==null){
						throw new EngineException("Workflow Session超时，sessionId是："+sessionId);
					}
					WorkflowSessionLocalImpl session = (WorkflowSessionLocalImpl)cacheElement.getValue();

					return session;
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}

			}

		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.server.WorkflowEngineService#createProcessInstance(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl createProcessInstance1(String sessionId,
			final String workflowProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.createProcessInstance(workflowProcessId);
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
	}

	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl createProcessInstance2(String sessionId,
			final String workflowProcessId,final int version) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.createProcessInstance(workflowProcessId,version);
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl createProcessInstance4(String sessionId,
			final String workflowProcessId,final int version,final String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.createProcessInstance(workflowProcessId,version,subProcessId);
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
	}
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl createProcessInstance3(String sessionId,
			final String workflowProcessId,final String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.createProcessInstance(workflowProcessId,subProcessId);
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
	}
	

	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl runProcessInstance(
			String sessionId,
			final String processInstanceId,
			final String bizId,
			final MapConvertor mapConvertor){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				processInstance = statement.runProcessInstance(processInstanceId, bizId, mapConvertor.getMap());

				return processInstance;
			}
			
		});
		return procInst;
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl startProcess2(
			String sessionId,final String workflowProcessId,
			final int version,final String bizId,
			final MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException{
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.startProcess(workflowProcessId,version,bizId,mapConvertor.getMap());
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
		
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl startProcess4(
			String sessionId,final String workflowProcessId,
			final int version,final String subProcessId,final String bizId,
			final MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException{
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.startProcess(workflowProcessId,version,subProcessId,bizId,mapConvertor.getMap());
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
		
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl startProcess1(
			String sessionId,final String workflowProcessId,final String bizId,
			final MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException{
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.startProcess(workflowProcessId,bizId,mapConvertor.getMap());
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
		
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl startProcess3(
			String sessionId,final String workflowProcessId,final String subProcessId,
			final String bizId,
			final MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException{
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance processInstance = null;
				try {
					processInstance = statement.startProcess(workflowProcessId,subProcessId,bizId,mapConvertor.getMap());
				} catch (InvalidModelException e) {
					throw new EngineException(e);
				} catch (WorkflowProcessNotFoundException e) {
					throw new EngineException(e);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return processInstance;
			}
			
		});
		return procInst;
	}	
	
	@SuppressWarnings("unchecked")
	public ActivityInstanceImpl  abortActivityInstance(
			String sessionId,final String activityInstanceId,
			final String note) throws InvalidOperationException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		ActivityInstanceImpl actInst = (ActivityInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ActivityInstance actInst = null;
				try {
					actInst = statement.abortActivityInstance(activityInstanceId,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return actInst;
			}
			
		});
		return actInst;
	}
	
	@SuppressWarnings("unchecked")
	public ActivityInstanceImpl  suspendActivityInstance(
			String sessionId,final String activityInstanceId,
			final String note) throws InvalidOperationException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		ActivityInstanceImpl actInst = (ActivityInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ActivityInstance actInst = null;
				try {
					actInst = statement.suspendActivityInstance(activityInstanceId,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return actInst;
			}
			
		});
		return actInst;
	}
	
	@SuppressWarnings("unchecked")
	public ActivityInstanceImpl  restoreActivityInstance(
			String sessionId,final String activityInstanceId,
			final String note) throws InvalidOperationException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		ActivityInstanceImpl actInst = (ActivityInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ActivityInstance actInst = null;
				try {
					actInst = statement.restoreActivityInstance(activityInstanceId,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return actInst;
			}
			
		});
		return actInst;
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl  abortProcessInstance(
			String sessionId,final String processInstanceId,
			final String note) throws InvalidOperationException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

	
			public Object doInTransaction(TransactionStatus status){
				ProcessInstance procInst = null;
				try {
					procInst = statement.abortProcessInstance(processInstanceId,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return procInst;
			}
			
		});
		return procInst;
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl  suspendProcessInstance(
			String sessionId,final String processInstanceId,
			final String note) throws InvalidOperationException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				ProcessInstance procInst = null;
				try {
					procInst = statement.suspendProcessInstance(processInstanceId,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return procInst;
			}
			
		});
		return procInst;
	}
	
	@SuppressWarnings("unchecked")
	public ProcessInstanceImpl  restoreProcessInstance(
			String sessionId,final String processInstanceId,
			final String note) throws InvalidOperationException {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		ProcessInstanceImpl procInst = (ProcessInstanceImpl)springTransactionTemplate.execute(new TransactionCallback(){

	
			public Object doInTransaction(TransactionStatus status){
				ProcessInstance procInst = null;
				try {
					procInst = statement.restoreProcessInstance(processInstanceId,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return procInst;
			}
			
		});
		return procInst;
	}
	
	
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl claimWorkItem(String sessionId,final String workItemId){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.claimWorkItem(workItemId);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
	}
	
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl withdrawWorkItem(String sessionId,final String workItemId){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.withdrawWorkItem(workItemId);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
	}
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl disclaimWorkItem(String sessionId,final String workItemId,
			final String attachmentId,
			final String attachmentType,final String note){
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.disclaimWorkItem(workItemId,attachmentId,attachmentType,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
		
	}
	
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl completeWorkItem1(String sessionId,final String workItemId,
			final String attachmentId,
			final String attachmentType,final String note){
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.completeWorkItem(workItemId,attachmentId,attachmentType,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
		
	}
	
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl completeWorkItem2(String sessionId,final String workItemId,
			final MapConvertor mapConvertor,
			final String attachmentId,
			final String attachmentType,final String note){
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.completeWorkItem(workItemId,mapConvertor.getMap(),attachmentId,attachmentType,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
		
	}
	
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl completeWorkItemAndJumpTo1(String sessionId,final String workItemId,
			final String targetActivityId,
			final String attachmentId,
			final String attachmentType,final String note){
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.completeWorkItemAndJumpTo(workItemId,targetActivityId,attachmentId,attachmentType,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
		
	}
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl completeWorkItemAndJumpTo2(String sessionId,final String workItemId,
			final String targetActivityId,
			final MapConvertor assignmentStrategy,
			final String attachmentId,
			final String attachmentType,final String note){
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.completeWorkItemAndJumpTo(workItemId,targetActivityId,assignmentStrategy.getMap(),attachmentId,attachmentType,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
		
	}
	
	@SuppressWarnings("unchecked")
	public LocalWorkItemImpl reassignWorkItemTo(String sessionId,final String workItemId,
			final ReassignmentHandler handler,
			final String attachmentId,
			final String attachmentType,final String note){
		
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		@SuppressWarnings("rawtypes")
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				WorkItem workItem = null;
				try {
					workItem = statement.reassignWorkItemTo(workItemId,handler,attachmentId,attachmentType,note);
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return workItem;
			}
			
		});
		return workItem;
		
	}
	
	public ObjectWrapper getVariableValue(String sessionId,ScopeBean scopeBean,
			String varName){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();

		Object result = statement.getVariableValue(scopeBean, varName);
		
		ObjectWrapper objWrapper = new ObjectWrapper();
		objWrapper.setOriginalValue(result);
		
		return objWrapper;
	}
	
	@WebMethod
	public MapConvertor getVariableValues(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="scope") ScopeBean scopeBean){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();

		Map<String,Object> vars = statement.getVariableValues(scopeBean);
		
		MapConvertor convertor = new MapConvertor();
		convertor.putAll(vars, MapConvertor.MAP_TYPE_VARIABLE);
		return convertor;
	}
	
	
	@SuppressWarnings("unchecked")
	public void setVariableValue1(String sessionId,final ScopeBean scopeBean,final String name,
			final ObjectWrapper obj){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		
		springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				try {
					statement.setVariableValue(scopeBean, name, obj==null?null:obj.getOriginalValue());
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return null;
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public void setVariableValue2(String sessionId,final ScopeBean scopeBean,final String name,
			final ObjectWrapper obj,final PropertiesConvertor convertor){
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		
		springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status){
				try {
					statement.setVariableValue(scopeBean, name, obj==null?null:obj.getOriginalValue(),
							convertor==null?null:convertor.getProperties());
				} catch(InvalidOperationException e){
					throw new EngineException(e);
				}
				return null;
			}
			
		});
	}
	
	public boolean isSessionValid(String sessionId){
		try{
			WorkflowSessionLocalImpl sessionLocalImpl = validateSession(sessionId);
			if (sessionLocalImpl!=null)return true;
			else return false;
		}catch(Exception e){
			return false;
		}
		
	}
	/* (non-Javadoc)
	 * @see org.fireflow.server.WorkflowEngineService#test(java.lang.String)
	 */
//	@Override
//	public Customer test(String name) {
//		if (name.equals("zhangsan")){
//			Customer c = new Customer();
//			Address a = new Address();
//			a.setAddress("天河东路123号");
//			c.setContactInfo(a);
//			
//			c.addContactInfo(a);
//			
//			PhoneNumber phone = new PhoneNumber();
//			phone.setNumber("186203203012");
//			c.addContactInfo(phone);
//			
//			return c;
//		}else{
//			Customer c = new Customer();
//			PhoneNumber phone = new PhoneNumber();
//			phone.setNumber("186203203012");
//			c.setContactInfo(phone);
//			return c;
//		}
//	}
//
//	public ContactInfo test2(String s){
//		if (s.equals("a")){
//			Address a = new Address();
//			a.setAddress("天河东路123号");
//			return a;
//		}else{
//			PhoneNumber phone = new PhoneNumber();
//			phone.setNumber("186203203012");
//			return phone;
//		}
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.server.WorkflowEngineService#updateProcessDescriptor(java.lang.String, org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl)
	 */
	public void updateProcessDescriptor(String sessionId,
			final ProcessDescriptorImpl processDescriptor) {
		final WorkflowSessionLocalImpl session = validateSession(sessionId);
		final WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement();
		springTransactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus status) {
				statement.updateProcessDescriptor(processDescriptor);
				return null;
			}
			
		});
		
	}
}
