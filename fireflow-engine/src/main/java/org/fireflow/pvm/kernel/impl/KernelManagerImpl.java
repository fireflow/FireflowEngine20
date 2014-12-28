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
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pvm.kernel.BookMark;
import org.fireflow.pvm.kernel.ExecutionEntrance;
import org.fireflow.pvm.kernel.KernelException;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.translate.Process2PObjectTranslator;




/**
 * @author 非也
 * @version 2.0
 */
public class KernelManagerImpl  extends AbsEngineModule implements KernelManager, RuntimeContextAware {
	private static Log log = LogFactory.getLog(KernelManagerImpl.class);
	protected RuntimeContext runtimeContext = null;
	protected ThreadLocal<Vector<BookMark>> bookMarkQueue = new ThreadLocal<Vector<BookMark>>() {
		public Vector<BookMark> initialValue() {

			return new Vector<BookMark>();

		}

	};
	protected Map<PObjectKey,PObject> processObjectStorage = new HashMap<PObjectKey,PObject>();

	protected Map<ProcessKey,Boolean> loadedProcesses = new HashMap<ProcessKey,Boolean>();

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#executeProcessObject(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.ProcessObjectKey)
	 */
//	public void startPObject1(WorkflowSession session,
//			PObjectKey processObjectKey) {
//		this.loadProcess(ProcessKey.valueOf(processObjectKey));
//		
//		PObject processObject = this.getProcessObject(processObjectKey);
//		if (processObject==null){
//			throw new KernelException("No process object for "+processObjectKey.toString());
//		}
//		Token token = new TokenImpl();
//		token.setBusinessPermitted(true);
//		token.setState(TokenState.INITIALIZED);
//		token.setProcessId(processObjectKey.getProcessId());
//		token.setVersion(processObjectKey.getVersion());
//		token.setElementId(processObjectKey.getWorkflowElementId());
//		token.setProcessType(processObjectKey.getProcessType());
//		token.setStepNumber(0);
//		token.setProcessType( processObjectKey.getProcessType());
//
//		
//		BookMark bookMark = new BookMark();
//		bookMark.setToken(token);
//		bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
//		this.addBookMark(bookMark);
//		
//		this.execute(session);
//	}
	
	public void startPObject(WorkflowSession session,PObjectKey childPObjectKey,
				Token parentToken,ProcessInstance childProcessInstance){
		this.loadProcess(ProcessKey.valueOf(childPObjectKey));
		Token childToken = new TokenImpl(parentToken);
		
		if (childProcessInstance!=null){
			childToken.setProcessInstanceId(childProcessInstance.getId());
			childToken.setElementInstanceId(childProcessInstance.getId());
		}
		
		childToken.setProcessId(childPObjectKey.getProcessId());
		childToken.setProcessType(childPObjectKey.getProcessType());
		childToken.setVersion(childPObjectKey.getVersion());
		
		childToken.setElementId(childPObjectKey.getWorkflowElementId());
		if (parentToken!=null){
			childToken.setParentTokenId(parentToken.getId());
		}
		
		
		BookMark bookMark = new BookMark();
		bookMark.setToken(childToken);
		bookMark.setExecutionEntrance(ExecutionEntrance.TAKE_TOKEN);
		
		this.addBookMark(bookMark);
	}
	
	public void loadProcess(ProcessKey pk ){
		if (this.loadedProcesses.get(pk)!=null && this.loadedProcesses.get(pk)){
			return;//已经装载
		}
		else{
			Process2PObjectTranslator translator = this.runtimeContext.getEngineModule(Process2PObjectTranslator.class, pk.getProcessType());
			
			List<PObject> processObjectList = null;
			try {
				processObjectList = translator.translateProcess(pk);
			} catch (InvalidModelException e) {
				throw new KernelException(null,e);
			} catch (WorkflowProcessNotFoundException e) {
				throw new KernelException(null,e);
			}
			
			if (processObjectList!=null){
				for(PObject po:processObjectList){
					this.processObjectStorage.put(po.getKey(), po);
				}
			}
			this.loadedProcesses.put(pk,Boolean.TRUE);
		}
	}

	public void addBookMark(BookMark bookMark){
		if (bookMark.getExecutionEntrance().equals(ExecutionEntrance.TAKE_TOKEN)){
			Token token = bookMark.getToken();
			PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, token.getProcessType());
			TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();
			tokenPersistenceService.saveOrUpdate(token);
		}
		
		bookMarkQueue.get().add(bookMark);

	}
	
	public boolean hasChildrenInQueue(Token token){
		Vector<BookMark> queue = bookMarkQueue.get();
		if (queue.size()==0){
			return false;
		}
		for (BookMark bookMark:queue){
			Token tk = bookMark.getToken();
			if (tk.getParentTokenId()!=null && tk.getParentTokenId().equals(token.getId())){
				return true;
			}
		}
		return false;
	}
	
	private BookMark getBookMark(){
		log.debug(this.viewTheBookMarkQueue());		
		if (bookMarkQueue.get().size()>0){
			return bookMarkQueue.get().remove(0);
		}else{
			return null;
		}
	}
	
	public void execute(WorkflowSession session){

		BookMark bookMark = null;
		while((bookMark=this.getBookMark())!=null){
			Token token = bookMark.getToken();
			Token sourceToken = (Token)bookMark.getExtraArg(BookMark.SOURCE_TOKEN);
			PObject po = this.getProcessObject(token);
			ExecutionEntrance entrance = bookMark.getExecutionEntrance();
			switch (entrance){
				case TAKE_TOKEN:
					po.takeToken(session, token, sourceToken);
					break;
					
				case FORWARD_TOKEN:					
					po.forwardToken(session, token, sourceToken);
					break;
				//（2012-02-05，该动作容易和handleTermination混淆，意义也不是特别大，暂且注销）
				/*
				case HANDLE_CANCELLATION:
					po.handleCancellation(session, token, sourceToken);
					break;
				*/
				case HANDLE_COMPENSATION:
					String compensationCode = (String)bookMark.getExtraArg(BookMark.COMPENSATION_CODE);
					po.handleCompensation(session, token, sourceToken, compensationCode);
					break;
				case HANDLE_FAULT:
					String faultCode = (String)bookMark.getExtraArg(BookMark.ERROR_CODE);
					po.handleFault(session, token, sourceToken, faultCode);
					break;
				case HANDLE_TERMINATION:
					
					po.handleTermination(session, token, sourceToken);
				;
			}
			
		}
	}
	
	public void fireTerminationEvent(WorkflowSession session,Token listenerToken,Token sourceToken){
		BookMark bookMark = new BookMark();
		bookMark.setToken(listenerToken);
		bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
		bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_TERMINATION);
		
		this.addBookMark(bookMark);
	}
	
	public void fireCompensationEvent(WorkflowSession session,Token listenerToken,Token sourceToken,List<String> compensationCodes){
		if (compensationCodes!=null && compensationCodes.size()>0){
			for (String compensationCode : compensationCodes){
				BookMark bookMark = new BookMark();
				bookMark.setToken(listenerToken);
				bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_COMPENSATION);
				bookMark.setExtraArg(BookMark.COMPENSATION_CODE, compensationCode);
				bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
				
				this.addBookMark(bookMark);	
			}
		}
	}
	
	public void fireFaultEvent(WorkflowSession session,Token listenerToken,Token sourceToken, String faultCode){
		BookMark bookMark = new BookMark();
		bookMark.setToken(listenerToken);
		bookMark.setExtraArg(BookMark.SOURCE_TOKEN, sourceToken);
		bookMark.setExecutionEntrance(ExecutionEntrance.HANDLE_FAULT);
		bookMark.setExtraArg(BookMark.ERROR_CODE, faultCode);
		
		this.addBookMark(bookMark);
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		runtimeContext = ctx;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#getParentToken(org.fireflow.pvm.kernel.Token)
	 */
	public Token getParentToken(Token currentToken) {
		if (currentToken.getParentTokenId()==null || currentToken.getParentTokenId().trim().equals("")){
			return null;
		}
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, currentToken.getProcessType());
		TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();
		return tokenPersistenceService.findParentToken(currentToken);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#getProcessObject(org.fireflow.pvm.kernel.Token)
	 */
	public PObject getProcessObject(Token token) {
		if (token==null )return null;
		ProcessKey processKey = new ProcessKey(token.getProcessId(),token.getVersion(),token.getProcessType());
		this.loadProcess(processKey);
		PObjectKey pObjectKey = new PObjectKey(token.getProcessId(),token.getVersion(),token.getProcessType(),token.getElementId());
		return this.processObjectStorage.get(pObjectKey);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#getProcessObject(org.fireflow.pvm.kernel.ProcessObjectKey)
	 */
	public PObject getProcessObject(PObjectKey key) {
		ProcessKey processKey = new ProcessKey(key.getProcessId(),key.getVersion(),key.getProcessType());
		this.loadProcess(processKey);
		return processObjectStorage.get(key);
	}
	
	public Object getWorkflowElement(PObjectKey key){
		PObject pobj = this.getProcessObject(key);
		if (pobj==null)return null;
		return pobj.getWorkflowElement();
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#getToken(java.lang.String, java.lang.String)
	 */
	public Token getTokenById(String tokenId,String processType) {
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, processType);
		TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();

		return tokenPersistenceService.find(Token.class, tokenId);
	}
	
	public Token getTokenByElementInstanceId(String elementInstanceId,String processType){
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, processType);
		TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();

		return tokenPersistenceService.findTokenByElementInstanceId(elementInstanceId);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#getChildren(org.fireflow.pvm.kernel.Token)
	 */
	public List<Token> getChildren(Token token) {
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, token.getProcessType());
		TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();

		return tokenPersistenceService.findChildTokens(token);
	}


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.kernel.KernelManager#getChildren4Compensation(org.fireflow.pvm.kernel.Token)
	 */
	public List<Token> getChildren4Compensation(Token token) {
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, token.getProcessType());
		TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();
		
		return tokenPersistenceService.findChildTokens4Compensation(token);
	}

	public void saveOrUpdateToken(Token token){
		PersistenceService persistenceStrategy = this.runtimeContext.getEngineModule(PersistenceService.class, token.getProcessType());
		TokenPersister tokenPersistenceService = persistenceStrategy.getTokenPersister();

		tokenPersistenceService.saveOrUpdate(token);
	}
	
	/**
	 * 显示bookmark队列，用于调试
	 * @return
	 */
	public String viewTheBookMarkQueue(){
		Vector<BookMark> queue = bookMarkQueue.get();
		StringBuffer buf = new StringBuffer("The bookmark queue is :\n======================\n");
		int index = 0;
		for(BookMark bookMark:queue){
			
			buf.append(bookMark.getToken().getElementId())
				.append("[").append(bookMark.getToken().getState().name()).append("]")
				.append("-->")
				.append(bookMark.getExecutionEntrance().name());
			index++;
			if (index<queue.size()){
				buf.append("\n");				
			}
		}
		buf.append("\n======================");
		return buf.toString();
	}
	
	public void clearCachedPObject(){
		this.processObjectStorage.clear();
		this.loadedProcesses.clear();
	}
}


