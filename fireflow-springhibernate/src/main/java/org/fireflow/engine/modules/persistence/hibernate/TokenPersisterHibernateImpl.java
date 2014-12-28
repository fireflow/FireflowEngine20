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

import org.fireflow.engine.modules.persistence.TokenPersister;
import org.fireflow.pvm.kernel.OperationContextName;
import org.fireflow.pvm.kernel.Token;
import org.fireflow.pvm.kernel.TokenState;
import org.fireflow.pvm.kernel.impl.TokenHistory;
import org.fireflow.pvm.kernel.impl.TokenImpl;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class TokenPersisterHibernateImpl extends AbsPersisterHibernateImpl
		implements TokenPersister {


	public Class getEntityClass4Runtime(Class interfaceClz){
		return TokenImpl.class;
	}

	public Class getEntityClass4History(Class interfaceClz){
		return TokenHistory.class;
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.TokenPersister#findChildTokens(org.fireflow.pvm.kernel.Token)
	 */
	public java.util.List<Token> findChildTokens(final Token token) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TokenImpl.class);
				c.add(Restrictions.eq("parentTokenId", token.getId()));
							
				return c.list();
			}
			
		});
		return (java.util.List<Token>)result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.TokenPersister#findChildTokens4Compensation(org.fireflow.pvm.kernel.Token)
	 */
	public java.util.List<Token> findChildTokens4Compensation(final Token token) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TokenImpl.class);
				c.add(Restrictions.eq("parentTokenId", token.getId()));
				c.add(Restrictions.eq("operationContextName", OperationContextName.NORMAL))	;		
				c.addOrder(Order.desc("stepNumber"));
				return c.list();
			}
			
		});
		return (java.util.List<Token>)result;
	}
	
	public List<Token> findSiblings(final Token token){
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TokenImpl.class);
				c.add(Restrictions.eq("processInstanceId", token.getProcessInstanceId()));
				c.add(Restrictions.eq("elementId", token.getElementId()));
				c.add(Restrictions.eq("operationContextName", token.getOperationContextName()))	;	
				c.add(Restrictions.lt("state", TokenState.DELIMITER));

				return c.list();
			}
			
		});
		return (List<Token>)result;
	}
	
	public Token findTokenByElementInstanceId(final String elementInstanceId){
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TokenImpl.class);
				c.add(Restrictions.eq("elementInstanceId", elementInstanceId));
				return c.uniqueResult();
			}


			
		});
		return (Token)result;
	}
	
	
	public int countAliveToken(final String processInstanceId,final String nodeId,final OperationContextName operationContextName){
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TokenImpl.class);
				c.add(Restrictions.eq("processInstanceId", processInstanceId));
				c.add(Restrictions.eq("elementId", nodeId));
				c.add(Restrictions.eq("operationContextName", operationContextName))	;
				c.add(Restrictions.lt("state", TokenState.DELIMITER));
				c.setProjection(Projections.rowCount());
				return c.uniqueResult();
			}
			
		});
		if (result ==null){
			return 0;
		}
		else if (result instanceof Integer){
			return ((Integer)result).intValue();
		}
		else if (result instanceof Long){
			return ((Long)result).intValue();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.persistence.TokenPersister#findParentToken(org.fireflow.pvm.kernel.Token)
	 */
	public Token findParentToken(Token token) {
		String pid = token.getParentTokenId();
		if (pid==null || pid.trim().equals("")){
			return null;
		}
		return this.find(TokenImpl.class, pid);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.TokenPersister#findAttachedTokens(org.fireflow.pvm.kernel.Token)
	 */
	public List<Token> findAttachedTokens(final Token token) {
		Object result = this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(TokenImpl.class);
				c.add(Restrictions.eq("attachedToToken", token.getId()));

				return c.list();
			}
			
		});
		return (List<Token>)result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.TokenPersister#deleteAllTokens()
	 */
	public void deleteAllTokens() {
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String deleteToken = "Delete From TokenImpl";
				Query q4DeleteToken = session.createQuery(deleteToken);
				q4DeleteToken.executeUpdate();
				
				return null;
			}
		});	
		
	}
	
//	public void deleteToken(Token token){
//		this.getHibernateTemplate().delete(token);
//	}

}
