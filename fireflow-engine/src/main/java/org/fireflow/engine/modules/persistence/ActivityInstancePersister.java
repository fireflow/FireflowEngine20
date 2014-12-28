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
package org.fireflow.engine.modules.persistence;

import java.util.List;

import org.fireflow.engine.entity.runtime.ActivityInstance;

/**
 * @author 非也
 * @version 2.0
 */
public interface ActivityInstancePersister extends Persister{
	/**
	 * 删除所有的activity instance
	 */
	public void deleteAllActivityInstances();
	
	public int countAliveActivityInstance(String processInstanceId,String nodeId);
	
	public void lockActivityInstance(String activityInstanceId);
	
	/**
	 * 查询流程实例中同一个活动的所有实例
	 * @param processInstanceId
	 * @param activityId
	 * @return
	 */
	public List<ActivityInstance> findActivityInstances(String processInstanceId,String activityId);
	
	/**
	 * 查询同一个流程实例的活动实例
	 * @param processInstanceId
	 * @return
	 */
	public List<ActivityInstance> findActivityInstances(String processInstanceId);
//	public ActivityInstance findActivityInstance(String processInstanceId,String activityId,Integer stepNumber);
}
