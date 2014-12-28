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
package org.fireflow.engine.modules.ousystem;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author 非也
 * @version 2.0
 */
public interface Actor extends Serializable {
//	public static final String ID = "ID";
//	public static final String NAME = "NAME";
//	public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
	
	public String getId();
	public String getName();

	public String getProperty(String propertyName);
	public Properties getProperties();
	/**
	 * 对于User、Group类型类型的对象，返回的是departmentId;<br/>
	 * 其他类型的对象返回的是其父对象的Id
	 * @return
	 */
	public String getParentId();
}
