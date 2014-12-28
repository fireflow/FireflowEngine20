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
package org.fireflow.pdl.fpdl.process.features.endnode.impl;

import org.fireflow.pdl.fpdl.process.features.endnode.ThrowFaultFeature;

/**
 * @author 非也
 * @version 2.0
 */
public class ThrowFaultFeatureImpl implements ThrowFaultFeature {
	String errorCode = null;
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.endnode.ThrowExceptionDecorator#getExceptionCode()
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.decorator.endnode.ThrowExceptionDecorator#setExceptionCode(java.lang.String)
	 */
	public void setErrorCode(String exceptionCode) {
		this.errorCode = exceptionCode;
	}

}
