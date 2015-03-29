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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;

/**
 * @author 非也
 * @version 2.0
 */
public enum ProcessInstanceProperty implements EntityProperty {
	ID("id"),
	BIZ_ID("bizId"),
	
	PROCESS_ID("processId"),
	VERSION("version"),
	PROCESS_TYPE("processsType"),
	SUBPROCESS_ID("subProcessId"),
	
	PROCESS_NAME("processName"),
	PROCESS_DISPLAY_NAME("processDisplayName"),
	BIZ_TYPE("bizType"),
	PACKAGE_ID("packageId"),
	
	SUBPROCESS_NAME("subProcessName"),
	SUBPROCESS_DISPLAY_NAME("subProcessDisplayName"),
	
	STATE("state"),
	IS_SUSPENDED("isSuspended"),
	
	CREATOR_ID("creatorId"),
	CREATOR_NAME("creatorName"),
	CREATOR_ORG_ID("creatorOrgId"),
	CREATOR_ORG_NAME("creatorOrgName"),
	
	CREATED_TIME("createdTime"),
	STARTED_TIME("startedTime"),
	END_TIME("endTime"),
	EXPIRED_TIME("expiredTime"),


	PARENT_PROCESS_INSTANCE_ID("parentProcessInstanceId"),
	PARENT_ACTIVITY_INSTANCE_ID("parentActivityInstanceId"),
	PARENT_SCOPE_ID("parentScopeId"),
	
	TOKEN_ID("tokenId"),
	
	NOTE("note")
	
	;
	
	
	private String propertyName = null;
	private ProcessInstanceProperty(String propertyName){
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
	
    public static ProcessInstanceProperty fromValue(String v) {
        for (ProcessInstanceProperty c: ProcessInstanceProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_PROCESS_INSTANCE;
	}
//	public List<EntityProperty> getAllProperties(){
//		List<EntityProperty> all = new ArrayList<EntityProperty>();
//		all.add(ID);
//		return all;
//	}

}
