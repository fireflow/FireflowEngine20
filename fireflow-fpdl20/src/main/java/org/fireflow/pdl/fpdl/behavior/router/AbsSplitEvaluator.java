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
package org.fireflow.pdl.fpdl.behavior.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.data.Expression;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pvm.kernel.Token;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public abstract class AbsSplitEvaluator implements SplitEvaluator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pdl.fpdl.behavior.router.SplitEvaluator#
	 * determineNextTransitions(org.fireflow.client.WorkflowSession,
	 * org.fireflow.pvm.kernel.Token, org.fireflow.pdl.fpdl.process.Node)
	 */
	public List<String> determineNextTransitions(WorkflowSession session,
			Token token4Node, Node node) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl) session;

		Map<String, Object> fireflowVariableContext = ScriptEngineHelper
				.fulfillScriptContext(session, ctx,
						sessionLocal.getCurrentProcessInstance(),
						sessionLocal.getCurrentActivityInstance());

		List<String> result = new ArrayList<String>();

		List<Transition> leavingTransitions = node.getLeavingTransitions();
		Transition defaultTransition = null;
		if (leavingTransitions != null) {
			for (Transition transition : leavingTransitions) {
				if (transition.isDefault()) {
					defaultTransition = transition;
					continue;
				}
				
				if (canBeFired(transition,session,ctx,fireflowVariableContext)){
					result.add(transition.getId());
				}

			}
		}

		if (result.size() == 0) {
			if (defaultTransition != null) {
				result.add(defaultTransition.getId());
			}
		}
		return result;
	}
	
	/**
	 * 一般情况下，自定义分支评价器只要覆盖该方法即可
	 * @param transition
	 * @param session
	 * @param rtCtx
	 * @param scriptEngineVariableContext
	 * @return
	 */
	protected boolean canBeFired(Transition transition,WorkflowSession session,
			RuntimeContext rtCtx,Map<String, Object> scriptEngineVariableContext){
		boolean b = true;//默认是可以执行的
		Expression expression = transition.getCondition();
		if (expression != null && expression.getBody() != null
				&& !expression.getBody().trim().equals("")) {

			Object obj = ScriptEngineHelper.evaluateExpression(rtCtx,
					expression, scriptEngineVariableContext);
			if (obj instanceof Boolean) {
				b = ((Boolean) obj).booleanValue();
			}

		}

		return b;
	}

}
