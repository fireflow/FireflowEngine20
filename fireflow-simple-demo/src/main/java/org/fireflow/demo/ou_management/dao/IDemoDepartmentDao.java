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

import org.fireflow.demo.ou_management.entity.DemoDepartment;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface IDemoDepartmentDao {
	/**
	 * 通过Id返回Department对象
	 * @param id
	 * @return
	 */
	public DemoDepartment findDepartmentById(String id);
	
	/**
	 * 返回所有顶层组织机构。对于树状组织机构而言，一般情况下返回的结果集中只有一条记录
	 * @return
	 */
	public List<DemoDepartment> findAllTopDepartments();
	
	public List<DemoDepartment> findChildDepartments(String parentId);
}
