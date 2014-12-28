package org.fireflow.samples.event;

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.modules.instancemanager.event.AbsActivityInstanceEventListener;
import org.fireflow.engine.modules.instancemanager.event.ActivityInstanceEvent;
import org.fireflow.pdl.fpdl20.process.Activity;

public class MyActivityEventListener extends AbsActivityInstanceEventListener {

	/**
	 * Activity正常结束，或者被Abort都会触发该方法
	 */
	@Override
	protected void afterActivityInstanceEnd(ActivityInstanceEvent e) {
		ActivityInstance actInst = e.getSource();
		Activity activity = (Activity)e.getWorkflowElement();
		System.out.println("--调用事件MyActivityEventListener.afterActivityInstanceEnd(...)");
		System.out.println("----发起事件的活动实例是：id="+actInst.getId()+";displayName="+
				actInst.getDisplayName()+"; state="+actInst.getState().getDisplayName()+
				";活动实例的挂起标志="+actInst.isSuspended());
		System.out.println("----该实例对应的活动节点是：id="+activity.getId());
		System.out.println("------------------------------------");

		super.afterActivityInstanceEnd(e);
	}

	@Override
	protected void onActivityInstanceCreated(ActivityInstanceEvent e) {
		ActivityInstance actInst = e.getSource();
		Activity activity = (Activity)e.getWorkflowElement();
		System.out.println("--调用事件MyActivityEventListener.onActivityInstanceCreated(...)");
		System.out.println("----发起事件的活动实例是：id="+actInst.getId()+";displayName="+
				actInst.getDisplayName()+"; state="+actInst.getState().getDisplayName()+
				";活动实例的挂起标志="+actInst.isSuspended());
		System.out.println("----该实例对应的活动节点是：id="+activity.getId());
		System.out.println("------------------------------------");

		super.onActivityInstanceCreated(e);
	}

	@Override
	protected void onActivityInstanceRestored(ActivityInstanceEvent e) {
		ActivityInstance actInst = e.getSource();
		Activity activity = (Activity)e.getWorkflowElement();
		System.out.println("--调用事件MyActivityEventListener.onActivityInstanceRestored(...)");
		System.out.println("----发起事件的活动实例是：id="+actInst.getId()+";displayName="+
				actInst.getDisplayName()+"; state="+actInst.getState().getDisplayName()+
				";活动实例的挂起标志="+actInst.isSuspended());
		System.out.println("----该实例对应的活动节点是：id="+activity.getId());
		System.out.println("------------------------------------");

		super.onActivityInstanceRestored(e);
	}

	@Override
	protected void onActivityInstanceSuspended(ActivityInstanceEvent e) {
		ActivityInstance actInst = e.getSource();
		Activity activity = (Activity)e.getWorkflowElement();
		System.out.println("--调用事件MyActivityEventListener.onActivityInstanceSuspended(...)");
		System.out.println("----发起事件的活动实例是：id="+actInst.getId()+";displayName="+
				actInst.getDisplayName()+"; state="+actInst.getState().getDisplayName()+
				";活动实例的挂起标志="+actInst.isSuspended());
		System.out.println("----该实例对应的活动节点是：id="+activity.getId());
		System.out.println("------------------------------------");

		super.onActivityInstanceSuspended(e);
	}

}
