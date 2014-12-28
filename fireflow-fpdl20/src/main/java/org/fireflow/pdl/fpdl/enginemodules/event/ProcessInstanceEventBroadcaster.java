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
package org.fireflow.pdl.fpdl.enginemodules.event;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.event.Event;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventListener;
import org.fireflow.pdl.fpdl.enginemodules.instancemanager.ActivityInstanceManagerFpdl20Impl;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ProcessInstanceEventBroadcaster implements EventBroadcaster {
	private Log log = LogFactory.getLog(ProcessInstanceEventBroadcaster.class);
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.EventBroadcaster#fireEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.modules.event.Event)
	 */
	public void fireEvent(WorkflowSession session, Event event) {
		
		SubProcess subProcess = (SubProcess)event.getWorkflowElement();
		
		List<EventListenerDef> eventListeners = subProcess.getEventListeners();
		if (eventListeners != null) {
			for (EventListenerDef eventListenerDef : eventListeners) {
				fireEvent(session, eventListenerDef, (ProcessInstanceEvent)event);
			}
		}

	}

	
	private void fireEvent(WorkflowSession session,
			EventListenerDef eventListenerDef, ProcessInstanceEvent event) {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(
				BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);

		String listenerBeanName = eventListenerDef.getListenerBeanName();
		String listenerClassName = eventListenerDef.getListenerClassName();

		try {
			Object listener = null;
			if (listenerBeanName != null && !listenerBeanName.trim().equals("")) {
				// 首先应用BeanName
				listener = beanFactory.getBean(listenerBeanName);
			} else {
				listener = beanFactory.createBean(listenerClassName);
			}
			if (listener != null
					&& (listener instanceof ProcessInstanceEventListener)) {
				((ProcessInstanceEventListener) listener)
						.onProcessInstanceEventFired(event);
			}
		} catch (Exception e) {// 对于listener报出的所有异常均被阻挡，避免事件监听器影响正常流程。

			log.error(e.getMessage(), e);
		}
	}
}
