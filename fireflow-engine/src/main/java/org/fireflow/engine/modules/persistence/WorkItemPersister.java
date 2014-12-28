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

import org.fireflow.engine.entity.runtime.WorkItem;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public interface WorkItemPersister  extends Persister{
	/**
	 * 删除所有的WorkItem，该方法提供给Simulator使用，业务系统一般 用不上该方法。
	 * 
	 */
	public void deleteAllWorkItems();
	/**
	 * 删除ActivityInstance所属的状态为Initialized的workItem
	 * @param activityInstanceId
	 */
	public void deleteWorkItemsInInitializedState(String activityInstanceId,String parentWorkItemId);
	
	/**
	 * 查询同一个activityInstance的WorkItem
	 * @param activityInstanceId
	 * @return
	 */
	public List<WorkItem> findWorkItemsForActivityInstance(String activityInstanceId);
	
	public List<WorkItem> findWorkItemsForActivityInstance(String activityInstanceId,String parentWorkItemId);
}
