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

import java.util.ArrayList;
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
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 * @author 非也
 * @version 2.0
 */
public abstract class AbstractPObject implements PObject {
	protected PObjectKey key = null;
	protected Object workflowElement = null;
	protected WorkflowBehavior behavior = null;
	protected boolean acceptCancellation = false;
	protected boolean acceptCompensation = false;
//	protected PObject cancellationHandler = null;//（2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	protected Map<String,PObject> faultHandlers = new HashMap<String,PObject>();
	protected PObject defaultFaultHandler = null;
	protected Map<String,PObject> compensationHandlers = new HashMap<String,PObject>();
	protected PObject defaultCompensationHandler = null;
	
	public AbstractPObject(PObjectKey key) {
		this.key = key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pvm.kernel.ProcessObject#getKey()
	 */
	public PObjectKey getKey() {
		return key;
	}

	public void setKey(PObjectKey key) {
		this.key = key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pvm.kernel.ProcessObject#getWorkflowBehavior()
	 */
	public WorkflowBehavior getWorkflowBehavior() {
		return this.behavior;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.pvm.kernel.ProcessObject#getWorkflowElement()
	 */
	public Object getWorkflowElement() {
		return this.workflowElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#setWorkflowBehavior(org.fireflow
	 * .pvm.pdllogic.WorkflowBehavior)
	 */
	public void setWorkflowBehavior(WorkflowBehavior processBehavior) {
		this.behavior = processBehavior;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#setWorkflowElement(java.lang.Object
	 * )
	 */
	public void setWorkflowElement(Object wfElement) {
		this.workflowElement = wfElement;

	}

	public boolean isCancellable() {
		return this.acceptCancellation;
	}

	public void setCancellable(Boolean b) {
		this.acceptCancellation = b;
	}

	public boolean isCompensable() {
		return this.acceptCompensation;
	}

	public void setCompensable(Boolean b) {
		this.acceptCompensation = b;
	}

	/* （2012-02-05，Cancel动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
	public PObject getCancellationHandler() {
		return cancellationHandler;
	}
	public void setCancellationHandler(PObject pobject){
		this.cancellationHandler = pobject;
	}
	*/


	/**
	 * 异常处理句柄
	 * @return
	 */
	public PObject getFaultHandler(String errorCode){
		return this.faultHandlers.get(errorCode);
	}
	
	public PObject getDefaultFaultHandler(){
		return this.defaultFaultHandler;
	}
	
	public void setFaultHandler(String errorCode,PObject handler){
		this.setFaultHandler(errorCode, handler, false);
	}
	
	public void setFaultHandler(String errorCode,PObject handler,boolean isDefaultFaultHandler){
		this.faultHandlers.put(errorCode, handler);
		if (isDefaultFaultHandler){
			this.defaultFaultHandler = handler;
		}
	}
	
	/**
	 * 补偿处理句柄
	 * @return
	 */
	public PObject getCompensationHandler(String compensationCode){
		return this.compensationHandlers.get(compensationCode);
	}
	public PObject getDefaultCompensationHandler(){
		return this.defaultCompensationHandler;
	}
	public void setCompensationHandler(String compensationCode,PObject handler){
		this.setCompensationHandler(compensationCode, handler, false);
	}
	public void setCompensationHandler(String compensationCode,PObject handler,boolean isDefaultCompensationHandler){
		this.compensationHandlers.put(compensationCode, handler);
		if (isDefaultCompensationHandler){
			this.defaultCompensationHandler = handler;
		}
	}
	
	// ////////////////////////////////////////////////////////////////
	// ///////////// 行为逻辑 ///////////////////////////////////
	// ///////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.fireflow.pvm.kernel.ProcessObject#takeToken(org.fireflow.engine.
	 * WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	public void takeToken(WorkflowSession session, Token token, Token sourceToken) {
		if (!token.getState().equals(TokenState.INITIALIZED)) {
			return;
			//在synchronizer汇聚的情况下，takeToken可能被执行多次，所以不能抛出异常。
//			throw new KernelException(
//					"Illegal token state ,the TokenState.INITIALIZED is expected,but it is  "
//							+ token.getState().name());
		}

		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = ctx
				.getDefaultEngineModule(KernelManager.class);

		WorkflowBehavior behavior = this.getWorkflowBehavior();
		Boolean canBeFired = behavior.prepare(session, token, this
				.getWorkflowElement());

		kernelManager.saveOrUpdateToken(token);
		if (!canBeFired) {
			return;// 继续等待
		}
		token.setState(TokenState.RUNNING);
		kernelManager.saveOrUpdateToken(token);
		 behavior.onTokenStateChanged(session, token,
		 this.getWorkflowElement());
		
		// 如果isAlive则执行业务逻辑
		if (token.isBusinessPermitted()) {
			executeBusinessLogicLogic(session, token);
		} else {
			this.forwardToken(session, token, token);
		}
	}

	protected void executeBusinessLogicLogic(WorkflowSession session,
			Token token) {
		if (!token.getState().equals(TokenState.RUNNING)) {
			throw new KernelException(this,
					"Illegal token state ,the TokenState.RUNNING is expected,but it is  "
							+ token.getState().name());
		}
		ExecuteResult result = behavior.execute(session, token, this
				.getWorkflowElement());
		BusinessStatus status = result.getStatus();
		if (status.equals(BusinessStatus.RUNNING)) {
			// 正在运行中（一般是长任务）
			return;
		} else if (status.equals(BusinessStatus.FAULTING)) {// 异常
			this.catchFault(session, token, result);
		}

		else if (status.equals(BusinessStatus.COMPLETED)) {// 已经结束

			BookMark bookMark = new BookMark();
			bookMark.setToken(token);
			bookMark.setExtraArg(BookMark.SOURCE_TOKEN, token);
			bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
			
			RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
					.getRuntimeContext();
			KernelManager kernelManager = ctx
					.getDefaultEngineModule(KernelManager.class);
			kernelManager.addBookMark(bookMark);

		} else {
			throw new KernelException(this,
					"Not supported workflow behavior status for NodeInstance.takeToken(...) ,"
							+ "WorkflowBehaviorStatus.RUNNING or WorkflowBehaviorStatus.FAULTING or status.equals(WorkflowBehaviorStatus.COMPLETED is expected,but the status is "
							+ status.name());// 非法的返回状态
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.pvm.kernel.ProcessObject#forwardToken(org.fireflow.engine
	 * .WorkflowSession, org.fireflow.pvm.kernel.Token)
	 */
	public void forwardToken(WorkflowSession session, Token token, Token sourceToken) {
		//TODO 2012-02-03 token的状态有可能被前一个bookmark修改过，导致与数据库中的真实状态不匹配，所以在非hibernate情况下要校验一致性。
		if (token.getState().getValue() > TokenState.DELIMITER.getValue()) {
			return;
		}
		RuntimeContext runtimeContext = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = runtimeContext
				.getDefaultEngineModule(KernelManager.class);


		if (token.getState() != null
				&& token.getState().equals(TokenState.RUNNING)) {
			_forwardRunningToken(session,kernelManager,token,sourceToken);
		} else if (token.getState() != null
				&& token.getState().equals(TokenState.FAULTING)) {
			
			Token parentToken = kernelManager.getParentToken(token);

			PObject parent = null;
			if (parentToken != null) {
				parent = kernelManager.getProcessObject(parentToken);
			}
			Token callbackToken = null;
			if (token.getCallbackTokenId() != null) {
				callbackToken = kernelManager.getTokenById(token.getCallbackTokenId(),
						token.getProcessType());
			}
			//不需要调用behavior.continueOn(session,	token, this.getWorkflowElement());
			//此种情况默认为ContinueDirection.CloseMe()
			if (callbackToken != null) {
				BookMark bookMark = new BookMark();
				bookMark.setToken(callbackToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
			} else if (parentToken != null && parent != null) {
				BookMark bookMark = new BookMark();
				bookMark.setToken(parentToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
			}
			if (!kernelManager.hasChildrenInQueue(token)) {
				
				token.setState(TokenState.FAULTED);
				kernelManager.saveOrUpdateToken(token);
				behavior.onTokenStateChanged(session, token, this
						.getWorkflowElement());
			}

		} else if (token.getState() != null
				&& token.getState().equals(TokenState.COMPENSATING)) {
			_forwardCompensatingToken(session,kernelManager,token,sourceToken);

		} else if (token.getState() != null
				&& token.getState().equals(TokenState.ABORTING)) {
			//不需要调用behavior.continueOn(session,	token, this.getWorkflowElement());
			//此种情况默认为ContinueDirection.CloseMe()
			Token parentToken = kernelManager.getParentToken(token);

			PObject parent = null;
			if (parentToken != null) {
				parent = kernelManager.getProcessObject(parentToken);
			}
			Token callbackToken = null;
			if (token.getCallbackTokenId() != null) {
				callbackToken = kernelManager.getTokenById(token.getCallbackTokenId(),
						token.getProcessType());
			}
			if (callbackToken != null) {
				BookMark bookMark = new BookMark();
				bookMark.setToken(callbackToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
			} else if (parentToken != null && parent != null) {
				BookMark bookMark = new BookMark();
				bookMark.setToken(parentToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
			}

			if (!kernelManager.hasChildrenInQueue(token)) {
				token.setState(TokenState.ABORTED);
				kernelManager.saveOrUpdateToken(token);
				behavior.onTokenStateChanged(session, token, this
						.getWorkflowElement());
			}

		}

		else {
			throw new KernelException(this,
					"Illegal token state ,the TokenState.RUNNING or TokenState.COMPENSATING or TokenState.CANCELLING is expected,but it is  "
							+ token.getState().name());
		}

	}
	
	

	protected void catchFault(WorkflowSession session, Token token,
			ExecuteResult behaviorResult) {

	}
	
	protected void _forwardRunningToken(WorkflowSession session ,KernelManager kernelManager,Token thisToken,Token sourceToken){
	
		Token callbackToken = null;
		if (thisToken.getCallbackTokenId() != null) {
			callbackToken = kernelManager.getTokenById(thisToken.getCallbackTokenId(),
					thisToken.getProcessType());
		}
		
		WorkflowBehavior behavior = this.getWorkflowBehavior();
		ContinueDirection continueDirection = behavior.continueOn(session,
				thisToken, this.getWorkflowElement());
		
		if (continueDirection.getDirection() == ContinueDirection.WAITING_FOR_CLOSE) {
			return;
		} else if (continueDirection.getDirection() == ContinueDirection.RUN_AGAIN) {
			this.executeBusinessLogicLogic(session, thisToken);
		} else if (continueDirection.getDirection() == ContinueDirection.CLOSE_ME) {
			if (continueDirection.getNextProcessObjectKeys() == null
					|| continueDirection.getNextProcessObjectKeys().size() == 0) {
				
				Token parentToken = kernelManager.getParentToken(thisToken);
				PObject parent = kernelManager.getProcessObject(parentToken);
				
				if (callbackToken!=null){
					BookMark bookMark = new BookMark();
					bookMark.setToken(callbackToken);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
					bookMark
							.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
					kernelManager.addBookMark(bookMark);
				}
				else if (parentToken != null && parent != null) {
					BookMark bookMark = new BookMark();
					bookMark.setToken(parentToken);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
					bookMark
							.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
					kernelManager.addBookMark(bookMark);
				}
				if (!kernelManager.hasChildrenInQueue(thisToken)) {
					thisToken.setState(TokenState.COMPLETED);
					kernelManager.saveOrUpdateToken(thisToken);
					behavior.onTokenStateChanged(session, thisToken, this
					 .getWorkflowElement());
				}
			} else {
				List<PObjectKey> keys = continueDirection
						.getNextProcessObjectKeys();
				if (keys != null && keys.size() > 0) {
					// 启动新的节点
					for (PObjectKey key : keys) {
						Token newToken = new TokenImpl(thisToken);
						newToken.setElementId(key.getWorkflowElementId());

						BookMark bookMark = new BookMark();
						bookMark.setToken(newToken);
						bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
						bookMark
								.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
						kernelManager.addBookMark(bookMark);
					}
				}
				thisToken.setState(TokenState.COMPLETED);
				kernelManager.saveOrUpdateToken(thisToken);
				behavior.onTokenStateChanged(session, thisToken, this
				 .getWorkflowElement());
			}

		} else if (continueDirection.getDirection() == ContinueDirection.START_NEXT_AND_WAITING_FOR_CLOSE) {
			List<PObjectKey> keys = continueDirection
					.getNextProcessObjectKeys();
			if (keys != null && keys.size() > 0) {
				// 启动新的节点
				for (PObjectKey key : keys) {
					Token newToken = new TokenImpl(thisToken);
					newToken.setElementId(key.getWorkflowElementId());

					BookMark bookMark = new BookMark();
					bookMark.setToken(newToken);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
					bookMark
							.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
					kernelManager.addBookMark(bookMark);
				}
			}
		}
	}

	protected void _forwardCompensatingToken(WorkflowSession session ,KernelManager kernelManager,Token thisToken,Token sourceToken){
		//不需要调用behavior.continueOn(session,	token, this.getWorkflowElement());
		//此种情况默认为ContinueDirection.CloseMe()
		
		//首先检查本流程的（本节点的）compensationChain是否为空，
		if (thisToken.getNextCompensationToken()!=null){
			
			Token nextCompensationToken = kernelManager.getTokenById(thisToken.getNextCompensationToken(), thisToken.getProcessType());
			BookMark bookMark = new BookMark();
			bookMark.setToken(nextCompensationToken);
			bookMark
					.setExecutionEntrance(ExecutionEntrance.HANDLE_COMPENSATION);
			bookMark.setExtraArg(BookMark.COMPENSATION_CODE,
					nextCompensationToken.getCompensationCode());
			bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
			kernelManager.addBookMark(bookMark);
		}else{
			
			Token parentToken = kernelManager.getParentToken(thisToken);
			PObject parent = kernelManager.getProcessObject(parentToken);
			
			Token callbackToken = null;
			if (thisToken.getCallbackTokenId() != null) {
				callbackToken = kernelManager.getTokenById(thisToken.getCallbackTokenId(),
						thisToken.getProcessType());
			}
			
			if (callbackToken!=null){
				BookMark bookMark = new BookMark();
				bookMark.setToken(callbackToken);
				bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
			}
			else if (parentToken != null && parent != null) {
				BookMark bookMark = new BookMark();
				bookMark.setToken(parentToken);
				bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.FORWARD_TOKEN);
				kernelManager.addBookMark(bookMark);
			}
		}
		if (!kernelManager.hasChildrenInQueue(thisToken)) {
			thisToken.setState(TokenState.COMPENSATED);
			kernelManager.saveOrUpdateToken(thisToken);
			behavior.onTokenStateChanged(session, thisToken, this
					.getWorkflowElement());
		}
	}
	/**
	 * 将子节点倒序执行补偿
	 * @param 表示补偿操作是否完成
	 */
	protected boolean _handleCompensation(KernelManager kernelManager,Token listenerToken,List<Token> childrenInCompensationOrder,String compensationCode,Token sourceToken){
		if (childrenInCompensationOrder==null || childrenInCompensationOrder.size()==0)return true;
		List<Token> compensationChain = new ArrayList<Token>();
		for (int i=0;i<childrenInCompensationOrder.size();i++){
			Token t = childrenInCompensationOrder.get(i);
			PObject pobject = kernelManager.getProcessObject(t);
			if (pobject.isAcceptCompensation(t, compensationCode)){
				compensationChain.add(t);
			}
		}
		for (int i=0;i<compensationChain.size();i++){
			Token t = compensationChain.get(i);
			t.setCompensationCode(compensationCode);
			if (i<compensationChain.size()-1){
				t.setNextCompensationToken(compensationChain.get(i+1).getId());
			}
			kernelManager.saveOrUpdateToken(t);
		}
		if (compensationChain.size()>0){
			Token childToken = compensationChain.get(0);
			BookMark bookMark = new BookMark();
			bookMark.setToken(childToken);
			bookMark
					.setExecutionEntrance(ExecutionEntrance.HANDLE_COMPENSATION);
			bookMark.setExtraArg(BookMark.COMPENSATION_CODE,
					compensationCode);
			bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
			kernelManager.addBookMark(bookMark);
			return false;
		}
		else{
			return true;
		}
	}
	
	public void handleTermination(WorkflowSession session ,Token thisToken, Token sourceToken){
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
		.getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		boolean hasAliveChildren = false;
		List<Token> children = kernelManager.getChildren(thisToken);
		if (children!=null && children.size()>0){
			for (Token childToken:children){
				if (childToken.getState().getValue()<TokenState.DELIMITER.getValue()){
					hasAliveChildren = true;
					BookMark bookMark = new BookMark();
					bookMark.setToken(childToken);
					bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
					bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);
					kernelManager.addBookMark(bookMark);
				}
			}				
		}
		
		if (hasAliveChildren){
			thisToken.setState(TokenState.ABORTING);
			kernelManager.saveOrUpdateToken(thisToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
		}else{
		
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
	

	public void handleFault(WorkflowSession session, Token thisToken, Token sourceToken, String faultCode) {
		if (!thisToken.getState().equals(TokenState.RUNNING)) {
			throw new KernelException(this,
					"Illegal token state ,the TokenState.RUNNING is expected,but it is  "
							+ thisToken.getState().name());
		}
		//System.out.println("===Inside "+this.getKey().getWorkflowElementId()+": 进入错误处理逻辑");
		RuntimeContext ctx = ((WorkflowSessionLocalImpl) session)
				.getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);

		PObject processObject = null;
		if (faultCode==null || faultCode.trim().equals("")){
			processObject = this.defaultFaultHandler;
			String s = "null";
			if (processObject!=null){
				s = processObject.getKey().getWorkflowElementId();
			}
			//System.out.println("===Inside "+this.getKey().getWorkflowElementId()+": 采用defaultFaultHandler="+s);
		}else{
			processObject = this.getFaultHandler(faultCode);
			//System.out.println("===Inside "+this.getKey().getWorkflowElementId()+": 根据faultCode获得faultHandler="+processObject);
			if (processObject==null){//如果没有找到匹配的错误处理器，则自动采用缺省的错误处理器
				processObject = this.defaultFaultHandler;
			}
			
		}


		if (processObject!=null){
			//1、将活动的子节点中止掉
			List<Token> children = kernelManager.getChildren(thisToken);
			if (children!=null && children.size()>0){
				for (Token childToken:children){
					if (childToken.getState().getValue()<TokenState.DELIMITER.getValue()){
						
						BookMark bookMark = new BookMark();
						bookMark.setToken(childToken);
						bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
						bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);
						kernelManager.addBookMark(bookMark);
					}
				}				
			}
			
			//2、进入异常处理子流程
			Token newToken = sourceToken==null?(new TokenImpl(thisToken)):(new TokenImpl(sourceToken));
			newToken.setElementId(processObject.getKey().getWorkflowElementId());
			newToken.setBusinessPermitted(true);
			newToken.setCallbackTokenId(thisToken.getId());
			newToken.setOperationContextName(OperationContextName.FAULT);
			
			BookMark bookMark = new BookMark();
			bookMark.setToken(newToken);
			bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
			bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
			kernelManager.addBookMark(bookMark);
			
			thisToken.setState(TokenState.FAULTING);
			kernelManager.saveOrUpdateToken(thisToken);
			WorkflowBehavior behavior = this.getWorkflowBehavior();
			behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
			
		}else{
	
			FaultHandler faultHandler = this.getWorkflowBehavior().getFaultHandler(faultCode);
			
			if (faultHandler!=null){
				//1、将活动的子节点中止掉
				List<Token> children = kernelManager.getChildren(thisToken);
				if (children!=null && children.size()>0){
					for (Token childToken:children){
						if (childToken.getState().getValue()<TokenState.DELIMITER.getValue()){
							
							BookMark bookMark = new BookMark();
							bookMark.setToken(childToken);
							bookMark.setExtraArg(BookMark.SOURCE_TOKEN, thisToken);
							bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);
							kernelManager.addBookMark(bookMark);
						}
					}				
				}
				
				//2、处理异常
				faultHandler.handleFault(session, thisToken, this.getWorkflowElement(), faultCode);
				
				thisToken.setState(TokenState.FAULTING);
				kernelManager.saveOrUpdateToken(thisToken);
				WorkflowBehavior behavior = this.getWorkflowBehavior();
				behavior.onTokenStateChanged(session, thisToken, this.getWorkflowElement());
			}else{
				
				String parentTokenId = thisToken.getParentTokenId();
				Token parentToken = null;
				if (parentTokenId!=null){
					parentToken = kernelManager.getParentToken(thisToken);
				}
				// 向上层抛出
				if (parentToken != null) {
					BookMark bookMark = new BookMark();
					bookMark.setToken(parentToken);
					bookMark
							.setExecutionEntrance(ExecutionEntrance.HANDLE_FAULT);
					bookMark.setExtraArg(BookMark.ERROR_CODE, faultCode);
					kernelManager.addBookMark(bookMark);

					thisToken.setState(TokenState.FAULTED);//如果向上层抛出，则应该记录为Faulted
					kernelManager.saveOrUpdateToken(thisToken);
					WorkflowBehavior behavior = this.getWorkflowBehavior();
					behavior.onTokenStateChanged(session, thisToken, this
							.getWorkflowElement());
				}
				//如果没有父元素，则抛出异常
				else{
					thisToken.setState(TokenState.FAULTED);//如果向上层抛出，则应该记录为Faulted
					kernelManager.saveOrUpdateToken(thisToken);
					WorkflowBehavior behavior = this.getWorkflowBehavior();
					behavior.onTokenStateChanged(session, thisToken, this
							.getWorkflowElement());		
					//抛出异常
					throw new KernelException(this,"No fault handler found for FaultCode="+faultCode);
				}
			}
		}
	}		
}
