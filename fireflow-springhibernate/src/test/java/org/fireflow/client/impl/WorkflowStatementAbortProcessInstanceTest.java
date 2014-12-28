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
package org.fireflow.client.impl;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
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
import org.fireflow.service.human.HumanService;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowStatementAbortProcessInstanceTest  extends FireWorkflowJunitEnviroment {
	private static final String processName = "Process4TestWorkflowStatement";
	private static final String processDisplayName = "测试流程";
	private static final String bizId = "bizobj123";
	private static final String note = "test abort process instance";
	/**
	 * Test method for {@link org.fireflow.client.WorkflowStatement#abortProcessInstance(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAbortProcessInstance() {
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
		
		WorkflowQuery<ProcessInstance> query = session.createWorkflowQuery(ProcessInstance.class);
		query.add(Restrictions.eq(ProcessInstanceProperty.ID, processInstanceId));
		ProcessInstance procInst = query.unique();
		
		Assert.assertEquals(ProcessInstanceState.RUNNING, procInst.getState());
		

		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {
				try {
					stmt.abortProcessInstance(processInstanceId, note);
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}
				return null;
			}
		});
		assertResult(session);

	}
	
	public void assertResult(WorkflowSession session){
		super.assertResult(session);
		WorkflowQuery<ProcessInstance> query = session.createWorkflowQuery(ProcessInstance.class);
		query.add(Restrictions.eq(ProcessInstanceProperty.ID, processInstanceId));
		ProcessInstance procInst = query.unique();
		
		Assert.assertEquals(ProcessInstanceState.ABORTED, procInst.getState());
		Assert.assertEquals(note, procInst.getNote());
		
	}

	/**
	 * Start-->Router-->Activity1(with catch compenstion)-->End1
	 *       	  |         |-->HandleCompensation
	 *            |-->Activity2-->End2
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
		return process;
	}
	
}
