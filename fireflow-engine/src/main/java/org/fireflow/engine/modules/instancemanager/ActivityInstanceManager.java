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
package org.fireflow.engine.modules.instancemanager;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEventTrigger;

/**
 * @author 非也
 * @version 2.0
 */
public interface ActivityInstanceManager extends EngineModule{
	
	public ActivityInstance createActivityInstance(WorkflowSession session,ProcessInstance processInstance,Object activity);

	
	public boolean runActivityInstance(WorkflowSession session,Object workflowElement,ActivityInstance activityInstance) throws ServiceInvocationException;

	
	/**
	 * 返回值必须是 org.fireflow.pvm.pdllogic.ContinueDirection中定义的几个整形常量：
	 * WAITING_FOR_CLOSE，CLOSE_ME_AND_AWAKEN_PARENT，RUN_AGAIN，CLOSE_ME_AND_START_NEXT，START_NEXT_AND_WAITING_FOR_CLOSE之一。
	 * @param session
	 * @param activityInstance
	 * @return
	 */
	public int tryCloseActivityInstance(WorkflowSession session,ActivityInstance activityInstance,Object workflowElement);
	
	
	
	public void onServiceCompleted(WorkflowSession session,ActivityInstance activityInstance);


	public void changeActivityInstanceState(WorkflowSession session,ActivityInstance activityInstance,ActivityInstanceState newState,Object workflowElement);
	
	public ActivityInstance suspendActivityInstance(WorkflowSession session , ActivityInstance activityInstance);
	
	public ActivityInstance restoreActivityInstance(WorkflowSession session , ActivityInstance activityInstance);
	
	public void fireActivityInstanceEvent(WorkflowSession session,ActivityInstance actInstance,Object workflowElement,ActivityInstanceEventTrigger eventType);
}
