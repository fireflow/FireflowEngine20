package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.nutz.dao.Cnd;

public class ActivityInstancePersisterNutzImpl extends AbsPersisterNutzImpl
		implements ActivityInstancePersister {

	public void deleteAllActivityInstances() {
		dao().clear(ActivityInstanceImpl.class);

	}

	public int countAliveActivityInstance(String processInstanceId,
			String nodeId) {
		int result = dao().count(ActivityInstanceImpl.class, Cnd.where(ActivityInstanceProperty.PROCESS_INSTANCE_ID.getPropertyName(),
				"=",processInstanceId)
				.and(ActivityInstanceProperty.NODE_ID.getPropertyName(),"=",nodeId)
				.and(ActivityInstanceProperty.STATE.getPropertyName(),"<",ActivityInstanceState.DELIMITER.getValue()));
		return result;
		

	}

	public void lockActivityInstance(String activityInstanceId) {
		// TODO Auto-generated method stub

	}

	public List<ActivityInstance> findActivityInstances(
			String processInstanceId, String activityId) {
		List result = dao().query(ActivityInstanceImpl.class, Cnd.where(ActivityInstanceProperty.PROCESS_INSTANCE_ID.getPropertyName(),
				"=",processInstanceId)
				.and(ActivityInstanceProperty.NODE_ID.getPropertyName(),"=",activityId));
		return (List<ActivityInstance>)result;
	}

	public List<ActivityInstance> findActivityInstances(String processInstanceId) {
		List result = dao().query(ActivityInstanceImpl.class, Cnd.where(ActivityInstanceProperty.PROCESS_INSTANCE_ID.getPropertyName(),
				"=",processInstanceId));
		return (List<ActivityInstance>)result;
	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ActivityInstanceImpl.class;
	}

	@Override
	public Class getEntityClass4History(Class interfaceClz) {
		return ActivityInstanceHistory.class;
	}
	
	

}
