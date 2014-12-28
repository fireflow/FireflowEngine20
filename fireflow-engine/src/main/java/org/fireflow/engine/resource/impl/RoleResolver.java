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
package org.fireflow.engine.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.ResourceDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class RoleResolver extends ResourceResolver {
	private static Log log = LogFactory.getLog(RoleResolver.class);
	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.ResourceResolver#resolve(org.fireflow.engine.WorkflowSession, org.fireflow.model.resourcedef.Resource, java.util.Map)
	 */
	public List<User> resolve(WorkflowSession session, ProcessInstance currentProcessInstance,
			ActivityInstance currentActivityInstance, ResourceDef resource) {
		List<User> users = new ArrayList<User>();
		
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		ProcessInstance processInstance = localSession.getCurrentProcessInstance();
		if (processInstance==null){
			log.warn("Current process instance is null,can NOT retrieve the actors");
			return users;
		}		
		String roleId = resource.getValue();
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		OUSystemConnector ouSystemAdapter = rtCtx.getEngineModule(OUSystemConnector.class, processInstance.getProcessType());
		
		List<User> us = ouSystemAdapter.findUsersInRole(roleId);
		if(us!=null){
			users.addAll(us);
		}
		return users;
	}

}
