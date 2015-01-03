/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.modules.persistence.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.Persister;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public abstract class AbsPersisterHibernateImpl // extends HibernateDaoSupport
		implements Persister {
	protected PersistenceService persistenceService = null;

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.Persister#List(org.fireflow.engine.WorkflowQuery)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends WorkflowEntity> java.util.List<T> list(final WorkflowQuery<T> q) {
		Class tmp = null;
		if (q.isQueryFromHistory()){
			tmp = this.getEntityClass4History(q.getEntityClass());
		}else{
			tmp = this.getEntityClass4Runtime(q.getEntityClass());
		}
		final Class entityClass = tmp;
		if (entityClass==null) return null;
		Object result = this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(entityClass);

						if (isConnectToActivityInstance(q)){
							criteria.createAlias("activityInstance", "activityInstance");
						}

						List<? extends org.fireflow.client.query.Criterion> fireCriterions = q
								.getAllCriterions();
						if (fireCriterions != null && fireCriterions.size() > 0) {
							for (org.fireflow.client.query.Criterion fireCriterion : fireCriterions) {
								org.hibernate.criterion.Criterion hibernateCriterion = PersisterUtils
										.fireCriterion2HibernateCriterion(fireCriterion);
								criteria.add(hibernateCriterion);
							}
						}
						List<org.fireflow.client.query.Order> fireOrders = q
								.getAllOrders();
						if (fireOrders != null && fireOrders.size() > 0) {
							for (org.fireflow.client.query.Order fireOrder : fireOrders) {
								org.hibernate.criterion.Order hibernateOrder = PersisterUtils
										.fireOrder2HibernateOrder(fireOrder);
								criteria.addOrder(hibernateOrder);
							}
						}
						

						if (q.getPageNumber()>0){
							int pageSize = q.DEFAULT_PAGE_SIZE;
							if (q.getPageSize()>0){
								pageSize = q.getPageSize();
							}
							int first = (q.getPageNumber()-1)*pageSize;
							
							criteria.setFirstResult(first);//从0开始计数；
							
							criteria.setMaxResults(pageSize);
						}
						


						return criteria.list();
					}

				});
		return (java.util.List<T>)result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.Persister#count(org.fireflow.engine.WorkflowQuery)
	 */
	public <T extends WorkflowEntity> int count(final WorkflowQuery<T> q) {
		Class tmp = null;
		if (q.isQueryFromHistory()){
			tmp = this.getEntityClass4History(q.getEntityClass());
		}else{
			tmp = this.getEntityClass4Runtime(q.getEntityClass());
		}
		final Class entityClass = tmp;
		if (entityClass==null) return 0;
		Object result = this.getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session.createCriteria(entityClass);
						if (isConnectToActivityInstance(q)){
							criteria.createAlias("activityInstance", "activityInstance");
						}
						List<? extends org.fireflow.client.query.Criterion> fireCriterions = q
								.getAllCriterions();
						if (fireCriterions != null && fireCriterions.size() > 0) {
							for (org.fireflow.client.query.Criterion fireCriterion : fireCriterions) {
								org.hibernate.criterion.Criterion hibernateCriterion = PersisterUtils
										.fireCriterion2HibernateCriterion(fireCriterion);
								criteria.add(hibernateCriterion);
							}
						}

						
						criteria.setProjection(Projections.rowCount());

						return criteria.uniqueResult();
					}

				});
		if (result instanceof Integer) {
			return (Integer) result;
		} else if (result instanceof Long) {
			return ((Long) result).intValue();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.Persister#find(java.lang.Class, java.lang.String)
	 */
	public <T extends WorkflowEntity> T fetch(Class<T> entityClz, String entityId) {
		if (entityId==null || entityId.trim().equals(""))return null;
		Class clz = this.getEntityClass4Runtime(entityClz);
		Object obj = null;
		if (clz!=null){
			obj = this.getHibernateTemplate().get(clz, entityId);
		}
		if (obj==null){
			clz = this.getEntityClass4History(entityClz);
			obj = this.getHibernateTemplate().get(clz, entityId);
		}
		return (T)obj;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.Persister#saveOrUpdate(java.lang.Object)
	 */
	public void saveOrUpdate(Object entity) {
		this.getHibernateTemplate().saveOrUpdate(entity);
	}

//	public abstract <T> Class<T> getEntityClass(WorkflowQuery<T> q);
	
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
	
	private boolean isConnectToActivityInstance(WorkflowQuery q){
		/*
		List<org.fireflow.client.query.Criterion> criterionList = q.getAllCriterions();
		if (criterionList!=null){
			for (org.fireflow.client.query.Criterion c : criterionList){
				if (c.getEntityProperty().getColumnName().startsWith("ACTIVITY_INSTANCE_$_")){
					return true;
				}
			}
		}
		List<org.fireflow.client.query.Order> fireOrders = q.getAllOrders();
		if (fireOrders!=null){
			for (org.fireflow.client.query.Order order : fireOrders){
				if(order.getEntityProperty().getColumnName().startsWith("ACTIVITY_INSTANCE_$_")){
					return true;
				}
			}
		}
		return false;
		*/
		
		/* 所有的对象间关联被取消，2014-12-30 非也，
		Class entityClass = q.getEntityClass();
		
		if (entityClass.isAssignableFrom(WorkItem.class) || entityClass.isAssignableFrom(ScheduleJob.class)) {
			return true;
		}
		*/
		return false;
	}

	/**
	 * @return the persistenceService
	 */
	public PersistenceService getPersistenceService() {
		return persistenceService;
	}

	/**
	 * @param persistenceService the persistenceService to set
	 */
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}
	
	public HibernateTemplate getHibernateTemplate(){
		PersistenceServiceHibernateImpl svc = (PersistenceServiceHibernateImpl)this.getPersistenceService();
		
		return svc.getHibernateTemplate();
	}
}
