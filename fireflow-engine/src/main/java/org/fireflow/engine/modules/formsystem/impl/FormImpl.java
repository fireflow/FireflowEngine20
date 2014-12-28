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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.modules.formsystem.Form;
import org.fireflow.engine.modules.formsystem.UrlParameter;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="form")
@XmlType(name="formType")
@XmlAccessorType(XmlAccessType.FIELD)
public class FormImpl implements Form{
	
	@XmlElement(name="categoryId")
	private String categoryId = null;
	
	@XmlElement(name="formId")
	private String formId = null;
	
	@XmlElement(name="displayName")
	private String displayName = null;
	
	@XmlElement(name="urlString")
	private String urlString = null;
	
	@XmlElementWrapper(name="urlParameters")
	@XmlElementRefs({
		@XmlElementRef(type=UrlParameterImpl.class)
	})
	private List<UrlParameter> urlParameters = new ArrayList<UrlParameter>();

	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the urlString
	 */
	public String getUrlString() {
		return urlString;
	}

	/**
	 * @param urlString the urlString to set
	 */
	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	/**
	 * @return the urlParameters
	 */
	public List<UrlParameter> getUrlParameters() {
		return urlParameters;
	}

	/**
	 * @param urlParameters the urlParameters to set
	 */
	public void setUrlParameters(List<UrlParameter> urlParameters) {
		this.urlParameters = urlParameters;
	}
	
	public void addUrlParameter(UrlParameter param){
		if (urlParameters==null){
			urlParameters = new ArrayList<UrlParameter>();
		}
		urlParameters.add(param);
	}
}
