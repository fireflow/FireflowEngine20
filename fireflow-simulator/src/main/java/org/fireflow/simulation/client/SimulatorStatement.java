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
package org.fireflow.simulation.client;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.formsystem.Form;
import org.fireflow.engine.modules.formsystem.FormCategory;
import org.fireflow.engine.modules.formsystem.impl.FormCategoryImpl;
import org.fireflow.engine.modules.formsystem.impl.FormImpl;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.Role;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.simulation.support.BreakPoint;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface SimulatorStatement extends WorkflowStatement {
	public void addBreakPoint(BreakPoint breakPoint);
	

	public void clearAllBreakPoint();

	public void clearBreakPoint(BreakPoint breakPoint);

	public List<BreakPoint> getAllBreakPoint();
	
	public void addBreakPointList(List<BreakPoint> breakPointList);
	
	public boolean forwardBreakPoint(BreakPoint breakpoint)throws EngineException;
	
	public boolean forwardToken(String tokenId)throws EngineException;

	public void initSimulator();
	

	public  List<FormCategory> findAllTopCategories();
	
	public List<FormCategory> findChildCategories(String parentCategoryId);
	
	public List<Form> findForms(String categoryId);
	
	public Form getFormById(String formId);
	
	public FormCategory getCategoryById(String categoryId);
	
	//===========与OU System Connector相关的方法====
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
