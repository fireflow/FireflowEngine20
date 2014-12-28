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
package org.fireflow.pdl.fpdl.test.service.human;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.ResourceDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class CustomerResourceResolver extends ResourceResolver{
	public static String ADMINISTRATOR = "Administrator";
	public static String ACTOR = "Actor";
	public static String READER = "Reader";

	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.ResourceResolver#resolve(org.fireflow.engine.WorkflowSession, org.fireflow.model.resourcedef.Resource, java.util.Map)
	 */
	public List<User> resolve(WorkflowSession session, ProcessInstance currentProcessInstance,
			ActivityInstance currentActivityInstance, ResourceDef resource) {
		List<User> users = new ArrayList<User>();
		String flag = resource.getExtendedAttributes().get("FLAG");
		if (flag==null){
			flag = "1";
		}
		if (flag.equals("1")){
			UserImpl u = new UserImpl();
			u.setId(ADMINISTRATOR);
			u.setName("管理者王总");
			users.add(u);
		}
		if (flag.equals("2")){
			UserImpl u = new UserImpl();
			u.setId(ACTOR);
			u.setName("操作者张三");
			users.add(u);
		}	
		if (flag.equals("3")){
			UserImpl u = new UserImpl();
			u.setId(READER);
			u.setName("抄送者李总监");
			users.add(u);
		}		
		return users;
	}

}
