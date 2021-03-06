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
package org.fireflow;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class JunitInitializer extends HibernateDaoSupport{
	public void clearData(){
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteWorkItem = "Delete From org.fireflow.engine.entity.runtime.impl.AbsWorkItem";
				Query q4DeleteWorkItem = session.createQuery(deleteWorkItem);
				q4DeleteWorkItem.executeUpdate();
				
				String deleteScheduleJob = "Delete From org.fireflow.engine.entity.runtime.impl.ScheduleJobImpl";
				Query q4DeleteScheduleJob = session.createQuery(deleteScheduleJob);
				q4DeleteScheduleJob.executeUpdate();
				
				String deleteVariable = "Delete From org.fireflow.engine.entity.runtime.impl.VariableImpl";
				Query q4DeleteVariable = session.createQuery(deleteVariable);
				q4DeleteVariable.executeUpdate();
				
				
				String deleteActivityInstance  = "Delete From org.fireflow.engine.entity.runtime.impl.ActivityInstanceImpl";
				Query q4DeleteActivityInstance = session.createQuery(deleteActivityInstance);
				q4DeleteActivityInstance.executeUpdate();
				
				String deleteProcessInstance = "Delete From org.fireflow.engine.entity.runtime.impl.ProcessInstanceImpl";
				Query q4DeleteProcessInstance = session.createQuery(deleteProcessInstance);
				q4DeleteProcessInstance.executeUpdate();
				
				Query q1 = session.createQuery("FROM org.fireflow.pvm.kernel.impl.TokenImpl");
				List l1 = q1.list();
				
				String deleteToken = "Delete From org.fireflow.pvm.kernel.impl.TokenImpl";
				Query q4DeleteToken = session.createQuery(deleteToken);
				q4DeleteToken.executeUpdate();
				
				Query q = session.createQuery("FROM org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl");
				List l = q.list();
				
				String deleteProcessRepository = "Delete From org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl";
				Query q4DeleteProcessRepository = session.createQuery(deleteProcessRepository);
				q4DeleteProcessRepository.executeUpdate();
				
				
				return null;
			}
			
		});
	}

}
