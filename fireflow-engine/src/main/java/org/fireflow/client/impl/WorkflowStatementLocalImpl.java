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
package org.fireflow.client.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.WorkflowQueryDelegate;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.config.FireflowConfig;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.LocalWorkItem;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.Persister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.JavaDataTypeConvertor;
import org.firesoa.common.util.Utils;

/**
 * @author 非也
 * @version 2.0
 */
public class WorkflowStatementLocalImpl implements WorkflowStatement,
		WorkflowQueryDelegate {
	WorkflowSessionLocalImpl session = null;
	Map<String, Object> attributes = new HashMap<String, Object>();
	protected String processType = null;

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Object execute(StatementCallback callback) {
		session.clearAttributes();
		session.setAllAttributes(attributes);
		return callback.doInStatement(session);
	}

	public WorkflowStatementLocalImpl(WorkflowSessionLocalImpl s) {
		this.session = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#setDynamicAssignmentHandler(java
	 * .lang.String, org.fireflow.engine.service.human.AssignmentHandler)
	 */
	public WorkflowStatement setDynamicAssignmentHandler(String activityId,
			AssignmentHandler dynamicAssignmentHandler) {
		this.session.setDynamicAssignmentHandler(activityId,
				dynamicAssignmentHandler);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#getCurrentActivityInstance()
	 */
	public ActivityInstance getCurrentActivityInstance() {
		return this.session.getCurrentActivityInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#getCurrentProcessInstance()
	 */
	public ProcessInstance getCurrentProcessInstance() {
		return this.session.getCurrentProcessInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#getLatestCreatedWorkItems()
	 */
	public List<WorkItem> getLatestCreatedWorkItems() {
		return this.session.getLatestCreatedWorkItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.api.WorkflowStatement#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	public WorkflowStatement setAttribute(String name, Object attr) {
		attributes.put(name, attr);
		return this;
	}

	/******************************************************************************/
	/************                                                        **********/
	/************ 与process instance 相关的API **********/
	/************                                                        **********/
	/************                                                        **********/
	/******************************************************************************/
	public ProcessInstance startProcess(String workflowProcessId, int version,
			String subProcessId, String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		ProcessInstance processInstance = this.createProcessInstance(
				workflowProcessId, version, subProcessId);
		return this.runProcessInstance(processInstance.getId(), bizId,
				variables);
	}

	public ProcessInstance startProcess(String workflowProcessId,
			String subProcessId, String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		ProcessInstance processInstance = this.createProcessInstance(
				workflowProcessId, subProcessId);
		// System.out.println("=============Server端收到的流程变量是===");
		// if (variables!=null){
		// Iterator<String> keys = variables.keySet().iterator();
		// while(keys.hasNext()){
		// String key = keys.next();
		// Object value = variables.get(key);
		// System.out.println(key+"===="+value);
		// }
		// }
		// System.out.println("=============Server端收到的流程变量如上");
		return this.runProcessInstance(processInstance.getId(), bizId,
				variables);

	}

	public ProcessInstance startProcess(String workflowProcessId, int version,
			String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		ProcessInstance processInstance = this.createProcessInstance(
				workflowProcessId, version);
		return this.runProcessInstance(processInstance.getId(), bizId,
				variables);
		// RuntimeContext ctx = this.session.getRuntimeContext();
		//
		//
		// ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class,
		// processType);
		//
		// session.setAttribute(InternalSessionAttributeKeys.BIZ_ID, bizId);
		// session.setAttribute(InternalSessionAttributeKeys.VARIABLES,
		// variables);
		// RuntimeContext context =
		// ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		// KernelManager kernelManager =
		// context.getDefaultEngineModule(KernelManager.class);
		// //启动WorkflowProcess实际上是启动该WorkflowProcess的main_flow，
		// kernelManager.startPObject(session, new
		// PObjectKey(workflowProcessId,version,processType,
		// processUtil.getProcessEntryId(workflowProcessId, version,
		// workflowProcessId)));
		//
		// return session.getCurrentProcessInstance();

	}

	public ProcessInstance startProcess(String workflowProcessId, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		// 首先需要根据workflowProcessId找到待启动的流程，查找策略有多种，可能根据流程族来查找，也可能直接找到当前最新版本的流程。
		// RuntimeContext runtimeContext = this.session.getRuntimeContext();
		// ProcessLoadStrategy loadStrategy = runtimeContext.getEngineModule(
		// ProcessLoadStrategy.class, this.getProcessType());
		//
		// ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(session,
		// workflowProcessId, this.getProcessType());
		// return this.startProcess(workflowProcessId, pk.getVersion(), bizId,
		// variables);
		ProcessInstance processInstance = this
				.createProcessInstance(workflowProcessId);
		return this.runProcessInstance(processInstance.getId(), bizId,
				variables);
	}

	public ProcessInstance startProcess(Object process, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		ProcessInstance processInstance = this.createProcessInstance(process);
		return this.runProcessInstance(processInstance.getId(), bizId,
				variables);
		// ProcessDescriptor repository = this.uploadProcessObject(process,
		// Boolean.TRUE, null);
		// return this.startProcess(repository.getProcessId(),
		// repository.getVersion(), bizId, variables);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#abortProcessInstance(java.lang.
	 * String, java.lang.String)
	 */
	public ProcessInstance abortProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ProcessInstance processInstance = this.getEntity(processInstanceId,
				ProcessInstance.class);
		if (processInstance == null) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is not found.");
		}
		if (processInstance.getState().getValue() > ProcessInstanceState.DELIMITER
				.getValue()
				|| processInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is dead.");
		}

		resetSession(this.session);// 先清理session
		if (!StringUtils.isEmpty(note)) {
			Map<EntityProperty, Object> fieldsValues = new HashMap<EntityProperty, Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES,
					fieldsValues);
		}
		session.setCurrentProcessInstance(processInstance);

		KernelManager kernelManager = ctx
				.getDefaultEngineModule(KernelManager.class);
		Token token = kernelManager.getTokenById(processInstance.getTokenId(),
				processInstance.getProcessType());

		kernelManager.fireTerminationEvent(session, token, null);
		kernelManager.execute(session);

		return processInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#restoreProcessInstance(java.lang
	 * .String, java.lang.String)
	 */
	public ProcessInstance restoreProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ProcessInstance processInstance = this.getEntity(processInstanceId,
				ProcessInstance.class);
		if (processInstance == null) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is not found.");
		}
		if (processInstance.getState().getValue() > ProcessInstanceState.DELIMITER
				.getValue()
				|| processInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is dead.");
		}

		this.resetSession(session);
		if (!StringUtils.isEmpty(note)) {
			Map<EntityProperty, Object> fieldsValues = new HashMap<EntityProperty, Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES,
					fieldsValues);
		}
		session.setCurrentProcessInstance(processInstance);

		ProcessInstanceManager procInstMgr = ctx.getEngineModule(
				ProcessInstanceManager.class, this.processType);
		procInstMgr.restoreProcessInstance(session, processInstance);
		return processInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#suspendProcessInstance(java.lang
	 * .String, java.lang.String)
	 */
	public ProcessInstance suspendProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ProcessInstance processInstance = this.getEntity(processInstanceId,
				ProcessInstance.class);
		if (processInstance == null) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is not found.");
		}
		if (processInstance.getState().getValue() > ProcessInstanceState.DELIMITER
				.getValue()
				|| processInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Process instance for id="
					+ processInstanceId + " is dead.");
		}

		this.resetSession(session);
		if (!StringUtils.isEmpty(note)) {
			Map<EntityProperty, Object> fieldsValues = new HashMap<EntityProperty, Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES,
					fieldsValues);
		}

		session.setCurrentProcessInstance(processInstance);

		ProcessInstanceManager procInstMgr = ctx.getEngineModule(
				ProcessInstanceManager.class, this.processType);
		procInstMgr.suspendProcessInstance(session, processInstance);
		return processInstance;
	}

	/******************************************************************************/
	/************                                                        **********/
	/************ ActivityInstance相关的 API **********/
	/************                                                        **********/
	/************                                                        **********/
	/******************************************************************************/

	public ActivityInstance suspendActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ActivityInstance activityInstance = this.getEntity(activityInstanceId,
				ActivityInstance.class);
		if (activityInstance == null) {
			throw new InvalidOperationException("Activity instance for id="
					+ activityInstanceId + " is not found.");
		}
		if (activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
				.getValue()
				|| activityInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Activiy instance for id="
					+ activityInstanceId + " is dead.");
		}
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)) {
			Map<EntityProperty, Object> fieldsValues = new HashMap<EntityProperty, Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES,
					fieldsValues);
		}

		session.setCurrentActivityInstance(activityInstance);

		ActivityInstanceManager actInstMgr = ctx.getEngineModule(
				ActivityInstanceManager.class, this.processType);
		actInstMgr.suspendActivityInstance(session, activityInstance);
		return activityInstance;
	}

	public ActivityInstance restoreActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ActivityInstance activityInstance = this.getEntity(activityInstanceId,
				ActivityInstance.class);
		if (activityInstance == null) {
			throw new InvalidOperationException("Activity instance for id="
					+ activityInstanceId + " is not found.");
		}
		if (activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
				.getValue()
				|| activityInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Activiy instance for id="
					+ activityInstanceId + " is dead.");
		}
		this.resetSession(session);
		if (!StringUtils.isEmpty(note)) {
			Map<EntityProperty, Object> fieldsValues = new HashMap<EntityProperty, Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES,
					fieldsValues);
		}

		session.setCurrentActivityInstance(activityInstance);

		ActivityInstanceManager actInstMgr = ctx.getEngineModule(
				ActivityInstanceManager.class, this.processType);
		actInstMgr.restoreActivityInstance(session, activityInstance);
		return activityInstance;
	}

	public ActivityInstance abortActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();
		ActivityInstance activityInstance = this.getEntity(activityInstanceId,
				ActivityInstance.class);
		if (activityInstance == null) {
			throw new InvalidOperationException("Activity instance for id="
					+ activityInstanceId + " is not found.");
		}
		if (activityInstance.getState().getValue() > ActivityInstanceState.DELIMITER
				.getValue()
				|| activityInstance instanceof ProcessInstanceHistory) {
			throw new InvalidOperationException("Activiy instance for id="
					+ activityInstanceId + " is dead.");
		}

		this.resetSession(session);
		if (!StringUtils.isEmpty(note)) {
			Map<EntityProperty, Object> fieldsValues = new HashMap<EntityProperty, Object>();
			fieldsValues.put(ProcessInstanceProperty.NOTE, note);
			session.setAttribute(InternalSessionAttributeKeys.FIELDS_VALUES,
					fieldsValues);
		}
		session.setCurrentActivityInstance(activityInstance);

		KernelManager kernelManager = ctx
				.getDefaultEngineModule(KernelManager.class);
		Token token = kernelManager.getTokenById(activityInstance.getTokenId(),
				activityInstance.getProcessType());

		kernelManager.fireTerminationEvent(session, token, null);
		kernelManager.execute(session);

		return activityInstance;
	}

	/******************************************************************************/
	/************                                                        **********/
	/************ workItem 相关的API **********/
	/************                                                        **********/
	/************                                                        **********/
	/******************************************************************************/

	// 该方法不需要，2012-11-10
	// public void updateWorkItem(WorkItem workItem)
	// throws InvalidOperationException {
	// if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {
	// throw new InvalidOperationException(
	// "Can not update the work item ,it is not in Running State.");
	// }
	// RuntimeContext rtCtx = this.session.getRuntimeContext();
	//
	// PersistenceService persistenceService = rtCtx.getEngineModule(
	// PersistenceService.class, processType);
	// WorkItemPersister workItemPersister = persistenceService
	// .getWorkItemPersister();
	// workItemPersister.saveOrUpdate(workItem);
	// }

	public WorkItem claimWorkItem(String workItemId)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.fetch(WorkItem.class, workItemId);

		return claimWorkItem(workItem);
	}

	private WorkItem claimWorkItem(WorkItem wi)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();
		if (wi == null)
			throw new InvalidOperationException(
					"Claim work item failed. The work item is not found ,maybe ,it is claimed by others.");
		if (wi.getState().getValue() != WorkItemState.INITIALIZED
				.getValue()) {

			throw new InvalidOperationException(
					"Claim work item failed. The state of the work item is "
							+ wi.getState().getValue() + "("
							+ wi.getState().getDisplayName() + ")");
		}
		if (!(wi instanceof LocalWorkItem)){
			throw new InvalidOperationException(
					"该工作项是通过SOA方案集成的RemoteWorkItem，不能通过本方法执行签收操作。 ");

		}
		
		LocalWorkItem workItem = (LocalWorkItem)wi;

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();

		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Claim work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Claim work item failed .The  correspond activity instance is suspended");
		}

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.claimWorkItem(session, workItem);
	}

	public WorkItem disclaimWorkItem(String workItemId, String attachmentId,
			String attachmentType, String note)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem wi = workItemPersister.fetch(WorkItem.class, workItemId);

		if (wi == null)
			throw new InvalidOperationException(
					"退签收失败，没有找到id=["+workItemId+"]的工作项。");
		
		if (!(wi instanceof LocalWorkItem)){
			throw new InvalidOperationException(
					"该工作项是通过SOA方案集成的RemoteWorkItem，不能通过本方法执行退签收操作。 ");

		}
		
		LocalWorkItem workItem = (LocalWorkItem)wi;

		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Disclaim work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Dislaim work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Disclaim work item failed .The  correspond activity instance is suspended");
		}
		// 将审批意见等信息写入workItem
		workItem.setAttachmentId(attachmentId);
		workItem.setAttachmentType(attachmentType);
		workItem.setNote(note);

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.disclaimWorkItem(session, workItem);
	}

	public WorkItem withdrawWorkItem(String workItemId)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem workItem = workItemPersister.fetch(WorkItem.class, workItemId);

		if (workItem == null)
			throw new InvalidOperationException(
					"取回工作项失败，没有找到Id=["+workItemId+"]的工作项");
		if (!(workItem instanceof LocalWorkItem)){
			throw new InvalidOperationException(
					"该工作项是通过SOA方案集成的RemoteWorkItem，不能通过本方法执行取回操作。 ");

		}
		if (workItem.getState().getValue() < WorkItemState.DELIMITER.getValue()) {

			throw new InvalidOperationException(
					"Withdraw work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.withdrawWorkItem(session, workItem);
	}

	public WorkItem completeWorkItem(String workItemId,
			Map<String, AssignmentHandler> assignmentStrategy,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		if (assignmentStrategy != null) {
			this.session.dynamicAssignmentHandlers.putAll(assignmentStrategy);
		}
		return completeWorkItem(workItemId, attachmentId, attachmentType, note);
	}

	public WorkItem completeWorkItem(String workItemId, String attachmentId,
			String attachmentType, String note)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem wi = workItemPersister.fetch(WorkItem.class, workItemId);

		if (wi == null)
			throw new InvalidOperationException(
					"结束工作项操作失败，没有找到id=["+workItemId+"]的工作项。");
		if (!(wi instanceof LocalWorkItem)){
			throw new InvalidOperationException(
					"该工作项是通过SOA方案集成的RemoteWorkItem，不能通过本方法执行结束工作项的操作。 ");

		}
		LocalWorkItem workItem = (LocalWorkItem)wi;
		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Complete work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Complete work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Complete work item failed .The  correspond activity instance is suspended");
		}

		// 将审批意见等信息写入workItem
		// 将审批意见等信息写入workItem
		workItem.setAttachmentId(attachmentId);
		workItem.setAttachmentType(attachmentType);
		workItem.setNote(note);

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.completeWorkItem(session, workItem);
	}

	public WorkItem completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,
			Map<String, AssignmentHandler> assignmentStrategy,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		if (assignmentStrategy != null) {
			this.session.dynamicAssignmentHandlers.putAll(assignmentStrategy);
		}
		return this.completeWorkItemAndJumpTo(workItemId, targetActivityId,
				attachmentId, attachmentType, note);
	}

	public WorkItem completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId, String attachmentId,
			String attachmentType, String note)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem wi = workItemPersister.fetch(WorkItem.class, workItemId);

		if (wi == null)
			throw new InvalidOperationException(
					"Complete work item failed. The work item is not found .");

		if (!(wi instanceof LocalWorkItem)){
			throw new InvalidOperationException(
					"该工作项是通过SOA方案集成的RemoteWorkItem，不能通过本方法执行结束工作项的操作。 ");

		}
		
		LocalWorkItem workItem = (LocalWorkItem)wi;
		
		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Complete work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Complete work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Complete work item failed .The  correspond activity instance is suspended");
		}

		// 将审批意见等信息写入workItem
		workItem.setAttachmentId(attachmentId);
		workItem.setAttachmentType(attachmentType);
		workItem.setNote(note);

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		return workItemMgr.completeWorkItemAndJumpTo(session, workItem,
				targetActivityId);
	}

	public WorkItem reassignWorkItemTo(String workItemId,
			ReassignmentHandler dynamicAssignmentHandler, String attachmentId,
			String attachmentType, String note)
			throws InvalidOperationException {
		RuntimeContext rtCtx = this.session.getRuntimeContext();

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		WorkItemPersister workItemPersister = persistenceService
				.getWorkItemPersister();

		WorkItem wi = workItemPersister.fetch(WorkItem.class, workItemId);

		if (dynamicAssignmentHandler == null) {
			throw new NullPointerException(
					"DynamicAssignmentHandler can NOT be null");
		}

		if (wi == null)
			throw new InvalidOperationException(
					"Complete work item failed. The work item is not found .");

		if (!(wi instanceof LocalWorkItem)){
			throw new InvalidOperationException(
					"该工作项是通过SOA方案集成的RemoteWorkItem，不能通过本方法执行委派工作项的操作。 ");

		}
		
		LocalWorkItem workItem = (LocalWorkItem)wi;
		
		ActivityInstance activityInstance = (ActivityInstance) workItem
				.getActivityInstance();
		if (workItem.getState().getValue() != WorkItemState.RUNNING.getValue()) {

			throw new InvalidOperationException(
					"Complete work item failed. The state of the work item is "
							+ workItem.getState().getValue() + "("
							+ workItem.getState().getDisplayName() + ")");
		}
		if (activityInstance.getState().getValue() != ActivityInstanceState.INITIALIZED
				.getValue()
				&& activityInstance.getState().getValue() != ActivityInstanceState.RUNNING
						.getValue()) {
			throw new InvalidOperationException(
					"Complete work item failed .The state of the correspond activity instance is "
							+ activityInstance.getState() + "("
							+ activityInstance.getState().getDisplayName()
							+ ")");
		}

		if (activityInstance.isSuspended()) {
			throw new InvalidOperationException(
					"Complete work item failed .The  correspond activity instance is suspended");
		}

		WorkItemManager workItemMgr = rtCtx.getEngineModule(
				WorkItemManager.class, this.processType);
		ProcessLanguageManager processUtil = rtCtx.getEngineModule(ProcessLanguageManager.class,
				this.processType);
		ProcessKey pKey = new ProcessKey(activityInstance.getProcessId(),
				activityInstance.getVersion(),
				activityInstance.getProcessType());

		Object theActivity = null;
		ServiceBinding serviceBinding = null;
		ResourceBinding resourceBinding = null;
		try {
			theActivity = this.getWorkflowDefinitionElement(activityInstance);

			serviceBinding = processUtil.getServiceBinding(theActivity);

			resourceBinding = processUtil.getResourceBinding(theActivity);
		} catch (InvalidModelException e) {
			throw new InvalidOperationException(e);
		}

		// 将审批意见等信息写入workItem
		workItem.setAttachmentId(attachmentId);
		workItem.setAttachmentType(attachmentType);
		workItem.setNote(note);

		workItemMgr.reassignWorkItemTo(session, workItem,
				dynamicAssignmentHandler, theActivity, serviceBinding,
				resourceBinding);

		return workItem;
	}

	/*****************************************************************/
	/*************** 流程定义相关的api ******************************/
	/*****************************************************************/
	private ProcessDescriptor _uploadProcess(Object process, String processXml,
			int version) {
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class, processType);

		ProcessDescriptor descriptor = null;
		descriptor = createNewProcessDescriptor(ctx,process);
		
		if(version>0){//做更新操作，先查询楚原有的ProcessDescriptor，如果没有，仍然做插入操作。
			ProcessDescriptor oldDescriptor = processPersister.findProcessDescriptorByProcessKey(
					new ProcessKey(descriptor.getProcessId(),version,processType));
			if (oldDescriptor!=null){
				((ProcessDescriptorImpl) oldDescriptor).setHasCallbackService(descriptor.getHasCallbackService());
				((ProcessDescriptorImpl) oldDescriptor).setTimerStart(descriptor.getTimerStart());
				((ProcessDescriptorImpl) oldDescriptor).setPackageId(descriptor.getPackageId());
				((ProcessDescriptorImpl) oldDescriptor).setDisplayName(descriptor.getDisplayName());
				((ProcessDescriptorImpl) oldDescriptor).setDescription(descriptor.getDescription());
				((ProcessDescriptorImpl) oldDescriptor).setLastUpdateTime(calendarService.getSysDate());
				
				descriptor = oldDescriptor;//覆盖已有的流程
			}else{
				((ProcessDescriptorImpl) descriptor).setVersion(version);//插入指定版本的流程
			}
		}
		
		((ProcessDescriptorImpl) descriptor).setLastEditor(this.session // 最后修改人
				.getCurrentUser().getId() + "["
				+ this.session.getCurrentUser().getName() + "]");
		

		return processPersister.persistProcessToRepository(processXml,
				descriptor);
	}
	
	private ProcessDescriptor createNewProcessDescriptor(RuntimeContext ctx,Object process){
		ProcessLanguageManager processLanguageManager = ctx.getEngineModule(ProcessLanguageManager.class,
				processType);
		
		
		// 通过ProcessUtil已经生成了ProcessId,ProcessName等8个属性，Id,version属性需要自动生成
		ProcessDescriptor descriptor = processLanguageManager.generateProcessDescriptor(process);
		
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		
		int v = processPersister.findTheLatestVersion(descriptor.getProcessId(), descriptor.getProcessType());
		((ProcessDescriptorImpl) descriptor).setVersion(v+1);
		
		return descriptor;
	}

	public ProcessDescriptor uploadProcessXml(String processXml,int version)
			throws InvalidModelException {
		if (processXml == null)
			throw new InvalidModelException("流程定义文件不能为空。");

		// 识别字符集
		String encoding = Utils.findXmlCharset(processXml);

		RuntimeContext ctx = this.session.getRuntimeContext();
		ByteArrayInputStream byteIn = null;
		try {
			byteIn = new ByteArrayInputStream(processXml.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new InvalidModelException(e);
		}
		ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class,
				processType);
		Object processObj = processUtil.deserializeXml2Process(byteIn);

		return this._uploadProcess(processObj, processXml, version);
	}
	
	public void updateProcessDescriptor(ProcessDescriptor processDescriptor){
		if (processDescriptor==null ){
			NullPointerException e = new NullPointerException("入口参数ProcessDescriptor为空！");
		}
		if (processDescriptor.getVersion()<=0){
			EngineException e = new EngineException("ProcessDescriptor的version字段值不能为0或者负数。");
		}
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		
		CalendarService calendarService = ctx.getEngineModule(CalendarService.class, processType);

		((ProcessDescriptorImpl) processDescriptor).setLastUpdateTime(calendarService.getSysDate());
		((ProcessDescriptorImpl) processDescriptor).setLastEditor(this.session // 最后修改人
				.getCurrentUser().getId() + "["
				+ this.session.getCurrentUser().getName() + "]");
		
		processPersister.saveOrUpdate(processDescriptor);		
		
	}

	public ProcessDescriptor uploadProcessObject(Object process,int version)
			throws InvalidModelException {
		RuntimeContext ctx = this.session.getRuntimeContext();

		ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class,
				processType);

		String processXml = processUtil.serializeProcess2Xml(process);

		return this._uploadProcess(process, processXml,version);
	}

	public ProcessDescriptor uploadProcessStream(InputStream stream,int version)
			throws InvalidModelException {
		InputStream inStream = stream;
		if (!inStream.markSupported()) {
			inStream = new BufferedInputStream(stream);
		}

		RuntimeContext ctx = this.session.getRuntimeContext();

		ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class,
				processType);

		// 读取Xml字符串
		String processXml = null;
		Object processObject = null;
		try {
			String charset = Utils.findXmlCharset(inStream);
			if (charset == null || charset.trim().equals("")) {
				charset = "UTF-8";
			}
			processXml = Utils.inputStream2String(inStream, charset);
			// 如果存在Process间的交叉引用，此处可能会死锁，报错
			// WorkflowProcess 之间的调用，不考虑使用Import机制，2012-04-30
			if (processXml != null) {
				ByteArrayInputStream byteArrayInStream = new ByteArrayInputStream(
						processXml.getBytes(charset));
				processObject = processUtil
						.deserializeXml2Process(byteArrayInStream);
			}

		} catch (IOException e) {
			throw new InvalidModelException(e);
		}

		return this._uploadProcess(processObject, processXml, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#uploadResources(java.io.InputStream
	 * , java.util.Map)
	 */
	/*
	public List<ResourceDescriptor> uploadResourcesStream(InputStream inStream,
			Boolean publishState,
			Map<ResourceDescriptorProperty, Object> resourceDescriptorKeyValue)
			throws InvalidModelException {
		Map<ResourceDescriptorProperty, Object> props = new HashMap<ResourceDescriptorProperty, Object>();

		if (resourceDescriptorKeyValue != null) {
			props.putAll(resourceDescriptorKeyValue);
		}
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		ResourcePersister resourcePersister = persistenceService
				.getResourcePersister();
		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, this.processType);
		props.put(ResourceDescriptorProperty.LAST_EDITOR, this.session
				.getCurrentUser().getId()
				+ "["
				+ this.session.getCurrentUser().getName() + "]");
		props.put(ResourceDescriptorProperty.LAST_EDIT_TIME,
				calendarService.getSysDate());
		props.put(ResourceDescriptorProperty.PUBLISH_STATE, publishState);

		try {
			List<ResourceDescriptor> descriptors = resourcePersister
					.persistResourceFileToRepository(inStream, props);
			return descriptors;
		} catch (DeserializerException e) {
			throw new InvalidModelException(e);
		}
	}
	*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#uploadServices(java.io.InputStream,
	 * java.util.Map)
	 */
	/*
	public List<ServiceDescriptor> uploadServicesStream(InputStream inStream,
			Boolean publishState,
			Map<ServiceDescriptorProperty, Object> serviceDescriptorKeyValue)
			throws InvalidModelException {
		Map<ServiceDescriptorProperty, Object> props = new HashMap<ServiceDescriptorProperty, Object>();

		if (serviceDescriptorKeyValue != null) {
			props.putAll(serviceDescriptorKeyValue);
		}
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		ServicePersister servicePersister = persistenceService
				.getServicePersister();
		CalendarService calendarService = ctx.getEngineModule(
				CalendarService.class, this.processType);
		props.put(ServiceDescriptorProperty.LAST_EDITOR, this.session
				.getCurrentUser().getId()
				+ "["
				+ this.session.getCurrentUser().getName() + "]");
		props.put(ServiceDescriptorProperty.LAST_EDIT_TIME,
				calendarService.getSysDate());
		props.put(ServiceDescriptorProperty.PUBLISH_STATE, publishState);
		try {
			List<ServiceDescriptor> descriptors = servicePersister
					.persistServiceFileToRepository(inStream, props);
			return descriptors;
		} catch (DeserializerException e) {
			throw new InvalidModelException(e);
		}
	}
	*/

	/*****************************************************************/
	/*************** 流程变量相关的api ******************************/
	/*****************************************************************/

	public Object getVariableValue(Scope scope, String name) {
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		VariablePersister variablePersister = persistenceService
				.getVariablePersister();

		Variable var = variablePersister.findVariable(scope.getScopeId(), name);
		if (var != null) {
			return var.getPayload();
		} else {
			return null;
		}

	}

	public void setVariableValue(Scope scope, String name, Object value)
			throws InvalidOperationException {
		this.setVariableValue(scope, name, value, null);
	}

	public void setVariableValue(Scope scope, String name, Object value,
			Properties headers) throws InvalidOperationException {
		RuntimeContext ctx = this.session.getRuntimeContext();

		// 进行流程变量的类型校验，如果流程定义中有名称为参数“name”的变量定义，则value的类型必须和变量的类型相匹配。
		ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class,
				scope.getProcessType());

		Property property = null;
		try {
			Object wfDefElm = this.getWorkflowDefinitionElement(scope);
			property = processUtil.getProperty(wfDefElm, name);
		} catch (InvalidModelException e) {
			throw new InvalidOperationException(e);
		}

		// 检查变量值和变量类型是否一致
		if (property != null && property.getDataType() != null && value != null) {
			QName qName = property.getDataType();
			// java类型
			if (qName.getNamespaceURI().endsWith(NameSpaces.JAVA.getUri())) {
				String className = qName.getLocalPart();

				if (value instanceof org.w3c.dom.Document
						|| value instanceof org.dom4j.Document) {
					throw new ClassCastException("Can NOT cast from DOM to "
							+ className);
				}

				try {
					if (!JavaDataTypeConvertor.isTypeValueMatch(className,
							value)) {
						throw new InvalidOperationException("设置流程变量失败，变量所需类型为"
								+ className + ",而设置值的类型是"
								+ value.getClass().getName());
					}
				} catch (ClassNotFoundException e) {
					throw new InvalidOperationException(e);
				}

			}
			// xml类型
			else {
				if (!(value instanceof org.w3c.dom.Document)
						&& !(value instanceof org.dom4j.Document)) {
					throw new ClassCastException("Can NOT cast from "
							+ value.getClass().getName() + " to " + qName);
				}

			}
		}
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		VariablePersister variablePersister = persistenceService
				.getVariablePersister();
		Variable v = variablePersister.findVariable(scope.getScopeId(), name);
		// System.out.println("===已经存在同名变量==name="+v.getName()+";新的值是"+value);
		if (v != null) {
			((AbsVariable) v).setPayload(value);
			if (headers != null && headers.size() > 0) {
				v.getHeaders().putAll(headers);
			}
			variablePersister.saveOrUpdate(v);
		} else {
			v = new VariableImpl();
			((AbsVariable) v).setScopeId(scope.getScopeId());
			((AbsVariable) v).setName(name);
			((AbsVariable) v).setProcessElementId(scope.getProcessElementId());
			((AbsVariable) v).setPayload(value);
			if (value != null) {
				if (value instanceof org.w3c.dom.Document) {
					if (property != null && property.getDataType() != null) {
						((AbsVariable) v).setDataType(property.getDataType());
					}
					v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME,
							"org.w3c.dom.Document");
				} else if (value instanceof org.dom4j.Document) {
					if (property != null && property.getDataType() != null) {
						((AbsVariable) v).setDataType(property.getDataType());
					}
					v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME,
							"org.dom4j.Document");
				} else {
					((AbsVariable) v).setDataType(new QName(NameSpaces.JAVA
							.getUri(), value.getClass().getName()));
				}

			}
			((AbsVariable) v).setProcessId(scope.getProcessId());
			((AbsVariable) v).setVersion(scope.getVersion());
			((AbsVariable) v).setProcessType(scope.getProcessType());

			if (headers != null && headers.size() > 0) {
				v.getHeaders().putAll(headers);
			}
			variablePersister.saveOrUpdate(v);
		}
		return;
	}

	public Map<String, Object> getVariableValues(Scope scope) {
		RuntimeContext ctx = this.session.getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(
				PersistenceService.class, this.processType);
		VariablePersister variablePersister = persistenceService
				.getVariablePersister();

		List<Variable> vars = variablePersister.findVariables(scope
				.getScopeId());
		Map<String, Object> varValues = new HashMap<String, Object>();
		if (vars != null && vars.size() > 0) {
			for (Variable var : vars) {
				varValues.put(var.getName(), var.getPayload());
			}
		}
		return varValues;
	}

	public <T extends WorkflowEntity> List<T> executeQueryList(
			WorkflowQuery<T> q) {
		Class<T> entityClass = q.getEntityClass();
		Persister persister = this.getPersister(entityClass);
		return persister.list(q);
	}

	public <T extends WorkflowEntity> int executeQueryCount(WorkflowQuery<T> q) {
		Class<T> entityClass = q.getEntityClass();
		Persister persister = this.getPersister(entityClass);
		return persister.count(q);
	}

	public <T extends WorkflowEntity> T getEntity(String entityId,
			Class<T> entityClass) {
		Persister persister = this.getPersister(entityClass);
		return persister.fetch(entityClass, entityId);
	}

	public Object getWorkflowProcess(ProcessKey key)
			throws InvalidModelException {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		ProcessRepository repository = processPersister
				.findProcessRepositoryByProcessKey(key);

		return repository.getProcessObject();
	}

	public String getWorkflowProcessXml(ProcessKey key)	{
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		String xml = processPersister
				.findProcessXml(key);

		return xml;
	}

	private <T> Persister getPersister(Class<T> entityClass) {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.processType);
		Persister persister = null;
		if (entityClass.isAssignableFrom(ActivityInstance.class)) {
			persister = persistenceService.getActivityInstancePersister();
		} else if (entityClass.isAssignableFrom(ProcessInstance.class)) {
			persister = persistenceService.getProcessInstancePersister();
		} else if (entityClass.isAssignableFrom(WorkItem.class)) {
			persister = persistenceService.getWorkItemPersister();
		} else if (entityClass.isAssignableFrom(Token.class)) {
			persister = persistenceService.getTokenPersister();
		} else if (entityClass.isAssignableFrom(Variable.class)) {
			persister = persistenceService.getVariablePersister();
		} else if (entityClass.isAssignableFrom(ProcessRepository.class)
				|| entityClass.isAssignableFrom(ProcessDescriptor.class)) {
			persister = persistenceService.getProcessPersister();
		} else if (entityClass.isAssignableFrom(ScheduleJob.class)) {
			persister = persistenceService.getScheduleJobPersister();
		} else if (entityClass.isAssignableFrom(FireflowConfig.class)) {
			persister = persistenceService.getFireflowConfigPersister();
		}
		return persister;
	}

	/**
	 * WorkflowStatement都是客户端调用，在调用执行前，把上一次调用的一些遗留清除
	 * 
	 * @param session
	 */
	private void resetSession(WorkflowSessionLocalImpl session) {
		session.setCurrentActivityInstance(null);
		session.setCurrentProcessInstance(null);
		session.setLatestCreatedWorkItems(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang
	 * .String)
	 */
	public ProcessInstance createProcessInstance(String workflowProcessId)
			throws InvalidModelException, WorkflowProcessNotFoundException {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		ProcessLoadStrategy loadStrategy = runtimeContext.getEngineModule(
				ProcessLoadStrategy.class, this.getProcessType());
		User u = session.getCurrentUser();
		ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(
				workflowProcessId, this.getProcessType(),u,session);
		if (pk == null) {
			throw new WorkflowProcessNotFoundException("流程库中没有processId="
					+ workflowProcessId + "的流程。");
		}
		return this.createProcessInstance(workflowProcessId, pk.getVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang
	 * .Object)
	 */
	public ProcessInstance createProcessInstance(Object process)
			throws InvalidModelException {
		ProcessDescriptor repository = this.uploadProcessObject(process,
				0);
		((ProcessDescriptorImpl)repository).setPublishState(true);
		this.updateProcessDescriptor(repository);
		return _createProcessInstance(process, repository, null);
	}

	private ProcessInstance _createProcessInstance(Object workflowProcess,
			ProcessDescriptor processDescriptor, String subProcessId) {
		RuntimeContext ctx = this.session.getRuntimeContext();

		ProcessLanguageManager processUtil = ctx.getEngineModule(ProcessLanguageManager.class,
				processType);
		String processEntryId = null;
		if (subProcessId == null || subProcessId.trim().equals("")) {
			processEntryId = processUtil.getProcessEntryId(
					processDescriptor.getProcessId(),
					processDescriptor.getVersion(),
					processDescriptor.getProcessType());
		} else {
			processEntryId = subProcessId;
		}

		ProcessInstanceManager procInstMgr = ctx.getEngineModule(
				ProcessInstanceManager.class, this.processType);

		ProcessInstance processInstance = procInstMgr.createProcessInstance(
				session, workflowProcess, processEntryId, processDescriptor,
				null);

		((WorkflowSessionLocalImpl) session)
				.setCurrentProcessInstance(processInstance);
		return processInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang
	 * .String, int)
	 */
	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.getProcessType());
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		ProcessRepository repository = processPersister
				.findProcessRepositoryByProcessKey(new ProcessKey(
						workflowProcessId, version, this.getProcessType()));
		if (repository == null) {
			throw new WorkflowProcessNotFoundException("流程库中没有ProcessId="
					+ workflowProcessId + ",version=" + version + "的流程定义文件。");
		}
		Object workflowProcess = repository.getProcessObject();
		return _createProcessInstance(workflowProcess, repository, null);
	}

	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version, String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		PersistenceService persistenceService = runtimeContext.getEngineModule(
				PersistenceService.class, this.getProcessType());
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();
		ProcessRepository repository = processPersister
				.findProcessRepositoryByProcessKey(new ProcessKey(
						workflowProcessId, version, this.getProcessType()));
		if (repository == null) {
			throw new WorkflowProcessNotFoundException("流程库中没有ProcessId="
					+ workflowProcessId + ",version=" + version + "的流程定义文件。");
		}
		Object workflowProcess = repository.getProcessObject();
		return _createProcessInstance(workflowProcess, repository, subProcessId);
	}

	public ProcessInstance createProcessInstance(String workflowProcessId,
			String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		ProcessLoadStrategy loadStrategy = runtimeContext.getEngineModule(
				ProcessLoadStrategy.class, this.getProcessType());

		User u = session.getCurrentUser();
		ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(
				workflowProcessId, this.getProcessType(),u,session);
		if (pk == null) {
			throw new WorkflowProcessNotFoundException("流程库中没有processId="
					+ workflowProcessId + "的流程。");
		}
		return this.createProcessInstance(workflowProcessId, pk.getVersion(),
				subProcessId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.WorkflowStatement#runProcessInstance(java.lang.String
	 * , java.lang.String, java.util.Map)
	 */
	public ProcessInstance runProcessInstance(String processInstanceId,
			String bizId, Map<String, Object> variables) {
		RuntimeContext runtimeContext = this.session.getRuntimeContext();
		ProcessInstanceManager procInstMgr = runtimeContext.getEngineModule(
				ProcessInstanceManager.class, this.getProcessType());
		return procInstMgr.runProcessInstance(session, processInstanceId,
				processType, bizId, variables);
	}


	public Object getWorkflowDefinitionElement(Scope scope)
			throws InvalidModelException {
		Object workflowElement = null;
		// 首先从KenelManager的换从中查抄，只要KernelManager已经load改流程，便可命中
		KernelManager kernelManager = this.session.getRuntimeContext()
				.getDefaultEngineModule(KernelManager.class);

		if (scope instanceof ActivityInstance) {
			ActivityInstance actInst = (ActivityInstance) scope;
			PObjectKey pobjectKey = new PObjectKey(actInst.getProcessId(),
					actInst.getVersion(), actInst.getProcessType(),
					actInst.getNodeId());

			PObject pObject = kernelManager.getProcessObject(pobjectKey);
			workflowElement = pObject.getWorkflowElement();
		} else if (scope instanceof ProcessInstance) {
			ProcessInstance procInst = (ProcessInstance) scope;
			PObjectKey pobjectKey = new PObjectKey(procInst.getProcessId(),
					procInst.getVersion(), procInst.getProcessType(),
					procInst.getSubProcessId());

			PObject pObject = kernelManager.getProcessObject(pobjectKey);
			workflowElement = pObject.getWorkflowElement();

		} else {
			return null;
		}

		if (workflowElement != null) {
			return workflowElement;
		}

		// 如果没有找到，则从数据库查找
		ProcessLanguageManager processUtil = this.session.getRuntimeContext()
				.getEngineModule(ProcessLanguageManager.class, scope.getProcessType());
		if (scope instanceof ActivityInstance) {
			ActivityInstance actInst = (ActivityInstance) scope;
			ProcessKey procKey = new ProcessKey(actInst.getProcessId(),
					actInst.getVersion(), actInst.getProcessType());

			workflowElement = processUtil.findActivity(procKey,
					actInst.getSubProcessId(), actInst.getNodeId());
		} else if (scope instanceof ProcessInstance) {
			ProcessInstance procInst = (ProcessInstance) scope;
			ProcessKey procKey = new ProcessKey(procInst.getProcessId(),
					procInst.getVersion(), procInst.getProcessType());

			workflowElement = processUtil.findSubProcess(procKey,
					procInst.getSubProcessId());
		}
		return workflowElement;//
	}
}
