/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.server.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.invocation.impl.DynamicAssignmentHandler;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlType(name="assignmentHandlerMapEntryType")
@XmlAccessorType(XmlAccessType.FIELD) 
public class AssignmentHandlerMapEntry extends AbsMapEntry {
	@XmlElement(name="activityId")
	private String key = null;
	
	@XmlElement(name="assignmentHandler")
	private DynamicAssignmentHandler value = null;
	/* (non-Javadoc)
	 * @see org.fireflow.misc.AbsMapEntry#getKey()
	 */
	@Override
	public String getKey() {
		return key;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.misc.AbsMapEntry#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.misc.AbsMapEntry#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String k) {
		this.key = k;
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.misc.AbsMapEntry#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object v) {
		this.value = (DynamicAssignmentHandler)v;		
	}

}
