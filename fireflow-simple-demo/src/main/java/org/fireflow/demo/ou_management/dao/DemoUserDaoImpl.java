/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.demo.ou_management.dao;

import java.sql.SQLException;
import java.util.List;

import org.fireflow.demo.ou_management.entity.DemoUser;
import org.fireflow.engine.modules.ousystem.User;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class DemoUserDaoImpl extends HibernateDaoSupport implements
		IDemoUserDao {

	/* (non-Javadoc)
	 * @see org.fireflow.demo.ou_management.dao.IDemoUserDao#authenticateUser(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public DemoUser authenticateUser(final String userId, final String password) {
		Object obj = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria cretiria = session.createCriteria(DemoUser.class)
							.add(Restrictions.eq("userId", userId))
							.add(Restrictions.eq("password", password));
				
				return cretiria.uniqueResult();
			}
			
		});
		DemoUser user = (DemoUser)obj;
		return user;
	}

	public DemoUser findUserById(String userId){
		return (DemoUser)this.getHibernateTemplate().load(DemoUser.class, userId);
	}
	
	@SuppressWarnings("unchecked")
	public List<DemoUser> findUsersInDepartment(final String deptId){
		List<DemoUser> obj = (List<DemoUser>)this.getHibernateTemplate().execute(new HibernateCallback(){

	
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria cretiria = session.createCriteria(DemoUser.class)
							.add(Restrictions.eq("departmentId", deptId));
				
				return cretiria.list();
			}
			
		});
		
		return obj;
	}
}
