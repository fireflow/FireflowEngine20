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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.nutz.ScheduleJobStateValueAdaptor;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.fireflow.server.support.DateTimeXmlAdapter;
import org.nutz.dao.entity.annotation.Column;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlType(name="absScheduleJobType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ScheduleJobImpl.class,ScheduleJobHistory.class})
public abstract class AbsScheduleJob extends AbsWorkflowEntity implements ScheduleJob{
	@XmlElement(name="name")
	@Column("NAME")
	protected String name = null;
	
	@XmlElement(name="displayName")
	@Column("DISPLAY_NAME")
	protected String displayName = null;
	
	@XmlElement(name="createdTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("CREATED_TIME")
	protected Date createdTime;
	
	@XmlElement(name="triggeredTimes")
	@Column("TRIGGED_TIMES")
	protected Integer triggeredTimes = 0;
	
	@XmlElement(name="latestTriggeredTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("LATEST_TRIGGERED_TIME")
	protected Date latestTriggeredTime = null;
	
	@XmlElement(name="triggerType")
	@Column("TRIGGER_TYPE")
	protected String triggerType;
	
	@XmlElement(name="triggerExpression")
	@Column("TRIGGER_EXPRESSION")
	protected String triggerExpression;
	
	@XmlElement(name="endTime")
	@XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
	@Column("END_TIME")
	protected Date endTime;

	@XmlElement(name="state")
	@Column(value="STATE",adaptor = ScheduleJobStateValueAdaptor.class)
	protected ScheduleJobState state = ScheduleJobState.RUNNING;
	

	
	@XmlElement(name="processId")
	@Column("PROCESS_ID")
	protected String processId;
	
	@XmlElement(name="processType")
	@Column("PROCESS_TYPE")
	protected String processType;
	
	@XmlElement(name="version")
	@Column("VERSION")
	protected Integer version;
	
	@XmlElement(name="createNewProcessInstance")
	@Column("CREATE_NEW_PROCESS_INSTANCE")
	protected Boolean createNewProcessInstance = false;
	
	@XmlElement(name="cancelAttachedToActivity")
	@Column("CANCEL_ATTACHED_TO_ACTIVITY")
	protected Boolean cancelAttachedToActivity=false;
	
	@XmlElement(name="note")
	@Column("NOTE")
	protected String note;
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the triggedTimes
	 */
	public Integer getTriggeredTimes() {
		return triggeredTimes;
	}
	/**
	 * @param triggedTimes the triggedTimes to set
	 */
	public void setTriggeredTimes(Integer triggedTimes) {
		this.triggeredTimes = triggedTimes;
	}
	/**
	 * @return the latestTriggedTime
	 */
	public Date getLatestTriggeredTime() {
		return latestTriggeredTime;
	}
	/**
	 * @param latestTriggedTime the latestTriggedTime to set
	 */
	public void setLatestTriggeredTime(Date latestTriggedTime) {
		this.latestTriggeredTime = latestTriggedTime;
	}	

	/**
	 * @return the cron
	 */
	public String getTriggerExpression() {
		return triggerExpression;
	}
	/**
	 * @param cron the cron to set
	 */
	public void setTriggerExpression(String expression) {
		this.triggerExpression = expression;
	}
	/**
	 * @return the state
	 */
	public ScheduleJobState getState() {
		return state;
	}
	
	
	/**
	 * @return the triggerType
	 */
	public String getTriggerType() {
		return triggerType;
	}
	/**
	 * @param triggerType the triggerType to set
	 */
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(ScheduleJobState state) {
		this.state = state;
	}

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
	 * @return the createNewProcessInstance
	 */
	public Boolean isCreateNewProcessInstance() {
		return createNewProcessInstance;
	}
	/**
	 * @param createNewProcessInstance the createNewProcessInstance to set
	 */
	public void setCreateNewProcessInstance(Boolean createNewProcessInstance) {
		this.createNewProcessInstance = createNewProcessInstance;
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
	public void setNote(String note) {
		this.note = note;
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

	public Boolean isCancelAttachedToActivity(){
		return this.cancelAttachedToActivity;
	}
	
	public void setCancelAttachedToActivity(Boolean b){
		this.cancelAttachedToActivity = b;
	}
	
}
