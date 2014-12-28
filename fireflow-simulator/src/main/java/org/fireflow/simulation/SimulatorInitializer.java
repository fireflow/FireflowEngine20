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
package org.fireflow.simulation;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class SimulatorInitializer extends HibernateDaoSupport{
	RuntimeContext runtimeContext = null;
	
	
	
	/**
	 * @return the runtimeContext
	 */
	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}



	/**
	 * @param runtimeContext the runtimeContext to set
	 */
	public void setRuntimeContext(RuntimeContext runtimeContext) {
		this.runtimeContext = runtimeContext;
	}



	@SuppressWarnings("unchecked")
	public void initSimulator(){
		PersistenceService persistenceService = runtimeContext.getDefaultEngineModule(PersistenceService.class);
		WorkItemPersister workItemPersister = persistenceService.getWorkItemPersister();
		workItemPersister.deleteAllWorkItems();
		
		ScheduleJobPersister scheduleJobPersister = persistenceService.getScheduleJobPersister();
		scheduleJobPersister.deleteAllScheduleJobs();
		
		VariablePersister varPersister = persistenceService.getVariablePersister();
		varPersister.deleteAllVariables();
		
		ActivityInstancePersister actInstPersister = persistenceService.getActivityInstancePersister();
		actInstPersister.deleteAllActivityInstances();
		
		ProcessInstancePersister procInstPersister = persistenceService.getProcessInstancePersister();
		procInstPersister.deleteAllProcessInstances();
		
		TokenPersister tokenPersister = persistenceService.getTokenPersister();
		tokenPersister.deleteAllTokens();
		
		
		ProcessPersister processPersister = persistenceService.getProcessPersister();
		processPersister.deleteAllProcesses();
		
	}
}
