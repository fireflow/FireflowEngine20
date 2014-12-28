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
package org.fireflow.engine.modules.persistence;

import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;


/**
 * @author 非也
 * @version 2.0
 */
public interface Persister {
	/**
	 * 通过对象的Id查询对象实例，如果在运行时表没有找到，则继续查询历史表。
	 * @param <T>
	 * @param entityClz
	 * @param entityId
	 * @return
	 */
	public <T extends WorkflowEntity> T find(Class<T> entityClz,String entityId);
	
	public <T extends WorkflowEntity> List<T> list(WorkflowQuery<T> q);
	public <T extends WorkflowEntity> int count(WorkflowQuery<T> q);
	/**
	 * 保存或者更新对象
	 * @param entity
	 */
	public void saveOrUpdate(Object entity);
	
	public PersistenceService getPersistenceService();
	
	public void setPersistenceService(PersistenceService persistenceService);
}
