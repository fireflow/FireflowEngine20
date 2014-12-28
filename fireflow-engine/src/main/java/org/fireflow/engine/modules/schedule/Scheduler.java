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
package org.fireflow.engine.modules.schedule;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ScheduleJob;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface Scheduler extends EngineModule{
	public static final String JOB_GROUP_NAME = "org.fireflow.constants.schedule.FIRE_WORKFLOW_JOBS";
	public void schedule(ScheduleJob scheduleJob,RuntimeContext runtimeContext);
	public void unSchedule(ScheduleJob scheduleJob,RuntimeContext runtimeContext);
	public void onTimerTriggered(ScheduleJob scheduleJob,RuntimeContext runtimeContext);
	/**
	 * job是否还会被触发。
	 * @param job
	 * @param runtimeContext
	 * @return
	 */
	public boolean ifJobCanBeFiredAgain(ScheduleJob job,RuntimeContext runtimeContext);
	
	/**
	 * 调度队列中是否还有job待触发。
	 * @param runtimeContext
	 * @return
	 */
	public boolean hasJobInSchedule(RuntimeContext runtimeContext);
}
