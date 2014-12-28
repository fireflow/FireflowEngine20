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
package org.fireflow.simulation.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.client.impl.WorkflowSessionRemoteImpl;
import org.fireflow.client.impl.WorkflowStatementRemoteImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.formsystem.Form;
import org.fireflow.engine.modules.formsystem.FormCategory;
import org.fireflow.engine.modules.formsystem.impl.FormCategoryImpl;
import org.fireflow.engine.modules.formsystem.impl.FormImpl;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.Role;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.DepartmentImpl;
import org.fireflow.engine.modules.ousystem.impl.GroupImpl;
import org.fireflow.engine.modules.ousystem.impl.RoleImpl;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.simulation.FireflowSimulator;
import org.fireflow.simulation.client.SimulatorStatement;
import org.fireflow.simulation.support.BreakPoint;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SimulatorStatementImpl extends WorkflowStatementRemoteImpl implements SimulatorStatement{
	public SimulatorStatementImpl(WorkflowSessionRemoteImpl session,String processType){
		super(session, processType);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#addBreakPoint(org.fireflow.simulation.support.BreakPoint)
	 */
	public void addBreakPoint(BreakPoint breakPoint) {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		simulator.addBreakPoint(this.remoteSession.getSessionId(), breakPoint);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#clearAllBreakPoint()
	 */
	public void clearAllBreakPoint() {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		simulator.clearAllBreakPoint(this.remoteSession.getSessionId());
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#clearBreakPoint(org.fireflow.simulation.support.BreakPoint)
	 */
	public void clearBreakPoint(BreakPoint breakPoint) {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		simulator.clearBreakPoint(this.remoteSession.getSessionId(),breakPoint);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#getAllBreakPoint()
	 */
	public List<BreakPoint> getAllBreakPoint() {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.getAllBreakPoint(this.remoteSession.getSessionId());

	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#forwardBreakPoint(org.fireflow.simulation.support.BreakPoint)
	 */
	public boolean forwardBreakPoint(BreakPoint breakpoint) throws EngineException {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.forwardBreakPoint(this.remoteSession.getSessionId(),breakpoint);
	}
	
	public void addBreakPointList(List<BreakPoint> breakPointList){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		simulator.addBreakPointList(this.remoteSession.getSessionId(),breakPointList);
	}
	public boolean forwardToken(String tokenId)throws EngineException{
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.forwardToken(this.remoteSession.getSessionId(), tokenId);
	}
	
	public void initSimulator(){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		simulator.initSimulator(this.remoteSession.getSessionId());

	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#findAllTopCategories()
	 */
	public List<FormCategory> findAllTopCategories() {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<FormCategoryImpl> list = simulator.findAllTopCategories(this.remoteSession.getSessionId());
		
		List<FormCategory> result = new ArrayList<FormCategory>();
		if (list!=null){
			for (FormCategoryImpl obj : list){
				result.add(obj);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#findChildCategories(java.lang.String)
	 */
	public List<FormCategory> findChildCategories(String parentCategoryId) {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<FormCategoryImpl> list = simulator.findChildCategories(this.remoteSession.getSessionId(),parentCategoryId);
		
		List<FormCategory> result = new ArrayList<FormCategory>();
		if (list!=null){
			for (FormCategoryImpl obj : list){
				result.add(obj);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#findForms(java.lang.String)
	 */
	public List<Form> findForms(String categoryId) {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<FormImpl> list = simulator.findForms(this.remoteSession.getSessionId(),categoryId);
		
		List<Form> result = new ArrayList<Form>();
		if (list!=null){
			for (FormImpl obj : list){
				result.add(obj);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#getFormById(java.lang.String)
	 */
	public Form getFormById(String formId) {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		Form form = simulator.getFormById(this.remoteSession.getSessionId(), formId);
		return form;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.simulation.client.SimulatorStatement#getCategoryById(java.lang.String)
	 */
	public FormCategory getCategoryById(String categoryId) {
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		FormCategory category = simulator.getCategoryById(this.remoteSession.getSessionId(), categoryId);
		return category;
	}
	
//	public void shutdown(){
//		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
//		simulator.shutdown();
//	}
	
	//===========与OU System Connector相关的方法====

	public User findUserById(String userId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.findUserById(this.remoteSession.getSessionId(), userId);
	}
	public List<User> findUsersInDepartment(String deptId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<UserImpl> list =  simulator.findUsersInDepartment(this.remoteSession.getSessionId(), deptId);
		List<User> result = new ArrayList<User>();
		if (list!=null){
			for (User u : list){
				result.add(u);
			}
		}
		return result;
	}
	public List<User> findUsersInRole(String roleId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<UserImpl> list =  simulator.findUsersInRole(this.remoteSession.getSessionId(), roleId);
		List<User> result = new ArrayList<User>();
		if (list!=null){
			for (User u : list){
				result.add(u);
			}
		}
		return result;
	}
	public List<User> findUsersInGroup(String groupId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<UserImpl> list =  simulator.findUsersInGroup(this.remoteSession.getSessionId(), groupId);
		List<User> result = new ArrayList<User>();
		if (list!=null){
			for (User u : list){
				result.add(u);
			}
		}
		return result;
	}
	
	public Department findDepartmentById(String id){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.findDepartmentById(this.remoteSession.getSessionId(), id);
	}
	public List<Department> findAllTopDepartments(){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<DepartmentImpl> list = simulator.findAllTopDepartments(this.remoteSession.getSessionId());
		List<Department> result = new ArrayList<Department>();
		if (list!=null){
			for (DepartmentImpl dpt : list){
				result.add(dpt);
			}
		}
		return result;
		
	}
	public List<Department> findChildDepartments(String parentId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<DepartmentImpl> list = simulator.findChildDepartments(this.remoteSession.getSessionId(),parentId);
		List<Department> result = new ArrayList<Department>();
		if (list!=null){
			for (DepartmentImpl dpt : list){
				result.add(dpt);
			}
		}
		return result;
	}
	
	public Role findRoleById(String id){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.findRoleById(this.remoteSession.getSessionId(), id);
	}
	public List<Role> findAllTopRoles(){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<RoleImpl> list = simulator.findAllTopRoles(this.remoteSession.getSessionId());
		List<Role> result = new ArrayList<Role>();
		if (list!=null){
			for (Role role : list){
				result.add(role);
			}
		}
		return result;
	}
	public List<Role> findChildRoles(String parentRoleId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<RoleImpl> list = simulator.findChildRoles(this.remoteSession.getSessionId(),parentRoleId);
		List<Role> result = new ArrayList<Role>();
		if (list!=null){
			for (Role role : list){
				result.add(role);
			}
		}
		return result;
	}
	public List<Role> findRolesInDepartment(String deptId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<RoleImpl> list = simulator.findRolesInDepartment(this.remoteSession.getSessionId(),deptId);
		List<Role> result = new ArrayList<Role>();
		if (list!=null){
			for (Role role : list){
				result.add(role);
			}
		}
		return result;
	}
	
	public Group findGroupById(String id){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		return simulator.findGroupById(this.remoteSession.getSessionId(), id);
	}
	public List<Group> findAllGroups(){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<GroupImpl> list = simulator.findAllGroups(this.remoteSession.getSessionId());
		List<Group> result = new ArrayList<Group>();
		if (list!=null){
			for (GroupImpl g : list){
				result.add(g);
			}
		}
		return result;
	}
	public List<Group> findGroupsInDepartment(String deptId){
		FireflowSimulator simulator = (FireflowSimulator)this.getWorkflowServer();
		List<GroupImpl> list = simulator.findGroupsInDepartment(this.remoteSession.getSessionId(),deptId);
		List<Group> result = new ArrayList<Group>();
		if (list!=null){
			for (GroupImpl g : list){
				result.add(g);
			}
		}
		return result;
	}
}
