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
package org.fireflow.engine.entity.config;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public enum FireflowConfigProperty implements EntityProperty {
	ID("id"),
	CONFIG_ID("configId"),
	CONFIG_NAME("configName"),
	CONFIG_VALUE("configValue"),
	DESCRIPTION("description"),
	CATEGORY_ID("categoryId"),
	PARENT_CONFIG_ID("parentConfigId"),
	LAST_EDITOR("lastEditor"),
	LAST_UPDATE_TIME("lastUpdateTime")
	;
	
	private String propertyName = null;
	private FireflowConfigProperty(String propertyName){
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
	
    public static FireflowConfigProperty fromValue(String v) {
        for (FireflowConfigProperty c: FireflowConfigProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_FIREFLOW_CONFIG;
	}
}
