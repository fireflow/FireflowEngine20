/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.engine.modules.workitem;

import org.fireflow.engine.entity.runtime.RemoteWorkItem;

/**
 * 统一待办中心操作接口
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface WorkItemCenter {
	/**
	 * 向统一待办中心增加一个WorkItem
	 * @param workItem
	 */
	public void addWorkItem(RemoteWorkItem workItem);
	
	/**
	 * 向统一待办中心更新一个已经存在的WorkItem；
	 * 如果统一待办中心不存在该workItem，则不执行任何动作。
	 * @param workItem
	 */
	public void updateWorkItem(RemoteWorkItem workItem);
	
	/**
	 * 删除一个工作项（物理删除）
	 * @param workItem
	 */
	public void deleteWorkItem(RemoteWorkItem workItem);
	
	/**
	 * 如果WokItem已经存在，则做update操作，否则做insert操作。
	 * @param workItem
	 */
	public void saveWorkItem(RemoteWorkItem workItem);
}
