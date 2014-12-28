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
package org.fireflow.pvm.kernel.impl;

import java.util.List;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.pvm.kernel.ArcInstance;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.NodeInstance;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class ArcInstanceImpl extends AbstractPObject implements
		ArcInstance {
	protected NodeInstance fromNodeInstance = null;
	protected NodeInstance toNodeInstance = null;

	public ArcInstanceImpl(PObjectKey key){
		super(key);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pvm.kernel.ArcInstance#getFrom()
	 */
//	@Override
	public NodeInstance getFrom() {
		return fromNodeInstance;
	}

	public void setFrom(NodeInstance from) {
		this.fromNodeInstance = from;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pvm.kernel.ArcInstance#getTo()
	 */
//	@Override
	public NodeInstance getTo() {
		return toNodeInstance;
	}

	public void setTo(NodeInstance to) {
		this.toNodeInstance = to;
	}

	public boolean isCancellable(){
		return false;
	}
	
	public boolean isAcceptCompensation(Token token,String compensationCode){
		return false;
	}

	public boolean isCompensable(){
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.fireflow.pvm.kernel.ProcessObject#takeToken(org.fireflow.engine.
	 * WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	@Override
	public void takeToken(WorkflowSession session, Token token, Token sourceToken) {
		if (!token.getState().equals(TokenState.INITIALIZED)) {
			throw new KernelException(this,
					"Illegal token state ,the TokenState.INITIALIZED is expected,but it is  "
							+ token.getState().name());
		}
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
		.getRuntimeContext();
		KernelManager kernelManager = ctx
		.getDefaultEngineModule(KernelManager.class);
		
		WorkflowBehavior behavior = this.getWorkflowBehavior();

		token.setState(TokenState.RUNNING);
		kernelManager.saveOrUpdateToken(token);
		behavior.onTokenStateChanged(session, token, this.getWorkflowElement());
		

		// ArcInstance上的behavior一般功能是计算token.alive的值和token.value的值。
		ExecuteResult result = behavior.execute(session, token, this
				.getWorkflowElement());
		BusinessStatus status = result.getStatus();

		
		if (status.equals(BusinessStatus.COMPLETED)) {
			BookMark bookMark = new BookMark();
			bookMark.setToken(token);
			bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);


			kernelManager.addBookMark(bookMark);
		} 
		else if (status.equals(BusinessStatus.RUNNING)){
			//调试状态
			return;
		}
		
		else {
			// 不支持其他类型的Status
			throw new KernelException(this,
					"Not supported workflow behavior status for arc instance ,the WorkflowBehaviorStatus.COMPLETED is expected ,but the status is "
							+ status.name());
		}
			
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#forwardToken(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	@Override
	public void forwardToken(WorkflowSession session, Token token, Token sourceToken) {
		this.complete(session, token);
	}

	public void complete(WorkflowSession session, Token token) {

		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = runtimeContext
				.getDefaultEngineModule(KernelManager.class);

		ContinueDirection continueDirection = behavior.continueOn(session,
				token, this.getWorkflowElement());

		// 执行后继结点
		if (continueDirection.getDirection() == ContinueDirection.CLOSE_ME) {

			List<PObjectKey> keys = continueDirection
					.getNextProcessObjectKeys();
			if (keys != null && keys.size() > 0) {
				// 启动新的节点
				for (PObjectKey key : keys) {

					Token newToken = new TokenImpl(token);
					newToken.setElementId(key.getWorkflowElementId());
					BookMark bookMark = new BookMark();
					bookMark.setToken(newToken);
					bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
					kernelManager.addBookMark(bookMark);
				}
			}
		} else {
			throw new KernelException(this,
					"Unsupported ContinueDirection type for this element, whose id is ["
							+ this.getKey().toString() + "]");
		}
		token.setState(TokenState.COMPLETED);
		kernelManager.saveOrUpdateToken(token);
		WorkflowBehavior behavior = this.getWorkflowBehavior();
		behavior.onTokenStateChanged(session, token, this.getWorkflowElement());
	}

	/*
	 * (non-Javadoc)
	 * （2012-02-03，该动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#handleCancellation(org.fireflow
	 * .engine.WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	/*
	public void handleCancellation(WorkflowSession session, Token existToken, Token sourceToken) {
		// TODO Auto-generated method stub

	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#handleCompensation(org.fireflow
	 * .engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.String)
	 */
	public void handleCompensation(WorkflowSession session, Token existToken,
			Token sourceToken, String compensationCode) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#handleFault(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.String)
	 */
	@Override
	public void handleFault(WorkflowSession session, Token existToken,
			Token sourceToken, String faultCode) {
		// TODO Auto-generated method stub

	}
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.PObject#handleTermination(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, org.fireflow.pvm.kernel.Token)
	 */
	@Override
	public void handleTermination(WorkflowSession session, Token thisToken,
			Token sourceToken) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
		.getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		thisToken.setState(TokenState.ABORTED);
		kernelManager.saveOrUpdateToken(thisToken);
		WorkflowBehavior behavior = this.getWorkflowBehavior();
		behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
		
		//触发父节点的forword
		Token parentToken = kernelManager.getParentToken(thisToken);

		PObject parent = null;
		if (parentToken != null) {
			parent = kernelManager.getProcessObject(parentToken);
		}
		BookMark bookMark = new BookMark();
		bookMark.setToken(parentToken);
		bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
		kernelManager.addBookMark(bookMark);
		
		
	}

}
