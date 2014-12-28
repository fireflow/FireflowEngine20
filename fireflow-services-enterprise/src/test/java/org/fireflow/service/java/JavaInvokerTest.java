/**
 * Copyright 2007-2011 非也
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
package org.fireflow.service.java;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.VariableProperty;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.InputAssignmentImpl;
import org.fireflow.model.binding.impl.OutputAssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.model.servicedef.impl.JavaInterfaceDef;
import org.fireflow.service.AbsTestContext;
import org.fireflow.service.java.mock.Operand;
import org.fireflow.service.java.mock.Result;
import org.fireflow.service.mock.ActivityMock;
import org.firesoa.common.schema.NameSpaces;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
@SuppressWarnings("unused")
//hibernate
@ContextConfiguration(locations = { "classpath:/applicationContext.xml", 
		"classpath:/org/fireflow/service/java/JavaInvokerTest-context.xml"
                                })
public class JavaInvokerTest extends AbsTestContext {
	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;
	
	/**
	 * Test method for {@link org.firesoa.service.modules.java.JavaInvoker#invoke(org.firesoa.common.context.RuntimeContext, org.firesoa.ServiceDef.def.Service, java.lang.String, java.util.List, Object)}.
	 */
	@Test
	public void testInvoke() {
		final ServiceDef service = this.buildService1();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("calculate",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(3, inputs.size());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.MathsAction"),inputs.get(0).getDataType());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Operand"),inputs.get(1).getDataType());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"int"),inputs.get(2).getDataType());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(1, outputs.size());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result"), outputs.get(0).getDataType());
		
		//

		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					procInst.setVariableValue(session, "operand", new Operand(2,3));
					procInst.setVariableValue(session, "action", org.fireflow.service.java.mock.MathsAction.ADD);
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("calculate");

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					
					//arg0
					AssignmentImpl inputAssignment = new AssignmentImpl();
					String jsBody = "importClass(org.fireflow.service.java.mock.MathsAction);var action=org.fireflow.service.java.mock.MathsAction.MULTIPLY; action;";
//					String jsBody = "processVars.get(\"action\");";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setLanguage("JavaScript");
					exp.setName("from1");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0");
					toExpression.setName("arg0");
					toExpression.setDisplayName("arg0");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg1
					inputAssignment = new InputAssignmentImpl();
					jsBody = "processVars.get(\"operand\");";
					exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setName("from1");
					exp.setLanguage("JavaScript");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg1");
					toExpression.setName("arg1");
					toExpression.setDisplayName("arg1");
					toExpression.setDataType(operations.get(0).getInputs().get(1).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg2
					inputAssignment = new InputAssignmentImpl();
					jsBody = "2";
					exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setName("from2");
					exp.setLanguage("JavaScript");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg2");
					toExpression.setName("arg2");
					toExpression.setDataType(operations.get(0).getInputs().get(2).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);
					
					//构造输出映射
					List<Assignment> outputAssignments = new ArrayList<Assignment>();
					
					//  1、 输出到实例级别的流程变量
					AssignmentImpl outputAssignment = new AssignmentImpl();
					ExpressionImpl expression = new ExpressionImpl();
					expression.setLanguage("JavaScript");
					expression.setBody("outputs.get(\""+operations.get(0).getOutputs().get(0).getName()+"\");");
					expression.setName(JavaInterfaceDef.OUTPUT_NAME_PREFIX+"calculate");
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/processVars/result");
					toExpression.setName("result");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					//  2、输出到活动级别的流程变量
					outputAssignment = new OutputAssignmentImpl();
					expression = new ExpressionImpl();
					expression.setLanguage("JavaScript");
					expression.setBody("outputs.get(\""+operations.get(0).getOutputs().get(0).getName()+"\");");
					expression.setName(JavaInterfaceDef.OUTPUT_NAME_PREFIX+"calculate");
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/activityVars/result1");
					toExpression.setName("result1");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					//  3、输出到session attribute
					outputAssignment = new OutputAssignmentImpl();
					expression = new ExpressionImpl();
					expression.setLanguage("JavaScript");
					expression.setBody("outputs.get(\""+operations.get(0).getOutputs().get(0).getName()+"\");");
					expression.setName(JavaInterfaceDef.OUTPUT_NAME_PREFIX+"calculate");
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/sessionAttributes/result2");
					toExpression.setName("result2");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					serviceBinding.setOutputAssignments(outputAssignments);
					
					//执行java 调用
					boolean b = invoker.invoke(session, actInst, serviceBinding, null, new ActivityMock(service));
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		

		//验证
		// 1、验证processInstance中是否有result
		Object result = procInst.getVariableValue(session, "result");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Result);
		Assert.assertEquals(6, ((Result)result).getResult());
		
		// 2、验证 activityInstance中是否有 result
		result = actInst.getVariableValue(session, "result1");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Result);
		Assert.assertEquals(6, ((Result)result).getResult());
		
		// 3、验证session中是否有 result 
		result = ((WorkflowSessionLocalImpl)session).getAttribute("result2");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Result);
		Assert.assertEquals(6, ((Result)result).getResult());
		
		
		//顺便测试一下存取org.w3c.dom.Document和org.dom4j.Document;
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				try{
					String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Person id=\"123456\"><name>张三</name></Person>";
					ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
					SAXReader reader = new SAXReader();
					org.dom4j.Document dom4jDoc = reader.read(in);
					procInst.setVariableValue(session, "dom4jDoc", dom4jDoc);
					
					WorkflowQuery<Variable> variableQuery = session.createWorkflowQuery(Variable.class);
					Variable v = variableQuery.add(Restrictions.eq(VariableProperty.NAME, "dom4jDoc"))
								.add(Restrictions.eq(VariableProperty.SCOPE_ID, procInst.getId()))
								.unique();
					
					Assert.assertNotNull(v);
					Assert.assertTrue(v.getPayload() instanceof org.dom4j.Document);
					Assert.assertEquals(v.getHeaders().get(Variable.HEADER_KEY_CLASS_NAME), "org.dom4j.Document");

					
					DOMWriter domWriter = new DOMWriter();
					org.w3c.dom.Document w3cDom = domWriter.write(dom4jDoc);
					procInst.setVariableValue(session, "w3cDom", w3cDom);
					
					variableQuery.reset();
					v = variableQuery.add(Restrictions.eq(VariableProperty.NAME, "w3cDom"))
								.add(Restrictions.eq(VariableProperty.SCOPE_ID, procInst.getId()))
								.unique();
					
					Assert.assertNotNull(v);
					Assert.assertTrue(v.getPayload() instanceof org.w3c.dom.Document);
					Assert.assertEquals(v.getHeaders().get(Variable.HEADER_KEY_CLASS_NAME), "org.w3c.dom.Document");
				}catch(Exception e){
					e.printStackTrace();
					
				}

				return null;
			}
		});


	}


	@Test
	public void testInvoke2() {
		final ServiceDef service = this.buildService2();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("calculate",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(3, inputs.size());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.MathsAction"),inputs.get(0).getDataType());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Operand"),inputs.get(1).getDataType());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"int"),inputs.get(2).getDataType());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(1, outputs.size());
		Assert.assertEquals(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result"), outputs.get(0).getDataType());
		
		//

		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					procInst.setVariableValue(session, "operand", new Operand(2,3));
					procInst.setVariableValue(session, "action", org.fireflow.service.java.mock.MathsAction.ADD);
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造service binding
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("calculate");
//					serviceBinding.setOperation(operations.get(0));

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					
					//arg0
					InputAssignmentImpl inputAssignment = new InputAssignmentImpl();					
					String jsBody = "processVars.get(\"action\");";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setLanguage("JavaScript");
					exp.setName("from1");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg0");
					toExpression.setName("arg0");
					toExpression.setDataType(operations.get(0).getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg1
					inputAssignment = new InputAssignmentImpl();
					jsBody = "processVars.get(\"operand\");";
					exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setName("from1");
					exp.setLanguage("JavaScript");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg1");
					toExpression.setName("arg1");
					toExpression.setDataType(operations.get(0).getInputs().get(1).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg2
					inputAssignment = new InputAssignmentImpl();
					jsBody = "2";
					exp = new ExpressionImpl();
					exp.setBody(jsBody);
					exp.setName("from2");
					exp.setLanguage("JavaScript");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/arg2");
					toExpression.setName("arg2");
					toExpression.setDataType(operations.get(0).getInputs().get(2).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);
					
					//构造输出映射
					List<Assignment> outputAssignments = new ArrayList<Assignment>();
					
					//  1、 输出到实例级别的流程变量
					OutputAssignmentImpl outputAssignment = new OutputAssignmentImpl();
					ExpressionImpl expression = new ExpressionImpl();
					expression.setLanguage("JavaScript");
					expression.setBody("outputs.get(\""+operations.get(0).getOutputs().get(0).getName()+"\");");
					expression.setName(JavaInterfaceDef.OUTPUT_NAME_PREFIX+"calculate");
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/processVars/result4");
					toExpression.setName("result4");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					//  2、输出到活动级别的流程变量
					outputAssignment = new OutputAssignmentImpl();
					expression = new ExpressionImpl();
					expression.setLanguage("JavaScript");
					expression.setBody("outputs.get(\""+operations.get(0).getOutputs().get(0).getName()+"\");");
					expression.setName(JavaInterfaceDef.OUTPUT_NAME_PREFIX+"calculate");
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/activityVars/result5");
					toExpression.setName("result5");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					//  3、输出到session attribute
					outputAssignment = new OutputAssignmentImpl();
					expression = new ExpressionImpl();
					expression.setLanguage("JavaScript");
					expression.setBody("outputs.get(\""+operations.get(0).getOutputs().get(0).getName()+"\");");
					expression.setName(JavaInterfaceDef.OUTPUT_NAME_PREFIX+"calculate");
					outputAssignment.setFrom(expression);
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/sessionAttributes/result6");
					toExpression.setName("result6");
					toExpression.setDataType(new QName(NameSpaces.JAVA.getUri(),"org.fireflow.service.java.mock.Result",NameSpaces.JAVA.getPrefix()));
					
					outputAssignment.setTo(toExpression);
					
					outputAssignments.add(outputAssignment);
					
					serviceBinding.setOutputAssignments(outputAssignments);
					
					//执行java 调用
					boolean b = invoker.invoke(session, actInst, serviceBinding, null, new ActivityMock(service));
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				} catch (InvalidOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		});
		

		//验证
		// 1、验证processInstance中是否有result
		Object result = procInst.getVariableValue(session, "result4");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Result);
		Assert.assertEquals(5, ((Result)result).getResult());
		
		// 2、验证 activityInstance中是否有 result
		result = actInst.getVariableValue(session, "result5");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Result);
		Assert.assertEquals(5, ((Result)result).getResult());
		
		// 3、验证session中是否有 result 
		result = ((WorkflowSessionLocalImpl)session).getAttribute("result6");
		Assert.assertNotNull(result);
		Assert.assertTrue(result instanceof Result);
		Assert.assertEquals(5, ((Result)result).getResult());
		
	}


	public ServiceDef buildService1(){
		JavaService svc = new JavaService();
		svc.setName("java_service_1");
		svc.setDisplayName("测试Calculator");
		svc.setDescription("This is a java service");
		svc.setBizCategory("test\\modules\\java");
		svc.setJavaClassName("org.fireflow.service.java.mock.CalculatorImpl");
		
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.fireflow.service.java.mock.Calculator");
		svc.setInterface(javaInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		return svc;
	}
	
	public ServiceDef buildService2(){
		JavaService svc = new JavaService();
		svc.setName("java_service_2");
		svc.setDisplayName("测试Calculator2");
		svc.setDescription("This is a java service");
		svc.setBizCategory("test\\modules\\java");
		svc.setJavaBeanName("calculator");
		
		svc.setInvokerBeanName("javaServiceInvoker");
		
		JavaInterfaceDef javaInterface = new JavaInterfaceDef();
		javaInterface.setInterfaceClassName("org.fireflow.service.java.mock.Calculator");
		svc.setInterface(javaInterface);

		svc.getExtendedAttributes().put("key1", "value1");
		svc.getExtendedAttributes().put("key2", "value2");
		
		return svc;
	}
	

}
