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
package org.fireflow.engine.entity.config;

import java.util.Date;

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 委派设置
 * 
 * @author 非也
 * @version 2.0
 */
public interface ReassignConfig extends WorkflowEntity{
	public static final String AGENT_TYPE_USER = "USER";
	public static final String AGENT_TYPE_DEPT = "DEPT";
	public static final String AGENT_TYPE_ROLE = "ROLE";
	public static final String AGENT_TYPE_GROUP = "GROUP";

	/**
	 * 被委派的流程的Id
	 * @return
	 */
	public String getProcessId();
	public String getProcessName();
	public String getProcessDisplayName();
	public String getProcessType();
	public String getActivityId();
	public String getActivityName();
	public String getActivityDisplayName();
	
	/**
	 * 委派人Id
	 * @return
	 */
	public String getGrantorId();
	
	public String getGrantorName();
	
	public String getGrantorDeptId();
	
	public String getGrantorDeptName();
	
	
	/**
	 * 代理人Id
	 * @return
	 */
	public String getAgentId();
	
	public String getAgentName();
	
	
	/**
	 * 代理人类型，可以是ReassignConfig.AGENT_TYPE_USER,
	 * ReassignConfig.AGENT_TYPE_DEPT,ReassignConfig.AGENT_TYPE_ROLE,
	 * 或者ReassignConfig.AGENT_TYPE_GROUP
	 * @return
	 */
	public String getAgentType();
	
//	public String getAgentDeptId();
	
//	public String getAgentDeptName();
	
	/**
	 * 生效时间
	 * @return
	 */
	public Date getStartTime();
	
	public Date getEndTime();
	
	/**
	 * 是否有效
	 * @return
	 */
	public Boolean getAlive();
}
