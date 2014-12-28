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
package org.fireflow.demo.ou_management.dao;

import java.util.List;

import org.fireflow.demo.ou_management.entity.DemoUser;
import org.fireflow.engine.modules.ousystem.User;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface IDemoUserDao {
	/**
	 * 用户认证，直接采用最简单的认证方法，仅适合本demo
	 * @param userId
	 * @param password
	 * @return
	 */
	public DemoUser authenticateUser(String userId,String password);
	
	/**
	 * 通过UserId获得User对象
	 * @param userId
	 * @return
	 */
	public DemoUser findUserById(String userId);
	
	/**
	 * 查找同一部门的所有的人
	 * @param deptId
	 * @return
	 */
	public List<DemoUser> findUsersInDepartment(String deptId);
}
