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
package org.fireflow.engine.modules.persistence.hibernate;

import org.fireflow.engine.context.EngineModule;
import org.fireflow.engine.modules.persistence.PersistenceService;
import org.fireflow.engine.modules.persistence.PersistenceServiceDefaultImpl;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

/**
 * 
 * @author 非也 nychen2000@163.com Fire Workflow 官方网站：www.firesoa.com 或者
 *         www.fireflow.org
 * 
 */
public class PersistenceServiceHibernateImpl extends
		PersistenceServiceDefaultImpl implements PersistenceService,
		EngineModule {
	private HibernateTemplate hibernateTemplate;

	public final void setSessionFactory(SessionFactory sessionFactory) {
		if (this.hibernateTemplate == null
				|| sessionFactory != this.hibernateTemplate.getSessionFactory()) {
			this.hibernateTemplate = createHibernateTemplate(sessionFactory);
		}
	}

	private HibernateTemplate createHibernateTemplate(
			SessionFactory sessionFactory) {
		return new HibernateTemplate(sessionFactory);
	}

	public final SessionFactory getSessionFactory() {
		return (this.hibernateTemplate != null ? this.hibernateTemplate
				.getSessionFactory() : null);
	}

	public final HibernateTemplate getHibernateTemplate() {
		return this.hibernateTemplate;
	}

	public final void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	protected final Session getSession()
			throws DataAccessResourceFailureException, IllegalStateException {

		return getSession(this.hibernateTemplate.isAllowCreate());
	}

	protected final Session getSession(boolean allowCreate)
			throws DataAccessResourceFailureException, IllegalStateException {

		return (!allowCreate ? SessionFactoryUtils.getSession(
				getSessionFactory(), false) : SessionFactoryUtils.getSession(
				getSessionFactory(),
				this.hibernateTemplate.getEntityInterceptor(),
				this.hibernateTemplate.getJdbcExceptionTranslator()));
	}

	protected final DataAccessException convertHibernateAccessException(
			HibernateException ex) {
		return this.hibernateTemplate.convertHibernateAccessException(ex);
	}
	protected final void releaseSession(Session session) {
		SessionFactoryUtils.releaseSession(session, getSessionFactory());
	}
}
