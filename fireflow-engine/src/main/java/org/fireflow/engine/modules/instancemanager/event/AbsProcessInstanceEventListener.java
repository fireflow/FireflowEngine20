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
package org.fireflow.engine.modules.instancemanager.event;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsProcessInstanceEventListener implements
		ProcessInstanceEventListener {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventListener#onProcessInstanceEventFired(org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEvent)
	 */
	final public void onProcessInstanceEventFired(ProcessInstanceEvent e) {
		ProcessInstanceEventTrigger type = (ProcessInstanceEventTrigger)e.getEventTrigger();
		if (type.equals(ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_CREATED)){
			this.onProcessInstanceCreated(e);
		}
		else if (type.equals(ProcessInstanceEventTrigger.AFTER_PROCESS_INSTANCE_END)){
			this.afterProcessInstanceEnd(e);
		}
		else if (type.equals(ProcessInstanceEventTrigger.BEFORE_PROCESS_INSTANCE_RUN)){
			beforeProcessInstanceRun(e);
		}
		else if (type.equals(ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_SUSPENDED)){
			onProcessInstanceSuspended(e);
		}
		else if (type.equals(ProcessInstanceEventTrigger.ON_PROCESS_INSTANCE_RESTORED)){
			onProcessInstanceRestored(e);
		}
	}
	
	protected void onProcessInstanceCreated(ProcessInstanceEvent e){
		
	}
	protected void afterProcessInstanceEnd(ProcessInstanceEvent e){
		
	}

	protected void beforeProcessInstanceRun(ProcessInstanceEvent e){
		
	}
	
	protected void onProcessInstanceSuspended(ProcessInstanceEvent e){
		
	}
	
	protected void onProcessInstanceRestored(ProcessInstanceEvent e){
		
	}
}
