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
public enum WorkItemProperty implements EntityProperty {
	ID("id"),
	WORKITEM_TYPE("workItemType"),
	
	WORKITEM_NAME("workItemName"),
	SUBJECT("subject"),
	STATE("state"),
	
	CREATED_TIME("createdTime"),
	CLAIMED_TIME("claimedTime"),
	END_TIME("endTime"),
	EXPIRED_TIME("expiredTime"),
	
	OWNER_ID("ownerId"),
	OWNER_NAME("ownerName"),
	OWNER_DEPT_ID("ownerDeptId"),
	OWNER_DEPT_NAME("ownerDeptName"),

	ACTION_RUL("actionUrl"),
	MOBILE_ACTION_URL("mobileActionUrl"),
	
	ORIGINAL_SYSTEM_NAME("originalSystemName"),
	BIZ_ID("bizId"),
	NOTE("note"),
	
	REMOTE_WORKITEM_ID("remoteWorkItemId"),
	
	OWNER_TYPE("ownerType"),
	
	ASSIGNMENT_STRATEGY("assignmentStrategy"),
	
	RESPONSIBLE_PERSON_ID("responsiblePersonId"),
	RESPONSIBLE_PERSON_NAME("responsiblePersonName"),
	RESPONSIBLE_PERSON_DEPT_ID("responsiblePersonDeptId"),
	RESPONSIBLE_PERSON_DEPT_NAME("responsiblePersonDeptName"),
	
	REASSIGN_TYPE("reassignType"),
	PARENT_WORKITEM_ID("parentWorkItemId"),
	
	ATTACHMENT_ID("attachmentId"),
	ATTACHMENT_TYPE("attachmentType"),
	
	PROCINST_CREATOR_NAME("procInstCreatorName"),
	PROCINST_CREATOR_ID("procInstCreatorId"),
	PROcINST_CREATED_TIME("procInstCreatedTime"),
	PROCESSS_ID("processId"),
	PROCESS_TYPE("processType"),
	VERSION("version"),
	SUBPROCESS_ID("subProcessId"),
	ACTIVITY_ID("activityId"),	
	PROCESS_INSTANCE_ID("processInstanceId"),
	STEP_NUMBER("stepNumber"),
	
	ACTIVITY_INSTANCE_ID("activityInstanceId"), 

	;
	
	
	private String propertyName = null;
	private WorkItemProperty(String propertyName){
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
	
	public List<EntityProperty> getAllProperties(){
		List<EntityProperty> all = new ArrayList<EntityProperty>();
		all.add(ID);
		return all;
	}
	
    public static WorkItemProperty fromValue(String v) {
        for (WorkItemProperty c: WorkItemProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_WORKITEM;
	}

}
