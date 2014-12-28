/**
 * Copyright 2007-2010 非也
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
package org.fireflow.engine.entity.repository;


/**
 * @author 非也
 * @version 2.0
 */
public interface ProcessDescriptor extends RepositoryDescriptor{
    /**
     * 流程Id
     * @return
     */
    public String getProcessId();

    /**
     * 流程版本号
     * @return
     */
    public Integer getVersion();
    
    
    /**
     * 流程类别，可能是Fireworkflow流程，BPMN2.0流程或者BPEL流程
     * @return
     */
	public String getProcessType() ;
	
    /**
     * 获得业务流程业务类别
     * @return
     */
    public String getPackageId();
    
    /**
     * 是否是定时启动的流程
     * @return
     */
    public Boolean getTimerStart();
    
    /**
     * 是否具有回调接口，回调接口将被发布成WebService。
     * @return
     */
    public Boolean getHasCallbackService();

    /**
     * 转换成ProcessRepository对象（实际是ProcessRepositoryImpl对象）
     * @param descriptor
     * @return
     */
    public ProcessRepository toProcessRepository();
}
