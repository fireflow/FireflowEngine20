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

import org.fireflow.engine.entity.runtime.impl.ProcessInstanceHistory;
import org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl;
import org.fireflow.engine.modules.persistence.ProcessInstancePersister;
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
public class ProcessInstancePersisterHibernateImpl extends AbsPersisterHibernateImpl
		implements ProcessInstancePersister {

	/**
	 * 获得运行时表的对象class
	 * @return
	 */
	public Class getEntityClass4Runtime(Class interfaceClz){
		return ProcessInstanceImpl.class;
	}
	
	/**
	 * 获得历史表的对象class
	 * @return
	 */
	public Class getEntityClass4History(Class interfaceClz){
		return ProcessInstanceHistory.class;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ProcessInstancePersister#deleteAllProcessInstances()
	 */
	public void deleteAllProcessInstances() {
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteProcessInstance = "Delete From org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl";
				Query q4DeleteProcessInstance = session.createQuery(deleteProcessInstance);
				q4DeleteProcessInstance.executeUpdate();
				
				return null;
			}
		});	
		
	}
	
}
