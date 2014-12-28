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

import org.fireflow.client.WorkflowSession;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * 可执行过程语言的最顶层对象，类似于Java语言中的Object对象。<br/>
 * 任何一个流程语言的语言元素在执行时，都映射成某种ProcessObject对象。
 * @author 非也
 * @version 2.0
 */
public interface PObject {

	/**
	 * ProcessObject的唯一标示
	 * @return
	 */
	public PObjectKey getKey();
	public void setKey(PObjectKey key);
	
	/**
	 * 获得该ProcessObject的原始的流程语言元素。
	 * @return
	 */
	public Object getWorkflowElement();
	public void setWorkflowElement(Object wfElement);
	
	/**
	 * 获得ProcessObject的行为对象。
	 * @return
	 */
	public WorkflowBehavior getWorkflowBehavior();
	public void setWorkflowBehavior(WorkflowBehavior processBehavior);
	
	
	/**
	 * 异常处理句柄
	 * @return
	 */
	public PObject getFaultHandler(String errorCode);
	
	public PObject getDefaultFaultHandler();
	
	public void setFaultHandler(String errorCode,PObject hanlder);
	
	public void setFaultHandler(String errorCode,PObject hanlder,boolean isDefaultFaultHandler);
	
	/**
	 * 补偿处理句柄
	 * @return
	 */
	public PObject getCompensationHandler(String compensationCode);
	public PObject getDefaultCompensationHandler();
	public void setCompensationHandler(String compensationCode,PObject handler);
	public void setCompensationHandler(String compensationCode,PObject handler,boolean isDefaultCompensationHandler);
	
	/**
	 * 取消操作的句柄
	 * （2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	 * @return
	 */
	/*
	public PObject getCancellationHandler();
	public void setCancellationHandler(PObject pobject);
	*/
	
	/**
	 * 是否接受取消操作，只有接受取消操作的PObject，系统才会调用他的handleCancellation()、handleTermination()方法。
	 * @return true表示可以接受取消操作。
	 */
	public boolean isCancellable();
	
	public void setCancellable(Boolean b);
	
	/**
	 * 是否接受补偿操作，只有接受补偿操作的PObject，系统才会调用他的handleCompensation()方法。
	 * 
	 * @return
	 */
	public boolean isCompensable();
	
	public void setCompensable(Boolean b);
	
	public boolean isAcceptCompensation(Token token,String compensationCode);
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////  ProcessObject的行为抽象           ///////////////////
	//////////////////////////////////////////////////////////////////////
	/**
	 * 获得一个新的token，token的状态必须是TokenState.INITIALIZED
	 * @param sourceToken TODO
	 */
	public void takeToken(WorkflowSession session,Token thisToken, Token sourceToken);
	
	/**
	 * 将token继续向前推动，token的状态必须小于TokenState.DELIMITER
	 * @param session
	 * @param sourceToken 触发existToken进行
	 * @param listenerToken 执行forward动作的token
	 */
	public void forwardToken(WorkflowSession session ,Token listenerToken, Token sourceToken);
	
	/**
	 * 处理异常，Token的状态必须是TokenState.RUNNING
	 * @param session
	 * @param listenerToken
	 * @param sourceToken TODO
	 * @param errorCode TODO
	 */
	public void handleFault(WorkflowSession session ,Token listenerToken, Token sourceToken, String errorCode);
	
	/**
	 * 处理补偿，Token的状态必须是TokenState.COMPLETED
	 * @param session
	 * @param listenerToken 响应补偿事件的Token
	 * @param sourceToken TODO
	 * @param compensationCode 补偿事件代码
	 */
	public void handleCompensation(WorkflowSession session ,Token listenerToken, Token sourceToken, String compensationCode);

	/**
	 * 处理取消操作,Token的状态必须是TokenState.RUNNING或者TokenState.FAULTING
	 * 
	 * （2012-02-03，该动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	 * @param session
	 * @param existToken
	 * @param sourceToken TODO
	 * 
	 */
	/*
	public void handleCancellation(WorkflowSession session ,Token existToken, Token sourceToken);
	*/
	
	
	/**
	 * 处理中止操作,
	 * @param session
	 * @param listenerToken
	 * @param sourceToken
	 */
	public void handleTermination(WorkflowSession session ,Token listenerToken, Token sourceToken);
}
