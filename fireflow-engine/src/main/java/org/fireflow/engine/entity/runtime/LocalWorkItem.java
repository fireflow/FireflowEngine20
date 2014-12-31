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
package org.fireflow.engine.entity.runtime;

import java.util.Date;

import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface LocalWorkItem extends WorkItem{
    //////////////////////////////////////////////////////////
	///////////////// 工作项属性              /////////////////////////
	/////////////////////////////////////////////////////////

    
    /**
     * 所有者类型，取值为USER,ROLE,DEPARTMENT,GROUP；
     * 即org.fireflow.model.resourcedef.ResourceType的部分枚举值。<br>
     * 该字段备用，便于日后工作项“后期绑定”的实现<<br>     * 目前，该字段的值固定为"USER"(即org.fireflow.model.resourcedef.ResourceType.USER)
     * @return
     */
    public String getOwnerType();

    /**
     * 责任人Id<b<br>    * 当张三将工作委托给李四时，李四仅仅是代理完成该工作，张三仍然需要对该工作负责。此字段用于存储张三的id。<br<br>   * 在执行WorkflowStatement.reassignWorkItemTo(...)时，将产生该字段的值。
     * @return
     */
    public String getResponsiblePersonId();
    
    
    /**
     * 责任人姓名<br><br>  * 当张三将工作委托给李四时，李四仅仅是代理完成该工作，张三仍然需要对该工作负责。此字段用于存储张三的姓名。<br><br> * 在执行WorkflowStatement.reassignWorkItemTo(...)时，将产生该字段的值。
     * @return
     */
    public String getResponsiblePersonName();
    
    
    /**
     * 责任人部门Id
     * 当张三将工作委托给李四时，李四仅仅是代理完成该工作，张三仍然需要对该工作负责。此字段用于存储张三所属部门的id。<br>
<br>* 在执行WorkflowStatement.reassignWorkItemTo(...)时，将产生该字段的值。
     * @return
     */
    public String getResponsiblePersonDeptId();
    
    /**
     * 责任人部门名称
     * 当张三将工作委托给李四时，李四仅仅是代理完成该工作，张三仍然需要对该工作负责。此字段用于存储张三所属部门的名称。<br>
 <br> 在执行WorkflowStatement.reassignWorkItemTo(...)时，将产生该字段的值。 
     * @return
     */
    public String getResponsiblePersonDeptName();
    
    /**
     * 返回审批意见信息Id，用于关联到外部的审批意见表或者附件表
     * @return
     */
    public String getAttachmentId();
    
    public void setAttachmentId(String attachementId);
    
    /**
     * 附件的类型信息，具体内涵由业务系统解释。<br>
  <br>例如：如果业务系统审批已经不是集中存储在一张表里面，此字段也可以用于
     * 存储审批意见（或者附件信息）表的表名。
     * @return
     */
    public String getAttachmentType();
    
    public void setAttachmentType(String type);
    
    public String getActivityInstanceId(); 
    
    /**
     * 返回任务实例
     * @return
     */
//    public ActivityInstance getActivityInstance();

    /**
     * 委派(加签)操作中父工作项Id
     * @return
     */
    public String getParentWorkItemId();
    
    /**
     * 委派(加签)类型
     * 在执行WorkflowStatement.reassignWorkItemTo(...)时，将产生该字段的值。 
     * @return
     */
    public String getReassignType();
    
	/**
	 * 返回任务实例的分配策略。
	 * @see org.fireflow.model.resourcedef.WorkItemAssignmentStrategy
	 * 
	 * @return
	 */
	public WorkItemAssignmentStrategy getAssignmentStrategy();
	

	/////////////////////////////////////////////////////////////////////
	/////////////  下面是冗余数据，为查询方便 /////////////////////////////
	////////////////////////////////////////////////////////////////////
	public String getProcInstCreatorName();

	public String getProcInstCreatorId();
	
	public Date getProcInstCreatedTime();
	
	/**
	 * 执行步骤号，便于查询排序，等于对应的activityInstance的stepNumber
	 * @return
	 */
	public int getStepNumber();
	
	public String getProcessId();
	
	public int getVersion();
	
	public String getProcessType();
	
	public String getSubProcessId();
	
	public String getActivityId();
	
	public String getProcessInstanceId();

}
