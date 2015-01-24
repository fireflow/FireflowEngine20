package org.fireflow.demo.workflow.ext;

import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.modules.workitem.event.AbsWorkItemEventListener;
import org.fireflow.engine.modules.workitem.event.WorkItemEvent;

public class OmitInitialzedStateListener extends AbsWorkItemEventListener {

	@Override
	protected void beforeWorkItemCreated(WorkItemEvent e) {
		AbsWorkItem wi = (AbsWorkItem)e.getSource();
		wi.setState(WorkItemState.RUNNING);//直接改为running状态
	}

}
