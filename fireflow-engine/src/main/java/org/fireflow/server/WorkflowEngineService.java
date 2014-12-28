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
package org.fireflow.server;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.fireflow.client.impl.WorkflowQueryImpl;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.model.InvalidModelException;
import org.fireflow.server.support.MapConvertor;
import org.fireflow.server.support.ObjectWrapper;
import org.fireflow.server.support.PropertiesConvertor;
import org.fireflow.server.support.ScopeBean;

/**
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */

@WebService(name=WorkflowEngineService.PORT_TYPE,
		targetNamespace=WorkflowEngineService.TARGET_NAMESPACE)
public interface WorkflowEngineService extends EngineModule {
	public static final String PORT_TYPE = "WorkflowEngineService";
	public static final String PORT_NAME = "WorkflowEngineServicePort";
	public static final String SERVICE_LOCAL_NAME = "WorkflowEngineServiceService";
	public static final String TARGET_NAMESPACE = "http://www.fireflow.org/services/WorkflowEngineService";
	public static final QName SERVICE_QNAME = new QName(TARGET_NAMESPACE,SERVICE_LOCAL_NAME);
	public static final QName PORT_QNAME = new QName(TARGET_NAMESPACE,PORT_NAME);
	/**
	 * login成功后，返回session; 如果登录失败，则抛出
	 * org.fireflow.engine.exception.EngineException
	 * @param userName
	 * @param password
	 * @return
	 */
	@WebMethod
	public @WebResult(name="workflowSession") WorkflowSessionLocalImpl login(
			@WebParam(name="userName") String userName,
			@WebParam(name="password") String password)throws EngineException;
	
	
	/**
	 * 返回的Descriptor里面包含流程版本信息。
	 * @param processXml
	 * @param processDescriptor
	 * @return
	 */
	@WebMethod
	public @WebResult(name="processDescriptor") ProcessDescriptorImpl uploadProcessXml(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="processXml") String processXml,
			@WebParam(name="version") int version)throws EngineException;

	@WebMethod
	public void updateProcessDescriptor(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="processDescriptor") ProcessDescriptorImpl processDescriptor);
	
	@WebMethod
	public @WebResult(name="processXml") String getWorkflowProcessXml(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="processId") String processId,
			@WebParam(name="processVersion") int processVersion,
			@WebParam(name="processType") String processType)throws EngineException;

	//此处是workflowEntity还是workflowEntities呢？应该是workflowEntity!
	@WebMethod
	public  @WebResult(name="workflowEntity") List<AbsWorkflowEntity> executeQueryList(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowQuery") WorkflowQueryImpl q);

	@WebMethod
	public @WebResult(name="workflowEntity") AbsWorkflowEntity getEntity(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="entityId") String entityId,
			@WebParam(name="entityClassName") String entityClassName);
	
	@WebMethod
	public @WebResult(name="entityCount") int executeQueryCount(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowQuery") WorkflowQueryImpl q);

	
	/**
	 * 远程创建流程实例。
	 * @param workflowProcessId
	 * @return
	 * @throws InvalidModelException
	 * @throws WorkflowProcessNotFoundException
	 */
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl createProcessInstance1(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId)
	throws InvalidModelException, WorkflowProcessNotFoundException;
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl createProcessInstance2(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="version") int version)
	throws InvalidModelException, WorkflowProcessNotFoundException;
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl createProcessInstance4(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="version") int version,
			@WebParam(name="subProcessId") String subProcessId)
	throws InvalidModelException, WorkflowProcessNotFoundException;
	
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl createProcessInstance3(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="subProcessId") String subProcessId)
	throws InvalidModelException, WorkflowProcessNotFoundException;
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl runProcessInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="processInstanceId") String processInstanceId,
			@WebParam(name="bizId") String bizId,
			@WebParam(name="variables") MapConvertor mapConvertor);
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl startProcess2(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="version") int version,
			@WebParam(name="bizId") String bizId,
			@WebParam(name="variables") MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException;
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl startProcess4(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="version") int version,
			@WebParam(name="subProcessId") String subProcessId,
			@WebParam(name="bizId") String bizId,
			@WebParam(name="variables") MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException;
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl startProcess1(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="bizId") String bizId,
			@WebParam(name="variables") MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException;
	
	@WebMethod
	public @WebResult(name="processInstance") ProcessInstanceImpl startProcess3(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workflowProcessId") String workflowProcessId,
			@WebParam(name="subProcessId") String subProcessId,
			@WebParam(name="bizId") String bizId,
			@WebParam(name="variables") MapConvertor mapConvertor)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException;
	
	@WebMethod
	public @WebResult(name="activityInstance") ActivityInstanceImpl  suspendActivityInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="activityInstanceId")String activityInstanceId,
			@WebParam(name="note")String note) throws InvalidOperationException ;
	
	@WebMethod
	public @WebResult(name="activityInstance") ActivityInstanceImpl  abortActivityInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="activityInstanceId")String activityInstanceId,
			@WebParam(name="note")String note) throws InvalidOperationException ;
	
	@WebMethod
	public @WebResult(name="activityInstance") ActivityInstanceImpl  restoreActivityInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="activityInstanceId")String activityInstanceId,
			@WebParam(name="note")String note) throws InvalidOperationException ;
	
	@WebMethod
	public @WebResult(name="activityInstance") ProcessInstanceImpl  abortProcessInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="activityInstanceId")String processInstanceId,
			@WebParam(name="note")String note) throws InvalidOperationException ;	
	
	@WebMethod
	public @WebResult(name="activityInstance") ProcessInstanceImpl  suspendProcessInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="activityInstanceId")String processInstanceId,
			@WebParam(name="note")String note) throws InvalidOperationException ;	
	
	@WebMethod
	public @WebResult(name="activityInstance") ProcessInstanceImpl  restoreProcessInstance(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="activityInstanceId")String processInstanceId,
			@WebParam(name="note")String note) throws InvalidOperationException ;	
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl claimWorkItem(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl withdrawWorkItem(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl disclaimWorkItem(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId,
			@WebParam(name="attachmentId") String attachmentId,
			@WebParam(name="attachmentType") String attachmentType,
			@WebParam(name="note") String note);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl completeWorkItem1(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId,
			@WebParam(name="attachmentId") String attachmentId,
			@WebParam(name="attachmentType") String attachmentType,
			@WebParam(name="note") String note);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl completeWorkItem2(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId,
			@WebParam(name="assignmentStrategy") MapConvertor assignmentStrategy,			
			@WebParam(name="attachmentId") String attachmentId,
			@WebParam(name="attachmentType") String attachmentType,
			@WebParam(name="note") String note);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl completeWorkItemAndJumpTo1(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId,
			@WebParam(name="targetActivityId") String targetActivityId,
			@WebParam(name="attachmentId") String attachmentId,
			@WebParam(name="attachmentType") String attachmentType,
			@WebParam(name="note") String note);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl completeWorkItemAndJumpTo2(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId,
			@WebParam(name="targetActivityId") String targetActivityId,
			@WebParam(name="assignmentStrategy") MapConvertor assignmentStrategy,			
			@WebParam(name="attachmentId") String attachmentId,
			@WebParam(name="attachmentType") String attachmentType,
			@WebParam(name="note") String note);
	
	@WebMethod
	public @WebResult(name="workItem") LocalWorkItemImpl reassignWorkItemTo(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="workItemId") String workItemId,
			@WebParam(name="reassignHandler") ReassignmentHandler reassignHandler,
			@WebParam(name="attachmentId") String attachmentId,
			@WebParam(name="attachmentType") String attachmentType,
			@WebParam(name="note") String note)
			throws InvalidOperationException;
	@WebMethod
	public @WebResult(name="varValue") ObjectWrapper getVariableValue(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="scope") ScopeBean scopeBean,
			@WebParam(name="varName") String varName);
	
	@WebMethod
	public @WebResult(name="varValues") MapConvertor getVariableValues(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="scope") ScopeBean scopeBean);
	
	@WebMethod
	public void setVariableValue1(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="scope") ScopeBean scopeBean,
			@WebParam(name="varName") String name,
			@WebParam(name="varValue") ObjectWrapper obj);
	
	@WebMethod
	public void setVariableValue2(
			@WebParam(name="sessionId") String sessionId,
			@WebParam(name="scope") ScopeBean scopeBean,
			@WebParam(name="varName") String name,
			@WebParam(name="varValue") ObjectWrapper obj,
			@WebParam(name="headers")PropertiesConvertor convertor );
	
	public @WebResult(name="result") boolean isSessionValid(@WebParam(name="sessionId")String sessionId);
	/* 下面两个方法用于测试 */
	
//	@WebMethod
//	public @WebResult(name="customer") Customer test(@WebParam(name="name") String name);
	/*
	@WebMethod
	public ContactInfo test2(String s);
	*/
	

}
