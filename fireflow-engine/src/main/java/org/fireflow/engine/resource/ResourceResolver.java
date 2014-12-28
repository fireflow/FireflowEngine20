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
package org.fireflow.engine.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.resource.impl.ActivityInstancePerformerResolver;
import org.fireflow.engine.resource.impl.DepartmentResolver;
import org.fireflow.engine.resource.impl.GroupResolver;
import org.fireflow.engine.resource.impl.ProcessInstanceCreatorResolver;
import org.fireflow.engine.resource.impl.RoleResolver;
import org.fireflow.engine.resource.impl.UserResolver;
import org.fireflow.engine.resource.impl.VariableImplicationResolver;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.resourcedef.ResourceType;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class ResourceResolver {
	private static Map<ResourceType,ResourceResolver> resolversMap = new HashMap<ResourceType,ResourceResolver>();
	static {
		resolversMap.put(ResourceType.ROLE, new RoleResolver());
		resolversMap.put(ResourceType.GROUP, new GroupResolver());
		resolversMap.put(ResourceType.DEPARTMENT, new DepartmentResolver());
		resolversMap.put(ResourceType.USER, new UserResolver());
		resolversMap.put(ResourceType.ACTIVITY_INSTANCE_PERFORMER, new ActivityInstancePerformerResolver());
		resolversMap.put(ResourceType.PROCESS_INSTANCE_CREATOR,new ProcessInstanceCreatorResolver());
		resolversMap.put(ResourceType.VARIABLE_IMPLICATION, new VariableImplicationResolver());
	}
	
	
	public static ResourceResolver getResourceResolverForType(ResourceType type){
		
		return resolversMap.get(type);
	}
	/**
	 * 将资源解析成实际的User对象列表。
	 * @param currentProcessInstance TODO
	 * @param currentActivityInstance TODO
	 * @param resource
	 * @return
	 */
	public abstract List<User> resolve(WorkflowSession session,ProcessInstance currentProcessInstance,ActivityInstance currentActivityInstance, ResourceDef resource); 
}
