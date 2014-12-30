package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.fireflow.engine.entity.config.ReassignConfig;
import org.fireflow.engine.entity.config.ReassignConfigProperty;
import org.fireflow.engine.entity.config.impl.ReassignConfigImpl;
import org.fireflow.engine.modules.persistence.ReassignConfigPersister;
import org.nutz.dao.Cnd;

public class ReassignConfigPersisterNutzImpl extends AbsPersisterNutzImpl
		implements ReassignConfigPersister {

	public List<ReassignConfig> findReassignConfig(String processId,
			String processType, String activityId, String userId) {
		Cnd cnd = Cnd.where(ReassignConfigProperty.PROCESS_ID.getPropertyName(),"=",processId)
					.and(ReassignConfigProperty.PROCESS_TYPE.getPropertyName(),"=",processType)
					.and(ReassignConfigProperty.ACTIVITY_ID.getPropertyName(),"=",activityId)
					.and(ReassignConfigProperty.GRANTOR_ID.getPropertyName(),"=",userId);
		List l = dao().query(ReassignConfigImpl.class, cnd);
		return (List<ReassignConfig>)l;
	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ReassignConfigImpl.class;
	}

}
