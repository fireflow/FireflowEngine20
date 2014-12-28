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
package org.fireflow.engine.modules.ousystem;

import java.util.List;

import org.fireflow.engine.context.EngineModule;

/**
 * 
 * 组织机构与用户管理系统接口，业务系统提供该接口实现类便可与现有的组织机构管理系统对接。
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface OUSystemConnector extends EngineModule{
	public User login(String userId,String password);
	public User findUserById(String userId);
	public List<User> findUsersInDepartment(String deptId);
	public List<User> findUsersInRole(String roleId);
	public List<User> findUsersInGroup(String groupId);
	
	public Department findDepartmentById(String id);
	public List<Department> findAllTopDepartments();
	public List<Department> findChildDepartments(String parentId);
	
	public Role findRoleById(String id);
	public List<Role> findAllTopRoles();
	public List<Role> findChildRoles(String parentRoleId);
	public List<Role> findRolesInDepartment(String deptId);
	
	public Group findGroupById(String id);
	public List<Group> findAllGroups();
	public List<Group> findGroupsInDepartment(String deptId);
	
}
