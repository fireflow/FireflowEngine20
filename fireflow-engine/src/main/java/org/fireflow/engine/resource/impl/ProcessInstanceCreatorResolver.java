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
import java.util.Properties;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.ResourceDef;

/**
 * 解析流程实例创建者
 * 
 * @author 非也
 * @version 2.0
 */
public class ProcessInstanceCreatorResolver extends ResourceResolver{

	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.ResourceResolver#resolve(org.fireflow.model.resourcedef.Resource, java.util.Map)
	 */
	public List<User> resolve(WorkflowSession session,ProcessInstance currentProcessInstance,
			ActivityInstance currentActivityInstance, ResourceDef resource) {
		
		List<User> users = new ArrayList<User>();
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		ProcessInstance processInstance = localSession.getCurrentProcessInstance();
		if (processInstance==null)return users;
		
		String userId = processInstance.getCreatorId();
		if (FireWorkflowSystem.getInstance().getId().equals(userId)){
			users.add(FireWorkflowSystem.getInstance());
			return users;
		}
		
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		OUSystemConnector ouSystemAdapter = rtCtx.getEngineModule(OUSystemConnector.class, processInstance.getProcessType());

//		User u = ouSystemAdapter.findUserById(userId);
		//不从数据库查询，而是构造一个User，提高效率
		UserImpl u = new UserImpl();
		Properties props = new Properties();
		u.setId(processInstance.getCreatorId());
		u.setName( processInstance.getCreatorName());
		u.setDeptId(processInstance.getCreatorDeptId());
		u.setDeptName( processInstance.getCreatorDeptName());
		u.setProperties(props);
		
		users.add(u);
		return users;
	}

}
