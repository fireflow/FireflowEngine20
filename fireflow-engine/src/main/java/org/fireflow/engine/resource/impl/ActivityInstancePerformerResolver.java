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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.fireflow.engine.resource.ResourceResolver;
import org.fireflow.model.resourcedef.ResourceDef;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ActivityInstancePerformerResolver extends ResourceResolver {
	private static Log log = LogFactory.getLog(ActivityInstancePerformerResolver.class);
	public static final String REFERENCED_ACTIVITY_ID = "referencedActivityId";
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.resource.ResourceResolver#resolve(org.fireflow.engine.WorkflowSession, org.fireflow.model.resourcedef.Resource, java.util.Map)
	 */
	public List<User> resolve(WorkflowSession session, ProcessInstance currentProcessInstance,
			ActivityInstance currentActivityInstance, ResourceDef resource) {
		List<User> users = new ArrayList<User>();
		WorkflowSessionLocalImpl localSession = (WorkflowSessionLocalImpl)session;
		ProcessInstance processInstance = localSession.getCurrentProcessInstance();
		if (processInstance==null){
			log.error("Current process instance is null,can NOT retrieve the actors");
			return users;
		}
		
		String referencedActivityId = resource.getValue();
		
		if (referencedActivityId==null || referencedActivityId.trim().equals("")){
			log.error("The parameter value of 'referencedActivityId' is null,can NOT retrieve the actors");
			return users;
		}
		
		RuntimeContext rtCtx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processInstance.getProcessType());
		ActivityInstancePersister actPersister = persistenceService.getActivityInstancePersister();
		List<ActivityInstance> actInstList = actPersister.findActivityInstances(processInstance.getId(), referencedActivityId);
		
		if (actInstList==null || actInstList.size()==0){
			log.error("Can NOT find activity instances for [activityId='"+referencedActivityId+"' , processInstanceId='"+processInstance.getId()+"']");
			return users;
		}
		
		ActivityInstance activityInstance = actInstList.get(0);
		for (ActivityInstance tmp : actInstList){
			if (activityInstance.getStepNumber()<tmp.getStepNumber()){
				activityInstance = tmp;
			}
		}
		
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		List<WorkItem> workItemList = workItemPersister.findWorkItemsForActivityInstance(activityInstance.getId());
		if (workItemList==null || workItemList.size()==0){
			log.warn("Can NOT find work items for [activityId='"+referencedActivityId+"' , processInstanceId='"+processInstance.getId()+"']");
			return users;
		}
		OUSystemConnector ouSystemAdapter = rtCtx.getEngineModule(OUSystemConnector.class, processInstance.getProcessType());
		
		for (WorkItem wi : workItemList){
			if (wi.getState().equals(WorkItemState.COMPLETED)){
//				User u = ouSystemAdapter.findUserById(wi.getUserId());
				UserImpl u = new UserImpl();
				Properties props = new Properties();
				u.setId(processInstance.getCreatorId());
				u.setName(processInstance.getCreatorName());
				u.setDeptId(processInstance.getCreatorDeptId());
				u.setDeptName(processInstance.getCreatorDeptName());
				u.setProperties(props);
				users.add(u);
			}
		}
		return users;
	}

}
