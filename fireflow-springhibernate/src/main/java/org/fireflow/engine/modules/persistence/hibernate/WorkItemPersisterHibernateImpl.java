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

import org.apache.commons.lang.StringUtils;
import org.fireflow.engine.entity.runtime.WorkItem;
import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.engine.entity.runtime.impl.AbsWorkItem;
import org.fireflow.engine.entity.runtime.impl.WorkItemHistory;
import org.fireflow.engine.modules.persistence.WorkItemPersister;
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
public class WorkItemPersisterHibernateImpl extends AbsPersisterHibernateImpl implements WorkItemPersister {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.hibernate.AbsPersisterHibernateImpl#getEntityClass4Runtime(java.lang.Class)
	 */
	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return AbsWorkItem.class;
	}

	public Class getEntityClass4History(Class interfaceClz){
		
		return WorkItemHistory.class;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.WorkItemPersister#deleteWorkItemsInInitializedState(java.lang.String)
	 */
	public void deleteWorkItemsInInitializedState(final String activityInstanceId,final String parentWorkItemId) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = null;
                if (!StringUtils.isEmpty(parentWorkItemId)){
                	hql = "delete from org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl  where activityInstance.id=:activityInstanceId and parentWorkItemId=:parentWorkItemId and state=:state";
                }else{
                	hql = "delete from org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl  where activityInstance.id=:activityInstanceId and state=:state";
                }
                Query query = arg0.createQuery(hql);
                query.setString("activityInstanceId", activityInstanceId);
                if (!StringUtils.isEmpty(parentWorkItemId)){
                	query.setString("parentWorkItemId",parentWorkItemId);
                }                
                query.setParameter("state", WorkItemState.INITIALIZED);
                return query.executeUpdate();
            }
        });
		
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.WorkItemPersister#findWorkItemsForActivityInstance(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<WorkItem> findWorkItemsForActivityInstance(
			final String activityInstanceId) {
        List<WorkItem> result = (List<WorkItem>)this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "from org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl  where activityInstance.id=? ";
                Query query = arg0.createQuery(hql);
                query.setString(0, activityInstanceId);
                return query.list();
            }
        });
        
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkItem> findWorkItemsForActivityInstance(
			final String activityInstanceId, final String parentWorkItemId) {
        List<WorkItem> result = (List<WorkItem>)this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "from org.fireflow.engine.entity.runtime.impl.LocalWorkItemImpl  where activityInstance.id=? and parentWorkItemId=?";
                Query query = arg0.createQuery(hql);
                query.setString(0, activityInstanceId);
                query.setString(1, parentWorkItemId);
                return query.list();
            }
        });
        
        return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.WorkItemPersister#deleteAllWorkItems()
	 */
	public void deleteAllWorkItems() {
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteWorkItem = "Delete From AbsWorkItem";
				Query q4DeleteWorkItem = session.createQuery(deleteWorkItem);
				q4DeleteWorkItem.executeUpdate();
				
				return null;
			}
		});
		
	}
	
}
