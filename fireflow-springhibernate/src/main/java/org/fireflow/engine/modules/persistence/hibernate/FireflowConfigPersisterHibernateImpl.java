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

import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.config.impl.FireflowConfigImpl;
import org.fireflow.engine.modules.persistence.FireflowConfigPersister;
import org.fireflow.engine.modules.persistence.PersistenceService;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public class FireflowConfigPersisterHibernateImpl  extends AbsPersisterHibernateImpl implements
		FireflowConfigPersister {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.hibernate.AbsPersisterHibernateImpl#getEntityClass4Runtime(java.lang.Class)
	 */
	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		
		return FireflowConfigImpl.class;
	}

}
