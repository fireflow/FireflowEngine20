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

import org.fireflow.demo.ou_management.entity.DemoDepartment;
import org.fireflow.demo.ou_management.entity.DemoUser;
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
public class DemoDepartmentDaoImpl extends HibernateDaoSupport implements
		IDemoDepartmentDao {
	public DemoDepartment findDepartmentById(String id){
		return (DemoDepartment)this.getHibernateTemplate().load(DemoDepartment.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<DemoDepartment> findAllTopDepartments(){
		List<DemoDepartment> obj = (List<DemoDepartment>)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria cretiria = session.createCriteria(DemoDepartment.class)
							.add(Restrictions.or(Restrictions.isNull("parentDepartmentId"), Restrictions.eq("parentDepartmentId", "")));
				
				return cretiria.list();
			}
			
		});
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<DemoDepartment> findChildDepartments(final String parentId){
		List<DemoDepartment> obj = (List<DemoDepartment>)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria cretiria = session.createCriteria(DemoDepartment.class)
							.add(Restrictions.eq("parentDepartmentId",parentId));
				
				return cretiria.list();
			}
			
		});
		return obj;
	}
}
