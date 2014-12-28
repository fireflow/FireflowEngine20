package org.fireflow.engine.invocation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.client.impl.WorkflowSessionLocalImpl;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.config.ReassignConfig;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.invocation.AssignmentHandler;
import org.fireflow.engine.modules.ousystem.OUSystemConnector;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.ousystem.impl.FireWorkflowSystem;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ReassignConfigPersister;
import org.fireflow.engine.modules.workitem.WorkItemManager;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;

public abstract class AbsAssignmentHandler implements AssignmentHandler{
	/**
	 * 
	 */
	private static final long serialVersionUID = 433878253467183907L;
	

	public abstract List<User> resolvePotentialOwners(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance);
	public abstract List<User> resolveReaders(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance);
	public abstract List<User> resolveAdministrators(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity,ProcessInstance processInstance,ActivityInstance activityInstance);
	public abstract WorkItemAssignmentStrategy resolveAssignmentStrategy(WorkflowSession session, ResourceBinding resourceBinding,
			Object theActivity);
	public abstract Map<WorkItemProperty,Object> resolveWorkItemPropertyValues();
	
	
	public List<WorkItem> assign(WorkflowSession session,
			ActivityInstance activityInstance,WorkItemManager workItemManager,Object theActivity,
			ServiceBinding serviceBinding, ResourceBinding resourceBinding)
			throws EngineException {
		WorkflowSessionLocalImpl sessionLocal = (WorkflowSessionLocalImpl)session;

		RuntimeContext runtimeContext = sessionLocal.getRuntimeContext();
		ProcessInstance processInstance = sessionLocal.getCurrentProcessInstance();

		List<WorkItem> result = new ArrayList<WorkItem>();
		
		Map<WorkItemProperty,Object> values = this.resolveWorkItemPropertyValues();
		if(values==null){
			values = new HashMap<WorkItemProperty,Object>();
		}		

		List<User> potentialOwners =  this.resolvePotentialOwners(session,resourceBinding,theActivity,processInstance,activityInstance);		
		if (potentialOwners==null || potentialOwners.size()==0){
			//通知业务领导进行处理
			List<User> administrators = this.resolveAdministrators(session,resourceBinding,theActivity,processInstance,activityInstance);	
			if (administrators==null || administrators.size()==0){
				//TODO 赋值给Fireflow内置用户，并记录警告信息
				WorkItem wi = workItemManager.createWorkItem(session, processInstance, activityInstance, FireWorkflowSystem.getInstance(),theActivity, null);
				result.add(wi);
			}else{
				//这种情况下，ASSIGNMENT_STRATEGY固定为WorkItem.ASSIGNMENT_ANY
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, WorkItemAssignmentStrategy.ASSIGN_TO_ANY);
				
				for (User user : administrators) {					
					WorkItem wi = workItemManager.createWorkItem(session,
							processInstance, activityInstance, user, theActivity,values);

					result.add(wi);
					
					List<User> agents = findReassignTo(runtimeContext, activityInstance
							.getProcessId(), activityInstance.getProcessType(),
							activityInstance.getNodeId(), user.getId());
					if (agents != null && agents.size() != 0) {
						ReassignmentHandler dynamicAssignmentHandler = new ReassignmentHandler();
						dynamicAssignmentHandler.setPotentialOwners(agents);
						dynamicAssignmentHandler.setReassignType(WorkItem.REASSIGN_AFTER_ME);
						dynamicAssignmentHandler.setParentWorkItemId(wi.getId());
						
						StringBuffer sbuf= new StringBuffer("工作项被自动委派给：");
						for (int i = 0;i<agents.size();i++){
							User u = agents.get(i);
							sbuf.append(u.getName());
							if (i<agents.size()-1)sbuf.append("、");
						}
						wi.setNote(sbuf.toString());
						
						List<WorkItem> agentWorkItems = workItemManager
								.reassignWorkItemTo(session, wi,
										dynamicAssignmentHandler,
										theActivity,
										serviceBinding,
										resourceBinding);
						
						result.addAll(agentWorkItems);
					}

				}
			}
		}else{
			WorkItemAssignmentStrategy strategy = this.resolveAssignmentStrategy(session,resourceBinding,theActivity);
			if (strategy!=null){
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, strategy);
			}else{
				values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, WorkItemAssignmentStrategy.ASSIGN_TO_ANY);
			}
			for (User user : potentialOwners) {
				
				WorkItem wi = workItemManager.createWorkItem(session,
						processInstance, activityInstance, user,theActivity, values);
				result.add(wi);
				
				//设置note
				
				
				List<User> agents = findReassignTo(runtimeContext, activityInstance
						.getProcessId(), activityInstance.getProcessType(),
						activityInstance.getNodeId(), user.getId());
				if (agents != null && agents.size() != 0) {
					ReassignmentHandler dynamicAssignmentHandler = new ReassignmentHandler();
					dynamicAssignmentHandler.setPotentialOwners(agents);
					dynamicAssignmentHandler.setReassignType(WorkItem.REASSIGN_AFTER_ME);
					dynamicAssignmentHandler.setParentWorkItemId(wi.getId());
					
					StringBuffer sbuf= new StringBuffer("工作项被自动委派给：");
					for (int i = 0;i<agents.size();i++){
						User u = agents.get(i);
						sbuf.append(u.getName());
						if (i<agents.size()-1)sbuf.append("、");
					}
					wi.setNote(sbuf.toString());
					
					List<WorkItem> agentWorkItems = workItemManager
							.reassignWorkItemTo(session, wi,
									dynamicAssignmentHandler,theActivity,
									serviceBinding,resourceBinding);
					
					result.addAll(agentWorkItems);
				}				
			}			
		}
		

		List<User> readers = this.resolveReaders(session,resourceBinding,theActivity,processInstance,activityInstance);
		if (readers != null && readers.size() > 0) {
			values.put(WorkItemProperty.ASSIGNMENT_STRATEGY, WorkItemAssignmentStrategy.ASSIGN_TO_ANY);
			values.put(WorkItemProperty.STATE, WorkItemState.READONLY);
			for (User user : readers) {
				WorkItem wi = workItemManager.createWorkItem(session,
						processInstance, activityInstance, user,theActivity, values);

				result.add(wi);
			}
		}
		return result;
	}
	protected List<User> findReassignTo(RuntimeContext rtCtx,String processId,String processType,String activityId,String userId){
		PersistenceService persistenceService = rtCtx.getEngineModule(PersistenceService.class, processType);
		ReassignConfigPersister persister = persistenceService.getReassignConfigPersister();
		
		List<ReassignConfig> configs = persister.findReassignConfig(processId, processType, activityId,userId);
		
		if (configs==null || configs.size()==0) return null;
		
		List<User> agents = new ArrayList<User>();
		OUSystemConnector ousystem = rtCtx.getEngineModule(OUSystemConnector.class, processType);
		for (ReassignConfig config : configs){
			User u = ousystem.findUserById(config.getAgentId());
			agents.add(u);
		}
		return agents;
	}	

}
