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
package org.fireflow.pvm.kernel;

import java.util.Locale;
import java.util.ResourceBundle;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.config.FireflowConfigProperty;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public enum TokenProperty implements EntityProperty{
	ID("id"),
	PROCESS_INSTANCE_ID("processInstanceId"),
	ELEMENT_INSTANCE_ID("elementInstanceId"),
	ALIVE("alive"),
	VALUE("value"),
	STEP_NUMBER("stepNumber"),
	STATE("state"),
	PROCESS_ID("processId"),
	VERSION("version"),
	PROCESS_TYPE("processType"),
	ELEMENT_ID("elementId"),
	OPERATION_CONTEXT_NAME("operationContextName"),
	CALLBACK_TOKEN_ID("callbackTokenId"),
	PARENT_TOKEN_ID("parentTokenId")
	;
	private String propertyName = null;
	private TokenProperty(String propertyName){
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
    public static TokenProperty fromValue(String v) {
        for (TokenProperty c: TokenProperty.values()) {
            if (c.getPropertyName().equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
	public String getEntityName(){
		return WorkflowEntity.ENTITY_NAME_TOKEN;
	}
}
