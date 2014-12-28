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
package org.fireflow.engine.modules.instancemanager.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.InternalSessionAttributeKeys;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.event.EventBroadcasterManager;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbsProcessInstanceManager  extends AbsEngineModule implements ProcessInstanceManager,RuntimeContextAware{
	Log log = LogFactory.getLog(AbsProcessInstanceManager.class);
	
	protected RuntimeContext runtimeContext = null;
	public ProcessInstance runProcessInstance(WorkflowSession session,String processInstanceId,String processType,
			String bizId, Map<String, Object> variables) {
		if (processInstanceId==null || processInstanceId.trim().equals("")){
			throw new EngineException("流程实例Id不能为空");
		}
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		KernelManager kernelManager = runtimeContext.getDefaultEngineModule(KernelManager.class);
		PersistenceService persistenceStrategy = runtimeContext.getEngineModule(PersistenceService.class,processType);
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();
		ProcessPersister processPersister = persistenceStrategy.getProcessPersister();

		ProcessInstance processInstance = (ProcessInstance)localSession.getCurrentProcessInstance();
		if (processInstance==null || !processInstanceId.equals(processInstance.getId())){
			processInstance = procInstPersistenceService.find(ProcessInstance.class, processInstanceId);
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(processInstance);
		}
		
		if (processInstance==null || !ProcessInstanceState.INITIALIZED.equals(processInstance.getState())){
			return processInstance;//说明已经是运行状态，无需再次运行。
		}
		if (bizId!=null&& !bizId.trim().equals("")){
			((ProcessInstanceImpl)processInstance).setBizId(bizId);
			procInstPersistenceService.saveOrUpdate(processInstance);
		}


		ProcessRepository repository = null;
		try {
			repository = processPersister.findProcessRepositoryByProcessKey( ProcessKey.valueOf(processInstance));
		} catch (InvalidModelException e) {
			throw new EngineException(processInstance,e.getMessage());
		}
		
		initProcessInstanceVariables(processInstance,repository==null?null:repository.getProcessObject(),variables);
		

		//提取到parentToken
		Token parentToken = null;
		if (processInstance.getParentActivityInstanceId()!=null && 
				!processInstance.getParentActivityInstanceId().equals("")){
			parentToken = kernelManager.getTokenByElementInstanceId(processInstance.getParentActivityInstanceId(), processType);
		}
		
		//启动流程实例
		kernelManager.startPObject(session, new PObjectKey(processInstance.getProcessId(),
				processInstance.getVersion(),processInstance.getProcessType(),processInstance.getSubProcessId()), 
				parentToken,processInstance);
		
		if (parentToken==null){//当parentToken不为空时，kernelManager已经处于执行状态；否则需要调用kernelManager.execute(session)启动一下
			kernelManager.execute(session);
		}
		
		return processInstance;
	}
	
	protected abstract void initProcessInstanceVariables(ProcessInstance processInstance,Object subflow,Map<String,Object> initVariables);
	
	public void changeProcessInstanceSate(WorkflowSession session,ProcessInstance procInst,ProcessInstanceState state,Object workflowElement){
		CalendarService calendarService = runtimeContext.getEngineModule(CalendarService.class,procInst.getProcessType());
		PersistenceService persistenceStrategy = runtimeContext.getEngineModule(PersistenceService.class, procInst.getProcessType());
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();

		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		//从session取相关字段的值
		//TODO 是否需要state.getValue()==ProcessInstanceState.ABORTED.getValue() 这个判断
		if (state.getValue()==ProcessInstanceState.ABORTED.getValue()){
			Map<EntityProperty,Object> fieldsValues = (Map<EntityProperty,Object>)localSession.getAttribute(InternalSessionAttributeKeys.FIELDS_VALUES);
			if (fieldsValues!=null){
				String note = (String)fieldsValues.get(ProcessInstanceProperty.NOTE);
				if (!StringUtils.isEmpty(note)){
					((ProcessInstanceImpl)procInst).setNote(note);
				}
			}
		}

		
		((ProcessInstanceImpl)procInst).setState(state);
		if (state.getValue()>ProcessInstanceState.DELIMITER.getValue()){
			((ProcessInstanceImpl)procInst).setEndTime(calendarService.getSysDate());
		}
		procInstPersistenceService.saveOrUpdate(procInst);
		
		if (state.getValue() > ProcessInstanceState.DELIMITER.getValue()) {
			// 发布AFTER_PROCESS_INSTANCE_END事件
			this.fireProcessInstanceEvent(session, procInst,
					workflowElement, ProcessInstanceEventTrigger.AFTER_PROCESS_INSTANCE_END);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#abortProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
//	public ProcessInstance abortProcessInstance(WorkflowSession session,
//			ProcessInstance processInstance) {		
//		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
//		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		
//		//1、首先abort相应的activityInstance
//		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
//		List<ActivityInstance> activityInstanceList = actInstPersister.findActivityInstances(processInstance.getId());
//		ActivityInstanceManager actInstMgr = context.getEngineModule(ActivityInstanceManager.class, processInstance.getProcessType());
//		if(activityInstanceList!=null){
//			for (ActivityInstance activityInstance : activityInstanceList){
//				if (activityInstance.getState().getValue()<ActivityInstanceState.DELIMITER.getValue()){
//					actInstMgr.abortActivityInstance(session, activityInstance);
//				}
//			}
//		}
//		
//		//2、然后abort processInstance
//		CalendarService calendarService = context.getEngineModule(CalendarService.class, processInstance.getProcessType());
//		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
//		((ProcessInstanceImpl)processInstance).setState(ProcessInstanceState.ABORTED);
//		((ProcessInstanceImpl)processInstance).setEndTime(calendarService.getSysDate());
//		persister.saveOrUpdate(processInstance);
		
//		KernelManager kernelManager = context.getDefaultEngineModule(KernelManager.class);		
//		Token token = kernelManager.getToken(processInstance.getTokenId(), processInstance.getProcessType());
//
//		kernelManager.fireTerminationEvent(session, token, null);
//
//		return processInstance;
//	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#restoreProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
	public ProcessInstance restoreProcessInstance(WorkflowSession session,
			ProcessInstance processInstance) {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		
		//1、首先restore相应的activityInstance
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		List<ActivityInstance> activityInstanceList = actInstPersister.findActivityInstances(processInstance.getId());
		ActivityInstanceManager actInstMgr = context.getEngineModule(ActivityInstanceManager.class, processInstance.getProcessType());
		if(activityInstanceList!=null){
			for (ActivityInstance activityInstance : activityInstanceList){
				if (activityInstance.getState().getValue()<ActivityInstanceState.DELIMITER.getValue()){
					actInstMgr.restoreActivityInstance(session, activityInstance);
				}
			}
		}
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		//2、然后restore processInstance
		//从session取相关字段的值
		Map<EntityProperty,Object> fieldsValues = (Map<EntityProperty,Object>)localSession.getAttribute(InternalSessionAttributeKeys.FIELDS_VALUES);
		if (fieldsValues!=null){
			String note = (String)fieldsValues.get(ProcessInstanceProperty.NOTE);
			if (!StringUtils.isEmpty(note)){
				((ProcessInstanceImpl)processInstance).setNote(note);
			}
		}
		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
		((ProcessInstanceImpl)processInstance).setSuspended(false);
		persister.saveOrUpdate(processInstance);
		
		//触发事件
		try{
			WorkflowStatement stmt = localSession.createWorkflowStatement();
			Object thisSubProcess = stmt.getWorkflowDefinitionElement(processInstance);
			this.fireProcessInstanceEvent(localSession, processInstance, thisSubProcess, ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_RESTORED);
			
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		return processInstance;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#suspendProcessInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance)
	 */
	public ProcessInstance suspendProcessInstance(WorkflowSession session,
			ProcessInstance processInstance) {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		//1、首先suspend相应的activityInstance
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		List<ActivityInstance> activityInstanceList = actInstPersister.findActivityInstances(processInstance.getId());
		ActivityInstanceManager actInstMgr = context.getEngineModule(ActivityInstanceManager.class, processInstance.getProcessType());
		if(activityInstanceList!=null){
			for (ActivityInstance activityInstance : activityInstanceList){
				if (activityInstance.getState().getValue()<ActivityInstanceState.DELIMITER.getValue()){
					actInstMgr.suspendActivityInstance(session, activityInstance);
				}
			}
		}
		
		//2、然后suspend processInstance
		//从session取相关字段的值
		Map<EntityProperty,Object> fieldsValues = (Map<EntityProperty,Object>)localSession.getAttribute(InternalSessionAttributeKeys.FIELDS_VALUES);
		if (fieldsValues!=null){
			String note = (String)fieldsValues.get(ProcessInstanceProperty.NOTE);
			if (!StringUtils.isEmpty(note)){
				((ProcessInstanceImpl)processInstance).setNote(note);
			}
		}
		ProcessInstancePersister persister = persistenceService.getProcessInstancePersister();
		((ProcessInstanceImpl)processInstance).setSuspended(true);
		persister.saveOrUpdate(processInstance);
		
		//触发事件
		try{
			WorkflowStatement stmt = localSession.createWorkflowStatement();
			Object thisSubProcess = stmt.getWorkflowDefinitionElement(processInstance);
			this.fireProcessInstanceEvent(localSession, processInstance, thisSubProcess, ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_SUSPENDED);
			
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}

		return processInstance;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;		
	}
	
	public void fireProcessInstanceEvent(WorkflowSession session,ProcessInstance procInst,Object subflow,ProcessInstanceEventTrigger eventType){
		try{
			WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
			RuntimeContext rtCtx = sessionLocalImpl.getRuntimeContext();
			EventBroadcasterManager evetBroadcasterMgr = rtCtx.getDefaultEngineModule(EventBroadcasterManager.class);
			
			EventBroadcaster broadcaster = evetBroadcasterMgr.getEventBroadcaster(ProcessInstanceEvent.class.getName());
			if (broadcaster!=null){
				ProcessInstanceEvent event = new ProcessInstanceEvent();
				event.setSource(procInst);
				event.setEventTrigger(eventType);
				event.setWorkflowElement(subflow);
				event.setCurrentWorkflowSession(sessionLocalImpl);
				
				broadcaster.fireEvent(sessionLocalImpl, event);
			}
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}

	}
}
