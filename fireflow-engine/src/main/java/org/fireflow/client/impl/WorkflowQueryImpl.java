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
package org.fireflow.client.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.client.query.AbsCriterion;
import org.fireflow.client.query.Criterion;
import org.fireflow.client.query.Order;
import org.fireflow.client.query.Restrictions;
import org.fireflow.client.query.WorkflowQueryDelegate;
import org.fireflow.engine.entity.WorkflowEntity;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.entity.runtime.ActivityInstanceProperty;
import org.fireflow.server.support.ClassXmlAdapter;
import org.fireflow.server.support.CriterionListXmlAdapter;

/**
 * 
 * 
 * @author 非也
 * @version 2.0
 */
@XmlRootElement(name="workflowQuery")
@XmlType(name="workflowQueryType",propOrder={"interfaceClass","criterions","orders","firstResult","maxResults","queryFromHistory"})
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowQueryImpl<T extends WorkflowEntity> implements WorkflowQuery<T> {
	@XmlElement(name="interfaceClass")
	@XmlJavaTypeAdapter(value = ClassXmlAdapter.class)
	Class<T> interfaceClass = null;
	
	@XmlElement(name="criterionList")
	@XmlJavaTypeAdapter(CriterionListXmlAdapter.class)
	List<AbsCriterion> criterions = new ArrayList<AbsCriterion>();
	
	@XmlElement(name="orderBy")
	List<Order> orders = new ArrayList<Order>();
	
	@XmlElement(name="firstResult")
	int firstResult = 0;
	
	@XmlElement(name="maxResults")
	int maxResults = -1;//表示查询所有
	
	@XmlElement(name="queryFromHistory")
	boolean queryFromHistory = false;
	
//	WorkflowSession session = null;
	
	
	@XmlTransient
	WorkflowQueryDelegate queryDelegate = null;
	
	public WorkflowQueryImpl(){
		
	}
	
	public WorkflowQueryImpl(Class<T> interfaceClass){
//		this.session = session;
		this.interfaceClass = interfaceClass;
		
	}
	
	public void setQueryDelegate(WorkflowQueryDelegate delegate){
		queryDelegate = delegate;
	}
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#add(org.fireflow.engine.Criterion)
	 */
	public WorkflowQuery<T> add(Criterion criterion) {
		
		criterions.add((AbsCriterion)criterion);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#addOrder(org.fireflow.engine.Order)
	 */
	public WorkflowQuery<T> addOrder(Order order) {
		orders.add(order);
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#count()
	 */
	public int count() {
		return this.queryDelegate.executeQueryCount(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#get(java.lang.String)
	 */
	public T get(String entityId) {
		return (T)this.queryDelegate.getEntity(entityId, this.interfaceClass);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#getFirstResult()
	 */
	public int getFirstResult() {
		return this.firstResult;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#getMaxResults()
	 */
	public int getMaxResults() {
		return this.maxResults;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#isQueryFromHistory()
	 */
	public boolean isQueryFromHistory() {
		
		return this.queryFromHistory;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#list()
	 */
	public List<T> list() {
		return this.queryDelegate.executeQueryList(this);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#reset()
	 */
	public WorkflowQuery<T> reset() {
		this.criterions.clear();
		this.orders.clear();
		this.firstResult=0;
		this.maxResults=-1;
		this.queryFromHistory = false;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#setFirstResult(int)
	 */
	public WorkflowQuery<T> setFirstResult(int rowNum) {
		this.firstResult = rowNum;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#setMaxResults(int)
	 */
	public WorkflowQuery<T> setMaxResults(int size) {
		this.maxResults = size;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#setQueryFromHistory(boolean)
	 */
	public WorkflowQuery<T> setQueryFromHistory(boolean b) {
		this.queryFromHistory = b;
		return this;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.WorkflowQuery#unique()
	 */
	public T unique() {
		int oldMaxResult = this.maxResults;
		this.maxResults = 1;
		List<T> entities = this.queryDelegate.executeQueryList(this);
		if (entities!=null && entities.size()>0){
			this.maxResults = oldMaxResult;
			return entities.get(0);
		}
		this.maxResults = oldMaxResult;
		return null;
	}

	public Class<T> getEntityClass(){
		return this.interfaceClass;
	}
	
	public List<? extends Criterion> getAllCriterions(){
		return this.criterions;
	}
	public List<Order> getAllOrders(){
		return this.orders;
	}
	
	public static void main(String[] args){
		WorkflowQueryImpl<ActivityInstance> impl = new WorkflowQueryImpl<ActivityInstance>(ActivityInstance.class);
		impl.add(Restrictions.eq(ActivityInstanceProperty.ID, "213"));
		impl.add(Restrictions.isNotNull(ActivityInstanceProperty.BIZ_TYPE));
		
		List<? extends Criterion> critierionList = impl.getAllCriterions();
		
		for (Criterion c : critierionList){
			System.out.println(c);
		}
	
	}
}
