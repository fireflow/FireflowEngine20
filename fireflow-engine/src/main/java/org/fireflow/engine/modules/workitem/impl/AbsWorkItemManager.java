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

import java.util.ArrayList;
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
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.LocalWorkItem;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsActivityInstance;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.DynamicAssignmentHandler;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.event.EventBroadcasterManager;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
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
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.kernel.impl.TokenImpl;

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
	protected boolean isSkip(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivityObj){
		return false;
	}
	
	protected boolean isRedo(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivityObj){
		return false;
	}
	
	
	
	public boolean invoke(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivityObj) throws ServiceInvocationException {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		
		boolean assignDone = false;
		
		// 1、检查是否是重做，如果是重做则应用重做策略
		if (isSkip(session,activityInstance,serviceBinding,resourceBinding,theActivityObj)){
			WorkflowQuery<WorkItem> wiQuery = session.createWorkflowQuery(WorkItem.class);
			wiQuery.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, activityInstance.getProcessInstanceId()))
					.add(Restrictions.eq(WorkItemProperty.ACTIVITY_ID, activityInstance.getNodeId()));//只要有产生工作项，则认为已经被执行过一次

			List<WorkItem> completedWorkItemList = wiQuery.list();
			if (completedWorkItemList!=null && completedWorkItemList.size()>0){
				return true;//true表示结束当前activity instance继续往前流转
			}
			
			
		}else if (isRedo(session,activityInstance,serviceBinding,resourceBinding,theActivityObj)){
			//先查找当前环节已经存在的工作项，
			//查询当前实例的，同一环节的，已经完成的工作项
			WorkflowQuery<WorkItem> wiQuery = session.createWorkflowQuery(WorkItem.class);
			wiQuery.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, activityInstance.getProcessInstanceId()))
					.add(Restrictions.eq(WorkItemProperty.ACTIVITY_ID, activityInstance.getNodeId()))
					.add(Restrictions.eq(WorkItemProperty.STATE, WorkItemState.COMPLETED));

			List<WorkItem> completedWorkItemList = wiQuery.list();
			if (completedWorkItemList!=null || completedWorkItemList.size()>0){
				//分配给上次完成本工作的人
				List<User> owners = new ArrayList<User>();
				for (WorkItem wi : completedWorkItemList){
					UserImpl u = new UserImpl();
					u.setId(wi.getOwnerId());
					u.setName(wi.getOwnerName());
					u.setDeptId(wi.getOwnerDeptId());
					u.setDeptName(wi.getOwnerDeptName());
					owners.add(u);
				}
				DynamicAssignmentHandler assignmentHandler = new DynamicAssignmentHandler();
				assignmentHandler.setPotentialOwners(owners);
				assignmentHandler.setAssignmentStrategy(resourceBinding.getAssignmentStrategy());
				
				assignmentHandler.assign(session, activityInstance,this,theActivityObj, serviceBinding, resourceBinding);
				
				return false;//return false表示该服务持续执行,需要引擎等待
			}
			
		}
			
		// 2、检查有无设置DynamicAssignmentHandler
		AssignmentHandler _handler = ((WorkflowSessionLocalImpl) session)
				.consumeDynamicAssignmentHandler(activityInstance.getNodeId());
		if (_handler != null) {
			_handler.assign(session, activityInstance, this, theActivityObj,
					serviceBinding, resourceBinding);
			return false;// return false表示该服务持续执行，需要引擎等待
		}

		// 3、调用resourceBinding的设置进行workitem分配

		if (resourceBinding != null) {
			String assignmentHandlerBeanName = resourceBinding
					.getAssignmentHandlerBeanName();
			String assignmentHandlerClassName = resourceBinding
					.getAssignmentHandlerClassName();
			BeanFactory beanFactory = runtimeContext
					.getDefaultEngineModule(BeanFactory.class);
			AssignmentHandler handler = null;

			if (!StringUtils.isEmpty(assignmentHandlerBeanName)) {
				// 3、检查是否有AssignmentHandlerBeanName
				handler = (AssignmentHandler) beanFactory
						.getBean(assignmentHandlerBeanName);
			} else if (!StringUtils.isEmpty(assignmentHandlerClassName)) {
				// 4、检查是否有AssignmentHandlerClassName
				handler = (AssignmentHandler) beanFactory
						.createBean(assignmentHandlerClassName);
			}

			if (handler != null) {
				handler.assign(session, activityInstance, this, theActivityObj,
						serviceBinding, resourceBinding);
				assignDone = true;
			}
		}
		
		if (!assignDone){
			//TODO 分配给系统用户，并记录流程日志
			//TODO 此处直接调用WorkItemManager，待优化
			this.createWorkItem(session, ((WorkflowSessionLocalImpl)session).getCurrentProcessInstance(), activityInstance, FireWorkflowSystem.getInstance(), theActivityObj,null);
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
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, workItem.getProcessType());

		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		
		ActivityInstance activityInstance = actInstPersister.fetch(ActivityInstanceImpl.class,workItem.getActivityInstanceId());
		String processType = "";
		if (activityInstance!=null){
			processType = activityInstance.getProcessType();
		}

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
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, workItem.getProcessType());
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		
		ActivityInstance activityInstance = actInstPersister.fetch(ActivityInstanceImpl.class, workItem.getActivityInstanceId());
		
		WorkflowStatement __statement = currentSession.createWorkflowStatement();
		Object thisActivity = null;
		try {
			thisActivity = __statement.getWorkflowDefinitionElement(activityInstance);
		} catch (InvalidModelException e) {
			log.error(e.getMessage(),e);
		}
		//这个事件估计没有什么实际意义
		this.fireWorkItemEvent(currentSession, workItem, thisActivity, WorkItemEventTrigger.BEFORE_WORKITEM_CLAIMED);
		
		
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
		LocalWorkItemImpl workItemToBeDisclaimed = (LocalWorkItemImpl)wi;
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, workItemToBeDisclaimed.getProcessType());

		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		
		ActivityInstance thisActivityInstance = actInstPersister.fetch(ActivityInstanceImpl.class, workItemToBeDisclaimed.getActivityInstanceId());	
		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		
		WorkflowStatement localStatement = currentSession.createWorkflowStatement();
		
		ProcessKey pKey = new ProcessKey(thisActivityInstance.getProcessId(),thisActivityInstance.getVersion(),thisActivityInstance.getProcessType());

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
		LocalWorkItemImpl workItemToBeCompleted = (LocalWorkItemImpl)wi;
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)currentSession;
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, workItemToBeCompleted.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  workItemToBeCompleted.getProcessType());
		ActivityInstanceManager activityInstanceManager = rtCtx.getEngineModule(ActivityInstanceManager.class, workItemToBeCompleted.getProcessType());
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();

		ActivityInstance thisActivityInstance = actInstPersister.fetch(ActivityInstanceImpl.class, workItemToBeCompleted.getActivityInstanceId());

				
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
									.getActivityInstanceId(), workItemToBeCompleted
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
    	
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, workItemToBeReassign.getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();

		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		

    	
    	String thisActInstId = workItemToBeReassign.getActivityInstanceId();
    	
		ActivityInstance thisActivityInstance = actInstPersister.fetch(ActivityInstanceImpl.class, thisActInstId);
		
		
		ProcessInstance thisProcessInstance = thisActivityInstance.getProcessInstance(currentSession);

		((WorkflowSessionLocalImpl)currentSession).setCurrentActivityInstance(thisActivityInstance);
		((WorkflowSessionLocalImpl)currentSession).setCurrentProcessInstance(thisProcessInstance);
		

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

		WorkflowQuery<WorkItem> wiQuery = currentSession.createWorkflowQuery(WorkItem.class);
		wiQuery.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID,
				((LocalWorkItem)workItem).getProcessInstanceId()))
				.add(Restrictions.eq(WorkItemProperty.SUBPROCESS_ID, ((LocalWorkItem)workItem).getSubProcessId()))
				.add(Restrictions.gt(WorkItemProperty.STEP_NUMBER,  ((LocalWorkItem)workItem).getStepNumber()));
		
		List<WorkItem> nextWorkItemList = wiQuery.list();
		
		if (nextWorkItemList==null || nextWorkItemList.size()==0){
			throw new InvalidOperationException("无后续工作项，无法取回");
		}
		
		List<String> nextActInstanceIdList = new ArrayList<String>();
		boolean canBeWithdrawn = true;
		int maxStepNumber = 0;
		for (WorkItem wi : nextWorkItemList){
			if (wi.getState().getValue()>WorkItemState.INITIALIZED.getValue()){
				//说明有工作项已经被签收，不能做取回操作
				canBeWithdrawn = false;
			}
			String actInstId = ((LocalWorkItem)wi).getActivityInstanceId();
			if (!nextActInstanceIdList.contains(actInstId)){
				nextActInstanceIdList.add(actInstId);
			}
			
			if (maxStepNumber<((LocalWorkItem)wi).getStepNumber()){
				maxStepNumber = ((LocalWorkItem)wi).getStepNumber();
			}
		}
		
		if (!canBeWithdrawn){
			throw new InvalidOperationException("有后续工作项已经被签收，不能做取回操作。");

		}
		
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)currentSession;
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, ((LocalWorkItem)workItem).getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  ((LocalWorkItem)workItem).getProcessType());
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();

		TokenPersister tokenPersister = persistenceService.getTokenPersister();
		
		//1、先创建本环节的token,actinst和工作项，
		int newStepNumber = maxStepNumber+1;
		ActivityInstance thisActivityInstance = actInstPersister.fetch(ActivityInstance.class, ((LocalWorkItem)workItem).getActivityInstanceId());
		Token thisToken = tokenPersister.fetch(Token.class, thisActivityInstance.getTokenId());
		
		Token newToken = new TokenImpl(thisToken);
		newToken.setElementId(thisToken.getElementId());
		newToken.setFromToken(thisToken.getFromToken());
		newToken.setStepNumber(newStepNumber);
		newToken.setState(TokenState.RUNNING);
		
		tokenPersister.saveOrUpdate(newToken);
		
		ActivityInstanceImpl newActivityInstance = (ActivityInstanceImpl)((ActivityInstanceImpl)thisActivityInstance).clone();
		newActivityInstance.setId(null);
		newActivityInstance.setTokenId(newToken.getId());
		newActivityInstance.setStepNumber(newStepNumber);
		newActivityInstance.setState(ActivityInstanceState.RUNNING);
		
		actInstPersister.saveOrUpdate(newActivityInstance);
		
		LocalWorkItemImpl newWorkItem = (LocalWorkItemImpl)(( LocalWorkItemImpl)workItem).clone();
		newWorkItem.setId(null);
		newWorkItem.setActivityInstanceId(newActivityInstance.getId());
		newWorkItem.setStepNumber(newStepNumber);
		newWorkItem.setState(WorkItemState.INITIALIZED);
		
		workItemPersister.saveOrUpdate(newWorkItem);
		
		
		//2、然后对后续环节实例做abort操作
		WorkflowStatement stmt = currentSession.createWorkflowStatement();
		
		for (String actInstId : nextActInstanceIdList){
			stmt.abortActivityInstance(actInstId, "被取回");
		}
		return newWorkItem;
	}

	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.service.human.WorkItemManager#completeWorkItem(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.WorkItem, java.lang.String, java.lang.String, java.lang.String)
	 */
	public WorkItem completeWorkItem(WorkflowSession currentSession,
			WorkItem wi) throws InvalidOperationException {
		LocalWorkItemImpl workItemToBeCompleted = (LocalWorkItemImpl)wi;
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)currentSession).getRuntimeContext();
		
		ActivityInstanceManager actInstMgr = rtCtx.getEngineModule(ActivityInstanceManager.class, workItemToBeCompleted.getProcessType());
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, workItemToBeCompleted.getProcessType());
		CalendarService calendarService = rtCtx.getEngineModule(CalendarService.class,  workItemToBeCompleted.getProcessType());
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();


		
		ActivityInstance thisActivityInstance = actInstPersister.fetch(ActivityInstanceImpl.class, workItemToBeCompleted.getActivityInstanceId());

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
									.getActivityInstanceId(), workItemToBeCompleted
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
									.getActivityInstanceId(), workItemToBeCompleted
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
