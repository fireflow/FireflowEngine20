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
package org.fireflow.demo.common.dao;

import java.util.List;

import org.fireflow.demo.biztrip.entity.BusinessTripBasicInfo;
import org.fireflow.demo.common.entity.CommonApprovalInfo;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class CommonApprovalInfoDaoImpl extends HibernateDaoSupport implements
		ICommonApprovalInfoDao {

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.ICommonApprovalInfoDao#create(org.fireflow.demo.biztrip.entity.CommonApprovalInfo)
	 */
	public void create(CommonApprovalInfo instance) {
		getHibernateTemplate().save(instance);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.ICommonApprovalInfoDao#remove(org.fireflow.demo.biztrip.entity.CommonApprovalInfo)
	 */
	public void remove(CommonApprovalInfo instance) {
		getHibernateTemplate().delete(instance);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.ICommonApprovalInfoDao#modify(org.fireflow.demo.biztrip.entity.CommonApprovalInfo)
	 */
	public void modify(CommonApprovalInfo instance) {
		getHibernateTemplate().update(instance);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.ICommonApprovalInfoDao#findById(int)
	 */
	public CommonApprovalInfo findById(int id) {
		return (CommonApprovalInfo)getHibernateTemplate().get(CommonApprovalInfo.class, id);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.ICommonApprovalInfoDao#findAll()
	 */
	public List<CommonApprovalInfo> findAll() {
		List<CommonApprovalInfo> results = 
			(List<CommonApprovalInfo>) getHibernateTemplate().find("FROM CommonApprovalInfoServlet ORDER BY bizId");
		return results;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.demo.biztrip.dao.ICommonApprovalInfoDao#query(java.lang.String)
	 */
	public List<CommonApprovalInfo> query(String condition) {
		List<CommonApprovalInfo> results = (List<CommonApprovalInfo>) getHibernateTemplate().find("FROM CommonApprovalInfoServlet WHERE " + condition);
		return results;
	}

}
