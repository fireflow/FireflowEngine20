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
import org.fireflow.engine.entity.repository.ServiceDescriptor;
import org.fireflow.engine.entity.repository.ServiceDescriptorProperty;
import org.fireflow.engine.entity.repository.ServiceRepository;
import org.fireflow.engine.entity.repository.impl.ServiceDescriptorImpl;
import org.fireflow.engine.entity.repository.impl.ServiceRepositoryImpl;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.modules.persistence.ServicePersister;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.io.DeserializerException;
import org.fireflow.model.io.service.ServiceParser;
import org.fireflow.model.servicedef.ServiceDef;
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
public class ServicePersisterHibernateImpl extends AbsPersisterHibernateImpl implements ServicePersister {
	private static Log log = LogFactory.getLog(ServicePersisterHibernateImpl.class);

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ServicePersister#findServiceRepositoryByFileName(java.lang.String)
	 */
	public ServiceRepository findServiceRepositoryByFileName(
			final String serviceFileName) {
		ServiceRepository result = (ServiceRepository)this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria c = session.createCriteria(ServiceRepositoryImpl.class);
				c.add(Restrictions.eq("fileName", serviceFileName));
				
				return c.uniqueResult();
			}
			
		});

		if (result!=null && result.getServiceContent()!=null){
			try {
				String content = result.getServiceContent();
				String charset = Utils.findXmlCharset(content);
				ByteArrayInputStream byteIn = new ByteArrayInputStream(content.getBytes(charset));
				
				
				List<ServiceDef> services = ServiceParser.deserialize(byteIn);
				
				((ServiceRepositoryImpl)result).setServices(services);
			} catch (UnsupportedEncodingException e) {
				log.error(e);
			}catch(DeserializerException e){
				log.error(e);
			}
			catch(IOException e){
				log.error(e);
			} catch (InvalidModelException e) {
				log.error(e);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.ServicePersister#persistServiceFileToRepository(java.io.InputStream, java.util.Map)
	 */
	public List<ServiceDescriptor> persistServiceFileToRepository(
			InputStream serviceFileInput,
			Map<ServiceDescriptorProperty, Object> properties)throws InvalidModelException,DeserializerException {
		if (properties==null) throw new EngineException("The service descriptor properties can NOT be emtpy!");
		final String fileName = (String)properties.get(ServiceDescriptorProperty.FILE_NAME);
		String lastEditor = (String)properties.get(ServiceDescriptorProperty.LAST_EDITOR);
		Date lastEditTime = (Date)properties.get(ServiceDescriptorProperty.LAST_EDIT_TIME);
		
		if (fileName==null || fileName.trim().equals("")){
			throw new EngineException("The FILE_NAME property can NOT be emtpy!");
		}
		ServiceRepository repository = repositoryFromInputStream(fileName,serviceFileInput);

		this.saveOrUpdate(repository);
		
		//将Service Descriptor先删后插……
		this.getHibernateTemplate().execute(new HibernateCallback(){

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String delete = "Delete From ServiceDescriptorImpl m Where m.fileName=:fileName";
				
				Query q4Delete = session.createQuery(delete);
				q4Delete.setString("fileName", fileName);
				q4Delete.executeUpdate();
				return null;
			}
			
		});
		
		
		List<ServiceDef> services = repository.getServices();
		List<ServiceDescriptor> descriptors = new ArrayList<ServiceDescriptor>();
		if (services!=null){
			for (ServiceDef svc : services){
				ServiceDescriptorImpl desc = new ServiceDescriptorImpl();
				
				desc.setServiceId(svc.getId());
				desc.setBizType(svc.getBizCategory());
				desc.setName(svc.getName());
				desc.setDisplayName(svc.getDisplayName());
				desc.setDescription(svc.getDescription());
				
				desc.setFileName(fileName);
				desc.setLastEditor(lastEditor);

				
				Object obj = properties.get(ServiceDescriptorProperty.PUBLISH_STATE);
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

	private ServiceRepository repositoryFromInputStream(String serviceFileName,InputStream inStream)
			throws InvalidModelException,DeserializerException{
		ServiceRepositoryImpl repository = (ServiceRepositoryImpl)this.findServiceRepositoryByFileName(serviceFileName);
		if (repository==null){
			repository = new ServiceRepositoryImpl();
		}
		
		try {
			byte[] bytes = Utils.inputStream2ByteArray(inStream);
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
			
			String charset = Utils.findXmlCharset(bytesIn);
			
			List<ServiceDef> services = ServiceParser.deserialize(bytesIn);
			
			
			repository.setServiceContent(new String(bytes,charset));
			repository.setFileName(serviceFileName);
			repository.setServices(services);
			//TODO repository.setServiceDescriptors(...);
			
			return repository;
		} catch (IOException e) {
			throw new DeserializerException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.persistence.hibernate.AbsPersisterHibernateImpl#getEntityClass4Runtime(java.lang.Class)
	 */
	@Override
	public Class getEntityClass4Runtime(Class interfaceClz) {
		return ServiceDescriptorImpl.class;
	}

}
