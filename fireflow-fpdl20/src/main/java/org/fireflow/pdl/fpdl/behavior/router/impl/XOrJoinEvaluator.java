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
import org.fireflow.pdl.fpdl.behavior.router.JoinEvaluator;
import org.fireflow.pdl.fpdl.process.Synchronizer;
import org.fireflow.pvm.kernel.Token;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class XOrJoinEvaluator implements JoinEvaluator {
	public static final String JOIN_DESCRIPTION = "汇聚逻辑：任意输入Transition到达后立即执行后续分支。如果有N条输入Transition到达，则后续分支被执行N次";
	public String getJoinDescription(){
		return JOIN_DESCRIPTION;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pdl.fpdl.behavior.router.JoinEvaluator#canBeFired(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, org.fireflow.pdl.fpdl.process.Synchronizer)
	 */
	public int canBeFired(WorkflowSession session, Token current_token_for_router,
			List<Token> siblingTokens, Synchronizer router) {
		return current_token_for_router.getStepNumber();//任何一条输入都可以触发
	}

}
