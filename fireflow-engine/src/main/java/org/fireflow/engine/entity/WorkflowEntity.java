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
package org.fireflow.engine.entity;

import java.util.Date;


/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface WorkflowEntity {
	public static final String ENTITY_NAME_ACTIVITY_INSTANCE = "ActivityInstance";
	public static final String ENTITY_NAME_PROCESS_INSTANCE = "ProcessInstance";
	public static final String ENTITY_NAME_WORKITEM = "WorkItem";
	public static final String ENTITY_NAME_VARIABLE = "Variable";
	public static final String ENTITY_NAME_TOKEN = "Token";
	public static final String ENTITY_NAME_FIREFLOW_CONFIG = "FireflowConfig";
	public static final String ENTITY_NAME_REASSIGN_CONFIG = "ReassignConfig";
	public static final String ENTITY_NAME_SCHEDULE_JOB = "ScheduleJob";
	public static final String ENTITY_NAME_PROCESS_DESCRIPTOR = "ProcessDescriptor";
	public static final String ENTITY_NAME_SERVICE_DESCRIPTOR = "ServiceDescriptor";
	public static final String ENTITY_NAME_RESOUCE_DESCRIPTOR = "ResourceDescriptor";
	
	/**
	 * WorkflowEntity 的 Id
	 * 
	 * @return
	 */
	public String getId();
	
	//TODO 将公共字段设置在此处，如lastUpdateTime

	public Date getLastUpdateTime();
}
