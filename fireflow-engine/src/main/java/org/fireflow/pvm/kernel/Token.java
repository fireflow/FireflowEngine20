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
package org.fireflow.pvm.kernel;

import org.fireflow.engine.entity.WorkflowEntity;

/**
 * 
 * @author 非也
 * @version 2.0
 */
public interface Token extends WorkflowEntity{
	///////////////////////////////////////////////////////////
	///////////////// 下面是token的 图属性                          ////////////
	//////////////////////////////////////////////////////////
	public void setId(String id);

	/**
	 * 设置token的值，该值表示token的数量，在petri 网中表示资源的数量。
	 * @param value
	 */
	public void setValue(Integer value);
	public Integer getValue();
	
	/**
	 * 指示业务逻辑是否可以被启动。
	 * 等价于fpdl1.0的alive属性
	 * @param b
	 */
	public void setBusinessPermitted(Boolean b);
	public Boolean isBusinessPermitted();
		
	///////////////////////////////////////////////////////////
	///////////////// 下面是token的业务属性                          ////////////
	//////////////////////////////////////////////////////////	
	/**
	 * token状态
	 */
	public TokenState getState();
	public void setState(TokenState state);
	
	
	/**
	 * 步数
	 * @param stepNumber
	 */
	public void setStepNumber(Integer stepNumber);
	public Integer getStepNumber();
	

	/**
	 * 流程元素的Id；流程元素可能是结点，也可能是边。
	 * @param workflowElementId
	 */
	public void setElementId(String workflowElementId);
	public String getElementId();
	
	/**
	 * 流程Id
	 * @param processId
	 */
	public void setProcessId(String processId);
	public String getProcessId();
	
	/**
	 * 流程版本
	 * @param version
	 */
	public void setVersion(Integer version);
	public Integer getVersion();
	
	/**
	 * 流程类别，BPEL,FPDL,BPMN等等
	 * @return
	 */
	public String getProcessType();
	public void setProcessType(String processType);
	
	/**
	 * 流程实例的Id
	 * @param procInstId
	 */
	public void setProcessInstanceId(String procInstId);
	public String getProcessInstanceId();
	
	/**
	 * 节点实例Id
	 * @param elementInstanceId
	 */
	public void setElementInstanceId(String elementInstanceId);
	
	public String getElementInstanceId();
	
	/**
	 * 父tokenId
	 * @return
	 */
	public String getParentTokenId();
	public void setParentTokenId(String pid);
	
	
	/**
	 * 取值为NORMAL,FAULT,CANCELLATION,COMPENSATION
	 */
	public OperationContextName getOperationContextName();
	public void setOperationContextName(OperationContextName opCtxName);
	
	/**
	 * 回调token的键值
	 * @return
	 */
	public String getCallbackTokenId();
	
	public void setCallbackTokenId(String id);
	
	public String getFromToken();
	public void setFromToken(String tokenId);
	
	public String getAttachedToToken();
	
	public void setAttachedToToken(String tokenId);
	
	/**
	 * 下一个补偿节点对应的Token
	 * @return
	 */
	public String getNextCompensationToken();
	public void setNextCompensationToken(String tokenId);
	
	/**
	 * 
	 * @return
	 */
	public String getCompensationCode();
	public void setCompensationCode(String compensationCode);
	
	/**
	 * 对应的PObject是否为容器，NetInstance显然是容器，
	 * NodeInstance由其承载的Service决定，只有承载子流程类型的Service时才是容器
	 * @return
	 */
	public Boolean isContainer();
	public void setContainer(Boolean b);
}
