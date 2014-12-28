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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.fireflow.engine.entity.runtime.WorkItemState;
import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
public class AssignmentStrategyType implements UserType, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8996470819246200319L;

	private static final int[] TYPES = new int[] { Types.VARCHAR };  
	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
	 */
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		String str = (String)arg0;
		return WorkItemAssignmentStrategy.fromValue(str);
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	public Serializable disassemble(Object arg0) throws HibernateException {
		return ((WorkItemAssignmentStrategy)arg0).getValue();
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		WorkItemAssignmentStrategy _x = (WorkItemAssignmentStrategy)x;
		WorkItemAssignmentStrategy _y = (WorkItemAssignmentStrategy)y;
		if (_x!=null){
			return _x.equals(_y);
		}else{
			if (_y!=null){
				return false;
			}else{
				return true;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	public int hashCode(Object arg0) throws HibernateException {
		return ((WorkItemAssignmentStrategy)arg0).hashCode();
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		String str = (String) Hibernate.STRING.nullSafeGet(rs,names[0]);
		if (str!=null){
			return WorkItemAssignmentStrategy.fromValue(str);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException {
		if (value != null) {
			Hibernate.STRING.nullSafeSet(st, ((WorkItemAssignmentStrategy)value).getValue(), index);
		} else {
			Hibernate.STRING.nullSafeSet(st, null, index);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		return arg0;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	public Class returnedClass() {
		return WorkItemAssignmentStrategy.class;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return TYPES;
	}

}
