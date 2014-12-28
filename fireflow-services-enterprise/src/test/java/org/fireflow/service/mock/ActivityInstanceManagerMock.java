package org.fireflow.service.mock;

import java.util.Date;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.modules.calendar.CalendarService;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.instancemanager.impl.AbsActivityInstanceManager;

public class ActivityInstanceManagerMock extends AbsActivityInstanceManager {

	public ActivityInstance createActivityInstance(WorkflowSession session,
			ProcessInstance processInstance, Object activity) {
		CalendarService calendarService = this.runtimeContext.getDefaultEngineModule(CalendarService.class);
		
		
//		Node node = (Node)activity;
		ActivityInstanceImpl actInst = new ActivityInstanceImpl();
		actInst.setName(activity.toString());
		String displayName = activity.toString();		
//		actInst.setDisplayName((displayName==null || displayName.trim().equals(""))?node.getName():displayName);
		actInst.setState(ActivityInstanceState.INITIALIZED);
		
		actInst.setProcessName(processInstance.getProcessName());
		actInst.setProcessDisplayName(processInstance.getProcessDisplayName());
		actInst.setBizType(processInstance.getBizType());
		
		actInst.setProcessId(processInstance.getProcessId());
		actInst.setVersion(processInstance.getVersion());
		actInst.setProcessType(processInstance.getProcessType());
		actInst.setProcessInstanceId(processInstance.getId());
		actInst.setNodeId(activity.toString());		
		actInst.setBizId(processInstance.getBizId());
		
		actInst.setParentScopeId(processInstance.getScopeId());
		
		Date now = calendarService.getSysDate();
		actInst.setCreatedTime(now);

//		if (node instanceof Activity){
//			Activity fpdl20Activity = (Activity)node;
//			
//			if (fpdl20Activity.getDuration()!=null && fpdl20Activity.getDuration().getValue()>0){
//				Date expiredDate = calendarService.dateAfter(now, fpdl20Activity.getDuration());
//				actInst.setExpiredTime(expiredDate);
//			}
//			
//		}
		
		return actInst;
	}

	public boolean runActivityInstance(WorkflowSession session,
			Object workflowElement, ActivityInstance activityInstance)
			throws ServiceInvocationException {
		// TODO Auto-generated method stub
		return true;
	}

	public int tryCloseActivityInstance(WorkflowSession session,
			ActivityInstance activityInstance, Object workflowElement) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void fireActivityInstanceEvent(WorkflowSession session,
			ActivityInstance actInstance, Object workflowElement,
			ProcessInstanceEventTrigger eventType) {
		// TODO Auto-generated method stub

	}

}
