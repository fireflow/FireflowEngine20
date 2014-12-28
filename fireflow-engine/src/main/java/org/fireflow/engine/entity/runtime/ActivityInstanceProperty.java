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
 * @author 非也
 * @version 2.0
 */
public enum ActivityInstanceProperty implements EntityProperty{
	ID("id"),
	NAME("name"),
	DISPLAY_NAME("displayName"),
	NODE_ID("nodeId"),
	
	PROCESS_ID("processId"),
	VERSION("version"),
	PROCESS_TYPE("processType"),
	SUBPROCESS_ID("subProcessId"),
	PROCESS_NAME("processName"),
	PROCESS_DISPLAY_NAME("processDisplayName"),
	SUBPROCESS_NAME("subProcessName"),
	SUBPROCESS_DISPLAY_NAME("subProcessDisplayName"),
	BIZ_TYPE("bizType"),
	
	SERVICE_ID("serviceId"),
	SERVICE_VERSION("serviceVersion"),
	SERVICE_TYPE("serviceType"),
	
	BIZ_ID("bizId"),
	SUB_BIZ_ID("subBizId"),
	PROCINST_CREATOR_ID("procInstCreatorId"),
	PROCINST_CREATOR_NAME("procInstCreatorName"),
	PROCINST_CREATED_TIME("procInstCreatedTime"),


	STATE("state"),
	CREATED_TIME("createdTime"),
	STARTED_TIME("startedTime"),
	END_TIME("endTime"),
	EXPIRED_TIME("expiredTime"),
	IS_SUSPENDED("isSuspended"),
	
	STEP_NUMBER("stepNumber"),
	PROCESS_INSTANCE_ID("processInstanceId"),
	PARENT_SCOPE_ID("parentScopeId"),
	TOKEN_ID("tokenId"),
	
	TARGET_ACTIVITY_ID("targetActivityId"),
//	ASSIGNMENT_STRATEGY("assignmentStrategy"),	
	NOTE("note")
	
	;
	
	
	private String propertyName = null;
	private ActivityInstanceProperty(String propertyName){
		this.propertyName = propertyName;
	}
	
	public String getPropertyName(){
		return this.propertyName;
	}
	
	public String getColumnName(){
		return this.name();
	}
	
	public String getDisplayName(Locale locale){
		ResourceBundle resb = ResourceBundle.getBundle("myres", locale);
		return resb.getString(this.name());
	}
	
	public String getDisplayName(){
		return this.getDisplayName(Locale.getDefault());
	}
	
    public static ActivityInstanceProperty fromValue(String v) {
        for (ActivityInstanceProperty c: ActivityInstanceProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_ACTIVITY_INSTANCE;
	}
//	public List<EntityProperty> getAllProperties(){
//		List<EntityProperty> all = new ArrayList<EntityProperty>();
//		all.add(ID);
//		return all;
//	}
}
