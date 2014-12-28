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
package org.fireflow.simulation.behavior;

import java.util.ArrayList;
import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;
import org.fireflow.simulation.support.BreakPointContainer;

/**
 * @author 非也
 * @version 2.0
 */
public class TransitionBehavior implements WorkflowBehavior {
	private BreakPointContainer breakPointContainer = null;
	
	public BreakPointContainer getBreakPointContainer() {
		return breakPointContainer;
	}

	public void setBreakPointContainer(BreakPointContainer breakPointContainer) {
		this.breakPointContainer = breakPointContainer;
	}

	public CompensationHandler getCompensationHandler(String compensationCode){
		return null;
	}
	
	//（2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
//	public CancellationHandler getCancellationHandler(){
//		return null;
//	}
	
	public FaultHandler getFaultHandler(String errorCode){
		return null;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		Transition transition = (Transition)workflowElement;
		Node nextNode = transition.getToNode();
		List<PObjectKey> poKeys = new ArrayList<PObjectKey>();
		PObjectKey poKey = new PObjectKey(token.getProcessId(),token.getVersion(),token.getProcessType(),nextNode.getId());
		poKeys.add(poKey);
		
		ContinueDirection direction = ContinueDirection.closeMe();
		direction.setNextProcessObjectKeys(poKeys);
		return direction;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		ExecuteResult result = new ExecuteResult();

		if (this.breakPointContainer==null || 
				!this.breakPointContainer.isExist(token)){
			result.setStatus(BusinessStatus.COMPLETED);
		}else{
			//有断点，则返回Running状态
			result.setStatus(BusinessStatus.RUNNING);
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#prepare(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		return true;
	}

}
