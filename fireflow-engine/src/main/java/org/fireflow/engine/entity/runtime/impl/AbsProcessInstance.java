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

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.WorkflowStatement;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.nutz.ProcessInstanceStateValueAdaptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.server.support.DateTimeXmlAdapter;
import org.nutz.dao.entity.annotation.Column;

/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absProcessInstanceType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ProcessInstanceImpl.class,ProcessInstanceHistory.class})
public abstract class AbsProcessInstance extends AbsWorkflowEntity implements ProcessInstance {
	@XmlElement(name="bizId")
	@Column("BIZ_ID")
	protected String bizId = null;

	@XmlElement(name="processId")
	@Column("PROCESS_ID")
    protected String processId = null;
	
	@XmlElement(name="version")
	@Column("VERSION")
    protected Integer version = null;
	
	@XmlElement(name="processType")
	@Column("PROCESS_TYPE")
    protected String processType = null;
	
	@XmlElement(name="subProcessId")
	@Column("SUBPROCESS_ID")
    protected String subProcessId = null;
    
	@XmlElement(name="processName")
	@Column("PROCESS_NAME")
    protected String processName = null;
	
	@XmlElement(name="processDisplayName")
	@Column("PROCESS_DISPLAY_NAME")
    protected String processDisplayName = null;
	
	@XmlElement(name="bizType")
	@Column("BIZ_TYPE")
    protected String bizType = null;
	
	@XmlElement(name="packageId")
	@Column("PACKAGE_ID")
	protected String packageId = null;
    
	@XmlElement(name="subProcessName")
	@Column("SUBPROCESS_NAME")
    protected String subProcessName = null;
	
	@XmlElement(name="subProcessDisplayName")
	@Column("SUBPROCESS_DISPLAY_NAME")
    protected String subProcessDisplayName = null;
    
	@XmlElement(name="state")
	@Column(value="STATE",adaptor=ProcessInstanceStateValueAdaptor.class)
    protected ProcessInstanceState state = null;
	
	@XmlElement(name="suspended")
	@Column("SUSPENDED")
    protected Boolean suspended = Boolean.FALSE;
    
	@XmlElement(name="creatorId")
	@Column("CREATOR_ID")
    protected String creatorId = null;
	
	@XmlElement(name="creatorName")
	@Column("CREATOR_NAME")
    protected String creatorName = null;
	
	@XmlElement(name="creatorOrgId")
	@Column("CREATOR_ORG_ID")
    protected String creatorOrgId = null;
	
	@XmlElement(name="creatorOrgName")
	@Column("CREATOR_ORG_NAME")
    protected String creatorOrgName = null;
    
	@XmlElement(name="createdTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("CREATED_TIME")
    protected Date createdTime = null;
	
	@XmlElement(name="startedTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("STARTED_TIME")
    protected Date startedTime = null;
	
	@XmlElement(name="endTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("END_TIME")
    protected Date endTime = null;
	
	@XmlElement(name="expiredTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("EXPIRED_TIME")
    protected Date expiredTime = null;
    
	@XmlElement(name="parentProcessInstanceId")
	@Column("PARENT_PROCESS_INSTANCE_ID")
    protected String parentProcessInstanceId = null;
	
	@XmlElement(name="parentActivityInstanceId")
	@Column("PARENT_ACTIVITY_INSTANCE_ID")
    protected String parentActivityInstanceId = null;
	
	@XmlElement(name="parentScopeId")
	@Column("PARENT_SCOPE_ID")
    protected String parentScopeId = null;
    
	@XmlElement(name="tokenId")
	@Column("TOKEN_ID")
    protected String tokenId = null;
    
	@XmlElement(name="note")
	@Column("NOTE")
    protected String note;
    

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#bizId()
	 */
	public String getBizId() {
		
		return this.bizId;
	}
	
	public void setBizId(String bizId){
		this.bizId = bizId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatedTime()
	 */
	public Date getCreatedTime() {
		return this.createdTime;
	}
	
	public void setCreatedTime(Date time){
		this.createdTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorId()
	 */
	public String getCreatorId() {
		return this.creatorId;
	}
	
	public void setCreatorId(String uid){
		this.creatorId = uid;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorName()
	 */
	public String getCreatorName() {
		return this.creatorName;
	}
	
	public void setCreatorName(String creatorName){
		this.creatorName = creatorName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorOrgId()
	 */
	public String getCreatorDeptId() {
		return this.creatorOrgId;
	}
	
	public void setCreatorDeptId(String orgId){
		this.creatorOrgId = orgId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getCreatorOrgName()
	 */
	public String getCreatorDeptName() {
		return this.creatorOrgName;
	}
	
	public void setCreatorDeptName(String orgName){
		this.creatorOrgName = orgName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getDisplayName()
	 */
	public String getProcessDisplayName() {
		return this.processDisplayName;
	}
	
	public void setProcessDisplayName(String displayName){
		this.processDisplayName = displayName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getEndTime()
	 */
	public Date getEndTime() {
		return this.endTime;
	}
	
	public void setEndTime(Date time){
		this.endTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getExpiredTime()
	 */
	public Date getExpiredTime() {
		return this.expiredTime;
	}
	
	public void setExpiredTime(Date time){
		this.expiredTime = time;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getName()
	 */
	public String getProcessName() {
		return this.processName;
	}
	
	public void setProcessName(String name){
		this.processName = name;
	}

	public String getSubProcessName(){
		return this.subProcessName;
	}
	
	public void setSubProcessName(String subflowName){
		this.subProcessName = subflowName;
	}
	
	public String getSubProcessDisplayName(){
		return this.subProcessDisplayName;
	}
	
	public void setSubProcessDisplayName(String subflowDisplayName){
		this.subProcessDisplayName = subflowDisplayName;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getNote()
	 */
	public String getNote() {
		return this.note;
	}
	
	public void setNote(String note){
		this.note = note;
	}
	

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getParentActivityInstanceId()
	 */
	public String getParentActivityInstanceId() {
		
		return this.parentActivityInstanceId;
	}
	
	public void setParentActivityInstanceId(String pActInstId){
		this.parentActivityInstanceId = pActInstId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getParentProcessInstanceId()
	 */
	public String getParentProcessInstanceId() {

		return this.parentProcessInstanceId;
	}
	
	public void setParentProcessInstanceId(String pProcInstId){
		this.parentProcessInstanceId = pProcInstId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getProcessId()
	 */
	public String getProcessId() {
		return this.processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getProcessType()
	 */
	public String getProcessType() {
		return this.processType;
	}

	public void setProcessType(String processType){
		this.processType = processType;
	}
	
    public String getSubProcessId(){
    	return this.subProcessId;
    }
    
    public void setSubProcessId(String subflowId){
    	this.subProcessId = subflowId;
    }
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getStartedTime()
	 */
	public Date getStartedTime() {
		return this.startedTime;
	}
	
	public void setStartedTime(Date time){
		this.startedTime = time;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getState()
	 */
	public ProcessInstanceState getState() {
		return this.state;
	}
	
	public void setState(ProcessInstanceState state){
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getVersion()
	 */
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer v){
		this.version = v;
	}



	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizCategory) {
		this.bizType = bizCategory;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#isSuspended()
	 */
	public Boolean isSuspended() {
		
		return this.suspended;
	}
	
	public void setSuspended(Boolean b){
		this.suspended = b;
	}

	public String getScopeId(){
		return this.id;
	}
	
	public String getProcessElementId(){
		return this.subProcessId;
	}
	
	public String getParentScopeId(){
		return this.parentScopeId;
	}
	
	public void setParentScopeId(String pscopeId){
		this.parentScopeId = pscopeId;
	}
	
	

	/**
	 * @return the tokenId
	 */
	public String getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId the tokenId to set
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public Object getVariableValue(WorkflowSession session,String name){
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		return stmt.getVariableValue(this, name);
	}
	public void setVariableValue(WorkflowSession session ,String name ,Object value)throws InvalidOperationException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		stmt.setVariableValue(this, name,value);
	}
	public void setVariableValue(WorkflowSession session ,String name ,Object value,Properties headers)throws InvalidOperationException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		stmt.setVariableValue(this, name,value,headers);
	}
	public Map<String,Object> getVariableValues(WorkflowSession session){
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		return stmt.getVariableValues(this);
	}	
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ProcessInstance#getWorkflowProcess(org.fireflow.engine.WorkflowSession)
	 */
	public Object getWorkflowProcess(WorkflowSession session)
	throws InvalidModelException {
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		ProcessKey pk = new ProcessKey(this.processId,this.version,this.processType);
		return stmt.getWorkflowProcess(pk);
	}

	/**
	 * @return the packageId
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	
	
}
