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
package org.fireflow.engine.entity.repository.impl;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.RepositoryDescriptor;
import org.fireflow.server.support.DateTimeXmlAdapter;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@XmlRootElement(name="absRepositoryDescriptor")
@XmlType(name="absRepositoryDescriptorType",
		propOrder={"name","displayName","description",
		"publishState","validDateFrom","validDateTo","ownerId","ownerName",
		"approver","approvedTime","lastEditor","updateLog"})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbsRepositoryDescriptorImpl extends AbsWorkflowEntity implements
		RepositoryDescriptor {
	@XmlElement(name="name")
	protected String name; //流程英文名称
	
	@XmlElement(name="displayName")
    protected String displayName;//流程显示名称
	
	@XmlElement(name="description")
    protected String description;//流程业务说明

    @XmlElement(name="publishState")
    protected Boolean publishState;//是否发布，1=已经发布,0未发布

    @XmlElement(name="validDateFrom")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    protected Date validDateFrom;
    
    @XmlElement(name="validDateTo")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    protected Date validDateTo;
    
    @XmlElement(name="ownerName")
    protected String ownerName = null;//流程所有者名称
    
    @XmlElement(name="ownerId")
    protected String ownerId = null;//流程所有者Id    
    
    @XmlElement(name="approver")
    protected String approver = null;//批准发布人
    
    @XmlElement(name="approvedTime")
    @XmlJavaTypeAdapter(value=DateTimeXmlAdapter.class)
    protected Date approvedTime = null;//批准发布时间
    
    @XmlElement(name="lastEditor")
    protected String lastEditor = null;//最后编辑流程的操作者姓名
    
    @XmlElement(name="updateLog")
    protected String updateLog = null;//发布日志
    
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	public Date getApprovedTime() {
		
		return this.approvedTime;
	}
	
	public void setApprovedTime(Date time){
		this.approvedTime = time;
	}


	public String getApprover() {
		return this.approver;
	}
	
	public void setApprover(String approver){
		this.approver = approver;
	}


	public String getLastEditor() {
		return this.lastEditor;
	}
	
	public void setLastEditor(String editorName){
		this.lastEditor = editorName;
	}

	
	public String getOwnerId() {
		return this.ownerId;
	}
	
	public void setOwnerId(String orgId){
		this.ownerId = orgId;
	}

	public String getOwnerName() {
		return this.ownerName;
	}
	
	public void setOwnerName(String orgName){
		this.ownerName = orgName;
	}

//	/**
//	 * @return the fileName
//	 */
//	public String getFileName() {
//		return fileName;
//	}
//
//	/**
//	 * @param fileName the fileName to set
//	 */
//	public void setFileName(String fileName) {
//		this.fileName = fileName;
//	}
    

	public Boolean getPublishState() {
		return publishState;
	}

	public void setPublishState(Boolean state){
		this.publishState = state;
	}

	/**
	 * @return the validDateFrom
	 */
	public Date getValidDateFrom() {
		return validDateFrom;
	}

	/**
	 * @param validDateFrom the validDateFrom to set
	 */
	public void setValidDateFrom(Date validDateFrom) {
		this.validDateFrom = validDateFrom;
	}

	/**
	 * @return the validDateTo
	 */
	public Date getValidDateTo() {
		return validDateTo;
	}

	/**
	 * @param validDateTo the validDateTo to set
	 */
	public void setValidDateTo(Date validDateTo) {
		this.validDateTo = validDateTo;
	}

	/**
	 * @return the updateLog
	 */
	public String getUpdateLog() {
		return updateLog;
	}

	/**
	 * @param updateLog the updateLog to set
	 */
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	
	
}
