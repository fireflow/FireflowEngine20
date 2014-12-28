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
package org.fireflow.engine.modules.event;

import org.fireflow.client.WorkflowSession;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface Event {
	/**
	 * 返回该事件的触发点
	 * @return
	 */
	public EventTrigger getEventTrigger();
	
	/**
	 * 获得事件源，是ProcessInstance,ActivityInstance,WorkItem之一
	 * @return
	 */
	public Object getSource();
	
	/**
	 * 获得事件源对应的Workflow元素，一般是SubProcess或者Activity
	 * @return
	 */
	public Object getWorkflowElement();
	
	/**
	 * 当前的WorkflowSession
	 * @return
	 */
	public WorkflowSession getCurrentWorkflowSession();
}
