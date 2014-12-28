package org.fireflow.service.mock;

import java.io.InputStream;

import org.fireflow.engine.entity.repository.ProcessDescriptor;
import org.fireflow.engine.entity.repository.ProcessKey;
import org.fireflow.engine.entity.repository.ProcessRepository;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.WebservicePublishException;
import org.fireflow.engine.modules.processlanguage.AbsProcessLanguageManager;
import org.fireflow.model.InvalidModelException;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.data.Property;
import org.fireflow.model.resourcedef.ResourceDef;
import org.fireflow.model.servicedef.ServiceDef;

public class ProcessLanguageMock extends AbsProcessLanguageManager {
	public String serializeProcess2Xml(Object process)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object deserializeXml2Process(InputStream inStream)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public ProcessRepository serializeProcess2ProcessRepository(Object process)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceBinding getServiceBinding(ProcessKey processKey,
			String subflowId, String activityId) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceBinding getResourceBinding(ProcessKey processKey,
			String subflowId, String activityId) throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object findActivity(ProcessKey processKey, String subflow, String activityId)
			throws InvalidModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public Property getProperty(ProcessKey processKey, String processElementId, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getProcessEntryElementId(java.lang.String, int, java.lang.String)
	 */
	public String getProcessEntryId(String workflowProcessId,
			int version, String processType) {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see org.fireflow.engine.modules.processlanguage.ProcessLanguageManager#getServiceDef(org.fireflow.engine.entity.runtime.ActivityInstance, java.lang.Object, java.lang.String)
	 */
	public ServiceDef getServiceDef(ActivityInstance activityInstance,
			Object activity, String serviceId) {
		ActivityMock mock = (ActivityMock)activity;
		return mock.getServiceDef();
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

}
