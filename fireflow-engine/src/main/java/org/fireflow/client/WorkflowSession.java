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
package org.fireflow.client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.User;

/**
 * WorkflowSession是所有工作流操作的入口，相当于Jdbc的connection对象。
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public interface WorkflowSession extends Serializable {
	public static final String CURRENT_PROCESS_INSTANCE = "CURRENT_PROCESS_INSTANCE";
	public static final String CURRENT_ACTIVITY_INSTANCE = "CURRENT_ACTIVITY_INSTANCE";
//	public static final String LATEST_CREATED_WORKITEMS = "LATEST_CREATED_WORKITEMS";
	/**
	 * 唯一标示一个session
	 */
	public String getSessionId();
	/**
	 * 返回当前连接BPM子系统的用户
	 * @return
	 */
	public User getCurrentUser();
	

	
	//获得当前的流程实例
	//该方法在远程接口中不合理，因此在接口中注销
	//public ProcessInstance getCurrentProcessInstance();
	
	//获得当前的活动实例。
	//该方法在远程接口中不合理，因此在接口中注销
	//public ActivityInstance getCurrentActivityInstance();
	
	//获得最近一次流程操作所创建的所有的工作项。
	//该方法用于远程接口不合理，因为新产生的工作项可能非常多，通过该方法返回会导致
	//网络通信障碍，因此在接口中注销。
	//public List<WorkItem> getLatestCreatedWorkItems();	
	
	/**
	 * 创建Statement
	 * @return
	 */
	public WorkflowStatement createWorkflowStatement(String processType);
	
	/**
	 * 创建Statement，使用缺省的流程类别，即"FPDL";缺省流程类别可以在RuntimeContext中设置。
	 * @return
	 */
	public WorkflowStatement createWorkflowStatement();
	
	
	/**
	 * 创建Query
	 * @param <T> 需要查询的Entity的class类
	 * @param t 流程类别名称，如"FPDL"。对于Fpdl 2.0，可以用常量FpdlConstants.PROCESS_TYPE。
	 * @return
	 * WorkfowQuery只是一个存储查询条件信息的Bean,不需要该方法，2013-02-20
	 */
	//public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c,String processType);
	
	/**
	 * 创建Query，使用缺省的流程类别，即"FPDL";缺省流程类别可以在RuntimeContext中设置。
	 * @param <T> 需要查询的Entity的class类
	 * @param c 
	 * @return
	 */
	public <T extends WorkflowEntity> WorkflowQuery<T> createWorkflowQuery(Class<T> c);
	
	/**
	 * 为活动id等于activityId的实例指定一个动态的工作项分配句柄。
	 * @param activityId
	 * @param assignmentHandler
	 */
//	public WorkflowSession setDynamicAssignmentHandler(String activityId,AssignmentHandler assignmentHandler);
	
	/**
	 * 返回当前session中保存的所有的动态工作项分配句柄
	 * @return
	 */
//	public Map<String,AssignmentHandler> getDynamicAssignmentHandler();
}
