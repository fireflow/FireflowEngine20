package org.fireflow.engine.invocation;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.pvm.pdllogic.ContinueDirection;


/**
 * 相当于TaskInstanceRunner；ServieInvoker的实现类必须是无状态的，即不能有成员变量。
 * @author 陈乜云
 *
 */
public interface ServiceInvoker {
	public static final int CLOSE_ACTIVITY = ContinueDirection.CLOSE_ME;
	public static final int WAITING_FOR_CLOSE = ContinueDirection.WAITING_FOR_CLOSE;
	public static final int START_NEXT_AND_WAITING_FOR_CLOSE = ContinueDirection.START_NEXT_AND_WAITING_FOR_CLOSE;
	
	
	/**
	 * 执行Service，如果是同步调用，则返回true；
	 * 如果是异步调用（需要长时间执行的service）,则返回false；
	 * 异步service结束后回调onServiceCompleted(WorkflowSession session,ActivityInstance activityInstance)方法
	 * @param theActivity TODO
	 * @param params
	 * @return true表示同步调用；false表示异步调用
	 */
	public boolean invoke(WorkflowSession session,ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivity)throws ServiceInvocationException;

	
	/**
	 * ActivityInstanceManager调用该方法决定activityInstance是否可以结束。返回值是：<br/>
	 * ServiceInvoker.CLOSE_ACTIVITY：该值表示activityInstance可以被关闭，并启动后续活动；<br/>
	 * ServiceInvoker.WAITING_FOR_CLOSE：该值表示activityInstance继续保持Runing状态<br/>
	 * ServiceInvoker.START_NEXT_AND_WAITING_FOR_CLOSE：该值表示启动后续活动，但是当前activityInstance继续保持Running状态。
	 * 
	 * @param session
	 * @param activityInstance
	 * @param theActivity TODO
	 * @param serviceBinding TODO
	 * @return
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,ActivityInstance activityInstance, Object theActivity, ServiceBinding serviceBinding);

}
