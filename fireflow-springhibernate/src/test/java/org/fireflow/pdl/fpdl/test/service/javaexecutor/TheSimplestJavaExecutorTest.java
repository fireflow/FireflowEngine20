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
package org.fireflow.pdl.fpdl.test.service.javaexecutor;

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
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenProperty;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.service.java.JavaService;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class TheSimplestJavaExecutorTest  extends FireWorkflowJunitEnviroment{
	protected static final String processName = "TheSimplestJavaExecutorProcess";
	protected static final String processDisplayName = "测试Java调用";
	protected static final String bizId = "TheJunitTester";

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
	 * Start-->Activity1-->Activity2-->End
	 */
	public WorkflowProcess createWorkflowProcess(){
		//构造流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		
		SubProcess subflow = process.getMainSubProcess();
		
		PropertyImpl property = new PropertyImpl(subflow,"x");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("1");
		subflow.getProperties().add(property);
		
		property = new PropertyImpl(subflow,"y");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("5");
		subflow.getProperties().add(property);
		
		property = new PropertyImpl(subflow,"z");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("0");
		subflow.getProperties().add(property);
		
		property = new PropertyImpl(subflow,"m");//流程变量m
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("0");
		subflow.getProperties().add(property);		
		
		StartNodeImpl startNode = new StartNodeImpl(subflow,"Start");
		ActivityImpl activity1 = new ActivityImpl(subflow,"Activity1");
		ActivityImpl activity2 = new ActivityImpl(subflow,"Activity2");
		EndNodeImpl endNode = new EndNodeImpl(subflow,"End");
		
		subflow.setEntry(startNode);
		subflow.getStartNodes().add(startNode);
		subflow.getActivities().add(activity1);
		subflow.getActivities().add(activity2);
		subflow.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(subflow,"start_activity1");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity1);
		startNode.getLeavingTransitions().add(transition1);
		activity1.getEnteringTransitions().add(transition1);
		
		TransitionImpl t_act1_act2 = new TransitionImpl(subflow,"activity1_activity2");
		t_act1_act2.setFromNode(activity1);
		t_act1_act2.setToNode(activity2);
		activity1.getLeavingTransitions().add(t_act1_act2);
		activity2.getEnteringTransitions().add(t_act1_act2);
		
		TransitionImpl transition2 = new TransitionImpl(subflow,"activity2_end");
		transition2.setFromNode(activity2);
		transition2.setToNode(endNode);
		activity2.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		subflow.getTransitions().add(transition1);
		subflow.getTransitions().add(transition2);
		subflow.getTransitions().add(t_act1_act2);
		
		//构造java service 1
		JavaService javaService = new JavaService();
		process.addService(javaService);
		
		javaService.setName("JavaService1");
		javaService.setDisplayName("Java调用1");
		javaService.setJavaClassName("org.fireflow.pdl.fpdl.test.service.javaexecutor.MathOperationBean");
		JavaInterfaceDef _interface = new JavaInterfaceDef();
		_interface.setInterfaceClassName("org.fireflow.pdl.fpdl.test.service.javaexecutor.IMathOperationBean");
		javaService.setInterface(_interface);
		
		//将service绑定到activity1
		OperationDef operation = _interface.getOperation("add");
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
		serviceBinding.setServiceId(javaService.getId());
		serviceBinding.setOperationName("add");
		
		//aa 赋值
		AssignmentImpl inputAssignment = new AssignmentImpl();
		ExpressionImpl expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.x");
		expression.setName("x");
		inputAssignment.setFrom(expression);
		
		expression = new ExpressionImpl();
		expression.setLanguage("XPATH");
		expression.setName("aa");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		expression.setBody(ScriptContextVariableNames.INPUTS+"/"+operation.getInputs().get(0).getName());
		inputAssignment.setTo(expression);
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		//bb赋值
		inputAssignment = new AssignmentImpl();
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.y");
		inputAssignment.setFrom(expression);
		
		expression = new ExpressionImpl();
		expression.setLanguage("XPATH");
		expression.setName("bb");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		expression.setBody(ScriptContextVariableNames.INPUTS+"/"+operation.getInputs().get(1).getName());
		inputAssignment.setTo(expression);
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		//返回值赋值
		AssignmentImpl outputAssignment = new AssignmentImpl();
		serviceBinding.getOutputAssignments().add(outputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody(ScriptContextVariableNames.OUTPUTS+"."+operation.getOutputs().get(0).getName());		
		expression.setName(operation.getOutputs().get(0).getName());
		outputAssignment.setFrom(expression);
		
		expression = new ExpressionImpl();
		expression.setLanguage("XPATH");
		expression.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/z");	
		expression.setName("z");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		outputAssignment.setTo(expression);

		activity1.setServiceBinding(serviceBinding);
		
		
		//将service1绑定到activity2
		operation = _interface.getOperation("multiply");
		serviceBinding = new ServiceBindingImpl();
//		serviceBinding.setService(javaService);
		serviceBinding.setServiceId(javaService.getId());
//		serviceBinding.setOperation(operation);
		serviceBinding.setOperationName(operation.getOperationName());
		
		//aa赋值
		inputAssignment = new AssignmentImpl();
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.y");
		inputAssignment.setFrom(expression);
		
		expression = new ExpressionImpl();
		expression.setLanguage("XPATH");
		expression.setName("aa");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		expression.setBody(ScriptContextVariableNames.INPUTS+"/"+operation.getInputs().get(0).getName());
		inputAssignment.setTo(expression);
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		//b赋值
		inputAssignment = new AssignmentImpl();
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody("processVars.z");	
		expression.setName("z");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		inputAssignment.setFrom(expression);
		
		expression = new ExpressionImpl();
		expression.setLanguage("XPATH");
		expression.setName("b");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		expression.setBody(ScriptContextVariableNames.INPUTS+"/"+operation.getInputs().get(1).getName());
		inputAssignment.setTo(expression);
		
		serviceBinding.getInputAssignments().add(inputAssignment);
		
		//返回值
		outputAssignment = new AssignmentImpl();
		serviceBinding.getOutputAssignments().add(outputAssignment);
		
		expression = new ExpressionImpl();
		expression.setLanguage("JEXL");
		expression.setBody(ScriptContextVariableNames.OUTPUTS+"."+operation.getOutputs().get(0).getName());		
		expression.setName(operation.getOutputs().get(0).getName());
		outputAssignment.setFrom(expression);
		
		expression = new ExpressionImpl();
		expression.setLanguage("XPATH");
		expression.setName("m");
		expression.setDataType(new QName(NameSpaces.JAVA.getUri(),"int"));
		expression.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/m");	
		outputAssignment.setTo(expression);


		activity2.setServiceBinding(serviceBinding);		
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
		Assert.assertEquals(ProcessInstanceState.COMPLETED, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(), procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(), procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNotNull(procInst.getEndTime());
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
		Assert.assertEquals(8, tokenList.size());
		
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
		Assert.assertNotNull(activityInstance.getEndTime());
		Assert.assertNotNull( activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getId(), activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(), activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(), activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());
		
		Assert.assertEquals(new Integer(1),activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,activityInstance.getProcessType());
		Assert.assertEquals(procInst.getProcessName(), activityInstance.getProcessName());
		Assert.assertEquals(procInst.getProcessDisplayName(), activityInstance.getProcessDisplayName());
		
		WorkflowQuery<Variable> q4Var = session.createWorkflowQuery(Variable.class);
		List<Variable> vars = q4Var.list();
		Assert.assertNotNull(vars);
		Assert.assertEquals(4, vars.size());
		
		for (Variable v : vars){
			if (v.getName().equals("z")){
				Assert.assertEquals(6, v.getPayload());
			}
			if (v.getName().equals("m")){
				Assert.assertEquals(30,v.getPayload());
			}
		}
	}	
}
