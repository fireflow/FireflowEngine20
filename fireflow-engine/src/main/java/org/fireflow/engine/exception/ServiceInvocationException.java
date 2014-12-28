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
package org.fireflow.engine.exception;

import org.fireflow.engine.entity.runtime.ActivityInstance;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ServiceInvocationException extends Exception {
	//下面是一些预定义的错误代号
	public static final String SERVICE_DEF_NOT_FOUND = "SERVICE_DEF_NOT_FOUND";
	public static final String SERVICE_OBJECT_NOT_FOUND = "SERVICE_OBJECT_NOT_FOUND";
	public static final String OPERATION_NOT_FOUND = "OPERATION_NOT_FOUND";
	public static final String PROCESS_DEF_NOT_FOUND = "PROCESS_DEF_NOT_FOUND";
	public static final String INVALID_PROCESS_MODEL = "INVALID_PROCESS_MODEL";

	
	protected ActivityInstance activityInstance = null;
	protected String errorCode = null;
	
	/**
	 * 
	 */
	public ServiceInvocationException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ServiceInvocationException(String arg, Throwable arg0) {
		super(arg, arg0);
	}

	/**
	 * @param arg0
	 */
	public ServiceInvocationException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public ServiceInvocationException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public ActivityInstance getActivityInstance() {
		return activityInstance;
	}

	public void setActivityInstance(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
	}

	@Override
	public String getMessage() {
		StringBuffer sbuf = new StringBuffer();
		if (this.activityInstance!=null){
			sbuf.append("Inside ActivityInstance[processId=")
				.append(this.activityInstance.getProcessId())
				.append(",processVersion=")
				.append(this.activityInstance.getVersion())
				.append(",processType=")
				.append(this.activityInstance.getProcessType())
				.append(",activityName=")
				.append(this.activityInstance.getName())
				.append(",activityDispName=")
				.append(this.activityInstance.getDisplayName())
				.append(",actInstId=")
				.append(this.activityInstance.getId())
				.append("];");
		}
		if (this.errorCode!=null){
			sbuf.append("ErrorCode=").append(this.errorCode).append("; ");
		}
		sbuf.append(super.getMessage());
		return sbuf.toString();
	}

}
