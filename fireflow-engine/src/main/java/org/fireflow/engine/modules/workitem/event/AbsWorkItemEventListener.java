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


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsWorkItemEventListener implements WorkItemEventListener {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.event.WorkItemEventListener#onWorkItemEventFired(org.fireflow.engine.modules.instancemanager.event.WorkItemEvent)
	 */
	final public void onWorkItemEventFired(WorkItemEvent e) {
		WorkItemEventTrigger type = e.getEventTrigger();
		if (type.equals(WorkItemEventTrigger.ON_WORKITEM_CREATED)){
			this.onWorkItemCreated(e);
		}

		else if (type.equals(WorkItemEventTrigger.AFTER_WORKITEM_END)){
			this.afterWorkItemEnd(e);
		}
		else if (type.equals(WorkItemEventTrigger.AFTER_WORKITEM_CLAIMED)){
			this.afterWorkItemClaimed(e);
		}
		else if (type.equals(WorkItemEventTrigger.BEFORE_WORKITEM_CLAIMED)){
			beforeWorkItemClaimed(e);
		}
	}

	protected void onWorkItemCreated(WorkItemEvent e){
		
	}
	
	protected void afterWorkItemEnd(WorkItemEvent e){
		
	}
	
	protected void afterWorkItemClaimed(WorkItemEvent e){
		
	}
	
	protected void beforeWorkItemClaimed(WorkItemEvent e){
		
	}
}
