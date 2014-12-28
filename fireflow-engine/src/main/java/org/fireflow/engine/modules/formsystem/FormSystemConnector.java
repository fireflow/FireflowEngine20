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
package org.fireflow.engine.modules.formsystem;

import java.util.List;

import org.fireflow.engine.context.EngineModule;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface FormSystemConnector extends EngineModule{
	/**
	 * 从表单系统查找所有的顶层业务类别
	 * @return
	 */
	public List<FormCategory> findAllTopCategories();
	
	/**
	 * 查找下级业务类别
	 * @return
	 */
	public List<FormCategory> findChildCategories(String parentCategoryId);
	
	/**
	 * 根据业务类别查找其表单
	 * @param categoryId
	 * @return
	 */
	public List<Form> findForms(String categoryId);
	
	/**
	 * 根据表单Id查找表单对象
	 * @param formId
	 * @return
	 */
	public Form getFormById(String formId);
	
	/**
	 * 根据categoryId返回category对象
	 * @param categoryId
	 * @return
	 */
	public FormCategory getCategoryById(String categoryId);
}
