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
package org.fireflow.engine.modules.formsystem.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.fireflow.engine.modules.formsystem.UrlParameter;
import org.fireflow.server.support.QNameXmlAdapter;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="urlParameter")
@XmlType(name="urlParameterType")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlParameterImpl implements UrlParameter {
	@XmlElement(name="name")
	private String name = null;
	
	@XmlElement(name="initialValueAsString")
	private String initialValueAsString = null;
	
	@XmlElement(name="dataType")
	//@XmlJavaTypeAdapter(QNameXmlAdapter.class)//QName有缺省的映射机制
	private QName dataType = null;

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.UrlParameter#setName(java.lang.String)
	 */
	public void setName(String nm) {
		name = nm;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.UrlParameter#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.UrlParameter#setDataType(javax.xml.namespace.QName)
	 */
	public void setDataType(QName dt) {
		dataType = dt;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.UrlParameter#getDataType()
	 */
	public QName getDataType() {
		return dataType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.UrlParameter#getInitialValueAsString()
	 */
	public String getInitialValueAsString() {
		return initialValueAsString;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.formsystem.UrlParameter#setInitialValueAsString(java.lang.String)
	 */
	public void setInitialValueAsString(String initialValue) {
		initialValueAsString = initialValue;

	}

}
