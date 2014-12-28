package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.Persister;

public abstract class AbsPersisterNutzImpl implements Persister {
	protected PersistenceService persistenceService = null;

	public <T extends WorkflowEntity> T find(Class<T> entityClz, String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends WorkflowEntity> List<T> list(WorkflowQuery<T> q) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T extends WorkflowEntity> int count(WorkflowQuery<T> q) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void saveOrUpdate(Object entity) {
		// TODO Auto-generated method stub

	}

	public PersistenceService getPersistenceService() {
		return this.persistenceService;
	}

	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

}
