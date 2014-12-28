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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.server.support.EntityPropertyXmlAdapter;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="isNotNull")
@XmlType(name="isNotNullType")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotNullExpression  extends AbsCriterion implements Criterion {
	@XmlAttribute(name="field")
	@XmlJavaTypeAdapter(value=EntityPropertyXmlAdapter.class)
	private EntityProperty property;

	public NotNullExpression(){
		
	}
	
	public NotNullExpression(EntityProperty property) {
		this.property = property;
	}

	public String toSqlString() {
		return property.getColumnName() + " "+getOperation();
	}
	public String toString() {
		return property.getColumnName() + " "+getOperation();
	}

	public String getOperation(){
		return Criterion.OPERATION_IS_NOT_NULL;
	}
	public EntityProperty getEntityProperty(){
		return property;
	}
	public Object[] getValues(){
		return null;
	}
}
