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
package org.fireflow.model.resourcedef;

import java.util.List;
import java.util.Map;

import org.fireflow.model.ModelElement;
import org.fireflow.model.data.Input;

/**
 * 

 * @author 非也
 * @version 2.0
 */
public interface ResourceDef extends ModelElement{
//	public static final String EXT_ATTR_KEY_ACTIVITY_ID = "ACTIVITY_ID";
//	public static final String EXT_ATTR_KEY_DEPARTMENT_ID = "DEPARTMENT_ID";
//	public static final String EXT_ATTR_KEY_ROLE_ID = "ROLE_ID";
//	public static final String EXT_ATTR_KEY_USER_ID = "USER_ID";
//	public static final String EXT_ATTR_KEY_GROUP_ID = "GROUP_ID";
//	public static final String EXT_ATTR_KEY_VARIABLE_NAME = "VARIABLE_NAME";
	
	public ResourceType getResourceType() ;
	
	public void setResourceType(ResourceType type);
	

	public String getValue();
	
	public void setValue(String value);

	public String getResolverBeanName();
	
	public void setResolverBeanName(String beanName);
	
	public String getResolverClassName();
	
	public void setResolverClassName(String className);
	
	public Map<String,String> getExtendedAttributes();
}
