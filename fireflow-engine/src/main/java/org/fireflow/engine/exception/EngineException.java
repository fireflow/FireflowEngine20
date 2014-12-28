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
package org.fireflow.engine.exception;

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;

/**
 * 引擎exception定义，如果发生该异常，说明系统有bug，或者管理员必须对系统进行人工干预。
 * @author chennieyun
 *
 */
@SuppressWarnings("serial")
public class EngineException extends RuntimeException {
	/**
     * 抛出异常的流程实例的Id
     */
    String processInstanceId = null;
    /**
     * 抛出异常的流程定义的Id
     */
    String processId = null;
    
    Integer version = null;
    
    String processType = null;
    
    /**
     * 抛出异常的流程的名称
     */
    String processName = null;
    /**
     * 抛出异常的流程的显示名称
     */
    String processDisplayName = null;
    /**
     * 抛出异常的流程元素的Id
     */
    String workflowElementId = null;
    /**
     * 抛出异常的流程元素的名称
     */
    String workflowElementName = null;
    /**
     * 抛出异常的流程元素的显示名称
     */
    String workflowElementDisplayName = null;
    
    String workflowElementInstanceId  = null;
    
    public EngineException(String message){
    	super(message);
    }
    
    /**
     * @deprecated
     * @param processInstance
     * @param activityInstance
     * @param message
     */
    public EngineException(ProcessInstance processInstance,ActivityInstance activityInstance,String message){
    	super(message);
		if (activityInstance != null) {
			this.processInstanceId = activityInstance.getProcessInstanceId();
			this.processId = activityInstance.getProcessId();
			this.processType = activityInstance.getProcessType();
			this.version = activityInstance.getVersion();
			this.workflowElementId = activityInstance.getNodeId();
			this.workflowElementName = activityInstance.getName();
			this.workflowElementDisplayName = activityInstance.getDisplayName();
			this.workflowElementInstanceId = activityInstance.getId();
			this.processName = activityInstance.getProcessName();
			this.processDisplayName = activityInstance.getProcessDisplayName();
		}
//    	if (processInstance!=null){
//    		this.processName = processInstance.getName();
//    		this.processDisplayName = processInstance.getDisplayName();
//    	}
    	
    	
    }
    
    public EngineException(ActivityInstance activityInstance,String message){
    	super(message);
		if (activityInstance != null) {
			this.processInstanceId = activityInstance.getProcessInstanceId();
			this.processId = activityInstance.getProcessId();
			this.processType = activityInstance.getProcessType();
			this.version = activityInstance.getVersion();
			this.workflowElementId = activityInstance.getNodeId();
			this.workflowElementName = activityInstance.getName();
			this.workflowElementDisplayName = activityInstance.getDisplayName();
			this.workflowElementInstanceId = activityInstance.getId();
			this.processName = activityInstance.getProcessName();
			this.processDisplayName = activityInstance.getProcessDisplayName();
		}
    }
    public EngineException(ProcessInstance processInstance,String message){
    	super(message);
		if (processInstance != null) {
			this.processInstanceId = processInstance.getId();
			this.processId = processInstance.getProcessId();
			this.processType = processInstance.getProcessType();
			this.version = processInstance.getVersion();
			this.processName = processInstance.getProcessName();
			this.processDisplayName = processInstance.getProcessDisplayName();
		}
    }    

    public EngineException(Throwable e){
    	super(e);
    }
//	/**
//	 * 
//	 * @param processInstance 发生异常的流程实例
//	 * @param workflowElement 发生异常的流程环节或者Task
//	 * @param errMsg 错误信息
//	 */
//    public EngineException(ProcessInstance processInstance,ModelElement workflowElement,String errMsg){
////        super(processInstance,workflowElement,errMsg);
//    }
//
//    /**
//     * 
//     * @param processInstanceId 发生异常的流程实例Id
//     * @param process 发生异常的流程
//     * @param workflowElementId 发生异常的环节或者Task的Id
//     * @param errMsg 错误信息
//     */
//    public EngineException(String processInstanceId, WorkflowProcessImpl process,
//            String workflowElementId, String errMsg) {
////        super(null, null, errMsg);
//        this.setProcessInstanceId(processInstanceId);
//        if (process != null) {
//            this.setProcessId(process.getId());
//            this.setProcessName(process.getName());
//            this.setProcessDisplayName(process.getDisplayName());
//
//            ModelElement workflowElement = process.findWFElementById(workflowElementId);
//            if (workflowElement != null) {
//                this.setWorkflowElementId(workflowElement.getId());
//                this.setWorkflowElementName(workflowElement.getName());
//                this.setWorkflowElementDisplayName(workflowElement.getDisplayName());
//            }
//        }
//    }
}
