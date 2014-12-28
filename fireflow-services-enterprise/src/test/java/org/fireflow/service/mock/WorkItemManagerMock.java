package org.fireflow.service.mock;

import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.workitem.WorkItemCenter;
import org.fireflow.engine.modules.workitem.impl.AbsWorkItemManager;

public class WorkItemManagerMock extends AbsWorkItemManager {


	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.workitem.WorkItemManager#getWorkItemCenter()
	 */
	public WorkItemCenter getWorkItemCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.workitem.WorkItemManager#createWorkItem(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ProcessInstance, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.engine.modules.ousystem.User, java.lang.Object, java.util.Map)
	 */
	public WorkItem createWorkItem(WorkflowSession currentSession,
			ProcessInstance processInstance, ActivityInstance activityInstance,
			User user, Object theActivity,
			Map<WorkItemProperty, Object> workitemPropertyValues)
			throws EngineException {
		// TODO Auto-generated method stub
		return null;
	}

}
