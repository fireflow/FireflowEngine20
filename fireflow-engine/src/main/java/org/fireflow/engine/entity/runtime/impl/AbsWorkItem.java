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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.runtime.RemoteWorkItem;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.fireflow.server.support.DateTimeXmlAdapter;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absWorkItemType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({LocalWorkItemImpl.class,RemoteWorkItemImpl.class,WorkItemHistory.class})
public abstract class AbsWorkItem extends AbsWorkflowEntity implements WorkItem{
	/**
	 * work item 类型，LOCAL表示本地WorkItem，REMOTE表示远程workitem，NOT_FF表示非FF workitem；，如果为远程workitem则需要应用到workflowEngineLocation
	 */
	@XmlElement(name="workItemType")
//	@Column("WORKITEM_TYPE")
//	@ColDefine(width=256)
	protected String workItemType = WORKITEM_TYPE_LOCAL;//是否为远程workitem
	
	/**
	 * 等于activityInstance.displayName
	 */
	@XmlElement(name="workItemName")
//	@Column("WORKITEM_NAME")
	protected String workItemName = null;
	
	/**
	 * 工作项摘要
	 */
	@XmlElement(name="subject")
//	@Column("SUBJECT")
	protected String subject = null;//
	
	@XmlElement(name="state")
//	@Column(value="STATE",adaptor=WorkItemStateValueAdaptor.class)
	protected WorkItemState state = WorkItemState.INITIALIZED;
	
	/**
	 * 创建时间
	 */
	@XmlElement(name="createdTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("CREATED_TIME")
	protected Date createdTime;
	

    /**
     * 签收时间
     */
	@XmlElement(name="claimedTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("CLAIMED_TIME")
	protected Date claimedTime;
	
	@XmlElement(name="endTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("END_TIME")
	protected Date endTime;
	
	/**
	 * 到期时间，等于ActivityInstance.expiredTime
	 */
	@XmlElement(name="expiredTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
//	@Column("EXPIRED_TIME")
	protected Date expiredTime;
	
	@XmlElement(name="ownerId")
//	@Column("OWNER_ID")
	protected String ownerId;
	
	@XmlElement(name="ownerName")
//	@Column("OWNER_NAME")
	protected String ownerName;
	
	@XmlElement(name="ownerDeptId")
//	@Column("OWNER_DETP_ID")
	protected String ownerDeptId;
	
	@XmlElement(name="ownerDeptName")
//	@Column("OWNER_DETP_NAME")
	protected String ownerDeptName;
	
	/**
	 * 表单Url
	 */
	@XmlElement(name="actionUrl")
//	@Column("ACTION_URL")
	protected String actionUrl;
	
	@XmlElement(name="mobileActionUrl")
//	@Column("MOBILE_ACTION_URL")
	protected String mobileActionUrl;
	
	/**
	 * 产生该工作项的业务系统名称
	 */
	@XmlElement(name="originalSystemName")
//	@Column("ORIGINAL_SYSTEM_NAME")
	protected String originalSystemName = null;
	
	@XmlElement(name="bizId")
//	@Column("BIZ_ID")
	protected String bizId = null;
	
	@XmlElement(name="note")
	protected String note;
	
	/**
	 * 工作项在原业务系统的Id
	 */
	@XmlElement(name="remoteWorkItemId")
//	@Column("REMOTE_WORKITEM_ID")
	protected String remoteWorkItemId = null;

	@XmlElement(name="ownerType")
//	@Column("OWNER_TYPE")
	protected String ownerType;

	@XmlElement(name="assignmentStrategy")
//	@Column(value="ASSIGNMENT_STRATEGY",adaptor=AssignmentStrategyValueAdaptor.class)
//	@ColDefine(width=256)
    protected WorkItemAssignmentStrategy assignmentStrategy = WorkItemAssignmentStrategy.ASSIGN_TO_ANY;
	
	@XmlElement(name="responsiblePersonId")
//	@Column("RESPONSIBLE_PERSON_ID")
	protected String responsiblePersonId;
	
	@XmlElement(name="responsiblePersonName")
//	@Column("RESPONSIBLE_PERSON_NAME")
	protected String responsiblePersonName;
	
	@XmlElement(name="responsiblePersonOrgId")
//	@Column("RESPONSIBLE_PERSON_DEPT_ID")
	protected String responsiblePersonOrgId;
	
	@XmlElement(name="responsiblePersonOrgName")
//	@Column("RESPONSIBLE_PERSON_DEPT_NAME")
	protected String responsiblePersonOrgName;	

	
	@XmlElement(name="reassignType")
//	@Column("REASSIGN_TYPE")
	protected String reassignType;
	
	@XmlElement(name="parentWorkItemId")
//	@Column("PARENT_WORKITEM_ID")
	protected String parentWorkItemId = WorkItem.NO_PARENT_WORKITEM;

	@XmlElement(name="attachmentId")
//	@Column("ATTACHMENT_ID")
	protected String attachmentId;
	
	@XmlElement(name="attachmentType")
//	@Column("ATTACHMENT_TYPE")
	protected String attachmentType;
	
	
	@XmlElement(name="activityInstanceId")
//	@Column("ACTIVITY_INSTANCE_ID")
	protected String activityInstanceId = null;
	
	
    //////////////////////////////////////
    ///////////  冗余字段 便于查询 /////////
    /////////////////////////////////////
	/**
	 * 流程创建者的姓名。
	 */
	@XmlElement(name="procInstCreatorName")
//	@Column("PROCINST_CREATOR_NAME")
	protected String procInstCreatorName = null;
	
	@XmlElement(name="procInstCreatorId")
//	@Column("PROCINST_CREATOR_ID")
	protected String procInstCreatorId = null;
	
	@XmlElement(name="procInstCreatedTime")
//	@Column("PROCINST_CREATED_TIME")
	protected Date procInstCreatedTime = null;
	
	@XmlElement(name="processId")
//	@Column("PROCESS_ID")
    protected String processId = null;
	
	@XmlElement(name="version")
//	@Column("VERSION")
    protected int version = 0;
	
	@XmlElement(name="processType")
//	@Column("PROCESS_TYPE")
    protected String processType = null;
	
	@XmlElement(name="subProcessId")
//	@Column("SUBPROCESS_ID")
    protected String subProcessId = null;
	
	@XmlElement(name="processInstanceId")
//	@Column("PROCESS_INSTANCE_ID")
    protected String processInstanceId = null;
	
	@XmlElement(name="activityId")
//	@Column("ACTIVITY_ID")
    protected String activityId = null;
	
	@XmlElement(name="stepNumber")
//	@Column("STEP_NUMBER")
    protected int stepNumber = -1;
    
    

	public Date getProcInstCreatedTime() {
		return procInstCreatedTime;
	}

	public void setProcInstCreatedTime(Date procInstCreatedTime) {
		this.procInstCreatedTime = procInstCreatedTime;
	}

	/**
	 * @return the state
	 */
	public WorkItemState getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(WorkItemState state) {
		this.state = state;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the claimedTime
	 */
	public Date getClaimedTime() {
		return claimedTime;
	}

	/**
	 * @param claimedTime the claimedTime to set
	 */
	public void setClaimedTime(Date claimedTime) {
		this.claimedTime = claimedTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the userId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setOwnerId(String userId) {
		this.ownerId = userId;
	}

	/**
	 * @return the userName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setOwnerName(String userName) {
		this.ownerName = userName;
	}

	/**
	 * @return the userOrgId
	 */
	public String getOwnerDeptId() {
		return ownerDeptId;
	}

	/**
	 * @param userOrgId the userOrgId to set
	 */
	public void setOwnerDeptId(String userOrgId) {
		this.ownerDeptId = userOrgId;
	}

	/**
	 * @return the userOrgName
	 */
	public String getOwnerDeptName() {
		return ownerDeptName;
	}

	/**
	 * @param userOrgName the userOrgName to set
	 */
	public void setOwnerDeptName(String userOrgName) {
		this.ownerDeptName = userOrgName;
	}
	
	public String getOwnerType(){
		return this.ownerType;
	}
	
	public void setOwnerType(String ownerType){
		this.ownerType = ownerType;
	}

	/**
	 * @return the responsiblePersonId
	 */
	public String getResponsiblePersonId() {
		return responsiblePersonId;
	}

	/**
	 * @param responsiblePersonId the responsiblePersonId to set
	 */
	public void setResponsiblePersonId(String responsiblePersonId) {
		this.responsiblePersonId = responsiblePersonId;
	}

	/**
	 * @return the responsiblePersonName
	 */
	public String getResponsiblePersonName() {
		return responsiblePersonName;
	}

	/**
	 * @param responsiblePersonName the responsiblePersonName to set
	 */
	public void setResponsiblePersonName(String responsiblePersonName) {
		this.responsiblePersonName = responsiblePersonName;
	}

	/**
	 * @return the responsiblePersonOrgId
	 */
	public String getResponsiblePersonDeptId() {
		return responsiblePersonOrgId;
	}

	/**
	 * @param responsiblePersonOrgId the responsiblePersonOrgId to set
	 */
	public void setResponsiblePersonDeptId(String responsiblePersonOrgId) {
		this.responsiblePersonOrgId = responsiblePersonOrgId;
	}

	/**
	 * @return the responsiblePersonOrgName
	 */
	public String getResponsiblePersonDeptName() {
		return responsiblePersonOrgName;
	}

	/**
	 * @param responsiblePersonOrgName the responsiblePersonOrgName to set
	 */
	public void setResponsiblePersonDeptName(String responsiblePersonOrgName) {
		this.responsiblePersonOrgName = responsiblePersonOrgName;
	}

	/**
	 * @return the attachmentId
	 */
	public String getAttachmentId() {
		return attachmentId;
	}

	/**
	 * @param attachmentId the attachmentId to set
	 */
	public void setAttachmentId(String commentId) {
		this.attachmentId = commentId;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String commentDetail) {
		this.note = commentDetail;
	}

	/**
	 * @return the parentWorkItemId
	 */
	public String getParentWorkItemId() {
		return parentWorkItemId;
	}

	/**
	 * @param parentWorkItemId the parentWorkItemId to set
	 */
	public void setParentWorkItemId(String parentWorkItemId) {
		this.parentWorkItemId = parentWorkItemId;
	}

	/**
	 * @return the reassignType
	 */
	public String getReassignType() {
		return reassignType;
	}

	/**
	 * @param reassignType the reassignType to set
	 */
	public void setReassignType(String reassignType) {
		this.reassignType = reassignType;
	}


	public String getActionUrl() {
		return actionUrl;
	}

	public void setActionUrl(String url) {
		this.actionUrl = url;
	}

	public String getMobileActionUrl(){
		return this.mobileActionUrl;
	}
	
	public void setMobileActionUrl(String url){
		this.mobileActionUrl = url;
	}

	

	public WorkItemAssignmentStrategy getAssignmentStrategy() {
		
		return this.assignmentStrategy;
	}
	
	public void setAssignmentStrategy(WorkItemAssignmentStrategy assignmentStrategy){
		this.assignmentStrategy = assignmentStrategy;
	}
	
	
	
	
	public String getWorkItemName() {
		return workItemName;
	}

	public void setWorkItemName(String workItemName) {
		this.workItemName = workItemName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String description) {
		this.subject = description;
	}

	public String getOriginalSystemName() {
		return originalSystemName;
	}

	public void setOriginalSystemName(String originalSystemName) {
		this.originalSystemName = originalSystemName;
	}

	public String getProcInstCreatorName() {
		return procInstCreatorName;
	}

	public void setProcInstCreatorName(String procInstCreatorName) {
		this.procInstCreatorName = procInstCreatorName;
	}

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public Date getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getResponsiblePersonOrgId() {
		return responsiblePersonOrgId;
	}

	public void setResponsiblePersonOrgId(String responsiblePersonOrgId) {
		this.responsiblePersonOrgId = responsiblePersonOrgId;
	}

	public String getResponsiblePersonOrgName() {
		return responsiblePersonOrgName;
	}

	public void setResponsiblePersonOrgName(String responsiblePersonOrgName) {
		this.responsiblePersonOrgName = responsiblePersonOrgName;
	}


	public String getWorkItemType() {
		return workItemType;
	}

	public void setWorkItemType(String workItemType) {
		this.workItemType = workItemType;
	}
	

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getSubProcessId() {
		return subProcessId;
	}

	public void setSubProcessId(String subProcessId) {
		this.subProcessId = subProcessId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	
	

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	
	

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}
	
	/**
	 * @return the remoteWorkItemId
	 */
	public String getRemoteWorkItemId() {
		return remoteWorkItemId;
	}

	/**
	 * @param remoteWorkItemId the remoteWorkItemId to set
	 */
	public void setRemoteWorkItemId(String remoteWorkItemId) {
		this.remoteWorkItemId = remoteWorkItemId;
	}
	
	
	
	/**
	 * @return the procInstCreatorId
	 */
	public String getProcInstCreatorId() {
		return procInstCreatorId;
	}

	/**
	 * @param procInstCreatorId the procInstCreatorId to set
	 */
	public void setProcInstCreatorId(String procInstCreatorId) {
		this.procInstCreatorId = procInstCreatorId;
	}
    public String getActivityInstanceId(){
    	return this.activityInstanceId;
    }
    public void setActivityInstanceId(String actInstId){
    	this.activityInstanceId = actInstId;
    }
//	/**
//	 * @return the activityInstance
//	 */
//	public abstract ActivityInstance getActivityInstance();
//
//	
//	/**
//	 * @param activityInstance the activityInstance to set
//	 */
//	public abstract void setActivityInstance(ActivityInstance activityInstance);

	public Object clone(){
		AbsWorkItem wi = null;
		
		if (this instanceof LocalWorkItemImpl){
			wi = new LocalWorkItemImpl();
		}else if (this instanceof WorkItemHistory){
			wi = new WorkItemHistory();
		}else if (this instanceof RemoteWorkItem){
			wi = new RemoteWorkItemImpl();
		}else return null;
		
		
		wi.setWorkItemType(this.workItemType);
		wi.setWorkItemName(this.workItemName);
		wi.setSubject(this.subject);
		wi.setState(state);
		
		wi.setCreatedTime(createdTime);
		wi.setClaimedTime(claimedTime);
		wi.setEndTime(endTime);
		wi.setExpiredTime(this.expiredTime);

		wi.setOwnerId(ownerId);
		wi.setOwnerName(ownerName);
		wi.setOwnerDeptId(ownerDeptId);
		wi.setOwnerDeptName(ownerDeptName);
		
		wi.setActionUrl(actionUrl);
		wi.setMobileActionUrl(this.mobileActionUrl);
		
		
		
		wi.setOriginalSystemName(this.originalSystemName);
		wi.setBizId(this.bizId);
		wi.setNote(note);
		wi.setRemoteWorkItemId(this.getRemoteWorkItemId());
		
		wi.setOwnerType(this.ownerType);
		
		wi.setAssignmentStrategy(assignmentStrategy);
		wi.setResponsiblePersonDeptId(responsiblePersonOrgId);
		wi.setResponsiblePersonDeptName(responsiblePersonOrgName);
		wi.setResponsiblePersonId(responsiblePersonId);
		wi.setResponsiblePersonName(responsiblePersonName);
		
		wi.setParentWorkItemId(parentWorkItemId);
		wi.setReassignType(reassignType);
		
		wi.setAttachmentId(attachmentId);
		wi.setAttachmentType(attachmentType);
		
		wi.setProcInstCreatorName(this.procInstCreatorName);
		wi.setProcInstCreatorId(this.procInstCreatorId);
		wi.setProcessId(this.processId);
		wi.setSubProcessId(subProcessId);
		wi.setVersion(version);
		wi.setProcessType(processType);
		wi.setProcessInstanceId(processInstanceId);
		wi.setActivityId(activityId);
		wi.setStepNumber(this.stepNumber);
		
		wi.setActivityInstanceId(this.activityInstanceId);
//		wi.setActivityInstance(this.activityInstance);
		wi.setProcInstCreatedTime(this.procInstCreatedTime);
		
		return wi;
	}

}
