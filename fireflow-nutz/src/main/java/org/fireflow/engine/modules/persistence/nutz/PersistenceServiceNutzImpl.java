package org.fireflow.engine.modules.persistence.nutz;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.PersistenceServiceDefaultImpl;
import org.nutz.dao.impl.NutDao;

public class PersistenceServiceNutzImpl extends PersistenceServiceDefaultImpl
		implements PersistenceService, EngineModule {
	private NutDao nutzDao = null;

	public NutDao getNutzDao() {
		return nutzDao;
	}

	public void setNutzDao(NutDao nutzDao) {
		this.nutzDao = nutzDao;
	}
	
	
}
