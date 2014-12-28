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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.engine.entity.EntityState;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.server.support.EntityPropertyXmlAdapter;
import org.fireflow.server.support.ObjectListXmlAdapter;
import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="in")
@XmlType(name="inType")
@XmlAccessorType(XmlAccessType.FIELD)
public class InExpression  extends AbsCriterion implements Criterion {
	@XmlAttribute(name="property")
	@XmlJavaTypeAdapter(EntityPropertyXmlAdapter.class)
	private EntityProperty property;
	
	@XmlElement(name="values")
	@XmlJavaTypeAdapter(ObjectListXmlAdapter.class)
	private Object[] values ;
	
	public InExpression(){
		
	}

	public InExpression(EntityProperty property, Object[] values) {
		if (values!=null){
			for (Object obj : values){
				if (obj!=null){
					if (!JavaDataTypeConvertor.isPrimaryObject(obj)
							&& !(obj instanceof EntityState)
							&& !(obj instanceof OperationContextName)
							&& !(obj instanceof WorkItemAssignmentStrategy)){
						throw new IllegalArgumentException("In表达式只接受基本数据类型（包含String ,java.util.Date,不含byte），而你输入的value是"+obj.getClass().getName());
					}
				}
			}
		}
		
		this.property = property;
		
		if (values!=null){
			this.values = values;
		}else{
			this.values  = new Object[]{};
		}

	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.Criterion#toSqlString()
	 */
	public String toSqlString() {
		return property.getColumnName() + " in (" + getPlaceHolder(values) + ")";
	}

	public String toString() {
		return property.getColumnName() + " in (" + getValuesString(values) + ")";
	}
	
	private String getValuesString(Object[] values){
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<values.length;i++){
			Object v = values[i];
			buf.append(valueToSQLString(v)); 
			if (i<values.length-1){
				buf.append(",");
			}
		}
		return buf.toString();
	}
	

	
	private String getPlaceHolder(Object[] values){
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<values.length;i++){
			Object v = values[i];
			buf.append("?");
			if (i<values.length-1){
				buf.append(",");
			}
		}
		return buf.toString();
	}
	
	public String getOperation(){
		return Criterion.OPERATION_IN;
	}
	public EntityProperty getEntityProperty(){
		return property;
	}
	public Object[] getValues(){
		return values;
	}	
}
