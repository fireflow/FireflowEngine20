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
package org.fireflow.service.call;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.client.query.Restrictions;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.ProcessInstanceProperty;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.engine.modules.instancemanager.ProcessInstanceManager;
import org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;
import org.fireflow.engine.modules.script.ScriptContextVariableNames;
import org.fireflow.engine.modules.script.ScriptEngineHelper;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.Assignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CallServiceInvoker implements ServiceInvoker {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.ServiceInvoker#invoke(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, org.fireflow.model.binding.ServiceBinding, org.fireflow.model.binding.ResourceBinding, java.lang.Object)
//	 */
	public boolean invoke(WorkflowSession session,
			ActivityInstance activityInstance, ServiceBinding serviceBinding,
			ResourceBinding resourceBinding, Object theActivity)
			throws ServiceInvocationException {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;

		ProcessInstance oldProcessInstance = sessionLocal.getCurrentProcessInstance();
		ActivityInstance oldActivityInstance = sessionLocal.getCurrentActivityInstance();
		
		((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(null);
		((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(null);
		
		ProcessLanguageManager processUtil = context.getEngineModule(ProcessLanguageManager.class, activityInstance.getProcessType());
		try{
			//1、确定子流程的ProcessId,SubflowId,版本号等信息
			CallServiceDef subflowService = (CallServiceDef)processUtil.getServiceDef(activityInstance, theActivity, serviceBinding.getServiceId());
			if (subflowService==null){
				ServiceInvocationException ex = new ServiceInvocationException("没有找到Id为"+serviceBinding.getServiceId()+"的服务");
				ex.setErrorCode(ServiceInvocationException.SERVICE_DEF_NOT_FOUND);
				ex.setActivityInstance(activityInstance);
				throw ex;
			}
			String subflowId = subflowService.getSubProcessId();
			String processId = subflowService.getProcessId();
			String processType = activityInstance.getProcessType();
			Integer version = subflowService.getProcessVersion();
			//同一流程只能用相同的版本号
			if (processId.equals(activityInstance.getProcessId())){
				version = activityInstance.getVersion();
			}
			
			if (version==CallServiceDef.THE_LATEST_VERSION){
				//查找流程的最新版本号。
				ProcessLoadStrategy loadStrategy = context.getEngineModule(
						ProcessLoadStrategy.class, activityInstance.getProcessType());
				User u = session.getCurrentUser();
				ProcessKey pk = loadStrategy.findTheProcessKeyForRunning(
						processId, activityInstance.getProcessType(),u,session);
				
				version = pk.getVersion();
			}
			
			//2、构建输入参数
			Map<String, Object> variables = null;
			try {
				variables = AbsServiceInvoker.resolveInputAssignments(context, session, oldProcessInstance, oldActivityInstance, serviceBinding,subflowService);
			} catch (ScriptException e) {
				ServiceInvocationException ex = new ServiceInvocationException(e);
				ex.setErrorCode(findRootCause(e).getClass().getName());
				ex.setActivityInstance(activityInstance);
				throw ex;
			}
			
			ProcessInstanceManager processInstanceManager = context.getEngineModule(ProcessInstanceManager.class, processType);
			PersistenceService persistenceService = context.getEngineModule(PersistenceService.class,processType);
			ProcessPersister processPersister = persistenceService.getProcessPersister();
			ProcessRepository repository = null;
			try {
				repository = processPersister.findProcessRepositoryByProcessKey(new ProcessKey(processId,version,processType));
				if (repository==null){
					ServiceInvocationException ex = new ServiceInvocationException("流程库中没有ProcessId="+processId+",version="+version+"的流程定义文件。");
					ex.setActivityInstance(activityInstance);
					ex.setErrorCode(ServiceInvocationException.PROCESS_DEF_NOT_FOUND);
					throw ex;
				}
			} catch (InvalidModelException e) {
				ServiceInvocationException ex = new ServiceInvocationException(e.getMessage(),e);
				ex.setErrorCode(ServiceInvocationException.INVALID_PROCESS_MODEL);
				ex.setActivityInstance(activityInstance);
				throw ex;
			}

			Object workflowProcess = repository.getProcessObject();
			
			//启动子流程
//			Token parentToken = kernelManager.getToken(activityInstance.getTokenId(), activityInstance.getProcessType());
			ProcessInstance childProcessInstance = processInstanceManager.createProcessInstance(session, workflowProcess, subflowId, repository, oldActivityInstance);
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(childProcessInstance);
			processInstanceManager.runProcessInstance(session, childProcessInstance.getId(), childProcessInstance.getProcessType(), oldActivityInstance.getBizId(), variables);
			
		}finally{
			((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(oldProcessInstance);
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(oldActivityInstance);
		}

		return false;//表示异步调用
	}
	
	private Throwable findRootCause(Throwable e){

		if (e.getCause()==null){
			return e;
		}
		return findRootCause(e.getCause());
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.ServiceInvoker#determineActivityCloseStrategy(org.fireflow.engine.WorkflowSession, org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object)
	 */
	public int determineActivityCloseStrategy(WorkflowSession session,
			ActivityInstance activityInstance, Object theActivity, ServiceBinding serviceBinding) {
		RuntimeContext context = ((WorkflowSessionLocalImpl)session).getRuntimeContext();
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;

		int result = ServiceInvoker.CLOSE_ACTIVITY;
		//此处可以增加特殊逻辑，以判断子流程是否可以结束。
		


		if (result==ServiceInvoker.CLOSE_ACTIVITY){
			//校验currentActivityInstance和currentProcessInstance
			((WorkflowSessionLocalImpl)session).setCurrentActivityInstance(activityInstance);
			ProcessInstance parentProcessInstance = sessionLocal.getCurrentProcessInstance();
			if (parentProcessInstance==null || !parentProcessInstance.getId().equals(activityInstance.getProcessInstanceId())){
				parentProcessInstance = activityInstance.getProcessInstance(session);
				((WorkflowSessionLocalImpl)session).setCurrentProcessInstance(parentProcessInstance);
			}
			
			//查询出子流程实例
			WorkflowQuery<ProcessInstance> query = session.createWorkflowQuery(ProcessInstance.class);
			query.add(Restrictions.eq(ProcessInstanceProperty.PARENT_ACTIVITY_INSTANCE_ID, activityInstance.getId()))
				.add(Restrictions.eq(ProcessInstanceProperty.PARENT_PROCESS_INSTANCE_ID, parentProcessInstance.getId()));
			
			ProcessInstance subProcInst = query.unique();//在不定制的情况下，只可能创建一个子流程
			Map<String,Object> subProcInstVars = subProcInst.getVariableValues(session);
			
			
			//处理输出，将子流程的流程变量反馈到父流程实例或者父活动实例
//			OperationDef operation = serviceBinding.getOperation();
			List<Assignment> outputAssignments = serviceBinding
					.getOutputAssignments();
			if (outputAssignments != null && outputAssignments.size() > 0) {
				Map<String, Object> scriptContext = new HashMap<String, Object>();

				scriptContext.put(ScriptContextVariableNames.OUTPUTS,
						subProcInstVars);

				try {
					ScriptEngineHelper.assignOutputToVariable(session, context,
							parentProcessInstance, activityInstance,
							outputAssignments, scriptContext);
				} catch (ScriptException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		
		return result;
	}

}
