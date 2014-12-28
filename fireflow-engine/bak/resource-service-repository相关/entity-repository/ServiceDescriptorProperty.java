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
package org.fireflow.engine.entity.repository;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;

/**
 * @author 非也
 * @version 2.0
 * @deprecated
 */
public enum ServiceDescriptorProperty implements EntityProperty {
	ID("id"),
	NAME("name"),
	DISPLAY_NAME("displayName"),
	DESCRIPTION("description"),
	FILE_NAME("fileName"),

	BIZ_TYPE("bizType"),
	
	PUBLISH_STATE("publishState"),
	LAST_EDITOR("lastEditor"),
	LAST_EDIT_TIME("lastEditTime"),
	LAST_OPERATION("lastOperation"),
	
	OWNER_DEPT_ID("ownerDeptId"),
	OWNER_DEPT_NAME("ownerDeptName"),
	APPROVER("approver"),
	APPROVED_TIME("approvedTime"),
	
	//Service 特定的属性
	SERVICE_ID("serviceId"),
	;
	private String propertyName = null;
	private ServiceDescriptorProperty(String propertyName){
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
	
    public static ServiceDescriptorProperty fromValue(String v) {
        for (ServiceDescriptorProperty c: ServiceDescriptorProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_SERVICE_DESCRIPTOR;
	}
}
