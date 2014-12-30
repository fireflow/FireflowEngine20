package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemProperty;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.WorkItemHistory;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;

public class WorkItemPersisterNutzImpl extends AbsPersisterNutzImpl implements
		WorkItemPersister {

	public void deleteAllWorkItems() {
		dao().clear(LocalWorkItemImpl.class);

	}

	public void deleteWorkItemsInInitializedState(String activityInstanceId,
			String parentWorkItemId) {
		Sql sql = null;
		if (StringUtils.isEmpty(parentWorkItemId)){
			sql = Sqls.create("DELETE FROM T_FF_RT_WORKITEM where ACTIVITY_INSTANCE_ID=@actInstId  AND STATE=@state");
			sql.params().set("actInstId", activityInstanceId);
			sql.params().set("state", 0);
		}else{
			sql = Sqls.create("DELETE FROM T_FF_RT_WORKITEM where ACTIVITY_INSTANCE_ID=@actInstId AND PARENT_WORKITEM_ID=@pWorkItemId AND STATE=@state");
			sql.params().set("actInstId", activityInstanceId);
			sql.params().set("pWorkItemId",parentWorkItemId);
			sql.params().set("state", 0);
		}

		dao().execute(sql);

	}

	public List<WorkItem> findWorkItemsForActivityInstance(
			String activityInstanceId) {
		List l = dao().query(LocalWorkItemImpl.class, Cnd.where(WorkItemProperty.ACTIVITY_INSTANCE_ID.getPropertyName(),"=",activityInstanceId));
		return (List<WorkItem>)l;
	}

	public List<WorkItem> findWorkItemsForActivityInstance(
			String activityInstanceId, String parentWorkItemId) {
		List l = dao().query(LocalWorkItemImpl.class, Cnd.where(WorkItemProperty.ACTIVITY_INSTANCE_ID.getPropertyName(),"=",activityInstanceId)
														.and(WorkItemProperty.PARENT_WORKITEM_ID.getPropertyName(), "=", parentWorkItemId));
		return (List<WorkItem>)l;
	}

	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return LocalWorkItemImpl.class;
	}

	@Override
	public Class getEntityClass4History(Class interfaceClz) {
		return WorkItemHistory.class;
	}

	
}
