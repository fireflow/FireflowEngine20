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

import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.model.InvalidModelException;

/**
 * @author 非也
 * @version 2.0
 */
public interface ProcessPersister extends Persister {
	/**
	 * 删除所有的流程定义，该api给simulator使用
	 */
	public void deleteAllProcesses();
	/**
	 * 将流程定义对象持久化到流程库中，流程库的具体实现是由ProcessRepositoryPersistenceService决定的。
	 * ProcessRepositoryPersistenceService可以是内方式存储或者数据库方式存储，或者其他方式。
	 * 该方法的主要作用是将process对象转换成一个ProcessRepository对象，交给ProcessRepositoryPersistenceService。
	 * @param process
	 * @param descriptorKeyValues
	 * @return
	 * @deprecated
	public ProcessRepository persistProcessToRepository(Object process,
			Map<ProcessDescriptorProperty, Object> descriptorKeyValues)throws InvalidModelException;
	*/

	/**
	 * 如果descriptor.getId()不为空，表示覆盖；否则表示插入；插入时需要重新计算version字段
	 * @param processXml
	 * @param descriptor
	 * @return
	 */
	public ProcessRepository persistProcessToRepository(String processXml,ProcessDescriptor descriptor) ;
	
	public ProcessRepository findProcessRepositoryByProcessKey(
			ProcessKey processKey)throws InvalidModelException;
	
	public ProcessRepository findTheLatestVersionOfProcessRepository(
			String processId, String processType)throws InvalidModelException;
	
	public String findProcessXml(ProcessKey processKey);
	
	public ProcessDescriptor findProcessDescriptorByProcessKey(ProcessKey processKey);	

	public ProcessDescriptor findTheLatestVersionOfProcessDescriptor(
			String processId, String processType);	
	
	public int findTheLatestVersion(String processId, String processType);
	
	public int findTheLatestPublishedVersion(String processId,String processType);
	
	public boolean isUseProcessCache();
	
	public void setUseProcessCache(boolean b);
}
