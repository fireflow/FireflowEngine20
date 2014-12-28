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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.DocumentResult;
import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.Scope;
import org.fireflow.engine.entity.runtime.Variable;
import org.fireflow.engine.entity.runtime.impl.AbsVariable;
import org.fireflow.engine.entity.runtime.impl.VariableHistory;
import org.fireflow.engine.entity.runtime.impl.VariableImpl;
import org.fireflow.engine.modules.persistence.VariablePersister;
import org.firesoa.common.schema.NameSpaces;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class VariablePersisterHibernateImpl extends AbsPersisterHibernateImpl
		implements VariablePersister {
	Log log = LogFactory.getLog(VariablePersisterHibernateImpl.class);

	public Class getEntityClass4Runtime(Class interfaceClz){
		return VariableImpl.class;
	}

	public Class getEntityClass4History(Class interfaceClz){
		return VariableHistory.class;
	}


	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.VariablePersister#findVariable(java.lang.String, java.lang.String)
	 */
	public Variable findVariable(final String scopeId, final String name) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(VariableImpl.class);
				criteria.add(Restrictions.eq("scopeId", scopeId));
				criteria.add(Restrictions.eq("name", name));
				return criteria.uniqueResult();
			}
			
		});
		Variable v = (Variable)result;
		return v;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.VariablePersister#findVariables(java.lang.String)
	 */
	public java.util.List<Variable> findVariables(final String scopeId) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria = session.createCriteria(VariableImpl.class);
				criteria.add(Restrictions.eq("scopeId", scopeId));
				return criteria.list();
			}
			
		});
		List<Variable> vars = (java.util.List<Variable>)result;
		return vars;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.VariablePersister#deleteAllVariables()
	 */
	public void deleteAllVariables() {
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteVariable = "Delete From VariableImpl";
				Query q4DeleteVariable = session.createQuery(deleteVariable);
				q4DeleteVariable.executeUpdate();
				
				return null;
			}
		});	
	}


}
