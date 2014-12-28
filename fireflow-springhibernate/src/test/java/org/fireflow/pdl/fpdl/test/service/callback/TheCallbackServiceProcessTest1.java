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
package org.fireflow.pdl.fpdl.test.service.callback;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;

import org.apache.commons.jxpath.JXPathContext;
import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.environment.Environment;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.OutputImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
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
import org.fireflow.service.callback.CallbackService;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.w3c.dom.Document;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class TheCallbackServiceProcessTest1 extends FireWorkflowJunitEnviroment{
	protected static final String processName = "TheSimplestSquenceProcess";
	protected static final String processDisplayName = "将流程发布为Webservice";
	protected static final String description = "流程中的Activity绑定Callback service";
	protected static final String bizId = "biz_123";
	protected static final String approveResult = "Very good!";
	protected static final String responseResult = "OK";
	
	protected static String targetNsUri = "http://www.fireflow.org/junit/callbackservice";
	protected static String serviceName = "AcceptApproveResult";
	protected static String serviceVersion = "1";
	protected static String interfaceName = "AcceptResult";
	protected static  QName serviceQName = new QName(targetNsUri,serviceName+"_"+serviceVersion);
	protected static  QName portQName = new QName(targetNsUri,interfaceName+"_Port");
	
	@Test
	public void testCallbackService(){
		final WorkflowSession session = WorkflowSessionFactory.createWorkflowSession(fireflowRuntimeContext,FireWorkflowSystem.getInstance());
		final WorkflowStatement stmt = session.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);
		
		
		//0、构建流程定义
		final WorkflowProcess process = getWorkflowProcess();
		
		//1、首先发布流程
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {

				
				//发布
				try {
					ProcessDescriptor descriptor = stmt.uploadProcessObject(process, 0);
					((ProcessDescriptorImpl)descriptor).setPublishState(true);
					stmt.updateProcessDescriptor(descriptor);
				} catch (InvalidModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 			
				return null;
			}
		});
		
		//2、调用CallbackManager的init方法发布Webservice
		//TODO 通过WorkflowServer发布webservice
		
		/*
		WebServiceManager callbackManager = this.runtimeContext.getEngineModule(WebServiceManager.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		try {
			callbackManager.publishAllCallbackServices();
		} catch (WebservicePublishException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		//3、启动流程实例		
		transactionTemplate.execute(new TransactionCallback(){
			public Object doInTransaction(TransactionStatus arg0) {
				
				//启动流程
				try {
					
					ProcessInstance processInstance = stmt.startProcess(process.getId(), bizId, null);
					
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
		
		//用Jaxws客户端调用Webservice		
		Environment env = fireflowRuntimeContext.getEngineModule(Environment.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		URL url = null;
		try{
			String contextPath = env.getWebserviceContextPath();
			if (!contextPath.startsWith("/")){
				contextPath = "/"+contextPath;
			}
			if (!contextPath.endsWith("/")){
				contextPath = contextPath+"/";
			}
			String address = "http://"+env.getWebserviceIP()+":"
						+Integer.toString(env.getWebservicePort())
						+contextPath;
			url = new URL(address+serviceQName.getLocalPart()+"?wsdl");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		javax.xml.ws.Service jawsService = javax.xml.ws.Service.create(url, serviceQName);
		Dispatch<Source> dispatch = jawsService.createDispatch(portQName, Source.class, javax.xml.ws.Service.Mode.PAYLOAD);
		
		String messageStr = "<cal:acceptRequest  xmlns:cal=\"http://www.fireflow.org/junit/callbackservice\">"+
         					"<cal:id>"+bizId+"</cal:id>"+
         					"<cal:approveResult>"+approveResult+"</cal:approveResult>"+
         					"</cal:acceptRequest>";
		java.io.ByteArrayInputStream byteInStream = new java.io.ByteArrayInputStream(messageStr.getBytes());
		StreamSource source = new StreamSource(byteInStream);
		
		Source response = dispatch.invoke(source);
		
		DOMResult result = new DOMResult();
//		StreamResult result = new StreamResult(System.out);
		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();	
			transformer = transformerFactory.newTransformer();
			transformer.transform(response, result);	
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Couldn't parse response stream.", e);
		} catch (TransformerException e) {
			throw new RuntimeException("Couldn't parse response stream.", e);
		}
	
		Document theResponsePayload = (Document)result.getNode();
		Assert.assertNotNull(theResponsePayload);
		JXPathContext jxpathContext = JXPathContext.newContext(theResponsePayload);
		jxpathContext.registerNamespace("ns0", targetNsUri);
		String response2 = (String)jxpathContext.getValue("ns0:acceptResponse/ns0:response2");
		
		Assert.assertEquals(responseResult, response2);
		
		this.assertResult(session);
	}
	
	
	@Override
	public void assertResult(WorkflowSession session) {
		// TODO Auto-generated method stub
		super.assertResult(session);
		
		// 验证ProcessInstance信息
		WorkflowQuery<ProcessInstance> q4ProcInst = session
				.createWorkflowQuery(ProcessInstance.class);
		ProcessInstance procInst = q4ProcInst.get(processInstanceId);
		Assert.assertNotNull(procInst);

		Assert.assertEquals(bizId, procInst.getBizId());
		Assert.assertEquals(processName, procInst.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,
				procInst.getProcessType());
		Assert.assertEquals(new Integer(1), procInst.getVersion());
		Assert.assertEquals(processName, procInst.getProcessName());// name
																	// 为空的情况下默认等于processId,
		Assert.assertEquals(processDisplayName, procInst.getProcessDisplayName());// displayName为空的情况下默认等于name
		Assert.assertEquals(ProcessInstanceState.COMPLETED, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getId(),
				procInst.getCreatorId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getName(),
				procInst.getCreatorName());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptId(),
				procInst.getCreatorDeptId());
		Assert.assertEquals(FireWorkflowSystem.getInstance().getDeptName(),
				procInst.getCreatorDeptName());
		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNotNull(procInst.getEndTime());
		Assert.assertNull(procInst.getParentActivityInstanceId());
		Assert.assertNull(procInst.getParentProcessInstanceId());
		Assert.assertNull(procInst.getParentScopeId());
		Assert.assertNull(procInst.getNote());

		// 验证Token信息
		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class);
		q4Token.add(
				Restrictions.eq(TokenProperty.PROCESS_INSTANCE_ID,
						processInstanceId)).addOrder(
				Order.asc(TokenProperty.STEP_NUMBER));

		List<Token> tokenList = q4Token.list();
		Assert.assertNotNull(tokenList);
		Assert.assertEquals(6, tokenList.size());

		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME, procInstToken.getElementId());
		Assert.assertEquals(processInstanceId,
				procInstToken.getElementInstanceId());
		Assert.assertEquals(processName, procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,
				procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.COMPLETED, procInstToken.getState());
		Assert.assertNull(procInstToken.getParentTokenId());
		Assert.assertTrue(procInstToken.isBusinessPermitted());
		Assert.assertEquals(procInst.getTokenId(), procInstToken.getId());

		Token startNodeToken = tokenList.get(1);
		Assert.assertEquals(processName, startNodeToken.getProcessId());
		Assert.assertEquals(new Integer(1), startNodeToken.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,
				startNodeToken.getProcessType());
		Assert.assertEquals(procInstToken.getId(),
				startNodeToken.getParentTokenId());
		Assert.assertTrue(startNodeToken.isBusinessPermitted());

		Token activity1Token = tokenList.get(3);

		// 验证ActivityInstance信息
		WorkflowQuery<ActivityInstance> q4ActInst = session
				.createWorkflowQuery(ActivityInstance.class);
		q4ActInst.add(
				Restrictions.eq(ActivityInstanceProperty.PROCESS_INSTANCE_ID,
						processInstanceId)).add(
				Restrictions.eq(ActivityInstanceProperty.NODE_ID, processName
						+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+ ".Activity1"));
		List<ActivityInstance> actInstList = q4ActInst.list();
		Assert.assertNotNull(actInstList);
		Assert.assertEquals(1, actInstList.size());
		ActivityInstance activityInstance = actInstList.get(0);
		Assert.assertEquals(bizId, activityInstance.getBizId());
		Assert.assertEquals("Activity1", activityInstance.getName());
		Assert.assertEquals("Activity1", activityInstance.getDisplayName());
		Assert.assertEquals(processInstanceId,
				activityInstance.getParentScopeId());
		Assert.assertNotNull(activityInstance.getCreatedTime());
		Assert.assertNotNull(activityInstance.getStartedTime());
		Assert.assertNotNull(activityInstance.getEndTime());
		Assert.assertNotNull(activityInstance.getExpiredTime());
		Assert.assertNotNull(activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getId(),
				activityInstance.getTokenId());
		Assert.assertEquals(activity1Token.getElementId(),
				activityInstance.getNodeId());
		Assert.assertEquals(activity1Token.getElementInstanceId(),
				activityInstance.getId());
		Assert.assertNotNull(activityInstance.getScopeId());

		Assert.assertEquals(new Integer(1), activityInstance.getVersion());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,
				activityInstance.getProcessType());
		Assert.assertEquals(procInst.getProcessName(),
				activityInstance.getProcessName());
		Assert.assertEquals(procInst.getProcessDisplayName(),
				activityInstance.getProcessDisplayName());
		
		Assert.assertEquals(ActivityInstanceState.COMPLETED, activityInstance.getState());
		
		//验证activity1的流程变量信息
		WorkflowQuery<Variable> q4Var = session.createWorkflowQuery(Variable.class);
		q4Var.add(Restrictions.eq(VariableProperty.PROCESS_ID, processName))
		      .add(Restrictions.eq(VariableProperty.SCOPE_ID, activityInstance.getId()));
		Variable var = q4Var.unique();
		Assert.assertEquals("approveResult",var.getName());
		Assert.assertEquals(approveResult, var.getPayload());
	}


	/* 
	 * Start-->CallbackService-->End
	 * 
	 * @see org.fireflow.FireWorkflowJunitEnviroment#createWorkflowProcess()
	 */
	@Override
	public WorkflowProcess createWorkflowProcess() {
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		process.setDescription(description);
		
		SubProcess mainflow = process.getMainSubProcess();
		
		PropertyImpl property = new PropertyImpl(mainflow,"id");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("");
		mainflow.getProperties().add(property);
		
		property = new PropertyImpl(mainflow,"response");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("OK");
		mainflow.getProperties().add(property);
		
		mainflow.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(process.getMainSubProcess(),"Start");
		
		ActivityImpl activity = new ActivityImpl(process.getMainSubProcess(),"Activity1");
		activity.setDuration(new Duration(6,Duration.DAY));
		property = new PropertyImpl(activity,"approveResult");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("");
		activity.getProperties().add(property);
		
		EndNodeImpl endNode = new EndNodeImpl(process.getMainSubProcess(),"End");
		
		mainflow.setEntry(startNode);
		mainflow.getStartNodes().add(startNode);
		mainflow.getActivities().add(activity);
		mainflow.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(process.getMainSubProcess(),"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity);
		startNode.getLeavingTransitions().add(transition1);
		activity.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(process.getMainSubProcess(),"activity2end");
		transition2.setFromNode(activity);
		transition2.setToNode(endNode);
		activity.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		mainflow.getTransitions().add(transition1);
		mainflow.getTransitions().add(transition2);
		
		//创建CallbackService 并绑定到activity1
		CallbackService callbackService = new CallbackService();
		callbackService.setName(serviceName);
		callbackService.setTargetNamespaceUri(this.targetNsUri);
		CommonInterfaceDef commonInterface = new CommonInterfaceDef();
		commonInterface.setName(interfaceName);
		callbackService.setInterface(commonInterface);
		
		OperationDefImpl op = new OperationDefImpl();
		op.setOperationName("accept");
		commonInterface.getOperations().add(op);
		
		InputImpl input = new InputImpl();
		input.setName("id");
		input.setDataType(new QName(NameSpaces.XSD.getUri(),"string"));
		op.getInputs().add(input);
		
		input = new InputImpl();
		input.setName("approveResult");
		input.setDataType(new QName(NameSpaces.XSD.getUri(),"string"));
		op.getInputs().add(input);
		
		OutputImpl output = new OutputImpl();
		output.setName("response1");
		output.setDataType(new QName(NameSpaces.XSD.getUri(),"string"));
		op.getOutputs().add(output);
		
		output = new OutputImpl();
		output.setName("response2");
		output.setDataType(new QName(NameSpaces.XSD.getUri(),"string"));
		op.getOutputs().add(output);

		ExpressionImpl correlation = new ExpressionImpl();
		correlation.setLanguage("JEXL");
		correlation.setBody(ScriptContextVariableNames.CURRENT_PROCESS_INSTANCE+
				".bizId==Xpath:getValue('"+ScriptContextVariableNames.INPUTS+
				"/ns0:"+op.getOperationName()+"Request/ns0:id')");
		correlation.getNamespaceMap().put("ns0",callbackService.getTargetNamespaceUri());
		callbackService.setCorrelation(correlation);
		
		
		//绑定
		ServiceBindingImpl svcBinding = new ServiceBindingImpl();
//		svcBinding.setService(callbackService);
		svcBinding.setServiceId(callbackService.getId());
//		svcBinding.setOperation(op);
		svcBinding.setOperationName(op.getOperationName());
		
		//io输入映射
		// id-->processVars.id
		AssignmentImpl assignment = new AssignmentImpl();
		ExpressionImpl from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.INPUTS+"/ns0:"+op.getOperationName()+"Request/ns0:id");
		from.setLanguage("XPATH");
		from.getNamespaceMap().put("ns0",callbackService.getTargetNamespaceUri());
		assignment.setFrom(from);
		
		ExpressionImpl to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/"+"id");
		to.setLanguage("XPATH");
		assignment.setTo(to);
		svcBinding.getInputAssignments().add(assignment);
		
		// approveResult-->activityVars.approveResult
		assignment = new AssignmentImpl();
		from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.INPUTS+"/ns0:"+op.getOperationName()+"Request/ns0:approveResult");
		from.setLanguage("XPATH");
		from.getNamespaceMap().put("ns0",callbackService.getTargetNamespaceUri());
		assignment.setFrom(from);
		
		to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.ACTIVITY_VARIABLES+"/"+"approveResult");
		to.setLanguage("XPATH");
		assignment.setTo(to);
		svcBinding.getInputAssignments().add(assignment);
		
		//io输出映射
		// processVars.id-->response1
		assignment = new AssignmentImpl();
		from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/id");
		from.setLanguage("XPATH");
		assignment.setFrom(from);
		
		to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.OUTPUTS+"/ns0:"+op.getOperationName()+"Response/ns0:response1");
		to.setLanguage("XPATH");
		to.getNamespaceMap().put("ns0", callbackService.getTargetNamespaceUri());
		assignment.setTo(to);
		svcBinding.getOutputAssignments().add(assignment);
		
		// processVars.response-->response2
		assignment = new AssignmentImpl();
		from = new ExpressionImpl();
		from.setBody(ScriptContextVariableNames.PROCESS_VARIABLES+"/response");
		from.setLanguage("XPATH");
		assignment.setFrom(from);
		
		to = new ExpressionImpl();
		to.setBody(ScriptContextVariableNames.OUTPUTS+"/ns0:"+op.getOperationName()+"Response/ns0:response2");
		to.setLanguage("XPATH");
		to.getNamespaceMap().put("ns0", callbackService.getTargetNamespaceUri());
		assignment.setTo(to);
		svcBinding.getOutputAssignments().add(assignment);
		
		//设置到activity和workflowprocess
		process.addService(callbackService);
		activity.setServiceBinding(svcBinding);
		
		return process;
	}

}
