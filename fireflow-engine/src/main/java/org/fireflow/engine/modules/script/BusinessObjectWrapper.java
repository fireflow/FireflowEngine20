package org.fireflow.engine.modules.script;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ProcessInstance;
import org.fireflow.engine.modules.persistence.PersistenceService;

public interface BusinessObjectWrapper {
	public Object resolveBusinessObject(RuntimeContext rtx,ProcessInstance processInstance,PersistenceService persistenceService);
}
