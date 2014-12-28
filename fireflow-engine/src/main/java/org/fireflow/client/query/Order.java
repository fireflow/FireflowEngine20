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
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.EntityProperty;
import org.fireflow.server.support.EntityPropertyXmlAdapter;

/**
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="orderBy")
@XmlType(name="orderByType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Order {
	
	@XmlAttribute(name="asc")
	private boolean ascending = true;
	
	@XmlValue
	@XmlJavaTypeAdapter(value = EntityPropertyXmlAdapter.class)
	private EntityProperty property;
	
	public String toString() {
		return (property==null?"":property.getColumnName()) + ' ' + (ascending?"asc":"desc");
	}
	
//	public Order ignoreCase() {
//		ignoreCase = true;
//		return this;
//	}

	public Order(){
		
	}
	/**
	 * Constructor for Order.
	 */
	public Order(EntityProperty propertyName, boolean ascending) {
		this.property = propertyName;
		this.ascending = ascending;
	}

	/**
	 * Render the SQL fragment
	 *
	 */
	public String toSqlString() {
		return property.getColumnName() + ' ' + (ascending?"asc":"desc");
	}

	/**
	 * Ascending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(EntityProperty propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(EntityProperty propertyName) {
		return new Order(propertyName, false);
	}
	
	public EntityProperty getEntityProperty(){
		return this.property;
	}
	
	public Boolean isAscending(){
		return this.ascending;
	}
}
