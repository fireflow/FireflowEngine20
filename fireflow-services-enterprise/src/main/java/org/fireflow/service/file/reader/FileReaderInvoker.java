package org.fireflow.service.file.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.client.WorkflowSession;
import org.fireflow.engine.context.RuntimeContext;
import org.fireflow.engine.entity.runtime.ActivityInstance;
import org.fireflow.engine.exception.ServiceInvocationException;
import org.fireflow.engine.invocation.ServiceInvoker;
import org.fireflow.engine.invocation.impl.AbsServiceInvoker;
import org.fireflow.model.binding.OutputAssignment;
import org.fireflow.model.binding.ResourceBinding;
import org.fireflow.model.binding.ServiceBinding;
import org.fireflow.model.servicedef.ServiceDef;

public class FileReaderInvoker extends AbsServiceInvoker implements ServiceInvoker {
	public static final String SERVICE_TYPE = "FILE_READER";

	public String getServiceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getServiceObject(RuntimeContext runtimeContext,
			WorkflowSession session, ActivityInstance activityInstance,
			ServiceBinding serviceBinding,ServiceDef svc,Object activity) throws ServiceInvocationException {
		FileReadServiceDef service = (FileReadServiceDef)svc;
		FileReaderImpl reader = new FileReaderImpl();
		reader.setFileReadService(service);
		return reader;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.invocation.AbsServiceInvoker#getParameterTypes(java.lang.String, java.lang.Object[])
	 */
	@Override
	protected Class[] getParameterTypes(Class serviceClass, String methodName, Object[] params) {
		Class[] paramTypes = new Class[]{Long.class};
		return paramTypes;
	}
	

}
