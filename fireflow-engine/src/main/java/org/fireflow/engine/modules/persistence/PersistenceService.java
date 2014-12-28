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

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.context.RuntimeContextAware;
import org.fireflow.engine.modules.processlanguage.ProcessLanguageManager;

/**
 * @author 非也
 * @version 2.0
 */
public interface PersistenceService extends EngineModule,RuntimeContextAware{

	public TokenPersister getTokenPersister();
	public ActivityInstancePersister getActivityInstancePersister();
	public ProcessInstancePersister getProcessInstancePersister();
	public VariablePersister getVariablePersister();
	public ProcessPersister getProcessPersister();
	
	public ScheduleJobPersister getScheduleJobPersister();
	
	public WorkItemPersister getWorkItemPersister();
	
	public ReassignConfigPersister getReassignConfigPersister();
	
//	public ResourcePersister getResourcePersister();
//	
//	public ServicePersister getServicePersister();
	

	public FireflowConfigPersister getFireflowConfigPersister();
	
	public ProcessLanguageManager getProcessLanguageManager(String processType);

}
