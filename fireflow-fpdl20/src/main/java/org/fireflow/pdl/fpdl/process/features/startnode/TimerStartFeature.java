package org.fireflow.pdl.fpdl.process.features.startnode;

import org.fireflow.engine.invocation.TimerOperationName;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.features.Feature;

public interface TimerStartFeature extends Feature {

	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);
	
	/**
	 * 该字段表示事件触发时，是否将所依附的Activity取消。默认为不取消(false)
	 * @return
	 */
	public boolean getCancelAttachedToActivity();
	
	public TimerOperationName getTimerOperationName();
	public Expression getStartTimeExpression();
	public Expression getEndTimeExpression();
	public Expression getRepeatCountExpression();
	public Expression getRepeatIntervalExpression();
	public Expression getCronExpression();
}
