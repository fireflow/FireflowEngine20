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
public abstract class AbsActivityInstanceEventListener implements
		ActivityInstanceEventListener {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEventListener#onActivityInstanceEventFired(org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent)
	 */
	final public void onActivityInstanceEventFired(ActivityInstanceEvent e){
		ActivityInstanceEventTrigger type = (ActivityInstanceEventTrigger)e.getEventTrigger();
		if (type.equals(ActivityInstanceEventTrigger.ON_ACTIVITY_INSTANCE_CREATED)){
			this.onActivityInstanceCreated(e);
		}
		else if (type.equals(ActivityInstanceEventTrigger.AFTER_ACTIVITY_INSTANCE_END)){
			this.afterActivityInstanceEnd(e);
		}
		
		else if (type.equals(ActivityInstanceEventTrigger.ON_ACTIVITY_INSTANCE_SUSPENDED)){
			this.onActivityInstanceSuspended(e);
		}
		else if (type.equals(ActivityInstanceEventTrigger.ON_ACTIVITY_INSTANCE_RESTORED)){
			this.onActivityInstanceRestored(e);
		}
	}
	protected void onActivityInstanceCreated(ActivityInstanceEvent e){
		
	}

	protected void afterActivityInstanceEnd(ActivityInstanceEvent e){
		
	}
	
	protected void onActivityInstanceSuspended(ActivityInstanceEvent e){
		
	}
	
	protected void onActivityInstanceRestored(ActivityInstanceEvent e){
		
	}
}
