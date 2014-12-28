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
import org.fireflow.engine.modules.beanfactory.BeanFactory;
import org.fireflow.engine.modules.event.Event;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEventListener;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ActivityInstanceEventbroadcaster implements EventBroadcaster {
	private Log log = LogFactory.getLog(ActivityInstanceEventbroadcaster.class);
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.EventBroadcaster#fireEvent(org.fireflow.engine.WorkflowSession, org.fireflow.engine.modules.event.Event)
	 */
	public void fireEvent(WorkflowSession session, Event event) {
		Object workflowElement = event.getWorkflowElement();
		if (workflowElement!=null && workflowElement instanceof Activity){
			Activity activity = (Activity)event.getWorkflowElement();
			List<EventListenerDef> eventListeners = activity.getEventListeners();
			if (eventListeners != null) {
				for (EventListenerDef eventListenerDef : eventListeners) {
					fireEvent(session, eventListenerDef, (ActivityInstanceEvent)event);
				}
			}
		}
	}

//	public void fireActivityInstanceEvent(WorkflowSession session,ActivityInstance actInstance,Object workflowElement,InstanceEventType eventType){
//		if (!(workflowElement instanceof Activity))return ;
//		ActivityInstanceEvent event = new ActivityInstanceEvent();
//		event.setSource(actInstance);
//		event.setEventType(eventType);
//		
//		Activity activity = (Activity)workflowElement;
//		List<EventListenerDef> eventListeners = activity.getEventListeners();
//		if (eventListeners != null) {
//			for (EventListenerDef eventListenerDef : eventListeners) {
//				fireEvent(session, eventListenerDef, event);
//			}
//		}
//	}
	
	private void fireEvent(WorkflowSession session,
			EventListenerDef eventListenerDef, ActivityInstanceEvent event) {
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		BeanFactory beanFactory = runtimeContext.getEngineModule(
				BeanFactory.class, FpdlConstants.PROCESS_TYPE_FPDL20);

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
					&& (_listener instanceof ActivityInstanceEventListener)) {
				((ActivityInstanceEventListener) _listener)
						.onActivityInstanceEventFired(event);
			}
		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}

	}
}
