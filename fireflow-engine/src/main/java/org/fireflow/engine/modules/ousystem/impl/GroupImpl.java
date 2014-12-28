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

import org.fireflow.engine.modules.ousystem.Group;
import org.fireflow.engine.modules.ousystem.User;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="group")
@XmlType(name="groupType")
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupImpl extends AbsActor implements Group {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4304683385733993209L;

	/**
	 * 可以为空
	 */
	@XmlElement(name="deptId")
	private String deptId = null;
	
	/**
	 * 可以为空
	 */
	@XmlElement(name="deptName")
	private String deptName = null;

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.User#getDeptId()
	 */
	public String getDeptId() {
		return deptId;
	}
	
	public void setDeptId(String argDeptId){
		this.deptId = argDeptId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.ousystem.User#getDeptName()
	 */
	public String getDeptName() {
		return deptName;
	}
	
	public void setDeptName(String argDeptName){
		this.deptName = argDeptName;
	}
	
	public String getParentId(){
		return this.getDeptId();
	}

	public void copy(Group g){
		this.setId(g.getId());
		this.setName(g.getName());
		this.setDeptId(g.getDeptId());
		this.setDeptName(g.getDeptName());
		this.setProperties(g.getProperties());
	}
}
