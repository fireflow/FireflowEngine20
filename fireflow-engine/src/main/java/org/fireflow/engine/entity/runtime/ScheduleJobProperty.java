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
package org.fireflow.engine.entity.runtime;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public enum ScheduleJobProperty implements EntityProperty {
	ID("id"),
	NAME("name"),
	DISPLAY_NAME("displayName"),
	CREATED_TIME("createdTime"),
	TRIGGERED_TIMES("triggeredTimes"),
	LATEST_TRIGGERED_TIME("latestTriggeredTime"),
	TRIGGER_TYPE("triggerType"),
	TRIGGER_EXPRESSION("triggerExpression"),
	END_TIME("endTime"),
	STATE("state"),
	
	PROCESS_ID("processId"),
	VERSION("version"),
	PROCESS_TYPE("processsType"),
	
	CREATE_NEW_PROCESS_INSTANCE("createNewProcessInstance"),
	NOTE("note"),
	
	ACTIVITY_INSTANCE_$_ID("activityInstance.id"), 
	ACTIVITY_INSTANCE_$_PROCESSINSTANCE_ID(	"activityInstance.processInstanceId"),
	ACTIVITY_INSTANCE_$_BIZ_ID(	"activityInstance.bizId"), 
	ACTIVITY_INSTANCE_$_ACTIVITY_ID("activityInstance.activityId"),

	ACTIVITY_INSTANCE_$_SUSPENDED("activityInstance.suspended"),

	ACTIVITY_INSTANCE_$_PROCESS_NAME("activityInstance.processName"),
	ACTIVITY_INSTANCE_$_PROCESS_DISPLAY_NAME("activityInstance.processDisplayName"),
	ACTIVITY_INSTANCE_$_STEP_NUMBER("activityInstance.stepNumber");
	
	private String propertyName = null;
	private ScheduleJobProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	public String getPropertyName(){
		return this.propertyName;
	}
	
	public String getColumnName(){
		return this.name();
	}
	
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}
	
    public static ScheduleJobProperty fromValue(String v) {
        for (ScheduleJobProperty c: ScheduleJobProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_SCHEDULE_JOB;
	}
}
