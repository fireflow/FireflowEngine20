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
package org.fireflow.pdl.bpel.enginemodules;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.repository.impl.ProcessRepositoryImpl;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.EngineException;
import org.fireflow.engine.exception.WebservicePublishException;
import org.fireflow.engine.modules.processlanguage.AbsProcessLanguageManager;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;
import org.fireflow.pdl.bpel.BpelProcess;

/**
 * @author 非也
 * @version 2.0
 */
public class BpelManager extends AbsProcessLanguageManager {

	Map<String ,ProcessRepository> hashMapRepository = new HashMap<String,ProcessRepository>();
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getAllTheLatestVersionsOfProcess()
//	 */
//	@Override
//	public List<ProcessRepository> getAllTheLatestVersionsOfProcessRepository(String processType) {
//		// TODO Auto-generated method stub
//		return null;
//	}




//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.repository.ProcessRepositoryService#persistProcessToRepository(java.lang.Object, java.lang.String, java.util.Map)
//	 */
//	@Override
//	public ProcessRepository persistProcessToRepository(Object process, Map<ProcessDescriptorProperty, Object> metadata) {
//		ProcessRepositoryImpl repository = new ProcessRepositoryImpl();
//		BpelProcess bpelProcess = (BpelProcess)process;
////		repository.setProcess(bpelProcess);
//		repository.setProcessType(BpelConstants.PROCESS_TYPE);
//		repository.setProcessId(bpelProcess.getId());
//		repository.setVersion(1);
//		repository.setName(bpelProcess.getName());
//		repository.setDisplayName(bpelProcess.getName());
//		repository.setPublishState(true);
//		hashMapRepository.put(repository.getProcessId(),repository);
//		return repository;
//	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getActivity(org.fireflow.engine.entity.repository.ProcessKey, java.lang.String)
	 */
	public Object findActivity(ProcessKey processKey, String subflow, String activityId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getResourceBinding(org.fireflow.engine.entity.repository.ProcessKey, java.lang.String)
	 */
	public ResourceBinding getResourceBinding(ProcessKey processKey,
			String subflowId, String activityId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getServiceBinding(org.fireflow.engine.entity.repository.ProcessKey, java.lang.String)
	 */
	public ServiceBinding getServiceBinding(ProcessKey processKey,
			String subflowId, String activityId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getWorkflowProcess(org.fireflow.engine.entity.repository.ProcessKey)
	 */
	public Object getWorkflowProcess(ProcessKey processKey) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#deserializeXml2Process(java.lang.String)
	 */
	public Object deserializeXml2Process(InputStream inStream) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#serializeProcess2Xml(java.lang.Object)
	 */
	public String serializeProcess2Xml(Object process) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#serializeProcess2ProcessRepository(java.lang.Object)
	 */
	public ProcessRepository serializeProcess2ProcessRepository(Object process) {
		ProcessRepositoryImpl repository = new ProcessRepositoryImpl();
		BpelProcess bpelProcess = (BpelProcess)process;
		repository.setProcessId(bpelProcess.getId());
		repository.setName(bpelProcess.getName());
		repository.setProcessType("BPEL");
		repository.setVersion(1);
		repository.setPublishState(true);
		repository.setProcessObject(process);
		//repository.setFileName(bpelProcess.getId()+".bpel.xml");
		return repository;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getProcessEntryElementId(java.lang.String, int, java.lang.String)
	 */
	public String getProcessEntryId(String workflowProcessId,
			int version, String processType) {
		return workflowProcessId;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getProperty(org.fireflow.engine.entity.repository.ProcessKey, java.lang.String, java.lang.String)
	 */
	public Property getProperty(ProcessKey processKey,
			String workflowElementId, String propertyName)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}



	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getServiceDef(org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object, java.lang.String)
	 */
	public ServiceDef getServiceDef(ActivityInstance activityInstance,
			Object activity, String serviceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getResourceDef(org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object, java.lang.String)
	 */
	public ResourceDef getResourceDef(ActivityInstance activityInstance,
			Object activity, String resourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#generateProcessDescriptor(java.lang.Object)
	 */
	public ProcessDescriptor generateProcessDescriptor(Object process) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getServiceBinding(java.lang.Object)
	 */
	public ServiceBinding getServiceBinding(Object activity)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getResourceBinding(java.lang.Object)
	 */
	public ResourceBinding getResourceBinding(Object activity)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getProperty(java.lang.Object, java.lang.String)
	 */
	public Property getProperty(Object workflowDefinitionElement,
			String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#findSubProcess(org.fireflow.engine.entity.repository.ProcessKey, java.lang.String)
	 */
	public Object findSubProcess(ProcessKey processKey, String subflowId)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#publishAllProcessServices()
	 */
	public void publishAllProcessServices() throws WebservicePublishException {
		// TODO Auto-generated method stub
		
	}




//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getProcessByProcessKey(org.fireflow.engine.entity.repository.ProcessKey)
//	 */
//	@Override
//	public ProcessRepository getProcessRepositoryByProcessKey(ProcessKey processKey) {
//		return hashMapRepository.get(processKey.getProcessId());
//	}




//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getTheLatestVersionOfProcess(java.lang.String, java.lang.String)
//	 */
//	@Override
//	public ProcessRepository getTheLatestVersionOfProcessRepository(String processId,
//			String processType) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	/* (non-Javadoc)
//	 * @see org.fireflow.engine.repository.ProcessRepositoryService#getProcessDescriptorByProcessKey(org.fireflow.engine.entity.repository.ProcessKey)
//	 */
//	@Override
//	public ProcessDescriptor getProcessDescriptorByProcessKey(
//			ProcessKey processKey) {
//		return hashMapRepository.get(processKey.getProcessId());
//	}

}
