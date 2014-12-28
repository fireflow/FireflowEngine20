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

import java.util.List;


/**
 * InstanceCreator,InstanceExecutor,InstanceTerminator的类属性描述符
 * @author 非也
 * @version 2.0
 */
public class ParameterDescriptor {
	private String name = null;
	private String displayName = null;
	private String dataType = null;
	private String dataPattern = null;
	private String inputControl = null;//输入方式
	private List<String> selectItems = null;//下拉列表的选项
	private boolean required = false;//是否必须录入
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataPattern() {
		return dataPattern;
	}
	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}
	
	
}
