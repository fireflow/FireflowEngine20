package org.fireflow.samples.event;

import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.modules.workitem.event.AbsWorkItemEventListener;
import org.fireflow.engine.modules.workitem.event.WorkItemEvent;
import org.fireflow.pdl.fpdl.process.Activity;

public class MyWorkItemEventListener extends AbsWorkItemEventListener {

	@Override
	protected void afterWorkItemEnd(WorkItemEvent e) {
		WorkItem workItem = e.getSource();
		Activity activity = (Activity)e.getWorkflowElement();
		System.out.println("~~调用事件MyWorkItemEventListener.afterWorkItemEnd(...)");
		System.out.println("~~~~发起事件的工作项是：id="+workItem.getId()+";主题="+
				workItem.getSubject()+"; state="+workItem.getState().getDisplayName());
		
		System.out.println("~~~~该工作项对应的活动节点是：id="+activity.getId());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		super.afterWorkItemEnd(e);
	}

	@Override
	protected void afterWorkItemCreated(WorkItemEvent e) {
		WorkItem workItem = e.getSource();
		Activity activity = (Activity)e.getWorkflowElement();
		System.out.println("~~调用事件MyWorkItemEventListener.onWorkItemCreated(...)");
		System.out.println("~~~~发起事件的工作项是：id="+workItem.getId()+";主题="+
				workItem.getSubject()+"; state="+workItem.getState().getDisplayName());
		
		System.out.println("~~~~该工作项对应的活动节点是：id="+activity.getId());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		super.afterWorkItemEnd(e);
	}

}
