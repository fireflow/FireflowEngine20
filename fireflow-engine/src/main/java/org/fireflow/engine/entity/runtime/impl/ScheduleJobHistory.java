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
package org.fireflow.engine.entity.runtime.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="scheduleJobHistory")
@XmlType(name="scheduleJobHistoryType")
@XmlAccessorType(XmlAccessType.FIELD)
//@Table("T_FF_HIS_SCHEDULE")
public class ScheduleJobHistory extends AbsScheduleJob implements ScheduleJob {
	
	//TODO 此处为何还需要activityInstanceId属性？
	private String activityInstanceId = null;

	/**
	 * @return the activityInstanceId
	 */
	public String getActivityInstanceId() {
		return activityInstanceId;
	}

	/**
	 * @param activityInstanceId the activityInstanceId to set
	 */
	public void setActivityInstanceId(String activityInstanceId) {
		this.activityInstanceId = activityInstanceId;
	}

	public ActivityInstance getActivityInstance() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
