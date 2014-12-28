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
package org.fireflow.client;

import java.util.List;

import org.fireflow.client.query.Criterion;
import org.fireflow.client.query.Order;
import org.fireflow.engine.entity.WorkflowEntity;


/**
 * @author 非也
 * @version 2.0
 */
public interface WorkflowQuery<T extends WorkflowEntity> {
	/**
	 * 返回结果集
	 * @return
	 */
	public List<T> list();
	
	/**
	 * 返回符合条件的记录数
	 * @return
	 */
	public int count();

	/**
	 * 如果符合查询条件的只有一条记录，通过该方法返回唯一记录
	 * @return
	 */
	public T unique();
	
	/**
	 * 根据实体的id返回实体，如果运行时标没有发现该实体，则从尝试从历史表查询。
	 * @param entityId
	 * @return
	 */
	public T get(String entityId);
	
	/**
	 * 重置所有的查询条件，firstResult值重置为0,maxResult值重置为-1，queryFromHistory重置为false;
	 */
	public WorkflowQuery<T> reset();
	/**
	 * 是否从历史表查询数据，缺省情况下从运行时标查询数据。
	 * @param b
	 */
	public WorkflowQuery<T> setQueryFromHistory(boolean b);
	public boolean isQueryFromHistory();
	
	/**
	 * 设置第一行的位置，从0开始
	 * @param rowNum
	 */
	public WorkflowQuery<T> setFirstResult(int rowNum);
	
	public int getFirstResult();
	
	/**
	 * 设置返回的记录数，-1表示没有限制
	 * @param size
	 */
	public WorkflowQuery<T> setMaxResults(int size);
	public int getMaxResults();
	
	public WorkflowQuery<T> add(Criterion criterion);
	
	public WorkflowQuery<T> addOrder(Order order);
	
	public Class<T> getEntityClass();
	
	public List<? extends Criterion> getAllCriterions();
	public List<Order> getAllOrders();
}
