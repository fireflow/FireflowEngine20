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
import org.fireflow.model.InvalidModelException;

/**
 * 流程实例接口<br>
 * 
 * @author 非也,nychen2000@163.com
 * @author lifw555@gmail.com
 */
public interface ProcessInstance  extends Scope,WorkflowEntity{
    /////////////////////////////////////////////////////////////////
    ///////////////////    实例属性      ////////////////////////////////
    ////////////////////////////////////////////////////////////////
    
    /**
     * 返回流程实例所关联的业务表单的Id
     * @return
     */
    public String getBizId();

    /**
     * 流程实例的name（与流程定义的name相同）
     * @return
     */
    public String getProcessName();

    /**
     * 流程实例的DisplayName（与流程定义的DisplayName相同）
     * @return
     */
    public String getProcessDisplayName();
    
    public String getSubProcessName();
    
    public String getSubProcessDisplayName();
    
    /**
     * 增加一个bizType字段，以方便查询
     * @return
     */
    public String getBizType();
    
    
    public String getPackageId();

    /**
     * 流程定义的id
     * @return
     */
    public String getProcessId();

    /**
     * 流程实例的状态。对流程实例的状态字段作如下规定：小于5的状态为“活动”状态，大于等于5的状态为“非活动”状态。<br>
     * 活动状态包括：INITIALIZED,RUNNING,SUSPENDED<br>
     * 非活动状态包括：COMPLETED,CANCELED
     * @return
     */
    public ProcessInstanceState getState();

    /**
     * 流程定义的Version
     * @return
     */
    public Integer getVersion();
    
    /**
     * 获得当前实例对应的subflow，对于没有subflow的模型，该值等于processId。
     * @return
     */
    public String getSubProcessId();

    /**
     * 流程实例创建者ID
     * @return
     */
    public String getCreatorId();
    
    /**
     * 实例创建者的姓名
     * @return
     */
    public String getCreatorName();
    
    /**
     * 创建者所在部门Id
     * @return
     */
    public String getCreatorDeptId();

    /**
     * 创建者所在部门名称
     * @return
     */
    public String getCreatorDeptName();
    
    /**
     * 返回流程实例的创建时间
     * @return 流程实例的创建时间
     */
    public Date getCreatedTime();

    /**
     * 返回流程实例的启动时间，即执行IProcessInstance.run()的时间
     * @return
     */
    public Date getStartedTime();

    /**
     * 返回流程实例的结束时间
     * @return
     */
    public Date getEndTime();

    /**
     * 返回流程实例的到期时间
     * @return
     */
    public Date getExpiredTime();

    /**
     * 是否挂起
     * @return
     */
    public Boolean isSuspended();

    /**
     * get the parent process instance's id , null if no parent process instance.
     * @return
     */
    public String getParentProcessInstanceId();

    /**
     * get the parent taskinstance's id ,null if no parent taskinstance.
     * @return
     */
    public String getParentActivityInstanceId();
    
    /**
     * 备注信息
     * @return
     */
    public String getNote();
    
    /**
     * 获得流程类型值
     * @return
     */
    public String getProcessType();
    
    public String getTokenId();
    
    
    /////////////////////////////////////////////////////////////////
    ///////////////////    业务操作    ////////////////////////////////
    ////////////////////////////////////////////////////////////////


    /**
     * return the corresponding workflow process.
     * @return
     */
    public Object getWorkflowProcess(WorkflowSession session) throws InvalidModelException;


   
}
