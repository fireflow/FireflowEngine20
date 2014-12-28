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
package org.fireflow.pdl.bpel.enginemodules;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.pdl.bpel.BpelConstants;
import org.firesoa.common.schema.NameSpaces;

/**
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceManagerBpelImpl extends AbsProcessInstanceManager {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#createProcessInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, java.lang.String, java.util.Map, org.fireflow.engine.entity.repository.ProcessDescriptor, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public ProcessInstance createProcessInstance(WorkflowSession session,
			Object workflowProcess,String processEntryId, ProcessDescriptor descriptor,
			ActivityInstance parentActivityInstance) {
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();

		CalendarService calendarService = context.getDefaultEngineModule(CalendarService.class);
		User u = sessionLocal.getCurrentUser();
		
		ProcessInstanceImpl processInstance = new ProcessInstanceImpl();
		processInstance.setProcessId(descriptor.getProcessId());
		processInstance.setVersion(descriptor.getVersion());
		processInstance.setProcessType(descriptor.getProcessType());
		
		processInstance.setSubProcessId(descriptor.getProcessId());
		processInstance.setSubProcessName(descriptor.getName());
		processInstance.setSubProcessDisplayName(descriptor.getDisplayName());
		
//		processInstance.setBizId(bizId);
		processInstance.setProcessName(descriptor.getName());
		processInstance.setProcessDisplayName(descriptor.getDisplayName());
		processInstance.setState(ProcessInstanceState.INITIALIZED);

		processInstance.setCreatedTime(calendarService.getSysDate());

		processInstance.setCreatorId(u.getId());
		processInstance.setCreatorName(u.getName());
		processInstance.setCreatorDeptId(u.getDeptId());
		processInstance.setCreatorDeptName(u.getDeptName());
		
//		processInstance.setExpiredTime(time);
		
		
		PersistenceService persistenceService = this.getRuntimeContext().getEngineModule(PersistenceService.class, descriptor.getProcessType());
		ProcessInstancePersister processInstancePersister = persistenceService.getProcessInstancePersister();
		
		processInstancePersister.saveOrUpdate(processInstance);
		
		
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ProcessInstanceManager#fireProcessInstanceEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, java.lang.Object, int)
	 */
	public void fireProcessInstanceEvent(WorkflowSession session,
			ProcessInstance processInstance, Object workflowProcess,
			ProcessInstanceEventTrigger eventType) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.impl.AbsProcessInstanceManager#initProcessInstanceVariables(org.fireflow.engine.entity.runtime.ProcessInstance, java.lang.Object, java.util.Map)
	 */
	@Override
	protected void initProcessInstanceVariables(
			ProcessInstance newProcessInstance, Object subflow,
			Map<String, Object> variables) {
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, BpelConstants.PROCESS_TYPE);
		VariablePersister variableService = persistenceStrategy.getVariablePersister();

		//初始化流程变量
		if (variables != null && variables.size() > 0) {
			Iterator<Entry<String, Object>> it = variables.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				
				VariableImpl v = new VariableImpl();
				((AbsVariable)v).setScopeId(newProcessInstance.getScopeId());
				((AbsVariable)v).setName(entry.getKey());
				((AbsVariable)v).setProcessElementId(newProcessInstance.getProcessElementId());
				((AbsVariable)v).setPayload(entry.getValue());
				((AbsVariable)v).setProcessId(newProcessInstance.getProcessId());
				((AbsVariable)v).setVersion(newProcessInstance.getVersion());
				((AbsVariable)v).setProcessType(newProcessInstance.getProcessType());
				Object value = entry.getValue();
				if (value!=null){
					if (value instanceof org.w3c.dom.Document){
						v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.w3c.dom.Document");
					}else if (value instanceof org.dom4j.Document){
						v.getHeaders().put(Variable.HEADER_KEY_CLASS_NAME, "org.dom4j.Document");
					}else{
						((AbsVariable)v).setDataType(new QName(NameSpaces.JAVA.getUri(),value.getClass().getName()));
					}
					
				}
				variableService.saveOrUpdate(v);
			}
		}
		
		
	}



}
