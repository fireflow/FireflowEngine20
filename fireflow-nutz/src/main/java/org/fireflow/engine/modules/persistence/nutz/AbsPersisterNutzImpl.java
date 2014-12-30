package org.fireflow.engine.modules.persistence.nutz;

import java.util.List;
import java.util.UUID;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.query.Order;
import org.fireflow.engine.entity.AbsWorkflowEntity;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.config.impl.FireflowConfigImpl;
import org.fireflow.engine.entity.config.impl.ReassignConfigImpl;
import org.fireflow.engine.entity.repository.impl.ProcessDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.entity.runtime.impl.RemoteWorkItemImpl;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.Persister;
import org.fireflow.pvm.kernel.impl.TokenImpl;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.trans.Atom;

public abstract class AbsPersisterNutzImpl implements Persister {
	private PersistenceService persistenceService = null;
	protected FieldFilter fieldFilter = null;
	
	public AbsPersisterNutzImpl(){
		fieldFilter = FieldFilter.create(TokenImpl.class, "^(elementInstanceId|businessPermitted|value|stepNumber|state)$")
						.set(ActivityInstanceImpl.class,"^(bizId|subBizId|state|suspended|startedTime|expiredTime|endTime|canBeWithdrawn|note)$")
						.set(ProcessInstanceImpl.class, "^(bizId|state|suspended|startedTime|expiredTime|endTime|tokenId|note)$")
						.set(LocalWorkItemImpl.class, "^(state|claimedTime|endTime|expiredTime|note|remoteWorkItemId|attachmentId|attachmentType)$")
						.set(RemoteWorkItemImpl.class, "^(state|claimedTime|endTime|expiredTime|note|remoteWorkItemId|attachmentId|attachmentType)$")
						.set(ScheduleJobImpl.class, "^(triggeredTimes|endTime|state|note)$")
						.set(VariableImpl.class, "^(headers|dataType|payload)$")
						.set(ReassignConfigImpl.class, "^(alive|startTime|endTime|agentId|agentName|agentType)$")
						.set(FireflowConfigImpl.class, "^(configName|configValue|description|lastEditor)$")
						.set(ProcessDescriptorImpl.class, "^(name|displayName|description|packageId|publishState|validDateFrom|validDateTo|timerStart|hasCallbackService|ownerId|ownerName|approver|approveTime|lastEditor|updateLog)$")
						.set(ProcessRepositoryImpl.class, "^(name|displayName|description|packageId|publishState|validDateFrom|validDateTo|timerStart|hasCallbackService|ownerId|ownerName|approver|approveTime|lastEditor|updateLog|processContent)$");
		
	}

	/**
	 * TODO 暂时未考虑workitem表和activityinstance表关联查询条件
	 */
	public <T extends WorkflowEntity> T fetch(Class<T> entityClz, String entityId) {
		if (entityId==null || entityId.trim().equals(""))return null;
		Class clz = this.getEntityClass4Runtime(entityClz);
		Object obj = null;
		if (clz!=null){
			obj = dao().fetch(clz, entityId);
			if (WorkItem.class.isAssignableFrom(clz) || ScheduleJob.class.isAssignableFrom(clz)){
				dao().fetchLinks(obj, null);
			}
		}
		if (obj==null){
			clz = this.getEntityClass4History(entityClz);
			obj = dao().fetch(clz, entityId);
			if (WorkItem.class.isAssignableFrom(clz) || ScheduleJob.class.isAssignableFrom(clz)){
				dao().fetchLinks(obj, null);
			}
		}
		return (T)obj;
	}

	public <T extends WorkflowEntity> List<T> list(WorkflowQuery<T> q) {
		Class tmp = null;
		if (q.isQueryFromHistory()){
			tmp = this.getEntityClass4History(q.getEntityClass());
		}else{
			tmp = this.getEntityClass4Runtime(q.getEntityClass());
		}
		final Class entityClass = tmp;
		if (entityClass==null) return null;
		
		SimpleCriteria nutzCri = null;
		
		//查询条件转换
		List<? extends org.fireflow.client.query.Criterion> fireCriterions = q
		.getAllCriterions();
		if (fireCriterions != null && fireCriterions.size() > 0) {
			for (org.fireflow.client.query.Criterion fireCriterion : fireCriterions) {
				if (nutzCri==null){
					nutzCri = Cnd.cri();
					
				}
				nutzCri.where().and(NutzPersisterUtils.fireCriterion2NutzSqlExpression(fireCriterion));

			}
		}
		if (nutzCri==null){
			nutzCri = Cnd.cri();
			
		}
		
		//排序转换
		List<Order> allOrders = q.getAllOrders();
		if (allOrders!=null && allOrders.size()>0){
			for (Order order : allOrders){
				if (order.isAscending()){
					nutzCri.getOrderBy().asc(order.getEntityProperty().getPropertyName());
				}else{
					nutzCri.getOrderBy().desc(order.getEntityProperty().getPropertyName());
				}
				
			}
		}
		//分页
		Pager pager = null;
		if (q.getPageNumber()>0){
			if (q.getPageSize()<=0){
				pager = dao().createPager(q.getPageNumber(), WorkflowQuery.DEFAULT_PAGE_SIZE);
			}else{
				pager = dao().createPager(q.getPageNumber(), q.getPageSize());
			}
			
		}
		
		if (pager!=null){
			return dao().query(entityClass, nutzCri, pager);
		}else{
			return dao().query(entityClass, nutzCri);
		}
	}

	/**
	 * TODO 暂时未考虑workitem表和activityinstance表关联查询条件
	 */
	public <T extends WorkflowEntity> int count(WorkflowQuery<T> q) {
		Class tmp = null;
		if (q.isQueryFromHistory()){
			tmp = this.getEntityClass4History(q.getEntityClass());
		}else{
			tmp = this.getEntityClass4Runtime(q.getEntityClass());
		}
		final Class entityClass = tmp;
		if (entityClass==null) return 0;
		
		SimpleCriteria nutzCri = null;
		List<? extends org.fireflow.client.query.Criterion> fireCriterions = q
		.getAllCriterions();
		if (fireCriterions != null && fireCriterions.size() > 0) {
			for (org.fireflow.client.query.Criterion fireCriterion : fireCriterions) {
				if (nutzCri==null){
					nutzCri = Cnd.cri();
					
				}
				nutzCri.where().and(NutzPersisterUtils.fireCriterion2NutzSqlExpression(fireCriterion));

			}
		}

		return dao().count(entityClass, nutzCri);

	}

	public void saveOrUpdate(Object entity) {
		final AbsWorkflowEntity wfEntity = (AbsWorkflowEntity)entity;
		String id = wfEntity.getId();
		if (id==null || id.trim().equals("")){
			//insert 操作
			String newId = UUID.randomUUID().toString();
			wfEntity.setId(newId);
			dao().insert(wfEntity);
		}else{
			//update 操作
			//TODO 会出现事物不一致的问题吗？
			fieldFilter.run(new Atom(){
				public void run(){
					dao().update(wfEntity);
				}
			});
			
		}
	}

	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;

	}
	
	public NutDao dao(){
		if (persistenceService==null) return null;
		return ((PersistenceServiceNutzImpl)persistenceService).getDao();
	}
	
	/**
	 * 获得运行时表的对象class
	 * @return
	 */
	public abstract Class getEntityClass4Runtime(Class interfaceClz);
	
	/**
	 * 获得历史表的对象class
	 * @return
	 */
	public Class getEntityClass4History(Class interfaceClz){
		return null;
	}

}
