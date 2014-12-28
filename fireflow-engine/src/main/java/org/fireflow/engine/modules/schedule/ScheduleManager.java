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
package org.fireflow.engine.modules.schedule;

/**
 * 1、负责为含有TimerStart的流程创建并保存org.fireflow.engine.entity.runtime.ScheduleJob。
 *    （ScheduleManager定时扫描ProcessDescriptor.isTimerStart来确定流程是否是定时开始）
 * 2、负责在系统启动时Load所有的org.fireflow.engine.entity.runtime.ScheduleJob，并装载到Scheduler中。
 * 3、负责在集群环境下，在不同节点上合理分配org.fireflow.engine.entity.runtime.ScheduleJob。
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class ScheduleManager {

}
