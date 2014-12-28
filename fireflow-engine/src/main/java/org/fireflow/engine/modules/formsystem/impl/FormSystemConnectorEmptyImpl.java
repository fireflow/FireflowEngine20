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
package org.fireflow.engine.modules.formsystem.impl;

import java.util.List;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.formsystem.FormCategory;
import org.fireflow.engine.modules.formsystem.Form;
import org.fireflow.engine.modules.formsystem.FormSystemConnector;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FormSystemConnectorEmptyImpl implements FormSystemConnector {
	public void init(RuntimeContext runtimeContext) throws EngineException {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormSystemConnector#findAllTopCategories()
	 */
	public List<FormCategory> findAllTopCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormSystemConnector#findChildCategories(java.lang.String)
	 */
	public List<FormCategory> findChildCategories(String parentCategoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormSystemConnector#findForms(java.lang.String)
	 */
	public List<Form> findForms(String categoryId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormSystemConnector#getFormById(java.lang.String)
	 */
	public Form getFormById(String formId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormSystemConnector#getCategoryById(java.lang.String)
	 */
	public FormCategory getCategoryById(String categoryId) {
		// TODO Auto-generated method stub
		return null;
	}

}
