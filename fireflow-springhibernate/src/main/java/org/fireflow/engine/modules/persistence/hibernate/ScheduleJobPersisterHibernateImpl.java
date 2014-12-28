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

import org.fireflow.engine.entity.runtime.ScheduleJob;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobHistory;
import org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl;
import org.fireflow.engine.modules.persistence.ScheduleJobPersister;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ScheduleJobPersisterHibernateImpl extends
		AbsPersisterHibernateImpl implements ScheduleJobPersister {



	public Class getEntityClass4Runtime(Class interfaceClz){
		return ScheduleJobImpl.class;
	}

	public Class getEntityClass4History(Class interfaceClz){
		return ScheduleJobHistory.class;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.ScheduleJobPersister#findScheduleJob4ActivityInstance(java.lang.String)
	 */
	public java.util.List<ScheduleJob> findScheduleJob4ActivityInstance(
			final String activityInstanceId) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query q = session.createQuery("from ScheduleJobImpl c where c.activityInstance.id=:activityInstanceId");
				q.setString("activityInstanceId", activityInstanceId);
				return q.list();
			}
			
		});
		return (java.util.List<ScheduleJob>)result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ScheduleJobPersister#deleteAllScheduleJobs()
	 */
	public void deleteAllScheduleJobs() {
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteScheduleJob = "Delete From ScheduleJobImpl";
				Query q4DeleteScheduleJob = session.createQuery(deleteScheduleJob);
				q4DeleteScheduleJob.executeUpdate();
				
				return null;
			}
		});	
		
	}

}
