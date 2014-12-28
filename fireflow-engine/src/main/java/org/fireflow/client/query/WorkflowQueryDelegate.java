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
package org.fireflow.client.query;

import java.util.List;

import org.fireflow.client.WorkflowQuery;
import org.fireflow.engine.entity.WorkflowEntity;

/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
public interface WorkflowQueryDelegate {
	public <T extends WorkflowEntity> List<T> executeQueryList(WorkflowQuery<T> q);
	public <T extends WorkflowEntity> int executeQueryCount(WorkflowQuery<T> q);
	/**
	 * 根据实体的Id返回实体对象，如果运行时表没有发现该对象，则从历史表中查询。
	 * @param <T>
	 * @param entityId 实体Id
	 * @param entityClass 实体接口类
	 * @return
	 */
	public <T extends WorkflowEntity> T getEntity(String entityId,Class<T> entityClass);
}
