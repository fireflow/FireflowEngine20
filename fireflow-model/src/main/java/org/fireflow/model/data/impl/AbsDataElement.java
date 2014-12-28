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
package org.fireflow.model.data.impl;

import javax.xml.namespace.QName;

import org.fireflow.model.data.DataElement;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsDataElement implements DataElement {
	private String name = null;
	private String displayName = null;
    /**
     * 数据类型，数据类型必须是一个合法的java类名，如 java.lang.String，java.lang.Integer等。
     */
    private QName dataType;
	
	/* (non-Javadoc)
	 * @see org.fireflow.model.data.DataElement#getDataType()
	 */
	public QName getDataType() {
		return dataType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.data.DataElement#getDisplayName()
	 */
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.data.DataElement#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.data.DataElement#setDataType(java.lang.String)
	 */
	public void setDataType(QName dataType) {
		this.dataType = dataType;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.data.DataElement#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.model.data.DataElement#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;

	}

}
