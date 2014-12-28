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
package org.fireflow.client.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlType(name="absCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({BetweenExpression.class,InExpression.class,
	LogicalExpression.class,NotNullExpression.class,NullExpression.class,
	SimpleExpression.class})
public abstract class AbsCriterion implements Criterion {

	public String valueToSQLString(Object value){
		if (JavaDataTypeConvertor.isDouble(value.getClass().getName())
				|| JavaDataTypeConvertor.isFloat(value.getClass().getName())
				|| JavaDataTypeConvertor.isShort(value.getClass().getName())
				|| JavaDataTypeConvertor.isLong(value.getClass().getName())
				|| JavaDataTypeConvertor.isInt(value.getClass().getName())){
			return value.toString();

		}
		else if (JavaDataTypeConvertor.isDate(value.getClass().getName())){//日期函数只能暂时这么处理
			return "'"+value.toString()+"'";
		}
		else if (JavaDataTypeConvertor.isString(value.getClass().getName())
				|| JavaDataTypeConvertor.isChar(value.getClass().getName())
				|| JavaDataTypeConvertor.isBoolean(value.getClass().getName())){//boolean当字符串处理
			return "'"+value.toString()+"'";
		}
		
		return "'"+value.toString()+"'";
	}
	
}
