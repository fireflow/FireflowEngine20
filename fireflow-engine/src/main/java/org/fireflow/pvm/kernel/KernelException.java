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
package org.fireflow.pvm.kernel;

/**
 * @author 非也
 * @version 2.0
 */
public class KernelException extends RuntimeException {
	PObject source = null;
	/**
	 * 
	 */
	public KernelException(PObject source) {
		super();
		this.source = source;
	}

	/**
	 * @param message
	 * @param cause
	 */
	public KernelException(PObject source,String message, Throwable cause) {
		super(message, cause);
		this.source = source;
	}

	/**
	 * @param message
	 */
	public KernelException(PObject source,String message) {
		super(message);
		this.source = source;
	}

	/**
	 * @param cause
	 */
	public KernelException(PObject source,Throwable cause) {
		super(cause);
		this.source = source;
	}

	
	
	@Override
	public String getMessage() {
		StringBuffer sbuf = new StringBuffer();
		if (this.source!=null){
			PObjectKey key = this.source.getKey();
			sbuf.append("Inside PObject[processId=").append(key.getProcessId())
				.append(",processVersion=").append(key.getVersion())
				.append(",processType=").append(key.getProcessType())
				.append(",elementId=").append(key.getWorkflowElementId()).append("]:");
				
		}
		sbuf.append(super.getMessage());
		return sbuf.toString();
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = -6110055565500078994L;

}
