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
package org.fireflow.pdl.fpdl.test.wfcontrolpattern.fault;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.endnode.impl.ThrowFaultFeatureImpl;
import org.fireflow.pdl.fpdl.process.features.startnode.impl.CatchCompensationFeatureImpl;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.RouterImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.KernelException;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class HandleFaultTest2 extends FireWorkflowJunitEnviroment {
	protected static final String processName = "ThrowFaultTest";
	protected static final String processDiplayName = "异常流程测试，由EndNode主动触发Fault，无catFault节点，系统报错";
	protected static final String bizId = "ThisIsAJunitTest";

	@Test(expected=KernelException.class)
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
	 * Start-->Router-->Activity1(with catch compenstion)-->End1(throw fault)
	 *       	  |         |-->HandleCompensation
	 *            |-->Activity2-->End2
	 * 流程级别的异常在End1抛出后，没有相应的处理分支，系统将报错
	 * 
	 * 
	 * 
	 */
	public WorkflowProcess createWorkflowProcess(){
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDiplayName);
		
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
		/* 取消该异常处理子流程，使得流程报错。
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
		
		*/
		return process;
	}
}
