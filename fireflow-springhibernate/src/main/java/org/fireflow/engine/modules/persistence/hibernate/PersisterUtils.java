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

import org.fireflow.client.query.Criterion;
import org.hibernate.criterion.Restrictions;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class PersisterUtils {
	public static org.hibernate.criterion.Criterion fireCriterion2HibernateCriterion(
			org.fireflow.client.query.Criterion fireCriterion) {
		String operation = fireCriterion.getOperation().trim();
		
		if (Criterion.OPERATION_EQ.equals(operation)){
			return Restrictions.eq(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_NE.equals(operation)){
			return Restrictions.ne(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_LIKE.equals(operation)){
			return Restrictions.like(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_GT.equals(operation)){
			return Restrictions.gt(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (Criterion.OPERATION_LT.equals(operation)){
			return Restrictions.lt(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}	
		else if (Criterion.OPERATION_GE.equals(operation)){
			return Restrictions.ge(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}		
		else if (Criterion.OPERATION_LE.equals(operation)){
			return Restrictions.le(fireCriterion.getEntityProperty().getPropertyName(), fireCriterion.getValues()[0]);
		}
		else if (operation.equals(Criterion.OPERATION_IS_NULL)){
			return Restrictions.isNull(fireCriterion.getEntityProperty().getPropertyName());
		}	
		else if (operation.equals(Criterion.OPERATION_IS_NOT_NULL)){
			return Restrictions.isNotNull(fireCriterion.getEntityProperty().getPropertyName());
		}	
		else if (operation.equals(Criterion.OPERATION_IN)){
			return Restrictions.in(fireCriterion.getEntityProperty().getPropertyName(),fireCriterion.getValues());
		}	
		else if (Criterion.OPERATION_BETWEEN.equals(operation)){
			return Restrictions.between(fireCriterion.getEntityProperty().getPropertyName(),fireCriterion.getValues()[0],fireCriterion.getValues()[1]);
		}		
		else if (Criterion.OPERATION_AND.equals(operation)){
			org.fireflow.client.query.Criterion left = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[0];
			org.fireflow.client.query.Criterion right = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[1];
			org.hibernate.criterion.Criterion hLeft = fireCriterion2HibernateCriterion(left);
			org.hibernate.criterion.Criterion hRight = fireCriterion2HibernateCriterion(right);
			return Restrictions.and(hLeft, hRight);
			
		}		
		else if (Criterion.OPERATION_OR.equals(operation)){
			org.fireflow.client.query.Criterion left = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[0];
			org.fireflow.client.query.Criterion right = (org.fireflow.client.query.Criterion)fireCriterion.getValues()[1];
			org.hibernate.criterion.Criterion hLeft = fireCriterion2HibernateCriterion(left);
			org.hibernate.criterion.Criterion hRight = fireCriterion2HibernateCriterion(right);
			return Restrictions.or(hLeft, hRight);
			
		}	
		return null;
	}
	
	public static org.hibernate.criterion.Order fireOrder2HibernateOrder(
			org.fireflow.client.query.Order fireOrder) {
		if (fireOrder.isAscending()){
			return org.hibernate.criterion.Order.asc(fireOrder.getEntityProperty().getPropertyName());
		}else{
			return org.hibernate.criterion.Order.desc(fireOrder.getEntityProperty().getPropertyName());
		}
	}
}
