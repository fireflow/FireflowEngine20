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
package org.fireflow.engine.modules.persistence;

import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;

/**
 * @author 非也
 * @version 2.0
 */
public class PersistenceServiceDefaultImpl  extends AbsEngineModule implements PersistenceService {
	TokenPersister tokenPersister = null;
	ActivityInstancePersister activityInstancePersister = null;
	ProcessInstancePersister processInstancePersister = null;
	VariablePersister variablePersister = null;
	ProcessPersister processPersister = null;
	ScheduleJobPersister scheduleJobPersister = null;
	
	WorkItemPersister workItemPersister = null;
	
	ReassignConfigPersister reassignConfigPersister = null;
	
//	ServicePersister servicePersister = null;
//	
//	ResourcePersister resourcePersister = null;
	
	FireflowConfigPersister fireflowConfigPersister = null;
	
	RuntimeContext ctx = null;
	
	/**
	 * @return the reassignConfigPersister
	 */
	public ReassignConfigPersister getReassignConfigPersister() {
		return reassignConfigPersister;
	}

	/**
	 * @param reassignConfigPersister the reassignConfigPersister to set
	 */
	public void setReassignConfigPersister(
			ReassignConfigPersister reassignConfigPersister) {
		this.reassignConfigPersister = reassignConfigPersister;
		this.reassignConfigPersister.setPersistenceService(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.PersistenceStrategy#getActivityInstancePersistenceService()
	 */
	public ActivityInstancePersister getActivityInstancePersister() {
		return activityInstancePersister;
	}
	
	public void setActivityInstancePersister(ActivityInstancePersister persister){
		this.activityInstancePersister = persister;
		this.activityInstancePersister.setPersistenceService(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.PersistenceStrategy#getTokenPersistenceService()
	 */
	public TokenPersister getTokenPersister() {
		return tokenPersister;
	}
	
	public void setTokenPersister(TokenPersister persister){
		this.tokenPersister = persister;
		this.tokenPersister.setPersistenceService(this);
	}

	public ProcessInstancePersister getProcessInstancePersister(){
		return processInstancePersister;
	}
	
	public void setProcessInstancePersister(ProcessInstancePersister persister){
		processInstancePersister = persister;
		this.processInstancePersister.setPersistenceService(this);
	}

	public VariablePersister getVariablePersister() {
		return variablePersister;
	}

	public void setVariablePersister(VariablePersister variablePersister) {
		this.variablePersister = variablePersister;
		this.variablePersister.setPersistenceService(this);
	}
	
	public ProcessPersister getProcessPersister(){
		return processPersister;
	}
	
	public void setProcessPersister(ProcessPersister persister){
		processPersister = persister;
		this.processPersister.setPersistenceService(this);
	}
	
	public ScheduleJobPersister getScheduleJobPersister(){
		return this.scheduleJobPersister;
	}
	
	public void setScheduleJobPersister(ScheduleJobPersister persister){
		this.scheduleJobPersister = persister;
		this.scheduleJobPersister.setPersistenceService(this);
	}

	/**
	 * @return the workItemPersister
	 */
	public WorkItemPersister getWorkItemPersister() {
		return workItemPersister;
	}

	/**
	 * @param workItemPersister the workItemPersister to set
	 */
	public void setWorkItemPersister(WorkItemPersister workItemPersister) {
		this.workItemPersister = workItemPersister;
		this.workItemPersister.setPersistenceService(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org.fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		this.ctx = ctx;
	}

//	/**
//	 * @return the servicePersister
//	 */
//	public ServicePersister getServicePersister() {
//		return servicePersister;
//	}
//
//	/**
//	 * @param servicePersister the servicePersister to set
//	 */
//	public void setServicePersister(ServicePersister servicePersister) {
//		this.servicePersister = servicePersister;
//		this.servicePersister.setPersistenceService(this);
//	}
//
//	/**
//	 * @return the resourcePersister
//	 */
//	public ResourcePersister getResourcePersister() {
//		return resourcePersister;
//	}
//
//	/**
//	 * @param resourcePersister the resourcePersister to set
//	 */
//	public void setResourcePersister(ResourcePersister resourcePersister) {
//		this.resourcePersister = resourcePersister;
//		this.resourcePersister.setPersistenceService(this);
//	}
	
	public FireflowConfigPersister getFireflowConfigPersister(){
		return fireflowConfigPersister;
	}
	
	public void setFireflowConfigPersister(FireflowConfigPersister fireflowConfigPersister){
		this.fireflowConfigPersister = fireflowConfigPersister;
		this.fireflowConfigPersister.setPersistenceService(this);
	}
	
	public ProcessLanguageManager getProcessLanguageManager(String processType){
		if (this.ctx==null) return null;
		ProcessLanguageManager util = this.ctx.getEngineModule(ProcessLanguageManager.class, processType);
		return util;
	}
}
