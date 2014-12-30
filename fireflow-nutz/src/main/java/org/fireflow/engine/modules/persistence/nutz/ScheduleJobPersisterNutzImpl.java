package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.ScheduleJobProperty;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobHistory;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.nutz.dao.Cnd;

public class ScheduleJobPersisterNutzImpl extends AbsPersisterNutzImpl
		implements ScheduleJobPersister {

	public void deleteAllScheduleJobs() {
		dao().clear(ScheduleJobImpl.class);

	}

	public List<ScheduleJob> findScheduleJob4ActivityInstance(
			String activityInstanceId) {
		Cnd cnd = Cnd.where(ScheduleJobProperty.ACTIVITY_INSTANCE_ID.getPropertyName(), "=", activityInstanceId);
		
		List result = dao().query(ScheduleJobImpl.class, cnd);
		
		return (java.util.List<ScheduleJob>)result;
	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ScheduleJobImpl.class;
	}

	@Override
	public Class getEntityClass4History(Class interfaceClz) {
		return ScheduleJobHistory.class;
	}

}
