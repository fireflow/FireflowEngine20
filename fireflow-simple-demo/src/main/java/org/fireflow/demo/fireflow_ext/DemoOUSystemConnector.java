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
package org.fireflow.demo.fireflow_ext;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.demo.ou_management.dao.IDemoDepartmentDao;
import org.fireflow.demo.ou_management.dao.IDemoUserDao;
import org.fireflow.demo.ou_management.entity.DemoDepartment;
import org.fireflow.demo.ou_management.entity.DemoUser;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.Role;
import org.fireflow.engine.modules.ousystem.User;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class DemoOUSystemConnector implements OUSystemConnector {
	RuntimeContext runtimeContest = null;
	IDemoUserDao userDao = null;
	IDemoDepartmentDao departmentDao = null;
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.EngineModule#init(org.fireflow.engine.context.RuntimeContext)
	 */
	public void init(RuntimeContext rtCtx) throws EngineException {
		this.runtimeContest = rtCtx;
	}
	
	public void setDemoUserDao(IDemoUserDao dao){
		userDao = dao;
	}
	
	public void setDemoDepartmentDao(IDemoDepartmentDao dao){
		departmentDao = dao;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findUserById(java.lang.String)
	 */
	public User findUserById(String userId) {
		DemoUser user = userDao.findUserById(userId);
		return WorkflowUtil.convertAppUser2FireflowUser(user);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findUsersInDepartment(java.lang.String)
	 */
	public List<User> findUsersInDepartment(String deptId) {
		List<DemoUser> userList = userDao.findUsersInDepartment(deptId);
		List<User> fireUserList = new ArrayList<User>();
		for (DemoUser user : userList){
			User fireUser = WorkflowUtil.convertAppUser2FireflowUser(user);
			fireUserList.add(fireUser);
		}
		return fireUserList;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findUsersInRole(java.lang.String)
	 */
	public List<User> findUsersInRole(String roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findUsersInGroup(java.lang.String)
	 */
	public List<User> findUsersInGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findDepartmentById(java.lang.String)
	 */
	public Department findDepartmentById(String id) {
		return WorkflowUtil.convertAppDept2FireflowDept(departmentDao.findDepartmentById(id));
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findAllTopDepartments()
	 */
	public List<Department> findAllTopDepartments() {
		List<DemoDepartment> list = departmentDao.findAllTopDepartments();
		if (list==null) return null;
		List<Department> result = new ArrayList<Department>();
		for (DemoDepartment dept : list){
			result.add(WorkflowUtil.convertAppDept2FireflowDept(dept));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findChildDepartments(java.lang.String)
	 */
	public List<Department> findChildDepartments(String parentId) {
		List<DemoDepartment> list = departmentDao.findChildDepartments(parentId);
		if (list==null) return null;
		List<Department> result = new ArrayList<Department>();
		for (DemoDepartment dept : list){
			result.add(WorkflowUtil.convertAppDept2FireflowDept(dept));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findRoleById(java.lang.String)
	 */
	public Role findRoleById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findAllTopRoles()
	 */
	public List<Role> findAllTopRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findChildRoles(java.lang.String)
	 */
	public List<Role> findChildRoles(String parentRoleId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findRolesInDepartment(java.lang.String)
	 */
	public List<Role> findRolesInDepartment(String deptId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findGroupById(java.lang.String)
	 */
	public Group findGroupById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findAllGroups()
	 */
	public List<Group> findAllGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#findGroupsInDepartment(java.lang.String)
	 */
	public List<Group> findGroupsInDepartment(String deptId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.OUSystemConnector#login(java.lang.String, java.lang.String)
	 */
	public User login(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

}
