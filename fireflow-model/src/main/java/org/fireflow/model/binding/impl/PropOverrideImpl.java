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
package org.fireflow.model.binding.impl;

import org.fireflow.model.binding.PropOverride;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 * @deprecated
 */
public class PropOverrideImpl implements PropOverride {
	private String propGroupName = null;
	private String propName = null;
	private String value = null;

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.PropOverride#getPropGroupName()
	 */
	public String getPropGroupName() {
		return propGroupName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.PropOverride#getPropName()
	 */
	public String getPropName() {
		return propName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.PropOverride#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.PropOverride#setPropGroupName(java.lang.String)
	 */
	public void setPropGroupName(String propGroupName) {
		this.propGroupName = propGroupName;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.PropOverride#setPropName(java.lang.String)
	 */
	public void setPropName(String propName) {
		this.propName = propName;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.process.binding.PropOverride#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.value = value;

	}

}
