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
package org.fireflow.engine.modules.loadstrategy.impl;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.AbsEngineModule;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy;
import org.fireflow.engine.modules.ousystem.User;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.ProcessPersister;
import org.fireflow.model.InvalidModelException;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class DefaultProcessLoadStrategy extends AbsEngineModule implements
		ProcessLoadStrategy {
	private RuntimeContext rtCtx = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.modules.loadstrategy.ProcessLoadStrategy#loadProcess
	 * (org.fireflow.engine.WorkflowSession, java.lang.String)
	 */
	public ProcessRepository findTheProcessForRunning(String workflowProcessId,
			String processType, User currentUser, WorkflowSession session)
			throws InvalidModelException {

		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();

		int v = processPersister.findTheLatestPublishedVersion(
				workflowProcessId, processType);
		if (v <= 0)
			return null;
		ProcessRepository repository = processPersister
				.findProcessRepositoryByProcessKey(new ProcessKey(
						workflowProcessId, v, processType));
		return repository;
	}

	public ProcessKey findTheProcessKeyForRunning(String workflowProcessId,
			String processType, User currentUser, WorkflowSession session) {
		PersistenceService persistenceService = rtCtx.getEngineModule(
				PersistenceService.class, processType);
		ProcessPersister processPersister = persistenceService
				.getProcessPersister();

		int v = processPersister.findTheLatestPublishedVersion(
				workflowProcessId, processType);
		if (v <= 0)
			return null;
		return new ProcessKey(workflowProcessId, v, processType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fireflow.engine.context.RuntimeContextAware#getRuntimeContext()
	 */
	public RuntimeContext getRuntimeContext() {
		return rtCtx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.context.RuntimeContextAware#setRuntimeContext(org
	 * .fireflow.engine.context.RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		rtCtx = ctx;

	}

}
