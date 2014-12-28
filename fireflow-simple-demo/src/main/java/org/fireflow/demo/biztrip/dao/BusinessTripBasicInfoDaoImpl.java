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
package org.fireflow.demo.biztrip.dao;

import java.util.List;

import org.fireflow.demo.biztrip.entity.BusinessTripBasicInfo;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class BusinessTripBasicInfoDaoImpl extends HibernateDaoSupport implements
		IBusinessTripBasicInfoDao {

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao#create(org.fireflow.demo.biztrip.entity.BusinessTripBasicInfo)
	 */
	public void create(BusinessTripBasicInfo instance) {
		getHibernateTemplate().save(instance);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao#remove(org.fireflow.demo.biztrip.entity.BusinessTripBasicInfo)
	 */
	public void remove(BusinessTripBasicInfo instance) {
		getHibernateTemplate().delete(instance);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao#modify(org.fireflow.demo.biztrip.entity.BusinessTripBasicInfo)
	 */
	public void modify(BusinessTripBasicInfo instance) {
		getHibernateTemplate().update(instance);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao#findById(int)
	 */
	public BusinessTripBasicInfo findById(int id) {
		return (BusinessTripBasicInfo)getHibernateTemplate().get(BusinessTripBasicInfo.class, id);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao#findAll()
	 */
	public List<BusinessTripBasicInfo> findAll() {
		List<BusinessTripBasicInfo> results = 
			(List<BusinessTripBasicInfo>) getHibernateTemplate().find("FROM BusinessTripBasicInfo ORDER BY bizId");
		return results;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.IBusinessTripBasicInfoDao#query(java.lang.String)
	 */
	public List<BusinessTripBasicInfo> query(String condition) {
		List<BusinessTripBasicInfo> results = (List<BusinessTripBasicInfo>) getHibernateTemplate().find("FROM BusinessTripBasicInfo WHERE " + condition);
		return results;
	}

}
