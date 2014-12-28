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
package org.fireflow.pdl.fpdl.enginemodules.event;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.event.Event;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.workitem.event.WorkItemEvent;
import org.fireflow.engine.modules.workitem.event.WorkItemEventListener;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 * TODO 缺省都是同步调用事件处理代码，应该增加异步调用的选项
 * 
 * @author 非也
 * @version 2.0
 */
public class WorkItemEventBroadcaster implements EventBroadcaster {
	private Log log = LogFactory.getLog(WorkItemEventBroadcaster.class);
	
	private void fireEvent(WorkflowSession session,EventListenerDef eventListenerDef,WorkItemEvent event){
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		
		String referencedBeanId = eventListenerDef.getListenerBeanName();
		String listenerClassName = eventListenerDef.getListenerClassName();

		try {
			Object _listener = null;
			if (referencedBeanId != null && !referencedBeanId.trim().equals("")) {
				_listener = beanFactory.getBean(referencedBeanId);
			} else {
				_listener = beanFactory.createBean(listenerClassName);
			}
			if (_listener != null
					&& (_listener instanceof WorkItemEventListener)) {
				((WorkItemEventListener) _listener).onWorkItemEventFired(event);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.EventBroadcaster#fireEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.modules.event.Event)
	 */
	public void fireEvent(WorkflowSession session, Event event) {
		Activity activity =  (Activity)event.getWorkflowElement();
		List<EventListenerDef> eventListeners = activity.getWorkItemEventListeners();
		if (eventListeners != null) {
			for (EventListenerDef eventListenerDef : eventListeners) {
				fireEvent(session, eventListenerDef, (WorkItemEvent)event);
			}
		}
	}

}
