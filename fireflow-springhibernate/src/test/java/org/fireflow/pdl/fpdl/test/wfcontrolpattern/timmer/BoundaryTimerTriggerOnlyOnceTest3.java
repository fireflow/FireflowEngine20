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
package org.fireflow.pdl.fpdl.test.wfcontrolpattern.timmer;

import java.util.List;

import javax.xml.namespace.QName;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobProperty;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.TimerOperationName;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.TimerStartFeatureImpl;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.service.human.HumanService;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 边定时器，仅触发一次；触发后所依附的ActivityInstance通过Workitem.complete()结束，
 * 则相应的timerStart也应处于Completed状态，且ScheduleJob处于Complted状态。
 * 定时器的触发时间是一个相对于ActivityInstance.startTime的相对时间。
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BoundaryTimerTriggerOnlyOnceTest3 extends FireWorkflowJunitEnviroment {

	protected static final String processName = "BoundaryTimerTriggerOnlyOnceTest1";
	protected static final String bizId = "ThisIsAJunitTest";

	@Test
	public void testStartProcess(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {
				//构建流程定义
				WorkflowProcess process = getWorkflowProcess();
				
				//启动流程
				ProcessInstance processInstance = null;
				try {
					processInstance = stmt.startProcess(process, bizId, null);
					
					if (processInstance!=null){
						processInstanceId = processInstance.getId();
					}
					
					return processInstance;
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WorkflowProcessNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				return null;
			}
			
		});
		
		//等待调度器结束
		Scheduler scheduler = fireflowRuntimeContext.getEngineModule(Scheduler.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		boolean hasJobInSchedule = scheduler.hasJobInSchedule(fireflowRuntimeContext);
		System.out.println();
		while(hasJobInSchedule){
			System.out.print("...");
			try {
				Thread.currentThread().sleep(3*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hasJobInSchedule = scheduler.hasJobInSchedule(fireflowRuntimeContext);
		}
		
		//将工作项结束掉
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {
				WorkflowQuery<WorkItem> workItemQuery = session.createWorkflowQuery(WorkItem.class);
				workItemQuery.add(Restrictions.eq(WorkItemProperty.BIZ_ID, bizId));
				WorkItem workItem = workItemQuery.unique();
				
				WorkflowStatement stmt = session.createWorkflowStatement();
				try {
					stmt.claimWorkItem(workItem.getId());
					stmt.completeWorkItem(workItem.getId(),null, null, null);
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		assertResult(session);
	}
	/**
	 * Start-->Activity(with timer start)-->End
	 *           |-->TimerHandler
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess(){
		//1、构造主干流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processName);
		

		SubProcess mainFlow = process.getMainSubProcess();
		
		PropertyImpl property = new PropertyImpl(mainFlow, "applicant");// 流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),
				"java.lang.String"));
		property.setInitialValueAsString("张三");
		mainFlow.getProperties().add(property);

		property = new PropertyImpl(process, "days");// 流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),
				"java.lang.Integer"));
		property.setInitialValueAsString("2");
		mainFlow.getProperties().add(property);
		
		mainFlow.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(mainFlow,"Start");
		ActivityImpl activity1 = new ActivityImpl(mainFlow,"Activity1");
		activity1.setDuration(new Duration(6,Duration.DAY));
		EndNodeImpl endNode = new EndNodeImpl(mainFlow,"End");
		
		mainFlow.setEntry(startNode);
		mainFlow.getStartNodes().add(startNode);
		mainFlow.getActivities().add(activity1);
		mainFlow.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(mainFlow,"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity1);
		startNode.getLeavingTransitions().add(transition1);
		activity1.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(mainFlow,"activity2end");
		transition2.setFromNode(activity1);
		transition2.setToNode(endNode);
		activity1.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		mainFlow.getTransitions().add(transition1);
		mainFlow.getTransitions().add(transition2);
		
		//2、构造Human service	
		HumanService humanService = new HumanService();
		humanService.setName("Apply");
		humanService.setDisplayName("申请");
		humanService.setFormUrl("abc/zyx.jsp");
		ExpressionImpl descExpression = new ExpressionImpl();
		descExpression.setLanguage("JEXL");
		descExpression
				.setBody("'请假申请[申请人:'+processVars.applicant+',请假天数:'+processVars.days+']'");
		humanService.setWorkItemSubject(descExpression);

		process.addService(humanService);

		// 将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//		serviceBinding.setService(humanService);
		serviceBinding.setServiceId(humanService.getId());

		activity1.setServiceBinding(serviceBinding);
		
		//3、构造一个定时器节点和相应的handler
		StartNodeImpl timerStartImpl = new StartNodeImpl(mainFlow,"timerStart");
		TimerStartFeatureImpl timerStartDecorator = new TimerStartFeatureImpl();
		timerStartDecorator.setTimerOperationName(TimerOperationName.TRIGGERED_ONLY_ONCE);
		
		ExpressionImpl expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.util.Date"));
		expression.setBody("DateUtil:dateAfter(currentActivityInstance.startedTime,1,'mi')");
		
		timerStartDecorator.setStartTimeExpression(expression);
		timerStartDecorator.setAttachedToActivity(activity1);
		
		timerStartImpl.setFeature(timerStartDecorator);
		
		activity1.getAttachedStartNodes().add(timerStartImpl);
		
		ActivityImpl timerHandler = new ActivityImpl(mainFlow,"timerHandler");
		
		TransitionImpl t_timerStart_timerHandler = new TransitionImpl(mainFlow ,"t_timerStart_timerHandler");
		
		t_timerStart_timerHandler.setFromNode(timerStartImpl);
		t_timerStart_timerHandler.setToNode(timerHandler);
		timerStartImpl.getLeavingTransitions().add(t_timerStart_timerHandler);
		timerHandler.getEnteringTransitions().add(t_timerStart_timerHandler);
		
		mainFlow.getStartNodes().add(timerStartImpl);
		mainFlow.getActivities().add(timerHandler);
		mainFlow.getTransitions().add(t_timerStart_timerHandler);
		
		
		return process;
	}
	
	public void assertResult(WorkflowSession session){
		super.assertResult(session);
		
		//验证ProcessInstance信息
		WorkflowQuery<ProcessInstance> q4ProcInst = session.createWorkflowQuery(ProcessInstance.class);
		ProcessInstance procInst = q4ProcInst.get(processInstanceId);
		Assert.assertNotNull(procInst);
		
		Assert.assertEquals(bizId,procInst.getBizId());
		Assert.assertEquals(processName, procInst.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20, procInst.getProcessType());
		Assert.assertEquals(new Integer(1), procInst.getVersion());
		Assert.assertEquals(processName, procInst.getProcessName());//name 为空的情况下默认等于processId,
		Assert.assertEquals(processName, procInst.getProcessDisplayName());//displayName为空的情况下默认等于name
		Assert.assertEquals(ProcessInstanceState.COMPLETED, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(), procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(), procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNotNull(procInst.getExpiredTime());
		Assert.assertNull(procInst.getParentActivityInstanceId());
		Assert.assertNull(procInst.getParentProcessInstanceId());
		Assert.assertNull(procInst.getParentScopeId());
		Assert.assertNull(procInst.getNote());
		
		//验证Token信息
		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class);
		q4Token.add(Restrictions.eq(TokenProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.addOrder(Order.asc(TokenProperty.STEP_NUMBER));
		
		List<Token> tokenList = q4Token.list();
		Assert.assertNotNull(tokenList);
		Assert.assertEquals(9, tokenList.size());
		
		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME,procInstToken.getElementId() );
		Assert.assertEquals(processInstanceId,procInstToken.getElementInstanceId());
		Assert.assertEquals(processName,procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20, procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.COMPLETED, procInstToken.getState());
		Assert.assertNull(procInstToken.getParentTokenId());
		Assert.assertTrue(procInstToken.isBusinessPermitted());
		Assert.assertEquals(procInst.getTokenId(), procInstToken.getId());
		
		Token startNodeToken = tokenList.get(1);
		Assert.assertEquals(processName, startNodeToken.getProcessId());
		Assert.assertEquals(new Integer(1), startNodeToken.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20, startNodeToken.getProcessType());
		Assert.assertEquals(procInstToken.getId(), startNodeToken.getParentTokenId());
		Assert.assertTrue(startNodeToken.isBusinessPermitted());
		
		Token activity1Token = tokenList.get(3);
		
		
		//验证ActivityInstance信息
		WorkflowQuery<ActivityInstance> q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".Activity1"));
		List<ActivityInstance> actInstList = q4ActInst.list();
		Assert.assertNotNull(actInstList);
		Assert.assertEquals(1, actInstList.size());
		ActivityInstance activityInstance = actInstList.get(0);
		Assert.assertEquals(bizId, activityInstance.getBizId());
		Assert.assertEquals("Activity1", activityInstance.getName());
		Assert.assertEquals("Activity1", activityInstance.getDisplayName());
		Assert.assertEquals(processInstanceId, activityInstance.getParentScopeId());
		Assert.assertNotNull(activityInstance.getCreatedTime());
		Assert.assertNotNull(activityInstance.getStartedTime());
		Assert.assertNotNull(activityInstance.getExpiredTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getId(), activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(), activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(), activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());
		Assert.assertEquals(ActivityInstanceState.COMPLETED, activityInstance.getState());
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getProcessName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getProcessDisplayName(), activityInstance.getProcessDisplayName());
		
		q4ActInst.reset();
		q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".timerStart"));
		ActivityInstance timerStartActInst = q4ActInst.unique();
		Assert.assertNotNull(timerStartActInst);
		Assert.assertEquals(ActivityInstanceState.ABORTED, timerStartActInst.getState());//边上的时间节点由主ActivityInstance来终结
		
		q4ActInst.reset();
		q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".timerHandler"));
		ActivityInstance timerHandlerActInst = q4ActInst.unique();
		Assert.assertNotNull(timerHandlerActInst);
		Assert.assertEquals(ActivityInstanceState.COMPLETED, timerHandlerActInst.getState());
		
		//验证ScheduleJob的状态
		WorkflowQuery<ScheduleJob> q4ScheduleJob = session.createWorkflowQuery(ScheduleJob.class);
		q4ScheduleJob .add(Restrictions.eq(ScheduleJobProperty.ACTIVITY_INSTANCE_ID, timerStartActInst.getId()));
		ScheduleJob scheduleJob = q4ScheduleJob.unique();
		Assert.assertNotNull(scheduleJob);
		Assert.assertEquals(ScheduleJobState.ABORTED, scheduleJob.getState());
		Assert.assertEquals(Integer.valueOf(1), scheduleJob.getTriggeredTimes());
	}
}
