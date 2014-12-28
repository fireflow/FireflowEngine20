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
				String deleteWorkItem = "Delete From WorkItemImpl";
				Query q4DeleteWorkItem = session.createQuery(deleteWorkItem);
				q4DeleteWorkItem.executeUpdate();
				
				String deleteScheduleJob = "Delete From ScheduleJobImpl";
				Query q4DeleteScheduleJob = session.createQuery(deleteScheduleJob);
				q4DeleteScheduleJob.executeUpdate();
				
				String deleteVariable = "Delete From VariableImpl";
				Query q4DeleteVariable = session.createQuery(deleteVariable);
				q4DeleteVariable.executeUpdate();
				
				
				String deleteActivityInstance  = "Delete From ActivityInstanceImpl";
				Query q4DeleteActivityInstance = session.createQuery(deleteActivityInstance);
				q4DeleteActivityInstance.executeUpdate();
				
				String deleteProcessInstance = "Delete From ProcessInstanceImpl";
				Query q4DeleteProcessInstance = session.createQuery(deleteProcessInstance);
				q4DeleteProcessInstance.executeUpdate();
				
				String deleteToken = "Delete From TokenImpl";
				Query q4DeleteToken = session.createQuery(deleteToken);
				q4DeleteToken.executeUpdate();
				
				String deleteProcessRepository = "Delete From ProcessRepositoryImpl";
				Query q4DeleteProcessRepository = session.createQuery(deleteProcessRepository);
				q4DeleteProcessRepository.executeUpdate();
				
//				String deleteServiceRepository = "Delete From ServiceRepositoryImpl";
//				Query q4DeleteServiceRepository = session.createQuery(deleteServiceRepository);
//				q4DeleteServiceRepository.executeUpdate();				
//
//				String deleteResourceRepository = "Delete From ResourceRepositoryImpl";
//				Query q4DeleteResourceRepository = session.createQuery(deleteResourceRepository);
//				q4DeleteResourceRepository.executeUpdate();	
//				
				return null;
			}
			
		});
	}

}
