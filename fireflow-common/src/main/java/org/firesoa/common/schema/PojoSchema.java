/**
 * Copyright 2007-2011 非也
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
package org.firesoa.common.schema;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * 该类表示一个Pojo class和它对应的XML schema信息，该schema是通过JAXB生成的。
 * 
 * @author 非也 www.firesoa.com
 * 
 *
 */
public class PojoSchema {
	private Class pojoClass = null;
	private QName qname = null;
	private Map<String,String> schemas = null;
	private String mainSchemaFileName = null;
	
	public String getSchema(){
		return schemas.get(mainSchemaFileName);
	}
	
	public String getMainSchemaFileName() {
		return mainSchemaFileName;
	}

	public void setMainSchemaFileName(String mainSchemaFileName) {
		this.mainSchemaFileName = mainSchemaFileName;
	}




	public Class getPojoClass() {
		return pojoClass;
	}
	public void setPojoClass(Class pojoClass) {
		this.pojoClass = pojoClass;
	}
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public Map<String,String> getAllSchemas() {
		return schemas;
	}
	public void setAllSchemas(Map<String,String> schemas) {
		this.schemas = schemas;
	}
}
