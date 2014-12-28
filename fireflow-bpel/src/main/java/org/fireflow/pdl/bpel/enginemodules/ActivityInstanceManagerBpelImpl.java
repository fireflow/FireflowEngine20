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

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.ActivityInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.impl.AbsActivityInstanceManager;
import org.fireflow.pdl.bpel.BpelActivity;

/**
 * @author 非也
 * @version 2.0
 */
public class ActivityInstanceManagerBpelImpl extends AbsActivityInstanceManager implements ActivityInstanceManager {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#createActivityInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, java.lang.Object)
	 */
	public ActivityInstance createActivityInstance(WorkflowSession session,
			ProcessInstance processInstance, Object activity) {
		CalendarService calendarService = this.runtimeContext.getDefaultEngineModule(CalendarService.class);

		BpelActivity bpelActivity = (BpelActivity)activity;
		ActivityInstanceImpl actInst = new ActivityInstanceImpl();
		actInst.setProcessId(processInstance.getProcessId());
		actInst.setVersion(processInstance.getVersion());
		actInst.setProcessType(processInstance.getProcessType());
		
		actInst.setSubProcessId(processInstance.getSubProcessId());
		actInst.setSubProcessName(processInstance.getSubProcessName());
		actInst.setSubProcessDisplayName(processInstance.getSubProcessDisplayName());
		
		actInst.setProcessInstanceId(processInstance.getId());
		actInst.setBizId(processInstance.getBizId());
		
		actInst.setNodeId(bpelActivity.getId());
		actInst.setCreatedTime(calendarService.getSysDate());
		actInst.setName(bpelActivity.getName());
		actInst.setDisplayName(bpelActivity.getName());
		actInst.setState(ActivityInstanceState.INITIALIZED);
		actInst.setParentScopeId(processInstance.getScopeId());
//		actInst.setStepNumber(?);
		
		return actInst;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#fireActivityInstanceEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object, int)
	 */
	public void fireActivityInstanceEvent(WorkflowSession session,
			ActivityInstance actInstance, Object workflowElement, ProcessInstanceEventTrigger eventType) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#runActivityInstance(org.fireflow.engine.WorkflowSession, java.lang.Object, org.fireflow.engine.entity.runtime.ActivityInstance)
	 */
	public boolean runActivityInstance(WorkflowSession session,
			Object workflowElement, ActivityInstance activityInstance)
			throws ServiceInvocationException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.instancemanager.ActivityInstanceManager#tryCloseActivityInstance(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object)
	 */
	public int tryCloseActivityInstance(WorkflowSession session,
			ActivityInstance activityInstance, Object workflowElement) {
		// TODO Auto-generated method stub
		return 0;
	}

}
