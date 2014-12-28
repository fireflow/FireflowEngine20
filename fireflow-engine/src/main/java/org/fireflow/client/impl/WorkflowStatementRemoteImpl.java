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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.invocation.impl.DynamicAssignmentHandler;
import org.fireflow.engine.invocation.impl.ReassignmentHandler;
import org.fireflow.model.InvalidModelException;
import org.fireflow.server.WorkflowEngineService;
import org.fireflow.server.support.MapConvertor;
import org.fireflow.server.support.ObjectWrapper;
import org.fireflow.server.support.PropertiesConvertor;
import org.fireflow.server.support.ScopeBean;
import org.firesoa.common.util.JavaDataTypeConvertor;
import org.firesoa.common.util.Utils;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowStatementRemoteImpl implements WorkflowStatement {
	protected WorkflowEngineService workflowServer = null;
	protected String processType = null;
	protected WorkflowSessionRemoteImpl remoteSession = null;
	
	public WorkflowStatementRemoteImpl(WorkflowSessionRemoteImpl session,String processType){
		remoteSession = session;
		this.processType = processType;
	}
	public WorkflowEngineService getWorkflowServer() {
		return workflowServer;
	}

	public void setWorkflowServer(WorkflowEngineService workflowServer) {
		this.workflowServer = workflowServer;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getProcessType()
	 */
	public String getProcessType() {
		return processType;
	}


//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowStatement#getCurrentProcessInstance()
//	 */
//	@Override
//	public ProcessInstance getCurrentProcessInstance() {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowStatement#getCurrentActivityInstance()
//	 */
//	@Override
//	public ActivityInstance getCurrentActivityInstance() {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowStatement#getLatestCreatedWorkItems()
//	 */
//	@Override
//	public List<WorkItem> getLatestCreatedWorkItems() {
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setDynamicAssignmentHandler(java.lang.String, org.fireflow.engine.invocation.AssignmentHandler)
	 */
//	@Override
//	public WorkflowStatement setDynamicAssignmentHandler(String activityId,
//			AssignmentHandler dynamicAssignmentHandler) {
//		return null;
//	}

//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.WorkflowStatement#setAttribute(java.lang.String, java.lang.Object)
//	 */
//	@Override
//	public WorkflowStatement setAttribute(String name, Object attr) {
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, int, java.lang.String, java.util.Map)
	 */
	public ProcessInstance startProcess(String workflowProcessId, int version,
			String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		if (!checkVariableMap(variables))return null;
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(variables,MapConvertor.MAP_TYPE_VARIABLE);
		
		return this.workflowServer.startProcess2(this.remoteSession.getSessionId(),
				workflowProcessId,version,bizId,mapConvertor);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, int, java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInstance startProcess(String workflowProcessId, int version,
			String subProcessId, String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		if (!checkVariableMap(variables))return null;
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(variables,MapConvertor.MAP_TYPE_VARIABLE);
		
		return this.workflowServer.startProcess4(this.remoteSession.getSessionId(),
				workflowProcessId,version,subProcessId,bizId,mapConvertor);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInstance startProcess(String workflowProcessId,
			String subProcessId, String bizId, Map<String, Object> variables)
			throws InvalidModelException, WorkflowProcessNotFoundException,
			InvalidOperationException {
		if (!checkVariableMap(variables))return null;
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(variables,MapConvertor.MAP_TYPE_VARIABLE);
		
		return this.workflowServer.startProcess3(this.remoteSession.getSessionId(),
				workflowProcessId,subProcessId,bizId,mapConvertor);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInstance startProcess(String workflowProcessId, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		if (!checkVariableMap(variables))return null;
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(variables,MapConvertor.MAP_TYPE_VARIABLE);
		
		return this.workflowServer.startProcess1(this.remoteSession.getSessionId(),
				workflowProcessId,bizId,mapConvertor);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#startProcess(java.lang.Object, java.lang.String, java.util.Map)
	 */
	public ProcessInstance startProcess(Object process, String bizId,
			Map<String, Object> variables) throws InvalidModelException,
			WorkflowProcessNotFoundException, InvalidOperationException {
		throw new UnsupportedOperationException("远程接口不支持该方法，只能为流程库中已经存在的流程创建实例");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String)
	 */
	public ProcessInstance createProcessInstance(String workflowProcessId)
			throws InvalidModelException, WorkflowProcessNotFoundException {
		return workflowServer.createProcessInstance1(remoteSession.getSessionId(), workflowProcessId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.Object)
	 */
	public ProcessInstance createProcessInstance(Object process)
			throws InvalidModelException {
		throw new UnsupportedOperationException("远程接口不支持该方法，只能为流程库中已经存在的流程创建实例");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, int)
	 */
	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		return workflowServer.createProcessInstance2(remoteSession.getSessionId(), workflowProcessId,version);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, int, java.lang.String)
	 */
	public ProcessInstance createProcessInstance(String workflowProcessId,
			int version, String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		return workflowServer.createProcessInstance4(remoteSession.getSessionId(), workflowProcessId,version,subProcessId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#createProcessInstance(java.lang.String, java.lang.String)
	 */
	public ProcessInstance createProcessInstance(String workflowProcessId,
			String subProcessId) throws InvalidModelException,
			WorkflowProcessNotFoundException {
		return workflowServer.createProcessInstance3(remoteSession.getSessionId(), workflowProcessId,subProcessId);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#runProcessInstance(java.lang.String, java.lang.String, java.util.Map)
	 */
	public ProcessInstance runProcessInstance(String processInstanceId,
			String bizId, Map<String, Object> variables) {
		//远程接口只能传递简单类型的流程参数（包括java.util.Date)
		if (!checkVariableMap(variables))return null;
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(variables,MapConvertor.MAP_TYPE_VARIABLE);
		return workflowServer.runProcessInstance(remoteSession.getSessionId(),processInstanceId,bizId, mapConvertor);
	}
	
	/**
	 * 检查流程参数是否为简单类型
	 * @param variables
	 * @return
	 */
	private boolean checkVariableMap(Map<String,Object> variables){
		if (variables==null)return true;
		Iterator<Entry<String,Object>> entries = variables.entrySet().iterator();
		if (entries!=null){
			while (entries.hasNext()){
				Map.Entry<String, Object>  entry = entries.next();
				Object value = entry.getValue();
				if (value!=null){
					if (!JavaDataTypeConvertor.isPrimaryObject(value)){
						throw new EngineException("远程接口只能传递简单类型的流程变量（含java.util.Date,不含byte），而你传入了"+
								entry.getKey()+"="+value.toString()+"，值对象类型是"+value.getClass().getName());
					}
				}
			}
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#abortProcessInstance(java.lang.String, java.lang.String)
	 */
	public ProcessInstance abortProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		return this.workflowServer.abortProcessInstance(this.remoteSession.getSessionId(),
				processInstanceId,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#suspendProcessInstance(java.lang.String, java.lang.String)
	 */
	public ProcessInstance suspendProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		return this.workflowServer.suspendProcessInstance(this.remoteSession.getSessionId(),
				processInstanceId,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#restoreProcessInstance(java.lang.String, java.lang.String)
	 */
	public ProcessInstance restoreProcessInstance(String processInstanceId,
			String note) throws InvalidOperationException {
		return this.workflowServer.restoreProcessInstance(this.remoteSession.getSessionId(),
				processInstanceId,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#suspendActivityInstance(java.lang.String, java.lang.String)
	 */
	public ActivityInstance suspendActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		return this.workflowServer.suspendActivityInstance(this.remoteSession.getSessionId(),
				activityInstanceId,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#restoreActivityInstance(java.lang.String, java.lang.String)
	 */
	public ActivityInstance restoreActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		return this.workflowServer.restoreActivityInstance(this.remoteSession.getSessionId(),
				activityInstanceId,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#abortActivityInstance(java.lang.String, java.lang.String)
	 */
	public ActivityInstance abortActivityInstance(String activityInstanceId,
			String note) throws InvalidOperationException {
		return this.workflowServer.abortActivityInstance(this.remoteSession.getSessionId(),
				activityInstanceId,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#claimWorkItem(java.lang.String)
	 */
	public WorkItem claimWorkItem(String workItemId)
			throws InvalidOperationException {
		return this.workflowServer.claimWorkItem(this.remoteSession.getSessionId(),
				workItemId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#disclaimWorkItem(java.lang.String, java.util.Map)
	 */
	public WorkItem disclaimWorkItem(String workItemId,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		return this.workflowServer.disclaimWorkItem(this.remoteSession.getSessionId(),
				workItemId,attachmentId,attachmentType,note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#withdrawWorkItem(java.lang.String)
	 */
	public WorkItem withdrawWorkItem(String workItemId)
			throws InvalidOperationException {
		return this.workflowServer.withdrawWorkItem(this.remoteSession.getSessionId(),
				workItemId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItem(java.lang.String, java.util.Map)
	 */
	public WorkItem completeWorkItem(String workItemId,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		return this.workflowServer.completeWorkItem1(this.remoteSession.getSessionId(),
				workItemId,attachmentId,attachmentType,note);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItem(java.lang.String, java.util.Map, java.util.Map)
	 */
	public WorkItem completeWorkItem(String workItemId,
			Map<String, AssignmentHandler> assignmentStrategy,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		if(!checkAssignmentStrategy(assignmentStrategy)) return null;
		
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(assignmentStrategy, MapConvertor.MAP_TYPE_ASSIGNMENT_HANDLER);
		
		
		return this.workflowServer.completeWorkItem2(this.remoteSession.getSessionId(),
				workItemId,mapConvertor,attachmentId,attachmentType,note);

	}
	
	/**
	 * 远程接口的动态指定操作者只能使用DynamicAssignmentHandler，其他自行扩展的AssignmentHandler不能使用。
	 * 因为无法通过webservice进行传输。
	 * @param assitnmentStrategy
	 * @return
	 */
	private boolean checkAssignmentStrategy(Map<String, AssignmentHandler> assitnmentStrategy){
		Iterator<Entry<String,AssignmentHandler>> iterator = assitnmentStrategy.entrySet().iterator();
		while (iterator.hasNext()){
			Entry<String,AssignmentHandler> entry = iterator.next();
			String key = entry.getKey();
			AssignmentHandler value = entry.getValue();
			if (value==null){
				throw new EngineException("参数assignmentStrategy中的AssignmentHandler不能为null");
			}
			String className = DynamicAssignmentHandler.class.getName();
			if (!className.equals(value.getClass().getName())){
				throw new  EngineException("参数assignmentStrategy中的AssignmentHandler只能是"+
						className+"的实例，而传入参数中含有"+value.getClass().getName()+"实例");
			}
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItemAndJumpTo(java.lang.String, java.lang.String, java.util.Map)
	 */
	public WorkItem completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		return this.workflowServer.completeWorkItemAndJumpTo1(this.remoteSession.getSessionId(),
				workItemId,targetActivityId,attachmentId,attachmentType,note);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#completeWorkItemAndJumpTo(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
	 */
	public WorkItem completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,
			Map<String, AssignmentHandler> assignmentStrategy,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		if(!checkAssignmentStrategy(assignmentStrategy)) return null;
		
		MapConvertor mapConvertor = new MapConvertor();
		mapConvertor.putAll(assignmentStrategy, MapConvertor.MAP_TYPE_ASSIGNMENT_HANDLER);
		
		
		return this.workflowServer.completeWorkItemAndJumpTo2(this.remoteSession.getSessionId(),
				workItemId,targetActivityId,mapConvertor,attachmentId,attachmentType,note);


	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#reassignWorkItemTo(java.lang.String, org.fireflow.engine.invocation.AssignmentHandler, java.util.Map)
	 */
	public WorkItem reassignWorkItemTo(String workItemId,
			ReassignmentHandler reassignHandler,
			String attachmentId, String attachmentType, String note)
			throws InvalidOperationException {
		return this.workflowServer.reassignWorkItemTo(
				this.remoteSession.getSessionId(),workItemId,
				reassignHandler,attachmentId, attachmentType, note);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadProcessObject(java.lang.Object, boolean, java.util.Map)
	 */
	public ProcessDescriptor uploadProcessObject(Object processObject,
			int version)
			throws InvalidModelException {
		throw new UnsupportedOperationException("远程接口不支持该方法，请先将流程对象转换成Xml或者InputStream，然后调用uploadProcessStream(String processXml...)或者uploadProcessStream(InputStream inStream...)");
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadProcessStream(java.io.InputStream, boolean, java.util.Map)
	 */
	public ProcessDescriptor uploadProcessStream(InputStream stream,
			int version)
			throws InvalidModelException {
		InputStream inStream = stream;
		
		if (!stream.markSupported()){
			inStream = new BufferedInputStream(stream) ;
		}
		try {
			String charset = Utils.findXmlCharset(inStream);
			String processXml = Utils.inputStream2String(inStream, charset);
			return this.uploadProcessXml(processXml, version);
		} catch (IOException e) {
			throw new InvalidModelException(e);
		}
	}
	
	public ProcessDescriptor uploadProcessXml(String processXml,int version)throws InvalidModelException{
		return this.workflowServer.uploadProcessXml(this.remoteSession.getSessionId(), processXml,version);
	}

	public void updateProcessDescriptor(ProcessDescriptor processDescriptor){
		this.workflowServer.updateProcessDescriptor(this.remoteSession.getSessionId(),(ProcessDescriptorImpl)processDescriptor);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadServicesStream(java.io.InputStream, java.lang.Boolean, java.util.Map)
	 */
	/*
	public List<ServiceDescriptor> uploadServicesStream(InputStream inStream,
			Boolean publishState,
			Map<ServiceDescriptorProperty, Object> metadata)
			throws InvalidModelException {
		throw new UnsupportedOperationException("远程接口暂不支持该方法");

	}
	*/

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#uploadResourcesStream(java.io.InputStream, java.lang.Boolean, java.util.Map)
	 */
	/*
	public List<ResourceDescriptor> uploadResourcesStream(InputStream inStream,
			Boolean publishState,
			Map<ResourceDescriptorProperty, Object> metadata)
			throws InvalidModelException {
		throw new UnsupportedOperationException("远程接口暂不支持该方法");

	}
	*/

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getVariableValue(org.fireflow.engine.entity.runtime.Scope, java.lang.String)
	 */
	public Object getVariableValue(Scope scope, String name) {
		if (scope==null || name==null)return null;
		ScopeBean bean = ScopeBean.fromScopeObject(scope);
		ObjectWrapper objWrapper = this.workflowServer.getVariableValue(this.remoteSession.getSessionId(),
				bean,name);
		
		if (objWrapper!=null){
			return objWrapper.getOriginalValue();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setVariableValue(org.fireflow.engine.entity.runtime.Scope, java.lang.String, java.lang.Object)
	 */
	public void setVariableValue(Scope scope, String name, Object value)
			throws InvalidOperationException {
		if (!JavaDataTypeConvertor.isPrimaryObject(value)){
			throw new InvalidOperationException("远程接口只能设置基本数据类型的流程变量（含java.util.Date，不含byte）；而当前设置数据类型是"+
					value.getClass().getName());
		}
		if (scope==null){
			throw new InvalidOperationException("scope参数不能为null");
		}
		
		ScopeBean bean = ScopeBean.fromScopeObject(scope);

		ObjectWrapper objWrapper = new ObjectWrapper();
		objWrapper.setOriginalValue(value);
		
		this.workflowServer.setVariableValue1(this.remoteSession.getSessionId(),
				bean,name,objWrapper);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#setVariableValue(org.fireflow.engine.entity.runtime.Scope, java.lang.String, java.lang.Object, java.util.Map)
	 */
	public void setVariableValue(Scope scope, String name, Object value,
			Properties headers) throws InvalidOperationException {
		if (!JavaDataTypeConvertor.isPrimaryObject(value)){
			throw new InvalidOperationException("远程接口只能设置基本数据类型的流程变量（含java.util.Date，不含byte）；而当前设置数据类型是"+
					value.getClass().getName());
		}
		if (scope==null){
			throw new InvalidOperationException("scope参数不能为null");
		}
		
		ScopeBean bean = ScopeBean.fromScopeObject(scope);

		ObjectWrapper objWrapper = new ObjectWrapper();
		objWrapper.setOriginalValue(value);
		
		PropertiesConvertor propConvertor = new PropertiesConvertor();
		propConvertor.putAll(headers);
		
		this.workflowServer.setVariableValue2(this.remoteSession.getSessionId(),
				bean, name, objWrapper,propConvertor);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getVariableValues(org.fireflow.engine.entity.runtime.Scope)
	 */
	public Map<String, Object> getVariableValues(Scope scope) {
		if (scope==null)return null;
		ScopeBean bean = ScopeBean.fromScopeObject(scope);
		
		MapConvertor convertor = this.workflowServer.getVariableValues(this.remoteSession.getSessionId(),
				bean);
		if (convertor!=null){
			return convertor.getMap();
		}else{
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowStatement#getWorkflowProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	public Object getWorkflowProcess(ProcessKey key)
			throws InvalidModelException {
		throw new UnsupportedOperationException("远程接口不支持该方法");
	}
	
	public String getWorkflowProcessXml(ProcessKey key) {
		return this.workflowServer.getWorkflowProcessXml(this.remoteSession.getSessionId(),
				key.getProcessId(), key.getVersion(), key.getProcessType());
	}

	public Object getWorkflowDefinitionElement(Scope scope)throws InvalidModelException{
		throw new UnsupportedOperationException("远程接口不支持该方法");
	}
}
