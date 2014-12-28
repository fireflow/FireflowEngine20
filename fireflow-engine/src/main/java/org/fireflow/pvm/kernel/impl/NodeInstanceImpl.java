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
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.NodeInstance;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class NodeInstanceImpl extends AbstractPObject implements NodeInstance {
//	protected ArcInstance defaultLeavingArcInstance = null;
//	protected List<ArcInstance> leavingArcInstances = new ArrayList<ArcInstance>();
//	protected List<ArcInstance> enteringArcInstances = new ArrayList<ArcInstance>();
//	protected NodeInstance parentNodeInstance = null;
//	protected NetInstance  netInstance = null;

	protected NodeInstance parentNodeInstance = null;

	
	public NodeInstanceImpl(PObjectKey key){
		super(key);
	}
	//////////////////////////////////////////////////////////////////
	///////////////    结构逻辑           ///////////////////////////////////
	/////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NodeInstance#getEnteringArcInstances()
	 */
//	@Override
//	public List<ArcInstance> getEnteringArcInstances() {
//		return enteringArcInstances;
//	}
//	
//	public void addEnteringArcInstance(ArcInstance arc){
//		this.enteringArcInstances.add(arc);
//	}


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NodeInstance#getLeavingArcInstances()
	 */
//	@Override
//	public List<ArcInstance> getLeavingArcInstances() {
//		return leavingArcInstances;
//	}
//
//	public void addLeavingArcInstance(ArcInstance arc){
//		this.leavingArcInstances.add(arc);
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NodeInstance#getDefaultLeavingArcInstance()
	 */
//	@Override
//	public ArcInstance getDefaultLeavingArcInstance() {
//		return this.defaultLeavingArcInstance;
//	}
//	public void setDefaultLeavingArcInstance(ArcInstance arc){
//		this.defaultLeavingArcInstance = arc;
//	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NodeInstance#getNetInstance()
	 */
//	@Override
//	public NetInstance getNetInstance() {
//		return this.netInstance;
//	}
//	
//	public void setNetInstance(NetInstance net){
//		this.netInstance = net;
//	}


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NodeInstance#getParentNodeInstance()
	 */
//	@Override
//	public NodeInstance getParentNodeInstance() {
//		return this.parentNodeInstance;
//	}
//	
//	public void setParentNodeInstance(NodeInstance node){
//		this.parentNodeInstance = node;
//	}

	public boolean isAcceptCompensation(Token token,String compensationCode){
		if (this.isCompensable()){
			if (token.isContainer()){
				return true;
			}else{
				if (compensationCode==null || compensationCode.trim().equals("")){
					if (this.defaultCompensationHandler!=null){
						return true;
					}
				}else{
					if (this.getCompensationHandler(compensationCode)!=null){
						return true;
					}
				}
			}
		}
		return false;
	}

	//////////////////////////////////////////////////////////////////
	///////////////    行为逻辑           ///////////////////////////////////
	/////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.ProcessObject#handlerCompensation(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	public void handleCompensation(WorkflowSession session, Token listenerToken,
			Token sourceToken, String compensationCode) {
		if (!this.isCompensable()) {
			return;
		}
		if (!listenerToken.getState().equals(TokenState.COMPLETED)) {
			throw new KernelException(this,
					"Illegal token state ,the TokenState.COMPLETED is expected,but it is  "
							+ listenerToken.getState().name());
		}
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = ctx
				.getDefaultEngineModule(KernelManager.class);

		boolean compensationDone = true;// 补偿操作是否已经完成

		PObject processObject = null;
		if (compensationCode == null || compensationCode.trim().equals("")) {
			processObject = this.defaultCompensationHandler;
		} else {
			processObject = this.getCompensationHandler(compensationCode);
		}

		if (processObject != null) {

			Token newToken = new TokenImpl(sourceToken);
			newToken
					.setElementId(processObject.getKey().getWorkflowElementId());
			newToken.setBusinessPermitted(true);
			newToken.setOperationContextName(OperationContextName.COMPENSATION);
			newToken.setCallbackTokenId(listenerToken.getId());

			BookMark bookMark = new BookMark();
			bookMark.setToken(newToken);
			bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
			bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
			kernelManager.addBookMark(bookMark);

			compensationDone = false;
		} else {

			CompensationHandler compensationHandler = this
					.getWorkflowBehavior().getCompensationHandler(
							compensationCode);

			if (compensationHandler != null) {
				compensationHandler.handleCompensation(session, listenerToken,
						this.getWorkflowElement(), compensationCode);
				compensationDone = true;
			}

			else {
				// 下面的逻辑主要是针对子流程，如果当前的Node 的service是子流程的话，需要将补偿操作传递到子流程里面去。
				if (listenerToken.isContainer()) {
					List<Token> children = kernelManager
							.getChildren4Compensation(listenerToken);
					if (children != null && children.size() > 0) {
						compensationDone = _handleCompensation(kernelManager,
								listenerToken, children, compensationCode,
								sourceToken);

					}
				}
			}
		}
		
		if (compensationDone){
			listenerToken.setState(TokenState.COMPENSATED);
			kernelManager.saveOrUpdateToken(listenerToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, listenerToken, this
					.getWorkflowElement());

			// 通知父token（一般是父流程中的某个活动），使之从Compensating状态转移到compensated状态
			Token parentToken = kernelManager.getParentToken(listenerToken);
			if (parentToken != null) {
				PObject parent = null;
				parent = kernelManager.getProcessObject(parentToken);
				if (parent != null) {

					BookMark bookMark = new BookMark();
					bookMark.setToken(parentToken);
					bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, listenerToken);
					kernelManager.addBookMark(bookMark);

				}
			}
			
		}else{
			listenerToken.setState(TokenState.COMPENSATING);
			kernelManager.saveOrUpdateToken(listenerToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, listenerToken, this.getWorkflowElement());
			
		}
	}

	/**
	 * （2012-02-05，该动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	 * @param session
	 * @param thisToken
	 * @param sourceToken
	 */
	/*
	public void handleCancellation(WorkflowSession session, Token thisToken, Token sourceToken) {
		if (!this.isCancellable()){
			return;
		}
		if (!thisToken.getState().equals(TokenState.INITIALIZED) && !thisToken.getState().equals(TokenState.RUNNING)){
			throw new KernelException(
					"Illegal token state ,the TokenState.INITIALIZED or TokenState.RUNNING is expected,but it is  "
							+ thisToken.getState().name());
		}
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);

		PObject processObject = this.getCancellationHandler();
		if (processObject != null) {

			Token newToken = new TokenImpl(thisToken);
			newToken.setElementId(processObject.getKey().getWorkflowElementId());
			newToken.setBusinessPermitted(true);
			newToken.setCallbackTokenId(thisToken.getId());
			
			BookMark bookMark = new BookMark();
			bookMark.setToken(newToken);
			bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
			kernelManager.addBookMark(bookMark);

			
			thisToken.setState(TokenState.CANCELLING);
			kernelManager.saveOrUpdateToken(thisToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
			
			
		}else{
			CancellationHandler cancellationHandler = this.getWorkflowBehavior().getCancellationHandler();
			if (cancellationHandler!=null){
				cancellationHandler.handleCancellation(session, thisToken, this.getWorkflowElement());
				
				thisToken.setState(TokenState.CANCELLED);
				kernelManager.saveOrUpdateToken(thisToken);
				WorkflowBehavior behavior = this.getWorkflowBehavior();
				behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
				
				Token parentToken = kernelManager.getParentToken(thisToken);				
				PObject parent = null;
				if (parentToken!=null){
					parent = kernelManager.getProcessObject(parentToken);
				}
				
				if (parent!=null){					
					BookMark bookMark = new BookMark();
					bookMark.setToken(parentToken);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
					bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
					kernelManager.addBookMark(bookMark);

				}
			}else{
				List<Token> children = kernelManager.getChildren(thisToken);
				if (children!=null && children.size()>0){
					for (Token childToken:children){
						if ((childToken.getState().equals(TokenState.INITIALIZED) 
								|| childToken.getState().equals(TokenState.RUNNING))
								&& childToken.getOperationContextName().equals(OperationContextName.NORMAL)){
							
							BookMark bookMark = new BookMark();
							bookMark.setToken(childToken);
							bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
							bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_CANCELLATION);
							kernelManager.addBookMark(bookMark);
						}
					}	
					thisToken.setState(TokenState.CANCELLING);
					kernelManager.saveOrUpdateToken(thisToken);
					WorkflowBehavior behavior = this.getWorkflowBehavior();
					behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
									
				}
			}
		}
	}
	*/

	
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.ProcessObject#complete(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
//	protected void complete(WorkflowSession session, Token token) {
//
//		List<ArcInstance> leavingArcs = this.getLeavingArcInstances();
//		if (leavingArcs != null && leavingArcs.size() > 0) {
//			List<Token> newLeftTokens = new ArrayList<Token>();// 输出边上的token列表
//			for (ArcInstance arc : leavingArcs) {
//				if (this.defaultLeavingArcInstance != null
//						&& !arc.equals(this.defaultLeavingArcInstance)) {
//					Token newToken = new TokenImpl(token);// 产生新的token
//					newToken.setWorkflowElementId(arc.getKey()
//							.getWorkflowElementId());
//
//					arc.takeToken(session, newToken);
//
//					newLeftTokens.add(newToken);
//				}
//			}
//			if (defaultLeavingArcInstance != null) {
//				if (token.isAlive()) {
//					Token newToken = new TokenImpl(token);// 产生新的token
//					newToken.setWorkflowElementId(defaultLeavingArcInstance
//							.getKey().getWorkflowElementId());
//					for (Token _tmpToken : newLeftTokens) {
//						if (_tmpToken.isAlive()) {
//							newToken.setAlive(false);
//							break;
//						}
//					}
//					defaultLeavingArcInstance.takeToken(session, newToken);
//					newLeftTokens.add(newToken);
//				} else {
//					Token newToken = new TokenImpl(token);// 产生新的token
//					newToken.setWorkflowElementId(defaultLeavingArcInstance
//							.getKey().getWorkflowElementId());
//
//					defaultLeavingArcInstance.takeToken(session, newToken);
//					newLeftTokens.add(newToken);
//				}
//			}
//		} else if (this.parentNodeInstance != null) {
//			// 获得ParentNodeInstance对应的Token
//			Token parentToken = null;
//			this.parentNodeInstance.forwardToken(session, parentToken);
//		}
//		// 检查是否有callback processObject
//
//		// 最后交给NetInstance
//		else if (this.netInstance != null) {
//			Token netInstanceToken = null;
//			this.netInstance.forwardToken(session, netInstanceToken);
//		}
//
//	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NodeInstance#catchFault(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	public void catchFault(WorkflowSession session, Token token,
			ExecuteResult behaviorResult) {
		String faultCode = behaviorResult.getErrorCode();

		BookMark bookMark = new BookMark();
		bookMark.setToken(token);
		bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
		bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_FAULT);
		bookMark.setExtraArg(BookMark.ERROR_CODE, faultCode);
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.addBookMark(bookMark);

	}

}
