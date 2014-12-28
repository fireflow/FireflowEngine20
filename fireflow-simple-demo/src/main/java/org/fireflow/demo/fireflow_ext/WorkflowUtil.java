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

import javax.servlet.http.HttpSession;

import org.fireflow.demo.ou_management.entity.DemoDepartment;
import org.fireflow.demo.ou_management.entity.DemoUser;
import org.fireflow.demo.security.LoginServlet;
import org.fireflow.engine.modules.ousystem.Department;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.DepartmentImpl;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class WorkflowUtil {
	/**
	 * 从业务系统当前用户转换为工作流用户。<br/>
	 * 有两种方案可以实现该需求：1、业务系统中的用户对象（如DemoUser）实现org.fireflow.engine.modules.ousystem.User<br/>
	 * 2、将业务系统中的用户对象组装成org.fireflow.engine.modules.ousystem.impl.UserImpl返回，如本函数。
	 * 
	 * @param session
	 * @return
	 */
	public static User getCurrentWorkflowUser(HttpSession session){
		//从Session中获得当前用户
		DemoUser demoUser = (DemoUser)session.getAttribute(LoginServlet.CURRENT_USER_SESSION_KEY);
		User fireUser = convertAppUser2FireflowUser(demoUser);
		//userImpl.setProperties(properties);//可以设置更多属性
		return fireUser;
	}
	
	public static User convertAppUser2FireflowUser(DemoUser demoUser){
		if (demoUser==null)return null;
		UserImpl fireUser = new UserImpl();
		fireUser.setId(demoUser.getUserId());
		fireUser.setName(demoUser.getName());
		fireUser.setDeptId(demoUser.getDepartmentId());
		fireUser.setDeptName(demoUser.getDepartmentName());
		
		return fireUser;
	}
	
	public static Department convertAppDept2FireflowDept(DemoDepartment demoDept){
		if (demoDept==null)return null;
		DepartmentImpl dept = new DepartmentImpl();
		dept.setId(demoDept.getDepartmentId());
		dept.setName(demoDept.getDepartmentName());
		return dept;
	}
}
