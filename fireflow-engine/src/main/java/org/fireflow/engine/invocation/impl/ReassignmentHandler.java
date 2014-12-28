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
package org.fireflow.engine.invocation.impl;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;


/**
 * 加签处理器
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="reassignmentHandler")
@XmlType(name="reassignmentHandlerType")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReassignmentHandler extends DynamicAssignmentHandler {
	@XmlElement(name="reassginType")
	private String reassignType = WorkItem.REASSIGN_AFTER_ME;
	
	@XmlElement(name="parentWorkItemId")
	private String parentWorkItemId = null;
	
	public String getReassignType() {
		return reassignType;
	}
	public void setReassignType(String reassignType) {
		this.reassignType = reassignType;
	}
	public String getParentWorkItemId() {
		return parentWorkItemId;
	}
	public void setParentWorkItemId(String parentWorkItemId) {
		this.parentWorkItemId = parentWorkItemId;
	}
	
	public Map<WorkItemProperty,Object> resolveWorkItemPropertyValues(){
		Map<WorkItemProperty,Object> values = new HashMap<WorkItemProperty,Object>();
		values.put(WorkItemProperty.REASSIGN_TYPE, reassignType);
		values.put(WorkItemProperty.PARENT_WORKITEM_ID, parentWorkItemId);
		return values;
	}
}
