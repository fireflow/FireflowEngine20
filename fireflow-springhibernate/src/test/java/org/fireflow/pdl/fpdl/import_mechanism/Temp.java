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
package org.fireflow.pdl.fpdl.import_mechanism;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.impl.ResourceBindingImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.InputImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.io.SerializerException;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.resourcedef.ResourceType;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.model.resourcedef.impl.ResourceDefImpl;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.CommonInterfaceDef;
import org.fireflow.model.servicedef.impl.OperationDefImpl;
import org.fireflow.pdl.fpdl.io.FPDLSerializer;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.service.human.HumanService;
import org.firesoa.common.schema.NameSpaces;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class Temp {
	protected static final String processName = "TheSimplestHumanProcessTest";
	protected static final String bizId = "ThisIsAJunitTest";
	/**
	 * @param args
	 * @throws SerializerException 
	 * @throws InvalidModelException 
	 */
	public static void main(String[] args) throws IOException, InvalidModelException, SerializerException {
//		File f = new File("services.xml");
//		FileOutputStream fOut = new FileOutputStream(f);
//		
//		List<ServiceDef> services = buildServices();
//		ServiceParser.serialize(services, fOut);
//		
//		fOut.flush();
//		fOut.close();
		
		File f = new File("TheSimplestHumanProcessTest.xml");
		FileOutputStream fOut = new FileOutputStream(f);
		
		WorkflowProcess process = createWorkflowProcess();
		FPDLSerializer ser = new FPDLSerializer();
		ser.serialize(process, fOut);
		
		fOut.flush();
		fOut.close();
	}
	/**
	 * Start-->Activity(human service)-->End
	 * @return
	 */
	public static WorkflowProcess createWorkflowProcess(){
		//构造流程
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processName);
		
		SubProcess mainflow = process.getMainSubProcess();
		
		Duration du = new Duration(3,"DAY");
		mainflow.setDuration(du);
		
		PropertyImpl property = new PropertyImpl(mainflow,"applicant");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("张三");
		mainflow.getProperties().add(property);
		
		property = new PropertyImpl(process,"days");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("2");
		mainflow.getProperties().add(property);
		
		property = new PropertyImpl(process,"z");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.Integer"));
		property.setInitialValueAsString("3");
		mainflow.getProperties().add(property);
		
		StartNodeImpl startNode = new StartNodeImpl(mainflow,"Start");
		ActivityImpl activity = new ActivityImpl(mainflow,"Activity1");
		activity.setDuration(du);
		EndNodeImpl endNode = new EndNodeImpl(mainflow,"End");
		
		mainflow.setEntry(startNode);
		mainflow.getStartNodes().add(startNode);
		mainflow.getActivities().add(activity);
		mainflow.getEndNodes().add(endNode);
		
		TransitionImpl transition1 = new TransitionImpl(mainflow,"start2activity");
		transition1.setFromNode(startNode);
		transition1.setToNode(activity);
		startNode.getLeavingTransitions().add(transition1);
		activity.getEnteringTransitions().add(transition1);
		
		TransitionImpl transition2 = new TransitionImpl(mainflow,"activity2end");
		transition2.setFromNode(activity);
		transition2.setToNode(endNode);
		activity.getLeavingTransitions().add(transition2);
		endNode.getEnteringTransitions().add(transition2);
		
		mainflow.getTransitions().add(transition1);
		mainflow.getTransitions().add(transition2);
		
		//构造Human service			
		HumanService humanService = new HumanService();
		humanService.setName("Apply");
		humanService.setDisplayName("申请");
		humanService.setFormUrl("abc/zyx2.jsp");
		ExpressionImpl descExpression = new ExpressionImpl();
		descExpression.setLanguage("JEXL");
		descExpression.setBody("'请假申请[申请人:'+processVars.applicant+',请假天数:'+processVars.days+']'");
		humanService.setWorkItemSubject(descExpression);

		
		process.addService(humanService);

		
		//将service绑定到activity
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//		serviceBinding.setService(humanService);
		serviceBinding.setServiceId(humanService.getId());	

		activity.setServiceBinding(serviceBinding);
		
		//resourceBinding
		ResourceBindingImpl resourceBinding = new ResourceBindingImpl();
		resourceBinding.setAssignmentStrategy(WorkItemAssignmentStrategy.ASSIGN_TO_ALL);
		resourceBinding.setDisplayName("审批科");
		
		activity.setResourceBinding(resourceBinding);
		
		
		//业务领导
		ResourceDefImpl resource = new ResourceDefImpl();
		resource.getExtendedAttributes().put("FLAG", "1");
		resource.setName("Administrators");
		resource.setDisplayName("业务领导");
		resource.setResourceType(ResourceType.CUSTOM);
		resource.setResolverClassName("org.fireflow.pdl.fpdl.test.service.human.CustomerResourceResolver");
		process.addResource(resource);
		resourceBinding.addAdministratorRef(resource.getId());
		
		//操作者
		resource = new ResourceDefImpl();
		resource.getExtendedAttributes().put("FLAG", "2");
		resource.setName("Performers");
		resource.setDisplayName("操作者");
		resource.setResourceType(ResourceType.CUSTOM);
		resource.setResolverClassName("org.fireflow.pdl.fpdl.test.service.human.CustomerResourceResolver");
		process.addResource(resource);
		resourceBinding.addPotentialOwnerRef(resource.getId());
		
		//抄送人
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
	protected static List<ServiceDef> buildServices(){
		List<ServiceDef> list = new ArrayList<ServiceDef>();
		
		////////////service1//////////////////////////
		HumanService svc = new HumanService();
		svc.setName("Human_service_1");
		svc.setDisplayName("打开XYZ表单");
		svc.setDescription("This is a human service");
		svc.setBizCategory("abc\\xyz");
		svc.setInvokerBeanName("testInvokerBean");
		svc.setFormUrl("/xyz.jsp");
		
		//构建operation
		CommonInterfaceDef commonInterface = new CommonInterfaceDef();
		OperationDefImpl op = new OperationDefImpl();
		op.setOperationName("OpenForm");
		InputImpl input = new InputImpl();
		input.setName("bizObj1");
		input.setName("单据1");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.junit.Abc",NameSpaces.JAVA.getPrefix()));
		op.getInputs().add(input);
		
		input = new InputImpl();
		input.setName("todate");
		input.setDisplayName("当前日期");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.util.Date",NameSpaces.JAVA.getPrefix()));
		input.setDataPattern("yyyy-MM-dd");
		input.setDefaultValueAsString("2011-09-20");
		op.getInputs().add(input);
		
		commonInterface.getOperations().add(op);
		
		svc.setInterface(commonInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		list.add(svc);
		/////////////service2//////////////////////////
		svc = new HumanService();
		svc.setName("human_service_2");
		svc.setDisplayName("调用XYZ功能");
		svc.setDescription("This is a human service");
		svc.setBizCategory("abc\\xyz");

		
		commonInterface = new CommonInterfaceDef();
		op = new OperationDefImpl();
		op.setOperationName("OpenForm");
		input = new InputImpl();
		input.setName("bizObj2");
		input.setName("单据2");
		input.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.junit.XYZ",NameSpaces.JAVA.getPrefix()));
		op.getInputs().add(input);
		commonInterface.getOperations().add(op);
		
		svc.setInterface(commonInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		list.add(svc);
		return list;
	}

}
