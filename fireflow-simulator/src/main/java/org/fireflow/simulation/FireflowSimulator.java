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
package org.fireflow.simulation;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.formsystem.impl.FormCategoryImpl;
import org.fireflow.engine.modules.formsystem.impl.FormImpl;
import org.fireflow.engine.modules.ousystem.impl.DepartmentImpl;
import org.fireflow.engine.modules.ousystem.impl.GroupImpl;
import org.fireflow.engine.modules.ousystem.impl.RoleImpl;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;
import org.fireflow.server.WorkflowEngineService;
import org.fireflow.simulation.support.BreakPoint;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@WebService(name=WorkflowEngineService.PORT_TYPE,
		targetNamespace=WorkflowEngineService.TARGET_NAMESPACE)
public interface FireflowSimulator extends WorkflowEngineService {
	
	@WebMethod
	public void addBreakPoint(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="newBreakPoint") BreakPoint breakPoint);
	
	@WebMethod
	public void addBreakPointList(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="newBreakPoint") List<BreakPoint> breakPointList
			);
	
	@WebMethod
	public void clearAllBreakPoint(@WebParam(name="sessionId")String sessionId);
	
	@WebMethod
	public void clearBreakPoint(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="newBreakPoint") BreakPoint breakPoint);
	
	@WebMethod
	public @WebResult(name="breakPoint") List<BreakPoint> getAllBreakPoint(
			@WebParam(name="sessionId")String sessionId);
	
	/**
	 * 断点处继续执行下一步
	 * @param elementId
	 * @throws EngineException
	 */
	@WebMethod
	public @WebResult(name="result") boolean forwardBreakPoint(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="breakPoint") BreakPoint breakpoint)throws EngineException;
	
	@WebMethod
	public @WebResult(name="result") boolean forwardToken(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="tokenId") String tokenId)throws EngineException;
	
	@WebMethod
	public void initSimulator(@WebParam(name="sessionId")String sessionId);

	//****************************************************
	//*** 下面是 FormSystemConnector中的方法
	//*****************************************************
	/**
	 * 从表单系统查找所有的顶层业务类别
	 * @return
	 */
	@WebMethod
	public  @WebResult(name="formCategory")  List<FormCategoryImpl> findAllTopCategories(
			@WebParam(name="sessionId")String sessionId);
	
	/**
	 * 查找下级业务类别
	 * @return
	 */
	@WebMethod
	public  @WebResult(name="formCategory") List<FormCategoryImpl> findChildCategories(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="parentCategoryId") String parentCategoryId);
	
	/**
	 * 根据业务类别查找其表单
	 * @param categoryId
	 * @return
	 */
	@WebMethod
	public @WebResult(name="form") List<FormImpl> findForms(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="categoryId") String categoryId);
	
	/**
	 * 根据表单Id查找表单对象
	 * @param formId
	 * @return
	 */
	@WebMethod
	public @WebResult(name="form") FormImpl getFormById(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="formId") String formId);
	
	/**
	 * 根据categoryId返回category对象
	 * @param categoryId
	 * @return
	 */
	@WebMethod
	public @WebResult(name="formCategory") FormCategoryImpl getCategoryById(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="categoryId") String categoryId);
	
	//****************************************************
	//*** 下面是 OUSystemConnector中的方法
	//*****************************************************
	@WebMethod
	public @WebResult(name="user") UserImpl findUserById(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="userId") String userId);
	
	@WebMethod
	public @WebResult(name="user") List<UserImpl> findUsersInDepartment(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="deptId") String deptId);
	
	@WebMethod
	public @WebResult(name="user") List<UserImpl> findUsersInRole(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="roleId") String roleId);
	
	@WebMethod
	public @WebResult(name="user") List<UserImpl> findUsersInGroup(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="groupId") String groupId);
	
	@WebMethod
	public @WebResult(name="department") DepartmentImpl findDepartmentById(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="id") String id);
	
	@WebMethod
	public @WebResult(name="department") List<DepartmentImpl> findAllTopDepartments(
			@WebParam(name="sessionId")String sessionId);
	
	@WebMethod
	public @WebResult(name="department") List<DepartmentImpl> findChildDepartments(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="parentId") String parentId);
	
	@WebMethod
	public @WebResult(name="role") RoleImpl findRoleById(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="id")String id);
	
	@WebMethod
	public @WebResult(name="role") List<RoleImpl> findAllTopRoles(
			@WebParam(name="sessionId")String sessionId);
	
	@WebMethod
	public @WebResult(name="role") List<RoleImpl> findChildRoles(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="parentRoleId") String parentRoleId);
	
	@WebMethod
	public @WebResult(name="role") List<RoleImpl> findRolesInDepartment(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="departmentId") String deptId);
	
	@WebMethod
	public @WebResult(name="group") GroupImpl findGroupById(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="id")String id);
	
	@WebMethod
	public @WebResult(name="group") List<GroupImpl> findAllGroups(
			@WebParam(name="sessionId")String sessionId);
	
	@WebMethod
	public @WebResult(name="group") List<GroupImpl> findGroupsInDepartment(
			@WebParam(name="sessionId")String sessionId,
			@WebParam(name="departmentId") String deptId);
}
