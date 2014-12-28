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
package org.fireflow.engine.entity.runtime.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.runtime.WorkItem;


/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name="remoteWorkItem")
@XmlType(name="remoteWorkItemType")
@XmlAccessorType(XmlAccessType.FIELD)
public class RemoteWorkItemImpl extends AbsWorkItem implements java.io.Serializable, 
		org.fireflow.engine.entity.runtime.RemoteWorkItem {
	public RemoteWorkItemImpl(){
		this.setWorkItemType(WorkItem.WORKITEM_TYPE_REMOTE);
	}
	

}
