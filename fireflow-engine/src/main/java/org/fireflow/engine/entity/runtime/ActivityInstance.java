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
package org.fireflow.engine.entity.runtime;

import java.util.Date;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.InvalidOperationException;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.KernelException;

/**
 * 活动实例<br>
 * 对活动实例的状态字段作如下规定：小于10的状态为“活动”状态，大于等于10的状态为“非活动”状态。<br>
 * 活动状态包括：INITIALIZED,RUNNING,SUSPENDED<br>
 * 非活动状态包括：COMPLETED,CANCELED
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public interface ActivityInstance extends Scope,WorkflowEntity{
	/////////////////////////////////////////////////////////////////
	/////////          活动实例属性                         /////////////////////////
	////////////////////////////////////////////////////////////////

	/**
	 * 获得整个流程的业务单据Id
	 * @return
	 */
	public String getBizId();

	/**
	 * 获得本活动的子业务单据Id（备用）
	 * @return
	 */
	public String getSubBizId();
	
	/**
	 * 流程实例创建者（业务发起者）的Id，冗余该字段便于查询
	 * @return
	 */
	public String getProcInstCreatorId();
	
	/**
	 * 流程实例创建者（业务发起者）的名字，冗余该字段便于查询
	 * @return
	 */
	public String getProcInstCreatorName();
	
	/**
	 * 流程实例创建时间，冗余该字段便于查询
	 * @return
	 */
	public Date getProcInstCreatedTime();

	/**
	 * 返回任务Name
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 返回任务显示名
	 * 
	 * @return
	 */
	public String getDisplayName();

	/**
	 * 返回任务实例的状态
	 * 
	 * @return
	 */
	public ActivityInstanceState getState();

	/**
	 * 活动实例是否处于被挂起状态
	 * @return
	 */
	public Boolean isSuspended();
	
	/**
	 * 返回任务实例创建的时间
	 * 
	 * @return
	 */
	public Date getCreatedTime();

	/**
	 * 返回任务实例启动的时间
	 * 
	 * @return
	 */
	public Date getStartedTime();

	/**
	 * 返回任务实例结束的时间
	 * 
	 * @return
	 */
	public Date getEndTime();

	/**
	 * 返回任务实例到期日期
	 * 
	 * @return
	 */
	public Date getExpiredTime();// 过期时间


	/**
	 * 返回TaskInstance的"步数"。
	 * 
	 * @return
	 */
	public Integer getStepNumber();
	
	/**
	 * 返回对应的流程实例Id
	 * 
	 * @return
	 */
	public String getProcessInstanceId();

	/**
	 * 返回对应的流程的Id
	 * 
	 * @return
	 */
	public String getProcessId();

	/**
	 * 返回流程的版本
	 * 
	 * @return
	 */
	public Integer getVersion();

	public String getProcessType();
	
    /**
     * 获得当前实例对应的subflow，对于没有subflow的模型，该值等于processId。
     * @return
     */
    public String getSubProcessId();
	
    
    /**
     * processName
     * @return
     */
	public String getProcessName();
	
	/**
	 * processDisplayName
	 * @return
	 */
	public String getProcessDisplayName();
	
	
	public String getSubProcessName();
	
	public String getSubProcessDisplayName();
	
	/**
	 * 增加业务类别字段，便于查询。2011-04-02
	 * @return
	 */
	public String getBizType();
	/**
	 * 对应的流程节点的Id
	 * 
	 * @return
	 */
	public String getNodeId();

	/**
	 * 返回对应的服务定义的Id
	 * 
	 * @return
	 */
	public String getServiceId();
	
	public String getServiceVersion();
	
	/**
	 * 返回服务类型，取值为org.fireflow.model.Task.FORM,org.fireflow.model.Task.TOOL,
	 * org.fireflow.model.Task.SUBFLOW或者org.fireflow.model.Task.DUMMY
	 * @deprecated
	 * @return
	 */
	public String getServiceType();
	/**
	 * 当执行JumpTo和LoopTo操作时，返回目标Activity 的Id
	 * 
	 * @return
	 */
	public String getTargetActivityId();

	public Boolean getCanBeWithdrawn() ;
	/**
	 * 备注信息
	 * @return
	 */
	public String getNote();
	
	public String getTokenId();

    
	/////////////////////////////////////////////////////////////////
	/////////          获得关联的其他对象           /////////////////////////
	////////////////////////////////////////////////////////////////
	
	public ProcessInstance getProcessInstance(WorkflowSession session);
	
	/**
	 * 返回对应的活动定义
	 * 
	 * @return
	 * @throws org.fireflow.engine.exception.EngineException
	 */
//	public Object getActivity(WorkflowSession session) throws EngineException;
	
	/**
	 * 返回任务实例对应的流程
	 * 
	 * @return
	 * @throws org.fireflow.engine.exception.EngineException
	 */
	public Object getWorkflowProcess(WorkflowSession session) throws InvalidModelException;



}
