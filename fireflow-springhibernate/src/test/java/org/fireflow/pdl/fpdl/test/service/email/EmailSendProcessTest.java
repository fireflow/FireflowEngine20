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
package org.fireflow.pdl.fpdl.test.service.email;

import java.util.List;

import javax.xml.namespace.QName;

import org.fireflow.FireWorkflowJunitEnviroment;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowSessionFactory;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.binding.impl.ServiceBindingImpl;
import org.fireflow.model.data.impl.ExpressionImpl;
import org.fireflow.model.data.impl.PropertyImpl;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.servicedef.OperationDef;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.impl.ActivityImpl;
import org.fireflow.pdl.fpdl.process.impl.EndNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.StartNodeImpl;
import org.fireflow.pdl.fpdl.process.impl.TransitionImpl;
import org.fireflow.pdl.fpdl.process.impl.WorkflowProcessImpl;
import org.fireflow.service.email.MailTemplate;
import org.fireflow.service.email.send.MailSendServiceDef;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.ScriptLanguages;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class EmailSendProcessTest   extends FireWorkflowJunitEnviroment{
	protected static final String processName = "TheSimplestSquenceProcess";
	protected static final String processDisplayName = "最简单的测试流程";
	protected static final String description = "一个最简单的顺序流程，没有绑定服务和resource";
	protected static final String bizId = "biz_123";
	
	
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
	
	/* 
	 * Start-->EndmailSendService-->End
	 * 
	 * @see org.fireflow.FireWorkflowJunitEnviroment#createWorkflowProcess()
	 */
	@Override
	public WorkflowProcess createWorkflowProcess() {
		WorkflowProcessImpl process = new WorkflowProcessImpl(processName,processDisplayName);
		process.setDescription(description);
		
		SubProcess mainflow = process.getMainSubProcess();
		
		PropertyImpl property = new PropertyImpl(mainflow,"mailToList");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("firesoatest@yeah.net;firesoatest@sohu.com");
		mainflow.getProperties().add(property);
		
		property = new PropertyImpl(mainflow,"ccToList");//流程变量x
		property.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		property.setInitialValueAsString("firesoatest@126.com");
		mainflow.getProperties().add(property);
		
		mainflow.setDuration(new Duration(5,Duration.MINUTE));
		
		StartNodeImpl startNode = new StartNodeImpl(process.getMainSubProcess(),"Start");
		
		ActivityImpl activity = new ActivityImpl(process.getMainSubProcess(),"Activity1");
		activity.setDuration(new Duration(6,Duration.DAY));
		
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
		
		//创建EmailSendService 并绑定到activity1
		MailSendServiceDef emailSendService = this.buildService1();
		
		ServiceBinding svcBinding = this.buildServiceBinding(emailSendService);
		
		
		//设置到activity和workflowprocess
		process.addService(emailSendService);
		activity.setServiceBinding(svcBinding);
		
		return process;
	}
	
	public ServiceBinding buildServiceBinding(MailSendServiceDef service){
		List<OperationDef> operations = service.getInterface().getOperations();
		//构造service binding
		OperationDef operationDef = operations.get(0);
		ServiceBindingImpl serviceBinding = new ServiceBindingImpl();
//		serviceBinding.setService(service);
		serviceBinding.setServiceId(service.getId());
		serviceBinding.setOperationName("sendEMail");
//		serviceBinding.setOperation(operationDef);					


		return serviceBinding;
	}
	
	public MailSendServiceDef buildService1() {
		MailSendServiceDef svcDef = new MailSendServiceDef();
		svcDef.setName("EmailSendServiceBy163");
		svcDef.setDisplayName("163邮件发送服务");
		svcDef.setSmtpServer("smtp.163.com");
		svcDef.setUserName("firesoatest@163.com");
		svcDef.setPassword("firesoa123");
		
		MailTemplate mailTemplate = new MailTemplate();
		svcDef.setMailTemplate(mailTemplate);
		
		ExpressionImpl exp = new ExpressionImpl();
		exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
		exp.setBody("${"+ScriptContextVariableNames.PROCESS_VARIABLES+".mailToList}");
		exp.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		mailTemplate.setMailToList(exp);
		
		exp = new ExpressionImpl();
		exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
		exp.setBody("${"+ScriptContextVariableNames.PROCESS_VARIABLES+".ccToList}");
		exp.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		mailTemplate.setCarbonCopyList(exp);
		
		exp = new ExpressionImpl();
		exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
		exp.setBody("通过MailTemplate组装邮件的测试");
		exp.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		mailTemplate.setSubject(exp);
		
		exp = new ExpressionImpl();
		exp.setLanguage(ScriptLanguages.UNIFIEDJEXL.name());
		exp.setBody("    这是一封通过MailTemplate组装邮件的测试邮件，\n本邮件的业务主键是${"+ScriptContextVariableNames.CURRENT_PROCESS_INSTANCE+".bizId}");
		exp.setDataType(new QName(NameSpaces.JAVA.getUri(),"java.lang.String"));
		mailTemplate.setBody(exp);
		
		try {
			svcDef.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return svcDef;
		
	}

}
