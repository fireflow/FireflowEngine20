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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.NetInstance;
import org.fireflow.pvm.kernel.NodeInstance;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.CancellationHandler;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public class NetInstanceImpl extends AbstractPObject implements
		NetInstance {
	protected NodeInstance parentNodeInstance = null;

	
	public NetInstanceImpl(PObjectKey key){
		super(key);
	}
	//////////////////////////////////////////////////////////////////
	///////////////    结构逻辑           ///////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NetInstance#getParentNodeInstance()
	 */
//	@Override
//	public NodeInstance getParentNodeInstance() {
//		return parentNodeInstance;
//	}
//	
//	public void setParentNodeInstance(NodeInstance nodeInst){
//		this.parentNodeInstance = nodeInst;
//	}
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NetInstance#getCancellationHandle()
	 */

	public boolean isCancellable(){
		return true;
	}
	
	

	public boolean isCompensable(){
		return true;
	}

	//////////////////////////////////////////////////////////////////
	///////////////    行为逻辑           ///////////////////////////////////
	/////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NetInstance#catchFault(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	public void catchFault(WorkflowSession session, Token token,ExecuteResult result) {
		String faultCode = result.getErrorCode();
		
		BookMark bookMark = new BookMark();
		bookMark.setToken(token);
		bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_FAULT);
		bookMark.setExtraArg(BookMark.ERROR_CODE, faultCode);
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.addBookMark(bookMark);

	}


	/* (non-Javadoc)
	 * （2012-02-03，该动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	 * @see org.fireflow.pvm.kernel.NetInstance#handleCancellation(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	/*
	public void handleCancellation(WorkflowSession session, Token existToken, Token sourceToken) {
		if (!existToken.getState().equals(TokenState.RUNNING) || !existToken.getState().equals(TokenState.FAULTING)){
			throw new KernelException(
					"Illegal token state ,the TokenState.RUNNING or TokenState.FAULTING is expected,but it is  "
							+ existToken.getState().name());
		}

		//1、对已经完成的子token执行补偿操作
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = runtimeContext.getDefaultEngineModule(KernelManager.class);
		
		boolean cancellationDone = true;//取消操作已经完成
		
		List<Token> children = kernelManager.getChildren4Compensation(existToken);
		for (Token childToken:children){
			if (childToken.getState().equals(TokenState.COMPLETED)){
				PObject tmp = kernelManager.getProcessObject(childToken);
				if (tmp.isCompensable()) {
					BookMark bookMark = new BookMark();
					bookMark.setToken(childToken);
					bookMark
							.setExecutionEntrance(ExecutionEntrance.HANDLE_COMPENSATION);
					bookMark.setExtraArg(BookMark.COMPENSATION_CODE, null);
					kernelManager.addBookMark(bookMark);
					cancellationDone = false;
				}
			}
		}	

		//2、执行取消操作
		PObject processObject = this.getCancellationHandler();
		if (processObject!=null){

			Token newToken = new TokenImpl(existToken);
			newToken.setElementId(processObject.getKey().getWorkflowElementId());
			newToken.setBusinessPermitted(true);
			newToken.setCallbackTokenId(existToken.getId());
			newToken.setOperationContextName(OperationContextName.CANCELLATION);
			
			BookMark bookMark = new BookMark();
			bookMark.setToken(newToken);
			bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
			kernelManager.addBookMark(bookMark);
			cancellationDone = false;

		}else{
			
			CancellationHandler cancellationHandler = this.getWorkflowBehavior().getCancellationHandler();
			if (cancellationHandler!=null){
				cancellationHandler.handleCancellation(session, existToken, this.getWorkflowElement());
			}else{
				children = kernelManager.getChildren(existToken);
				for (Token childToken:children){
					if (childToken.getState().equals(TokenState.RUNNING)||childToken.getState().equals(TokenState.FAULTING)){
						PObject tmp = kernelManager.getProcessObject(childToken);
						if (tmp.isCancellable()){
							BookMark bookMark = new BookMark();
							bookMark.setToken(childToken);
							bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_CANCELLATION);
							kernelManager.addBookMark(bookMark);
							cancellationDone = false;
						}

					}
				}		
			}
			
		}
		
		//3、设置取消状态
		if (cancellationDone){
			existToken.setState(TokenState.CANCELLED);
			kernelManager.saveOrUpdateToken(existToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, existToken, this.getWorkflowElement());
		}else{
			existToken.setState(TokenState.CANCELLING);
			kernelManager.saveOrUpdateToken(existToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, existToken, this.getWorkflowElement());
		}
	}
	*/


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.NetInstance#handleCompensation(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.String)
	 */
	public void handleCompensation(WorkflowSession session, Token listenerToken, Token sourceToken, String compensationCode) {
		if (!listenerToken.getState().equals(TokenState.COMPLETED) && !listenerToken.getState().equals(TokenState.RUNNING)
				&& !listenerToken.getState().equals(TokenState.COMPENSATING)){
			throw new KernelException(this,
					"Illegal token state ,the TokenState.COMPLETED or TokenState.RUNNING is expected,but it is  "
							+ listenerToken.getState().name());
		}

		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = runtimeContext.getDefaultEngineModule(KernelManager.class);
		
		boolean compensationDone = true;//补偿操作是否已经完成
		
		//1、首先对正在运行的正常（operationContextName=NORMAL)的子token执行Abort操作
		List<Token> children = kernelManager.getChildren(listenerToken);
		for (Token token:children){
			if ((token.getState().equals(TokenState.INITIALIZED) 
					|| token.getState().equals(TokenState.RUNNING))
					&& token.getOperationContextName().equals(OperationContextName.NORMAL)){
				PObject tmp = kernelManager.getProcessObject(token);
				if (tmp.isCancellable()) {
					BookMark bookMark = new BookMark();
					bookMark.setToken(token);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
					bookMark
							.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);
					kernelManager.addBookMark(bookMark);
					
					compensationDone = false;
				}
			}
		}

		//2、然后执行补偿操作
		PObject processObject = null;
		if (compensationCode==null || compensationCode.trim().equals("")){
			processObject = this.defaultCompensationHandler;
		}else{
			processObject = this.getCompensationHandler(compensationCode);
		}
		
		if (processObject!=null){
			Token newToken = new TokenImpl(sourceToken);
			newToken.setElementId(processObject.getKey().getWorkflowElementId());
			newToken.setBusinessPermitted(true);
			newToken.setCallbackTokenId(listenerToken.getId());			
			newToken.setOperationContextName(OperationContextName.COMPENSATION);
			
			BookMark bookMark = new BookMark();
			bookMark.setToken(newToken);
			bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
			bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
			kernelManager.addBookMark(bookMark);
			
			compensationDone = false;
			
		}else{
			
			CompensationHandler compensationHandler = this.getWorkflowBehavior().getCompensationHandler(compensationCode);
			if (compensationHandler!=null){
				compensationHandler.handleCompensation(session, listenerToken, this.getWorkflowElement(), compensationCode);
			} else {
				//TODO 循环的情况难道要做多次补偿？
				//2011-1-16，如果牵涉到补偿，流程场景不应该有循环，所以该处处理方案问题不大。
				children = kernelManager.getChildren4Compensation(listenerToken);
				if (children != null) {
					boolean compensationDone2 = _handleCompensation(kernelManager,listenerToken,children,compensationCode,sourceToken);
					compensationDone = compensationDone && compensationDone2;
				}
			}
		}
		
		//3、进入补偿状态
		//2012-2-3，对于netinstance而言，应该不关心compensationDone的值，直接设置为COMPENSATING，而不需要设置为COMPENSATED
		/**********
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
		********/
		
		listenerToken.setState(TokenState.COMPENSATING);
		kernelManager.saveOrUpdateToken(listenerToken);
		WorkflowBehavior behavior = this.getWorkflowBehavior();
		behavior.onTokenStateChanged(session, listenerToken, this.getWorkflowElement());
	}
	

	
	public boolean isAcceptCompensation(Token token,String compensationCode){
		if (this.isCompensable()){
			return true;
		}else{
			return false;
		}
	}
}
