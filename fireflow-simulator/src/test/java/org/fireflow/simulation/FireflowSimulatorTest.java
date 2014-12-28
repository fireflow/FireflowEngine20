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
package org.fireflow.simulation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.namespace.QName;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.model.resourcedef.impl.ResourceDefImpl;
import org.fireflow.pdl.fpdl.io.FPDLSerializer;
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
import org.fireflow.server.WorkflowEngineService;
import org.fireflow.service.human.HumanService;
import org.fireflow.simulation.client.SimulatorSessionFactory;
import org.fireflow.simulation.client.SimulatorStatement;
import org.fireflow.simulation.support.BreakPoint;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@ContextConfiguration(locations = {
"classpath:/fireflow_config/FireflowContext_Simulator.xml","classpath:/fireflow_config/FireflowContext-Main.xml"})
public class FireflowSimulatorTest  extends AbstractJUnit4SpringContextTests {
	private static final String URLString = "http://127.0.0.1:9099/FireWorkflowServices";
	
	protected static final String processName = "TheSimplestHumanProcessTest";

	protected static final String bizId = "ThisIsAJunitTest";

	protected static String transion1_Id = null;
	protected static String processInstanceId = null;
	
	WorkflowSession session = null;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testStartProcess() throws MalformedURLException {
		RuntimeContext ctx = (RuntimeContext)this.applicationContext.getBean(RuntimeContext.Fireflow_Runtime_Context_Name);
		
		WorkflowEngineService svc = ctx.getDefaultEngineModule(WorkflowEngineService.class);
		
		
		if (session==null){
			session = SimulatorSessionFactory
			.createSimulatorSession(URLString);
		}
		final SimulatorStatement stmt = (SimulatorStatement)session
				.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);


		// 1 首先上传流程定义
		WorkflowProcess process = createWorkflowProcess();
		
		try {
			FPDLSerializer ser = new FPDLSerializer();
			String processXml = ser.serializeToXmlString(process);
			ProcessDescriptor descriptor = stmt.uploadProcessXml(processXml, 0);
			((ProcessDescriptorImpl)descriptor).setPublishState(true);
			stmt.updateProcessDescriptor(descriptor);
			
		} catch (InvalidModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerializerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//2.设置断点
		BreakPoint breakPoint = new BreakPoint();
		breakPoint.setProcessId(processName);
		breakPoint.setElementId(transion1_Id);
		stmt.addBreakPoint(breakPoint);
		
		//3、 然后运行流程
		try {
			ProcessInstance processInstance = stmt.startProcess(
					processName, bizId, null);

			if (processInstance != null) {
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

		assertResult(session);
	}

	@Test
	public void forwordBreakPoint() throws MalformedURLException{
		if (session==null){
			session = SimulatorSessionFactory
			.createSimulatorSession(URLString);
		}
		final SimulatorStatement stmt = (SimulatorStatement)session
				.createWorkflowStatement(FpdlConstants.PROCESS_TYPE_FPDL20);

		BreakPoint breakPoint = new BreakPoint();
		breakPoint.setProcessId(processName);
		breakPoint.setElementId(transion1_Id);
		
		boolean result = stmt.forwardBreakPoint(breakPoint);
		
		Assert.assertTrue(result);
		
		assertResult2(session);
	}
	
	/**
	 * Start-->Activity(human service)-->End
	 * 
	 * @return
	 */
	public WorkflowProcess createWorkflowProcess() {
		// 构造流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,
				processName);

		SubProcess mainflow = process.getMainSubProcess();

		Duration du = new Duration(3, "DAY");
		mainflow.setDuration(du);

		PropertyImpl property = new PropertyImpl(mainflow, "applicant");// 流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),
				"java.lang.String"));
		property.setInitialValueAsString("张三");
		mainflow.getProperties().add(property);

		property = new PropertyImpl(process, "days");// 流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),
				"java.lang.Integer"));
		property.setInitialValueAsString("2");
		mainflow.getProperties().add(property);

		property = new PropertyImpl(process, "z");// 流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),
				"java.lang.Integer"));
		property.setInitialValueAsString("3");
		mainflow.getProperties().add(property);

		StartNodeImpl startNode = new StartNodeImpl(mainflow, "Start");
		ActivityImpl activity = new ActivityImpl(mainflow, "Activity1");
		activity.setDuration(du);
		EndNodeImpl endNode = new EndNodeImpl(mainflow, "End");

		mainflow.setEntry(startNode);
		mainflow.getStartNodes().add(startNode);
		mainflow.getActivities().add(activity);
		mainflow.getEndNodes().add(endNode);

		TransitionImpl transition1 = new TransitionImpl(mainflow,
				"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity);
		startNode.getLeavingTransitions().add(transition1);
		activity.getEnteringTransitions().add(transition1);
		transion1_Id = transition1.getId();

		TransitionImpl transition2 = new TransitionImpl(mainflow,
				"activity2end");
		transition2.setFromNode(activity);
		transition2.setToNode(endNode);
		activity.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);

		mainflow.getTransitions().add(transition1);
		mainflow.getTransitions().add(transition2);

		// 构造Human service
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

		activity.setServiceBinding(serviceBinding);

		// resourceBinding
		ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
		resourceBinding
				.setAssignmentStrategy(WorkItemAssignmentStrategy.ASSIGN_TO_ALL);
		resourceBinding.setDisplayName("审批科");

		activity.setResourceBinding(resourceBinding);

		// 业务领导
		ResourceDefImpl resource = new ResourceDefImpl();
		resource.getExtendedAttributes().put("FLAG", "1");
		resource.setName("Administrators");
		resource.setDisplayName("业务领导");
		resource.setResourceType(ResourceType.CUSTOM);
		resource.setResolverClassName("org.fireflow.pdl.fpdl.test.service.human.CustomerResourceResolver");
		process.addResource(resource);
		resourceBinding.addAdministratorRef(resource.getId());

		// 操作者
		resource = new ResourceDefImpl();
		resource.getExtendedAttributes().put("FLAG", "2");
		resource.setName("Performers");
		resource.setDisplayName("操作者");
		resource.setResourceType(ResourceType.CUSTOM);
		resource.setResolverClassName("org.fireflow.pdl.fpdl.test.service.human.CustomerResourceResolver");
		process.addResource(resource);
		resourceBinding.addPotentialOwnerRef(resource.getId());

		// 抄送人
		resource = new ResourceDefImpl();
		resource.getExtendedAttributes().put("FLAG", "3");
		resource.setName("cc");
		resource.setDisplayName("抄送");
		resource.setResourceType(ResourceType.CUSTOM);
		resource.setResolverClassName("org.fireflow.pdl.fpdl.test.service.human.CustomerResourceResolver");
		process.addResource(resource);
		resourceBinding.addReaderRef(resource.getId());

		return process;
	}

	public void assertResult(WorkflowSession session) {
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
		Assert.assertEquals(processName, procInst.getProcessDisplayName());// displayName为空的情况下默认等于name
		Assert.assertEquals(ProcessInstanceState.RUNNING, procInst.getState());
		Assert.assertEquals(Boolean.FALSE, procInst.isSuspended());
		Assert.assertEquals(SimulatorSessionFactory.SIMULATOR_DEFAULT_USER_NAME,
				procInst.getCreatorId());

		Assert.assertNotNull(procInst.getCreatedTime());
		Assert.assertNull(procInst.getEndTime());
		Assert.assertNotNull(procInst.getExpiredTime());
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
		Assert.assertEquals(3, tokenList.size());//系统停留在transition1的位置，因此token数量是3（subprocess,startnode,transition1)

		Token procInstToken = tokenList.get(0);
		Assert.assertEquals(processName+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME, procInstToken.getElementId());
		Assert.assertEquals(processInstanceId,
				procInstToken.getElementInstanceId());
		Assert.assertEquals(processName, procInstToken.getProcessId());
		Assert.assertEquals(FpdlConstants.PROCESS_TYPE_FPDL20,
				procInstToken.getProcessType());
		Assert.assertEquals(new Integer(1), procInstToken.getVersion());
		Assert.assertEquals(TokenState.RUNNING, procInstToken.getState());
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
	}
	
	
	

	public void assertResult2(WorkflowSession session){
		WorkflowQuery<ProcessInstance> q4ProcInst = session
		.createWorkflowQuery(ProcessInstance.class);
		ProcessInstance procInst = q4ProcInst.get(processInstanceId);
		Assert.assertNotNull(procInst);

		WorkflowQuery<Token> q4Token = session.createWorkflowQuery(Token.class);
		q4Token.add(
				Restrictions.eq(TokenProperty.PROCESS_INSTANCE_ID,
						processInstanceId)).addOrder(
				Order.asc(TokenProperty.STEP_NUMBER));

		List<Token> tokenList = q4Token.list();
		Assert.assertNotNull(tokenList);
		Assert.assertEquals(4, tokenList.size());
		
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
		Assert.assertNull(activityInstance.getEndTime());
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

		// 验证Activity1的WorkItem
		WorkflowQuery<WorkItem> q4WorkItem = session
				.createWorkflowQuery(WorkItem.class);
		q4WorkItem.add(Restrictions.eq(
				WorkItemProperty.ACTIVITY_ID, processName
						+WorkflowElement.ID_SEPARATOR+WorkflowProcess.MAIN_PROCESS_NAME+ ".Activity1"));
		List<WorkItem> workItemList = q4WorkItem.list();
		Assert.assertNotNull(workItemList);
		Assert.assertTrue(workItemList.size()>0);
	}

}
