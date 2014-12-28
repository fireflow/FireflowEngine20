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
package org.fireflow.server.support;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.TokenState;
import org.firesoa.common.schema.NameSpaces;
import org.firesoa.common.util.JavaDataTypeConvertor;

/**
 * 将基本Java基本类型转换为Xml输出
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="objectValue")
@XmlType(name="objectValueType")
public class ObjectWrapper {
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	private Object originalValue = null;
	
	
	private String value = null;
	
	
	private String dataPattern = null;
	

	private QName dataType = null;

	@XmlValue
	public String getValue() {
		if (value==null && originalValue!=null){
			if (JavaDataTypeConvertor.isDate(originalValue.getClass().getName())){
				SimpleDateFormat format = new SimpleDateFormat(dataPattern);
				value = format.format((Date)originalValue);
			}
			else {
				value = originalValue.toString();
			}
		}
		return value;
	}
	
	public void setValue(String v){
		value = v;
		
		originalValue = null;//将originalValue重置，便于重新计算
	}
	
	
	@XmlTransient
	public Object getOriginalValue(){
		if (originalValue==null && value!=null){
			//计算OriginalValue
			try {
				if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&						
						JavaDataTypeConvertor.isPrimaryDataType(dataType.getLocalPart())){
					originalValue = JavaDataTypeConvertor.convertToJavaObject(dataType, value, dataPattern);

				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						ActivityInstanceState.class.getName().equals(dataType.getLocalPart())){
					originalValue = ActivityInstanceState.valueOf(value);
				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						WorkItemState.class.getName().equals(dataType.getLocalPart())){
					originalValue = WorkItemState.valueOf(value);
				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						ProcessInstanceState.class.getName().equals(dataType.getLocalPart())){
					originalValue = ProcessInstanceState.valueOf(value);
				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						ScheduleJobState.class.getName().equals(dataType.getLocalPart())){
					originalValue = ScheduleJobState.valueOf(value);
				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						TokenState.class.getName().equals(dataType.getLocalPart())){
					originalValue = TokenState.valueOf(value);
				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						OperationContextName.class.getName().equals(dataType.getLocalPart())){
					originalValue = OperationContextName.valueOf(value);
				}
				else if (NameSpaces.JAVA.getUri().equals(dataType.getNamespaceURI())&&
						WorkItemAssignmentStrategy.class.getName().equals(dataType.getLocalPart())){
					originalValue = WorkItemAssignmentStrategy.valueOf(value);
				}
				else{
					//TODO 需要用XStream转换回java对象?
					originalValue = value.toString();
				}
			
				
			} catch (ClassCastException e) {
				throw new RuntimeException(e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		//System.out.println("value=="+value+";originalValue=="+originalValue);
		return originalValue;
	}

	public void setOriginalValue(Object arg) {
		this.originalValue = arg;
		if (originalValue!=null){
			//TODO 此处应该转换为XSI的基本类型，暂时用Java类型代替
			dataType = new QName(NameSpaces.JAVA.getUri(),originalValue.getClass().getName());
			
			
			if (JavaDataTypeConvertor.isDate(originalValue.getClass().getName())){
				dataPattern = DEFAULT_DATE_PATTERN;
				
				value = null;//value设置为空，在第一次调用getValue()时进行计算
			}else{
				//TODO 需要用XStream转换成xml
				value = arg.toString();
			}
		}else{
			value = "";
			dataType = new QName(NameSpaces.JAVA.getUri(),String.class.getName());
		}
		
	}

	@XmlAttribute(name="data-type")
	//@XmlJavaTypeAdapter(QNameXmlAdapter.class)//QName有缺省的映射机制
	public QName getDataType(){
		return dataType;
	}
	
	public void setDataType(QName type){
		this.dataType = type;
	}
	
	@XmlAttribute(name="data-pattern")
	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String datePattern) {
		this.dataPattern = datePattern;
	}
}
