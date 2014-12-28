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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.fireflow.engine.entity.repository.ServiceDescriptor;
import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.repository.ServiceRepository;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;

/**
 * 
 * @deprecated service不需要单独存储。
 * @author 非也
 * @version 2.0
 */
public interface ServicePersister extends Persister {
	
	/**
	 * 根据文件名将ServiceRepository读取出来
	 * @param servicesFileName
	 * @return
	 */
	public ServiceRepository findServiceRepositoryByFileName(String serviceFileName)throws DeserializerException;
	
	/**
	 * 将一个服务定义文件保存到存储库中
	 * @param serviceFileInput
	 * @return
	 */
	public List<ServiceDescriptor> persistServiceFileToRepository(InputStream serviceFileInput,Map<ServiceDescriptorProperty,Object> properties)throws InvalidModelException,DeserializerException;
}
