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
package org.fireflow.pdl.bpel.structure;

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.pdl.bpel.BpelActivity;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;

/**
 * @author 非也
 * @version 2.0
 */
public class Sequence extends StructureActivity  {
	public Sequence(String name){
		super(name);
	}
	
	public Sequence addChild(BpelActivity bpelActivity){
		bpelActivity.setParent(this);
		Child childActivity = new Child(null,bpelActivity);
		this.addChild(childActivity);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token token,
			Object workflowElement) {
		if (this.getChildren()==null || this.getChildren().size()==0){
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		
		List<Token> closedChildTokens  = kernelManager.getChildren(token);
		boolean isRunning = false;
		for (Child child:this.getChildren()){
			if (!this.hasBeenExecuted(child.getChildBpelActivity(), closedChildTokens)){
				isRunning = true;
				this.executeChildActivity(session, token, child.getChildBpelActivity());
				break;
			}
		}
		if (isRunning){
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.RUNNING);
			return result;
		}else{
			ExecuteResult result = new ExecuteResult();
			result.setStatus(BusinessStatus.COMPLETED);
			return result;
		}
	}
	
	public boolean hasBeenExecuted(BpelActivity activity,List<Token> closedChildTokens){
		if (closedChildTokens==null || closedChildTokens.size()==0){
			return false;
		}
		for (Token childToken:closedChildTokens){
			if (childToken.getElementId().equals(activity.getId())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ContinueDirection continueOn(WorkflowSession session, Token token,
			Object workflowElement) {
		ContinueDirection result = ContinueDirection.closeMe();
		
		if (this.getChildren()==null || this.getChildren().size()==0){
			result = ContinueDirection.closeMe();
		}else{
			RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
			KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
			
			List<Token> closedChildTokens  = kernelManager.getChildren(token);
			if (closedChildTokens!=null){
				if (closedChildTokens.size()>=this.getChildren().size()){
					result = ContinueDirection.closeMe();
				}else{
					result = ContinueDirection.runAgain();
				}
			}else{
				result = ContinueDirection.runAgain();
			}
		}
		
				
		if (result.getDirection()==ContinueDirection.closeMe().getDirection()){
			int level = getLevel();
			for (int i=0;i<(level+1);i++){
				System.out.print("    ");//打印空格
			}
			System.out.println(this.getName()+" completed!");
		}
		return result ;
	}
}
