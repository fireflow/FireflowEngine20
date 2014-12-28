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
package org.fireflow.engine.modules.instancemanager;

import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.model.InvalidModelException;

/**
 * 流程实例相关操作的入口。
 * @author 非也
 * @version 2.0
 */
public interface ProcessInstanceManager extends EngineModule{
	/**
	 * 创建一个流程实例，初始化流程变量，并启动该流程实例。该接口一般情况下用于被WorkflowStatementLocalImpl调用。
	 * @param session
	 * @param workflowProcessId
	 * @param version
	 * @param bizId
	 * @param variables
	 * @return
	 * @throws InvalidModelException
	 * @throws WorkflowProcessNotFoundException
	 * @throws InvalidOperationException
	 */
//	public ProcessInstance startProcess(WorkflowSession session,String workflowProcessId, int version,String processType,
//			String bizId, Map<String, Object> variables)
//			throws InvalidModelException,
//			WorkflowProcessNotFoundException, InvalidOperationException;	

	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowProcess,String processEntryId,ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance);
	
	public ProcessInstance runProcessInstance(WorkflowSession session,String processInstanceId,String processType,
			String bizId, Map<String, Object> variables);
	
	/**
	 * 响应SubflowBehavior的onTokenStateChanged();
	 * @param session
	 * @param processInstance
	 * @param newState
	 */
	public void changeProcessInstanceSate(WorkflowSession session,ProcessInstance processInstance,ProcessInstanceState newState,Object workflowElement);

	/**
	 * 发布流程实例事件
	 * @param session
	 * @param processInstance 流程实例
	 * @param workflowElement 流程，FPDL (2.0)语言中，该对象为对应的SubProcess
	 * @param eventType 事件类型
	 */
	public void fireProcessInstanceEvent(WorkflowSession session,ProcessInstance processInstance,Object workflowElement,ProcessInstanceEventTrigger eventType);
	
	//2012-02-14 被changeProcessInstanceState代替。
//	public ProcessInstance abortProcessInstance(WorkflowSession session,ProcessInstance processInstance);
	public ProcessInstance suspendProcessInstance(WorkflowSession session,ProcessInstance processInstance);
	public ProcessInstance restoreProcessInstance(WorkflowSession session,ProcessInstance processInstance);
}
