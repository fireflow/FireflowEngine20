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
package org.fireflow.pdl.fpdl.test.wfcontrolpattern.fault;

import java.util.List;

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
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.ModelElement;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.endnode.impl.ThrowFaultFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.CatchCompensationFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.CatchFaultFeatureImpl;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.RouterImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.KernelException;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.service.human.HumanService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.ibm.wsdl.ServiceImpl;

/**
 * 本用例中异常直接抛出
 * 
 * @author 非也
 * @version 2.0
 */
public class HandleFaultTest extends FireWorkflowJunitEnviroment {

	protected static final String processName = "ThrowFaultTest";
	protected static final String processDisplayName = "异常流程测试，由EndNode主动触发Fault，catFault节点依附在流程上";
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
		assertResult(session);
	}
	
	/**
	 * Start-->Router-->Activity1(with catch compenstion)-->End(throw fault)
	 *       	  |         |-->HandleCompensation
	 *            |-->Activity2-->End2
	 * Start(with catchFault)-->HandleFault
	 * 
	 * 执行流程级别的fault handler，HandleCompensation分支不会被执行，Activity2将被Cancelled
	 * 
	 */
	public WorkflowProcess createWorkflowProcess(){
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		
		SubProcess subflow = process.getMainSubProcess();
		
		StartNodeImpl startNode = new StartNodeImpl(subflow,"Start");
		
		RouterImpl router1 = new RouterImpl(subflow,"Router");
		
		//////////////////////////////////////////////////////////////
		//////////  Activity1 及其 异常处理分支，以及EndNode1////////////
		//////////////////////////////////////////////////////////////
		ActivityImpl activity1 = new ActivityImpl(subflow,"Activity1");
		
		//补偿捕获节点
		StartNodeImpl catchCompensationNode = new StartNodeImpl(subflow,"CatchCompensation");
		CatchCompensationFeatureImpl catchCompensationDecorator = new CatchCompensationFeatureImpl();
		catchCompensationDecorator.setAttachedToActivity(activity1);
		catchCompensationNode.setFeature(catchCompensationDecorator);
		
		activity1.getAttachedStartNodes().add(catchCompensationNode);
		
		ActivityImpl handleCompensationNode = new ActivityImpl(subflow,"HandleCompensation");
		
		TransitionImpl transition0 = new TransitionImpl(subflow,"catchCompensation2HandleCompensation");
		transition0.setFromNode(catchCompensationNode);
		transition0.setToNode(handleCompensationNode);
		catchCompensationNode.getLeavingTransitions().add(transition0);
		handleCompensationNode.getEnteringTransitions().add(transition0);
		
		
		EndNodeImpl endNode1 = new EndNodeImpl(subflow,"End1");
		ThrowFaultFeatureImpl decorator = new ThrowFaultFeatureImpl();
		decorator.setErrorCode("ErrorCode001");
		endNode1.setFeature(decorator);
		
		//////////////////////////////////////////////////////////////
		//////////  Activity1以及EndNode2                 ////////////
		//////////////////////////////////////////////////////////////			
		ActivityImpl activity2 = new ActivityImpl(subflow,"Activity2");
		//构造Human service	
		String formUrl = "xyz/Application.jsp";		
		HumanService humanService = new HumanService();
		humanService.setName("HumanService");
		humanService.setDisplayName("人工任务");
		humanService.setFormUrl(formUrl);
		process.addService(humanService);
		
		//将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//		serviceBinding.setService(humanService);
		serviceBinding.setServiceId(humanService.getId());	

		activity2.setServiceBinding(serviceBinding);	

		
		EndNodeImpl endNode2 = new EndNodeImpl(subflow,"End2");
		
		
		////////////////////////////////////////////////
		/////////   转移   ///////////////////////////////
		////////////////////////////////////////////////
		TransitionImpl t_start_router = new TransitionImpl(subflow,"start_router1");
		t_start_router.setFromNode(startNode);
		t_start_router.setToNode(router1);
		startNode.getLeavingTransitions().add(t_start_router);
		router1.getEnteringTransitions().add(t_start_router);
		
		TransitionImpl t_router1_activity1 = new TransitionImpl(subflow,"router1_activity1");
		t_router1_activity1.setFromNode(router1);
		t_router1_activity1.setToNode(activity1);
		router1.getLeavingTransitions().add(t_router1_activity1);
		activity1.getEnteringTransitions().add(t_router1_activity1);
		
		TransitionImpl t_activity1_end1 = new TransitionImpl(subflow,"activity1_end1");
		t_activity1_end1.setFromNode(activity1);
		t_activity1_end1.setToNode(endNode1);
		activity1.getLeavingTransitions().add(t_activity1_end1);
		endNode1.getEnteringTransitions().add(t_activity1_end1);
		
		TransitionImpl t_router1_activity2 = new TransitionImpl(subflow,"router1_activity2");
		t_router1_activity2.setFromNode(router1);
		t_router1_activity2.setToNode(activity2);
		router1.getLeavingTransitions().add(t_router1_activity2);
		activity2.getEnteringTransitions().add(t_router1_activity2);		
		
		TransitionImpl t_activity2_end2 = new TransitionImpl(subflow,"activity2_end2");
		t_activity2_end2.setFromNode(activity1);
		t_activity2_end2.setToNode(endNode1);
		activity2.getLeavingTransitions().add(t_activity2_end2);
		endNode2.getEnteringTransitions().add(t_activity2_end2);
		
		subflow.setEntry(startNode);
		subflow.getStartNodes().add(startNode);
		subflow.getRouters().add(router1);
		subflow.getActivities().add(activity1);
		subflow.getActivities().add(activity2);
		subflow.getEndNodes().add(endNode1);
		subflow.getEndNodes().add(endNode2);
		subflow.getStartNodes().add(catchCompensationNode);
		subflow.getActivities().add(handleCompensationNode);
		
		subflow.getTransitions().add(transition0);
		subflow.getTransitions().add(t_start_router);
		subflow.getTransitions().add(t_router1_activity1);
		subflow.getTransitions().add(t_activity1_end1);
		subflow.getTransitions().add(t_router1_activity2);
		subflow.getTransitions().add(t_activity2_end2);
		
		//异常处理子流程
		StartNodeImpl catchFaultStartNode = new StartNodeImpl(subflow,"catchFault");
		CatchFaultFeatureImpl catchFaultDecorator = new CatchFaultFeatureImpl();
		catchFaultDecorator.setErrorCode("ErrorCode001");
		catchFaultStartNode.setFeature(catchFaultDecorator);
		
		ActivityImpl handleFaultAct = new ActivityImpl(subflow,"HandleFaultActivity");
		
		TransitionImpl t_catchFault_handleFault = new TransitionImpl(subflow,"t_catchFault_handleFault");
		t_catchFault_handleFault.setFromNode(catchFaultStartNode);
		t_catchFault_handleFault.setToNode(handleFaultAct);
		catchFaultStartNode.getLeavingTransitions().add(t_catchFault_handleFault);
		handleFaultAct.getEnteringTransitions().add(t_catchFault_handleFault);
		
		subflow.getStartNodes().add(catchFaultStartNode);
		subflow.getActivities().add(handleFaultAct);
		subflow.getTransitions().add(t_catchFault_handleFault);
		
		
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
		Assert.assertEquals(processDisplayName, procInst.getProcessDisplayName());//displayName为空的情况下默认等于name
		Assert.assertEquals(ProcessInstanceState.FAULTED, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(), procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(), procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNotNull(procInst.getEndTime());
		Assert.assertNull(procInst.getExpiredTime());
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
		Assert.assertEquals(15, tokenList.size());
		
		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME,procInstToken.getElementId() );
		Assert.assertEquals(processInstanceId,procInstToken.getElementInstanceId());
		Assert.assertEquals(processName,procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20, procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.FAULTED, procInstToken.getState());
		Assert.assertNull(procInstToken.getParentTokenId());
		Assert.assertTrue(procInstToken.isBusinessPermitted());
		Assert.assertEquals(procInst.getTokenId(), procInstToken.getId());
		
		Token startNodeToken = tokenList.get(1);
		Assert.assertEquals(processName, startNodeToken.getProcessId());
		Assert.assertEquals(new Integer(1), startNodeToken.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20, startNodeToken.getProcessType());
		Assert.assertEquals(procInstToken.getId(), startNodeToken.getParentTokenId());
		Assert.assertTrue(startNodeToken.isBusinessPermitted());
		

		//检验fromToken的有效性
		for (Token t:tokenList){
			if (t!=procInstToken){
				Assert.assertNotNull(t.getFromToken());
			}
		}
		
		//验证ActivityInstance信息
		WorkflowQuery<ActivityInstance> q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".Activity1"));
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
		Assert.assertNotNull(activityInstance.getEndTime());
		Assert.assertNull(activityInstance.getExpiredTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertNotNull(activityInstance.getScopeId());
		//由于Activity1上的compensationCode与endnode1 中的compensationCode不匹配，Activity1实际上没有被补偿
		Assert.assertEquals(ActivityInstanceState.COMPLETED, activityInstance.getState());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getProcessName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getProcessDisplayName(), activityInstance.getProcessDisplayName());
		
		q4ActInst.reset();
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
		.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".Activity2"));
		ActivityInstance actInst2 = q4ActInst.unique();
		Assert.assertEquals(ActivityInstanceState.ABORTED, actInst2.getState());
		
		WorkflowQuery<WorkItem> q4WorkItem = session.createWorkflowQuery(WorkItem.class);
		q4WorkItem.add(Restrictions.eq(WorkItemProperty.PROCESS_INSTANCE_ID, processInstanceId))
		.add(Restrictions.eq(WorkItemProperty.ACTIVITY_ID, processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".Activity2"));
		WorkItem wi = q4WorkItem.unique();
		Assert.assertNotNull(wi);
		Assert.assertEquals(WorkItemState.ABORTED, wi.getState());
		
	}	

}
