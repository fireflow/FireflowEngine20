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

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ProcessInstance;

/**
 * @author 非也
 * @version 2.0
 */
public interface KernelManager extends EngineModule{
	public void loadProcess(ProcessKey pk );

	/**
	 * 
	 * @param session
	 * @param childPOjbectKey
	 * @param parentToken
	 * @param childProcessInstance 启动ProcessInstance类型的PObject时，需要传入ProcessInstance实例
	 */
	public void startPObject(WorkflowSession session,PObjectKey childPOjbectKey,
			Token parentToken,ProcessInstance childProcessInstance);
	
	
	/**
	 * 在listenerToken上触发一个中止事件
	 * @param session
	 * @param listenerToken
	 * @param sourceToken
	 */
	public void fireTerminationEvent(WorkflowSession session,Token listenerToken,Token sourceToken);
	
	/**
	 * 在listenerToken上触发一个补偿事件。
	 * @param session
	 * @param listenerToken 处理补偿的token
	 * @param sourceToken 产生补偿事件的token
	 * @param compensationCode 补偿Id
	 */
	public void fireCompensationEvent(WorkflowSession session,Token listenerToken,Token sourceToken,List<String> compensationCodes);
	
	/**
	 * 在listenerToken上触发一个异常事件
	 * @param session
	 * @param listenerToken
	 * @param sourceToken
	 * @param errorCode
	 */
	public void fireFaultEvent(WorkflowSession session,Token listenerToken,Token sourceToken, String errorCode);
	
	/**
	 * 将书签加入到队列中。
	 * @param bookMark
	 */
	public void addBookMark(BookMark bookMark);
	
	/**
	 * 执行bookmark队列中的书签。
	 * @param session
	 */
	public void execute(WorkflowSession session);
	/**
	 * 检查是否有子token存在于bookmark队列中。
	 * @param token
	 * @return
	 */
	public boolean hasChildrenInQueue(Token token);
	/**
	 * 根据TokenId和流程类别获取Token对象。
	 * @param tokenId
	 * @param processType FPDL ,BPMN,BPEL等
	 * @return
	 */
	public Token getTokenById(String tokenId,String processType);
	
	public Token getTokenByElementInstanceId(String elementInstanceId,String processType);
	
	/**
	 * 获得父Token
	 * @param currentToken
	 * @return
	 */
	public Token getParentToken(Token currentToken);
	
	/**
	 * 获得token对应的processObject
	 * @param token
	 * @return
	 */
	public PObject getProcessObject(Token token);
	
	public Object getWorkflowElement(PObjectKey key);
	
	/**
	 * 获得子token列表。
	 * @param token
	 * @return
	 */
	public List<Token> getChildren(Token token);
	

	
	/**
	 * 找出需要执行补偿操作的子节点。<br/>
	 * @param token
	 * @return
	 */
	public List<Token> getChildren4Compensation(Token token);
	
	/**
	 * 根据ProcessObjectKey获得ProcessObject对象。
	 * @param key
	 * @return
	 */
	public PObject getProcessObject(PObjectKey key);
	
	public void saveOrUpdateToken(Token token);
	
	/**
	 * 将缓存的PObject清除
	 */
	public void clearCachedPObject();
}
