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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.modules.formsystem.FormCategory;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="formCategory")
@XmlType(name="formCategoryType")
@XmlAccessorType(XmlAccessType.FIELD)
public class FormCategoryImpl implements FormCategory {
	@XmlElement(name="categoryId")
	private String categoryId;
	
	@XmlElement(name="displayName")
	private String displayName;
	
	@XmlElement(name="parentId")
	private String parentId;

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormCategory#getCategoryId()
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormCategory#setCategoryId(java.lang.String)
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormCategory#getDisplayName()
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormCategory#setDisplayName(java.lang.String)
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormCategory#getParentId()
	 */
	public String getParentId() {
		return this.parentId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.FormCategory#setParentId(java.lang.String)
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
