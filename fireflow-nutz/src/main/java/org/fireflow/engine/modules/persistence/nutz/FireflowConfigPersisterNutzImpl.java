package org.fireflow.engine.modules.persistence.nutz;

import org.fireflow.engine.entity.config.impl.FireflowConfigImpl;
import org.fireflow.engine.modules.persistence.FireflowConfigPersister;

public class FireflowConfigPersisterNutzImpl extends AbsPersisterNutzImpl
		implements FireflowConfigPersister {

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		
		return FireflowConfigImpl.class;
	}

}
