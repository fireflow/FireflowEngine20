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

import org.fireflow.engine.entity.config.ReassignConfig;
import org.fireflow.engine.entity.config.impl.ReassignConfigImpl;
import org.fireflow.engine.modules.persistence.ReassignConfigPersister;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class ReassignConfigPersisterHibernateImpl extends AbsPersisterHibernateImpl implements
		ReassignConfigPersister {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ReassignConfigPersister#findReassignConfig(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ReassignConfig> findReassignConfig(final String processId,
			final String processType, final String activityId,
			final String userId) {
		List<ReassignConfig> result = (List<ReassignConfig>) this
				.getHibernateTemplate().execute(new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria criteria = session
								.createCriteria(ReassignConfigImpl.class);
						criteria.add(Restrictions.eq("processId", processId))
								.add(
										Restrictions.eq("processType",
												processType)).add(
										Restrictions.lt("activityId",
												activityId)).add(
										Restrictions.lt("grantorId", userId));

						return criteria.list();
					}

				});

		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.hibernate.AbsPersisterHibernateImpl#getEntityClass4Runtime(java.lang.Class)
	 */
	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ReassignConfigImpl.class;
	}


}
