/**
 * Copyright 2007-2008 非也
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
package org.fireflow.pdl.fpdl.process;

import java.util.List;

import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.misc.Duration;
import org.fireflow.pdl.fpdl.misc.LoopStrategy;
import org.fireflow.pdl.fpdl.process.event.EventListenerDef;

/**
 * 活动。<br/>
 * 从2.0开始，一个活动可以有多个输入，任何一个输入发生了都可以出发该活动的执行。<br/>
 * 一个活动也可以有多个输出，输出是否被执行是由输出边上的转移条件决定的。<br/>
 * 两个活动之间可以用Transition直接相连。
 * <Activity>
 * 	<ServiceBinding>
 * 		<!--
 *      <service/>
 *      --> 
 * 		<ref service="Approve_XX" operation="">
 * 		  <IOMapping>
 * 			<InputAssignments>
 * 				<InputAssignment from="an_expression" to="the_input_name">
 * 			<InputAssignments>
 * 			<OutputAssignments>
 * 				<OutputAssignment from="an_expression" to="the_process_property_name">
 * 			<OutputAssignments> 
 * 		  </IOMapping>
 * 		</ref>
 * 		<PropOverrides>
 * 			<PropOverride propGroupName="" propName="" value="">
 * 		</PropOverrides>
 * 	</ServiceBinding>
 *  <ResourceBinding>
 *  </ResourceBinding>
 * </Activity>
 * 
 * @author 非也,nychen2000@163.com
 *
 */
public interface Activity extends Node{
	/**
	 * 该环节的运行时长，用于计算超期时间点
	 * @return
	 */
	public Duration getDuration();
	public void setDuration(Duration du);
	
	/**
	 * 该环节的优先级，用于workItem排序，暂时保留
	 * @return
	 */
	public String getPriority();
	public void setPriority(String s);
	
	/**
	 * 在循环的情况下，重做策略
	 * @return
	 */
	public LoopStrategy getLoopStrategy();
	public void setLoopStrategy(LoopStrategy loopStrategy);
	
	/**
	 * 获得局部变量声明列表。（暂时保留）
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
	 * 本活动所引用的服务组件
	 * @return
	 */
	public ServiceBinding getServiceBinding();
	
	public void setServiceBinding(ServiceBinding serviceRef) ;
	

	/**
	 * 本活动的资源引用
	 * @return
	 */
	public ResourceBinding getResourceBinding();
	
	public void setResourceBinding(ResourceBinding actrsc);
	
	/**
	 * 返回依附于本活动的启动节点
	 * @return
	 */
	public List<StartNode> getAttachedStartNodes();
	
	/**
	 * 事件监听器接入点
	 */
	public List<EventListenerDef> getEventListeners();
	
	public List<EventListenerDef> getWorkItemEventListeners();
	
	/**
	 * 获得本节点的后继Activity，如果直接后继节点是网关(Router)，则继续寻找网关的后继节点。
	 * @return
	 */
	public List<Activity> getNextActivities();
	

}
