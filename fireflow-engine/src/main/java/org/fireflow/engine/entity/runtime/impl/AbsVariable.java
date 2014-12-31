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
package org.fireflow.engine.entity.runtime.impl;

import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.server.support.ObjectXmlAdapter;
import org.fireflow.server.support.PropertiesXmlAdapter;

/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absVariableType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({VariableImpl.class,VariableHistory.class})
public abstract class AbsVariable extends AbsWorkflowEntity implements Variable {
	@XmlElement(name="scopeId")
//	@Column("SCOPE_ID")
	protected String scopeId  = null;
	
	@XmlElement(name="name")
//	@Column("NAME")
	protected String name = null;
	
	@XmlElement(name="headers")
//	@Column(value="HEADERS",adaptor=VariableHeaderValueAdaptor.class)
	@XmlJavaTypeAdapter(PropertiesXmlAdapter.class)
	protected Properties headers = new Properties();
	
	@XmlElement(name="dataType")
//	@XmlJavaTypeAdapter(QNameXmlAdapter.class)//QName有缺省的映射，此处不需要
//	@Column(value="DATA_TYPE",adaptor=QNameValueAdaptor.class)
	protected QName dataType = null;
//	String javaClassName = null;

//	String valueAsString = null;
	
//	String mainSchemaFileName = null;
	
//	Map<String,String> schemas = null;
	@XmlElement(name="payload")
	@XmlJavaTypeAdapter(ObjectXmlAdapter.class)
//	@Column(value="PAYLOAD",adaptor=VariablePayloadValueAdaptor.class)
	protected Object payload = null;
	
	@XmlElement(name="processElementId")
//	@Column("PROCESS_ELEMENT_ID")
	protected String processElementId = null;
	
	@XmlElement(name="processId")
//	@Column("PROCESS_ID")
	protected String processId = null;
	
	@XmlElement(name="version")
//	@Column("VERSION")
	protected Integer version = null;
	
	@XmlElement(name="processType")
//	@Column("PROCESS_TYPE")
	protected String processType = null;
	
	public String getScopeId() {
		return scopeId;
	}
	public void setScopeId(String scopeId) {
		this.scopeId = scopeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public Properties getHeaders() {
		return headers;
	}
	public void setHeaders(Properties headers) {
		this.headers = headers;
	}
	
	public QName getDataType() {
		return dataType;
	}
	public void setDataType(QName dataType) {
		this.dataType = dataType;
	}
//	public String getValueAsString() {
//		return valueAsString;
//	}
//	public void setValueAsString(String variableValue) {
//		this.valueAsString = variableValue;
//	}
	/**
	 * @return the processId
	 */
	public String getProcessId() {
		return processId;
	}
	/**
	 * @param processId the processId to set
	 */
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}
	/**
	 * @return the processType
	 */
	public String getProcessType() {
		return processType;
	}
	/**
	 * @param processType the processType to set
	 */
	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Object getPayload(){
		return payload;
	}
	
	public void setPayload(Object value){
		this.payload = value;
	}
//	public String getMainSchemaFileName() {
//		return mainSchemaFileName;
//	}
//	public void setMainSchemaFileName(String mainSchemaFileName) {
//		this.mainSchemaFileName = mainSchemaFileName;
//	}
//	public Map<String, String> getSchemas() {
//		return schemas;
//	}
//	public void setSchemas(Map<String, String> schemas) {
//		this.schemas = schemas;
//	}
	public String getProcessElementId() {
		return processElementId;
	}
	public void setProcessElementId(String processElementId) {
		this.processElementId = processElementId;
	}
	
	
	
//	public String getJavaClassName() {
//		return javaClassName;
//	}
//	public void setJavaClassName(String javaClassName) {
//		this.javaClassName = javaClassName;
//	}

}
