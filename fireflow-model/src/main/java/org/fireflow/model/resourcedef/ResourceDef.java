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
 * <Resources>
 * 	<Resource id="org.fireflow.model.resourcedef.PROCESS_INSTANCE_CREATOR" name="Process Creator"
 *  	displayName="流程创建者" type="ProcessInstanceCreator" pid="">
 *   	<Resolver bean="org.fireflow.engine.resource.impl.ProcessInstanceCreatorResolver">
 *   	</Resolver>
 * 	</Resource>
 * 	<Resource id="dept_research" name="dept_research"
 *  	displayName="研发部" type="Department" pid="">
 *   	<Resolver bean="org.fireflow.engine.resource.impl.DepartmentResolver">
 *   	</Resolver>
 * 	</Resource>
 * 	<Resource id="role_manager" name="role_manager"
 *  	displayName="部门经理" type="Role" pid="">
 *   	<Resolver bean="#RoleResolver">
 *   	</Resolver>
 * 	</Resource>
 * </Resources>
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
	
	/**
	 * resource的值，具体含义由ResourceType决定。<br/>
	 * 当resourceType为USER时，表示用户Id;<br/>
	 * 当resourceType为ROLE时，表示RoleId;<br/>
	 * 当resourceType为Group时，表示GroupId;<br/>
	 * 当resourceType为Department时，表示DepartmentId;<br/>
	 * 当resourceType为PROCESS_INSTANCE_CREATOR时，表示processId;<br/>
	 * 当resourceType为ACTIVITY_INSTANCE_PERFORMER时，表示activityId;<br/>
	 * 当resourceType为VARIABLE_IMPLICATION时，表示变量的名字.
	 * @return
	 */
	public String getValue();
	
	public void setValue(String value);

	public String getResolverBeanName();
	
	public void setResolverBeanName(String beanName);
	
	public String getResolverClassName();
	
	public void setResolverClassName(String className);
	
	public Map<String,String> getExtendedAttributes();
}
