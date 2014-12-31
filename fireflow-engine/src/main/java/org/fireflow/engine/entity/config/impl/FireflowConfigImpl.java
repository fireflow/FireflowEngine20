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
package org.fireflow.engine.entity.config.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.config.FireflowConfig;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="fireflowConfig")
@XmlType(name="fireflowConfigType")
@XmlAccessorType(XmlAccessType.FIELD)

//@Table("T_FF_CFG_FIREFLOW_CONFIG")//nutz标注，已解耦
//@TableIndexes({@Index(name="IDX_CONFIG_ID",fields={"CONFIG_ID"}),
//				@Index(name="IDX_CATEGORY_ID",fields={"CATEGORY_ID"},unique=false)})
public class FireflowConfigImpl extends AbsWorkflowEntity implements FireflowConfig{
	@XmlElement(name="configId")
//	@Column("CONFIG_ID")
	private String configId;
	
	@XmlElement(name="configName")
//	@Column("CONFIG_NAME")
	private String configName;
	
	@XmlElement(name="configValue")
//	@Column("CONFIG_VALUE")
	private String configValue;
	
	@XmlElement(name="description")
//	@Column("DESCRIPTION")
	private String description;
	
	@XmlElement(name="categoryId")
//	@Column("CATEGORY_ID")
	private String categoryId;
	
	@XmlElement(name="parentConfigId")
//	@Column("PARENT_CONFIG_ID")
	private String parentConfigId;
	
	@XmlElement(name="lastEditor")
//	@Column("LAST_EDITOR")
	private String lastEditor;
	
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getParentConfigId() {
		return parentConfigId;
	}
	public void setParentConfigId(String parentConfigId) {
		this.parentConfigId = parentConfigId;
	}

	public String getLastEditor() {
		return lastEditor;
	}
	public void setLastEditor(String lastEditor) {
		this.lastEditor = lastEditor;
	}
	
	
}
