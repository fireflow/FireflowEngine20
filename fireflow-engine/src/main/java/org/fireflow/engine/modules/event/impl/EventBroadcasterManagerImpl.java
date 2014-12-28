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
package org.fireflow.engine.modules.event.impl;

import java.util.HashMap;
import java.util.Map;

import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.modules.event.EventBroadcaster;
import org.fireflow.engine.modules.event.EventBroadcasterManager;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class EventBroadcasterManagerImpl  extends AbsEngineModule implements EventBroadcasterManager {
	Map<String, EventBroadcaster> eventBroadcasterRegistry = new HashMap<String, EventBroadcaster>();
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.EventBroadcasterManager#getEventBroadcasters()
	 */
	public Map<String, EventBroadcaster> getEventBroadcasters() {
		return eventBroadcasterRegistry;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.EventBroadcasterManager#getEventBroadcaster(java.lang.String)
	 */
	public EventBroadcaster getEventBroadcaster(String eventClassName) {
		if (eventBroadcasterRegistry==null)return null;
		return eventBroadcasterRegistry.get(eventClassName);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.event.EventBroadcasterManager#setEventBroadcasters(java.util.Map)
	 */
	public void setEventBroadcasters(Map<String, EventBroadcaster> broadcasters) {
		eventBroadcasterRegistry = broadcasters;
		
	}



}
