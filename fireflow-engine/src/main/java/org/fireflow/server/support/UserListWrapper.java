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
package org.fireflow.server.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.UserImpl;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlType(name="userListWrapperType")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserListWrapper {
	@XmlElement(name="user")
	List<UserImpl> users = new ArrayList<UserImpl>();
	
	
	public void putAll(List<User> us){
		users.clear();
		for (User u : us){
			UserImpl impl = new UserImpl();
			impl.setId(u.getId());
			impl.setName(u.getName());
			impl.setDeptId(u.getDeptId());
			impl.setDeptName(u.getDeptName());
			
			impl.setProperties((Properties)u.getProperties().clone());
			
			users.add(impl);
		}
	}
	
	public List<User> getUsers(){
		List<User> result = new ArrayList<User>();
		for (UserImpl u : users){
			result.add(u);
		}
		return result;
	}
}
