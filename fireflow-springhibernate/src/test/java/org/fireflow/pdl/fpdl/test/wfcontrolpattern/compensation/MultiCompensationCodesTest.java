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
package org.fireflow.pdl.fpdl.test.wfcontrolpattern.compensation;

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
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.ModelElement;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.endnode.impl.ThrowCompensationFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.CatchCompensationFeatureImpl;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 多个CompensationCode的测试
 * 
 * @author 非也
 * @version 2.0
 */
public class MultiCompensationCodesTest extends FireWorkflowJunitEnviroment {

	protected static final String processName = "CompensationTestWithCompensationCode2";
	protected static final String processDisplayName = "补偿测试流程，测试过个CompensationCode，本例所有Activity被补偿";
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
				try {
					ProcessInstance processInstance = stmt.startProcess(process, bizId, null);
					
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
	 * Start-->Activity1(with catch compenstion)-->Activity2(with catch compensation)-->Activity3(with catch compensation)-->End(throw compensation)
	 *            |-->HandleCompensation1_1               |-->HandlerCompensation2_1            |-->HandlerCompensation3_1-->HandlerCompensation3_2
	 */
	public WorkflowProcess createWorkflowProcess(){
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		
		SubProcess subflow = process.getMainSubProcess();
		//*****************************************/
		//*************** 开始节点          *************/
		//****************************************/
		StartNodeImpl startNode = new StartNodeImpl(subflow,"Start");
		
		//*****************************************/
		//*********** Activity1及其异常分支 ********/
		//****************************************/
		ActivityImpl activity1 = new ActivityImpl(subflow,"Activity1");
		
		//异常捕获节点1
		StartNodeImpl catchCompensationNode1 = new StartNodeImpl(subflow,"CatchCompensation1");
		CatchCompensationFeatureImpl catchCompensationDecorator = new CatchCompensationFeatureImpl();
		catchCompensationDecorator.setAttachedToActivity(activity1);
		catchCompensationDecorator.setCompensationCode("ForActivity1");
		catchCompensationNode1.setFeature(catchCompensationDecorator);
		activity1.getAttachedStartNodes().add(catchCompensationNode1);
		
		ActivityImpl handleCompensationNode1_1 = new ActivityImpl(subflow,"HandleCompensation1_1");
		
		TransitionImpl transition1_1 = new TransitionImpl(subflow,"catchCompensation1_HandleCompensation1_1");
		transition1_1.setFromNode(catchCompensationNode1);
		transition1_1.setToNode(handleCompensationNode1_1);
		catchCompensationNode1.getLeavingTransitions().add(transition1_1);
		handleCompensationNode1_1.getEnteringTransitions().add(transition1_1);
		
		
		//*****************************************/
		//*********** Activity2及其异常分支 ********/
		//****************************************/
		ActivityImpl activity2 = new ActivityImpl(subflow,"Activity2");
		
		//异常捕获节点2
		StartNodeImpl catchCompensationNode2 = new StartNodeImpl(subflow,"CatchCompensation2");
		catchCompensationDecorator = new CatchCompensationFeatureImpl();
		catchCompensationDecorator.setAttachedToActivity(activity2);
		catchCompensationDecorator.setCompensationCode("ForActivity2AndActivity3");
		catchCompensationNode2.setFeature(catchCompensationDecorator);
		activity2.getAttachedStartNodes().add(catchCompensationNode2);
		
		ActivityImpl handleCompensationNode2_1 = new ActivityImpl(subflow,"HandleCompensation2_1");
		
		TransitionImpl transition2_1 = new TransitionImpl(subflow,"catchCompensation2_HandleCompensation2_1");
		transition2_1.setFromNode(catchCompensationNode2);
		transition2_1.setToNode(handleCompensationNode2_1);
		catchCompensationNode2.getLeavingTransitions().add(transition2_1);
		handleCompensationNode2_1.getEnteringTransitions().add(transition2_1);		
		
		//*****************************************/
		//*********** Activity3及其异常分支 ********/
		//****************************************/
		ActivityImpl activity3 = new ActivityImpl(subflow,"Activity3");
		
		//异常捕获节点3
		StartNodeImpl catchCompensationNode3 = new StartNodeImpl(subflow,"CatchCompensation3");
		catchCompensationDecorator = new CatchCompensationFeatureImpl();
		catchCompensationDecorator.setAttachedToActivity(activity3);
		catchCompensationDecorator.setCompensationCode("ForActivity2AndActivity3");
		catchCompensationNode3.setFeature(catchCompensationDecorator);
		activity3.getAttachedStartNodes().add(catchCompensationNode3);
		
		ActivityImpl handleCompensationNode3_1 = new ActivityImpl(subflow,"HandleCompensation3_1");
		
		TransitionImpl transition3_1 = new TransitionImpl(subflow,"catchCompensation3_HandleCompensation3_1");
		transition3_1.setFromNode(catchCompensationNode3);
		transition3_1.setToNode(handleCompensationNode3_1);
		catchCompensationNode3.getLeavingTransitions().add(transition3_1);
		handleCompensationNode3_1.getEnteringTransitions().add(transition3_1);	
		
		ActivityImpl handleCompensationNode3_2 = new ActivityImpl(subflow,"HandleCompensation3_2");
		
		TransitionImpl transition3_2 = new TransitionImpl(subflow,"catchCompensation3_1_HandleCompensation3_2");
		transition3_2.setFromNode(handleCompensationNode3_1);
		transition3_2.setToNode(handleCompensationNode3_2);
		handleCompensationNode3_1.getLeavingTransitions().add(transition3_2);
		handleCompensationNode3_2.getEnteringTransitions().add(transition3_2);	
		
		//*******************************************/
		//*********** 带有异常装饰器的结束节点 ********/
		//*******************************************/
		EndNodeImpl endNode = new EndNodeImpl(subflow,"End");
		ThrowCompensationFeatureImpl compensationDecorator = new ThrowCompensationFeatureImpl();
		compensationDecorator.addCompensationCode("ForActivity2AndActivity3");//只有CompensationCode=‘TheCompensationActivity’的catch compensation decorator才会被激发。
		compensationDecorator.addCompensationCode("ForActivity1");
		endNode.setFeature(compensationDecorator);
		
		//****************************************************/
		//****链接 activity1,activity2,activity3 **************/
		//****************************************************/
		TransitionImpl transition_start_activity1 = new TransitionImpl(subflow,"start_activity1");
		transition_start_activity1.setFromNode(startNode);
		transition_start_activity1.setToNode(activity1);
		startNode.getLeavingTransitions().add(transition_start_activity1);
		activity1.getEnteringTransitions().add(transition_start_activity1);
		
		TransitionImpl transition_activity1_activity2 = new TransitionImpl(subflow,"activity1_activity2");
		transition_activity1_activity2.setFromNode(activity1);
		transition_activity1_activity2.setToNode(activity2);
		activity1.getLeavingTransitions().add(transition_activity1_activity2);
		activity2.getEnteringTransitions().add(transition_activity1_activity2);
		
		TransitionImpl transition_activity2_activity3 = new TransitionImpl(subflow,"activity2_activity3");
		transition_activity2_activity3.setFromNode(activity2);
		transition_activity2_activity3.setToNode(activity3);
		activity2.getLeavingTransitions().add(transition_activity2_activity3);
		activity3.getEnteringTransitions().add(transition_activity2_activity3);
		
		TransitionImpl transition_activiyt3_end = new TransitionImpl(subflow,"activity3_end");
		transition_activiyt3_end.setFromNode(activity3);
		transition_activiyt3_end.setToNode(endNode);
		activity3.getLeavingTransitions().add(transition_activiyt3_end);
		endNode.getEnteringTransitions().add(transition_activiyt3_end);
		
		//组装到process中
		subflow.setEntry(startNode);
		subflow.getStartNodes().add(startNode);
		subflow.getStartNodes().add(catchCompensationNode1);
		subflow.getStartNodes().add(catchCompensationNode2);
		subflow.getStartNodes().add(catchCompensationNode3);
		
		subflow.getActivities().add(activity1);
		subflow.getActivities().add(activity2);
		subflow.getActivities().add(activity3);
		subflow.getActivities().add(handleCompensationNode1_1);
		subflow.getActivities().add(handleCompensationNode2_1);
		subflow.getActivities().add(handleCompensationNode3_1);
		subflow.getActivities().add(handleCompensationNode3_2);
		subflow.getEndNodes().add(endNode);
		
		
		subflow.getTransitions().add(transition1_1);
		subflow.getTransitions().add(transition2_1);
		subflow.getTransitions().add(transition3_1);
		subflow.getTransitions().add(transition3_2);

		subflow.getTransitions().add(transition_start_activity1);
		subflow.getTransitions().add(transition_activity1_activity2);
		subflow.getTransitions().add(transition_activity2_activity3);
		subflow.getTransitions().add(transition_activiyt3_end);
		
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
		Assert.assertEquals(ProcessInstanceState.COMPENSATED, procInst.getState());
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
		Assert.assertEquals(21, tokenList.size());
		
		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME,procInstToken.getElementId() );
		Assert.assertEquals(processInstanceId,procInstToken.getElementInstanceId());
		Assert.assertEquals(processName,procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20, procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.COMPENSATED, procInstToken.getState());
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
		Assert.assertEquals(activity1Token.getId(), activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(), activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(), activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());
		Assert.assertEquals(ActivityInstanceState.COMPENSATED, activityInstance.getState());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getProcessName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getProcessDisplayName(), activityInstance.getProcessDisplayName());
		
		//检查Activity2的状态
		q4ActInst.reset();
		q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".Activity2"));
		
		ActivityInstance actInst2 = q4ActInst.unique();
		Assert.assertEquals(ActivityInstanceState.COMPENSATED, actInst2.getState());
		
		
		//检查Activity3的状态
		q4ActInst.reset();
		q4ActInst = session.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID, processInstanceId))
				.add(Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName+ModelElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+".Activity3"));
		
		ActivityInstance actInst3 = q4ActInst.unique();
		Assert.assertEquals(ActivityInstanceState.COMPENSATED, actInst3.getState());
	}	
}
