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
package org.fireflow.model.misc;


/**
 * @author 非也
 * @version 2.0
 */
public class PropertyValue {
	private final String name;

	private final String orignalValue;

	private boolean converted = false;

	private Object convertedValue;
	
	public PropertyValue(String name, String value) {
		this.name = name;
		this.orignalValue = value;
		//v2.0版本中不考虑property转换问题
		this.convertedValue = this.orignalValue;
	}

	public boolean isConverted() {
		return converted;
	}

	public void setConverted(boolean converted) {
		this.converted = converted;
	}

	public Object getConvertedValue() {
		return convertedValue;
	}

	public void setConvertedValue(Object convertedValue) {
		this.convertedValue = convertedValue;
	}

	public String getName() {
		return name;
	}

	public String getOrignalValue() {
		return orignalValue;
	}	
	
	public int hashCode() {
		return this.name.hashCode() * 29 + this.orignalValue==null?0:this.orignalValue.hashCode();
	}

	public String toString() {
		return "bean property '" + this.name + "'";
	}

}
