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
package org.fireflow.engine.entity.repository;

import java.util.Date;

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 * 
 */
public interface RepositoryDescriptor extends WorkflowEntity{

	/**
     * 流程名称
     * @return
     */
    public String getName();
    
    /**
     * 流程中文名
     * @return
     */
    public String getDisplayName();

    /**
     * 流程描述信息
     * @return
     */
    public String getDescription() ;

   
    
    /**
     * 流程发布状态
     * @return
     */
    public Boolean getPublishState();

    /**
     * 流程生效日期起，缺省从流程保存到流程库开始算起。
     * 该字段不能为空。
     * @return
     */
    public Date getValidDateFrom();
    
    /**
     * 流程生效日期止，缺省可以设置一个很大的日期数。
     * 该字段不能为空。
     * @return
     */
    public Date getValidDateTo();

    /**
     * 流程所有者ID，该字段备用。
     * @return
     */
	public String getOwnerId() ;

	public String getOwnerName();
	
    /**
     * 批准人
     * @return
     */
    public String getApprover() ;
    
    
    /**
     * 批准时间
     * @return
     */
    public Date getApprovedTime() ;
    

    /**
     * 最后修改人姓名
     * @return
     */
	public String getLastEditor() ;

	/**
	 * 简要发布日志
	 * @return
	 */
	public String getUpdateLog();
}
