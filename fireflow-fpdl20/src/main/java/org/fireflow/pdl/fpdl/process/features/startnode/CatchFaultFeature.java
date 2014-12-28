package org.fireflow.pdl.fpdl.process.features.startnode;

import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.features.Feature;

public interface CatchFaultFeature  extends Feature{
	public static final String CATCH_ALL_FAULT = "org.fireflow.constants.fault.CATCH_ALL_FAULT";
	/**
	 * 被catch的Activity
	 * @return
	 */
	public Activity getAttachedToActivity();
	
	public void setAttachedToActivity(Activity act);
	
	/**
	 * 被监听的异常类的名称
	 * @return
	 */
	public String getErrorCode();
	
	public void setErrorCode(String errorCode);
}
