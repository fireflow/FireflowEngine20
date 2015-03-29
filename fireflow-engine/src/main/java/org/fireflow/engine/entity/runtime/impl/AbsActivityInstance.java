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
import org.fireflow.client.impl.WorkflowStatementLocalImpl;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.server.support.DateTimeXmlAdapter;


/**
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absActivityInstanceType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ActivityInstanceImpl.class,ActivityInstanceHistory.class})
public abstract class AbsActivityInstance extends AbsWorkflowEntity implements ActivityInstance {
	@XmlElement(name="procInstCreatorId")
//	@Column("PROCINST_CREATOR_ID")
	protected String procInstCreatorId = null;
	
	@XmlElement(name="procInstCreatorName")
//	@Column("PROCINST_CREATOR_NAME")
	protected String procInstCreatorName = null;
	
	@XmlElement(name="procInstCreatedTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("PROCINST_CREATED_TIME")
	protected Date procInstCreatedTime = null;
	
	@XmlElement(name="name")
//	@Column("NAME")
	protected String name = null;
	
	@XmlElement(name="displayName")
//	@Column("DISPLAY_NAME")
	protected String displayName = null;
	
	@XmlElement(name="nodeId")
//	@Column("NODE_ID")
	protected String nodeId = null;
    
	@XmlElement(name="processId")
//	@Column("PROCESS_ID")
	protected String processId = null;
	
	@XmlElement(name="version")
//	@Column("VERSION")
	protected Integer version = null;
	
	@XmlElement(name="processType")
//	@Column("PROCESS_TYPE")
	protected String processType = null;   
	
	@XmlElement(name="subProcessId")
//	@Column("SUBPROCESS_ID")
	protected String subProcessId = null;
	
	@XmlElement(name="processName")
//	@Column("PROCESS_NAME")
	protected String processName = null;
	
	@XmlElement(name="processDisplayName")
//	@Column("PROCESS_DISPLAY_NAME")
	protected String processDisplayName = null;
	
	@XmlElement(name="subProcessName")
//	@Column("SUBPROCESS_NAME")
	protected String subProcessName = null;
	
	@XmlElement(name="subProcessDisplayName")
//	@Column("SUBPROCESS_DISPLAY_NAME")
	protected String subProcessDisplayName= null;
	
	
	@XmlElement(name="bizType")
//	@Column("BIZ_TYPE")
	protected String bizType = null;
	
	@XmlElement(name="serviceId")
//	@Column("SERVICE_ID")
	protected String serviceId = null;
	
	@XmlElement(name="serviceVersion")
//	@Column("SERVICE_VERSION")
	protected String serviceVersion = null;
	
	@XmlElement(name="serviceType")
//	@Column("SERVICE_TYPE")
	protected String serviceType = null;
    
	@XmlElement(name="bizId")
//	@Column("BIZ_ID")
	protected String bizId = null;
	
	@XmlElement(name="subBizId")
//	@Column("SUB_BIZ_ID")
	protected String subBizId = null;

	@XmlElement(name="state")
//	@Column(value="STATE",adaptor=ActivityInstanceStateValueAdaptor.class)
	protected ActivityInstanceState state = ActivityInstanceState.INITIALIZED;
	
	@XmlElement(name="suspended")
//	@Column("SUSPENDED")
	protected Boolean suspended = Boolean.FALSE;
	
	@XmlElement(name="createdTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("CREATED_TIME")
	protected Date createdTime = null;
	
	@XmlElement(name="startedTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("STARTED_TIME")
	protected Date startedTime = null;
	
	@XmlElement(name="expiredTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("EXPIRED_TIME")
	protected Date expiredTime = null;
	
	@XmlElement(name="endTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("END_TIME")
	protected Date endTime = null;

	@XmlElement(name="processInstanceId")
//	@Column("PROCESSINSTANCE_ID")
	protected String processInstanceId = null;
	
	@XmlElement(name="parentScopeId")
//	@Column("PARENT_SCOPE_ID")
	protected String parentScopeId = null;
	
	@XmlElement(name="tokenId")
//	@Column("TOKEN_ID")
	protected String tokenId = null;
	
	@XmlElement(name="stepNumber")
//	@Column("STEP_NUMBER")
	protected Integer stepNumber = null;    

	@XmlElement(name="targetActivityId")
//	@Column("TARGET_ACTIVITY_ID")
	protected String targetActivityId = null;
	
	@XmlElement(name="fromActivityId")
//	@Column("FROM_ACTIVITY_ID")
	protected String fromActivityId = null;
	
	@XmlElement(name="canBeWithdrawn")
//	@Column("CAN_BE_WITHDRAWN")
	protected Boolean canBeWithdrawn = true;

	@XmlElement(name="note")
//	@Column("NOTE")
	protected String note = null;


//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getActivity(org.fireflow.engine.WorkflowSession)
//	 */
//	@Override
//	public Object getActivity(WorkflowSession session) throws EngineException {
//
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getActivityId()
	 */
	public String getNodeId() {
		
		return this.nodeId;
	}
	
	public void setNodeId(String nodeid){
		this.nodeId = nodeid;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getBizId()
	 */
	public String getBizId() {
		return this.bizId;
	}
	
	public void setBizId(String bizId){
		this.bizId = bizId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getCreatedTime()
	 */
	public Date getCreatedTime() {
		return this.createdTime;
	}
	
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getDisplayName()
	 */
	public String getDisplayName() {
		
		return this.displayName;
	}
	
	public void setDisplayName(String dispName){
		this.displayName = dispName;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getEndTime()
	 */
	public Date getEndTime() {
		
		return this.endTime;
	}
	
	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getExpiredTime()
	 */
	public Date getExpiredTime() {
		
		return this.expiredTime;
	}
	
	public void setExpiredTime(Date expiredTime){
		this.expiredTime = expiredTime;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getName()
	 */
	public String getName() {
		
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getNote()
	 */
	public String getNote() {
		
		return this.note;
	}
	
	public void setNote(String note){
		this.note = note;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getProcessId()
	 */
	public String getProcessId() {
		
		return this.processId;
	}
	
	public void setProcessId(String processId){
		this.processId = processId;
	}
	
	public String getSubProcessId(){
		return this.subProcessId;
	}
	
	public void setSubProcessId(String subflowId){
		this.subProcessId = subflowId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getProcessInstance(org.fireflow.engine.WorkflowSession)
	 */
	public ProcessInstance getProcessInstance(WorkflowSession session) {
		WorkflowStatementLocalImpl statement = (WorkflowStatementLocalImpl)session.createWorkflowStatement(this.getProcessType());
		return statement.getEntity(this.getProcessInstanceId(), ProcessInstance.class);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getProcessInstanceId()
	 */
	public String getProcessInstanceId() {
		
		return this.processInstanceId;
	}
	
	public void setProcessInstanceId(String processInstanceId){
		this.processInstanceId = processInstanceId;
	}
	
	public String getProcessType(){
		return this.processType;
	}
	
	public void setProcessType(String processType){
		this.processType = processType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getServiceId()
	 */
	public String getServiceId() {
		
		return this.serviceId;
	}

	public void setServiceId(String serviceId){
		this.serviceId = serviceId;
	}
	
	
	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getServiceType()
	 */
	public String getServiceType() {
		
		return this.serviceType;
	}
	
	public void setServiceType(String serviceType){
		this.serviceType = serviceType;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getStartedTime()
	 */
	public Date getStartedTime() {
		return this.startedTime;
	}
	
	public void setStartedTime(Date startedTime){
		this.startedTime = startedTime;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getState()
	 */
	public ActivityInstanceState getState() {
		return this.state;
	}
	
	public void setState(ActivityInstanceState state){
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getStepNumber()
	 */
	public Integer getStepNumber() {
		return this.stepNumber;
	}
	
	public void setStepNumber(Integer i){
		this.stepNumber = i;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getSubBizId()
	 */
	public String getSubBizId() {
		return this.subBizId;
	}
	
	public void setSubBizId(String s){
		this.subBizId = s;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getTargetActivityId()
	 */
	public String getTargetActivityId() {
		return this.targetActivityId;
	}
	
	public void setTargetActivityId(String s){
		this.targetActivityId = s;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getVersion()
	 */
	public Integer getVersion() {
		
		return this.version;
	}
	
	public void setVersion(Integer v){
		this.version = v;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#getWorkflowProcess(org.fireflow.engine.WorkflowSession)
	 */
	public Object getWorkflowProcess(WorkflowSession session)
	throws InvalidModelException{
		WorkflowStatement stmt = session.createWorkflowStatement(this.getProcessType());
		ProcessKey pk = new ProcessKey(this.processId,this.version,this.processType);
		return stmt.getWorkflowProcess(pk);
	}
	


	/* (non-Javadoc)
	 * @see org.fireflow.engine.entity.runtime.ActivityInstance#isSuspended()
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
		return this.nodeId;
	}
	
	public String getParentScopeId(){
		return this.parentScopeId;
	}
	
	public void setParentScopeId(String pscopeId){
		this.parentScopeId = pscopeId;
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
	
	public void setTokenId(String tokenId){
		this.tokenId = tokenId;
	}
	
	public String getTokenId(){
		return this.tokenId;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the processDisplayName
	 */
	public String getProcessDisplayName() {
		return processDisplayName;
	}

	/**
	 * @param processDisplayName the processDisplayName to set
	 */
	public void setProcessDisplayName(String processDisplayName) {
		this.processDisplayName = processDisplayName;
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

	/**
	 * @return the canBeWithdrawn
	 */
	public Boolean getCanBeWithdrawn() {
		return canBeWithdrawn;
	}

	/**
	 * @param canBeWithdrawn the canBeWithdrawn to set
	 */
	public void setCanBeWithdrawn(Boolean canBeWithdrawn) {
		this.canBeWithdrawn = canBeWithdrawn;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizCategory) {
		this.bizType = bizCategory;
	}

	public String getProcInstCreatorId() {
		return procInstCreatorId;
	}

	public void setProcInstCreatorId(String creatorId) {
		this.procInstCreatorId = creatorId;
	}

	public String getProcInstCreatorName() {
		return procInstCreatorName;
	}

	public void setProcInstCreatorName(String creatorName) {
		this.procInstCreatorName = creatorName;
	}

	public Date getProcInstCreatedTime() {
		return procInstCreatedTime;
	}

	public void setProcInstCreatedTime(Date processInstanceCreatedTime) {
		this.procInstCreatedTime = processInstanceCreatedTime;

	}
	
	
}
