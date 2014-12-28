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
package org.fireflow.client.impl;

/**
 * 用于系统内部传递属性的key常量，外部系统不应该使用本类。
 * @author 非也
 * @version 2.0
 */
public interface InternalSessionAttributeKeys {
	public static final String BIZ_ID = "org.fireflow.engine.impl.InternalSessionAttributeKeys.BIZ_ID";
	public static final String VARIABLES = "org.fireflow.engine.impl.InternalSessionAttributeKeys.VARIABLES";
	public static final String FIELDS_VALUES = "org.fireflow.engine.impl.InternalSessionAttributeKeys.FIELDS_VALUES";
}
