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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.entity.repository.ResourceDescriptor;
import org.fireflow.engine.entity.repository.ResourceDescriptorProperty;
import org.fireflow.engine.entity.repository.ResourceRepository;
import org.fireflow.engine.entity.repository.impl.ResourceDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ResourceRepositoryImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.persistence.ResourcePersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.resource.ResourceDeserializer;
import org.fireflow.model.resourcedef.ResourceDef;
import org.firesoa.common.util.Utils;
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
public class ResourcePersisterHibernateImpl extends AbsPersisterHibernateImpl implements ResourcePersister {
	private static Log log = LogFactory.getLog(ResourcePersisterHibernateImpl.class);

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ResourcePersister#findResourceRepositoryByFileName(java.lang.String)
	 */
	public ResourceRepository findResourceRepositoryByFileName(
			final String resourceFileName){
		ResourceRepository result = (ResourceRepository)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(ResourceRepositoryImpl.class);
				c.add(Restrictions.eq("fileName", resourceFileName));
				
				return c.uniqueResult();
			}
			
		});

		if (result!=null && 
				result.getResourceContent()!=null){
			try {
				String content = result.getResourceContent();
				String charset = Utils.findXmlCharset(content);
				ByteArrayInputStream byteIn = new ByteArrayInputStream(content.getBytes(charset));
				
				ResourceDeserializer parser = new ResourceDeserializer();
				List<ResourceDef> resources = parser.deserialize(byteIn);
				
				((ResourceRepositoryImpl)result).setResources(resources);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}catch(DeserializerException e){
				log.error(e);
			}
			catch(IOException e){
				log.error(e);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ResourcePersister#persistResourceFileToRepository(java.io.InputStream, java.util.Map)
	 */
	public List<ResourceDescriptor> persistResourceFileToRepository(
			InputStream resourceFileInput,
			Map<ResourceDescriptorProperty, Object> properties)throws DeserializerException,InvalidModelException {
		if (properties==null) throw new EngineException("The resource descriptor properties can NOT be emtpy!");
		final String fileName = (String)properties.get(ResourceDescriptorProperty.FILE_NAME);
		String lastEditor = (String)properties.get(ResourceDescriptorProperty.LAST_EDITOR);
		Date lastEditTime = (Date)properties.get(ResourceDescriptorProperty.LAST_EDIT_TIME);
		
		if (fileName==null || fileName.trim().equals("")){
			throw new EngineException("The FILE_NAME property can NOT be emtpy!");
		}
		ResourceRepository repository = repositoryFromInputStream(fileName,resourceFileInput);
		
		this.saveOrUpdate(repository);
		
		//将Service Descriptor先删后插……
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String delete = "Delete From ResourceDescriptorImpl m Where m.fileName=:fileName";
				
				Query q4Delete = session.createQuery(delete);
				q4Delete.setString("fileName", fileName);
				q4Delete.executeUpdate();
				return null;
			}
			
		});
		
		
		List<ResourceDef> services = repository.getResources();
		List<ResourceDescriptor> descriptors = new ArrayList<ResourceDescriptor>();
		if (services!=null){
			for (ResourceDef rsc : services){
				ResourceDescriptorImpl desc = new ResourceDescriptorImpl();
				desc.setResourceId(rsc.getId());
				desc.setName(rsc.getName());
				desc.setDisplayName(rsc.getDisplayName());
				desc.setDescription(rsc.getDescription());
				desc.setResourceType(rsc.getResourceType().getValue());
				
				desc.setFileName(fileName);
				desc.setLastEditor(lastEditor);
				
				Object obj = properties.get(ResourceDescriptorProperty.PUBLISH_STATE);
				Boolean publishState = Boolean.TRUE;
				if (obj!=null && obj instanceof Boolean){
					publishState = (Boolean) obj;
				}
				desc.setPublishState(publishState);
				
				
				this.saveOrUpdate(desc);
				descriptors.add(desc);
			}
		}
		return descriptors;
	}
	private ResourceRepository repositoryFromInputStream(String resourceFileName,InputStream inStream)throws DeserializerException,InvalidModelException{
		
		ResourceRepositoryImpl repository = (ResourceRepositoryImpl)this.findResourceRepositoryByFileName(resourceFileName);
		if (repository==null){
			repository = new ResourceRepositoryImpl();
		}
			
		
		ResourceDeserializer parser = new ResourceDeserializer();
		try {
			byte[] bytes = Utils.inputStream2ByteArray(inStream);
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
			
			String charset = Utils.findXmlCharset(bytesIn);
			
			List<ResourceDef> resources = parser.deserialize(bytesIn);
			
			repository.setResourceContent(new String(bytes,charset));
			repository.setFileName(resourceFileName);
			repository.setResources(resources);
			
			//TODO repository.setResourceDescriptors(descriptors)
			
			return repository;
		} catch (IOException e) {
			throw new InvalidModelException(e);
		}
	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.hibernate.AbsPersisterHibernateImpl#getEntityClass4Runtime(java.lang.Class)
	 */
	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		
		return ResourceDescriptorImpl.class;
	}


}
