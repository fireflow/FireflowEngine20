package org.fireflow.engine.modules.persistence.nutz;

import org.fireflow.engine.entity.runtime.impl.ProcessInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;

public class ProcessInstancePersisterNutzImpl extends AbsPersisterNutzImpl
		implements ProcessInstancePersister {

	public void deleteAllProcessInstances() {
		dao().clear(ProcessInstanceImpl.class);

	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ProcessInstanceImpl.class;
	}

	@Override
	public Class getEntityClass4History(Class interfaceClz) {
		return ProcessInstanceHistory.class;
	}

	
}
