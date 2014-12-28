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
public class Restrictions {

	public static SimpleExpression eq(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_EQ);
	}
	
	public static SimpleExpression ne(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_NE);
	}	
	
	public static SimpleExpression like(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_LIKE);
	}	
	
	public static SimpleExpression gt(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_GT);
	}

	public static SimpleExpression lt(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_LT);
	}
	
	public static SimpleExpression le(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_GE);
	}

	public static SimpleExpression ge(EntityProperty property, Object value) {
		return new SimpleExpression(property, value, Criterion.OPERATION_LE);
	}	
	
	public static LogicalExpression and(Criterion lhs, Criterion rhs) {
		return new LogicalExpression(lhs, rhs, Criterion.OPERATION_AND);
	}

	public static LogicalExpression or(Criterion lhs, Criterion rhs) {
		return new LogicalExpression(lhs, rhs, Criterion.OPERATION_OR );
	}
	
	public static Criterion isNull(EntityProperty property) {
		return new NullExpression(property);
	}
	
	public static Criterion isNotNull(EntityProperty property) {
		return new NotNullExpression(property);
	}
	
	public static Criterion in(EntityProperty property, Object[] values) {
		return new InExpression(property, values);
	}
	
	public static Criterion between(EntityProperty property, Object lo, Object hi) {
		return new BetweenExpression(property, lo, hi);
	}
}
