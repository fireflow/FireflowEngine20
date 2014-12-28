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
package org.fireflow.pdl.fpdl.process;

import java.util.List;

import org.fireflow.model.data.Property;
import org.fireflow.model.misc.Duration;
import org.fireflow.model.process.WorkflowElement;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface SubProcess extends WorkflowElement{
	/**
	 * 获得该子流程的预计的运行时间
	 * @return
	 */
	public Duration getDuration();
	
	/**
	 * 返回该子流程的预计的运行时间
	 * @param du
	 */
	public void setDuration(Duration du);
	
	/**
	 * 获得流程变量声明列表
	 * @return
	 */
	public List<Property> getProperties();

	/**
	 * 根据输入的流程变量的名称返回流程变量
	 * @param name 流程变量名称
	 * @return 流程变量（即Property对象）
	 */
	public Property getProperty(String name);
	/**
	 * 
	 * 流程的入口节点
	 * @return
	 */
	public Node getEntry();
	
	public void setEntry(Node start);
	
	/**
	 * 获得所有的开始节点
	 * @return
	 */
	public List<StartNode> getStartNodes();
	public StartNode getStartNode(String startNodeId);

	
	/**
	 * 获得流程图中的所有的活动
	 * @return
	 */
	public List<Activity> getActivities();
	public Activity getActivity(String activityId);

	
	/**
	 * 获得所有的路由节点
	 * @return
	 */
	public List<Router> getRouters();
	public Router getRouter(String routerId);

	
	/**
	 * 所有的结束节点
	 * @return
	 */
	public List<EndNode> getEndNodes();
	public EndNode getEndNode(String endNodeId);

	/**
	 * 所有的转移
	 * @return
	 */
	public List<Transition> getTransitions();
	public Transition getTransition(String transitionId);
	
	
	/**
	 * 通过Id查找流程元素，如开始节点，结束节点，活动，路由，转移等等
	 * @param id
	 * @return
	 */
	public WorkflowElement findWFElementById(String id);
	
	public List<EventListenerDef> getEventListeners();
}
