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
package org.fireflow.pdl.fpdl.behavior.router.impl;

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pdl.fpdl.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Synchronizer;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class AndJoinEvaluator implements JoinEvaluator {
	public static final String JOIN_DESCRIPTION = "汇聚逻辑：当所有输入Transition都到达后才执行后续分支。";
	public String getJoinDescription(){
		return JOIN_DESCRIPTION;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pdl.fpdl.behavior.router.JoinEvaluator#canBeFired(org.
	 * fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token,
	 * org.fireflow.pdl.fpdl.process.Synchronizer)
	 */
	public int canBeFired(WorkflowSession session, Token current_token_for_router,
			List<Token> siblingTokens, Synchronizer node) {
		List<Transition> enteringTransitionsList = node
				.getEnteringTransitions();
		// 仅有一条边的情况下，直接返回true
		if (enteringTransitionsList == null
				|| enteringTransitionsList.size() == 0
				|| enteringTransitionsList.size() == 1) {
			return current_token_for_router.getStepNumber();
		}

		//如果有多条边的情况，则
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(
				PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		TokenPersister tokenPersister = persistenceStrategy.getTokenPersister();
		
		if (siblingTokens==null || siblingTokens.size()==0){
			return -1;//显然非法，继续等待
		}
		
		if (siblingTokens.size()<enteringTransitionsList.size()){
			return -1;//还需等待汇聚
		}else{
			//表示汇聚完毕，返回stepnumber最大的值，其他的token置为completed状态
			Token tmpToken = current_token_for_router;
			for (Token sibling:siblingTokens){
				if (tmpToken.getStepNumber()>sibling.getStepNumber()){
					
				}else{
					Token t = tmpToken;
					tmpToken = sibling;
				}
				
				if (!sibling.getId().equals(current_token_for_router.getId())){
					sibling.setState(TokenState.COMPLETED);
				}
			}

			return tmpToken.getStepNumber();
		}
	}

}
