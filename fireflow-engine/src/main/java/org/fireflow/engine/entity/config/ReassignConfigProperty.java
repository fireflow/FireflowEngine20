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
package org.fireflow.engine.entity.config;

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
public enum ReassignConfigProperty  implements EntityProperty {
	PROCESS_ID("processId"),
	ROCESS_NAME("processName"),
	PROCESS_DISPLAY_NAME("processDisplayName"),
	PROCESS_TYPE("processType"),
	ACTIVITY_ID("activityId"),
	ACTIVITY_NAME("activityName"),
	ACTIVITY_DISPLAY_NAME("activityDisplayName"),
	GRANTOR_ID("grantorId"),
	GRANTOR_NAME("grantorName"),
	GRANTOR_DEPT_ID("grantorDeptId"),
	GRANTOR_DEPT_NAME("grantorDeptName"),
	AGENT_ID("agentId"),
	AGENT_NAME("agentName"),
	AGENT_TYPE("agentType"),
	START_TIME("startTime"),
	END_TIME("endTime"),
	ALIVE("alive")
	;

	String propertyName = null;
	private ReassignConfigProperty(String propName){
		propertyName = propName;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.EntityProperty#getPropertyName()
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.EntityProperty#getColumnName()
	 */
	public String getColumnName() {
		return this.name();
	}

	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("EngineMessages", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}
	
    public static ReassignConfigProperty fromValue(String v) {
        for (ReassignConfigProperty c: ReassignConfigProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_REASSIGN_CONFIG;
	}
}
