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
package org.fireflow.engine.modules.schedule.impl;


import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.modules.schedule.Scheduler;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ScheduleJobBean4SpringQuartz extends QuartzJobBean {

	/* (non-Javadoc)
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        ScheduleJob timerHandler = (ScheduleJob)dataMap.get(ScheduleJob.class.getName());
        RuntimeContext runtimeContext = (RuntimeContext)dataMap.get(RuntimeContext.class.getName());
        
        Scheduler timerHandlerManager = runtimeContext.getEngineModule(Scheduler.class, timerHandler.getProcessType());
        
                
        timerHandlerManager.onTimerTriggered(timerHandler, runtimeContext);

	}

}
