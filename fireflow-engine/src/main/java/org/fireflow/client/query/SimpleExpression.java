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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.EntityState;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.server.support.EntityPropertyXmlAdapter;
import org.fireflow.server.support.ObjectXmlAdapter;
import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="simpleExp")
@XmlType(name="simpleExpType",propOrder={"property","op","value"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleExpression  extends AbsCriterion  implements Criterion{
	@XmlElement(name="property")
	@XmlJavaTypeAdapter(EntityPropertyXmlAdapter.class)
	private EntityProperty property;
	
	@XmlElement(name="operation")
	private String op;

	@XmlElement(name="value")
	@XmlJavaTypeAdapter(ObjectXmlAdapter.class)
	private Object value;
	
	public SimpleExpression(){
		
	}

	
	public SimpleExpression(EntityProperty property, Object value, String op) {
		if (value!=null){
			if (!JavaDataTypeConvertor.isPrimaryObject(value)
					&& !(value instanceof EntityState)
					&& !(value instanceof OperationContextName)
					&& !(value instanceof WorkItemAssignmentStrategy)){
				throw new IllegalArgumentException("简单表达式只接受基本数据类型（包含String ,java.util.Date,不含byte），而你输入的value是"+value.getClass().getName());
			}
		}

		this.property = property;
		this.value = value;
		this.op = op;
	}
	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.Criterion#toSqlString()
	 */
	public String toSqlString() {
		return " "+property.getColumnName() +" "+ getOperation() + " ? ";
	}
	public String toString() {
		return property.getColumnName() +" "+ getOperation() +" "+ this.valueToSQLString(value);
	}

	
	public String getOperation(){
		return op;
	}
	public EntityProperty getEntityProperty(){
		return property;
	}
	public Object[] getValues(){
		return new Object[]{value};
	}
}
