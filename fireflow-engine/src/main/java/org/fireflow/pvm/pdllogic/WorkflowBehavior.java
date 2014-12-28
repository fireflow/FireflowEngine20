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
package org.fireflow.pvm.pdllogic;

import org.fireflow.client.WorkflowSession;
import org.fireflow.pvm.kernel.Token;

/**
 * @author 非也
 * @version 2.0
 */
public interface WorkflowBehavior {
	public Boolean prepare(WorkflowSession session,Token token,Object workflowElement);
	
	public ExecuteResult execute(WorkflowSession session,Token token, Object workflowElement);
		
	public ContinueDirection continueOn(WorkflowSession session,Token token, Object workflowElement);
	
	/**
	 * 中止, TODO 用abort还是terminate呢？
	 * @param session
	 * @param token
	 * @param workflowElement
	 * 2012-02-03 废除该方法，该方法容易引起混乱
	 */
//	public void abort(WorkflowSession session,Token token, Object workflowElement);

	public CompensationHandler getCompensationHandler(String compensationCode);
	
	/* （2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	public CancellationHandler getCancellationHandler();
	*/
	
	public FaultHandler getFaultHandler(String errorCode);
	
	public void onTokenStateChanged(WorkflowSession session,Token token, Object workflowElement);
	
}
