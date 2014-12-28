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
package org.fireflow.pdl.fpdl.enginemodules;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.exception.WorkflowProcessNotFoundException;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.pdl.fpdl.behavior.ActivityBehavior;
import org.fireflow.pdl.fpdl.behavior.EndNodeBehavior;
import org.fireflow.pdl.fpdl.behavior.RouterBehavior;
import org.fireflow.pdl.fpdl.behavior.SubProcessBehavior;
import org.fireflow.pdl.fpdl.misc.FpdlConstants;
import org.fireflow.pdl.fpdl.process.Activity;
import org.fireflow.pdl.fpdl.process.EndNode;
import org.fireflow.pdl.fpdl.process.Router;
import org.fireflow.pdl.fpdl.process.StartNode;
import org.fireflow.pdl.fpdl.process.SubProcess;
import org.fireflow.pdl.fpdl.process.Transition;
import org.fireflow.pdl.fpdl.process.WorkflowProcess;
import org.fireflow.pdl.fpdl.process.features.Feature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchCompensationFeature;
import org.fireflow.pdl.fpdl.process.features.startnode.CatchFaultFeature;
import org.fireflow.pvm.kernel.PObject;
import org.fireflow.pvm.kernel.PObjectKey;
import org.fireflow.pvm.kernel.impl.ArcInstanceImpl;
import org.fireflow.pvm.kernel.impl.NetInstanceImpl;
import org.fireflow.pvm.kernel.impl.NodeInstanceImpl;
import org.fireflow.pvm.pdllogic.WorkflowBehavior;
import org.fireflow.pvm.translate.Process2PObjectTranslator;

/**
 * 
 * @author 非也
 * @version 2.0
 */
public class Process2PObjectTranslatorFpdl20Impl  extends AbsEngineModule implements
		Process2PObjectTranslator {
	private WorkflowBehavior transitionBehavior = null;
	private WorkflowBehavior startNodeBehavior = null;
	private EndNodeBehavior endNodeBehavior = null;
	private ActivityBehavior activityBehavior = null;
	private RouterBehavior routerBehavior = null;
	private SubProcessBehavior subProcessBehavior = null;
	
	
//	private WorkflowProcessBehavior workflowProcessBehavior = null;

	public WorkflowBehavior getTransitionBehavior() {
		return transitionBehavior;
	}

	public void setTransitionBehavior(WorkflowBehavior transitionBehavior) {
		this.transitionBehavior = transitionBehavior;
	}

	public WorkflowBehavior getStartNodeBehavior() {
		return startNodeBehavior;
	}

	public void setStartNodeBehavior(WorkflowBehavior startNodeBehavior) {
		this.startNodeBehavior = startNodeBehavior;
	}

	public EndNodeBehavior getEndNodeBehavior() {
		return endNodeBehavior;
	}

	public void setEndNodeBehavior(EndNodeBehavior endNodeBehavior) {
		this.endNodeBehavior = endNodeBehavior;
	}

	public ActivityBehavior getActivityBehavior() {
		return activityBehavior;
	}

	public void setActivityBehavior(ActivityBehavior activityBehavior) {
		this.activityBehavior = activityBehavior;
	}

	public RouterBehavior getRouterBehavior() {
		return routerBehavior;
	}

	public void setRouterBehavior(RouterBehavior routerBehavior) {
		this.routerBehavior = routerBehavior;
	}

	public SubProcessBehavior getSubProcessBehavior() {
		return subProcessBehavior;
	}

	public void setSubProcessBehavior(SubProcessBehavior subProcessBehavior) {
		this.subProcessBehavior = subProcessBehavior;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.translate.PDL2ProcessObjectTranslator#translatePDL2ProcessObjects(org.fireflow.engine.entity.repository.ProcessRepository)
	 */
	public List<PObject> translateProcess(ProcessKey processKey ,Object process) {
		WorkflowProcess fpdl20Process = (WorkflowProcess)process;
		List<SubProcess> subflows = fpdl20Process.getLocalSubProcesses();

		List<PObject> allPObject = new ArrayList<PObject>();
		for (SubProcess subflow:subflows){
			List<PObject> pobjectList = translateSubflow(subflow,processKey);
			if (pobjectList!=null)	allPObject.addAll(pobjectList);
		}
		return allPObject;
	}
	
	private List<PObject> translateSubflow(SubProcess subflow,ProcessKey processKey){
	
		PObjectKey key = new PObjectKey(processKey.getProcessId(),
				processKey.getVersion(), processKey.getProcessType(), subflow.getId());

		
		PObject pObject = new NetInstanceImpl(key);
		pObject.setWorkflowBehavior(subProcessBehavior);
		pObject.setWorkflowElement(subflow);
		
		List<PObject> pobjectList = new ArrayList<PObject>();
		pobjectList.add(pObject);
		
		ProcessKey pk = ProcessKey.valueOf(key);
		
		List<StartNode> startNodes = subflow.getStartNodes();
		if (startNodes!=null && startNodes.size()>0){
			List<PObject> pos = this.translateStartNodes(startNodes, pk);
			pobjectList.addAll(pos);
		}
		
		List<Router> routers = subflow.getRouters();
		if (routers!=null && routers.size()>0){
			List<PObject> pos = this.translateRouters(routers, pk);
			pobjectList.addAll(pos);
		}
		
		List<EndNode> endNodes = subflow.getEndNodes();
		if (endNodes!=null && endNodes.size()>0){
			List<PObject> pos = this.translateEndNodes(endNodes, pk);
			pobjectList.addAll(pos);
		}
		
		List<Activity> activities = subflow.getActivities();
		if (activities!=null && activities.size()>0){
			List<PObject> pos = this.translateActivities(activities, pk);
			pobjectList.addAll(pos);
		}
		
		List<Transition> transitions = subflow.getTransitions();
		if (transitions!=null && transitions.size()>0){
			List<PObject> pos = this.translateTransitions(transitions, pk);
			pobjectList.addAll(pos);
		}

		assemblePObject(subflow,pobjectList,pk);
		
		return pobjectList;
	}
	
	private List<PObject> translateStartNodes(List<StartNode> startNodes,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (StartNode startNode:startNodes){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), startNode.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(startNodeBehavior);
			pObject.setWorkflowElement(startNode);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	private List<PObject> translateRouters(List<Router> routers,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (Router router:routers){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), router.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(routerBehavior);
			pObject.setWorkflowElement(router);
			
			result.add(pObject);
		}
		
		return result;
	}

	private List<PObject> translateEndNodes(List<EndNode> endNodes,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (EndNode endNode:endNodes){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), endNode.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(endNodeBehavior );
			pObject.setWorkflowElement(endNode);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	private List<PObject> translateActivities(List<Activity> activities,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (Activity activity:activities){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), activity.getId());
			
			PObject pObject = new NodeInstanceImpl(key);
			pObject.setCancellable(true);
			pObject.setCompensable(true);
			pObject.setWorkflowBehavior(activityBehavior );
			pObject.setWorkflowElement(activity);
			
			result.add(pObject);
		}
		
		return result;
	}
	
	private List<PObject> translateTransitions(List<Transition> transitions,ProcessKey pk){
		List<PObject> result = new ArrayList<PObject>();
		
		for (Transition transition:transitions){
			
			PObjectKey key = new PObjectKey(pk.getProcessId(),
					pk.getVersion(), pk
							.getProcessType(), transition.getId());
			
			PObject pObject = new ArcInstanceImpl(key);
			pObject.setCancellable(false);
			pObject.setCompensable(false);
			pObject.setWorkflowBehavior(transitionBehavior);
			pObject.setWorkflowElement(transition);//TODO 流程对象被众多PObject引用，是否浪费内存？
			
			result.add(pObject);
		}
		
		return result;
	}
	
	
	/**
	 * 组装异常处理器，补偿处理器等
	 * @param subflow
	 * @param pobjectList
	 */
	private void assemblePObject(SubProcess subflow ,List<PObject> pobjectList,ProcessKey pk){
		List<Activity> activities = subflow.getActivities();
		if (activities==null || activities.size()==0){
			return;
		}
		for (Activity activity : activities){

			PObjectKey pkey4Activity = new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),activity.getId());
			PObject pobject4Activity = this.findPObject(pobjectList, pkey4Activity);
			
			List<StartNode> attachedStartNodes = activity.getAttachedStartNodes();
			if (attachedStartNodes!=null && attachedStartNodes.size()>0){
				for (StartNode startNode : attachedStartNodes){
					Feature decorator = startNode.getFeature();
					if (decorator!=null && decorator instanceof CatchCompensationFeature){
						CatchCompensationFeature compensationDecorator = (CatchCompensationFeature)decorator;
						PObjectKey pkey = new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),startNode.getId());
						PObject po = this.findPObject(pobjectList, pkey);
						if (po!=null){
							String compensationCode = compensationDecorator.getCompensationCode();
							if (compensationCode==null || compensationCode.trim().equals("")){
								compensationCode = CatchCompensationFeature.CATCH_ALL_COMPENSATION;
							}else{
								//分号分割
								StringTokenizer tokenizer = new StringTokenizer(compensationCode,";");
								while (tokenizer.hasMoreTokens()){
									String code = tokenizer.nextToken();
									if (code==null || code.trim().equals("")){
										continue;
									}
									if (CatchCompensationFeature.CATCH_ALL_COMPENSATION.equals(code.trim())){
										((NodeInstanceImpl)pobject4Activity).setCompensationHandler(code.trim(),  po,true);
									}else{
										((NodeInstanceImpl)pobject4Activity).setCompensationHandler(code.trim(),  po);
									}
								}
							}
						}
					}else if (decorator!=null && decorator instanceof CatchFaultFeature){
						CatchFaultFeature exceptionDecorator = (CatchFaultFeature)decorator;
						PObjectKey pkey = new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),startNode.getId());
						PObject po = this.findPObject(pobjectList, pkey);
						if (po!=null){
							String errorCode = exceptionDecorator.getErrorCode();
							if (errorCode==null || errorCode.trim().equals("")){
								((NodeInstanceImpl)pobject4Activity).setFaultHandler(CatchFaultFeature.CATCH_ALL_FAULT, po,true);
							}
							else{
								//分号分割
								StringTokenizer tokenizer = new StringTokenizer(errorCode,";");
								while (tokenizer.hasMoreTokens()){
									String code = tokenizer.nextToken();
									if (code==null || code.trim().equals("")){
										continue;
									}
									
									if (CatchFaultFeature.CATCH_ALL_FAULT.equals(code.trim())){
										((NodeInstanceImpl)pobject4Activity).setFaultHandler(CatchFaultFeature.CATCH_ALL_FAULT, po,true);
									}
									else{
										((NodeInstanceImpl)pobject4Activity).setFaultHandler(code.trim(), po);
									}
								}
	
							}

							
						}
					}
					//TODO 需要catch cancellation decorator吗？
//					else if (decorator!=null && decorator instanceof CatchCancellationDecorator){
//						
//					}
//					else if ( is catchTimerDecorator){
//						
//					}
				}
			}
		}			
		//装配流程级别的handler
		List<StartNode> startNodes = subflow.getStartNodes();
		PObject pobject4Process = this.findPObject(pobjectList, new PObjectKey(pk.getProcessId(),pk.getVersion(),pk.getProcessType(),subflow.getId()));
		if (startNodes!=null && startNodes.size()>0){
			for (StartNode start : startNodes) {
				PObjectKey pkey4Start = new PObjectKey(pk.getProcessId(), pk
						.getVersion(), pk.getProcessType(), start.getId());
				PObject pobject4Start = this.findPObject(pobjectList,
						pkey4Start);

				Feature decorator = start.getFeature();
				if (decorator != null
						&& (decorator instanceof CatchFaultFeature)) {					
					CatchFaultFeature faultDecorator = (CatchFaultFeature) decorator;
					
					if (faultDecorator.getAttachedToActivity()==null){
						String errorCode = faultDecorator.getErrorCode();
						if (errorCode == null || errorCode.trim().equals("")) {
							pobject4Process
									.setFaultHandler("", pobject4Start, true);
						} else {
							pobject4Process.setFaultHandler(errorCode,
									pobject4Start);
						}
					}
				} else if (decorator != null
						&& (decorator instanceof CatchCompensationFeature)) {
					CatchCompensationFeature compensationDecorator = (CatchCompensationFeature) decorator;
					if (compensationDecorator.getAttachedToActivity() == null) {
						String compensationCode = compensationDecorator
								.getCompensationCode();
						if (compensationCode == null
								|| compensationCode.trim().equals("")) {
							compensationCode = CatchCompensationFeature.CATCH_ALL_COMPENSATION;
						}
						if (compensationCode
								.equals(CatchCompensationFeature.CATCH_ALL_COMPENSATION)) {
							pobject4Process.setCompensationHandler(
									compensationCode, pobject4Start, true);
						} else {
							pobject4Process.setCompensationHandler(
									compensationCode, pobject4Start);
						}
					}
				}
			}
		}
	}
	
	private PObject findPObject(List<PObject> pobjectList,PObjectKey pkey){
		if (pobjectList==null || pobjectList.size()==0){
			return null;
		}
		for (PObject pobject : pobjectList){
			if (pobject.getKey().equals(pkey)){
				return pobject;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.pvm.translate.Process2PObjectTranslator#translateProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	public List<PObject> translateProcess(ProcessKey processKey) throws InvalidModelException,WorkflowProcessNotFoundException{
		PersistenceService persistenceService = runtimeContext.getEngineModule(PersistenceService.class, FpdlConstants.PROCESS_TYPE_FPDL20);
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		ProcessRepository repository = processPersister.findProcessRepositoryByProcessKey(processKey);
		if (repository==null){
			throw new WorkflowProcessNotFoundException("The process is not found, id="+processKey.getProcessId()+", version="+processKey.getVersion()+", processType="+processKey.getProcessType());
		}else{
			return this.translateProcess(processKey, repository.getProcessObject());
		}
		
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
		this.runtimeContext = ctx;
		
	}
	
	private RuntimeContext runtimeContext = null;
}
