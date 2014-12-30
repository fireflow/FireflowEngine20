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
package org.fireflow.engine.modules.workitem.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsActivityInstance;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.event.EventBroadcasterManager;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.engine.modules.workitem.WorkItemCenter;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.engine.modules.workitem.event.WorkItemEvent;
import org.fireflow.engine.modules.workitem.event.WorkItemEventTrigger;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsWorkItemManager  extends AbsEngineModule implements WorkItemManager,ServiceInvoker {
	private static Log log = LogFactory.getLog(AbsWorkItemManager.class);
	
	public WorkItemCenter getWorkItemCenter(){
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//////////////         下面两个方法实现ServiceInvoker  //////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean invoke(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivity) throws ServiceInvocationException {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		//1、首先检查有无设置DynamicAssignmentHandler
		AssignmentHandler _handler = ((WorkflowSessionLocalImpl)session).consumeDynamicAssignmentHandler(activityInstance.getNodeId());
		if (_handler!=null){
			_handler.assign(session, activityInstance,this,theActivity, serviceBinding, resourceBinding);
			return false;//return false表示该服务持续执行
		}

		//TODO 2、然后检查是否是重做，如果是重做则应用重做策略
		
		
		//3、调用resourceBinding的设置进行workitem分配
		boolean assignDone = false;
		if (resourceBinding!=null){
			String assignmentHandlerBeanName = resourceBinding.getAssignmentHandlerBeanName();
			String assignmentHandlerClassName = resourceBinding.getAssignmentHandlerClassName();
			BeanFactory beanFactory = runtimeContext.getDefaultEngineModule(BeanFactory.class);
			AssignmentHandler handler = null;
			
			if (!StringUtils.isEmpty(assignmentHandlerBeanName)){
				//3、检查是否有AssignmentHandlerBeanName				
				handler = (AssignmentHandler)beanFactory.getBean(assignmentHandlerBeanName);
			}else if (!StringUtils.isEmpty(assignmentHandlerClassName)){
				//4、检查是否有AssignmentHandlerClassName
				handler = (AssignmentHandler)beanFactory.createBean(assignmentHandlerClassName);
			}
			
			if (handler!=null){
				handler.assign(session, activityInstance,this,theActivity, serviceBinding, resourceBinding);
				assignDone = true;
			}		
		}
		
		if (!assignDone){
			//TODO 分配给系统用户，并记录流程日志
			//TODO 此处直接调用WorkItemManager，待优化
			this.createWorkItem(session, ((WorkflowSessionLocalImpl)session).getCurrentProcessInstance(), activityInstance, FireWorkflowSystem.getInstance(), theActivity,null);
		}
		return false;//表示是异步调用
	}
	
	
	/**
	 * 默认的规则是：只要activityInstance有活动的workItem则不能够停止。<br/>
	 * 可以定制该方法，以实现“按百分比决定活动是否结束”的业务需求。
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,ActivityInstance activityInstance, Object theActivity, ServiceBinding serviceBinding){
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();

		List<WorkItem> workItems = workItemPersister.findWorkItemsForActivityInstance(activityInstance.getId());
		
		for (WorkItem wi : workItems){
			if (wi.getState().getValue()<WorkItemState.DELIMITER.getValue()){
				return ServiceInvoker.WAITING_FOR_CLOSE;
			}
		}
		
		return ServiceInvoker.CLOSE_ACTIVITY;
	}
	//////////////////////////////////////////////////////////////////////////////////
	/**
	 * 取消一个WorkItem
	 * @param wi
	 */
	protected void abortWorkItem(WorkflowSession currentSession,WorkItem wi,Object thisActivity){
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)wi;
		if (wi.getState().getValue()>WorkItemState.DELIMITER.getValue())return;
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		
		ActivityInstance activityInstance = workItem.getActivityInstance();
		String processType = "";
		if (activityInstance!=null){
			processType = activityInstance.getProcessType();
		}
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processType);

		WorkItemPersister persister = persistenceService.getWorkItemPersister();
		((LocalWorkItemImpl)workItem).setState(WorkItemState.ABORTED);
		
		persister.saveOrUpdate(wi);

		//发布事件
		this.fireWorkItemEvent(currentSession, wi, thisActivity, WorkItemEventTrigger.AFTER_WORKITEM_END);
	}
	
	/**
	 * 将同一个ActivityInstance的WorkItem取消
	 * @param actInstId
	 */
	public void abortWorkfItemOfTheSameActInst(WorkflowSession currentSession,ActivityInstance actInst){
		WorkflowQuery query = currentSession.createWorkflowQuery(WorkItem.class);
		query.add(Restrictions.eq(WorkItemProperty.ACTIVITY_INSTANCE_ID, actInst.getId()));
		List<WorkItem> workItemList = query.list();
		
		Object thisActivity = null;
		try{
			WorkflowStatement stmt = currentSession.createWorkflowStatement();
			thisActivity = stmt.getWorkflowDefinitionElement(actInst);
		}catch(Exception e){
			log.error(e.getMessage(),e);
		}
		
		if (workItemList!=null){
			for (WorkItem wi : workItemList){
				this.abortWorkItem(currentSession, wi,thisActivity);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#claimWorkItem(java.lang.String, java.lang.String)
	 */
	public WorkItem claimWorkItem(WorkflowSession currentSession,WorkItem wi){
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		LocalWorkItemImpl workItem = (LocalWorkItemImpl)wi;
		ActivityInstance activityInstance = workItem.getActivityInstance();
		
		WorkflowStatement __statement = currentSession.createWorkflowStatement();
		Object thisActivity = null;
		try {
			thisActivity = __statement.getWorkflowDefinitionElement(activityInstance);
		} catch (InvalidModelException e) {
			log.error(e.getMessage(),e);
		}
		//这个事件估计没有什么实际意义
		this.fireWorkItemEvent(currentSession, workItem, thisActivity, WorkItemEventTrigger.BEFORE_WORKITEM_CLAIMED);
		
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, activityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class, activityInstance.getProcessType());
		
		ActivityInstancePersister activityInstancePersister = persistenceService.getActivityInstancePersister();
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();

		activityInstancePersister.lockActivityInstance(activityInstance.getId());
		


		// 0、首先修改workitem的状态
		((AbsWorkItem) workItem).setState(WorkItemState.RUNNING);
		((AbsWorkItem) workItem).setClaimedTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItem);

		// 1、如果不是会签，则删除其他的workitem
		if (WorkItemAssignmentStrategy.ASSIGN_TO_ANY.equals(workItem.getAssignmentStrategy())) {
			workItemPersister.deleteWorkItemsInInitializedState(activityInstance.getId(),workItem.getParentWorkItemId());
		}

		// 2、将ActivityInstance的canBeWithdrawn字段改称false。即不允许被撤销
		((AbsActivityInstance)activityInstance).setCanBeWithdrawn(false);
		activityInstancePersister.saveOrUpdate(activityInstance);

		
		
		//发布事件
		this.fireWorkItemEvent(currentSession, workItem, thisActivity, WorkItemEventTrigger.AFTER_WORKITEM_CLAIMED);
		
		return workItem;
	}

	public WorkItem disclaimWorkItem(WorkflowSession currentSession,
			WorkItem wi)throws InvalidOperationException{
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		
		LocalWorkItemImpl workItemToBeDisclaimed = (LocalWorkItemImpl)wi;
		
		ActivityInstance thisActivityInstance = workItemToBeDisclaimed.getActivityInstance();	
		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		
		WorkflowStatement localStatement = currentSession.createWorkflowStatement();
		
		ProcessKey pKey = new ProcessKey(thisActivityInstance.getProcessId(),thisActivityInstance.getVersion(),thisActivityInstance.getProcessType());

		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class, thisActivityInstance.getProcessType());
		ProcessLanguageManager processService = rtCtx.getEngineModule(ProcessLanguageManager.class, thisActivityInstance.getProcessType());
		
		Object theActivity = null;
		try{
			theActivity = localStatement.getWorkflowDefinitionElement(thisActivityInstance);
		}catch(InvalidModelException e){
			log.error(e);
			throw new InvalidOperationException(e);
		}
		
		ServiceBinding serviceBinding = null;
		try{
			serviceBinding = processService.getServiceBinding(theActivity);
		}catch(InvalidModelException e){
			log.error(e);
			throw new InvalidOperationException(e);
		}
		ResourceBinding resourceBinding = null;
		try{
			resourceBinding = processService.getResourceBinding(theActivity);
		}catch(InvalidModelException e){
			log.error(e);
			throw new InvalidOperationException(e);
		}
		
		

		try {
			((AbsWorkItem)workItemToBeDisclaimed).setState(WorkItemState.DISCLAIMED);
			((AbsWorkItem)workItemToBeDisclaimed).setEndTime(calendarService.getSysDate());
			WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
			workItemPersister.saveOrUpdate(workItemToBeDisclaimed);
			
			//发布事件
			this.fireWorkItemEvent(currentSession, workItemToBeDisclaimed, theActivity, WorkItemEventTrigger.AFTER_WORKITEM_END);
			
			this.invoke(currentSession, thisActivityInstance,serviceBinding,resourceBinding, theActivity);

			return workItemToBeDisclaimed;
		} catch (ServiceInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new EngineException(thisActivityInstance,e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#completeWorkItemAndJumpTo(org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String)
	 */
	public WorkItem completeWorkItemAndJumpTo(WorkflowSession currentSession,
			WorkItem wi, String targetActivityId) throws InvalidOperationException{
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)currentSession;
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		
		LocalWorkItemImpl workItemToBeCompleted = (LocalWorkItemImpl)wi;
		ActivityInstance thisActivityInstance = workItemToBeCompleted.getActivityInstance();

		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  thisActivityInstance.getProcessType());
		ActivityInstanceManager activityInstanceManager = rtCtx.getEngineModule(ActivityInstanceManager.class, thisActivityInstance.getProcessType());
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		
		if (workItemToBeCompleted.getParentWorkItemId() != null && !workItemToBeCompleted.getParentWorkItemId().trim().equals("")
				&& !workItemToBeCompleted.getParentWorkItemId().trim().equals(
						WorkItem.NO_PARENT_WORKITEM)) {
			String reassignType = workItemToBeCompleted.getReassignType();
			if (reassignType == null || reassignType.trim().equals("")
					|| reassignType.trim().equals(WorkItem.REASSIGN_AFTER_ME)) {//后加签
				WorkItemAssignmentStrategy assignmentStrategy = workItemToBeCompleted.getAssignmentStrategy();
				if (assignmentStrategy != null
						&& !assignmentStrategy.equals(
								WorkItemAssignmentStrategy.ASSIGN_TO_ANY)) {

					List<WorkItem> workItemsWithSameParent = workItemPersister
							.findWorkItemsForActivityInstance(workItemToBeCompleted
									.getActivityInstance().getId(), workItemToBeCompleted
									.getParentWorkItemId());
					
					for(WorkItem wiTmp : workItemsWithSameParent){
						if (wiTmp.getState().getValue()<WorkItemState.DELIMITER.getValue()){
							throw new InvalidOperationException("Reassigned workitem can NOT jump to another activity.");
						}
					}
				}
			}
			else{//前加签
				throw new InvalidOperationException("Reassigned workitem can NOT jump to another activity.");
			}
		}

		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);
		
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		

		((AbsWorkItem)workItemToBeCompleted).setState(WorkItemState.COMPLETED);
		((AbsWorkItem)workItemToBeCompleted).setEndTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItemToBeCompleted);
		
		//发布事件
		try {
			WorkflowStatement stmt = currentSession.createWorkflowStatement();
			Object thisActivity = stmt
					.getWorkflowDefinitionElement(thisActivityInstance);
			this.fireWorkItemEvent(currentSession, workItemToBeCompleted,
					thisActivity, WorkItemEventTrigger.AFTER_WORKITEM_END);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		
		if (workItemToBeCompleted.getParentWorkItemId() == null
				|| workItemToBeCompleted.getParentWorkItemId().trim().equals("")
				|| workItemToBeCompleted.getParentWorkItemId().trim().equals(
						WorkItem.NO_PARENT_WORKITEM)) {

			localSession.setAttribute(WorkItemManager.TARGET_ACTIVITY_ID, targetActivityId);
			activityInstanceManager.onServiceCompleted(currentSession, thisActivityInstance);
		}

		return workItemToBeCompleted;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#reassignWorkItemTo(org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String)
	 */
    public List<WorkItem> reassignWorkItemTo(WorkflowSession currentSession,
			WorkItem wi, AssignmentHandler assignmentHandler,
			Object theActivity,ServiceBinding serviceBinding,
			ResourceBinding resourceBinding){
    	
    	LocalWorkItemImpl workItemToBeReassign = (LocalWorkItemImpl)wi;
    	
		ActivityInstance thisActivityInstance = workItemToBeReassign.getActivityInstance();
		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);

		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();

		List<WorkItem> result = assignmentHandler.assign(currentSession, thisActivityInstance, this, 
				theActivity, serviceBinding, resourceBinding);
		
//		转移到assignmentHandler里面进行工作项处理
//		List<WorkItem> result = new ArrayList<WorkItem>();
//		for (User user : users){
//			Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
//			values.put(WorkItemProperty.REASSIGN_TYPE, reassignType);
//			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, assignmentStrategy);
//			values.put(WorkItemProperty.PARENT_WORKITEM_ID,workItem.getId());
//			
//			WorkItem wi = this.createWorkItem(currentSession, thisProcessInstance, thisActivityInstance, user,theActivity, values);
//			
//			result .add(wi);
//		}
		
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class, thisActivityInstance.getProcessType());

		((AbsWorkItem)workItemToBeReassign).setState(WorkItemState.REASSIGNED);
		((AbsWorkItem)workItemToBeReassign).setEndTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItemToBeReassign);
		
		//发布事件
		try {
			this.fireWorkItemEvent(currentSession, workItemToBeReassign,
					theActivity, WorkItemEventTrigger.AFTER_WORKITEM_END);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		return result;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.service.form.WorkItemManager#rejectWorkItem(org.fireflow.engine.entity.runtime.WorkItem, java.lang.String)
//	 */
//	@Override
//	public void rejectWorkItem(WorkItem workItem, String comments)
//			throws InvalidOperationException {
//		// TODO Auto-generated method stub
//
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.form.WorkItemManager#withdrawWorkItem(org.fireflow.engine.entity.runtime.WorkItem)
	 */
	public WorkItem withdrawWorkItem(WorkflowSession currentSession,WorkItem workItem)
			throws InvalidOperationException {
		//TODO 待实现
		return null;
	}

	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.human.WorkItemManager#completeWorkItem(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String, java.lang.String)
	 */
	public WorkItem completeWorkItem(WorkflowSession currentSession,
			WorkItem wi) throws InvalidOperationException {
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		
		LocalWorkItemImpl workItemToBeCompleted = (LocalWorkItemImpl)wi;
		
		ActivityInstance thisActivityInstance = workItemToBeCompleted.getActivityInstance();

		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);
		
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		
		ActivityInstanceManager actInstMgr = rtCtx.getEngineModule(ActivityInstanceManager.class, thisActivityInstance.getProcessType());
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, thisActivityInstance.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  thisActivityInstance.getProcessType());
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		
		((AbsWorkItem)workItemToBeCompleted).setState(WorkItemState.COMPLETED);
		((AbsWorkItem)workItemToBeCompleted).setEndTime(calendarService.getSysDate());
		workItemPersister.saveOrUpdate(workItemToBeCompleted);
		
		//发布事件
		try {
			WorkflowStatement stmt = currentSession.createWorkflowStatement();
			Object thisActivity = stmt
					.getWorkflowDefinitionElement(thisActivityInstance);
			this.fireWorkItemEvent(currentSession, workItemToBeCompleted,
					thisActivity, WorkItemEventTrigger.AFTER_WORKITEM_END);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		
		//后续处理
		if (workItemToBeCompleted.getParentWorkItemId() == null
				|| workItemToBeCompleted.getParentWorkItemId().trim().equals("")
				|| workItemToBeCompleted.getParentWorkItemId().trim().equals(
						WorkItem.NO_PARENT_WORKITEM)) {

			
			actInstMgr.onServiceCompleted(currentSession, thisActivityInstance);
		}else{
			//委派的工作项特殊处理
			String reassignType = workItemToBeCompleted.getReassignType();
			if (reassignType == null || reassignType.trim().equals("")
					|| reassignType.trim().equals(WorkItem.REASSIGN_AFTER_ME)) {//后加签
				WorkItemAssignmentStrategy assignmentStrategy = workItemToBeCompleted.getAssignmentStrategy();
				if (assignmentStrategy == null
						|| assignmentStrategy.equals(
								WorkItemAssignmentStrategy.ASSIGN_TO_ANY)) {
					
					actInstMgr.onServiceCompleted(currentSession,
							thisActivityInstance);
				} else {
					List<WorkItem> workItemsWithSameParent = workItemPersister
							.findWorkItemsForActivityInstance(workItemToBeCompleted
									.getActivityInstance().getId(), workItemToBeCompleted
									.getParentWorkItemId());
					boolean canCompleteActivityInstance = true;
					for(WorkItem wiTmp : workItemsWithSameParent){
						if (wiTmp.getState().getValue()<WorkItemState.DELIMITER.getValue()){
							canCompleteActivityInstance = false;
							break;
						}
					}
					if (canCompleteActivityInstance){
						actInstMgr.onServiceCompleted(currentSession,
								thisActivityInstance);
					}

				}
			}
			else{//前加签
				WorkItemAssignmentStrategy assignmentStrategy = workItemToBeCompleted.getAssignmentStrategy();
				if (assignmentStrategy == null
						|| assignmentStrategy.equals(
								WorkItemAssignmentStrategy.ASSIGN_TO_ANY)) {
					WorkItem parentWorkItem = workItemPersister.fetch(WorkItem.class, workItemToBeCompleted.getParentWorkItemId());
					WorkItem newParentWi = this.cloneWorkItem(parentWorkItem, calendarService);
					workItemPersister.saveOrUpdate(newParentWi);
				} else {
					List<WorkItem> workItemsWithSameParent = workItemPersister
							.findWorkItemsForActivityInstance(workItemToBeCompleted
									.getActivityInstance().getId(), workItemToBeCompleted
									.getParentWorkItemId());
					
					boolean canReturnToParentWorkItem = true;
					for(WorkItem wiTmp : workItemsWithSameParent){
						if (wiTmp.getState().getValue()<WorkItemState.DELIMITER.getValue()){
							canReturnToParentWorkItem = false;
							break;
						}
					}
					if(canReturnToParentWorkItem){
						WorkItem parentWorkItem = workItemPersister.fetch(WorkItem.class, workItemToBeCompleted.getParentWorkItemId());
						WorkItem newParentWi = this.cloneWorkItem(parentWorkItem, calendarService);
						workItemPersister.saveOrUpdate(newParentWi);
					}

				}
			}
		}
		
		
		
		return workItemToBeCompleted;
	}
	private WorkItem cloneWorkItem(WorkItem wi,CalendarService calendarService){
		AbsWorkItem tmp = (AbsWorkItem)((AbsWorkItem)wi).clone();
		tmp.setState(WorkItemState.RUNNING);
		tmp.setClaimedTime(calendarService.getSysDate());
		tmp.setEndTime(null);
		tmp.setNote(null);
		tmp.setAttachmentId(null);
		tmp.setCreatedTime(calendarService.getSysDate());
		return tmp;
	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.service.human.WorkItemManager#completeWorkItem(org.fireflow.engine.WorkflowSession, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	@Override
//	public void completeWorkItem(WorkflowSession currentSession,
//			String workItemId, String commentSummary, String note,
//			String attachmentId,String processType) throws InvalidOperationException {
//		
//		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
//		
//		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processType);
//		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
//
//		WorkItem wi = workItemPersister.find(WorkItem.class, workItemId);
//		
//		this.completeWorkItem(currentSession, wi, commentSummary, note, attachmentId);
//		
//	}

	/* AbsWorkItemManager实现了ServiceInvoker，不需要该方法 ，2012-02-20 */
	/*
	protected ServiceInvoker getServiceInvoker(RuntimeContext runtimeContext,WorkItem workItem){
		ActivityInstance activityInstance = workItem.getActivityInstance();
		String processType = activityInstance.getProcessType();
		
		ProcessLanguageManager processUtil = runtimeContext.getEngineModule(ProcessLanguageManager.class,processType);
		ServiceBinding serviceBinding = null;
		try{
			serviceBinding = processUtil.getServiceBinding(new ProcessKey(activityInstance.getProcessId(),activityInstance.getVersion(),processType), activityInstance.getSubflowId(), activityInstance.getNodeId());
		}catch(InvalidModelException e){
			log.error(e);
		}
		if (serviceBinding==null)return null;
		
		ServiceDef service = serviceBinding.getService();
		if (service==null) return null;
		

		ServiceInvoker serviceInvoker = null;
		String invokerBeanName = service.getInvokerBeanName();
		String invokerClassName = service.getInvokerClassName();
		
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class,processType);
		if (!StringUtils.isEmpty(invokerBeanName)){
			
			serviceInvoker = (ServiceInvoker)beanFactory.getBean(invokerBeanName);
		}
		
		if (serviceInvoker==null && !StringUtils.isEmpty(invokerClassName)){
			serviceInvoker = (ServiceInvoker) beanFactory.createBean(invokerClassName);
		}
		return serviceInvoker;

	}
	*/
	
	public void fireWorkItemEvent(WorkflowSession session,WorkItem workItem,Object activity,WorkItemEventTrigger eventType){

		try{
			WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;
			RuntimeContext rtCtx = sessionLocalImpl.getRuntimeContext();
			EventBroadcasterManager evetBroadcasterMgr = rtCtx.getDefaultEngineModule(EventBroadcasterManager.class);
			
			EventBroadcaster broadcaster = evetBroadcasterMgr.getEventBroadcaster(WorkItemEvent.class.getName());
			if (broadcaster!=null){
				WorkItemEvent event = new WorkItemEvent();
				event.setSource(workItem);
				event.setEventTrigger(eventType);
				event.setWorkflowElement(activity);
				event.setCurrentWorkflowSession(sessionLocalImpl);
				
				broadcaster.fireEvent(sessionLocalImpl, event);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}

	}
}
