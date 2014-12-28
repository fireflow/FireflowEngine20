/**
 * Copyright 2007-2008 非也
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
package org.fireflow.engine.modules.instancemanager.event;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.modules.event.Event;
import org.fireflow.engine.modules.event.EventTrigger;

/**
 *
 * @author chennieyun
 */
public class ActivityInstanceEvent implements Event{

	private ActivityInstance source = null;
	private ActivityInstanceEventTrigger eventType = null;
	private Object workflowElement = null;
	
	public ActivityInstance getSource(){
		return source;
	}
	
	public void setSource(ActivityInstance activityInstance){
		this.source = activityInstance;
	}
	
	public EventTrigger getEventTrigger(){
		return eventType;
	}
	
	public void setEventTrigger(EventTrigger eventType){
		this.eventType = (ActivityInstanceEventTrigger)eventType;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.Event#getWorkflowElement()
	 */
	public Object getWorkflowElement() {
		return workflowElement;
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
