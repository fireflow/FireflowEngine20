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
package org.fireflow.client.query;

import org.fireflow.engine.entity.EntityProperty;

/**
 * @author 非也
 * @version 2.0
 */
public interface Criterion {
	public static final String OPERATION_EQ = "=";
	public static final String OPERATION_NE = "<>";
	public static final String OPERATION_GT = ">";
	public static final String OPERATION_LT = "<";
	public static final String OPERATION_GE = ">=";
	public static final String OPERATION_LE = "<=";
	public static final String OPERATION_LIKE = "like";
	public static final String OPERATION_IN = "in";
	public static final String OPERATION_AND = "and";
	public static final String OPERATION_OR = "or";
	public static final String OPERATION_IS_NULL = "is null";
	public static final String OPERATION_IS_NOT_NULL = "is not null";
	public static final String OPERATION_BETWEEN = "between";
	
	public String toSqlString();
	public String getOperation();
	public EntityProperty getEntityProperty();
	public Object[] getValues();
}
