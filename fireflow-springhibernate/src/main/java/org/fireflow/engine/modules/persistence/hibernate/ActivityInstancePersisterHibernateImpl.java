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

import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceState;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl;
import org.fireflow.engine.modules.persistence.ActivityInstancePersister;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ActivityInstancePersisterHibernateImpl extends AbsPersisterHibernateImpl implements
		ActivityInstancePersister {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.ActivityInstancePersister#countAliveActivityInstance(java.lang.String, java.lang.String)
	 */
	public int countAliveActivityInstance(final String processInstanceId,
			final String nodeId) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(ActivityInstanceImpl.class);
				criteria.add(Restrictions.eq("processInstanceId", processInstanceId))
					.add(Restrictions.eq("nodeId", nodeId))
					.add(Restrictions.lt("state", ActivityInstanceState.DELIMITER))
					.setProjection(Projections.rowCount());
				
				return criteria.uniqueResult();
			}
			
		});
		
		if (result instanceof Integer){
			return (Integer)result;
		}
		else if (result instanceof Long){
			return ((Long)result).intValue();
		}
		return 0;
	}

	/**
	 * 获得运行时表的对象class
	 * @return
	 */
	public Class getEntityClass4Runtime(Class interfaceClz){
		return ActivityInstanceImpl.class;
	}
	
	/**
	 * 获得历史表的对象class
	 * @return
	 */
	public Class getEntityClass4History(Class interfaceClz){
		return ActivityInstanceHistory.class;
	}
	
	public void lockActivityInstance(String activityInstanceId){
    	this.getHibernateTemplate().get(ActivityInstanceImpl.class, activityInstanceId,LockMode.UPGRADE);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ActivityInstancePersister#findActivityInstances(java.lang.String, java.lang.String)
	 */
	public List<ActivityInstance> findActivityInstances(
			final String processInstanceId, final String activityId) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(ActivityInstanceImpl.class);
				criteria.add(Restrictions.eq("processInstanceId", processInstanceId))
					.add(Restrictions.eq("nodeId", activityId))
					.addOrder(Order.desc("stepNumber"));
				
				return criteria.list();
			}
			
		});
		
		return (List<ActivityInstance>)result;
	}
	
	public List<ActivityInstance> findActivityInstances(final String processInstanceId){
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(ActivityInstanceImpl.class);
				criteria.add(Restrictions.eq("processInstanceId", processInstanceId))
					.addOrder(Order.desc("stepNumber"));
				
				return criteria.list();
			}
			
		});
		
		return (List<ActivityInstance>)result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ActivityInstancePersister#deleteAllActivityInstances()
	 */
	public void deleteAllActivityInstances() {
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteActivityInstance  = "Delete From org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl";
				Query q4DeleteActivityInstance = session.createQuery(deleteActivityInstance);
				q4DeleteActivityInstance.executeUpdate();
				
				return null;
			}
		});	
		
	}
}
