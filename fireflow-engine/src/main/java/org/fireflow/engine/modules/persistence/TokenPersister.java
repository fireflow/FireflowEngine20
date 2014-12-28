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
package org.fireflow.engine.modules.persistence;

import java.util.List;

import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.Token;

/**
 * @author 非也
 * @version 2.0
 */
public interface TokenPersister extends Persister {
	/**
	 * 删除所有的Token，该方法给Simulator用，业务系统一般用不上。
	 */
	public void deleteAllTokens();
	public Token findParentToken(Token token);
	public List<Token> findChildTokens(Token token);
	/**
	 * 查询出状态为COMPLETED的，且OperationContextName为Normal的token，结果集按照stepNumber倒序排列
	 * @param token
	 * @return
	 */
	public List<Token> findChildTokens4Compensation(Token token);
	
	/**
	 * 查询已经先期到达，正在等待汇聚的Token
	 * @param processInstanceId
	 * @param nodeId
	 * @param thisTokenId
	 * @param operationContextName
	 * @return
	 */
//	public Token findJoinPointToken(String processInstanceId,String nodeId,String thisTokenId,OperationContextName operationContextName);
	
	public int countAliveToken(String processInstanceId,String nodeId,OperationContextName operationContextName);
	
	/**
	 * Sibling判断标准<br/>
	 * 1)同一流程实例；<br/>
	 * 2)处于同一个个节点上；<br/>
	 * 3)opreationContext相同<br/>
	 * 4)处于活动状态，即state<delimiter。
	 * @param token
	 * @return
	 */
	public List<Token> findSiblings(Token token);
	
	public List<Token> findAttachedTokens(Token token);
	
	public Token findTokenByElementInstanceId(String elementInstanceId);

}
