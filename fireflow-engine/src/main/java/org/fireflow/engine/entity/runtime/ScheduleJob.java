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
package org.fireflow.engine.entity.runtime;

import java.util.Date;

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 定时器调度句柄，
 * 
 * @author 非也
 * @version 2.0
 */
public interface ScheduleJob extends WorkflowEntity{
	public static final String CRON = "CRON";
	public static final String STARTTIME_ENDTIME_INTERVAL = "STARTTIME_ENDTIME_INTERVAL";
	public static final String STARTTIME_REPEATCOUNT_INTERVAL = "STARTTIME_REPEATCOUNT_INTERVAL";
	
	public String getName();
	
	public String getDisplayName();
	
	
	/**
	 * 获得触发器类型，合法的返回值是：CRON , STARTTIME_ENDTIME_INTERVAL ,STARTTIME_REPEATCOUNT_INTERVAL;<br/>
	 * CRON：cron表达式<br/>
	 * STARTTIME_ENDTIME_INTERVAL：指定开始时间，结束时间和间隔时间<br/>
	 * STARTTIME_REPEATCOUNT_INTERVAL：指定开始时间，重复次数，时间间隔<br/>
	 * @return
	 */
	public String getTriggerType();
	
	/**
	 * 如果TriggerType=Cron，则该值是cron表达式，<br/>
	 * 如果TriggerType=STARTTIME_ENDTIME_INTERVAL，则该值的格式是：startTime|endTime|interval<br/>
	 * 如果TriggerType=STARTTIME_REPEATCOUNT_INTERVAL，则该值格式是：startTime|repeatCount|interval<br/>
	 * 触发器表达式
	 * @return
	 */
	public String getTriggerExpression();
	
    /**
     * 返回创建时间
     * @return
     */
    public Date getCreatedTime();


    /**
     * 被触发的次数
     * @return
     */
    public Integer getTriggeredTimes();
    
    /**
     * 最近一次被触发的时间
     * @return
     */
    public Date getLatestTriggeredTime();

    
    /**
     * 被置为非活动状态的时间
     * @return
     */
    public Date getEndTime();
    

	/**
	 * 状态
	 * @return
	 */
	public ScheduleJobState getState();
	
	/**
	 * 调度器对应的ActivityInstance；
	 * 对于起始节点的调度器句柄，该字段为空
	 * @return
	 */
	public ActivityInstance getActivityInstance();

	/**
	 * 流程Id，ProcessId,ProcessType,Version三个字段便于调度器启动新的流程实例
	 * @return
	 */
	public String getProcessId();
	
	/**
	 * 流程类型
	 * @return
	 */
	public String getProcessType();
	
	/**
	 * 流程版本
	 * @return
	 */
	public Integer getVersion();
	
	/**
	 * 是否启动新的流程实例
	 * @return
	 */
	public Boolean isCreateNewProcessInstance();
	
	/**
	 * 是否取消所依附的活动
	 * @return
	 */
	public Boolean isCancelAttachedToActivity();
	
	/**
	 * 备注信息
	 * @return
	 */
	public String getNote();
}
