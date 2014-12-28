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
package org.fireflow.engine.modules.workitem.event;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.modules.event.Event;
import org.fireflow.engine.modules.event.EventTrigger;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class WorkItemEvent implements Event {
	WorkItemEventTrigger eventType = null;
	WorkItem source = null;
	Object workflowElement = null;
	/**
	 * @return the eventType
	 */
	public WorkItemEventTrigger getEventTrigger() {
		return eventType;
	}
	/**
	 * @param eventType the eventType to set
	 */
	public void setEventTrigger(WorkItemEventTrigger eventType) {
		this.eventType = eventType;
	}
	/**
	 * @return the source
	 */
	public WorkItem getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(WorkItem source) {
		this.source = (WorkItem)source;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.Event#getWorkflowElement()
	 */
	public Object getWorkflowElement() {
		return this.workflowElement;
	}
	
	public void setWorkflowElement(Object wfElm){
		this.workflowElement = wfElm;
	}
	
    WorkflowSession currentSession = null;
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.Event#getCurrentWorkflowSession()
	 */
	public WorkflowSession getCurrentWorkflowSession() {
		return this.currentSession;
	}
	
	public void setCurrentWorkflowSession(WorkflowSession session){
		this.currentSession = session;
	}
}
