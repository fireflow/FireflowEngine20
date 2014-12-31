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

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 工作项对象。<br>br>
 *
 * @author 非也,nychen2000@163.com
 *
 */
public interface WorkItem extends WorkflowEntity{

    public static final String REASSIGN_AFTER_ME = "org.fireflow.constants.reassign_type.AFTER_ME";
    public static final String REASSIGN_BEFORE_ME = "org.fireflow.constants.reassign_type.BEFORE_ME";
    
    public static final String NO_PARENT_WORKITEM = "org.fireflow.constants.workitem.NO_PARENT_WORKITEM";
	
    public static final String WORKITEM_TYPE_LOCAL = "org.fireflow.constants.workitem_type.LOCAL_WORKITEM";
    public static final String WORKITEM_TYPE_REMOTE = "org.fireflow.constants.workitem_type.REMOTE_WORKITEM";
    
	/**
	 * 工作项名称，一般情况下等于对应的activity名称
	 * @return
	 */
	public String getWorkItemName() ;
	
	/**
	 * 工作项主题，即该工作项的工作内容摘要信息。用于工作项列表显示。
	 * @return
	 */
	public String getSubject();
	
    /**
     * 返回工作项的状态
     * @return
     */
    public WorkItemState getState();



    /**
     * 返回创建时间
     * @return
     */
    public Date getCreatedTime();


    /**
     * 返回签收时间
     * @return
     */
   public Date getClaimedTime();

    /**
     * 返回结束时间
     * @return
     */
    public Date getEndTime();

	/**
	 * 过期时间
	 * @return
	 */
	public Date getExpiredTime() ;
	
    /**
     * 返回工作项所有者的Id
     * @return
     */
    public String getOwnerId();
    
    /**
     * 工作项所有者姓名
     * @return
     */
    public String getOwnerName();
    
    /**
     * 返回工作项所有者所在部门的Id
     * @return
     */
    public String getOwnerDeptId();
    
    /**
     * 返回工作项所有者所在部门的名称
     * @return
     */
    public String getOwnerDeptName();
    
	/**
	 * 处理该工作项的URL<<br>	 * 
	 * @return
	 */
	public String getActionUrl();
	
	/**
	 * 移动终端URL。（备用）
	 * @return
	 */
	public String getMobileActionUrl();
	

	/**
	 * 产生该工作项的业务系统名称
	 * @return
	 */
	public String getOriginalSystemName() ;
	
	/**
	 * 工作项关联的业务对象的Id
	 * @return
	 */
	public String getBizId();
	
    /**
     * 对于简单的业务系统，如果审批意见不必单独存储在一张表中，则可以用
     * 该字段存储审批意见。
     * @return
     */
    public String getNote();
    
    public void setNote(String note);
    
	/**
	 * work item 类型，取值为RemoteWorkItem.WORKITEM_TYPE_LOCAL和RemoteWorkItem.WORKITEM_TYPE_REMOTE。<b<br> * WORKITEM_TYPE_LOCAL表示本地WorkItem，WORKITEM_TYPE_REMOTE表示远程workitem.
	 */
	public String getWorkItemType();
}
