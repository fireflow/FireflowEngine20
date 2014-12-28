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
package org.fireflow.service.email.send;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.impl.AssignmentImpl;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.servicedef.InterfaceDef;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.service.AbsTestContext;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@ContextConfiguration(locations = { "classpath:/applicationContext.xml"})
public class MailSenderInvokerTest  extends AbsTestContext {
	WorkflowSession session = null;
	ProcessInstance procInst = null;
	ActivityInstance actInst = null;
	/**
	 * Test method for {@link org.fireflow.engine.invocation.impl.AbsServiceInvoker#invoke(org.fireflow.client.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding, Object)}.
	 */
	@Test
	public void testInvoke() throws Exception{
		final MailSendServiceDef service = (MailSendServiceDef)this.buildService1();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("sendEMail",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(5, inputs.size());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(0, outputs.size());
		
		//插入测试数据
		TransactionTemplate transactionTemplate1 = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate1.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				try{
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					
					//构造流程变量
					procInst.setVariableValue(session, "process_id", "processId-123456");
					procInst.setVariableValue(session, "fromAddress", "firesoatest@163.com");
					
					String mailToList = "firesoatest@yeah.net;firesoatest@sohu.com";
					procInst.setVariableValue(session, "mailToList", mailToList);
					
					String ccList = "firesoatest@126.com";
					procInst.setVariableValue(session, "ccList", ccList);
					
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					procInst.setVariableValue(session, "subject", "测试邮件"+df.format(new Date()));
					
					procInst.setVariableValue(session, "emailBody", "这是一封普通的Text邮件体测试邮件");
					
					procInst.setVariableValue(session, "isHtml", Boolean.FALSE);
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造局部流程变量
					actInst.setVariableValue(session, "state", "0");
				}catch(Exception e){

					
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}

				
				return null;
			}
		});
		
		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					

					
					//构造service binding
					OperationDef operationDef = operations.get(0);
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("sendEMail");
//					serviceBinding.setOperation(operationDef);					
					

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					
					
					//arg1 = mailToList
					AssignmentImpl inputAssignment = new AssignmentImpl();
					String expressionBody = "/processVars/mailToList";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/mailToList");
					toExpression.setDataType(operationDef.getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg2 = ccToList
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/ccList");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/carbonCopyList");
					toExpression.setDataType(operationDef.getInputs().get(1).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg3 = subject
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/subject");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/subject");
					toExpression.setDataType(operationDef.getInputs().get(2).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg4 = body
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/emailBody");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/body");
					toExpression.setDataType(operationDef.getInputs().get(3).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg5 = isHtml
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/isHtml");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/bodyIsHtml");
					toExpression.setDataType(operationDef.getInputs().get(4).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);				
					
					//执行java 调用
					boolean b = invoker.invoke(session, actInst, serviceBinding, null, null);
					
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
				}

				catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}
				catch(Exception e){
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}
				return null;
			}
			
		});
	}
	
	
	@Test
	public void testInvoke2() throws Exception{
		final MailSendServiceDef service = (MailSendServiceDef)this.buildService2();
		
		//检验interface解析是否正确
		InterfaceDef _interface = service.getInterface();
		final List<OperationDef> operations = _interface.getOperations();
		Assert.assertNotNull(operations);
		Assert.assertEquals(1, operations.size());
		Assert.assertEquals("sendEMail",operations.get(0).getOperationName());
		List<Input> inputs = operations.get(0).getInputs();
		Assert.assertNotNull(inputs);
		Assert.assertEquals(5, inputs.size());
		
		List<Output> outputs = operations.get(0).getOutputs();
		Assert.assertNotNull(outputs);
		Assert.assertEquals(0, outputs.size());
		
		//插入测试数据
		TransactionTemplate transactionTemplate1 = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate1.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				try{
					RuntimeContext runtimeContext = (RuntimeContext)applicationContext.getBean("runtimeContext");
					session = WorkflowSessionFactory.createWorkflowSession(runtimeContext, FireWorkflowSystem.getInstance());
					
					//构造processInstance
					procInst = createProcessInstance(session, runtimeContext);
					((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(procInst);
					
					//构造流程变量
					procInst.setVariableValue(session, "process_id", "processId-123456");
					procInst.setVariableValue(session, "fromAddress", "firesoatest@163.com");
					
					String mailToList = "firesoatest@yeah.net;firesoatest@sohu.com";
					procInst.setVariableValue(session, "mailToList", mailToList);
					
					
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					procInst.setVariableValue(session, "subject", "测试邮件"+df.format(new Date()));
					
					procInst.setVariableValue(session, "emailBody", "<html><body style=\"color:red\">这是一封Html格式的测试邮件,通过SSL协议发送</body></html>");
					
					procInst.setVariableValue(session, "isHtml", Boolean.TRUE);
					
					//构造activityInstance
					actInst = createActivityInstance(runtimeContext, procInst);
					((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(actInst);
					
					//构造局部流程变量
					actInst.setVariableValue(session, "state", "0");
				}catch(Exception e){

					
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}

				
				return null;
			}
		});
		
		TransactionTemplate transactionTemplate = (TransactionTemplate)this.applicationContext.getBean("transactionTemplate");
		transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				//测试invoker
				String invokerClassName = service.getInvokerClassName();
				Class clz;
				try {
					clz = Class.forName(invokerClassName);
					ServiceInvoker invoker = (ServiceInvoker)clz.newInstance();
					

					
					//构造service binding
					OperationDef operationDef = operations.get(0);
					ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//					serviceBinding.setService(service);
					serviceBinding.setServiceId(service.getId());
					serviceBinding.setOperationName("sendEMail");
//					serviceBinding.setOperation(operationDef);					
					

					
					//构造输入映射
					List<Assignment> inputAssignments = new ArrayList<Assignment>();
					

					
					//arg1 = mailToList
					AssignmentImpl inputAssignment = new AssignmentImpl();
					String expressionBody = "/processVars/mailToList";
					ExpressionImpl exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					ExpressionImpl toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/mailToList");
					toExpression.setDataType(operationDef.getInputs().get(0).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					

					
					//arg3 = subject
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/subject");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/subject");
					toExpression.setDataType(operationDef.getInputs().get(2).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg4 = body
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/emailBody");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/body");
					toExpression.setDataType(operationDef.getInputs().get(3).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					//arg5 = isHtml
					inputAssignment = new AssignmentImpl();
					expressionBody = new String("/processVars/isHtml");
					 exp = new ExpressionImpl();
					exp.setBody(expressionBody);
					exp.setLanguage("xpath");
					inputAssignment.setFrom(exp);//
					
					toExpression = new ExpressionImpl();
					toExpression.setLanguage("xpath");
					toExpression.setBody("/inputs/bodyIsHtml");
					toExpression.setDataType(operationDef.getInputs().get(4).getDataType());
					inputAssignment.setTo(toExpression);
					inputAssignments.add(inputAssignment);
					
					serviceBinding.setInputAssignments(inputAssignments);				
					
					//执行java 调用
					boolean b = invoker.invoke(session, actInst, serviceBinding, null, null);
					
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
				}

				catch (ServiceInvocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}
				catch(Exception e){
					e.printStackTrace();
					Assert.fail(e.getMessage());
				}
				return null;
			}
			
		});
	}

	public MailSendServiceDef buildService1() throws Exception{
		MailSendServiceDef svcDef = new MailSendServiceDef();
		svcDef.setName("EmailService163");
		svcDef.setDisplayName("163邮件发送服务");
		svcDef.setSmtpServer("smtp.163.com");
		svcDef.setUserName("firesoatest@163.com");
		svcDef.setPassword("firesoa123");
		
		svcDef.afterPropertiesSet();
		
		return svcDef;
		
	}
	
	public MailSendServiceDef buildService2() throws Exception{
		MailSendServiceDef svcDef = new MailSendServiceDef();
		svcDef.setName("EmailService163");
		svcDef.setDisplayName("163邮件发送服务");
		svcDef.setUseSSL(true);
		svcDef.setSmtpPort(465);
		svcDef.setSmtpServer("smtp.163.com");
		svcDef.setUserName("firesoatest@163.com");
		svcDef.setPassword("firesoa123");
		
		svcDef.afterPropertiesSet();
		
		return svcDef;
		
	}
}
