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
package org.fireflow.engine.entity.runtime;

import javax.xml.namespace.QName;

import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.invocation.Message;

/**
 * 流程变量
 * @author 非也
 * @version 2.0
 */
public interface  Variable extends WorkflowEntity,Message{
	public static final String HEADER_KEY_CLASS_NAME = "org.fireflow.engine.variable.CLASS_NAME";
	public static final String HEADER_KEY_ENCODING = "org.fireflow.engine.variable.ENCODING";	
	/**
	 * 流程变量的名称，用于变量赋值，Javascript变量引用等场合。
	 * @return
	 */
	public String getName();
	

	
	/**
	 * 返回变量对应的QName；如果是Java类，则该值是经过JAXB转换后对应的QName
	 * @return
	 */
	public QName getDataType();
	

	
	/**
	 * 流程变量的作用域
	 * @return
	 */
	public String getScopeId();
	
	/**
	 * 变量对应的流程元素的Id，从scope对象获取
	 * @return
	 */
	public String getProcessElementId();
	
	public String getProcessId();
	public Integer getVersion();
	public String getProcessType();
	
	//////////////////////////////////////////////////////////////
	/////////////  TODO 下面的属性待考虑       ////////////////////////
	/////////////////////////////////////////////////////////////
	
	/**
	 * 如果变量是若干Schema文件组成的复杂schema，则该字段存储主文件名称。<br>
	 * 
	 * @return
	 */
//	public String getMainSchemaFileName();

	
	/**
	 * 返回xml格式的数据对象
	 * @return
	 */
//	public String getValueAsString();	
	
	/**
	 * 返回类型对应的Java Class
	 * @return
	 */
//	public String getJavaClassName();
	
	/**
	 * schema文件名和schema组成的Map
	 * @return
	 */
//	public Map<String,String> getSchemas();
}
