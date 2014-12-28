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
package org.fireflow.engine.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.config.impl.FireflowConfigImpl;
import org.fireflow.engine.entity.config.impl.ReassignConfigImpl;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.RemoteWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobHistory;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.entity.runtime.impl.VariableHistory;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.entity.runtime.impl.WorkItemHistory;
import org.fireflow.pvm.kernel.impl.TokenHistory;
import org.fireflow.pvm.kernel.impl.TokenImpl;
import org.fireflow.server.support.DateTimeXmlAdapter;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Readonly;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlType(name="absWorkflowEntityType",propOrder={"id","lastUpdateTime"})
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ProcessInstanceImpl.class,ProcessInstanceHistory.class,
	ActivityInstanceImpl.class,ActivityInstanceHistory.class,
	LocalWorkItemImpl.class,RemoteWorkItemImpl.class,WorkItemHistory.class,
	TokenImpl.class,TokenHistory.class,
	VariableImpl.class,VariableHistory.class,
	ScheduleJobImpl.class,ScheduleJobHistory.class,
	ReassignConfigImpl.class,FireflowConfigImpl.class})
public abstract class AbsWorkflowEntity implements WorkflowEntity {
	@XmlElement(name="entityId")
	@Name()
	protected String id = null;
	
	@XmlElement(name="lastUpdateTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("LAST_UPDATE_TIME")
	@Readonly
	protected Date lastUpdateTime = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.WorkflowEntity#getId()
	 */
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.WorkflowEntity#getLastUpdateTime()
	 */
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	
}
