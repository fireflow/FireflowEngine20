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
package org.fireflow.pdl.fpdl.behavior;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEventTrigger;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.model.data.Property;
import org.fireflow.pdl.fpdl.behavior.router.SplitEvaluator;
import org.fireflow.pdl.fpdl.behavior.router.impl.OrSplitEvaluator;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.features.Feature;
import org.fireflow.pdl.fpdl.process.features.startnode.TimerStartFeature;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.kernel.impl.TokenImpl;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityBehavior extends AbsNodeBehavior implements WorkflowBehavior {
	
	Log log = LogFactory.getLog(ActivityBehavior.class);
	
	//（2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
//	private CancellationHandler cancellationHandler = new ActivityCancellationHandler();
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#prepare(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, token.getProcessType());
		ActivityInstancePersister actInstPersistSvc = persistenceStrategy.getActivityInstancePersister();
		
		//0、校验processInstance与token的一致性
		ProcessInstance processInstance = sessionLocalImpl.getCurrentProcessInstance();

		if (processInstance==null || !processInstance.getId().equals(token.getProcessInstanceId())){
			WorkflowQuery<ProcessInstance> q4ProcInst = sessionLocalImpl.createWorkflowQuery(ProcessInstance.class);
			processInstance = q4ProcInst.get(token.getProcessInstanceId());
			sessionLocalImpl.setCurrentProcessInstance(processInstance);
		}
		
		//1、创建并保存环节实例
		ActivityInstanceImpl activityInstance = (ActivityInstanceImpl)activityInstanceMgr.createActivityInstance(session, processInstance, workflowElement);
		
		activityInstance.setStepNumber(token.getStepNumber());
		activityInstance.setTokenId(token.getId());
		actInstPersistSvc.saveOrUpdate(activityInstance);
		
		//2、设置session和token
		((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
		
		token.setElementInstanceId(activityInstance.getId());		
	
		//3、初始化活动的流程变量
		initActivityInstanceVariables(persistenceStrategy.getVariablePersister(),
				activityInstance,((Activity)workflowElement).getProperties(),null);
		
		//4、发布事件
		activityInstanceMgr.fireActivityInstanceEvent(session, activityInstance, workflowElement, ActivityInstanceEventTrigger.ON_ACTIVITY_INSTANCE_CREATED);
		

		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		Activity activity = (Activity)workflowElement;
		log.debug("Activity[id="+activity.getId()+"] Behavior executed!");
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();

		//1、检验currentActivityInstance和currentProcessInstance的一致性
		ProcessInstance oldProcInst = sessionLocalImpl.getCurrentProcessInstance();
		ActivityInstance oldActInst = sessionLocalImpl.getCurrentActivityInstance();

		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy.getActivityInstancePersister();
		ProcessInstancePersister processInstancePersister = persistenceStrategy.getProcessInstancePersister();
		
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
			ProcessInstance procInst = processInstancePersister.fetch(ProcessInstance.class, token.getProcessInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
		}
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			ActivityInstance actInst = actInstPersistenceService.fetch(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
		}
		
		//2、执行业务操作
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ActivityInstance currentActivityInstance = sessionLocalImpl.getCurrentActivityInstance();
		
		try {
			boolean b = activityInstanceMgr.runActivityInstance(session, workflowElement, currentActivityInstance);
			
			if (b){
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.COMPLETED);
				return result;
			}else{
				//只有在这种情况下，启动边上的Timer节点才有意义
				List<StartNode> attachedStartNodes = activity.getAttachedStartNodes();
				if (attachedStartNodes!=null){
					for (StartNode startNode : attachedStartNodes){
						Feature decorator = startNode.getFeature();
						//启动timer类型的边节点
						if (decorator instanceof TimerStartFeature){
							
							Token childToken = new TokenImpl(token);
							childToken.setElementId(startNode.getId());
							childToken.setParentTokenId(token.getParentTokenId());
							childToken.setAttachedToToken(token.getId());
							
							BookMark bookMark = new BookMark();
							bookMark.setToken(childToken);
							bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
							bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
							
							KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
							
							kernelManager.addBookMark(bookMark);
						}
					}
				}				
				
				
				ExecuteResult result = new ExecuteResult();
				result.setStatus(BusinessStatus.RUNNING);
				return result;
			}
		} catch (ServiceInvocationException e) {
			// TODO Auto-generated catch block
			//TODO 进行异常处理，记录日志
			e.printStackTrace();
			
			ExecuteResult result = new ExecuteResult();
			result.setErrorCode(e.getErrorCode());
			result.setStatus(BusinessStatus.FAULTING);
			return result;
		}finally{
			
			sessionLocalImpl.setCurrentProcessInstance(oldProcInst);
			sessionLocalImpl.setCurrentActivityInstance(oldActInst);
		}
		

	}
	
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		if (token.getState().getValue()<TokenState.DELIMITER.getValue() && 
				token.getState().getValue()!=TokenState.RUNNING.getValue()){
			return ContinueDirection.closeMe();
		}
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		
		//1、检验currentActivityInstance和currentProcessInstance的一致性
		ProcessInstance oldProcInst = sessionLocalImpl.getCurrentProcessInstance();
		ActivityInstance oldActInst = sessionLocalImpl.getCurrentActivityInstance();

		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy.getActivityInstancePersister();
		ProcessInstancePersister processInstancePersister = persistenceStrategy.getProcessInstancePersister();
		
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
			ProcessInstance procInst = processInstancePersister.fetch(ProcessInstance.class, token.getProcessInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
		}
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			ActivityInstance actInst = actInstPersistenceService.fetch(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
		}
		
		//2、执行业务操作
		try{
			ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, FpdlConstants.PROCESS_TYPE_FPDL20);
			ActivityInstance currentActivityInstance = sessionLocalImpl.getCurrentActivityInstance();
			int direction = activityInstanceMgr.tryCloseActivityInstance(session, currentActivityInstance,workflowElement);
			if (direction == ContinueDirection.WAITING_FOR_CLOSE) {
				return ContinueDirection.waitingForClose();// 继续等待；
			} 
			else if (direction==ContinueDirection.START_NEXT_AND_WAITING_FOR_CLOSE){
				//计算后续路由
				List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,token,workflowElement);
				
				ContinueDirection directionObj = ContinueDirection.startNextAndWaitingForClose();
				if (nextPObjectKeys.size()>0){
					directionObj.setNextProcessObjectKeys(nextPObjectKeys);
				}
				return directionObj;
			}
			else if (direction == ContinueDirection.CLOSE_ME) {

				//计算后续路由
				List<PObjectKey> nextPObjectKeys = determineNextPObjectKeys(session,token,workflowElement);
				
				ContinueDirection directionObj = ContinueDirection.closeMe();
				if (nextPObjectKeys.size()>0){
					directionObj.setNextProcessObjectKeys(nextPObjectKeys);
				}

				return directionObj;
			}
			else{
				//TODO 抛出异常
				throw new RuntimeException();
			}
		}finally{
			//2012-03-17 对于continueOn方法，不需要、也不应该在此重新设置olcProcInst和oldActInst
//			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
//			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}

	}
	
	//（2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
//	public CancellationHandler getCancellationHandler(){
//		return cancellationHandler;
//	}
	
	/* (non-Javadoc)
	 * TODO 该方法逻辑太复杂，有改善空间，2012-02-05
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		WorkflowSessionLocalImpl sessionLocalImpl = (WorkflowSessionLocalImpl)session;

		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ActivityInstancePersister actInstPersistenceService = persistenceStrategy.getActivityInstancePersister();
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		
		ActivityInstance oldActInst = sessionLocalImpl.getCurrentActivityInstance();
		ActivityInstance activityInstance = oldActInst;
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			activityInstance = actInstPersistenceService.fetch(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
		}
		ActivityInstanceManager activityInstanceMgr = ctx.getEngineModule(ActivityInstanceManager.class, token.getProcessType());
		try{
			ActivityInstanceState state = ActivityInstanceState.valueOf(token.getState().name());
			
			if (state.getValue()>ActivityInstanceState.DELIMITER.getValue()){
				// 停止边上的定时器对应的activityInstance
				List<Token> attachedTokens = tokenPersister.findAttachedTokens(token);
				if (attachedTokens!=null && attachedTokens.size()>0){
					KernelManager kernelManager = ctx.getEngineModule(KernelManager.class, token.getProcessType());
					for (Token attachedToken : attachedTokens){
						if (attachedToken.getState().getValue()<TokenState.DELIMITER.getValue()){
							BookMark bookMark = new BookMark();
							bookMark.setToken(attachedToken);
							bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);//此处只能用HANDLE_TERMINATION，因为HANDLE_FORWORD可能导致TimerStart继续执行。
							bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
							
							kernelManager.addBookMark(bookMark);
						}

					}
				}
				
				//将调度器中的timmer删除		
				//对于Activity而言，此段代码意义不大，因为Activity中没有Timer服务，所以暂时注释掉。2012-02-19
				/*
				ScheduleJobPersister scheduleJobPersister = persistenceStrategy.getScheduleJobPersister();
				List<ScheduleJob> scheduleJobs = scheduleJobPersister.findScheduleJob4ActivityInstance(activityInstance.getId());
				if (scheduleJobs!=null && scheduleJobs.size()>0){
					for (ScheduleJob job : scheduleJobs){
						if (job.getState().getValue()<ScheduleJobState.DELIMITER.getValue()){
							Scheduler scheduler = ctx.getEngineModule(Scheduler.class, activityInstance.getProcessType());
							scheduler.unSchedule(job, ctx);
							ScheduleJobState scheduleJobState = ScheduleJobState.COMPLETED;
							try{
								scheduleJobState = ScheduleJobState.valueOf(state.getValue());
							}catch(Exception e){
								scheduleJobState = ScheduleJobState.ABORTED;
							}
							((ScheduleJobImpl)job).setState(scheduleJobState);
							scheduleJobPersister.saveOrUpdate(job);
						}
					}
				}
				*/
			}
			
			activityInstanceMgr.changeActivityInstanceState(session, activityInstance, state, workflowElement);
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}
	}
	
	
	/*
	public void abort(WorkflowSession session,Token token,Object workflowElement){
		//1、检验currentActivityInstance和currentProcessInstance的一致性
		ProcessInstance oldProcInst = session.getCurrentProcessInstance();
		ActivityInstance oldActInst = session.getCurrentActivityInstance();

		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ActivityInstancePersister actInstPersistenceService = persistenceService.getActivityInstancePersister();
		ProcessInstancePersister processInstancePersister = persistenceService.getProcessInstancePersister();
		
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getProcessInstanceId())){
			ProcessInstance procInst = processInstancePersister.find(ProcessInstance.class, token.getProcessInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
		}
		if (oldActInst==null || !oldActInst.getId().equals(token.getElementInstanceId())){
			ActivityInstance actInst = actInstPersistenceService.find(ActivityInstance.class, token.getElementInstanceId());
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
		}
		
		try{
			ActivityInstance thisActInst = session.getCurrentActivityInstance();
			// 将Workitem设置为Cancelled状态，
			WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
			List<WorkItem> workItems = workItemPersister.findWorkItemsForActivityInstance(thisActInst.getId());
			if (workItems!=null && workItems.size()>0){
				for (WorkItem wi : workItems){
					if (wi.getState().getValue()<WorkItemState.DELIMITER.getValue()){
						((WorkItemImpl)wi).setState(WorkItemState.CANCELLED);
						workItemPersister.saveOrUpdate(wi);
					}
				}
			}
			
			//将调度器中的timmer删除		
			ScheduleJobPersister scheduleJobPersister = persistenceService.getScheduleJobPersister();
			List<ScheduleJob> scheduleJobs = scheduleJobPersister.findScheduleJob4ActivityInstance(thisActInst.getId());
			if (scheduleJobs!=null && scheduleJobs.size()>0){
				for (ScheduleJob job : scheduleJobs){
					if (job.getState().getValue()<ScheduleJobState.DELIMITER.getValue()){
						Scheduler scheduler = ctx.getEngineModule(Scheduler.class, thisActInst.getProcessType());
						scheduler.unSchedule(job, ctx);
						
						((ScheduleJobImpl)job).setState(ScheduleJobState.ABORTED);
						scheduleJobPersister.saveOrUpdate(job);
					}
				}
			}
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActInst);
		}
	}
	*/
	
	private void initActivityInstanceVariables(VariablePersister variablePersister,
			ActivityInstance activityInstance,List<Property> processProperties,Map<String,Object> initVariables){
		if (processProperties!=null){
			for (Property property:processProperties){
				String valueAsStr = property.getInitialValueAsString();
				Object value = null;
				if (valueAsStr!=null && valueAsStr.trim()!=null){
					try {
						value = JavaDataTypeConvertor.convertToJavaObject(property.getDataType(), property.getInitialValueAsString(), property.getDataPattern());
					} catch (ClassCastException e) {
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+activityInstance.getSubProcessId()+", variableName="+property.getName(), e);
					} catch (ClassNotFoundException e) {
						//TODO 记录流程日志
						log.warn("Initialize process instance variable error, subflowId="+activityInstance.getSubProcessId()+", variableName="+property.getName(), e);
					}
				}
				//从initVariables中获取value
				if (initVariables!=null){
					Object tmpValue = initVariables.remove(property.getName());
					if (tmpValue!=null){
						try {
							value = JavaDataTypeConvertor.dataTypeConvert(property.getDataType(), tmpValue, property.getDataPattern());
						} catch (ClassCastException e) {
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+activityInstance.getSubProcessId()+", variableName="+property.getName(), e);
						} catch (ClassNotFoundException e) {
							//TODO 记录流程日志
							log.warn("Initialize process instance variable error, subflowId="+activityInstance.getSubProcessId()+", variableName="+property.getName(), e);
						}
					}
				}
				
				createVariable(variablePersister,activityInstance,property.getName(),value,property.getDataType());
			}
		}
		
		if (initVariables!=null && !initVariables.isEmpty()){
			Iterator<String> keySet = initVariables.keySet().iterator();
			while (keySet.hasNext()){
				String key = keySet.next();
				Object value = initVariables.get(key);
				createVariable(variablePersister,activityInstance,key,value,null);
			}
		}

	}
	
	private void createVariable(VariablePersister variablePersister,
			ActivityInstance activityInstance,String name ,Object value,QName dataType){
		VariableImpl v = new VariableImpl();
		((AbsVariable)v).setScopeId(activityInstance.getScopeId());
		((AbsVariable)v).setName(name);
		((AbsVariable)v).setProcessElementId(activityInstance.getProcessElementId());
		((AbsVariable)v).setPayload(value);
		
		if (value!=null){
			if (value instanceof org.w3c.dom.Document){
				if (dataType != null ){
					((AbsVariable)v).setDataType(dataType);
				}
				v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.w3c.dom.Document");
			}else if (value instanceof org.dom4j.Document){
				if (dataType != null ){
					((AbsVariable)v).setDataType(dataType);
				}
				v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.dom4j.Document");
			}else{
				((AbsVariable)v).setDataType(new QName(NameSpaces.JAVA.getUri(),value.getClass().getName()));
			}
			
		}
		((AbsVariable)v).setProcessId(activityInstance.getProcessId());
		((AbsVariable)v).setVersion(activityInstance.getVersion());
		((AbsVariable)v).setProcessType(activityInstance.getProcessType());
		

		variablePersister.saveOrUpdate(v);
	}
	
	protected List<String> determineNextTransitions(
			WorkflowSession session, Token token4Node, Node node){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		
		String className = OrSplitEvaluator.class.getName();
		
		SplitEvaluator splitEvaluator = this.splitEvaluatorRegistry.get(className);
		if (splitEvaluator==null){
			splitEvaluator = (SplitEvaluator)beanFactory.createBean(className);
			splitEvaluatorRegistry.put(className, splitEvaluator);
		}
		return splitEvaluator.determineNextTransitions(session, token4Node, node);
	}
}
