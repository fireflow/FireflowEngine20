/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.modules.ousystem.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.modules.ousystem.Department;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="department")
@XmlType(name="departmentType")
@XmlAccessorType(XmlAccessType.FIELD)
public class DepartmentImpl extends AbsActor implements
		org.fireflow.engine.modules.ousystem.Department {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7311223069262345995L;

	@XmlElement(name="parentId")
	private String parentId = null;
	
	public String getParentId(){
		return parentId;
	}
	public void setParentId(String pid){
		parentId = pid;
	}
	
	public void copy(Department d){
		this.setId(d.getId());
		this.setName(d.getName());
		this.setProperties(d.getProperties());
		this.setParentId(d.getParentId());
	}
}
