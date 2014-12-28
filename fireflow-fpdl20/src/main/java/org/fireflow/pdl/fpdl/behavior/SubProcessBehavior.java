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
package org.fireflow.pdl.fpdl.behavior;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceState;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.instancemanager.event.ProcessInstanceEventTrigger;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Node;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pvm.kernel.KernelManager;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.pdllogic.BusinessStatus;
import org.fireflow.pvm.pdllogic.CompensationHandler;
import org.fireflow.pvm.pdllogic.ContinueDirection;
import org.fireflow.pvm.pdllogic.ExecuteResult;
import org.fireflow.pvm.pdllogic.FaultHandler;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SubProcessBehavior implements WorkflowBehavior {
	private static final Log log = LogFactory.getLog(SubProcessBehavior.class);
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
	
	/**
	 * prepare方法创建流程实例
	 */
	public Boolean prepare(WorkflowSession session, Token token,
			Object workflowElement) {
		
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;
		RuntimeContext context = sessionLocal.getRuntimeContext();
		
		PersistenceService persistenceService = context.getEngineModule(PersistenceService.class, token.getProcessType());

		ProcessInstancePersister procInstPersistSvc = persistenceService.getProcessInstancePersister();
		
		ProcessInstance newProcessInstance = (ProcessInstance)sessionLocal.getCurrentProcessInstance();
		if (newProcessInstance==null || !newProcessInstance.getId().equals(token.getProcessInstanceId())){
			WorkflowQuery<ProcessInstance> q4ProcInst = sessionLocal.createWorkflowQuery(ProcessInstance.class);
			newProcessInstance = q4ProcInst.get(token.getProcessInstanceId());
			sessionLocal.setCurrentProcessInstance(newProcessInstance);
		}
	


		
		//考虑到启动子流程的问题，不允许在此处设置processInstanceId，只能在KernelManager.startPObject(...)有设置
//		token.setProcessInstanceId(newProcessInstance.getId());
		//考虑到启动子流程的问题，不允许在此处设置elementInstanceId
//		token.setElementInstanceId(newProcessInstance.getId());
		
//		TokenPersister tokenPersister = persistenceService.getTokenPersister();
//		tokenPersister.saveOrUpdate(token);

		
		((ProcessInstanceImpl)newProcessInstance).setTokenId(token.getId());
		procInstPersistSvc.saveOrUpdate(newProcessInstance);
		
		//发布事件
		ProcessInstanceManager procInstManager = context.getEngineModule(ProcessInstanceManager.class, token.getProcessType());
		procInstManager.fireProcessInstanceEvent(session, newProcessInstance, workflowElement, ProcessInstanceEventTrigger.BEFORE_PROCESS_INSTANCE_RUN);
		
		
		return true;//true表示告诉虚拟机，“我”已经准备妥当了。
	}
	
	


	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#execute(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ExecuteResult execute(WorkflowSession session, Token processToken,
			Object workflowElement) {
		SubProcess subflow = (SubProcess)workflowElement;
		Node entry = subflow.getEntry();
		if (entry==null){
			throw new EngineException("子过程入口节点为空，无法继续执行；子过程Id 是"+subflow.getId());
		}
		PObjectKey pobjectKey = new PObjectKey(processToken.getProcessId(),processToken.getVersion(),processToken.getProcessType(),entry.getId());
		
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		kernelManager.startPObject(session, pobjectKey, processToken,null);
		
		ExecuteResult result = new ExecuteResult();
		result.setStatus(BusinessStatus.RUNNING);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#onTokenStateChanged(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public void onTokenStateChanged(WorkflowSession session, Token token,
			Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		PersistenceService persistenceStrategy = ctx.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ProcessInstancePersister procInstPersistenceService = persistenceStrategy.getProcessInstancePersister();
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;

		ProcessInstance oldProcInst = sessionLocal.getCurrentProcessInstance();
		ProcessInstance procInst = oldProcInst;
		if (oldProcInst==null || !oldProcInst.getId().equals(token.getElementInstanceId())){
			procInst = procInstPersistenceService.find(ProcessInstance.class, token.getElementInstanceId());
			
		}
		
		ProcessInstanceManager processInstanceManager = ctx.getEngineModule(ProcessInstanceManager.class, FpdlConstants.PROCESS_TYPE_FPDL20);

		try{
			ProcessInstanceState state = ProcessInstanceState.valueOf(token.getState().name());
			processInstanceManager.changeProcessInstanceSate(session, procInst, state,workflowElement);
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcInst);
		}

	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.pdllogic.WorkflowBehavior#continueOn(org.fireflow.engine.WorkflowSession, org.fireflow.pvm.kernel.Token, java.lang.Object)
	 */
	public ContinueDirection continueOn(WorkflowSession session,
			Token token, Object workflowElement) {
		RuntimeContext ctx = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		KernelManager kernelManager = ctx.getDefaultEngineModule(KernelManager.class);
		List<Token> childTokenList = kernelManager.getChildren(token);
		if (childTokenList==null || childTokenList.size()==0){
			return ContinueDirection.closeMe();
		}else{
			for (Token tk : childTokenList){
				if (tk.getState().getValue()<TokenState.DELIMITER.getValue()){
					return ContinueDirection.waitingForClose();
				}
			}
		}
		return ContinueDirection.closeMe();
	}


}
